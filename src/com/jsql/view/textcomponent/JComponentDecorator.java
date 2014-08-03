/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.textcomponent;

import javax.swing.JComponent;

/**
 * Decorate a swing component.
 * @param <T> Component like JTextField or JTextArea to decorate
 */
interface JComponentDecorator<T extends JComponent> {

    /**
     * Get back the decorated component.
     * @return Proxyfied component
     */
    T getProxy();
}