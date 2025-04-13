package com.jsql.model.injection.strategy;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.injection.vendor.model.VendorYaml;
import com.jsql.model.suspendable.SuspendableGetCharInsertion;
import com.jsql.model.suspendable.SuspendableGetVendor;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public class MediatorStrategy {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private final AbstractStrategy time;
    private final AbstractStrategy blind;
    private final AbstractStrategy blindBinary;
    private final AbstractStrategy multibit;
    private final StrategyError error;
    private final AbstractStrategy union;
    private final AbstractStrategy stack;

    private final List<AbstractStrategy> strategies;
    
    /**
     * Current injection strategy.
     */
    private AbstractStrategy strategy;

    private final InjectionModel injectionModel;
    
    public MediatorStrategy(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
        
        this.time = new StrategyTime(this.injectionModel);
        this.blind = new StrategyBlind(this.injectionModel);
        this.blindBinary = new StrategyBlindBinary(this.injectionModel);
        this.multibit = new StrategyMultibit(this.injectionModel);
        this.error = new StrategyError(this.injectionModel);
        this.union = new StrategyUnion(this.injectionModel);
        this.stack = new StrategyStack(this.injectionModel);

        this.strategies = Arrays.asList(this.time, this.blindBinary, this.blind, this.multibit, this.error, this.stack, this.union);
    }
    
    public String getMeta() {
        String strategyName = this.strategy == null ? StringUtils.EMPTY : this.strategy.toString().toLowerCase();
        var strategyMode = "default";
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isDiosStrategy()) {
            strategyMode = "dios";
        } else if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isZipStrategy()) {
            strategyMode = "zip";
        }
        return String.format("%s#%s", strategyName.replace(" ", "-"), strategyMode);
    }
    
    /**
     * Build correct data for GET, POST, HEADER.
     * Each can be either raw data (no injection), SQL query without index requirement,
     * or SQL query with index requirement.
     * @param urlBase Beginning of the request data
     * @param isUsingIndex False if request doesn't use indexes
     * @param sqlTrail SQL statement
     * @return Final data
     */
    public String buildPath(String urlBase, boolean isUsingIndex, String sqlTrail) {
        String result = urlBase;
        if (urlBase.contains(InjectionModel.STAR)) {
            if (!isUsingIndex) {
                result = urlBase.replace(InjectionModel.STAR, this.encodePath(sqlTrail));
            } else {
                result = urlBase.replace(
                    InjectionModel.STAR,
                    this.encodePath(
                        this.injectionModel.getIndexesInUrl().replaceAll(
                            String.format(VendorYaml.FORMAT_INDEX, this.getSpecificUnion().getVisibleIndex()),
                            Matcher.quoteReplacement(sqlTrail)  // Oracle column can contain regex char $ => quoteReplacement()
                        )
                    )
                );
            }
        }
        return result;
    }

    private String encodePath(String sqlTrail) {
        String sqlTrailEncoded = StringUtil.cleanSql(sqlTrail);

        if (!this.injectionModel.getMediatorUtils().getPreferencesUtil().isUrlEncodingDisabled()) {
            sqlTrailEncoded = sqlTrailEncoded
                .replace("'", "%27")
                .replace("(", "%28")
                .replace(")", "%29")
                .replace("{", "%7b")
                .replace("[", "%5b")
                .replace("]", "%5d")
                .replace("}", "%7d")
                .replace(">", "%3e")
                .replace("<", "%3c")
                .replace("?", "%3f")
                .replace("_", "%5f")
                .replace("\\", "%5c")
                .replace(",", "%2c");
        }

        // URL forbidden characters
        return (sqlTrailEncoded + this.injectionModel.getMediatorVendor().getVendor().instance().endingComment())
            .replace("\"", "%22")
            .replace("|", "%7c")
            .replace("`", "%60")
            .replace(StringUtils.SPACE, "%20")
            .replace("+", "%20");
    }
    
    /**
     * Find the insertion character, test each strategy, inject metadata and list databases.
     * @param parameterToInject to be tested, null when injection point
     * @return true when successful injection
     * @throws JSqlException when no params integrity, process stopped by user, or injection failure
     */
    public boolean testStrategies(SimpleEntry<String, String> parameterToInject) throws JSqlException {
        // Define insertionCharacter, i.e, -1 in "[..].php?id=-1 union select[..]",
        
        String parameterOriginalValue = null;
        
        // Fingerprint database
        this.injectionModel.getMediatorVendor().setVendor(this.injectionModel.getMediatorVendor().fingerprintVendor());
        
        // If not an injection point then find insertion character.
        // Force to 1 if no insertion char works and empty value from user,
        // Force to user's value if no insertion char works,
        // Force to insertion char otherwise.
        // parameterToInject null on true STAR injection
        // TODO Use also on Json injection where parameter == null
        if (parameterToInject != null) {
            parameterOriginalValue = parameterToInject.getValue();
                     
            // Test for params integrity
            String characterInsertionByUser = this.injectionModel.getMediatorUtils().getParameterUtil().initStar(parameterToInject);
            
            String characterInsertion = this.injectionModel.getMediatorUtils().getPreferencesUtil().isNotSearchingCharInsertion()
                ? characterInsertionByUser
                : new SuspendableGetCharInsertion(this.injectionModel).run(characterInsertionByUser);
            if (characterInsertion.contains(InjectionModel.STAR)) {  // When injecting all parameters or JSON
                parameterToInject.setValue(characterInsertion);
            } else {  // When injecting last parameter
                parameterToInject.setValue(characterInsertion.replaceAll("(\\w)$", "$1+") + InjectionModel.STAR);
            }
        } else if (this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlBase().contains(InjectionModel.STAR)) {
            String characterInsertion = new SuspendableGetCharInsertion(this.injectionModel).run(StringUtils.EMPTY);
            String urlBase = this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlBase();
            this.injectionModel.getMediatorUtils().getConnectionUtil().setUrlBase(
                // Space %20 for URL, do not use +
                urlBase.replace(InjectionModel.STAR, characterInsertion.replaceAll("(\\w)$", "$1%20") + InjectionModel.STAR)
            );
        }

        if (this.injectionModel.getMediatorVendor().getVendorByUser() == this.injectionModel.getMediatorVendor().getAuto()) {
            new SuspendableGetVendor(this.injectionModel).run();
        }

        // Test each injection strategies: time < blind binary < blind bitwise < multibit < error < stack < union
        this.time.checkApplicability();
        this.blindBinary.checkApplicability();
        this.blind.checkApplicability();

        if (parameterToInject != null) {
            // Multibit requires '0'
            // TODO char insertion 0' should also work on "where x='$param'"
            var backupCharacterInsertion = parameterToInject.getValue();
            parameterToInject.setValue(InjectionModel.STAR);
            this.multibit.checkApplicability();
            parameterToInject.setValue(backupCharacterInsertion);
        } else {
            this.multibit.checkApplicability();
        }

        this.error.checkApplicability();
        this.stack.checkApplicability();
        this.union.checkApplicability();

        // Set most efficient strategy first: union > stack > error > multibit > blind bitwise > blind binary > time
        this.union.activateWhenApplicable();
        this.stack.activateWhenApplicable();
        this.error.activateWhenApplicable();
        this.multibit.activateWhenApplicable();
        this.blind.activateWhenApplicable();
        this.blindBinary.activateWhenApplicable();
        this.time.activateWhenApplicable();

        if (this.injectionModel.getMediatorStrategy().getStrategy() == null) {  // no strategy found
            // Restore initial parameter value on injection failure
            // Only when not true STAR injection
            if (parameterOriginalValue != null) {
                parameterToInject.setValue(parameterOriginalValue.replace(InjectionModel.STAR, StringUtils.EMPTY));
            }

            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "No injection found");
            return false;
        }
        
        return true;
    }
    
    
    // Getter and setter

    public AbstractStrategy getUnion() {
        return this.union;
    }

    public StrategyUnion getSpecificUnion() {
        return (StrategyUnion) this.union;
    }

    public StrategyError getError() {
        return this.error;
    }

    public AbstractStrategy getBlind() {
        return this.blind;
    }

    public AbstractStrategy getMultibit() {
        return this.multibit;
    }

    public AbstractStrategy getTime() {
        return this.time;
    }

    public AbstractStrategy getStack() {
        return this.stack;
    }

    public List<AbstractStrategy> getStrategies() {
        return this.strategies;
    }

    public AbstractStrategy getStrategy() {
        return this.strategy;
    }

    public void setStrategy(AbstractStrategy strategy) {
        this.strategy = strategy;
    }

    public AbstractStrategy getBlindBinary() {
        return this.blindBinary;
    }
}
