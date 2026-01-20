package com.jsql.model.suspendable;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Request3;
import com.jsql.model.exception.JSqlRuntimeException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBit;
import com.jsql.model.injection.strategy.blind.InjectionVendor;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class SuspendableGetVendor extends AbstractSuspendable {

    private static final Logger LOGGER = LogManager.getRootLogger();

    public SuspendableGetVendor(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public String run(Object... args) {
        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Fingerprinting database with Boolean match...");

        AtomicBoolean isVendorFound = new AtomicBoolean(false);
        this.injectionModel.getMediatorVendor().getVendorsForFingerprint()
        .stream()
        .filter(vendor -> vendor != this.injectionModel.getMediatorVendor().getAuto())
        .filter(vendor -> StringUtils.isNotEmpty(
            vendor.instance().getModelYaml().getStrategy().getConfiguration().getFingerprint().getVendorSpecific()
        ))
        .forEach(vendor -> {
            if (isVendorFound.get()) {
                return;
            }
            String vendorSpecificWithOperator = this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBlindWithOperator(
                vendor.instance().getModelYaml().getStrategy().getConfiguration().getFingerprint().getVendorSpecific(),
                AbstractInjectionBit.BlindOperator.OR  // TODO should also test AND and no mode
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

                    this.injectionModel.sendToViews(new Request3.SetVendor(
                        this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlByUser(),
                        this.injectionModel.getMediatorVendor().getVendor()
                    ));
                }
            } catch (StoppedByUserSlidingException e) {
                throw new JSqlRuntimeException(e);
            }
        });
        return null;  // unused
    }
}