package com.jsql.view.swing.panel.util;

import javax.swing.SizeRequirements;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.ParagraphView;

@SuppressWarnings("serial")
public class HTMLEditorKitTextPaneWrap extends HTMLEditorKit {

    @Override
    public ViewFactory getViewFactory() {

        return new HTMLFactory() {

            @Override
            public View create(Element e) {
                
                View v = super.create(e);
                
                if (v instanceof InlineView) {
                    
                    return this.getInlineView(e);
                    
                } else if (v instanceof ParagraphView) {
                    
                    return this.getParagraphView(e);
                }
                
                return v;
            }

            private View getParagraphView(Element e) {
                
                return new ParagraphView(e) {

                    @Override
                    protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements valueR) {
                        
                        SizeRequirements r = valueR;
                        
                        if (r == null) {
                            
                            r = new SizeRequirements();
                        }
                        
                        float pref = this.layoutPool.getPreferredSpan(axis);
                        float min = this.layoutPool.getMinimumSpan(axis);
                        
                        // Don't include insets, Box.getXXXSpan will include
                        // them.
                        r.minimum = (int) min;
                        r.preferred = Math.max(r.minimum, (int) pref);
                        r.maximum = Integer.MAX_VALUE;
                        r.alignment = 0.5f;
                        
                        return r;
                    }
                };
            }

            private View getInlineView(Element e) {
                
                return new InlineView(e) {

                    @Override
                    public int getBreakWeight(int axis, float pos, float len) {
                        
                        return GoodBreakWeight;
                    }

                    @Override
                    public View breakView(int axis, int p0, float pos, float len) {
                        
                        if (axis == View.X_AXIS) {
                            
                            this.checkPainter();
                            int p1 = this.getGlyphPainter().getBoundedPosition(this, p0, pos, len);
                            
                            if (p0 == this.getStartOffset() && p1 == this.getEndOffset()) {
                                
                                return this;
                            }
                            
                            return this.createFragment(p0, p1);
                        }
                        
                        return this;
                    }
                };
            }
        };
    }
}