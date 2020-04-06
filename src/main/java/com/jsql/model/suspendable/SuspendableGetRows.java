package com.jsql.model.suspendable;

import static com.jsql.model.accessible.DataAccess.ENCLOSE_VALUE_RGX;
import static com.jsql.model.accessible.DataAccess.LEAD;
import static com.jsql.model.accessible.DataAccess.MODE;
import static com.jsql.model.accessible.DataAccess.SEPARATOR_CELL_RGX;
import static com.jsql.model.accessible.DataAccess.SEPARATOR_QTE_RGX;
import static com.jsql.model.accessible.DataAccess.TRAIL_RGX;
import static com.jsql.model.injection.vendor.model.VendorYaml.LIMIT;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.model.bean.database.Table;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.LoopDetectedSlidingException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.AbstractStrategy;
import com.jsql.util.StringUtil;

/**
 * Get data as chunks by performance query from SQL request.
 * 
 * <pre>
 * Single row format: \4[0-9A-F]*\5[0-9A-F]*c?\4
 * Row separator: \6
 * Tape example: \4xxRow#Xxx\5x\4\6\4xxRow#X+1xx\5x\4\6...\4\1\3\3\7</pre>
 * 
 * MID and LIMIT move two sliding windows in a 2D array tape in that order.
 * MID skips characters when collected, then LIMIT skips lines when collected.
 * The process can be interrupted by the user (stop/pause).
 */
public class SuspendableGetRows extends AbstractSuspendable<String> {
    
