/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.model.injection.strategy;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBit.BlindOperator;
import com.jsql.model.injection.strategy.blind.InjectionBlindBin;
import com.jsql.model.injection.vendor.model.VendorYaml;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrategyBlindBin extends AbstractStrategy {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private InjectionBlindBin injectionBlindBin;

    public StrategyBlindBin(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public void checkApplicability() throws StoppedByUserSlidingException {
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isStrategyBlindBinDisabled()) {
            LOGGER.log(LogLevelUtil.CONSOLE_INFORM, AbstractStrategy.FORMAT_SKIP_STRATEGY_DISABLED, this.getName());
            return;
        } else if (StringUtils.isEmpty(
            this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getBinary().getTest().getBin()
        )) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                AbstractStrategy.FORMAT_STRATEGY_NOT_IMPLEMENTED,
                this.getName(),
                this.injectionModel.getMediatorVendor().getVendor()
            );
            return;
        }

        this.checkInjection(BlindOperator.OR);
        this.checkInjection(BlindOperator.AND);
        this.checkInjection(BlindOperator.STACK);
        this.checkInjection(BlindOperator.NO_MODE);

        if (this.isApplicable) {
            this.allow();
            var requestMessageBinary = new Request();
            requestMessageBinary.setMessage(Interaction.MESSAGE_BINARY);
            requestMessageBinary.setParameters(this.injectionBlindBin.getInfoMessage());
            this.injectionModel.sendToViews(requestMessageBinary);
        } else {
            this.unallow();
        }
    }

    private void checkInjection(BlindOperator blindOperator) throws StoppedByUserSlidingException {
        if (this.isApplicable) {
            return;
        }
        LOGGER.log(
            LogLevelUtil.CONSOLE_DEFAULT,
            "{} [{}] with [{}]...",
            () -> I18nUtil.valueByKey(AbstractStrategy.KEY_LOG_CHECKING_STRATEGY),
            this::getName,
            () -> blindOperator
        );
        this.injectionBlindBin = new InjectionBlindBin(this.injectionModel, blindOperator);
        this.isApplicable = this.injectionBlindBin.isInjectable();
        if (this.isApplicable) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_SUCCESS,
                "{} [{}] injection with [{}]",
                () -> I18nUtil.valueByKey(AbstractStrategy.KEY_LOG_VULNERABLE),
                this::getName,
                () -> blindOperator
            );
        }
    }

    @Override
    public void allow(int... i) {
        this.injectionModel.appendAnalysisReport(
            StringUtil.formatReport(LogLevelUtil.COLOR_BLU, "### Strategy: " + this.getName())
            + this.injectionModel.getReportWithoutIndex(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBlindWithOperator(
                    this.injectionModel.getMediatorVendor().getVendor().instance().sqlBlind(StringUtil.formatReport(LogLevelUtil.COLOR_GREEN, "&lt;query&gt;"), "0", true),
                    this.injectionBlindBin.getBooleanMode()
                ),
                "metadataInjectionProcess",
                null
            )
        );
        this.markVulnerability(Interaction.MARK_BLIND_BIN_VULNERABLE);
    }

    @Override
    public void unallow(int... i) {
        this.markVulnerability(Interaction.MARK_BLIND_BIN_INVULNERABLE);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable, String metadataInjectionProcess) throws StoppedByUserSlidingException {
        return this.injectionBlindBin.inject(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlBlind(sqlQuery, startPosition, false),
            stoppable
        );
    }

    @Override
    public void activateWhenApplicable() {
        if (this.injectionModel.getMediatorStrategy().getStrategy() == null && this.isApplicable()) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                "{} [{}] with [{}]",
                () -> I18nUtil.valueByKey("LOG_USING_STRATEGY"),
                this::getName,
                () -> this.injectionBlindBin.getBooleanMode().name()
            );
            this.injectionModel.getMediatorStrategy().setStrategy(this);

            var requestMarkBlindBinStrategy = new Request();
            requestMarkBlindBinStrategy.setMessage(Interaction.MARK_BLIND_BIN_STRATEGY);
            this.injectionModel.sendToViews(requestMarkBlindBinStrategy);
        }
    }
    
    @Override
    public String getPerformanceLength() {
        return VendorYaml.DEFAULT_CAPACITY;
    }
    
    @Override
    public String getName() {
        return "Blind bin";
    }
}
