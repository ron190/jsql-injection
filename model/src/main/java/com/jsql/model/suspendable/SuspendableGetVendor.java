package com.jsql.model.suspendable;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.JSqlRuntimeException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBit;
import com.jsql.model.injection.strategy.blind.InjectionVendor;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SuspendableGetVendor extends AbstractSuspendable {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    public SuspendableGetVendor(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public String run(Object... args) {
        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Fingerprinting database with Boolean match...");

        AtomicBoolean isVendorFound = new AtomicBoolean(false);
        this.injectionModel.getMediatorVendor().getVendors()
        .stream()
        .filter(vendor -> vendor != this.injectionModel.getMediatorVendor().getAuto())
        .filter(vendor -> StringUtils.isNotEmpty(
            vendor.instance().getModelYaml().getStrategy().getConfiguration().getFingerprint().getVendorSpecific()
        ))
        .forEach(vendor -> {
            if (isVendorFound.get()) {
                return;
            }
            String vendorSpecificWithOperator = String.format(
                "%s %s",
                AbstractInjectionBit.BlindOperator.OR.name(),
                vendor.instance().getModelYaml().getStrategy().getConfiguration().getFingerprint().getVendorSpecific()
            );
            try {
                var injectionCharInsertion = new InjectionVendor(this.injectionModel, vendorSpecificWithOperator, vendor);
                if (injectionCharInsertion.isInjectable(vendorSpecificWithOperator)) {
                    if (this.isSuspended()) {
                        throw new StoppedByUserSlidingException();
                    }

                    LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Found [{}] using Boolean match", vendor);
                    this.injectionModel.getMediatorVendor().setVendor(vendor);
                    isVendorFound.set(true);

                    var requestSetVendor = new Request();
                    requestSetVendor.setMessage(Interaction.SET_VENDOR);
                    Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
                    msgHeader.put(Header.URL, this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlByUser());
                    msgHeader.put(Header.VENDOR, this.injectionModel.getMediatorVendor().getVendor());
                    requestSetVendor.setParameters(msgHeader);
                    this.injectionModel.sendToViews(requestSetVendor);
                }
            } catch (StoppedByUserSlidingException e) {
                throw new JSqlRuntimeException(e);
            }
        });
        return null;  // unused
    }
}