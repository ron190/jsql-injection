package com.jsql.view.interaction;

import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class SubscriberInteraction implements Subscriber<Request> {

    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private final String packageInteraction;

    /**
     * Observer pattern.<br>
     * Receive an update order from the model:<br>
     * - Use the Request message to get the Interaction class,<br>
     * - Pass the parameters to that class.
     */
    private Subscription subscription;
    
    public SubscriberInteraction(String packageInteraction) {
        this.packageInteraction = packageInteraction;
    }
    
    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(Request request) {
        this.subscription.request(1);
        if (Interaction.UNSUBSCRIBE.equals(request.getMessage())) {
            this.subscription.cancel();
            return;
        }
        
        // Display model thread name in logs instead of the observer name
        String nameThread = Thread.currentThread().getName();
        
        SwingUtilities.invokeLater(() -> {
            Thread.currentThread().setName("from " + nameThread);
            try {
                Class<?> cl = Class.forName(this.packageInteraction +"."+ request.getMessage());
                var types = new Class[]{ Object[].class };
                Constructor<?> constructor = cl.getConstructor(types);
                @SuppressWarnings({"java:S1905", "java:S3878"})  // rules opposite by intellij and sonar
                InteractionCommand interactionCommand = (InteractionCommand) constructor.newInstance(
                    new Object[] {  request.getParameters() }
                );
                interactionCommand.execute();
            } catch (ClassNotFoundException e) {
                LOGGER.log(LogLevelUtil.IGNORE, e);  // Ignore unused interaction message
            } catch (
                InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | SecurityException
                | IllegalArgumentException
                | InvocationTargetException
                e
            ) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
        });
    }

    @Override
    public void onError(Throwable e) {
        LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
    }

    @Override
    public void onComplete() {
        // Nothing
    }
}
