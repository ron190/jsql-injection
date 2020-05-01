package com.jsql.view.swing.panel.preferences;

import java.awt.Dimension;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import com.jsql.util.tampering.TamperingType;
import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.sql.lexer.HighlightedDocument;
import com.jsql.view.swing.tab.TabHeader.Cleanable;
import com.jsql.view.swing.text.JPopupTextPane;
import com.jsql.view.swing.text.JTextPanePlaceholder;
import com.jsql.view.swing.text.listener.DocumentListenerTyping;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
public class PanelTamperingPreferences extends JPanel implements Cleanable {
    
    private final JCheckBox checkboxIsTamperingBase64 = new JCheckBox();
    private final JCheckBox checkboxIsTamperingVersionComment = new JCheckBox();
    private final JCheckBox checkboxIsTamperingFunctionComment = new JCheckBox();
    private final JCheckBox checkboxIsTamperingEqualToLike = new JCheckBox();
    private final JCheckBox checkboxIsTamperingRandomCase = new JCheckBox();
    private final JCheckBox checkboxIsTamperingEval = new JCheckBox();
    private final JCheckBox checkboxIsTamperingHexToChar = new JCheckBox();
    private final JCheckBox checkboxIsTamperingStringToChar = new JCheckBox();
    private final JCheckBox checkboxIsTamperingQuoteToUtf8 = new JCheckBox();
    private final JRadioButton radioIsTamperingSpaceToMultilineComment = new JRadioButton();
    private final JRadioButton radioIsTamperingSpaceToDashComment = new JRadioButton();
    private final JRadioButton radioIsTamperingSpaceToSharpComment = new JRadioButton();
    
    JTextPane textPaneEval;

