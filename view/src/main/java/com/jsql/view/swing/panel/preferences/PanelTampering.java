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
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.stream.Stream;

public class PanelTampering extends JPanel {

    // no preferences init
    private final JCheckBox checkboxIsTamperingBase64 = new JCheckBox();
    private final JCheckBox checkboxIsTamperingVersionComment = new JCheckBox();
    private final JCheckBox checkboxIsTamperingFunctionComment = new JCheckBox();
    private final JCheckBox checkboxIsTamperingEqualToLike = new JCheckBox();
    private final JCheckBox checkboxIsTamperingRandomCase = new JCheckBox();
    private final JCheckBox checkboxIsTamperingEval = new JCheckBox();
    private final JCheckBox checkboxIsTamperingHexToChar = new JCheckBox();
    private final JCheckBox checkboxIsTamperingStringToChar = new JCheckBox();
    private final JCheckBox checkboxIsTamperingQuoteToUtf8 = new JCheckBox();
    private final JCheckBox checkboxIsTamperingCharToEncoding = new JCheckBox();
    private final JRadioButton radioIsTamperingSpaceToMultilineComment = new JRadioButton();
    private final JRadioButton radioIsTamperingSpaceToDashComment = new JRadioButton();
    private final JRadioButton radioIsTamperingSpaceToSharpComment = new JRadioButton();

    private static final RSyntaxTextArea TEXT_PANE_EVAL = new SyntaxTextArea("Click on a tamper to paste source and edit custom script");

    public PanelTampering(PanelPreferences panelPreferences) {
        this.checkboxIsTamperingEval.setToolTipText("Custom tamper in JavaScript and Java, e.g. sql.replace(/\\+/gm,'/**/')");

        PanelTampering.TEXT_PANE_EVAL.setText(StringUtils.EMPTY);
        PanelTampering.TEXT_PANE_EVAL.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        PanelTampering.TEXT_PANE_EVAL.setPopupMenu(new JPopupMenuComponent(PanelTampering.TEXT_PANE_EVAL));
        PanelTampering.applyTheme();

        var textAreaIsTamperingEval = new RTextScrollPane(PanelTampering.TEXT_PANE_EVAL, false);

        PanelTampering.TEXT_PANE_EVAL.getDocument().addDocumentListener(new DocumentListenerEditing() {
            @Override
            public void process() {
                MediatorHelper.model().getMediatorUtils().tamperingUtil().setCustomTamper(PanelTampering.TEXT_PANE_EVAL.getText());
            }
        });
        PanelTampering.TEXT_PANE_EVAL.setText(MediatorHelper.model().getMediatorUtils().tamperingUtil().getCustomTamper());
        this.checkboxIsTamperingEval.addActionListener(panelPreferences.getActionListenerSave());
        this.checkboxIsTamperingEval.setText("Enable user tamper script:");

        Stream.of(
            new SimpleEntry<>(this.checkboxIsTamperingBase64, TamperingType.BASE64),
            new SimpleEntry<>(this.checkboxIsTamperingFunctionComment, TamperingType.COMMENT_TO_METHOD_SIGNATURE),
            new SimpleEntry<>(this.checkboxIsTamperingVersionComment, TamperingType.VERSIONED_COMMENT_TO_METHOD_SIGNATURE),
            new SimpleEntry<>(this.checkboxIsTamperingEqualToLike, TamperingType.EQUAL_TO_LIKE),
            new SimpleEntry<>(this.checkboxIsTamperingRandomCase, TamperingType.RANDOM_CASE),
            new SimpleEntry<>(this.checkboxIsTamperingHexToChar, TamperingType.HEX_TO_CHAR),
            new SimpleEntry<>(this.checkboxIsTamperingStringToChar, TamperingType.STRING_TO_CHAR),
            new SimpleEntry<>(this.checkboxIsTamperingQuoteToUtf8, TamperingType.QUOTE_TO_UTF8),
            new SimpleEntry<>(this.checkboxIsTamperingCharToEncoding, TamperingType.CHAR_TO_ENCODING),
            new SimpleEntry<>(this.radioIsTamperingSpaceToMultilineComment, TamperingType.SPACE_TO_MULTILINE_COMMENT),
            new SimpleEntry<>(this.radioIsTamperingSpaceToDashComment, TamperingType.SPACE_TO_DASH_COMMENT),
            new SimpleEntry<>(this.radioIsTamperingSpaceToSharpComment, TamperingType.SPACE_TO_SHARP_COMMENT)
        )
        .forEach(entry -> {
            entry.getKey().setText(entry.getValue().instance().getDescription());
            entry.getKey().setToolTipText(entry.getValue().instance().getTooltip());
            entry.getKey().addMouseListener(new TamperingMouseAdapter(entry.getValue(), PanelTampering.TEXT_PANE_EVAL));
            entry.getKey().addActionListener(panelPreferences.getActionListenerSave());
        });

        var groupSpaceToComment = new ButtonGroup() {
            @Override
            public void setSelected(ButtonModel buttonModel, boolean isSelected) {
                if (isSelected) {  // Click a 2nd time to uncheck
                    super.setSelected(buttonModel, true);
                } else {
                    this.clearSelection();
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
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(labelCommonConversion)
                .addComponent(this.checkboxIsTamperingBase64)
                .addComponent(this.checkboxIsTamperingFunctionComment)
                .addComponent(this.checkboxIsTamperingVersionComment)
                .addComponent(this.checkboxIsTamperingEqualToLike)
                .addComponent(this.checkboxIsTamperingRandomCase)
                .addComponent(this.checkboxIsTamperingStringToChar)
                .addComponent(this.checkboxIsTamperingHexToChar)
                .addComponent(this.checkboxIsTamperingQuoteToUtf8)
                .addComponent(this.checkboxIsTamperingCharToEncoding)
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
                .addComponent(this.checkboxIsTamperingCharToEncoding)
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
        UiUtil.applySyntaxTheme(PanelTampering.TEXT_PANE_EVAL);
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

    public JCheckBox getCheckboxIsTamperingCharToEncoding() {
        return this.checkboxIsTamperingCharToEncoding;
    }

    public JCheckBox getCheckboxIsTamperingStringToChar() {
        return this.checkboxIsTamperingStringToChar;
    }
}
