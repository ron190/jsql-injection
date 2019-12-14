package com.jsql.model;

/**
 * Mediator registering usefull components in a loose dependance way.
 */
public class MediatorModel {
    
    private static InjectionModel model;
    
    /**
     * Utility class.
     */
    private MediatorModel() {
        //not called
    }

    // Registering Model
    public static void register(InjectionModel model) {
        MediatorModel.model = model;
    }

    public static InjectionModel model() {
        return model;
    }
    
}
