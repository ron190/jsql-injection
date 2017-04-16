package com.jsql.view.swing.tab.dnd;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.action.ActionCloseTabResult;
import com.jsql.view.swing.ui.CustomMetalTabbedPaneUI;

@SuppressWarnings("serial")
public class TabbedPaneDnD extends JTabbedPane {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    private static final int LINEWIDTH = 3;
    
    private static final Rectangle RBACKWARD = new Rectangle();
    
    private static final Rectangle RFORWARD  = new Rectangle();
    
    private final Rectangle lineRect = new Rectangle();
    
    private int dragTabIndex = -1;

    private static final int RWH = 20;
    
    private static final int BUTTON_SIZE = 30; //XXX 30 is magic number of scroll button size

    private DropMode dropMode = DropMode.INSERT;
    
    private transient DropLocationDnD dropLocation;

    public TabbedPaneDnD() {
        Handler h = new Handler();
        this.addMouseListener(h);
        this.addMouseMotionListener(h);
        
        this.addMouseWheelListener(mouseWheelEvent -> {
            JTabbedPane tabPane = (JTabbedPane) mouseWheelEvent.getSource();
            
            int dir = -mouseWheelEvent.getWheelRotation();
            int selIndex = tabPane.getSelectedIndex();
            int maxIndex = tabPane.getTabCount() - 1;
            
            if ((selIndex == 0 && dir < 0) || (selIndex == maxIndex && dir > 0)) {
                selIndex = maxIndex - selIndex;
            } else {
                selIndex += dir;
            }
            
            if (0 <= selIndex && selIndex < tabPane.getTabCount()) {
                tabPane.setSelectedIndex(selIndex);
            }
        });
        
        this.addPropertyChangeListener(h);
        // UIManager.put() is not enough
        this.setUI(new CustomMetalTabbedPaneUI());
        this.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, HelperUi.COLOR_COMPONENT_BORDER));
    }
    
    public static final class DropLocationDnD extends TransferHandler.DropLocation {
        private final int index;

        private boolean isDropable = true;
        
        private DropLocationDnD(Point p, int index) {
            super(p);
            this.index = index;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public void setDropable(boolean isDropable) {
            this.isDropable = isDropable;
        }
        
        public boolean isDropable() {
            return this.isDropable;
        }
    }

    private void clickArrowButton(String actionKey) {
        ActionMap map = this.getActionMap();
        
        if(map != null) {
            Action action = map.get(actionKey);
            if (action != null && action.isEnabled()) {
                action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, 0, 0));
            }
        }
    }
    
    public void autoScrollTest(Point pt) {
        Rectangle r = this.getTabAreaBounds();
        int tabPlacement = this.getTabPlacement();
        
        if (tabPlacement == TOP || tabPlacement == BOTTOM) {
            RBACKWARD.setBounds(
                r.x,
                r.y,
                RWH,
                r.height
            );
            RFORWARD.setBounds(
                r.x + r.width - RWH - BUTTON_SIZE,
                r.y,
                RWH + BUTTON_SIZE,
                r.height
            );
        } else if (tabPlacement == LEFT || tabPlacement == RIGHT) {
            RBACKWARD.setBounds(
                r.x,
                r.y,
                r.width,
                RWH
            );
            RFORWARD.setBounds(
                r.x,
                r.y + r.height - RWH - BUTTON_SIZE,
                r.width,
                RWH + BUTTON_SIZE
            );
        }
        
        if (RBACKWARD.contains(pt)) {
            this.clickArrowButton("scrollTabsBackwardAction");
        } else if (RFORWARD.contains(pt)) {
            this.clickArrowButton("scrollTabsForwardAction");
        }
    }
    
    public DropLocationDnD dropLocationForPointLocal(Point p) {
        switch (this.dropMode) {
            case INSERT:
                for (int i = 0 ; i < this.getTabCount() ; i++) {
                    if (this.getBoundsAt(i).contains(p)) {
                        return new DropLocationDnD(p, i);
                    }
                }
                if (this.getTabAreaBounds().contains(p)) {
                    return new DropLocationDnD(p, this.getTabCount());
                }
            break;
            
            case USE_SELECTION:
            case ON:
            case ON_OR_INSERT:
            default:
                assert false : "Unexpected drop mode";
        }
        
        return new DropLocationDnD(p, -1);
    }
    
    public final DropLocationDnD getDropLocation() {
        return this.dropLocation;
    }
    
    public Object setDropLocationLocal(TransferHandler.DropLocation location, boolean isDroping) {
        DropLocationDnD old = this.dropLocation;
        
        if (location == null || !isDroping) {
            this.dropLocation = new DropLocationDnD(new Point(), -1);
        } else if (location instanceof DropLocationDnD) {
            this.dropLocation = (DropLocationDnD) location;
        }
        
        this.firePropertyChange("dropLocation", old, this.dropLocation);
        return null;
    }
    
    public void exportTab(int dragIndex, JTabbedPane target, int targetIndex) {
        if (targetIndex < 0) {
            return;
        }

        Component cmp = this.getComponentAt(dragIndex);
        Container parent = target;
        while (parent != null) {
            if (cmp == parent) {
                return; //target==child: JTabbedPane in JTabbedPane
            }
            parent = parent.getParent();
        }
        
        if (target != null) {
            Component tab = this.getTabComponentAt(dragIndex);
            String str = this.getTitleAt(dragIndex);
            Icon icon = this.getIconAt(dragIndex);
            String tip = this.getToolTipTextAt(dragIndex);
            boolean flg = this.isEnabledAt(dragIndex);
            
            this.remove(dragIndex);
            target.insertTab(str, icon, cmp, tip, targetIndex);
            target.setEnabledAt(targetIndex, flg);
    
            target.setTabComponentAt(targetIndex, tab);
            target.setSelectedIndex(targetIndex);
            
            if (tab != null && tab instanceof JComponent) {
                ((JComponent) tab).scrollRectToVisible(tab.getBounds());
            }
        }
    }

    public void convertTab(int prev, int next) {
        if (next < 0 || prev == next) {
            return;
        }
        
        Component cmp = this.getComponentAt(prev);
        Component tab = this.getTabComponentAt(prev);
        String str = this.getTitleAt(prev);
        Icon icon = this.getIconAt(prev);
        String tip = this.getToolTipTextAt(prev);
        boolean flg = this.isEnabledAt(prev);
        int tgtindex = prev>next ? next : next-1;
        
        this.remove(prev);
        this.insertTab(str, icon, cmp, tip, tgtindex);
        this.setEnabledAt(tgtindex, flg);
        
        //When you drag'n'drop a disabled tab, it finishes enabled and selected.
        //pointed out by dlorde
        if (flg) {
            this.setSelectedIndex(tgtindex);
        }
        
        //I have a component in all tabs (jlabel with an X to close the tab) and when i move a tab the component disappear.
        //pointed out by Daniel Dario Morales Salas
        this.setTabComponentAt(tgtindex, tab);
    }
    
    public Rectangle getDropLineRect() {
        DropLocationDnD loc = this.getDropLocation();
        if (loc == null || !loc.isDropable()) {
            return null;
        }

        int index = loc.getIndex();
        if (index < 0) {
            this.lineRect.setRect(0, 0, 0, 0);
            return null;
        }
        
        boolean isZero = index==0;
        Rectangle r = this.getBoundsAt(isZero?0:index-1);
        if (this.getTabPlacement() == TOP || this.getTabPlacement() == BOTTOM) {
            this.lineRect.setRect(
                r.x - LINEWIDTH / 2d + r.width * (isZero ? 0 : 1),
                r.y,
                LINEWIDTH,
                r.height
            );
        } else {
            this.lineRect.setRect(
                r.x,
                r.y - LINEWIDTH / 2d + r.height * (isZero ? 0 : 1),
                r.width,
                LINEWIDTH
            );
        }
        
        return this.lineRect;
    }
    
    public Rectangle getTabAreaBounds() {
        Rectangle tabbedRect = this.getBounds();
        int xx = tabbedRect.x;
        int yy = tabbedRect.y;
        
        Component c = this.getSelectedComponent();
        if (c == null) {
            return tabbedRect;
        }
        
        Rectangle compRect = this.getSelectedComponent().getBounds();
        int tabPlacement = this.getTabPlacement();
        
        if (tabPlacement == TOP) {
            tabbedRect.height = tabbedRect.height - compRect.height;
        } else if (tabPlacement == BOTTOM) {
            tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
            tabbedRect.height = tabbedRect.height - compRect.height;
        } else if (tabPlacement == LEFT) {
            tabbedRect.width = tabbedRect.width - compRect.width;
        } else if (tabPlacement == RIGHT) {
            tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
            tabbedRect.width = tabbedRect.width - compRect.width;
        }
        
        tabbedRect.translate(-xx, -yy);
        return tabbedRect;
    }

    private class Handler extends MouseAdapter implements PropertyChangeListener { //, BeforeDrag
        private Point startPt;
        
        int gestureMotionThreshold;
        
        Handler() {
            try {
                this.gestureMotionThreshold = DragSource.getDragThreshold();
            } catch(ExceptionInInitializerError e) {
                // Fix #2205
                LOGGER.error(e.getMessage(), e);
            }
        }
        
        private void repaintDropLocation() {
            Component c = TabbedPaneDnD.this.getRootPane().getGlassPane();
            
            if (c instanceof PanelGhostGlass) {
                PanelGhostGlass glassPane = (PanelGhostGlass) c;
                glassPane.setTargetTabbedPane(TabbedPaneDnD.this);
                glassPane.repaint();
            }
        }
        
        // PropertyChangeListener
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            
            if ("dropLocation".equals(propertyName)) {
                this.repaintDropLocation();
            }
        }
        
        // MouseListener
        @Override
        public void mousePressed(MouseEvent e) {
            TabbedPaneDnD src = (TabbedPaneDnD) e.getSource();
            if (src.getTabCount() <= 1) {
                this.startPt = null;
                return;
            }
            
            Point tabPt = e.getPoint();
            int idx = src.indexAtLocation(tabPt.x, tabPt.y);
            
            //disabled tab, null component problem.
            //pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
            boolean flag = idx < 0 || !src.isEnabledAt(idx) || src.getComponentAt(idx) == null;
            this.startPt = flag ? null : tabPt;
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            Point tabPt = e.getPoint();
            
            if (
                this.startPt != null 
                && Math.sqrt(Math.pow((double) tabPt.x - this.startPt.x, 2d) + Math.pow((double) tabPt.y - this.startPt.y, 2d)) > this.gestureMotionThreshold
            ) {
                TabbedPaneDnD src = (TabbedPaneDnD) e.getSource();
                TransferHandler th = src.getTransferHandler();
                TabbedPaneDnD.this.setDragTabIndex(src.indexAtLocation(tabPt.x, tabPt.y));
                
                th.exportAsDrag(src, e, TransferHandler.MOVE);
                TabbedPaneDnD.this.lineRect.setRect(0, 0, 0, 0);
                src.getRootPane().getGlassPane().setVisible(true);
                src.setDropLocationLocal(new DropLocationDnD(tabPt, -1), true);
                this.startPt = null;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Point tabPt = e.getPoint();
            TabbedPaneDnD src = (TabbedPaneDnD) e.getSource();
            
            int i = src.indexAtLocation(tabPt.x, tabPt.y);
            
            if (-1 < i && e.getButton() == MouseEvent.BUTTON2) {
                ActionCloseTabResult.perform(i);
            }
        }
    }

    public int getDragTabIndex() {
        return this.dragTabIndex;
    }

    public void setDragTabIndex(int dragTabIndex) {
        this.dragTabIndex = dragTabIndex;
    }
    
}

