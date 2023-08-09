package com.jsql.view.swing.panel.preferences;

import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Stream;

public class PanelUserAgent extends JPanel {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final JCheckBox checkboxIsCustomUserAgent = new JCheckBox();
    private final JTextArea textfieldCustomUserAgent = new JPopupTextArea(new JTextAreaPlaceholder("Set User Agent")).getProxy();
    
    public PanelUserAgent() {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        var jsonScan = new StringBuilder();
        
        try (
            var inputStream = UiUtil.class.getClassLoader().getResourceAsStream("swing/list/user-agent.txt");
            var inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream));
            var reader = new BufferedReader(inputStreamReader)
        ) {

            String line;
            while ((line = reader.readLine()) != null) {

                jsonScan.append(line).append(System.lineSeparator());
            }

        } catch (IOException e) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        
        this.textfieldCustomUserAgent.getCaret().setBlinkRate(500);
        this.textfieldCustomUserAgent.setText(jsonScan.toString());
        MediatorHelper.model().getMediatorUtils().getUserAgentUtil().setCustomUserAgent(jsonScan.toString());
        this.textfieldCustomUserAgent.getDocument().addDocumentListener(new DocumentListenerEditing() {

            @Override
            public void process() {
                
                MediatorHelper.model().getMediatorUtils().getUserAgentUtil().setCustomUserAgent(
                    PanelUserAgent.this.textfieldCustomUserAgent.getText()
                );
            }
        });
        
        this.checkboxIsCustomUserAgent.setFocusable(false);
        var labelIsCheckingUpdate = new JButton("Customize User Agent (randomize multiple agents)");
        
        labelIsCheckingUpdate.addActionListener(actionEvent -> {
            
            this.checkboxIsCustomUserAgent.setSelected(!this.checkboxIsCustomUserAgent.isSelected());
            
            MediatorHelper.model().getMediatorUtils().getUserAgentUtil().setIsCustomUserAgent(
                this.checkboxIsCustomUserAgent.isSelected()
            );
        });
        
        var textAreaIsTamperingEval = new LightScrollPane(this.textfieldCustomUserAgent);
        textAreaIsTamperingEval.setBorder(UiUtil.BORDER_FOCUS_LOST);
        this.textfieldCustomUserAgent.setMinimumSize(new Dimension(40000, 100));
        
        var emptyLabelSessionManagement = new JLabel();
        
        Stream
        .of(labelIsCheckingUpdate)
        .forEach(label -> {
            
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorderPainted(false);
            label.setContentAreaFilled(false);
        });
        
        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        
        groupLayout
        .setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(this.checkboxIsCustomUserAgent)
                .addComponent(emptyLabelSessionManagement)
            )
            .addGroup(
                groupLayout
                .createParallelGroup()
                .addComponent(labelIsCheckingUpdate)
                .addComponent(textAreaIsTamperingEval)
            )
        );

        groupLayout
        .setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsCustomUserAgent)
                .addComponent(labelIsCheckingUpdate)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(emptyLabelSessionManagement)
                .addComponent(textAreaIsTamperingEval)
            )
        );
    }
}
