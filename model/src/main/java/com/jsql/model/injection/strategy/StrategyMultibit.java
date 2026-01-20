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
import com.jsql.model.bean.util.Request3;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBit.BlindOperator;
import com.jsql.model.injection.strategy.blind.InjectionMultibit;
import com.jsql.model.injection.vendor.model.VendorYaml;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrategyMultibit extends AbstractStrategy {

    private static final Logger LOGGER = LogManager.getRootLogger();

    private InjectionMultibit injection;

    public StrategyMultibit(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public void checkApplicability() throws StoppedByUserSlidingException {
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isStrategyMultibitDisabled()) {
            LOGGER.log(LogLevelUtil.CONSOLE_INFORM, AbstractStrategy.FORMAT_SKIP_STRATEGY_DISABLED, this.getName());
            return;
        } else if (StringUtils.isEmpty(
            this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getBinary().getMultibit()
        )) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                AbstractStrategy.FORMAT_STRATEGY_NOT_IMPLEMENTED,
                this.getName(),
                this.injectionModel.getMediatorVendor().getVendor()
            );
            return;
        }

        this.checkInjection(BlindOperator.NO_MODE);

        if (this.isApplicable) {
            this.allow();
            this.injectionModel.sendToViews(new Request3.MessageBinary(this.injection.getInfoMessage()));
        } else {
            this.unallow();
        }
    }

    private void checkInjection(BlindOperator blindOperator) throws StoppedByUserSlidingException {
        if (this.isApplicable) {
            return;
        }
        this.logChecking();
        this.injection = new InjectionMultibit(this.injectionModel, blindOperator);
        this.isApplicable = this.injection.isInjectable();
        if (this.isApplicable) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_SUCCESS,
                "{} Multibit injection",
                () -> I18nUtil.valueByKey(AbstractStrategy.KEY_LOG_VULNERABLE)
            );
        }
    }

    @Override
    public void allow(int... i) {
        this.injectionModel.appendAnalysisReport(
            StringUtil.formatReport(LogLevelUtil.COLOR_BLU, "### Strategy: " + this.getName())
            + this.injectionModel.getReportWithoutIndex(
                    this.injectionModel.getMediatorVendor().getVendor().instance().sqlMultibit(
                    this.injectionModel.getMediatorVendor().getVendor().instance().sqlBlind(
                        StringUtil.formatReport(LogLevelUtil.COLOR_GREEN, "&lt;query&gt;"),
                        "0",
                        true
                    ),
                    0,
                    1
                ),
                "metadataInjectionProcess",
                null
            )
        );
        this.injectionModel.sendToViews(new Request3.MarkMultibitVulnerable(this));
    }

    @Override
    public void unallow(int... i) {
        this.injectionModel.sendToViews(new Request3.MarkMultibitInvulnerable(this));
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable, String metadataInjectionProcess) throws StoppedByUserSlidingException {
        return this.injection.inject(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlBlind(sqlQuery, startPosition, false),
            stoppable
        );
    }

    @Override
    public void activateWhenApplicable() {
        if (this.injectionModel.getMediatorStrategy().getStrategy() == null && this.isApplicable()) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                "{} [{}]",
                () -> I18nUtil.valueByKey("LOG_USING_STRATEGY"),
                this::getName
            );
            this.injectionModel.getMediatorStrategy().setStrategy(this);
            this.injectionModel.sendToViews(new Request3.MarkMultibitStrategy(this));
        }
    }
    
    @Override
    public String getPerformanceLength() {
        return VendorYaml.DEFAULT_CAPACITY;
    }
    
    @Override
    public String getName() {
        return "Multibit";
    }
}
