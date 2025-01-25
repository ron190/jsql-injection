package com.jsql.view.swing.panel.preferences;

import com.jsql.util.tampering.TamperingType;
import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.panel.preferences.listener.TamperingMouseAdapter;
import com.jsql.view.swing.popupmenu.JPopupMenuComponent;
import com.jsql.view.swing.text.SyntaxTextArea;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.stream.Stream;

public class PanelTampering extends JPanel {

    // no preferences init
    private final JCheckBox checkboxIsTamperingBase64 = new JCheckBox(TamperingType.BASE64.instance().getDescription());
    private final JCheckBox checkboxIsTamperingVersionComment = new JCheckBox(TamperingType.VERSIONED_COMMENT_TO_METHOD_SIGNATURE.instance().getDescription());
    private final JCheckBox checkboxIsTamperingFunctionComment = new JCheckBox(TamperingType.COMMENT_TO_METHOD_SIGNATURE.instance().getDescription());
    private final JCheckBox checkboxIsTamperingEqualToLike = new JCheckBox(TamperingType.EQUAL_TO_LIKE.instance().getDescription());
    private final JCheckBox checkboxIsTamperingRandomCase = new JCheckBox(TamperingType.RANDOM_CASE.instance().getDescription());
    private final JCheckBox checkboxIsTamperingEval = new JCheckBox("Enable user tamper script :");
    private final JCheckBox checkboxIsTamperingHexToChar = new JCheckBox(TamperingType.HEX_TO_CHAR.instance().getDescription());
    private final JCheckBox checkboxIsTamperingStringToChar = new JCheckBox(TamperingType.STRING_TO_CHAR.instance().getDescription());
    private final JCheckBox checkboxIsTamperingQuoteToUtf8 = new JCheckBox(TamperingType.QUOTE_TO_UTF8.instance().getDescription());
    private final JRadioButton radioIsTamperingSpaceToMultilineComment = new JRadioButton(TamperingType.SPACE_TO_MULTILINE_COMMENT.instance().getDescription());
    private final JRadioButton radioIsTamperingSpaceToDashComment = new JRadioButton(TamperingType.SPACE_TO_DASH_COMMENT.instance().getDescription());
    private final JRadioButton radioIsTamperingSpaceToSharpComment = new JRadioButton(TamperingType.SPACE_TO_SHARP_COMMENT.instance().getDescription());

    private static final RSyntaxTextArea textPaneEval = new SyntaxTextArea("Click on a tamper to paste source and edit custom script");

