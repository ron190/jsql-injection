package com.jsql.view.interaction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.util.LogLevel;

public class SubscriberInteraction implements Subscriber<Request> {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private String packageInteraction;
    
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
        
        subscription.request(1);
        
        if (Interaction.UNSUBSCRIBE.equals(request.getMessage())) {
            
            subscription.cancel();
            return;
        }
        
        // Display model thread name in logs instead of the observer name
        String nameThread = Thread.currentThread().getName();
        
        SwingUtilities.invokeLater(() -> {
            
            Thread.currentThread().setName("from " + nameThread);
            
            try {
                Class<?> cl = Class.forName(this.packageInteraction +"."+ request.getMessage());
                var types = new Class[]{ Object[].class };
                Constructor<?> ct = cl.getConstructor(types);
    
                InteractionCommand o2 = (InteractionCommand) ct.newInstance(
                    new Object[] {
                        request.getParameters()
                    }
                );
                o2.execute();
                
            } catch (ClassNotFoundException e) {
                
                // Ignore unused interaction message
                LOGGER.log(LogLevel.IGNORE, e);
                
            } catch (
                InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | SecurityException
                | IllegalArgumentException
                | InvocationTargetException
                e
            ) {
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            }
        });
    }

    @Override
    public void onError(Throwable e) {

        LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
    }

    @Override
    public void onComplete() {
        // Nothing
    }
}
