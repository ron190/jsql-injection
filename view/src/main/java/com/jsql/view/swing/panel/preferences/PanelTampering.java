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
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
public class PanelTampering extends JPanel implements Cleanable {
    
    private JCheckBox checkboxIsTamperingBase64 = new JCheckBox();
    private JCheckBox checkboxIsTamperingVersionComment = new JCheckBox();
    private JCheckBox checkboxIsTamperingFunctionComment = new JCheckBox();
    private JCheckBox checkboxIsTamperingEqualToLike = new JCheckBox();
    private JCheckBox checkboxIsTamperingRandomCase = new JCheckBox();
    private JCheckBox checkboxIsTamperingEval = new JCheckBox();
    private JCheckBox checkboxIsTamperingHexToChar = new JCheckBox();
    private JCheckBox checkboxIsTamperingStringToChar = new JCheckBox();
    private JCheckBox checkboxIsTamperingQuoteToUtf8 = new JCheckBox();
    private JRadioButton radioIsTamperingSpaceToMultilineComment = new JRadioButton();
    private JRadioButton radioIsTamperingSpaceToDashComment = new JRadioButton();
    private JRadioButton radioIsTamperingSpaceToSharpComment = new JRadioButton();
    
    private JTextPane textPaneEval;

    public PanelTampering(PanelPreferences panelPreferences) {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        String tooltipIsTamperingBase64 = TamperingType.BASE64.instance().getTooltip();
        this.checkboxIsTamperingBase64.setToolTipText(tooltipIsTamperingBase64);
        this.checkboxIsTamperingBase64.setFocusable(false);
        var labelIsTamperingBase64 = new JButton(TamperingType.BASE64.instance().getDescription());
        labelIsTamperingBase64.setToolTipText(tooltipIsTamperingBase64);
        labelIsTamperingBase64.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingBase64.setSelected(!this.checkboxIsTamperingBase64.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingFunctionComment = TamperingType.COMMENT_TO_METHOD_SIGNATURE.instance().getTooltip();
        this.checkboxIsTamperingFunctionComment.setToolTipText(tooltipIsTamperingFunctionComment);
        this.checkboxIsTamperingFunctionComment.setFocusable(false);
        var labelIsTamperingFunctionComment = new JButton(TamperingType.COMMENT_TO_METHOD_SIGNATURE.instance().getDescription());
        labelIsTamperingFunctionComment.setToolTipText(tooltipIsTamperingFunctionComment);
        labelIsTamperingFunctionComment.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingFunctionComment.setSelected(!this.checkboxIsTamperingFunctionComment.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingEqualToLike = TamperingType.EQUAL_TO_LIKE.instance().getTooltip();
        this.checkboxIsTamperingEqualToLike.setToolTipText(tooltipIsTamperingEqualToLike);
        this.checkboxIsTamperingEqualToLike.setFocusable(false);
        var labelIsTamperingEqualToLike = new JButton(TamperingType.EQUAL_TO_LIKE.instance().getDescription());
        labelIsTamperingEqualToLike.setToolTipText(tooltipIsTamperingEqualToLike);
        labelIsTamperingEqualToLike.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingEqualToLike.setSelected(!this.checkboxIsTamperingEqualToLike.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingRandomCase = TamperingType.RANDOM_CASE.instance().getTooltip();
        this.checkboxIsTamperingRandomCase.setToolTipText(tooltipIsTamperingRandomCase);
        this.checkboxIsTamperingRandomCase.setFocusable(false);
        var labelIsTamperingRandomCase = new JButton(TamperingType.RANDOM_CASE.instance().getDescription());
        labelIsTamperingRandomCase.setToolTipText(tooltipIsTamperingRandomCase);
        labelIsTamperingRandomCase.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingRandomCase.setSelected(!this.checkboxIsTamperingRandomCase.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var tooltipIsTamperingEval = "Custom tamper in JavaScript, e.g sql.replace(/\\+/gm,'/**/')";
        this.checkboxIsTamperingEval.setToolTipText(tooltipIsTamperingEval);
        this.checkboxIsTamperingEval.setFocusable(false);
        
        this.textPaneEval = new JPopupTextPane(new JTextPanePlaceholder(tooltipIsTamperingEval)).getProxy();
        var textAreaIsTamperingEval = new LightScrollPane(this.textPaneEval);
        textAreaIsTamperingEval.setBorder(UiUtil.BORDER_FOCUS_LOST);
        textAreaIsTamperingEval.setMinimumSize(new Dimension(400, 100));
        
        var groupSpaceToComment = new ButtonGroup();
        groupSpaceToComment.add(this.radioIsTamperingSpaceToDashComment);
        groupSpaceToComment.add(this.radioIsTamperingSpaceToMultilineComment);
        groupSpaceToComment.add(this.radioIsTamperingSpaceToSharpComment);
        
        String tooltipIsTamperingSpaceToMultilineComment = TamperingType.SPACE_TO_MULTILINE_COMMENT.instance().getTooltip();
        this.radioIsTamperingSpaceToMultilineComment.setToolTipText(tooltipIsTamperingSpaceToMultilineComment);
        this.radioIsTamperingSpaceToMultilineComment.setFocusable(false);
        var labelIsTamperingSpaceToMultilineComment = new JButton(TamperingType.SPACE_TO_MULTILINE_COMMENT.instance().getDescription());
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
        var labelIsTamperingSpaceToDashComment = new JButton(TamperingType.SPACE_TO_DASH_COMMENT.instance().getDescription());
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
        var labelIsTamperingSpaceToSharpComment = new JButton(TamperingType.SPACE_TO_SHARP_COMMENT.instance().getDescription());
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
        var labelIsTamperingVersionComment = new JButton(TamperingType.VERSIONED_COMMENT_TO_METHOD_SIGNATURE.instance().getDescription());
        labelIsTamperingVersionComment.setToolTipText(tooltipIsTamperingVersionComment);
        labelIsTamperingVersionComment.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingVersionComment.setSelected(!this.checkboxIsTamperingVersionComment.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        String tooltipIsTamperingHexToChar = TamperingType.HEX_TO_CHAR.instance().getTooltip();
        this.checkboxIsTamperingHexToChar.setToolTipText(tooltipIsTamperingHexToChar);
        this.checkboxIsTamperingHexToChar.setFocusable(false);
        var labelIsTamperingHexToChar = new JButton(TamperingType.HEX_TO_CHAR.instance().getDescription());
        labelIsTamperingHexToChar.setToolTipText(tooltipIsTamperingHexToChar);
        labelIsTamperingHexToChar.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingHexToChar.setSelected(!this.checkboxIsTamperingHexToChar.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var tooltipIsTamperingQuoteToUtf8 = TamperingType.QUOTE_TO_UTF8.instance().getTooltip();
        this.checkboxIsTamperingQuoteToUtf8.setToolTipText(tooltipIsTamperingQuoteToUtf8);
        this.checkboxIsTamperingQuoteToUtf8.setFocusable(false);
        var labelIsTamperingQuoteToUtf8 = new JButton(TamperingType.QUOTE_TO_UTF8.instance().getDescription());
        labelIsTamperingQuoteToUtf8.setToolTipText(tooltipIsTamperingQuoteToUtf8);
        labelIsTamperingQuoteToUtf8.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingQuoteToUtf8.setSelected(!this.checkboxIsTamperingQuoteToUtf8.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        var tooltipIsTamperingStringToChar = TamperingType.STRING_TO_CHAR.instance().getTooltip();
        this.checkboxIsTamperingStringToChar.setToolTipText(tooltipIsTamperingStringToChar);
        this.checkboxIsTamperingStringToChar.setFocusable(false);
        var labelIsTamperingStringToChar = new JButton(TamperingType.STRING_TO_CHAR.instance().getDescription());
        labelIsTamperingStringToChar.setToolTipText(tooltipIsTamperingStringToChar);
        labelIsTamperingStringToChar.addActionListener(actionEvent -> {
            
            this.checkboxIsTamperingStringToChar.setSelected(!this.checkboxIsTamperingStringToChar.isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        Stream
        .of(
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
            
            entry.getKey().addMouseListener(new TamperingMouseAdapter(entry.getValue(), this.textPaneEval));
            
            entry.getKey().setHorizontalAlignment(SwingConstants.LEFT);
            entry.getKey().setBorderPainted(false);
            entry.getKey().setContentAreaFilled(false);
        });
        
        var document = new HighlightedDocument(HighlightedDocument.JAVASCRIPT_STYLE);
        document.setHighlightStyle(HighlightedDocument.JAVASCRIPT_STYLE);
        this.textPaneEval.setStyledDocument(document);
        
        document.addDocumentListener(new DocumentListenerEditing() {
            
            @Override
            public void process() {
                
                MediatorHelper.model().getMediatorUtils().getTamperingUtil().setCustomTamper(PanelTampering.this.textPaneEval.getText());
            }
        });
        
        this.textPaneEval.setText(MediatorHelper.model().getMediatorUtils().getTamperingUtil().getCustomTamper());
        
        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        
        groupLayout
        .setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
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
                groupLayout
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
        
        groupLayout
        .setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingBase64)
                .addComponent(labelIsTamperingBase64)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingFunctionComment)
                .addComponent(labelIsTamperingFunctionComment)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingVersionComment)
                .addComponent(labelIsTamperingVersionComment)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingEqualToLike)
                .addComponent(labelIsTamperingEqualToLike)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingRandomCase)
                .addComponent(labelIsTamperingRandomCase)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingStringToChar)
                .addComponent(labelIsTamperingStringToChar)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingHexToChar)
                .addComponent(labelIsTamperingHexToChar)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsTamperingQuoteToUtf8)
                .addComponent(labelIsTamperingQuoteToUtf8)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioIsTamperingSpaceToMultilineComment)
                .addComponent(labelIsTamperingSpaceToMultilineComment)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioIsTamperingSpaceToDashComment)
                .addComponent(labelIsTamperingSpaceToDashComment)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioIsTamperingSpaceToSharpComment)
                .addComponent(labelIsTamperingSpaceToSharpComment)
            )
            .addGroup(
                groupLayout
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
        
        UiUtil.stopDocumentColorer(this.textPaneEval);
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
