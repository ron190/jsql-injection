package com.jsql.view.swing.panel.address;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.util.RadioMenuItemIconCustom;
import com.jsql.view.swing.text.JPopupTextField;

@SuppressWarnings("serial")
public class RequestPanel extends JPanel {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private String typeRequest = "POST";

    public RequestPanel(PanelAddressBar panelAddressBar) {
        
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        this.setBorder(null);
        
        JButton buttonRequestMethod = new BasicArrowButton(SwingConstants.SOUTH);
        buttonRequestMethod.setBorderPainted(false);
        buttonRequestMethod.setOpaque(false);
        
        this.add(buttonRequestMethod);
        this.add(panelAddressBar.getRadioRequest());
        
        final var popup = new JPopupMenu();
        final var buttonGroup = new ButtonGroup();
        
        for (String protocol: new String[]{"OPTIONS", "HEAD", "POST", "PUT", "DELETE", "TRACE"}) {
            
            final JMenuItem newMenuItem = new JRadioButtonMenuItem(protocol, "POST".equals(protocol));
            
            newMenuItem.addActionListener(actionEvent -> {
                
                this.typeRequest = (newMenuItem.getText());
                panelAddressBar.getRadioRequest().setText(this.typeRequest);
            });
            
            popup.add(newMenuItem);
            buttonGroup.add(newMenuItem);
        }
        
        for (AbstractButton radioButton: Collections.list(buttonGroup.getElements())) {
            
            radioButton.setUI(
                new BasicRadioButtonMenuItemUI() {
                    
                    @Override
                    protected void doClick(MenuSelectionManager msm) {
                        
                        this.menuItem.doClick(0);
                    }
                }
            );
        }
        
        var panelCustomMethod = new JPanel(new BorderLayout());
        final JTextField inputCustomMethod = new JPopupTextField("CUSTOM").getProxy();

        final var radioCustomMethod = new JRadioButton();
        radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        radioCustomMethod.setIcon(new RadioMenuItemIconCustom());
        
        buttonGroup.add(radioCustomMethod);
        
        radioCustomMethod.addActionListener(actionEvent -> {
            
            if (StringUtils.isNotEmpty(inputCustomMethod.getText())) {
                
                this.typeRequest = (inputCustomMethod.getText());
                panelAddressBar.getRadioRequest().setText(this.typeRequest);
                
            } else {
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, "Define label of Custom request method");
            }
        });
      
        panelCustomMethod.add(radioCustomMethod, BorderLayout.LINE_START);
        panelCustomMethod.add(inputCustomMethod, BorderLayout.CENTER);
        popup.insert(panelCustomMethod, popup.getComponentCount());
        
        buttonRequestMethod.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                
                popup.applyComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()));
                
                if (ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))) {
                    
                    radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
                    
                } else {
                    
                    radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
                }
                
                popup.show(
                    e.getComponent(),
                    ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))
                    ? e.getComponent().getX() - e.getComponent().getWidth() - popup.getWidth()
                    : e.getComponent().getX(),
                    e.getComponent().getY() + e.getComponent().getWidth()
                );
                
                popup.setLocation(
                    ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
                    ? e.getComponent().getLocationOnScreen().x + e.getComponent().getWidth() - popup.getWidth()
                    : e.getComponent().getLocationOnScreen().x,
                    e.getComponent().getLocationOnScreen().y + e.getComponent().getWidth()
                );
            }
        });
    }

    public String getTypeRequest() {
        return this.typeRequest;
    }

    public void setTypeRequest(String typeRequest) {
        this.typeRequest = typeRequest;
    }
}
