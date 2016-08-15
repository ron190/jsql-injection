package com.jsql.model.suspendable;

import static com.jsql.model.accessible.DataAccess.MODE;
import static com.jsql.model.accessible.DataAccess.SEPARATOR;
import static com.jsql.model.accessible.DataAccess.TD;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeRequest;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.model.injection.strategy.AbstractStrategy;
import com.jsql.util.StringUtil;
import com.jsql.util.ThreadUtil;

/**
 * Get all data from a SQL request (remember that data will often been cut, we need to reach ALL the data)
 * We expect the following well formed line:
 * => hh[0-9A-F]*jj[0-9A-F]*c?hhgghh[0-9A-F]*jj[0-9A-F]*c?hhg...hi
 * We must check if that long line is cut, and where it is cut, basically we will move our position in a virtual 2D array,
 * and use LIMIT and MID to move the cursor ; LIMIT skips whole line (useful if result contains 1 or more complete row) ; and
 * MID skips characters in a line (useful if result contains less than 1 row).
 * The process can be interrupted by the user (stop/pause).
 */
public class SuspendableGetRows extends AbstractSuspendable<String> {
    
    @Override
    public String run(Object... args) throws JSqlException {
        String initialSQLQuery = (String) args[0];
        String[] sourcePage = (String[]) args[1];
        boolean isUsingLimit = (Boolean) args[2];
        int numberToFind = (Integer) args[3];
        AbstractElementDatabase searchName = (AbstractElementDatabase) args[4];
        ThreadUtil.put(searchName, this);

        String sqlQuery = new String(initialSQLQuery).replaceAll("\\{limit\\}", MediatorModel.model().vendor.instance().getSqlLimit(0));

        AbstractStrategy strategy = MediatorModel.model().getStrategy().instance();
        
        /*
         * As we know the expected number of rows (numberToFind), then it stops injection if all rows are found,
         * keep track of rows we have reached (limitSQLResult) and use these to skip entire rows,
         * keep track of characters we have reached (startPosition) and use these to skip characters,
         */
        String slidingWindowAllRows = "";
        String slidingWindowCurrentRow = "";
        int sqlLimit = 0;
        int charPositionInCurrentRow = 1;
        
        while (true) {

            if (this.isSuspended()) {
                StoppedByUserException e = new StoppedByUserException();
                e.setSlidingWindowAllRows(slidingWindowAllRows);
                e.setSlidingWindowCurrentRows(slidingWindowCurrentRow);
                throw e;
            } else if (strategy == null) {
                // Fix #1905 : NullPointerException on injectionStrategy.inject()
                throw new InjectionFailureException("Undefined startegy");
            }
            
            sourcePage[0] = strategy.inject(sqlQuery, Integer.toString(charPositionInCurrentRow), this);
            
            /**
             * After SQLi tag, gets characters between 1 and maxPerf
             * performanceQuery() gets 65536 characters or less
             * SQLiblahblah1337      ] : end or limit+1
             * SQLiblahblah      blah] : continue substr()
             */
            // Parse all the data we have retrieved
            Matcher regexAllLine = 
                Pattern
                    .compile(MODE +"SQLi(.{1,"+ strategy.getPerformanceLength() +"})")
                    .matcher(sourcePage[0]);
            
            Matcher regexEndOfLine = 
                Pattern
                    .compile(MODE +"SQLi\\x01\\x03\\x03\\x07")
                    .matcher(sourcePage[0]);
            
            if (regexEndOfLine.find() && isUsingLimit && !"".equals(slidingWindowAllRows)) {
                // Update the view only if there are value to find, and if it's not the root (empty tree)
                if (numberToFind > 0 && searchName != null) {
                    Request request = new Request();
                    request.setMessage(TypeRequest.UPDATE_PROGRESS);
                    request.setParameters(searchName, numberToFind);
                    MediatorModel.model().sendToViews(request);
                }
                break;
            }
            
            /*
             * Ending condition:
             * One row could be very long, longer than the database can provide
             * TODO Need verification
             */
            if (!regexAllLine.find() && isUsingLimit && !"".equals(slidingWindowAllRows)) {
                // Update the view only if there are value to find, and if it's not the root (empty tree)
                if (numberToFind > 0 && searchName != null) {
                    Request request = new Request();
                    request.setMessage(TypeRequest.UPDATE_PROGRESS);
                    request.setParameters(searchName, numberToFind);
                    MediatorModel.model().sendToViews(request);
                }
                break;
            }

            /*
             * Add the result to the data already found.
             */
            try {
                slidingWindowCurrentRow += regexAllLine.group(1);

                Request request = new Request();
                request.setMessage(TypeRequest.MESSAGE_CHUNK);
                request.setParameters(
                    Pattern
                        .compile(MODE +"\\x01\\x03\\x03\\x07.*")
                        .matcher(regexAllLine.group(1))
                        .replaceAll("\n")
                );
                MediatorModel.model().sendToViews(request);
            } catch (IllegalStateException e) {
                // Premature end of results
                // if it's not the root (empty tree)
                if (searchName != null) {
                    Request request = new Request();
                    request.setMessage(TypeRequest.END_PROGRESS);
                    request.setParameters(searchName);
                    MediatorModel.model().sendToViews(request);
                }

                throw new InjectionFailureException("Fetching fails: no data to parse"+ (searchName != null ? " for "+searchName : ""), e);
            }

            /*
             * Check how many rows we have collected from the beginning of that chunk
             */
            regexAllLine = 
                Pattern
                    .compile(
                        MODE +"("
                            + SEPARATOR 
                            + "([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?)\\x05([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?)(\\x08)?"
                            + SEPARATOR 
                        + ")")
                    .matcher(slidingWindowCurrentRow);
            int nbCompleteLine = 0;
            while (regexAllLine.find()) {
                nbCompleteLine++;
            }

            /*
             * Inform the view about the progression
             */
            if (isUsingLimit && numberToFind > 0 && searchName != null) {
                Request request = new Request();
                request.setMessage(TypeRequest.UPDATE_PROGRESS);
                request.setParameters(searchName, sqlLimit + nbCompleteLine);
                MediatorModel.model().sendToViews(request);
            }

            /*
             * We have properly reached the i at the end of the query: iLQS
             * => hhxxxxxxxxjj00hhgghh...hiLQS
             */
            /* Design Pattern: State? */
            if (nbCompleteLine>0 || slidingWindowCurrentRow.matches("(?s).*\\x01\\x03\\x03\\x07.*")) {
                /*
                 * Remove everything after our result
                 * => hhxxxxxxxxjj00hhgghh...h |-> iLQSjunk
                 */
                slidingWindowCurrentRow = 
                    Pattern
                        .compile(MODE +"\\x01\\x03\\x03\\x07.*")
                        .matcher(slidingWindowCurrentRow)
                        .replaceAll("");
                slidingWindowAllRows += slidingWindowCurrentRow;
                if (isUsingLimit) {
                    /*
                     * Remove everything not properly attached to the last row:
                     * 1. very start of a new row: XXXXXhhg[ghh]$
                     * 2. very end of the last row: XXXXX[jj00]$
                     */
                    slidingWindowAllRows = 
                        Pattern
                            .compile(
                                MODE +"("
                                    + TD + SEPARATOR
                                    + "|"
                                    + "\\x05\\d*"
                                + ")$"
                            )
                            .matcher(slidingWindowAllRows)
                            .replaceAll("");
                    slidingWindowCurrentRow = 
                        Pattern
                            .compile(
                                MODE +"("
                                    + TD + SEPARATOR
                                    + "|"
                                    + "\\x05\\d*"
                                + ")$")
                            .matcher(slidingWindowCurrentRow)
                            .replaceAll("");

                    /*
                     * Check either if there is more than 1 row and if there is less than 1 complete row
                     */
                    regexAllLine = 
                        Pattern
                            .compile(
                                MODE
                                + "[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]"
                                + SEPARATOR + TD + SEPARATOR 
                                + "[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]+?$"
                            )
                            .matcher(slidingWindowCurrentRow);
                    Matcher regexSearch2a2 = 
                        Pattern
                            .compile(
                                MODE + SEPARATOR +"[^\\x01-\\x03\\x05-\\x09\\x0B-\\x0C\\x0E-\\x1F]+?$"
                            )
                            .matcher(slidingWindowCurrentRow);

                    /*
                     * If there is more than 1 row, delete the last incomplete one in order to restart properly from it at the next loop,
                     * else if there is 1 row but incomplete, mark it as cut with the letter c
                     */
                    if (regexAllLine.find()) {
                        slidingWindowAllRows = 
                            Pattern
                                .compile(
                                    MODE + SEPARATOR +"[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]+?$"
                                )
                                .matcher(slidingWindowAllRows)
                                .replaceAll("");
                    } else if (regexSearch2a2.find()) {
                        slidingWindowAllRows += StringUtil.hexstr("05") + "1" + StringUtil.hexstr("0804");
                    }

                    /*
                     * Check how many rows we have collected from the very beginning of the query,
                     * then skip every rows we have already found via LIMIT
                     */
                    regexAllLine = 
                        /*
                         * Regex \\x{08}? not supported on Kali
                         * => \\x08? seems ok though
                         */
                        Pattern
                            .compile(
                                MODE +"("
                                    + SEPARATOR 
                                    + "[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?\\x05[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?\\x08?"
                                    + SEPARATOR 
                                +")"
                            )
                            .matcher(slidingWindowAllRows);

                    nbCompleteLine = 0;
                    while (regexAllLine.find()) {
                        nbCompleteLine++;
                    }
                    sqlLimit = nbCompleteLine;

                    // Inform the view about the progression
                    if (numberToFind > 0 && searchName != null) {
                        Request request = new Request();
                        request.setMessage(TypeRequest.UPDATE_PROGRESS);
                        request.setParameters(searchName, sqlLimit);
                        MediatorModel.model().sendToViews(request);
                    }

                    /*
                     * Ending condition: every expected rows have been retrieved.
                     * Inform the view about the progression
                     */
                    if (sqlLimit == numberToFind) {
                        if (numberToFind > 0 && searchName != null) {
                            Request request = new Request();
                            request.setMessage(TypeRequest.UPDATE_PROGRESS);
                            request.setParameters(searchName, numberToFind);
                            MediatorModel.model().sendToViews(request);
                        }
                        break;
                    }

                    /*
                     *  Add the LIMIT statement to the next SQL query and reset variables.
                     *  Put the character cursor to the beginning of the line, and reset the result of the current query
                     */
                    sqlQuery = 
                        Pattern
                            .compile(MODE +"\\{limit\\}")
                            .matcher(initialSQLQuery)
                            .replaceAll(MediatorModel.model().vendor.instance().getSqlLimit(sqlLimit));

                    slidingWindowCurrentRow = "";
                } else {
                    // Inform the view about the progression
                    if (numberToFind > 0 && searchName != null) {
                        Request request = new Request();
                        request.setMessage(TypeRequest.UPDATE_PROGRESS);
                        request.setParameters(searchName, numberToFind);
                        MediatorModel.model().sendToViews(request);
                    }
                    break;
                }
                
            }

            charPositionInCurrentRow = slidingWindowCurrentRow.length() + 1/* - StringUtils.countMatches(slidingWindowCurrentRow, "\n")*/;
        }
        
        ThreadUtil.remove(searchName);

        return slidingWindowAllRows;
    }
}