    public PanelTampering(PanelPreferences panelPreferences) {
        this.checkboxIsTamperingEval.setToolTipText("Custom tamper in JavaScript and Java, e.g. sql.replace(/\\+/gm,'/**/')");

        PanelTampering.textPaneEval.setText(StringUtils.EMPTY);
        PanelTampering.textPaneEval.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        PanelTampering.textPaneEval.setPopupMenu(new JPopupMenuComponent(PanelTampering.textPaneEval));
        PanelTampering.applyTheme();

        var textAreaIsTamperingEval = new RTextScrollPane(PanelTampering.textPaneEval, false);
        textAreaIsTamperingEval.setMinimumSize(new Dimension(800, 100));

        PanelTampering.textPaneEval.getDocument().addDocumentListener(new DocumentListenerEditing() {
            @Override
            public void process() {
                MediatorHelper.model().getMediatorUtils().getTamperingUtil().setCustomTamper(PanelTampering.textPaneEval.getText());
            }
        });
        PanelTampering.textPaneEval.setText(MediatorHelper.model().getMediatorUtils().getTamperingUtil().getCustomTamper());
        this.checkboxIsTamperingEval.addActionListener(panelPreferences.getActionListenerSave());

        Stream.of(
            new SimpleEntry<>(this.checkboxIsTamperingBase64, TamperingType.BASE64),
            new SimpleEntry<>(this.checkboxIsTamperingFunctionComment, TamperingType.COMMENT_TO_METHOD_SIGNATURE),
            new SimpleEntry<>(this.checkboxIsTamperingVersionComment, TamperingType.VERSIONED_COMMENT_TO_METHOD_SIGNATURE),
            new SimpleEntry<>(this.checkboxIsTamperingEqualToLike, TamperingType.EQUAL_TO_LIKE),
            new SimpleEntry<>(this.checkboxIsTamperingRandomCase, TamperingType.RANDOM_CASE),
            new SimpleEntry<>(this.checkboxIsTamperingHexToChar, TamperingType.HEX_TO_CHAR),
            new SimpleEntry<>(this.checkboxIsTamperingStringToChar, TamperingType.STRING_TO_CHAR),
            new SimpleEntry<>(this.checkboxIsTamperingQuoteToUtf8, TamperingType.QUOTE_TO_UTF8),
            new SimpleEntry<>(this.radioIsTamperingSpaceToMultilineComment, TamperingType.SPACE_TO_MULTILINE_COMMENT),
            new SimpleEntry<>(this.radioIsTamperingSpaceToDashComment, TamperingType.SPACE_TO_DASH_COMMENT),
            new SimpleEntry<>(this.radioIsTamperingSpaceToSharpComment, TamperingType.SPACE_TO_SHARP_COMMENT)
        )
        .forEach(entry -> {
            entry.getKey().setToolTipText(entry.getValue().instance().getTooltip());
            entry.getKey().addMouseListener(new TamperingMouseAdapter(entry.getValue(), PanelTampering.textPaneEval));
            entry.getKey().addActionListener(panelPreferences.getActionListenerSave());
        });

        var groupSpaceToComment = new ButtonGroup() {
            @Override
            public void setSelected(ButtonModel buttonModel, boolean b) {
                // Click a 2nd time to uncheck
                if (!b) {
                    this.clearSelection();
                } else {
                    super.setSelected(buttonModel, true);
                }
            }
        };
        groupSpaceToComment.add(this.radioIsTamperingSpaceToDashComment);
        groupSpaceToComment.add(this.radioIsTamperingSpaceToMultilineComment);
        groupSpaceToComment.add(this.radioIsTamperingSpaceToSharpComment);

        var labelCommonConversion = new JLabel("<html><b>Common tamper</b></html>");
        var labelSpaceTamper = new JLabel("<html><br /><b>Space tamper (click again to uncheck)</b></html>");
        var labelCustomConversion = new JLabel("<html><br /><b>Custom tamper (hover tamper to show implementation, click to paste)</b></html>");
        Arrays.asList(labelCommonConversion, labelSpaceTamper, labelCustomConversion).forEach(label -> label.setBorder(PanelGeneral.MARGIN));

        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        
        groupLayout.setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(labelCommonConversion)
                .addComponent(this.checkboxIsTamperingBase64)
                .addComponent(this.checkboxIsTamperingFunctionComment)
                .addComponent(this.checkboxIsTamperingVersionComment)
                .addComponent(this.checkboxIsTamperingEqualToLike)
                .addComponent(this.checkboxIsTamperingRandomCase)
                .addComponent(this.checkboxIsTamperingStringToChar)
                .addComponent(this.checkboxIsTamperingHexToChar)
                .addComponent(this.checkboxIsTamperingQuoteToUtf8)
                .addComponent(labelSpaceTamper)
                .addComponent(this.radioIsTamperingSpaceToMultilineComment)
                .addComponent(this.radioIsTamperingSpaceToDashComment)
                .addComponent(this.radioIsTamperingSpaceToSharpComment)
                .addComponent(labelCustomConversion)
                .addComponent(this.checkboxIsTamperingEval)
                .addComponent(textAreaIsTamperingEval)
            )
        );
        
        groupLayout.setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelCommonConversion)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingBase64)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingFunctionComment)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingVersionComment)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingEqualToLike)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingRandomCase)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingStringToChar)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingHexToChar)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingQuoteToUtf8)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelSpaceTamper)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioIsTamperingSpaceToMultilineComment)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioIsTamperingSpaceToDashComment)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioIsTamperingSpaceToSharpComment)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelCustomConversion)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingEval)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(textAreaIsTamperingEval)
            )
        );
    }

    public static void applyTheme() {
        UiUtil.applySyntaxTheme(PanelTampering.textPaneEval);
    }
    
    
    // Getter and setter

    public JCheckBox getCheckboxIsTamperingBase64() {
        return this.checkboxIsTamperingBase64;
    }
    
    public JCheckBox getCheckboxIsTamperingEqualToLike() {
        return this.checkboxIsTamperingEqualToLike;
    }
    
    public JCheckBox getCheckboxIsTamperingFunctionComment() {
        return this.checkboxIsTamperingFunctionComment;
    }
    
    public JCheckBox getCheckboxIsTamperingVersionComment() {
        return this.checkboxIsTamperingVersionComment;
    }
    
    public JCheckBox getCheckboxIsTamperingRandomCase() {
        return this.checkboxIsTamperingRandomCase;
    }
    
    public JCheckBox getCheckboxIsTamperingEval() {
        return this.checkboxIsTamperingEval;
    }
    
    public JRadioButton getRadioIsTamperingSpaceToDashComment() {
        return this.radioIsTamperingSpaceToDashComment;
    }
    
    public JRadioButton getRadioIsTamperingSpaceToMultilineComment() {
        return this.radioIsTamperingSpaceToMultilineComment;
    }
    
    public JRadioButton getRadioIsTamperingSpaceToSharpComment() {
        return this.radioIsTamperingSpaceToSharpComment;
    }
  
    public JCheckBox getCheckboxIsTamperingHexToChar() {
        return this.checkboxIsTamperingHexToChar;
    }
    
    public JCheckBox getCheckboxIsTamperingQuoteToUtf8() {
        return this.checkboxIsTamperingQuoteToUtf8;
    }

    public JCheckBox getCheckboxIsTamperingStringToChar() {
        return this.checkboxIsTamperingStringToChar;
    }
}
