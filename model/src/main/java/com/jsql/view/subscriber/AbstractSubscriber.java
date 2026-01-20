package com.jsql.view.subscriber;

import com.jsql.model.bean.util.Request3;
import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public abstract class AbstractSubscriber implements Subscriber<Request3> {

    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Observer pattern.<br>
     * Receive an update order from the model:<br>
     * - Use the Request message to get the Interaction class,<br>
     * - Pass the parameters to that class.
     */
    public Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);  // enable receiving items
    }

    @Override
    public void onNext(Request3 request) {
        this.subscription.request(1);

        // Display model thread name in logs instead of the observer name
        String nameThread = Thread.currentThread().getName();
        SwingUtilities.invokeLater(() -> {
            Thread.currentThread().setName("from " + nameThread);
            this.execute(request);
        });
    }

    protected abstract void execute(Request3 request);

    @Override
    public void onError(Throwable e) {
        LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
    }

    @Override
    public void onComplete() {
        // Nothing
    }
}
