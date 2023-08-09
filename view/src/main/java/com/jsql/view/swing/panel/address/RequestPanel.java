package com.jsql.view.swing.panel.address;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.util.RadioMenuItemIconCustom;
import com.jsql.view.swing.text.JPopupTextField;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

public class RequestPanel extends JPanel {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private String typeRequest = "GET";

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
        
        for (String protocol: new String[]{"DELETE", "GET", "HEAD", "OPTIONS", "POST", "PUT", "TRACE"}) {
            
            final JMenuItem newMenuItem = new JRadioButtonMenuItem(protocol, "GET".equals(protocol));
            
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
                
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Define label of Custom request method");
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
                    ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))
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