    public SuspendableGetRows(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public String run(Object... args) throws JSqlException {
        
        String initialSqlQuery = (String) args[0];
        String[] sourcePage = (String[]) args[1];
        boolean isMultipleRows = (Boolean) args[2];
        int countRowsToFind = (Integer) args[3];
        AbstractElementDatabase searchName = (AbstractElementDatabase) args[4];
        
        this.injectionModel.getMediatorUtils().getThreadUtil().put(searchName, this);

        AbstractStrategy strategy;
        // Fix #14417
        // TODO Optional
        if (this.injectionModel.getMediatorStrategy().getStrategy() != null) {
            strategy = this.injectionModel.getMediatorStrategy().getStrategy();
        } else {
            return StringUtils.EMPTY;
        }
        
        // Stop injection if all rows are found, skip rows and characters collected
        StringBuilder slidingWindowAllRows = new StringBuilder();
        StringBuilder slidingWindowCurrentRow = new StringBuilder();
        
        String previousChunk = StringUtils.EMPTY;
        int countAllRows = 0;
        int charPositionInCurrentRow = 1;
        int countInfiniteLoop = 0;
        
        String queryGetRows = this.getQuery(initialSqlQuery, countAllRows);
        
        while (true) {

            this.checkSuspend(strategy, slidingWindowAllRows, slidingWindowCurrentRow);
            
            sourcePage[0] = strategy.inject(queryGetRows, Integer.toString(charPositionInCurrentRow), this);
            
            // Parse all the data we have retrieved
            Matcher regexLeadFound = this.parseLeadFound(sourcePage[0], strategy.getPerformanceLength());
            Matcher regexTrailOnlyFound = this.parseTrailOnlyFound(sourcePage[0]);
            
            if (
                (!regexLeadFound.find() || regexTrailOnlyFound.find())
                && isMultipleRows
                && StringUtils.isNotEmpty(slidingWindowAllRows.toString())
            ) {
                
                this.sendProgress(countRowsToFind, countRowsToFind, searchName);
                break;
            }

            // Add the result to the data already found.
            // Fix #40947: OutOfMemoryError on append()
            try {
                String currentChunk = regexLeadFound.group(1);
                
                countInfiniteLoop = this.checkInfinite(countInfiniteLoop, previousChunk, currentChunk, slidingWindowCurrentRow, slidingWindowAllRows);
                
                previousChunk = currentChunk;
                
                slidingWindowCurrentRow.append(currentChunk);

                this.sendChunk(currentChunk);
                
            } catch (IllegalStateException | OutOfMemoryError e) {
                
                this.throwFatal(searchName, e);
            }

            // Check how many rows we have collected from the beginning of that chunk
            int countChunkRows = this.getCountRows(slidingWindowCurrentRow);

            this.sendProgress(countRowsToFind, countAllRows + countChunkRows, searchName);

            // End of rows detected: \1\3\3\7
            // => \4xxxxxxxx\500\4\6\4...\4\1\3\3\7
            if (countChunkRows > 0 || slidingWindowCurrentRow.toString().matches("(?s).*"+ TRAIL_RGX +".*")) {
                
                this.scrapeTrailJunk(slidingWindowCurrentRow);
                
                slidingWindowAllRows.append(slidingWindowCurrentRow.toString());
                
                if (isMultipleRows) {
                    
                    this.scrap(slidingWindowAllRows);
                    this.scrap(slidingWindowCurrentRow);

                    this.appendRowFixed(slidingWindowAllRows, slidingWindowCurrentRow);

                    countAllRows = this.getCountRows(slidingWindowAllRows);

                    this.sendProgress(countRowsToFind, countAllRows, searchName);

                    // Ending condition: every expected rows have been retrieved.
                    if (countAllRows == countRowsToFind) {
                        break;
                    }

                    // Add the LIMIT statement to the next SQL query and reset variables.
                    // Put the character cursor to the beginning of the line, and reset the result of the current query
                    queryGetRows = this.getQuery(initialSqlQuery, countAllRows);

                    slidingWindowCurrentRow.setLength(0);
                    
                } else {
                    
                    this.sendProgress(countRowsToFind, countRowsToFind, searchName);
                    break;
                }
            }

            charPositionInCurrentRow = slidingWindowCurrentRow.length() + 1;
        }
        
        this.injectionModel.getMediatorUtils().getThreadUtil().remove(searchName);

        return slidingWindowAllRows.toString();
    }

    private String getQuery(String initialSqlQuery, int countAllRows) {
        
        return initialSqlQuery.replace(LIMIT, this.injectionModel.getMediatorVendor().getVendor().instance().sqlLimit(countAllRows));
    }

    private void appendRowFixed(StringBuilder slidingWindowAllRows, StringBuilder slidingWindowCurrentRow) {
        
        // Check either if there is more than 1 row and if there is less than 1 complete row
        Matcher regexAtLeastOneRow =
            Pattern
            .compile(
                MODE
                + "[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]"
                + ENCLOSE_VALUE_RGX
                + SEPARATOR_CELL_RGX
                + ENCLOSE_VALUE_RGX
                + "[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]+?$"
            )
            .matcher(slidingWindowCurrentRow);
        
        Matcher regexRowIncomplete =
            Pattern
            .compile(
                MODE
                + ENCLOSE_VALUE_RGX
                + "[^\\x01-\\x03\\x05-\\x09\\x0B-\\x0C\\x0E-\\x1F]+?$"
            )
            .matcher(slidingWindowCurrentRow);

        // If there is more than 1 row, delete the last incomplete one in order to restart properly from it at the next loop,
        // else if there is 1 row but incomplete, mark it as cut with the letter c
        if (regexAtLeastOneRow.find()) {
            
            String allLine = slidingWindowAllRows.toString();
            slidingWindowAllRows.setLength(0);
            slidingWindowAllRows.append(
                Pattern
                .compile(
                    MODE
                    + ENCLOSE_VALUE_RGX
                    + "[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]+?$"
                )
                .matcher(allLine)
                .replaceAll(StringUtils.EMPTY)
            );
            
        } else if (regexRowIncomplete.find()) {
            slidingWindowAllRows.append(StringUtil.hexstr("05") + "1" + StringUtil.hexstr("0804"));
        }
    }

    private void scrapeTrailJunk(StringBuilder slidingWindowCurrentRow) {
        
        // Remove everything after chunk
        // => \4xxxxxxxx\500\4\6\4...\4 => \1\3\3\7junk
        String currentRow = slidingWindowCurrentRow.toString();
        slidingWindowCurrentRow.setLength(0);
        slidingWindowCurrentRow.append(
            Pattern
            .compile(MODE + TRAIL_RGX +".*")
            .matcher(currentRow)
            .replaceAll(StringUtils.EMPTY)
        );
    }

    private int getCountRows(StringBuilder slidingWindowCurrentRow) {
        
        Matcher regexAtLeastOneRow =
            Pattern
            .compile(
                MODE
                +"("
                + ENCLOSE_VALUE_RGX
                + "[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?"
                + SEPARATOR_QTE_RGX
                + "[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?\\x08?"
                + ENCLOSE_VALUE_RGX
                + ")"
            )
            .matcher(slidingWindowCurrentRow);
        
        int nbCompleteLine = 0;
        while (regexAtLeastOneRow.find()) {
            nbCompleteLine++;
        }
        
        return nbCompleteLine;
    }

    private void throwFatal(AbstractElementDatabase searchName, Throwable e) throws InjectionFailureException {
        
        // Premature end of results
        // if it's not the root (empty tree)
        if (searchName != null) {
            
            Request request = new Request();
            request.setMessage(Interaction.END_PROGRESS);
            request.setParameters(searchName);
            this.injectionModel.sendToViews(request);
        }

        StringBuilder messageError = new StringBuilder("Fetching fails: no data to parse");
        
        if (searchName != null) {
            messageError.append(" for "+ StringUtil.detectUtf8(searchName.toString()));
        }
        
        if (searchName instanceof Table && searchName.getChildCount() > 0) {
            messageError.append(", if possible retry with one column selected only");
        }
        
        throw new InjectionFailureException(messageError.toString(), e);
    }

    private void sendChunk(String currentChunk) {
        
        Request request = new Request();
        request.setMessage(Interaction.MESSAGE_CHUNK);
        request.setParameters(
            Pattern
            .compile(MODE + TRAIL_RGX +".*")
            .matcher(currentChunk)
            .replaceAll(StringUtils.EMPTY)
            .replaceAll("\\n", "\\\\\\n")
            .replaceAll("\\r", "\\\\\\r")
            .replaceAll("\\t", "\\\\\\t")
        );
        
        this.injectionModel.sendToViews(request);
    }

    // TODO pb for same char string like aaaaaaaaaaaaa...aaaaaaaaaaaaa
    // detected as infinite
    private int checkInfinite(
        int loop,
        String previousChunk,
        String currentChunk,
        StringBuilder slidingWindowCurrentRow,
        StringBuilder slidingWindowAllRows
    ) throws LoopDetectedSlidingException {
        
        int infiniteLoop = loop;
        
        if (previousChunk.equals(currentChunk)) {
            
            infiniteLoop++;
            if (infiniteLoop >= 20) {
                
                this.stop();
                
                throw new LoopDetectedSlidingException(
                    slidingWindowAllRows.toString(),
                    slidingWindowCurrentRow.toString()
                );
            }
        }
        
        return infiniteLoop;
    }

    private Matcher parseTrailOnlyFound(String sourcePage) {
        
        // TODO: prevent to find the last line directly: MODE + LEAD + .* + TRAIL_RGX
        // It creates extra query which can be endless if not nullified
        return Pattern
            .compile("(?s)"+ LEAD +"(?i)"+ TRAIL_RGX)
            .matcher(sourcePage);
    }

    /**
     * After ${lead} tag, gets characters between 1 and maxPerf
     * performanceQuery() gets 65536 characters or less
     * ${lead}blahblah1337      ] : end or limit+1
     * ${lead}blahblah      blah] : continue substr()
     */
    private Matcher parseLeadFound(String sourcePage, String performanceLength) throws InjectionFailureException {
        
        Matcher regexAtLeastOneRow;
        
        try {
            regexAtLeastOneRow =
                Pattern
                .compile("(?s)"+ LEAD +"(?i)(.{1,"+ performanceLength +"})")
                .matcher(sourcePage);
            
        } catch (PatternSyntaxException e) {
            
            // Fix #35382 : PatternSyntaxException null on SQLi(.{1,null})
            throw new InjectionFailureException("Row parsing failed using capacity", e);
        }
        
        return regexAtLeastOneRow;
    }

    private void checkSuspend(
        AbstractStrategy strategy,
        StringBuilder slidingWindowAllRows,
        StringBuilder slidingWindowCurrentRow
    ) throws StoppedByUserSlidingException, InjectionFailureException {
        
        if (this.isSuspended()) {
            
            throw new StoppedByUserSlidingException(
                slidingWindowAllRows.toString(),
                slidingWindowCurrentRow.toString()
            );
            
        } else if (strategy == null) {
            
            // Fix #1905 : NullPointerException on injectionStrategy.inject()
            throw new InjectionFailureException("Undefined startegy");
        }
    }

    private void scrap(StringBuilder slidingWindowAllRows) {
        
        // Remove everything not properly attached to the last row:
        // 1. very start of a new row: XXXXX\4[\6\4]$
        // 2. very end of the last row: XXXXX[\500]$

        String allRowsLimit = slidingWindowAllRows.toString();
        slidingWindowAllRows.setLength(0);
        slidingWindowAllRows.append(
            Pattern
            .compile(
                MODE
                +"("
                + SEPARATOR_CELL_RGX + ENCLOSE_VALUE_RGX
                +"|"
                + SEPARATOR_QTE_RGX +"\\d*"
                +")$"
            )
            .matcher(allRowsLimit)
            .replaceAll(StringUtils.EMPTY)
        );
    }

    private void sendProgress(int numberToFind, int countProgress, AbstractElementDatabase searchName) {
        
        if (numberToFind > 0 && searchName != null) {
            
            Request request = new Request();
            request.setMessage(Interaction.UPDATE_PROGRESS);
            request.setParameters(searchName, countProgress);
            this.injectionModel.sendToViews(request);
        }
    }
    
    public static List<List<String>> parse(String rows) throws InjectionFailureException {
        
        // Parse all the data we have retrieved
        Matcher regexSearch =
            Pattern
            .compile(
                MODE
                + ENCLOSE_VALUE_RGX
                + "([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?)"
                + SEPARATOR_QTE_RGX
                + "([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?)(\\x08)?"
                + ENCLOSE_VALUE_RGX
            )
            .matcher(rows);

        if (!regexSearch.find()) {
            throw new InjectionFailureException();
        }
        
        regexSearch.reset();

        int rowsFound = 0;
        List<List<String>> listValues = new ArrayList<>();

        // Build a 2D array of strings from the data we have parsed
        // => row number, occurrence, value1, value2...
        while (regexSearch.find()) {
            
            String values = regexSearch.group(1);
            int instances = Integer.parseInt(regexSearch.group(2));

            listValues.add(new ArrayList<String>());
            listValues.get(rowsFound).add(Integer.toString(rowsFound + 1));
            listValues.get(rowsFound).add("x"+ instances);
            
            for (String cellValue: values.split("\\x7F", -1)) {
                listValues.get(rowsFound).add(cellValue);
            }

            rowsFound++;
        }
        
        return listValues;
    }
}