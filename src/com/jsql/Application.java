package com.jsql;

import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.view.GUI;
import com.jsql.view.MediatorGUI;

public class Application {
    /**
     * Application starting point.
     * @param args CLI parameters (not used)
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                InjectionModel model = new InjectionModel();
                MediatorModel.register(model);
                MediatorGUI.register(model);
                MediatorGUI.register(new GUI());
            }
        });
    }
}
