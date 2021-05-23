/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model.injection.strategy.blind.patch;

/**
 * Diff Match and Patch
 *
 * Copyright 2006 Google Inc.
 * http://code.google.com/p/google-diff-match-patch/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

/*
 * Functions for diff, match and patch.
 * Computes the difference between two texts to create a patch.
 * Applies the patch onto another text, allowing for errors.
 *
 * @author fraser@google.com (Neil Fraser)
 */

/**
 * Class containing the diff, match and patch methods.
 * Also contains the behaviour settings.
 */
public class DiffMatchPatch {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    // Defaults.
    // Set these on your diff_match_patch instance to override the defaults.

    /**
     * Number of seconds to map a diff before giving up (0 for infinity).
     */
    public static final float DIFF_TIMEOUT = 1.0f;
    
    /**
     * Cost of an empty edit operation in terms of edit characters.
     */
    public static final short DIFF_EDIT_COST = 4;
    
    /**
     * At what point is no match declared (0.0 = perfection, 1.0 = very loose).
     */
    public static final float MATCH_THRESHOLD = 0.5f;
    
    /**
     * How far to search for a match (0 = exact location, 1000+ = broad match).
     * A match this many characters away from the expected location will add
     * 1.0 to the score (0.0 is a perfect match).
     */
    public static final int MATCH_DISTANCE = 1000;
    
    /**
     * When deleting a large block of text (over ~64 characters), how close do
     * the contents have to be to match the expected contents. (0.0 = perfection,
     * 1.0 = very loose).  Note that Match_Threshold controls how closely the
     * end points of a delete need to match.
     */
    public static final float PATCH_DELETE_THRESHOLD = 0.5f;
    
    /**
     * Chunk size for context length.
     */
    public static final short PATCH_MARGIN = 4;

    /**
     * The number of bits in an int.
     */
    private static final short MATCH_MAX_BITS = 32;

    // Define some regex patterns for matching boundaries.
    private static final Pattern BLANK_LINE_END = Pattern.compile("\\n\\r?\\n\\Z", Pattern.DOTALL);
    private static final Pattern BLANK_LINE_START = Pattern.compile("\\A\\r?\\n\\r?\\n", Pattern.DOTALL);

    /**
     * Internal class for returning results from diff_linesToChars().
     * Other less paranoid languages just use a three-element array.
     */
    protected static class LinesToCharsResult {
        protected String chars1;
        protected String chars2;
        protected List<String> lineArray;

        protected LinesToCharsResult(String chars1, String chars2,
                List<String> lineArray) {
            this.chars1 = chars1;
            this.chars2 = chars2;
            this.lineArray = lineArray;
        }
    }

    //  DIFF FUNCTIONS

    /**
     * The data structure representing a diff is a Linked list of Diff objects:
     * {Diff(Operation.DELETE, "Hello"), Diff(Operation.INSERT, "Goodbye"),
     *  Diff(Operation.EQUAL, " world.")}
     * which means: delete "Hello", add "Goodbye" and keep " world."
     */
    public enum Operation {
        DELETE, INSERT, EQUAL
    }

    /**
     * Find the differences between two texts.
     * Run a faster, slightly less optimal diff.
     * This method allows the 'checklines' of diff_main() to be optional.
     * Most of the time checklines is wanted, so default to true.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @return Linked List of Diff objects.
     */
    public List<Diff> diffMain(String text1, String text2) {
        return this.diffMain(text1, text2, true);
    }

    /**
     * Find the differences between two texts.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param checklines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff.
     * @return Linked List of Diff objects.
     */
    public LinkedList<Diff> diffMain(String text1, String text2, boolean checklines) {
        // Set a deadline by which time the diff must be complete.
        long deadline = System.currentTimeMillis() + (long) (DIFF_TIMEOUT * 1000);
        return this.diffMain(text1, text2, checklines, deadline);
    }

    /**
     * Find the differences between two texts.  Simplifies the problem by
     * stripping any common prefix or suffix off the texts before diffing.
     * @param valueText1 Old string to be diffed.
     * @param valueText2 New string to be diffed.
     * @param checklines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff.
     * @param deadline Time when the diff should be complete by.  Used
     *     internally for recursive calls.  Users should set DiffTimeout instead.
     * @return Linked List of Diff objects.
     */
    private LinkedList<Diff> diffMain(String valueText1, String valueText2, boolean checklines, long deadline) {
        String text1 = valueText1;
        String text2 = valueText2;
        
        // Check for null inputs.
        if (text1 == null || text2 == null) {
            throw new IllegalArgumentException("Null inputs. (diff_main)");
        }

        // Check for equality (speedup).
        LinkedList<Diff> diffs;
        if (text1.equals(text2)) {
            diffs = new LinkedList<>();
            if (text1.length() != 0) {
                diffs.add(new Diff(Operation.EQUAL, text1));
            }
            return diffs;
        }

        // Trim off common prefix (speedup).
        int commonlength = this.diffCommonPrefix(text1, text2);
        String commonprefix = text1.substring(0, commonlength);
        text1 = text1.substring(commonlength);
        text2 = text2.substring(commonlength);

        // Trim off common suffix (speedup).
        commonlength = this.diffCommonSuffix(text1, text2);
        String commonsuffix = text1.substring(text1.length() - commonlength);
        text1 = text1.substring(0, text1.length() - commonlength);
        text2 = text2.substring(0, text2.length() - commonlength);

        // Compute the diff on the middle block.
        diffs = this.diffCompute(text1, text2, checklines, deadline);

        // Restore the prefix and suffix.
        if (commonprefix.length() != 0) {
            diffs.addFirst(new Diff(Operation.EQUAL, commonprefix));
        }
        if (commonsuffix.length() != 0) {
            diffs.addLast(new Diff(Operation.EQUAL, commonsuffix));
        }

        this.diffCleanupMerge(diffs);
        return diffs;
    }

    /**
     * Find the differences between two texts.  Assumes that the texts do not
     * have any common prefix or suffix.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param checklines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff.
     * @param deadline Time when the diff should be complete by.
     * @return Linked List of Diff objects.
     */
    private LinkedList<Diff> diffCompute(String text1, String text2, boolean checklines, long deadline) {
        LinkedList<Diff> diffs = new LinkedList<>();

        if (text1.length() == 0) {
            // Just add some text (speedup).
            diffs.add(new Diff(Operation.INSERT, text2));
            return diffs;
        }

        if (text2.length() == 0) {
            // Just delete some text (speedup).
            diffs.add(new Diff(Operation.DELETE, text1));
            return diffs;
        }

        {
            // New scope so as to garbage collect longtext and shorttext.
            String longtext = text1.length() > text2.length() ? text1 : text2;
            String shorttext = text1.length() > text2.length() ? text2 : text1;
            int i = longtext.indexOf(shorttext);
            if (i != -1) {
                // Shorter text is inside the longer text (speedup).
                Operation op = (text1.length() > text2.length()) ?
                        Operation.DELETE : Operation.INSERT;
                diffs.add(new Diff(op, longtext.substring(0, i)));
                diffs.add(new Diff(Operation.EQUAL, shorttext));
                diffs.add(new Diff(op, longtext.substring(i + shorttext.length())));
                return diffs;
            }

            if (shorttext.length() == 1) {
                // Single character string.
                // After the previous speedup, the character can't be an equality.
                diffs.add(new Diff(Operation.DELETE, text1));
                diffs.add(new Diff(Operation.INSERT, text2));
                return diffs;
            }
        }

        // Check to see if the problem can be split in two.
        String[] hm = this.diffHalfMatch(text1, text2);
        if (hm != null) {
            // A half-match was found, sort out the return data.
            String text1A = hm[0];
            String text1B = hm[1];
            String text2A = hm[2];
            String text2B = hm[3];
            String midCommon = hm[4];
            // Send both pairs off for separate processing.
            LinkedList<Diff> diffsA = this.diffMain(text1A, text2A, checklines, deadline);
            List<Diff> diffsB = this.diffMain(text1B, text2B, checklines, deadline);
            // Merge the results.
            diffs = diffsA;
            diffs.add(new Diff(Operation.EQUAL, midCommon));
            diffs.addAll(diffsB);
            return diffs;
        }

        if (checklines && text1.length() > 100 && text2.length() > 100) {
            return this.diffLineMode(text1, text2, deadline);
        }

        return this.diffBisect(text1, text2, deadline);
    }

