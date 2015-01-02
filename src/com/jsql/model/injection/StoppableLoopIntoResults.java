package com.jsql.model.injection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.AbstractElementDatabase;
import com.jsql.model.bean.Request;
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
public class StoppableLoopIntoResults extends AbstractSuspendable {
    @Override
    public String action(Object... args) throws PreparationException, StoppableException {
        String initialSQLQuery = (String) args[0];
        String[] sourcePage = (String[]) args[1];
        boolean useLimit = (Boolean) args[2];
        int numberToFind = (Integer) args[3];
        AbstractElementDatabase searchName = (AbstractElementDatabase) args[4];
        MediatorModel.model().suspendables.remove(searchName);
        MediatorModel.model().suspendables.put(searchName, this);

        String sqlQuery = new String(initialSQLQuery).replaceAll("\\{limit\\}", MediatorModel.model().sqlStrategy.getLimit(0));

        AbstractInjectionStrategy injectionStrategy = MediatorModel.model().getInjectionStrategy();
        /*
         * As we know the expected number of rows (numberToFind), then it stops injection if all rows are found,
         * keep track of rows we have reached (limitSQLResult) and use these to skip entire rows,
         * keep track of characters we have reached (startPosition) and use these to skip characters,
         */
        String finalResultSourcea2 = "", currentResultSourcea2 = "";
        for (int limitSQLResult = 0, startPosition = 1;; startPosition = currentResultSourcea2.length() + 1) {

            // try {
            //     Thread.sleep(500);
            // } catch (InterruptedException e) {
            //     this.model.sendDebugMessage(e);
            // }

            if (this.stopOrPause()) {
                break;
            }
            
            sourcePage[0] = injectionStrategy.inject(sqlQuery, startPosition + "", this);
            
            /**
             * on prend entre 1 et maxPerf caractères après le marqueur SQLi
             * performanceQuery() trouve le max 65536 ou moins
             * SQLiblahblah1337      ] : fin ou limit+1
             * SQLiblahblah      blah] : continue substr()
             */
            // Parse all the data we have retrieved
            Matcher regexSearcha2 = Pattern.compile("SQLi(.{1,"+ injectionStrategy.getPerformanceLength() +"})", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(sourcePage[0]);
            
            Matcher regexSearch = Pattern.compile("SQLi\\x01\\x03\\x03\\x07", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(sourcePage[0]);
            if (regexSearch.find()) {
                if (useLimit && !"".equals(finalResultSourcea2)) {
                    //                        model.sendMessage("A");
                    // Update the view only if there are value to find, and if it's not the root (empty tree)
                    if (numberToFind > 0 && searchName != null) {
                        Request request = new Request();
                        request.setMessage("UpdateProgress");
                        request.setParameters(searchName, numberToFind);
                        MediatorModel.model().interact(request);
                    }
                    break;
                }
            }
            /*
             * Ending condition:
             * One row could be very long, longer than the database can provide
             * #Need verification
             */
            if (!regexSearcha2.find()) {
                if (useLimit && !"".equals(finalResultSourcea2)) {
                    //                        model.sendMessage("A");
                    // Update the view only if there are value to find, and if it's not the root (empty tree)
                    if (numberToFind > 0 && searchName != null) {
                        Request request = new Request();
                        request.setMessage("UpdateProgress");
                        request.setParameters(searchName, numberToFind);
                        MediatorModel.model().interact(request);
                    }
                    break;
                }
            }

            /*
             * Add the result to the data already found, informs the view about it.
             * If throws exception, inform the view about the failure.
             */
            try {
                currentResultSourcea2 += regexSearcha2.group(1);

                Request request = new Request();
                request.setMessage("MessageChunk");
                request.setParameters(regexSearcha2.group(1) + " ");
                MediatorModel.model().interact(request);
            } catch (IllegalStateException e) {
                // if it's not the root (empty tree)
                if (searchName != null) {
                    Request request = new Request();
                    request.setMessage("EndProgress");
                    request.setParameters(searchName);
                    MediatorModel.model().interact(request);
                }
                throw new PreparationException("Fetching fails: no data to parse for " + searchName);
            }

            /*
             * Check how many rows we have collected from the beginning of that chunk
             */
            regexSearcha2 = Pattern.compile("(\\x04([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?)\\x05([^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?)(\\x08)?\\x04)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSourcea2);
            int nbResult2 = 0;
            while (regexSearcha2.find()) {
                nbResult2++;
            }

            /*
             * Inform the view about the progression
             */
            if (useLimit) {
                if (numberToFind > 0 && searchName != null) {
                    Request request = new Request();
                    request.setMessage("UpdateProgress");
                    request.setParameters(searchName, limitSQLResult + nbResult2);
                    MediatorModel.model().interact(request);
                }
                //                    System.out.println("Request " + i + ", data collected "+(limitSQLResult + nbResult) + (numberToFind>0?"/"+numberToFind:"") + " << " + currentResultSource.length() + " bytes" );
            }

            /*
             * We have properly reached the i at the end of the query: iLQS
             * => hhxxxxxxxxjj00hhgghh...hiLQS
             */
            /* Design Pattern: State? */
            if (nbResult2>0 || currentResultSourcea2.matches("(?s).*\\x01\\x03\\x03\\x07.*")) {
                /*
                 * Remove everything after our result
                 * => hhxxxxxxxxjj00hhgghh...h |-> iLQSjunk
                 */
                finalResultSourcea2 += currentResultSourcea2 = Pattern.compile("\\x01\\x03\\x03\\x07.*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSourcea2).replaceAll("");
                if (useLimit) {
                    /*
                     * Remove everything not properly attached to the last row:
                     * 1. very start of a new row: XXXXXhhg[ghh]$
                     * 2. very end of the last row: XXXXX[jj00]$
                     */
                    finalResultSourcea2 = Pattern.compile("(\\x06\\x04|\\x05\\d*)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(finalResultSourcea2).replaceAll("");
                    currentResultSourcea2 = Pattern.compile("(\\x06\\x04|\\x05\\d*)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSourcea2).replaceAll("");

                    /*
                     * Check either if there is more than 1 row and if there is less than 1 complete row
                     */
                    regexSearcha2 = Pattern.compile("[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]\\x04\\x06\\x04[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]+?$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSourcea2);
                    Matcher regexSearch2a2 = Pattern.compile("\\x04[^\\x01-\\x03\\x05-\\x09\\x0B-\\x0C\\x0E-\\x1F]+?$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSourcea2);

                    /*
                     * If there is more than 1 row, delete the last incomplete one in order to restart properly from it at the next loop,
                     * else if there is 1 row but incomplete, mark it as cut with the letter c
                     */
                    if (regexSearcha2.find()) {
                        finalResultSourcea2 = Pattern.compile(
                            "\\x04[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]+?$",
                            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
                        ).matcher(finalResultSourcea2).replaceAll("");
                    } else if (regexSearch2a2.find()) {
                        finalResultSourcea2 += ToolsString.hexstr("05") + "1" + ToolsString.hexstr("0804");
                    }

                    /*
                     * Check how many rows we have collected from the very beginning of the query,
                     * then skip every rows we have already found via LIMIT
                     */
                    regexSearcha2 = Pattern.compile(
                            /*
                             * Regex \\x{08}? not supported on Kali
                             * => \\x08? seems ok though
                             */
                            "(\\x04[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?\\x05[^\\x01-\\x09\\x0B-\\x0C\\x0E-\\x1F]*?\\x08?\\x04)",
                            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
                            ).matcher(finalResultSourcea2);

                    nbResult2 = 0;
                    while (regexSearcha2.find()) {
                        nbResult2++;
                    }
                    limitSQLResult = nbResult2;

                    // Inform the view about the progression
                    //                        System.out.println("Request " + i + ", data collected " + limitSQLResult + (numberToFind>0 ? "/"+numberToFind : "" ));
                    if (numberToFind > 0 && searchName != null) {
                        Request request = new Request();
                        request.setMessage("UpdateProgress");
                        request.setParameters(searchName, limitSQLResult);
                        MediatorModel.model().interact(request);
                    }

                    /*
                     * Ending condition: every expected rows have been retrieved.
                     * Inform the view about the progression
                     */
                    if (limitSQLResult == numberToFind) {
                        //                            model.sendMessage("B");
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
                    sqlQuery = Pattern.compile(
                            "\\{limit\\}",
                            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
                            ).matcher(initialSQLQuery).replaceAll(
                                MediatorModel.model().sqlStrategy.getLimit(limitSQLResult)
                            );
                    startPosition = 1;
                    currentResultSourcea2 = "";
                } else {
                    // Inform the view about the progression
                    //                        model.sendMessage("C");
                    if (numberToFind > 0 && searchName != null) {
                        Request request = new Request();
                        request.setMessage("UpdateProgress");
                        request.setParameters(searchName, numberToFind);
                        MediatorModel.model().interact(request);
                    }
                    break;
                }
                
            }

        }

        return finalResultSourcea2;
    }
}

