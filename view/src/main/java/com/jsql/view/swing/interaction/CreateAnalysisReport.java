/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.interaction;

import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;

/**
 * Mark the injection as vulnerable to a blind injection.
 */
public class CreateAnalysisReport extends CreateTabHelper implements InteractionCommand {

    private final String content;

    public CreateAnalysisReport(Object[] interactionParams) {
        this.content = (String) interactionParams[0];
    }

    @Override
    public void execute() {
        SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addReportTab(this.content.trim()));
    }
}
