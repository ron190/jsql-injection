package com.jsql.model.suspendable;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.subscriber.Seal;
import com.jsql.model.exception.JSqlRuntimeException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBit;
import com.jsql.model.injection.strategy.blind.InjectionEngine;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class SuspendableGetEngine extends AbstractSuspendable {

    private static final Logger LOGGER = LogManager.getRootLogger();

    public SuspendableGetEngine(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public String run(Input input) throws JSqlException {
        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Fingerprinting database with boolean match (step 4)...");

        AtomicBoolean isEngineFound = new AtomicBoolean(false);
        this.injectionModel.getMediatorEngine().getEnginesForFingerprint()
        .stream()
        .filter(engine -> engine != this.injectionModel.getMediatorEngine().getAuto())
        .filter(engine -> StringUtils.isNotEmpty(
            engine.instance().getModelYaml().getStrategy().getConfiguration().getFingerprint().getEngineSpecific()
        ))
        .forEach(engine -> {
            if (isEngineFound.get()) {
                return;
            }
            String engineSpecificWithOperator = this.injectionModel.getMediatorEngine().getEngine().instance().sqlTestBlindWithOperator(
                engine.instance().getModelYaml().getStrategy().getConfiguration().getFingerprint().getEngineSpecific(),
                AbstractInjectionBit.BlindOperator.OR  // TODO should also test AND and no mode
            );
            try {
                var injectionEngine = new InjectionEngine(this.injectionModel, engineSpecificWithOperator, engine);
                if (injectionEngine.isInjectable(engineSpecificWithOperator)) {
                    if (this.isSuspended()) {
                        throw new StoppedByUserSlidingException();
                    }

                    LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Found [{}] using boolean match", engine);
                    this.injectionModel.getMediatorEngine().setEngine(engine);
                    isEngineFound.set(true);

                    this.injectionModel.sendToViews(new Seal.ActivateEngine(this.injectionModel.getMediatorEngine().getEngine()));
                }
            } catch (StoppedByUserSlidingException e) {
                throw new JSqlRuntimeException(e);
            }
        });
        return null;  // unused
    }
}