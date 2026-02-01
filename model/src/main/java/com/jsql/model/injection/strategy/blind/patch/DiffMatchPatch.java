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
package com.jsql.model.injection.strategy.blind.patch;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

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

    // Define some regex patterns for matching boundaries.
    private static final Pattern BLANK_LINE_END = Pattern.compile("\\n\\r?\\n\\Z", Pattern.DOTALL);
    private static final Pattern BLANK_LINE_START = Pattern.compile("\\A\\r?\\n\\r?\\n", Pattern.DOTALL);

    /**
     * Internal class for returning results from diff_linesToChars().
     * Other less paranoid languages just use a three-element array.
     */
    protected record LinesToCharsResult(String chars1, String chars2, List<String> lineArray) {}

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
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param checklines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff.
     * @return Linked List of Diff objects.
     */
    public LinkedList<Diff> diffMain(String text1, String text2, boolean checklines) {
        // Set a deadline by which time the diff must be complete.
        long deadline = System.currentTimeMillis() + (long) (DiffMatchPatch.DIFF_TIMEOUT * 1000);
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
            if (!text1.isEmpty()) {
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
        if (!commonprefix.isEmpty()) {
            diffs.addFirst(new Diff(Operation.EQUAL, commonprefix));
        }
        if (!commonsuffix.isEmpty()) {
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

        if (text1.isEmpty()) {
            // Just add some text (speedup).
            diffs.add(new Diff(Operation.INSERT, text2));
            return diffs;
        }

        if (text2.isEmpty()) {
            // Just delete some text (speedup).
            diffs.add(new Diff(Operation.DELETE, text1));
            return diffs;
        }

        {
            // New scope to garbage collect longtext and shorttext.
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
        diffs.add(new Diff(Operation.EQUAL, StringUtils.EMPTY));
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
        int vLength = 2 * maxD;
        int[] v1 = new int[vLength];
        int[] v2 = new int[vLength];
        for (int x = 0; x < vLength; x++) {
            v1[x] = -1;
            v2[x] = -1;
        }
        v1[maxD + 1] = 0;
        v2[maxD + 1] = 0;
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
                int k1Offset = maxD + k1;
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
                    int k2Offset = maxD + delta - k1;
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
                int k2Offset = maxD + k2;
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
                    int k1Offset = maxD + delta - k2;
                    if (k1Offset >= 0 && k1Offset < vLength && v1[k1Offset] != -1) {
                        int x1 = v1[k1Offset];
                        int y1 = maxD + x1 - k1Offset;
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
        lineArray.add(StringUtils.EMPTY);

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
                chars.append((char) (int) lineHash.get(line));
            } else {
                lineArray.add(line);
                lineHash.put(line, lineArray.size() - 1);
                chars.append((char) (lineArray.size() - 1));
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
        String bestCommon = StringUtils.EMPTY;
        String bestLongtextA = StringUtils.EMPTY;
        String bestLongtextB = StringUtils.EMPTY;
        String bestShorttextA = StringUtils.EMPTY;
        String bestShorttextB = StringUtils.EMPTY;
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
                while (!edit.isEmpty() && !equality2.isEmpty()
                        && edit.charAt(0) == equality2.charAt(0)) {
                    equality1.append(edit.charAt(0));
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
                    if (!bestEquality1.isEmpty()) {
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
                    if (!bestEquality2.isEmpty()) {
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
        if (one.isEmpty() || two.isEmpty()) {
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
        boolean blankLine1 = lineBreak1 && DiffMatchPatch.BLANK_LINE_END.matcher(one).find();
        boolean blankLine2 = lineBreak2 && DiffMatchPatch.BLANK_LINE_START.matcher(two).find();

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
                if (thisDiff.getText().length() < DiffMatchPatch.DIFF_EDIT_COST && (postIns || postDel)) {
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
                                        (lastequality.length() < DiffMatchPatch.DIFF_EDIT_COST / 2)
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
        diffs.add(new Diff(Operation.EQUAL, StringUtils.EMPTY));  // Add a dummy entry at the end.
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
                        if (!textDelete.isEmpty()) {
                            pointer.add(new Diff(Operation.DELETE, textDelete.toString()));
                        }
                        if (!textInsert.isEmpty()) {
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
        if (diffs.getLast().getText().isEmpty()) {
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
}