    /**
     * Do a quick line-level diff on both strings, then rediff the parts for
     * greater accuracy.
     * This speedup can produce non-minimal diffs.
     * @param valueText1 Old string to be diffed.
     * @param valueText2 New string to be diffed.
     * @param deadline Time when the diff should be complete by.
     * @return Linked List of Diff objects.
     */
    private LinkedList<Diff> diffLineMode(String valueText1, String valueText2, long deadline) {
        // Scan the text on a line-by-line basis first.
        LinesToCharsResult b = this.diffLinesToChars(valueText1, valueText2);
        String text1 = b.chars1;
        String text2 = b.chars2;
        List<String> linearray = b.lineArray;

        LinkedList<Diff> diffs = this.diffMain(text1, text2, false, deadline);

        // Convert the diff back to original text.
        this.diffCharsToLines(diffs, linearray);
        // Eliminate freak matches (e.g. blank lines)
        this.diffCleanupSemantic(diffs);

        // Rediff any replacement blocks, this time character-by-character.
        // Add a dummy entry at the end.
        diffs.add(new Diff(Operation.EQUAL, ""));
        int countDelete = 0;
        int countInsert = 0;
        StringBuilder textDelete = new StringBuilder();
        StringBuilder textInsert = new StringBuilder();
        ListIterator<Diff> pointer = diffs.listIterator();
        Diff thisDiff = pointer.next();
        while (thisDiff != null) {
            switch (thisDiff.getOperation()) {
            case INSERT:
                countInsert++;
                textInsert.append(thisDiff.getText());
                break;
            case DELETE:
                countDelete++;
                textDelete.append(thisDiff.getText());
                break;
            case EQUAL:
                // Upon reaching an equality, check for prior redundancies.
                if (countDelete >= 1 && countInsert >= 1) {
                    // Delete the offending records and add the merged ones.
                    pointer.previous();
                    for (int j = 0; j < countDelete + countInsert; j++) {
                        pointer.previous();
                        pointer.remove();
                    }
                    for (Diff newDiff : this.diffMain(textDelete.toString(), textInsert.toString(), false, deadline)) {
                        pointer.add(newDiff);
                    }
                }
                countInsert = 0;
                countDelete = 0;
                textDelete.setLength(0);
                textInsert.setLength(0);
                break;
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }
        diffs.removeLast();  // Remove the dummy entry at the end.

        return diffs;
    }

    /**
     * Find the 'middle snake' of a diff, split the problem in two
     * and return the recursively constructed diff.
     * See Myers 1986 paper: An O(ND) Difference Algorithm and Its Variations.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param deadline Time at which to bail if not yet complete.
     * @return LinkedList of Diff objects.
     */
    protected LinkedList<Diff> diffBisect(String text1, String text2, long deadline) {
        // Cache the text lengths to prevent multiple calls.
        int text1Length = text1.length();
        int text2Length = text2.length();
        int maxD = (text1Length + text2Length + 1) / 2;
        int vOffset = maxD;
        int vLength = 2 * maxD;
        int[] v1 = new int[vLength];
        int[] v2 = new int[vLength];
        for (int x = 0; x < vLength; x++) {
            v1[x] = -1;
            v2[x] = -1;
        }
        v1[vOffset + 1] = 0;
        v2[vOffset + 1] = 0;
        int delta = text1Length - text2Length;
        // If the total number of characters is odd, then the front path will
        // collide with the reverse path.
        boolean front = delta % 2 != 0;
        // Offsets for start and end of k loop.
        // Prevents mapping of space beyond the grid.
        int k1start = 0;
        int k1end = 0;
        int k2start = 0;
        int k2end = 0;
        for (int d = 0; d < maxD; d++) {
            // Bail out if deadline is reached.
            if (System.currentTimeMillis() > deadline) {
                break;
            }

            // Walk the front path one step.
            for (int k1 = -d + k1start; k1 <= d - k1end; k1 += 2) {
                int k1Offset = vOffset + k1;
                int x1;
                if (k1 == -d || (k1 != d && v1[k1Offset - 1] < v1[k1Offset + 1])) {
                    x1 = v1[k1Offset + 1];
                } else {
                    x1 = v1[k1Offset - 1] + 1;
                }
                int y1 = x1 - k1;
                while (x1 < text1Length && y1 < text2Length
                        && text1.charAt(x1) == text2.charAt(y1)) {
                    x1++;
                    y1++;
                }
                v1[k1Offset] = x1;
                if (x1 > text1Length) {
                    // Ran off the right of the graph.
                    k1end += 2;
                } else if (y1 > text2Length) {
                    // Ran off the bottom of the graph.
                    k1start += 2;
                } else if (front) {
                    int k2Offset = vOffset + delta - k1;
                    if (k2Offset >= 0 && k2Offset < vLength && v2[k2Offset] != -1) {
                        // Mirror x2 onto top-left coordinate system.
                        int x2 = text1Length - v2[k2Offset];
                        if (x1 >= x2) {
                            // Overlap detected.
                            return this.diffBisectSplit(text1, text2, x1, y1, deadline);
                        }
                    }
                }
            }

            // Walk the reverse path one step.
            for (int k2 = -d + k2start; k2 <= d - k2end; k2 += 2) {
                int k2Offset = vOffset + k2;
                int x2;
                if (k2 == -d || (k2 != d && v2[k2Offset - 1] < v2[k2Offset + 1])) {
                    x2 = v2[k2Offset + 1];
                } else {
                    x2 = v2[k2Offset - 1] + 1;
                }
                int y2 = x2 - k2;
                while (x2 < text1Length && y2 < text2Length
                        && text1.charAt(text1Length - x2 - 1)
                        == text2.charAt(text2Length - y2 - 1)) {
                    x2++;
                    y2++;
                }
                v2[k2Offset] = x2;
                if (x2 > text1Length) {
                    // Ran off the left of the graph.
                    k2end += 2;
                } else if (y2 > text2Length) {
                    // Ran off the top of the graph.
                    k2start += 2;
                } else if (!front) {
                    int k1Offset = vOffset + delta - k2;
                    if (k1Offset >= 0 && k1Offset < vLength && v1[k1Offset] != -1) {
                        int x1 = v1[k1Offset];
                        int y1 = vOffset + x1 - k1Offset;
                        // Mirror x2 onto top-left coordinate system.
                        x2 = text1Length - x2;
                        if (x1 >= x2) {
                            // Overlap detected.
                            return this.diffBisectSplit(text1, text2, x1, y1, deadline);
                        }
                    }
                }
            }
        }
        // Diff took too long and hit the deadline or
        // number of diffs equals number of characters, no commonality at all.
        LinkedList<Diff> diffs = new LinkedList<>();
        diffs.add(new Diff(Operation.DELETE, text1));
        diffs.add(new Diff(Operation.INSERT, text2));
        return diffs;
    }

    /**
     * Given the location of the 'middle snake', split the diff in two parts
     * and recurse.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param x Index of split point in text1.
     * @param y Index of split point in text2.
     * @param deadline Time at which to bail if not yet complete.
     * @return LinkedList of Diff objects.
     */
    private LinkedList<Diff> diffBisectSplit(String text1, String text2, int x, int y, long deadline) {
        String text1a = text1.substring(0, x);
        String text2a = text2.substring(0, y);
        String text1b = text1.substring(x);
        String text2b = text2.substring(y);

        // Compute both diffs serially.
        LinkedList<Diff> diffs = this.diffMain(text1a, text2a, false, deadline);
        List<Diff> diffsb = this.diffMain(text1b, text2b, false, deadline);

        diffs.addAll(diffsb);
        return diffs;
    }

    /**
     * Split two texts into a list of strings.  Reduce the texts to a string of
     * hashes where each Unicode character represents one line.
     * @param text1 First string.
     * @param text2 Second string.
     * @return An object containing the encoded text1, the encoded text2 and
     *     the List of unique strings.  The zeroth element of the List of
     *     unique strings is intentionally blank.
     */
    protected LinesToCharsResult diffLinesToChars(String text1, String text2) {
        List<String> lineArray = new ArrayList<>();
        Map<String, Integer> lineHash = new HashMap<>();
        // e.g. linearray[4] == "Hello\n"
        // e.g. linehash.get("Hello\n") == 4

        // "\x00" is a valid character, but various debuggers don't like it.
        // So we'll insert a junk entry to avoid generating a null character.
        lineArray.add("");

        String chars1 = this.diffLinesToCharsMunge(text1, lineArray, lineHash);
        String chars2 = this.diffLinesToCharsMunge(text2, lineArray, lineHash);
        return new LinesToCharsResult(chars1, chars2, lineArray);
    }

    /**
     * Split a text into a list of strings.  Reduce the texts to a string of
     * hashes where each Unicode character represents one line.
     * @param text String to encode.
     * @param lineArray List of unique strings.
     * @param lineHash Map of strings to indices.
     * @return Encoded string.
     */
    private String diffLinesToCharsMunge(String text, List<String> lineArray,
            Map<String, Integer> lineHash) {
        int lineStart = 0;
        int lineEnd = -1;
        String line;
        StringBuilder chars = new StringBuilder();
        // Walk the text, pulling out a substring for each line.
        // text.split('\n') would would temporarily double our memory footprint.
        // Modifying text would create many large strings to garbage collect.
        while (lineEnd < text.length() - 1) {
            lineEnd = text.indexOf('\n', lineStart);
            if (lineEnd == -1) {
                lineEnd = text.length() - 1;
            }
            line = text.substring(lineStart, lineEnd + 1);
            lineStart = lineEnd + 1;

            if (lineHash.containsKey(line)) {
                chars.append(String.valueOf((char) (int) lineHash.get(line)));
            } else {
                lineArray.add(line);
                lineHash.put(line, lineArray.size() - 1);
                chars.append(String.valueOf((char) (lineArray.size() - 1)));
            }
        }
        return chars.toString();
    }

    /**
     * Rehydrate the text in a diff from a string of line hashes to real lines of
     * text.
     * @param diffs LinkedList of Diff objects.
     * @param lineArray List of unique strings.
     */
    protected void diffCharsToLines(List<Diff> diffs, List<String> lineArray) {
        StringBuilder text;
        for (Diff diff : diffs) {
            text = new StringBuilder();
            for (int y = 0; y < diff.getText().length(); y++) {
                text.append(lineArray.get(diff.getText().charAt(y)));
            }
            diff.setText(text.toString());
        }
    }

    /**
     * Determine the common prefix of two strings
     * @param text1 First string.
     * @param text2 Second string.
     * @return The number of characters common to the start of each string.
     */
    public int diffCommonPrefix(String text1, String text2) {
        // Performance analysis: http://neil.fraser.name/news/2007/10/09/
        int n = Math.min(text1.length(), text2.length());
        for (int i = 0; i < n; i++) {
            if (text1.charAt(i) != text2.charAt(i)) {
                return i;
            }
        }
        return n;
    }

    /**
     * Determine the common suffix of two strings
     * @param text1 First string.
     * @param text2 Second string.
     * @return The number of characters common to the end of each string.
     */
    public int diffCommonSuffix(String text1, String text2) {
        // Performance analysis: http://neil.fraser.name/news/2007/10/09/
        int text1Length = text1.length();
        int text2Length = text2.length();
        int n = Math.min(text1Length, text2Length);
        for (int i = 1; i <= n; i++) {
            if (text1.charAt(text1Length - i) != text2.charAt(text2Length - i)) {
                return i - 1;
            }
        }
        return n;
    }

    /**
     * Determine if the suffix of one string is the prefix of another.
     * @param valueText1 First string.
     * @param valueText2 Second string.
     * @return The number of characters common to the end of the first
     *     string and the start of the second string.
     */
    protected int diffCommonOverlap(String valueText1, String valueText2) {
        String text1 = valueText1;
        String text2 = valueText2;
        
        // Cache the text lengths to prevent multiple calls.
        int text1Length = text1.length();
        int text2Length = text2.length();
        // Eliminate the null case.
        if (text1Length == 0 || text2Length == 0) {
            return 0;
        }
        // Truncate the longer string.
        if (text1Length > text2Length) {
            text1 = text1.substring(text1Length - text2Length);
        } else if (text1Length < text2Length) {
            text2 = text2.substring(0, text1Length);
        }
        int textLength = Math.min(text1Length, text2Length);
        // Quick check for the worst case.
        if (text1.equals(text2)) {
            return textLength;
        }

        // Start by looking for a single character match
        // and increase length until no match is found.
        // Performance analysis: http://neil.fraser.name/news/2010/11/04/
        int best = 0;
        int length = 1;
        while (true) {
            String pattern = text1.substring(textLength - length);
            int found = text2.indexOf(pattern);
            if (found == -1) {
                return best;
            }
            length += found;
            if (found == 0 || text1.substring(textLength - length).equals(
                    text2.substring(0, length))) {
                best = length;
                length++;
            }
        }
    }

    /**
     * Do the two texts share a substring which is at least half the length of
     * the longer text?
     * This speedup can produce non-minimal diffs.
     * @param text1 First string.
     * @param text2 Second string.
     * @return Five element String array, containing the prefix of text1, the
     *     suffix of text1, the prefix of text2, the suffix of text2 and the
     *     common middle.  Or null if there was no match.
     */
    protected String[] diffHalfMatch(String text1, String text2) {
        String longtext = text1.length() > text2.length() ? text1 : text2;
        String shorttext = text1.length() > text2.length() ? text2 : text1;
        if (longtext.length() < 4 || shorttext.length() * 2 < longtext.length()) {
            return null;  // Pointless.
        }

        // First check if the second quarter is the seed for a half-match.
        String[] hm1 = this.diffHalfMatchI(longtext, shorttext, (longtext.length() + 3) / 4);
        // Check again based on the third quarter.
        String[] hm2 = this.diffHalfMatchI(longtext, shorttext, (longtext.length() + 1) / 2);
        String[] hm;
        if (hm1 == null && hm2 == null) {
            return null;
        } else if (hm2 == null) {
            hm = hm1;
        } else if (hm1 == null) {
            hm = hm2;
        } else {
            // Both matched.  Select the longest.
            hm = hm1[4].length() > hm2[4].length() ? hm1 : hm2;
        }

        // A half-match was found, sort out the return data.
        if (text1.length() > text2.length()) {
            return hm;
        } else {
            return new String[]{hm[2], hm[3], hm[0], hm[1], hm[4]};
        }
    }

    /**
     * Does a substring of shorttext exist within longtext such that the
     * substring is at least half the length of longtext?
     * @param longtext Longer string.
     * @param shorttext Shorter string.
     * @param i Start index of quarter length substring within longtext.
     * @return Five element String array, containing the prefix of longtext, the
     *     suffix of longtext, the prefix of shorttext, the suffix of shorttext
     *     and the common middle.  Or null if there was no match.
     */
    private String[] diffHalfMatchI(String longtext, String shorttext, int i) {
        // Start with a 1/4 length substring at position i as a seed.
        String seed = longtext.substring(i, i + longtext.length() / 4);
        int j = -1;
        String bestCommon = "";
        String bestLongtextA = "";
        String bestLongtextB = "";
        String bestShorttextA = "";
        String bestShorttextB = "";
        while ((j = shorttext.indexOf(seed, j + 1)) != -1) {
            int prefixLength = this.diffCommonPrefix(longtext.substring(i),
                    shorttext.substring(j));
            int suffixLength = this.diffCommonSuffix(longtext.substring(0, i),
                    shorttext.substring(0, j));
            if (bestCommon.length() < suffixLength + prefixLength) {
                bestCommon = shorttext.substring(j - suffixLength, j)
                        + shorttext.substring(j, j + prefixLength);
                bestLongtextA = longtext.substring(0, i - suffixLength);
                bestLongtextB = longtext.substring(i + prefixLength);
                bestShorttextA = shorttext.substring(0, j - suffixLength);
                bestShorttextB = shorttext.substring(j + prefixLength);
            }
        }
        if (bestCommon.length() * 2 >= longtext.length()) {
            return new String[]{bestLongtextA, bestLongtextB,
                    bestShorttextA, bestShorttextB, bestCommon};
        } else {
            return null;
        }
    }

    /**
     * Reduce the number of edits by eliminating semantically trivial equalities.
     * @param diffs LinkedList of Diff objects.
     */
    public void diffCleanupSemantic(LinkedList<Diff> diffs) {
        if (diffs.isEmpty()) {
            return;
        }
        boolean changes = false;
        // Synchronized Stack to avoid Exception
        Stack<Diff> equalities = new Stack<>();  // Stack of qualities.
        String lastequality = null; // Always equal to equalities.lastElement().text
        ListIterator<Diff> pointer = diffs.listIterator();
        // Number of characters that changed prior to the equality.
        int lengthInsertions1 = 0;
        int lengthDeletions1 = 0;
        // Number of characters that changed after the equality.
        int lengthInsertions2 = 0;
        int lengthDeletions2 = 0;
        Diff thisDiff = pointer.next();
        while (thisDiff != null) {
            if (thisDiff.getOperation() == Operation.EQUAL) {
                // Equality found.
                equalities.push(thisDiff);
                lengthInsertions1 = lengthInsertions2;
                lengthDeletions1 = lengthDeletions2;
                lengthInsertions2 = 0;
                lengthDeletions2 = 0;
                lastequality = thisDiff.getText();
            } else {
                // An insertion or deletion.
                if (thisDiff.getOperation() == Operation.INSERT) {
                    lengthInsertions2 += thisDiff.getText().length();
                } else {
                    lengthDeletions2 += thisDiff.getText().length();
                }
                // Eliminate an equality that is smaller or equal to the edits on both
                // sides of it.
                if (
                    lastequality != null
                    && lastequality.length() <= Math.max(lengthInsertions1, lengthDeletions1)
                    && lastequality.length() <= Math.max(lengthInsertions2, lengthDeletions2)
                ) {
                    // Walk back to offending equality.
                    while (thisDiff != equalities.lastElement()) {
                        thisDiff = pointer.previous();
                    }
                    pointer.next();

                    // Replace equality with a delete.
                    pointer.set(new Diff(Operation.DELETE, lastequality));
                    // Insert a corresponding an insert.
                    pointer.add(new Diff(Operation.INSERT, lastequality));

                    equalities.pop();  // Throw away the equality we just deleted.
                    if (!equalities.empty()) {
                        // Throw away the previous equality (it needs to be reevaluated).
                        equalities.pop();
                    }
                    if (equalities.empty()) {
                        // There are no previous equalities, walk back to the start.
                        while (pointer.hasPrevious()) {
                            pointer.previous();
                        }
                    } else {
                        // There is a safe equality we can fall back to.
                        thisDiff = equalities.lastElement();
                        while (thisDiff != pointer.previous()) {
                            // Intentionally empty loop.
                        }
                    }

                    lengthInsertions1 = 0;  // Reset the counters.
                    lengthInsertions2 = 0;
                    lengthDeletions1 = 0;
                    lengthDeletions2 = 0;
                    lastequality = null;
                    changes = true;
                }
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }

        // Normalize the diff.
        if (changes) {
            this.diffCleanupMerge(diffs);
        }
        this.diffCleanupSemanticLossless(diffs);

        // Find any overlaps between deletions and insertions.
        // e.g: <del>abcxxx</del><ins>xxxdef</ins>
        //   -> <del>abc</del>xxx<ins>def</ins>
        // e.g: <del>xxxabc</del><ins>defxxx</ins>
        //   -> <ins>def</ins>xxx<del>abc</del>
        // Only extract an overlap if it is as big as the edit ahead or behind it.
        pointer = diffs.listIterator();
        Diff prevDiff = null;
        thisDiff = null;
        if (pointer.hasNext()) {
            prevDiff = pointer.next();
            if (pointer.hasNext()) {
                thisDiff = pointer.next();
            }
        }
        while (thisDiff != null) {
            if (prevDiff.getOperation() == Operation.DELETE &&
                    thisDiff.getOperation() == Operation.INSERT) {
                String deletion = prevDiff.getText();
                String insertion = thisDiff.getText();
                int overlapLength1 = this.diffCommonOverlap(deletion, insertion);
                int overlapLength2 = this.diffCommonOverlap(insertion, deletion);
                if (overlapLength1 >= overlapLength2) {
                    if (overlapLength1 >= deletion.length() / 2.0 ||
                            overlapLength1 >= insertion.length() / 2.0) {
                        // Overlap found.  Insert an equality and trim the surrounding edits.
                        pointer.previous();
                        pointer.add(new Diff(Operation.EQUAL,
                                insertion.substring(0, overlapLength1)));
                        prevDiff.setText(deletion.substring(0, deletion.length() - overlapLength1));
                        thisDiff.setText(insertion.substring(overlapLength1));
                        // pointer.add inserts the element before the cursor, so there is
                        // no need to step past the new element.
                    }
                } else {
                    if (overlapLength2 >= deletion.length() / 2.0 ||
                            overlapLength2 >= insertion.length() / 2.0) {
                        // Reverse overlap found.
                        // Insert an equality and swap and trim the surrounding edits.
                        pointer.previous();
                        pointer.add(new Diff(Operation.EQUAL,
                                deletion.substring(0, overlapLength2)));
                        prevDiff.setOperation(Operation.INSERT);
                        prevDiff.setText(insertion.substring(0, insertion.length() - overlapLength2));
                        thisDiff.setOperation(Operation.DELETE);
                        thisDiff.setText(deletion.substring(overlapLength2));
                        // pointer.add inserts the element before the cursor, so there is
                        // no need to step past the new element.
                    }
                }
                thisDiff = pointer.hasNext() ? pointer.next() : null;
            }
            prevDiff = thisDiff;
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }
    }

    /**
     * Look for single edits surrounded on both sides by equalities
     * which can be shifted sideways to align the edit to a word boundary.
     * e.g: The c<ins>at c</ins>ame. -> The <ins>cat </ins>came.
     * @param diffs LinkedList of Diff objects.
     */
    public void diffCleanupSemanticLossless(List<Diff> diffs) {
        StringBuilder equality1 = new StringBuilder();
        String edit;
        StringBuilder equality2 = new StringBuilder();
        String commonString;
        int commonOffset;
        int score;
        int bestScore;
        String bestEquality1;
        String bestEdit;
        String bestEquality2;
        // Create a new iterator at the start.
        ListIterator<Diff> pointer = diffs.listIterator();
        Diff prevDiff = pointer.hasNext() ? pointer.next() : null;
        Diff thisDiff = pointer.hasNext() ? pointer.next() : null;
        Diff nextDiff = pointer.hasNext() ? pointer.next() : null;
        // Intentionally ignore the first and last element (don't need checking).
        while (nextDiff != null) {
            if (prevDiff.getOperation() == Operation.EQUAL &&
                    nextDiff.getOperation() == Operation.EQUAL) {
                // This is a single edit surrounded by equalities.
                equality1.setLength(0);
                equality1.append(prevDiff.getText());
                edit = thisDiff.getText();
                equality2.setLength(0);
                equality2.append(nextDiff.getText());

                // First, shift the edit as far left as possible.
                commonOffset = this.diffCommonSuffix(equality1.toString(), edit);
                if (commonOffset != 0) {
                    commonString = edit.substring(edit.length() - commonOffset);
                    String substring = equality1.substring(0, equality1.length() - commonOffset);
                    equality1.setLength(0);
                    equality1.append(substring);
                    edit = commonString + edit.substring(0, edit.length() - commonOffset);
                    equality2.insert(0, commonString);
                }

                // Second, step character by character right, looking for the best fit.
                bestEquality1 = equality1.toString();
                bestEdit = edit;
                bestEquality2 = equality2.toString();
                bestScore = this.diffCleanupSemanticScore(equality1.toString(), edit)
                        + this.diffCleanupSemanticScore(edit, equality2.toString());
                while (edit.length() != 0 && equality2.length() != 0
                        && edit.charAt(0) == equality2.charAt(0)) {
                    equality1.append(Character.toString(edit.charAt(0)));
                    edit = edit.substring(1) + equality2.charAt(0);
                    String substring = equality2.substring(1);
                    equality2.setLength(0);
                    equality2.append(substring);
                    score = this.diffCleanupSemanticScore(equality1.toString(), edit)
                            + this.diffCleanupSemanticScore(edit, equality2.toString());
                    // The >= encourages trailing rather than leading whitespace on edits.
                    if (score >= bestScore) {
                        bestScore = score;
                        bestEquality1 = equality1.toString();
                        bestEdit = edit;
                        bestEquality2 = equality2.toString();
                    }
                }

                if (!prevDiff.getText().equals(bestEquality1)) {
                    // We have an improvement, save it back to the diff.
                    if (bestEquality1.length() != 0) {
                        prevDiff.setText(bestEquality1);
                    } else {
                        pointer.previous(); // Walk past nextDiff.
                        pointer.previous(); // Walk past thisDiff.
                        pointer.previous(); // Walk past prevDiff.
                        pointer.remove(); // Delete prevDiff.
                        pointer.next(); // Walk past thisDiff.
                        pointer.next(); // Walk past nextDiff.
                    }
                    thisDiff.setText(bestEdit);
                    if (bestEquality2.length() != 0) {
                        nextDiff.setText(bestEquality2);
                    } else {
                        pointer.remove(); // Delete nextDiff.
                        nextDiff = thisDiff;
                        thisDiff = prevDiff;
                    }
                }
            }
            prevDiff = thisDiff;
            thisDiff = nextDiff;
            nextDiff = pointer.hasNext() ? pointer.next() : null;
        }
    }

    /**
     * Given two strings, compute a score representing whether the internal
     * boundary falls on logical boundaries.
     * Scores range from 6 (best) to 0 (worst).
     * @param one First string.
     * @param two Second string.
     * @return The score.
     */
    private int diffCleanupSemanticScore(String one, String two) {
        if (one.length() == 0 || two.length() == 0) {
            // Edges are the best.
            return 6;
        }

        // Each port of this function behaves slightly differently due to
        // subtle differences in each language's definition of things like
        // 'whitespace'.  Since this function's purpose is largely cosmetic,
        // the choice has been made to use each language's native features
        // rather than force total conformity.
        char char1 = one.charAt(one.length() - 1);
        char char2 = two.charAt(0);
        boolean nonAlphaNumeric1 = !Character.isLetterOrDigit(char1);
        boolean nonAlphaNumeric2 = !Character.isLetterOrDigit(char2);
        boolean whitespace1 = nonAlphaNumeric1 && Character.isWhitespace(char1);
        boolean whitespace2 = nonAlphaNumeric2 && Character.isWhitespace(char2);
        boolean lineBreak1 = whitespace1
                && Character.getType(char1) == Character.CONTROL;
        boolean lineBreak2 = whitespace2
                && Character.getType(char2) == Character.CONTROL;
        boolean blankLine1 = lineBreak1 && BLANK_LINE_END.matcher(one).find();
        boolean blankLine2 = lineBreak2 && BLANK_LINE_START.matcher(two).find();

        if (blankLine1 || blankLine2) {
            // Five points for blank lines.
            return 5;
        } else if (lineBreak1 || lineBreak2) {
            // Four points for line breaks.
            return 4;
        } else if (nonAlphaNumeric1 && !whitespace1 && whitespace2) {
            // Three points for end of sentences.
            return 3;
        } else if (whitespace1 || whitespace2) {
            // Two points for whitespace.
            return 2;
        } else if (nonAlphaNumeric1 || nonAlphaNumeric2) {
            // One point for non-alphanumeric.
            return 1;
        }
        return 0;
    }

    /**
     * Reduce the number of edits by eliminating operationally trivial equalities.
     * @param diffs LinkedList of Diff objects.
     */
    public void diffCleanupEfficiency(LinkedList<Diff> diffs) {
        if (diffs.isEmpty()) {
            return;
        }
        boolean changes = false;
        // Synchronized Stack to avoid Exception
        Stack<Diff> equalities = new Stack<>();  // Stack of equalities.
        String lastequality = null; // Always equal to equalities.lastElement().text
        ListIterator<Diff> pointer = diffs.listIterator();
        // Is there an insertion operation before the last equality.
        boolean preIns = false;
        // Is there a deletion operation before the last equality.
        boolean preDel = false;
        // Is there an insertion operation after the last equality.
        boolean postIns = false;
        // Is there a deletion operation after the last equality.
        boolean postDel = false;
        Diff thisDiff = pointer.next();
        Diff safeDiff = thisDiff;  // The last Diff that is known to be unsplitable.
        while (thisDiff != null) {
            if (thisDiff.getOperation() == Operation.EQUAL) {
                // Equality found.
                if (thisDiff.getText().length() < DIFF_EDIT_COST && (postIns || postDel)) {
                    // Candidate found.
                    equalities.push(thisDiff);
                    preIns = postIns;
                    preDel = postDel;
                    lastequality = thisDiff.getText();
                } else {
                    // Not a candidate, and can never become one.
                    equalities.clear();
                    lastequality = null;
                    safeDiff = thisDiff;
                }
                postIns = postDel = false;
            } else {
                // An insertion or deletion.
                if (thisDiff.getOperation() == Operation.DELETE) {
                    postDel = true;
                } else {
                    postIns = true;
                }
                /*
                 * Five types to be split:
                 * <ins>A</ins><del>B</del>XY<ins>C</ins><del>D</del>
                 * <ins>A</ins>X<ins>C</ins><del>D</del>
                 * <ins>A</ins><del>B</del>X<ins>C</ins>
                 * <ins>A</del>X<ins>C</ins><del>D</del>
                 * <ins>A</ins><del>B</del>X<del>C</del>
                 */
                if (
                    lastequality != null
                    && (
                        (preIns && preDel && postIns && postDel)
                        || (
                            (lastequality.length() < DIFF_EDIT_COST / 2)
                            && ((preIns ? 1 : 0) + (preDel ? 1 : 0) + (postIns ? 1 : 0) + (postDel ? 1 : 0)) == 3
                        )
                    )
                ) {
                    // Walk back to offending equality.
                    while (thisDiff != equalities.lastElement()) {
                        thisDiff = pointer.previous();
                    }
                    pointer.next();

                    // Replace equality with a delete.
                    pointer.set(new Diff(Operation.DELETE, lastequality));
                    // Insert a corresponding an insert.
                    thisDiff = new Diff(Operation.INSERT, lastequality);
                    pointer.add(thisDiff);

                    equalities.pop();  // Throw away the equality we just deleted.
                    lastequality = null;
                    if (preIns && preDel) {
                        // No changes made which could affect previous entry, keep going.
                        postIns = postDel = true;
                        equalities.clear();
                        safeDiff = thisDiff;
                    } else {
                        if (!equalities.empty()) {
                            // Throw away the previous equality (it needs to be reevaluated).
                            equalities.pop();
                        }
                        if (equalities.empty()) {
                            // There are no previous questionable equalities,
                            // walk back to the last known safe diff.
                            thisDiff = safeDiff;
                        } else {
                            // There is an equality we can fall back to.
                            thisDiff = equalities.lastElement();
                        }
                        while (thisDiff != pointer.previous()) {
                            // Intentionally empty loop.
                        }
                        postIns = postDel = false;
                    }

                    changes = true;
                }
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }

        if (changes) {
            this.diffCleanupMerge(diffs);
        }
    }

    /**
     * Reorder and merge like edit sections.  Merge equalities.
     * Any edit section can move as long as it doesn't cross an equality.
     * @param diffs LinkedList of Diff objects.
     */
    public void diffCleanupMerge(LinkedList<Diff> diffs) {
        diffs.add(new Diff(Operation.EQUAL, ""));  // Add a dummy entry at the end.
        ListIterator<Diff> pointer = diffs.listIterator();
        int countDelete = 0;
        int countInsert = 0;
        StringBuilder textDelete = new StringBuilder();
        StringBuilder textInsert = new StringBuilder();
        Diff thisDiff = pointer.next();
        Diff prevEqual = null;
        int commonlength;
        while (thisDiff != null) {
            switch (thisDiff.getOperation()) {
            case INSERT:
                countInsert++;
                textInsert.append(thisDiff.getText());
                prevEqual = null;
                break;
            case DELETE:
                countDelete++;
                textDelete.append(thisDiff.getText());
                prevEqual = null;
                break;
            case EQUAL:
                if (countDelete + countInsert > 1) {
                    boolean bothTypes = countDelete != 0 && countInsert != 0;
                    // Delete the offending records.
                    pointer.previous();  // Reverse direction.
                    while (countDelete-- > 0) {
                        pointer.previous();
                        pointer.remove();
                    }
                    while (countInsert-- > 0) {
                        pointer.previous();
                        pointer.remove();
                    }
                    if (bothTypes) {
                        // Factor out any common prefixies.
                        commonlength = this.diffCommonPrefix(textInsert.toString(), textDelete.toString());
                        if (commonlength != 0) {
                            if (pointer.hasPrevious()) {
                                thisDiff = pointer.previous();
                                // Previous diff should have been an equality: thisDiff.getOperation() == Operation.EQUAL")
                                thisDiff.setText(thisDiff.getText() + textInsert.substring(0, commonlength));
                                pointer.next();
                            } else {
                                pointer.add(new Diff(Operation.EQUAL,
                                        textInsert.substring(0, commonlength)));
                            }
                            String substringIns = textInsert.substring(commonlength);
                            textInsert.setLength(0);
                            textInsert.append(substringIns);
                            String substringDel = textDelete.substring(commonlength);
                            textDelete.setLength(0);
                            textDelete.append(substringDel);
                        }
                        // Factor out any common suffixies.
                        commonlength = this.diffCommonSuffix(textInsert.toString(), textDelete.toString());
                        if (commonlength != 0) {
                            thisDiff = pointer.next();
                            thisDiff.setText(textInsert.substring(textInsert.length() - commonlength) + thisDiff.getText());
                            String substringIns = textInsert.substring(0, textInsert.length() - commonlength);
                            textInsert.setLength(0);
                            textInsert.append(substringIns);
                            String substringDel = textDelete.substring(0, textDelete.length() - commonlength);
                            textDelete.setLength(0);
                            textDelete.append(substringDel);
                            pointer.previous();
                        }
                    }
                    // Insert the merged records.
                    if (textDelete.length() != 0) {
                        pointer.add(new Diff(Operation.DELETE, textDelete.toString()));
                    }
                    if (textInsert.length() != 0) {
                        pointer.add(new Diff(Operation.INSERT, textInsert.toString()));
                    }
                    // Step forward to the equality.
                    thisDiff = pointer.hasNext() ? pointer.next() : null;
                } else if (prevEqual != null) {
                    // Merge this equality with the previous one.
                    prevEqual.setText(prevEqual.getText() + thisDiff.getText());
                    pointer.remove();
                    thisDiff = pointer.previous();
                    pointer.next();  // Forward direction
                }
                countInsert = 0;
                countDelete = 0;
                textDelete.setLength(0);
                textInsert.setLength(0);
                prevEqual = thisDiff;
                break;
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }
        if (diffs.getLast().getText().length() == 0) {
            diffs.removeLast();  // Remove the dummy entry at the end.
        }

        /*
         * Second pass: look for single edits surrounded on both sides by equalities
         * which can be shifted sideways to eliminate an equality.
         * e.g: A<ins>BA</ins>C -> <ins>AB</ins>AC
         */
        boolean changes = false;
        // Create a new iterator at the start.
        // (As opposed to walking the current one back.)
        pointer = diffs.listIterator();
        Diff prevDiff = pointer.hasNext() ? pointer.next() : null;
        thisDiff = pointer.hasNext() ? pointer.next() : null;
        Diff nextDiff = pointer.hasNext() ? pointer.next() : null;
        // Intentionally ignore the first and last element (don't need checking).
        while (nextDiff != null) {
            if (prevDiff.getOperation() == Operation.EQUAL &&
                    nextDiff.getOperation() == Operation.EQUAL) {
                // This is a single edit surrounded by equalities.
                if (thisDiff.getText().endsWith(prevDiff.getText())) {
                    // Shift the edit over the previous equality.
                    thisDiff.setText(prevDiff.getText()
                            + thisDiff.getText().substring(0, thisDiff.getText().length()
                                    - prevDiff.getText().length()));
                    nextDiff.setText(prevDiff.getText() + nextDiff.getText());
                    pointer.previous(); // Walk past nextDiff.
                    pointer.previous(); // Walk past thisDiff.
                    pointer.previous(); // Walk past prevDiff.
                    pointer.remove(); // Delete prevDiff.
                    pointer.next(); // Walk past thisDiff.
                    thisDiff = pointer.next(); // Walk past nextDiff.
                    nextDiff = pointer.hasNext() ? pointer.next() : null;
                    changes = true;
                } else if (thisDiff.getText().startsWith(nextDiff.getText())) {
                    // Shift the edit over the next equality.
                    prevDiff.setText(prevDiff.getText() + nextDiff.getText());
                    thisDiff.setText(thisDiff.getText().substring(nextDiff.getText().length())
                            + nextDiff.getText());
                    pointer.remove(); // Delete nextDiff.
                    nextDiff = pointer.hasNext() ? pointer.next() : null;
                    changes = true;
                }
            }
            prevDiff = thisDiff;
            thisDiff = nextDiff;
            nextDiff = pointer.hasNext() ? pointer.next() : null;
        }
        // If shifts were made, the diff needs reordering and another shift sweep.
        if (changes) {
            this.diffCleanupMerge(diffs);
        }
    }

    /**
     * loc is a location in text1, compute and return the equivalent location in
     * text2.
     * e.g. "The cat" vs "The big cat", 1->1, 5->8
     * @param diffs List of Diff objects.
     * @param loc Location within text1.
     * @return Location within text2.
     */
    public int diffXIndex(List<Diff> diffs, int loc) {
        int chars1 = 0;
        int chars2 = 0;
        int lastChars1 = 0;
        int lastChars2 = 0;
        Diff lastDiff = null;
        for (Diff aDiff : diffs) {
            if (aDiff.getOperation() != Operation.INSERT) {
                // Equality or deletion.
                chars1 += aDiff.getText().length();
            }
            if (aDiff.getOperation() != Operation.DELETE) {
                // Equality or insertion.
                chars2 += aDiff.getText().length();
            }
            if (chars1 > loc) {
                // Overshot the location.
                lastDiff = aDiff;
                break;
            }
            lastChars1 = chars1;
            lastChars2 = chars2;
        }
        if (lastDiff != null && lastDiff.getOperation() == Operation.DELETE) {
            // The location was deleted.
            return lastChars2;
        }
        // Add the remaining character length.
        return lastChars2 + (loc - lastChars1);
    }

    /**
     * Convert a Diff list into a pretty HTML report.
     * @param diffs List of Diff objects.
     * @return HTML representation.
     */
    public String diffPrettyHtml(List<Diff> diffs) {
        StringBuilder html = new StringBuilder();
        for (Diff aDiff : diffs) {
            String text = aDiff.getText().replace("&", "&amp;").replace("<", "&lt;")
                    .replace(">", "&gt;").replace("\n", "&para;<br>");
            switch (aDiff.getOperation()) {
            case INSERT:
                html.append("<ins style=\"background:#e6ffe6;\">").append(text)
                .append("</ins>");
                break;
            case DELETE:
                html.append("<del style=\"background:#ffe6e6;\">").append(text)
                .append("</del>");
                break;
            case EQUAL:
                html.append("<span>").append(text).append("</span>");
                break;
            }
        }
        return html.toString();
    }

    /**
     * Compute and return the source text (all equalities and deletions).
     * @param diffs List of Diff objects.
     * @return Source text.
     */
    public String diffText1(List<Diff> diffs) {
        StringBuilder text = new StringBuilder();
        for (Diff aDiff : diffs) {
            if (aDiff.getOperation() != Operation.INSERT) {
                text.append(aDiff.getText());
            }
        }
        return text.toString();
    }

    /**
     * Compute and return the destination text (all equalities and insertions).
     * @param diffs List of Diff objects.
     * @return Destination text.
     */
    public String diffText2(List<Diff> diffs) {
        StringBuilder text = new StringBuilder();
        for (Diff aDiff : diffs) {
            if (aDiff.getOperation() != Operation.DELETE) {
                text.append(aDiff.getText());
            }
        }
        return text.toString();
    }

    /**
     * Compute the Levenshtein distance; the number of inserted, deleted or
     * substituted characters.
     * @param diffs List of Diff objects.
     * @return Number of changes.
     */
    public int diffLevenshtein(List<Diff> diffs) {
        int levenshtein = 0;
        int insertions = 0;
        int deletions = 0;
        for (Diff aDiff : diffs) {
            switch (aDiff.getOperation()) {
            case INSERT:
                insertions += aDiff.getText().length();
                break;
            case DELETE:
                deletions += aDiff.getText().length();
                break;
            case EQUAL:
                // A deletion and an insertion is one substitution.
                levenshtein += Math.max(insertions, deletions);
                insertions = 0;
                deletions = 0;
                break;
            }
        }
        levenshtein += Math.max(insertions, deletions);
        return levenshtein;
    }

    /**
     * Crush the diff into an encoded string which describes the operations
     * required to transform text1 into text2.
     * E.g. =3\t-2\t+ing  -> Keep 3 chars, delete 2 chars, insert 'ing'.
     * Operations are tab-separated.  Inserted text is escaped using %xx notation.
     * @param diffs Array of Diff objects.
     * @return Delta text.
     * @throws UnsupportedEncodingException
     */
    public String diffToDelta(List<Diff> diffs) {
        StringBuilder text = new StringBuilder();
        for (Diff aDiff : diffs) {
            switch (aDiff.getOperation()) {
            case INSERT:
                text.append("+").append(URLEncoder.encode(aDiff.getText(), StandardCharsets.UTF_8)
                        .replace('+', ' ')).append("\t");
                break;
            case DELETE:
                text.append("-").append(aDiff.getText().length()).append("\t");
                break;
            case EQUAL:
                text.append("=").append(aDiff.getText().length()).append("\t");
                break;
            }
        }
        String delta = text.toString();
        if (delta.length() != 0) {
            // Strip off trailing tab character.
            delta = delta.substring(0, delta.length() - 1);
            delta = Patch.unescapeForEncodeUriCompatability(delta);
        }
        return delta;
    }

    /**
     * Given the original text1, and an encoded string which describes the
     * operations required to transform text1 into text2, compute the full diff.
     * @param text1 Source string for the diff.
     * @param delta Delta text.
     * @return Array of Diff objects or null if invalid.
     * @throws IllegalArgumentException If invalid input.
     * @throws UnsupportedEncodingException
     */
    public List<Diff> diffFromDelta(String text1, String delta) throws UnsupportedEncodingException {
        List<Diff> diffs = new LinkedList<>();
        int pointer = 0;  // Cursor in text1
        String[] tokens = delta.split("\t");
        for (String token : tokens) {
            if (token.length() == 0) {
                // Blank tokens are ok (from a trailing \t).
                continue;
            }
            // Each token begins with a one character parameter which specifies the
            // operation of this token (delete, insert, equality).
            String param = token.substring(1);
            switch (token.charAt(0)) {
            case '+':
                // decode would change all "+" to " "
                param = param.replace("+", "%2B");
                try {
                    param = URLDecoder.decode(param, StandardCharsets.UTF_8);
                } catch (IllegalArgumentException e) {
                    // Malformed URI sequence.
                    throw new IllegalArgumentException(
                            "Illegal escape in diff_fromDelta: " + param, e);
                }
                diffs.add(new Diff(Operation.INSERT, param));
                break;
            case '-':
                // Fall through.
            case '=':
                int n;
                try {
                    n = Integer.parseInt(param);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Invalid number in diff_fromDelta: " + param, e);
                }
                if (n < 0) {
                    throw new IllegalArgumentException(
                            "Negative number in diff_fromDelta: " + param);
                }
                String text;
                try {
                    int p1 = pointer;
                    pointer += n;
                    int p2 = pointer;
                    text = text1.substring(p1, p2);
                } catch (StringIndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("Delta length (" + pointer
                            + ") larger than source text length (" + text1.length()
                            + ").", e);
                }
                if (token.charAt(0) == '=') {
                    diffs.add(new Diff(Operation.EQUAL, text));
                } else {
                    diffs.add(new Diff(Operation.DELETE, text));
                }
                break;
            default:
                // Anything else is an error.
                throw new IllegalArgumentException(
                        "Invalid diff operation in diff_fromDelta: " + token.charAt(0));
            }
        }
        if (pointer != text1.length()) {
            throw new IllegalArgumentException("Delta length (" + pointer
                    + ") smaller than source text length (" + text1.length() + ").");
        }
        return diffs;
    }


    //  MATCH FUNCTIONS


    /**
     * Locate the best instance of 'pattern' in 'text' near 'loc'.
     * Returns -1 if no match found.
     * @param text The text to search.
     * @param pattern The pattern to search for.
     * @param valueLoc The location to search around.
     * @return Best match index or -1.
     */
    public int matchMain(String text, String pattern, int valueLoc) {
        // Check for null inputs.
        if (text == null || pattern == null) {
            throw new IllegalArgumentException("Null inputs. (match_main)");
        }

        int loc = Math.max(0, Math.min(valueLoc, text.length()));
        if (text.equals(pattern)) {
            // Shortcut (potentially not guaranteed by the algorithm)
            return 0;
        } else if (text.length() == 0) {
            // Nothing to match.
            return -1;
        } else if (loc + pattern.length() <= text.length()
                && text.substring(loc, loc + pattern.length()).equals(pattern)) {
            // Perfect match at the perfect spot!  (Includes case of null pattern)
            return loc;
        } else {
            // Do a fuzzy compare.
            return this.matchBitap(text, pattern, loc);
        }
    }

    /**
     * Locate the best instance of 'pattern' in 'text' near 'loc' using the
     * Bitap algorithm.  Returns -1 if no match found.
     * @param text The text to search.
     * @param pattern The pattern to search for.
     * @param loc The location to search around.
     * @return Best match index or -1.
     */
    protected int matchBitap(String text, String pattern, int loc) {
        // Initialise the alphabet.
        Map<Character, Integer> s = this.matchAlphabet(pattern);

        // Highest score beyond which we give up.
        double scoreThreshold = MATCH_THRESHOLD;
        // Is there a nearby exact match? (speedup)
        int bestLoc = text.indexOf(pattern, loc);
        if (bestLoc != -1) {
            scoreThreshold = Math.min(this.matchBitapScore(0, bestLoc, loc, pattern),
                    scoreThreshold);
            // What about in the other direction? (speedup)
            bestLoc = text.lastIndexOf(pattern, loc + pattern.length());
            if (bestLoc != -1) {
                scoreThreshold = Math.min(this.matchBitapScore(0, bestLoc, loc, pattern),
                        scoreThreshold);
            }
        }

        // Initialise the bit arrays.
        int matchmask = 1 << (pattern.length() - 1);
        bestLoc = -1;

        int binMin;
        int binMid;
        int binMax = pattern.length() + text.length();
        // Empty initialization added to appease Java compiler.
        int[] lastRd = new int[0];
        for (int d = 0; d < pattern.length(); d++) {
            // Scan for the best match; each iteration allows for one more error.
            // Run a binary search to determine how far from 'loc' we can stray at
            // this error level.
            binMin = 0;
            binMid = binMax;
            while (binMin < binMid) {
                if (this.matchBitapScore(d, loc + binMid, loc, pattern)
                        <= scoreThreshold) {
                    binMin = binMid;
                } else {
                    binMax = binMid;
                }
                binMid = (binMax - binMin) / 2 + binMin;
            }
            // Use the result from this iteration as the maximum for the next.
            binMax = binMid;
            int start = Math.max(1, loc - binMid + 1);
            int finish = Math.min(loc + binMid, text.length()) + pattern.length();

            int[] rd = new int[finish + 2];
            rd[finish + 1] = (1 << d) - 1;
            for (int j = finish; j >= start; j--) {
                int charMatch;
                if (text.length() <= j - 1 || !s.containsKey(text.charAt(j - 1))) {
                    // Out of range.
                    charMatch = 0;
                } else {
                    charMatch = s.get(text.charAt(j - 1));
                }
                if (d == 0) {
                    // First pass: exact match.
                    rd[j] = ((rd[j + 1] << 1) | 1) & charMatch;
                } else {
                    // Subsequent passes: fuzzy match.
                    rd[j] = (((rd[j + 1] << 1) | 1) & charMatch)
                            | (((lastRd[j + 1] | lastRd[j]) << 1) | 1) | lastRd[j + 1];
                }
                if ((rd[j] & matchmask) != 0) {
                    double score = this.matchBitapScore(d, j - 1, loc, pattern);
                    // This match will almost certainly be better than any existing
                    // match.  But check anyway.
                    if (score <= scoreThreshold) {
                        // Told you so.
                        scoreThreshold = score;
                        bestLoc = j - 1;
                        if (bestLoc > loc) {
                            // When passing loc, don't exceed our current distance from loc.
                            start = Math.max(1, 2 * loc - bestLoc);
                        } else {
                            // Already passed loc, downhill from here on in.
                            break;
                        }
                    }
                }
            }
            if (this.matchBitapScore(d + 1, loc, loc, pattern) > scoreThreshold) {
                // No hope for a (better) match at greater error levels.
                break;
            }
            lastRd = rd;
        }
        return bestLoc;
    }

    /**
     * Compute and return the score for a match with e errors and x location.
     * @param e Number of errors in match.
     * @param x Location of match.
     * @param loc Expected location of match.
     * @param pattern Pattern being sought.
     * @return Overall score for match (0.0 = good, 1.0 = bad).
     */
    private double matchBitapScore(int e, int x, int loc, String pattern) {
        float accuracy = (float) e / pattern.length();
        int proximity = Math.abs(loc - x);
        return accuracy + (proximity / (float) MATCH_DISTANCE);
    }

    /**
     * Initialise the alphabet for the Bitap algorithm.
     * @param pattern The text to encode.
     * @return Hash of character locations.
     */
    protected Map<Character, Integer> matchAlphabet(String pattern) {
        Map<Character, Integer> s = new HashMap<>();
        char[] charPattern = pattern.toCharArray();
        for (char c : charPattern) {
            s.put(c, 0);
        }
        int i = 0;
        for (char c : charPattern) {
            s.put(c, s.get(c) | (1 << (pattern.length() - i - 1)));
            i++;
        }
        return s;
    }


    //  PATCH FUNCTIONS


    /**
     * Increase the context until it is unique,
     * but don't let the pattern expand beyond Match_MaxBits.
     * @param patch The patch to grow.
     * @param text Source text.
     */
    protected void patchAddContext(Patch patch, String text) {
        if (text.length() == 0) {
            return;
        }
        String pattern = text.substring(patch.getStart2(), patch.getStart2() + patch.getLength1());
        int padding = 0;

        // Look for the first and last matches of pattern in text.  If two different
        // matches are found, increase the pattern length.
        while (text.indexOf(pattern) != text.lastIndexOf(pattern)
                && pattern.length() < DiffMatchPatch.MATCH_MAX_BITS - PATCH_MARGIN - PATCH_MARGIN) {
            padding += PATCH_MARGIN;
            pattern = text.substring(Math.max(0, patch.getStart2() - padding),
                    Math.min(text.length(), patch.getStart2() + patch.getLength1() + padding));
        }
        // Add one chunk for good luck.
        padding += PATCH_MARGIN;

        // Add the prefix.
        String prefix = text.substring(Math.max(0, patch.getStart2() - padding),
                patch.getStart2());
        if (prefix.length() != 0) {
            patch.getDiffs().addFirst(new Diff(Operation.EQUAL, prefix));
        }
        // Add the suffix.
        String suffix = text.substring(patch.getStart2() + patch.getLength1(),
                Math.min(text.length(), patch.getStart2() + patch.getLength1() + padding));
        if (suffix.length() != 0) {
            patch.getDiffs().addLast(new Diff(Operation.EQUAL, suffix));
        }

        // Roll back the start points.
        patch.setStart1(patch.getStart1() - prefix.length());
        patch.setStart2(patch.getStart2() - prefix.length());
        // Extend the lengths.
        patch.setLength1(patch.getLength1() + prefix.length() + suffix.length());
        patch.setLength2(patch.getLength2() + prefix.length() + suffix.length());
    }

    /**
     * Compute a list of patches to turn text1 into text2.
     * A set of diffs will be computed.
     * @param text1 Old text.
     * @param text2 New text.
     * @return LinkedList of Patch objects.
     */
    public List<Patch> patchMake(String text1, String text2) {
        if (text1 == null || text2 == null) {
            throw new IllegalArgumentException("Null inputs. (patch_make)");
        }
        // No diffs provided, compute our own.
        LinkedList<Diff> diffs = this.diffMain(text1, text2, true);
        if (diffs.size() > 2) {
            this.diffCleanupSemantic(diffs);
            this.diffCleanupEfficiency(diffs);
        }
        return this.patchMake(text1, diffs);
    }

    /**
     * Compute a list of patches to turn text1 into text2.
     * text1 will be derived from the provided diffs.
     * @param diffs Array of Diff objects for text1 to text2.
     * @return LinkedList of Patch objects.
     */
    public List<Patch> patchMake(LinkedList<Diff> diffs) {
        if (diffs == null) {
            throw new IllegalArgumentException("Null inputs. (patch_make)");
        }
        // No origin string provided, compute our own.
        String text1 = this.diffText1(diffs);
        return this.patchMake(text1, diffs);
    }

    /**
     * Compute a list of patches to turn text1 into text2.
     * text2 is not provided, diffs are the delta between text1 and text2.
     * @param text1 Old text.
     * @param diffs Array of Diff objects for text1 to text2.
     * @return Deque of Patch objects.
     */
    public List<Patch> patchMake(String text1, Deque<Diff> diffs) {
        if (text1 == null || diffs == null) {
            throw new IllegalArgumentException("Null inputs. (patch_make)");
        }

        List<Patch> patches = new LinkedList<>();
        if (diffs.isEmpty()) {
            return patches;  // Get rid of the null case.
        }
        Patch patch = new Patch();
        int charCount1 = 0;  // Number of characters into the text1 string.
        int charCount2 = 0;  // Number of characters into the text2 string.
        // Start with text1 (prepatch_text) and apply the diffs until we arrive at
        // text2 (postpatch_text). We recreate the patches one by one to determine
        // context info.
        String prepatchText = text1;
        String postpatchText = text1;
        for (Diff aDiff : diffs) {
            if (patch.getDiffs().isEmpty() && aDiff.getOperation() != Operation.EQUAL) {
                // A new patch starts here.
                patch.setStart1(charCount1);
                patch.setStart2(charCount2);
            }

            switch (aDiff.getOperation()) {
            case INSERT:
                patch.getDiffs().add(aDiff);
                patch.setLength2(patch.getLength2() + aDiff.getText().length());
                postpatchText = postpatchText.substring(0, charCount2)
                        + aDiff.getText() + postpatchText.substring(charCount2);
                break;
            case DELETE:
                patch.setLength1(patch.getLength1() + aDiff.getText().length());
                patch.getDiffs().add(aDiff);
                postpatchText = postpatchText.substring(0, charCount2)
                        + postpatchText.substring(charCount2 + aDiff.getText().length());
                break;
            case EQUAL:
                if (
                    aDiff.getText().length() <= 2 * PATCH_MARGIN
                    && !patch.getDiffs().isEmpty() && aDiff != diffs.getLast()
                ) {
                    // Small equality inside a patch.
                    patch.getDiffs().add(aDiff);
                    patch.setLength1(patch.getLength1() + aDiff.getText().length());
                    patch.setLength2(patch.getLength2() + aDiff.getText().length());
                }

                if (
                    aDiff.getText().length() >= 2 * PATCH_MARGIN
                    && !patch.getDiffs().isEmpty()
                ) {
                    // Time for a new patch.
                    this.patchAddContext(patch, prepatchText);
                    patches.add(patch);
                    patch = new Patch();
                    // Unlike Unidiff, our patch lists have a rolling context.
                    // http://code.google.com/p/google-diff-match-patch/wiki/Unidiff
                    // Update prepatch text & pos to reflect the application of the
                    // just completed patch.
                    prepatchText = postpatchText;
                    charCount1 = charCount2;
                }
                break;
            }

            // Update the current character count.
            if (aDiff.getOperation() != Operation.INSERT) {
                charCount1 += aDiff.getText().length();
            }
            if (aDiff.getOperation() != Operation.DELETE) {
                charCount2 += aDiff.getText().length();
            }
        }
        // Pick up the leftover patch if not empty.
        if (!patch.getDiffs().isEmpty()) {
            this.patchAddContext(patch, prepatchText);
            patches.add(patch);
        }

        return patches;
    }

    /**
     * Given an array of patches, return another array that is identical.
     * @param patches Array of Patch objects.
     * @return Array of Patch objects.
     */
    public LinkedList<Patch> patchDeepCopy(List<Patch> patches) {
        LinkedList<Patch> patchesCopy = new LinkedList<>();
        for (Patch aPatch : patches) {
            Patch patchCopy = new Patch();
            for (Diff aDiff : aPatch.getDiffs()) {
                Diff diffCopy = new Diff(aDiff.getOperation(), aDiff.getText());
                patchCopy.getDiffs().add(diffCopy);
            }
            patchCopy.setStart1(aPatch.getStart1());
            patchCopy.setStart2(aPatch.getStart2());
            patchCopy.setLength1(aPatch.getLength1());
            patchCopy.setLength2(aPatch.getLength2());
            patchesCopy.add(patchCopy);
        }
        return patchesCopy;
    }

    /**
     * Merge a set of patches onto the text.  Return a patched text, as well
     * as an array of true/false values indicating which patches were applied.
     * @param valuePatches Array of Patch objects
     * @param valueText Old text.
     * @return Two element Object array, containing the new text and an array of
     *      boolean values.
     */
    public Object[] patchApply(LinkedList<Patch> valuePatches, String valueText) {
        if (valuePatches.isEmpty()) {
            return new Object[]{valueText, new boolean[0]};
        }
        
        // Deep copy the patches so that no changes are made to originals.
        LinkedList<Patch> patches = this.patchDeepCopy(valuePatches);

        String nullPadding = this.patchAddPadding(patches);
        String text = nullPadding + valueText + nullPadding;
        this.patchSplitMax(patches);

        int x = 0;
        // delta keeps track of the offset between the expected and actual location
        // of the previous patch.  If there are patches expected at positions 10 and
        // 20, but the first patch was found at 12, delta is 2 and the second patch
        // has an effective expected position of 22.
        int delta = 0;
        boolean[] results = new boolean[patches.size()];
        for (Patch aPatch : patches) {
            int expectedLoc = aPatch.getStart2() + delta;
            String text1 = this.diffText1(aPatch.getDiffs());
            int startLoc;
            int endLoc = -1;
            if (text1.length() > DiffMatchPatch.MATCH_MAX_BITS) {
                // patch_splitMax will only provide an oversized pattern in the case of
                // a monster delete.
                startLoc = this.matchMain(text,
                        text1.substring(0, DiffMatchPatch.MATCH_MAX_BITS), expectedLoc);
                if (startLoc != -1) {
                    endLoc = this.matchMain(text,
                            text1.substring(text1.length() - DiffMatchPatch.MATCH_MAX_BITS),
                            expectedLoc + text1.length() - DiffMatchPatch.MATCH_MAX_BITS);
                    if (endLoc == -1 || startLoc >= endLoc) {
                        // Can't find valid trailing context.  Drop this patch.
                        startLoc = -1;
                    }
                }
            } else {
                startLoc = this.matchMain(text, text1, expectedLoc);
            }
            if (startLoc == -1) {
                // No match found.  :(
                results[x] = false;
                // Subtract the delta for this failed patch from subsequent patches.
                delta -= aPatch.getLength2() - aPatch.getLength1();
            } else {
                // Found a match.  :)
                results[x] = true;
                delta = startLoc - expectedLoc;
                String text2;
                if (endLoc == -1) {
                    text2 = text.substring(startLoc,
                            Math.min(startLoc + text1.length(), text.length()));
                } else {
                    text2 = text.substring(startLoc,
                            Math.min(endLoc + DiffMatchPatch.MATCH_MAX_BITS, text.length()));
                }
                if (text1.equals(text2)) {
                    // Perfect match, just shove the replacement text in.
                    text = text.substring(0, startLoc) + this.diffText2(aPatch.getDiffs())
                            + text.substring(startLoc + text1.length());
                } else {
                    // Imperfect match.  Run a diff to get a framework of equivalent
                    // indices.
                    List<Diff> diffs = this.diffMain(text1, text2, false);
                    if (text1.length() > DiffMatchPatch.MATCH_MAX_BITS
                            && this.diffLevenshtein(diffs) / (float) text1.length()
                            > DiffMatchPatch.PATCH_DELETE_THRESHOLD) {
                        // The end points match, but the content is unacceptably bad.
                        results[x] = false;
                    } else {
                        this.diffCleanupSemanticLossless(diffs);
                        int index1 = 0;
                        for (Diff aDiff : aPatch.getDiffs()) {
                            if (aDiff.getOperation() != Operation.EQUAL) {
                                int index2 = this.diffXIndex(diffs, index1);
                                if (aDiff.getOperation() == Operation.INSERT) {
                                    // Insertion
                                    text = text.substring(0, startLoc + index2) + aDiff.getText()
                                            + text.substring(startLoc + index2);
                                } else if (aDiff.getOperation() == Operation.DELETE) {
                                    // Deletion
                                    text = text.substring(0, startLoc + index2)
                                            + text.substring(startLoc + this.diffXIndex(diffs,
                                                    index1 + aDiff.getText().length()));
                                }
                            }
                            if (aDiff.getOperation() != Operation.DELETE) {
                                index1 += aDiff.getText().length();
                            }
                        }
                    }
                }
            }
            x++;
        }
        // Strip the padding off.
        text = text.substring(nullPadding.length(), text.length()
                - nullPadding.length());
        return new Object[]{text, results};
    }

    /**
     * Add some padding on text start and end so that edges can match something.
     * Intended to be called only from within patch_apply.
     * @param patches Array of Patch objects.
     * @return The padding string added to each side.
     */
    public String patchAddPadding(Deque<Patch> patches) {
        short paddingLength = DiffMatchPatch.PATCH_MARGIN;
        StringBuilder nullPadding = new StringBuilder();
        for (short x = 1; x <= paddingLength; x++) {
            nullPadding.append(String.valueOf((char) x));
        }

        // Bump all the patches forward.
        for (Patch aPatch : patches) {
            aPatch.setStart1(aPatch.getStart1() + paddingLength);
            aPatch.setStart2(aPatch.getStart2() + paddingLength);
        }

        // Add some padding on start of first diff.
        Patch patch = patches.getFirst();
        Deque<Diff> diffs = patch.getDiffs();
        if (diffs.isEmpty() || diffs.getFirst().getOperation() != Operation.EQUAL) {
            // Add nullPadding equality.
            diffs.addFirst(new Diff(Operation.EQUAL, nullPadding.toString()));
            patch.setStart1(patch.getStart1() - paddingLength);  // Should be 0.
            patch.setStart2(patch.getStart2() - paddingLength);  // Should be 0.
            patch.setLength1(patch.getLength1() + paddingLength);
            patch.setLength2(patch.getLength2() + paddingLength);
        } else if (paddingLength > diffs.getFirst().getText().length()) {
            // Grow first equality.
            Diff firstDiff = diffs.getFirst();
            int extraLength = paddingLength - firstDiff.getText().length();
            firstDiff.setText(nullPadding.substring(firstDiff.getText().length())
                    + firstDiff.getText());
            patch.setStart1(patch.getStart1() - extraLength);
            patch.setStart2(patch.getStart2() - extraLength);
            patch.setLength1(patch.getLength1() + extraLength);
            patch.setLength2(patch.getLength2() + extraLength);
        }

        // Add some padding on end of last diff.
        patch = patches.getLast();
        diffs = patch.getDiffs();
        if (diffs.isEmpty() || diffs.getLast().getOperation() != Operation.EQUAL) {
            // Add nullPadding equality.
            diffs.addLast(new Diff(Operation.EQUAL, nullPadding.toString()));
            patch.setLength1(patch.getLength1() + paddingLength);
            patch.setLength2(patch.getLength2() + paddingLength);
        } else if (paddingLength > diffs.getLast().getText().length()) {
            // Grow last equality.
            Diff lastDiff = diffs.getLast();
            int extraLength = paddingLength - lastDiff.getText().length();
            lastDiff.setText(lastDiff.getText() + nullPadding.substring(0, extraLength));
            patch.setLength1(patch.getLength1() + extraLength);
            patch.setLength2(patch.getLength2() + extraLength);
        }

        return nullPadding.toString();
    }

    /**
     * Look through the patches and break up any which are longer than the
     * maximum limit of the match algorithm.
     * Intended to be called only from within patch_apply.
     * @param patches List of Patch objects.
     */
    public void patchSplitMax(List<Patch> patches) {
        short patchSize = DiffMatchPatch.MATCH_MAX_BITS;
        String precontext;
        String postcontext;
        Patch patch;
        int start1;
        int start2;
        boolean empty;
        Operation diffType;
        String diffText;
        ListIterator<Patch> pointer = patches.listIterator();
        Patch bigpatch = pointer.hasNext() ? pointer.next() : null;
        while (bigpatch != null) {
            if (bigpatch.getLength1() <= DiffMatchPatch.MATCH_MAX_BITS) {
                bigpatch = pointer.hasNext() ? pointer.next() : null;
                continue;
            }
            // Remove the big old patch.
            pointer.remove();
            start1 = bigpatch.getStart1();
            start2 = bigpatch.getStart2();
            precontext = "";
            while (!bigpatch.getDiffs().isEmpty()) {
                // Create one of several smaller patches.
                patch = new Patch();
                empty = true;
                patch.setStart1(start1 - precontext.length());
                patch.setStart2(start2 - precontext.length());
                if (precontext.length() != 0) {
                    patch.setLength1(patch.setLength2(precontext.length()));
                    patch.getDiffs().add(new Diff(Operation.EQUAL, precontext));
                }
                while (!bigpatch.getDiffs().isEmpty()
                        && patch.getLength1() < patchSize - PATCH_MARGIN) {
                    diffType = bigpatch.getDiffs().getFirst().getOperation();
                    diffText = bigpatch.getDiffs().getFirst().getText();
                    if (diffType == Operation.INSERT) {
                        // Insertions are harmless.
                        patch.setLength2(patch.getLength2() + diffText.length());
                        start2 += diffText.length();
                        patch.getDiffs().addLast(bigpatch.getDiffs().removeFirst());
                        empty = false;
                    } else if (diffType == Operation.DELETE && patch.getDiffs().size() == 1
                            && patch.getDiffs().getFirst().getOperation() == Operation.EQUAL
                            && diffText.length() > 2 * patchSize) {
                        // This is a large deletion.  Let it pass in one chunk.
                        patch.setLength1(patch.getLength1() + diffText.length());
                        start1 += diffText.length();
                        empty = false;
                        patch.getDiffs().add(new Diff(diffType, diffText));
                        bigpatch.getDiffs().removeFirst();
                    } else {
                        // Deletion or equality.  Only take as much as we can stomach.
                        diffText = diffText.substring(0, Math.min(diffText.length(),
                                patchSize - patch.getLength1() - PATCH_MARGIN));
                        patch.setLength1(patch.getLength1() + diffText.length());
                        start1 += diffText.length();
                        if (diffType == Operation.EQUAL) {
                            patch.setLength2(patch.getLength2() + diffText.length());
                            start2 += diffText.length();
                        } else {
                            empty = false;
                        }
                        patch.getDiffs().add(new Diff(diffType, diffText));
                        if (diffText.equals(bigpatch.getDiffs().getFirst().getText())) {
                            bigpatch.getDiffs().removeFirst();
                        } else {
                            bigpatch.getDiffs().getFirst().setText(bigpatch.getDiffs().getFirst().getText()
                                    .substring(diffText.length()));
                        }
                    }
                }
                // Compute the head context for the next patch.
                precontext = this.diffText2(patch.getDiffs());
                precontext = precontext.substring(Math.max(0, precontext.length()
                        - PATCH_MARGIN));
                // Append the end context for this patch.
                if (this.diffText1(bigpatch.getDiffs()).length() > PATCH_MARGIN) {
                    postcontext = this.diffText1(bigpatch.getDiffs()).substring(0, PATCH_MARGIN);
                } else {
                    postcontext = this.diffText1(bigpatch.getDiffs());
                }
                if (postcontext.length() != 0) {
                    patch.setLength1(patch.getLength1() + postcontext.length());
                    patch.setLength2(patch.getLength2() + postcontext.length());
                    if (!patch.getDiffs().isEmpty()
                            && patch.getDiffs().getLast().getOperation() == Operation.EQUAL) {
                        patch.getDiffs().getLast().setText(patch.getDiffs().getLast().getText() + postcontext);
                    } else {
                        patch.getDiffs().add(new Diff(Operation.EQUAL, postcontext));
                    }
                }
                if (!empty) {
                    pointer.add(patch);
                }
            }
            bigpatch = pointer.hasNext() ? pointer.next() : null;
        }
    }

    /**
     * Take a list of patches and return a textual representation.
     * @param patches List of Patch objects.
     * @return Text representation of patches.
     */
    public String patchToText(List<Patch> patches) {
        StringBuilder text = new StringBuilder();
        for (Patch aPatch : patches) {
            text.append(aPatch);
        }
        return text.toString();
    }

    /**
     * Parse a textual representation of patches and return a List of Patch
     * objects.
     * @param textline Text representation of patches.
     * @return List of Patch objects.
     * @throws IllegalArgumentException If invalid input.
     * @throws UnsupportedEncodingException
     */
    public List<Patch> patchFromText(String textline) throws UnsupportedEncodingException {
        List<Patch> patches = new LinkedList<>();
        if (textline.length() == 0) {
            return patches;
        }
        List<String> textList = Arrays.asList(textline.split("\n"));
        Deque<String> text = new LinkedList<>(textList);
        Patch patch;
        Pattern patchHeader = Pattern.compile("^@@ -(\\d+),?(\\d*) \\+(\\d+),?(\\d*) @@$");
        Matcher m;
        char sign;
        String line;
        while (!text.isEmpty()) {
            m = patchHeader.matcher(text.getFirst());
            if (!m.matches()) {
                throw new IllegalArgumentException(
                        "Invalid patch string: " + text.getFirst());
            }
            patch = new Patch();
            patches.add(patch);
            patch.setStart1(Integer.parseInt(m.group(1)));
            if (m.group(2).length() == 0) {
                patch.setStart1(patch.getStart1() - 1);
                patch.setLength1(1);
            } else if ("0".equals(m.group(2))) {
                patch.setLength1(0);
            } else {
                patch.setStart1(patch.getStart1() - 1);
                patch.setLength1(Integer.parseInt(m.group(2)));
            }

            patch.setStart2(Integer.parseInt(m.group(3)));
            if (m.group(4).length() == 0) {
                patch.setStart2(patch.getStart2() - 1);
                patch.setLength2(1);
            } else if ("0".equals(m.group(4))) {
                patch.setLength2(0);
            } else {
                patch.setStart2(patch.getStart2() - 1);
                patch.setLength2(Integer.parseInt(m.group(4)));
            }
            text.removeFirst();

            while (!text.isEmpty()) {
                try {
                    sign = text.getFirst().charAt(0);
                } catch (IndexOutOfBoundsException e) {
                    
                    LOGGER.log(LogLevel.IGNORE, e);
                    
                    // Blank line?  Whatever.
                    text.removeFirst();
                    continue;
                }
                line = text.getFirst().substring(1);
                line = line.replace("+", "%2B");  // decode would change all "+" to " "
                try {
                    line = URLDecoder.decode(line, StandardCharsets.UTF_8);
                } catch (IllegalArgumentException e) {
                    // Malformed URI sequence.
                    throw new IllegalArgumentException(
                            "Illegal escape in patch_fromText: " + line, e);
                }
                if (sign == '-') {
                    // Deletion.
                    patch.getDiffs().add(new Diff(Operation.DELETE, line));
                } else if (sign == '+') {
                    // Insertion.
                    patch.getDiffs().add(new Diff(Operation.INSERT, line));
                } else if (sign == ' ') {
                    // Minor equality.
                    patch.getDiffs().add(new Diff(Operation.EQUAL, line));
                } else if (sign == '@') {
                    // Start of next patch.
                    break;
                } else {
                    // WTF?
                    throw new IllegalArgumentException(
                            "Invalid patch mode '" + sign + "' in: " + line);
                }
                text.removeFirst();
            }
        }
        return patches;
    }

}