    public PanelTamperingPreferences(PanelPreferences panelPreferences) {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        String tooltipIsTamperingBase64 = TamperingType.BASE64.instance().getTooltip();
        this.checkboxIsTamperingBase64.setToolTipText(tooltipIsTamperingBase64);
        this.checkboxIsTamperingBase64.setFocusable(false);
        JButton labelIsTamperingBase64 = new JButton(TamperingType.BASE64.instance().getDescription());
        labelIsTamperingBase64.setToolTipText(tooltipIsTamperingBase64);
        labelIsTamperingBase64.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingBase64.setSelected(!this.checkboxIsTamperingBase64.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingFunctionComment = TamperingType.COMMENT_TO_METHOD_SIGNATURE.instance().getTooltip();
        this.checkboxIsTamperingFunctionComment.setToolTipText(tooltipIsTamperingFunctionComment);
        this.checkboxIsTamperingFunctionComment.setFocusable(false);
        JButton labelIsTamperingFunctionComment = new JButton(TamperingType.COMMENT_TO_METHOD_SIGNATURE.instance().getDescription());
        labelIsTamperingFunctionComment.setToolTipText(tooltipIsTamperingFunctionComment);
        labelIsTamperingFunctionComment.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingFunctionComment.setSelected(!this.checkboxIsTamperingFunctionComment.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingEqualToLike = TamperingType.EQUAL_TO_LIKE.instance().getTooltip();
        this.checkboxIsTamperingEqualToLike.setToolTipText(tooltipIsTamperingEqualToLike);
        this.checkboxIsTamperingEqualToLike.setFocusable(false);
        JButton labelIsTamperingEqualToLike = new JButton(TamperingType.EQUAL_TO_LIKE.instance().getDescription());
        labelIsTamperingEqualToLike.setToolTipText(tooltipIsTamperingEqualToLike);
        labelIsTamperingEqualToLike.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingEqualToLike.setSelected(!this.checkboxIsTamperingEqualToLike.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingRandomCase = TamperingType.RANDOM_CASE.instance().getTooltip();
        this.checkboxIsTamperingRandomCase.setToolTipText(tooltipIsTamperingRandomCase);
        this.checkboxIsTamperingRandomCase.setFocusable(false);
        JButton labelIsTamperingRandomCase = new JButton(TamperingType.RANDOM_CASE.instance().getDescription());
        labelIsTamperingRandomCase.setToolTipText(tooltipIsTamperingRandomCase);
        labelIsTamperingRandomCase.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingRandomCase.setSelected(!this.checkboxIsTamperingRandomCase.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingEval = "Custom tamper in JavaScript, e.g sql.replace(/\\+/gm,'/**/')";
        this.checkboxIsTamperingEval.setToolTipText(tooltipIsTamperingEval);
        this.checkboxIsTamperingEval.setFocusable(false);
        
        textPaneEval = new JPopupTextPane(new JTextPanePlaceholder(tooltipIsTamperingEval)).getProxy();
        LightScrollPane textAreaIsTamperingEval = new LightScrollPane(textPaneEval);
        textAreaIsTamperingEval.setBorder(UiUtil.BORDER_FOCUS_LOST);
        textAreaIsTamperingEval.setMinimumSize(new Dimension(400, 100));
        
        ButtonGroup groupSpaceToComment = new ButtonGroup();
        groupSpaceToComment.add(this.radioIsTamperingSpaceToDashComment);
        groupSpaceToComment.add(this.radioIsTamperingSpaceToMultilineComment);
        groupSpaceToComment.add(this.radioIsTamperingSpaceToSharpComment);
        
        String tooltipIsTamperingSpaceToMultilineComment = TamperingType.SPACE_TO_MULTILINE_COMMENT.instance().getTooltip();
        this.radioIsTamperingSpaceToMultilineComment.setToolTipText(tooltipIsTamperingSpaceToMultilineComment);
        this.radioIsTamperingSpaceToMultilineComment.setFocusable(false);
        JButton labelIsTamperingSpaceToMultilineComment = new JButton(TamperingType.SPACE_TO_MULTILINE_COMMENT.instance().getDescription());
        labelIsTamperingSpaceToMultilineComment.setToolTipText(tooltipIsTamperingSpaceToMultilineComment);
        labelIsTamperingSpaceToMultilineComment.addActionListener(actionEvent -> {
            
            if (this.radioIsTamperingSpaceToMultilineComment.isSelected()) {
                groupSpaceToComment.clearSelection();
            } else {
                this.radioIsTamperingSpaceToMultilineComment.setSelected(!this.radioIsTamperingSpaceToMultilineComment.isSelected());
            }
            
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingSpaceToDashComment = TamperingType.SPACE_TO_DASH_COMMENT.instance().getTooltip();
        this.radioIsTamperingSpaceToDashComment.setToolTipText(tooltipIsTamperingSpaceToDashComment);
        this.radioIsTamperingSpaceToDashComment.setFocusable(false);
        JButton labelIsTamperingSpaceToDashComment = new JButton(TamperingType.SPACE_TO_DASH_COMMENT.instance().getDescription());
        labelIsTamperingSpaceToDashComment.setToolTipText(tooltipIsTamperingSpaceToDashComment);
        labelIsTamperingSpaceToDashComment.addActionListener(actionEvent -> {
            
            if (this.radioIsTamperingSpaceToDashComment.isSelected()) {
                groupSpaceToComment.clearSelection();
            } else {
                this.radioIsTamperingSpaceToDashComment.setSelected(!this.radioIsTamperingSpaceToDashComment.isSelected());
            }
            
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingSpaceToSharpComment = TamperingType.SPACE_TO_SHARP_COMMENT.instance().getTooltip();
        this.radioIsTamperingSpaceToSharpComment.setToolTipText(tooltipIsTamperingSpaceToSharpComment);
        this.radioIsTamperingSpaceToSharpComment.setFocusable(false);
        JButton labelIsTamperingSpaceToSharpComment = new JButton(TamperingType.SPACE_TO_SHARP_COMMENT.instance().getDescription());
        labelIsTamperingSpaceToSharpComment.setToolTipText(tooltipIsTamperingSpaceToSharpComment);
        labelIsTamperingSpaceToSharpComment.addActionListener(actionEvent -> {
            
            if (this.radioIsTamperingSpaceToSharpComment.isSelected()) {
                groupSpaceToComment.clearSelection();
            } else {
                this.radioIsTamperingSpaceToSharpComment.setSelected(!this.radioIsTamperingSpaceToSharpComment.isSelected());
            }
            
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingVersionComment = TamperingType.VERSIONED_COMMENT_TO_METHOD_SIGNATURE.instance().getTooltip();
        this.checkboxIsTamperingVersionComment.setToolTipText(tooltipIsTamperingVersionComment);
        this.checkboxIsTamperingVersionComment.setFocusable(false);
        JButton labelIsTamperingVersionComment = new JButton(TamperingType.VERSIONED_COMMENT_TO_METHOD_SIGNATURE.instance().getDescription());
        labelIsTamperingVersionComment.setToolTipText(tooltipIsTamperingVersionComment);
        labelIsTamperingVersionComment.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingVersionComment.setSelected(!this.checkboxIsTamperingVersionComment.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingHexToChar = TamperingType.HEX_TO_CHAR.instance().getTooltip();
        this.checkboxIsTamperingHexToChar.setToolTipText(tooltipIsTamperingHexToChar);
        this.checkboxIsTamperingHexToChar.setFocusable(false);
        JButton labelIsTamperingHexToChar = new JButton(TamperingType.HEX_TO_CHAR.instance().getDescription());
        labelIsTamperingHexToChar.setToolTipText(tooltipIsTamperingHexToChar);
        labelIsTamperingHexToChar.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingHexToChar.setSelected(!this.checkboxIsTamperingHexToChar.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingQuoteToUtf8 = TamperingType.QUOTE_TO_UTF8.instance().getTooltip();
        this.checkboxIsTamperingQuoteToUtf8.setToolTipText(tooltipIsTamperingQuoteToUtf8);
        this.checkboxIsTamperingQuoteToUtf8.setFocusable(false);
        JButton labelIsTamperingQuoteToUtf8 = new JButton(TamperingType.QUOTE_TO_UTF8.instance().getDescription());
        labelIsTamperingQuoteToUtf8.setToolTipText(tooltipIsTamperingQuoteToUtf8);
        labelIsTamperingQuoteToUtf8.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingQuoteToUtf8.setSelected(!this.checkboxIsTamperingQuoteToUtf8.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingStringToChar = TamperingType.STRING_TO_CHAR.instance().getTooltip();
        this.checkboxIsTamperingStringToChar.setToolTipText(tooltipIsTamperingStringToChar);
        this.checkboxIsTamperingStringToChar.setFocusable(false);
        JButton labelIsTamperingStringToChar = new JButton(TamperingType.STRING_TO_CHAR.instance().getDescription());
        labelIsTamperingStringToChar.setToolTipText(tooltipIsTamperingStringToChar);
        labelIsTamperingStringToChar.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingStringToChar.setSelected(!this.checkboxIsTamperingStringToChar.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        Stream.of(
            new SimpleEntry<>(labelIsTamperingBase64, TamperingType.BASE64),
            new SimpleEntry<>(labelIsTamperingFunctionComment, TamperingType.COMMENT_TO_METHOD_SIGNATURE),
            new SimpleEntry<>(labelIsTamperingVersionComment, TamperingType.VERSIONED_COMMENT_TO_METHOD_SIGNATURE),
            new SimpleEntry<>(labelIsTamperingEqualToLike, TamperingType.EQUAL_TO_LIKE),
            new SimpleEntry<>(labelIsTamperingRandomCase, TamperingType.RANDOM_CASE),
            new SimpleEntry<>(labelIsTamperingHexToChar, TamperingType.HEX_TO_CHAR),
            new SimpleEntry<>(labelIsTamperingStringToChar, TamperingType.STRING_TO_CHAR),
            new SimpleEntry<>(labelIsTamperingQuoteToUtf8, TamperingType.QUOTE_TO_UTF8),
            new SimpleEntry<>(labelIsTamperingSpaceToMultilineComment, TamperingType.SPACE_TO_MULTILINE_COMMENT),
            new SimpleEntry<>(labelIsTamperingSpaceToDashComment, TamperingType.SPACE_TO_DASH_COMMENT),
            new SimpleEntry<>(labelIsTamperingSpaceToSharpComment, TamperingType.SPACE_TO_SHARP_COMMENT)
        )
        .forEach(entry -> {
            
            entry.getKey().addMouseListener(new TamperingMouseAdapter(entry.getValue(), textPaneEval));
            
            entry.getKey().setHorizontalAlignment(SwingConstants.LEFT);
            entry.getKey().setBorderPainted(false);
            entry.getKey().setContentAreaFilled(false);
        });
        
        HighlightedDocument document = new HighlightedDocument(HighlightedDocument.JAVASCRIPT_STYLE);
        document.setHighlightStyle(HighlightedDocument.JAVASCRIPT_STYLE);
        textPaneEval.setStyledDocument(document);
        
        document.addDocumentListener(new DocumentListenerTyping() {
            
            @Override
            public void process() {
                
                MediatorHelper.model().getMediatorUtils().getTamperingUtil().setCustomTamper(textPaneEval.getText());
            }
        });
        
        textPaneEval.setText(MediatorHelper.model().getMediatorUtils().getTamperingUtil().getCustomTamper());
        
        GroupLayout groupLayoutTampering = new GroupLayout(this);
        this.setLayout(groupLayoutTampering);
        
        groupLayoutTampering
        .setHorizontalGroup(
            groupLayoutTampering
            .createSequentialGroup()
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(this.checkboxIsTamperingBase64)
                .addComponent(this.checkboxIsTamperingFunctionComment)
                .addComponent(this.checkboxIsTamperingVersionComment)
                .addComponent(this.checkboxIsTamperingEqualToLike)
                .addComponent(this.checkboxIsTamperingRandomCase)
                .addComponent(this.checkboxIsTamperingStringToChar)
                .addComponent(this.checkboxIsTamperingHexToChar)
                .addComponent(this.checkboxIsTamperingQuoteToUtf8)
                .addComponent(this.radioIsTamperingSpaceToMultilineComment)
                .addComponent(this.radioIsTamperingSpaceToDashComment)
                .addComponent(this.radioIsTamperingSpaceToSharpComment)
                .addComponent(this.checkboxIsTamperingEval)
            )
            .addGroup(
                groupLayoutTampering
                .createParallelGroup()
                .addComponent(labelIsTamperingBase64)
                .addComponent(labelIsTamperingFunctionComment)
                .addComponent(labelIsTamperingVersionComment)
                .addComponent(labelIsTamperingEqualToLike)
                .addComponent(labelIsTamperingRandomCase)
                .addComponent(labelIsTamperingStringToChar)
                .addComponent(labelIsTamperingHexToChar)
                .addComponent(labelIsTamperingQuoteToUtf8)
                .addComponent(labelIsTamperingSpaceToMultilineComment)
                .addComponent(labelIsTamperingSpaceToDashComment)
                .addComponent(labelIsTamperingSpaceToSharpComment)
                .addComponent(textAreaIsTamperingEval)
            )
        );
        
        groupLayoutTampering
        .setVerticalGroup(
            groupLayoutTampering
            .createSequentialGroup()
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingBase64)
                .addComponent(labelIsTamperingBase64)
            )
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingFunctionComment)
                .addComponent(labelIsTamperingFunctionComment)
            )
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingVersionComment)
                .addComponent(labelIsTamperingVersionComment)
            )
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingEqualToLike)
                .addComponent(labelIsTamperingEqualToLike)
            )
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingRandomCase)
                .addComponent(labelIsTamperingRandomCase)
            )
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingStringToChar)
                .addComponent(labelIsTamperingStringToChar)
            )
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingHexToChar)
                .addComponent(labelIsTamperingHexToChar)
            )
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingQuoteToUtf8)
                .addComponent(labelIsTamperingQuoteToUtf8)
            )
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioIsTamperingSpaceToMultilineComment)
                .addComponent(labelIsTamperingSpaceToMultilineComment)
            )
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioIsTamperingSpaceToDashComment)
                .addComponent(labelIsTamperingSpaceToDashComment)
            )
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioIsTamperingSpaceToSharpComment)
                .addComponent(labelIsTamperingSpaceToSharpComment)
            )
            .addGroup(
                groupLayoutTampering
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingEval)
                .addComponent(textAreaIsTamperingEval)
            )
        );
        
        Stream
        .of(
            this.checkboxIsTamperingEval,
            this.checkboxIsTamperingBase64,
            this.checkboxIsTamperingFunctionComment,
            this.checkboxIsTamperingVersionComment,
            this.checkboxIsTamperingEqualToLike,
            this.checkboxIsTamperingRandomCase,
            this.checkboxIsTamperingHexToChar,
            this.checkboxIsTamperingStringToChar,
            this.checkboxIsTamperingQuoteToUtf8,
            this.radioIsTamperingSpaceToMultilineComment,
            this.radioIsTamperingSpaceToDashComment,
            this.radioIsTamperingSpaceToSharpComment
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
    }

    @Override
    public void clean() {
        
        UiUtil.stopDocumentColorer(textPaneEval);
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
