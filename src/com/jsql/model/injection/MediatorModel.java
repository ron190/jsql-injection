package com.jsql.model.injection;

/**
 * Mediator registering usefull components in a loose dependance way.  
 */
public class MediatorModel {
    private static InjectionModel model;

    // Registering Model
    public static void register(InjectionModel model) {
        MediatorModel.model = model;
    }

    public static InjectionModel model() {
        return model;
    }
}
