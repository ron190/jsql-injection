package com.jsql.model.injection.suspendable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.AbstractElementDatabase;
import com.jsql.model.bean.Request;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.strategy.AbstractInjectionStrategy;
import com.jsql.tool.ToolsString;

/**
 * Get all data from a SQL request (remember that data will often been cut, we need to reach ALL the data)
 * We expect the following well formed line:
 * => hh[0-9A-F]*jj[0-9A-F]*c?hhgghh[0-9A-F]*jj[0-9A-F]*c?hhg...hi
 * We must check if that long line is cut, and where it is cut, basically we will move our position in a virtual 2D array,
 * and use LIMIT and MID to move the cursor ; LIMIT skips whole line (useful if result contains 1 or more complete row) ; and
 * MID skips characters in a line (useful if result contains less than 1 row).
 * The process can be interrupted by the user (stop/pause).
 */
public class SuspendableGetRows extends AbstractSuspendable {
    
    @Override
    public String action(Object... args) throws PreparationException, StoppableException {
        String initialSQLQuery = (String) args[0];
        String[] sourcePage = (String[]) args[1];
        boolean useLimit = (Boolean) args[2];
        int numberToFind = (Integer) args[3];
        AbstractElementDatabase searchName = (AbstractElementDatabase) args[4];
        MediatorModel.model().suspendables.put(searchName, this);

        String sqlQuery = new String(initialSQLQuery).replaceAll("\\{limit\\}", MediatorModel.model().currentVendor.getStrategy().getLimit(0));

        AbstractInjectionStrategy injectionStrategy = MediatorModel.model().getInjectionStrategy();
        
        /*
         * As we know the expected number of rows (numberToFind), then it stops injection if all rows are found,
         * keep track of rows we have reached (limitSQLResult) and use these to skip entire rows,
         * keep track of characters we have reached (startPosition) and use these to skip characters,
         */
        /**
         * TODO simpler loop
         */
        String slidingWindowAllRows = "", slidingWindowCurrentRow = "";
        int sqlLimit = 0, charPositionInCurrentRow = 1;
        
        while (true) {

            if (this.shouldSuspend()) {
                break;
            }
            
            sourcePage[0] = injectionStrategy.inject(sqlQuery, charPositionInCurrentRow + "", this);
            
            /**
             * on prend entre 1 et maxPerf caractères après le marqueur SQLi
             * performanceQuery() trouve le max 65536 ou moins
             * SQLiblahblah1337      ] : fin ou limit+1
             * SQLiblahblah      blah] : continue substr()
             */
            // Parse all the data we have retrieved
            Matcher regexAllLine = 
                    Pattern
                    .compile("(?si)SQLi(.{1,"+ injectionStrategy.getPerformanceLength() +"})")
                    .matcher(sourcePage[0]);
            
            Matcher regexEndOfLine = 
                    Pattern
                    .compile("(?si)SQLi\\x01\\x03\\x03\\x07")
                    .matcher(sourcePage[0]);
            
            if (regexEndOfLine.find() && useLimit && !"".equals(slidingWindowAllRows)) {
                // Update the view only if there are value to find, and if it's not the root (empty tree)
                if (numberToFind > 0 && searchName != null) {
                    Request request = new Request();
                    request.setMessage("UpdateProgress");
                    request.setParameters(searchName, numberToFind);
                    MediatorModel.model().interact(request);
                }
                break;
            }
            /*
             * Ending condition:
             * One row could be very long, longer than the database can provide
             * #Need verification
             */
            if (!regexAllLine.find() && useLimit && !"".equals(slidingWindowAllRows)) {
                // Update the view only if there are value to find, and if it's not the root (empty tree)
                if (numberToFind > 0 && searchName != null) {
                    Request request = new Request();
                    request.setMessage("UpdateProgress");
                    request.setParameters(searchName, numberToFind);
                    MediatorModel.model().interact(request);
                }
                break;
            }

            /*
             * Add the result to the data already found, informs the view about it.
             * If throws exception, inform the view about the failure.
             */
            try {
                slidingWindowCurrentRow += regexAllLine.group(1);

                Request request = new Request();
                request.setMessage("MessageChunk");
                request.setParameters(
                    Pattern
                    .compile("(?si)\\x01\\x03\\x03\\x07.*")
                    .matcher(regexAllLine.group(1))
                    .replaceAll("\n")
                );
                MediatorModel.model().interact(request);
            } catch (IllegalStateException e) {
                // if it's not the root (empty tree)
                if (searchName != null) {
                    Request request = new Request();
                    request.setMessage("EndProgress");
                    request.setParameters(searchName);
                    MediatorModel.model().interact(request);
                }
                /**
                 * TODO Injection Exception
                 */
                throw new PreparationException("Fetching fails: no data to parse for " + searchName + "\nSource page : " + sourcePage[0]);
            }

            /*
             * Check how many rows we have collected from the beginning of that chunk
             */
            regexAllLine = 
                    Pattern
                    .compile("(?si)(\\x04([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?)\\x05([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?)(\\x08)?\\x04)")
                    .matcher(slidingWindowCurrentRow);
            int nbCompleteLine = 0;
            while (regexAllLine.find()) {
                nbCompleteLine++;
            }

            /*
             * Inform the view about the progression
             */
            if (useLimit && numberToFind > 0 && searchName != null) {
                Request request = new Request();
                request.setMessage("UpdateProgress");
                request.setParameters(searchName, sqlLimit + nbCompleteLine);
                MediatorModel.model().interact(request);
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
                        .compile("(?si)\\x01\\x03\\x03\\x07.*")
                        .matcher(slidingWindowCurrentRow)
                        .replaceAll("");
                slidingWindowAllRows += slidingWindowCurrentRow;
                if (useLimit) {
                    /*
                     * Remove everything not properly attached to the last row:
                     * 1. very start of a new row: XXXXXhhg[ghh]$
                     * 2. very end of the last row: XXXXX[jj00]$
                     */
                    slidingWindowAllRows = 
                            Pattern
                            .compile("(?si)(\\x06\\x04|\\x05\\d*)$")
                            .matcher(slidingWindowAllRows)
                            .replaceAll("");
                    slidingWindowCurrentRow = 
                            Pattern
                            .compile("(?si)(\\x06\\x04|\\x05\\d*)$")
                            .matcher(slidingWindowCurrentRow)
                            .replaceAll("");

                    /*
                     * Check either if there is more than 1 row and if there is less than 1 complete row
                     */
                    regexAllLine = 
                            Pattern
                            .compile("(?si)[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]\\x04\\x06\\x04[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]+?$")
                            .matcher(slidingWindowCurrentRow);
                    Matcher regexSearch2a2 = 
                            Pattern
                            .compile("(?si)\\x04[^\\x01-\\x03\\x05-\\x09\\x0B-\\x0C\\x0E-\\x1F]+?$")
                            .matcher(slidingWindowCurrentRow);

                    /*
                     * If there is more than 1 row, delete the last incomplete one in order to restart properly from it at the next loop,
                     * else if there is 1 row but incomplete, mark it as cut with the letter c
                     */
                    if (regexAllLine.find()) {
                        slidingWindowAllRows = 
                                Pattern
                                .compile("(?si)\\x04[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]+?$")
                                .matcher(slidingWindowAllRows)
                                .replaceAll("");
                    } else if (regexSearch2a2.find()) {
                        slidingWindowAllRows += ToolsString.hexstr("05") + "1" + ToolsString.hexstr("0804");
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
                            .compile("(?si)(\\x04[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?\\x05[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?\\x08?\\x04)")
                            .matcher(slidingWindowAllRows);

                    nbCompleteLine = 0;
                    while (regexAllLine.find()) {
                        nbCompleteLine++;
                    }
                    sqlLimit = nbCompleteLine;

                    // Inform the view about the progression
                    if (numberToFind > 0 && searchName != null) {
                        Request request = new Request();
                        request.setMessage("UpdateProgress");
                        request.setParameters(searchName, sqlLimit);
                        MediatorModel.model().interact(request);
                    }

                    /*
                     * Ending condition: every expected rows have been retrieved.
                     * Inform the view about the progression
                     */
                    if (sqlLimit == numberToFind) {
                        if (numberToFind > 0 && searchName != null) {
                            Request request = new Request();
                            request.setMessage("UpdateProgress");
                            request.setParameters(searchName, numberToFind);
                            MediatorModel.model().interact(request);
                        }
                        break;
                    }

                    /*
                     *  Add the LIMIT statement to the next SQL query and reset variables.
                     *  Put the character cursor to the beginning of the line, and reset the result of the current query
                     */
                    sqlQuery = Pattern
                        .compile("(?si)\\{limit\\}")
                        .matcher(initialSQLQuery)
                        .replaceAll(MediatorModel.model().currentVendor.getStrategy().getLimit(sqlLimit));
                    charPositionInCurrentRow = 1;
                    slidingWindowCurrentRow = "";
                } else {
                    // Inform the view about the progression
                    if (numberToFind > 0 && searchName != null) {
                        Request request = new Request();
                        request.setMessage("UpdateProgress");
                        request.setParameters(searchName, numberToFind);
                        MediatorModel.model().interact(request);
                    }
                    break;
                }
                
            }

            charPositionInCurrentRow = slidingWindowCurrentRow.length() + 1;
        }

        return slidingWindowAllRows;
    }
}

