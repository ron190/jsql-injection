package com.jsql.view.swing.tab.dnd;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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

import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.action.ActionCloseTabResult;
import com.jsql.view.swing.ui.CustomMetalTabbedPaneUI;

@SuppressWarnings("serial")
public class DnDTabbedPane extends JTabbedPane {
    
    private static final int LINEWIDTH = 3;
    
    public static final Rectangle RBACKWARD = new Rectangle();
    
    public static final Rectangle RFORWARD  = new Rectangle();
    
    private final Rectangle lineRect = new Rectangle();
    
    public int dragTabIndex = -1;

    private static final int RWH = 20;
    
    private static final int BUTTON_SIZE = 30; //XXX 30 is magic number of scroll button size

    private DropMode dropMode = DropMode.INSERT;
    
    private transient DropLocation dropLocation;

    public DnDTabbedPane() {
        Handler h = new Handler();
        addMouseListener(h);
        addMouseMotionListener(h);
        
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                JTabbedPane tabPane = (JTabbedPane)e.getSource();
                
                int dir = -e.getWheelRotation();
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
            }
        });
        
        addPropertyChangeListener(h);
        // UIManager.put() is not sufficient
        setUI(new CustomMetalTabbedPaneUI());
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, HelperUi.COLOR_COMPONENT_BORDER));
    }
    
    public static final class DropLocation extends TransferHandler.DropLocation {
        private final int index;

        private boolean isDropable = true;
        
        private DropLocation(Point p, int index) {
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
        ActionMap map = getActionMap();
        
        if(map != null) {
            Action action = map.get(actionKey);
            if (action != null && action.isEnabled()) {
                action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, 0, 0));
            }
        }
    }
    
    public void autoScrollTest(Point pt) {
        Rectangle r = getTabAreaBounds();
        int tabPlacement = getTabPlacement();
        
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
            clickArrowButton("scrollTabsBackwardAction");
        } else if (RFORWARD.contains(pt)) {
            clickArrowButton("scrollTabsForwardAction");
        }
    }
    
    public DropLocation dropLocationForPointLocal(Point p) {
        switch (dropMode) {
            case INSERT:
                for (int i = 0 ; i < getTabCount() ; i++) {
                    if (getBoundsAt(i).contains(p)) {
                        return new DropLocation(p, i);
                    }
                }
                if (getTabAreaBounds().contains(p)) {
                    return new DropLocation(p, getTabCount());
                }
            break;
            
            case USE_SELECTION:
            case ON:
            case ON_OR_INSERT:
            default:
                assert false : "Unexpected drop mode";
        }
        
        return new DropLocation(p, -1);
    }
    
    public final DropLocation getDropLocation() {
        return dropLocation;
    }
    
    public Object setDropLocationLocal(TransferHandler.DropLocation location, Object state, boolean isDroping) {
        DropLocation old = dropLocation;
        
        if (location == null || !isDroping) {
            dropLocation = new DropLocation(new Point(), -1);
        } else if (location instanceof DropLocation) {
            dropLocation = (DropLocation) location;
        }
        
        firePropertyChange("dropLocation", old, dropLocation);
        return null;
    }
    
    public void exportTab(int dragIndex, JTabbedPane target, int targetIndex) {
        if (targetIndex < 0) {
            return;
        }

        Component cmp = getComponentAt(dragIndex);
        Container parent = target;
        while (parent != null) {
            if (cmp == parent) {
                return; //target==child: JTabbedPane in JTabbedPane
            }
            parent = parent.getParent();
        }
        
        if (target != null) {
            Component tab = getTabComponentAt(dragIndex);
            String str = getTitleAt(dragIndex);
            Icon icon = getIconAt(dragIndex);
            String tip = getToolTipTextAt(dragIndex);
            boolean flg = isEnabledAt(dragIndex);
            
            remove(dragIndex);
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
        
        Component cmp = getComponentAt(prev);
        Component tab = getTabComponentAt(prev);
        String str = getTitleAt(prev);
        Icon icon = getIconAt(prev);
        String tip = getToolTipTextAt(prev);
        boolean flg = isEnabledAt(prev);
        int tgtindex = prev>next ? next : next-1;
        
        remove(prev);
        insertTab(str, icon, cmp, tip, tgtindex);
        setEnabledAt(tgtindex, flg);
        
        //When you drag'n'drop a disabled tab, it finishes enabled and selected.
        //pointed out by dlorde
        if (flg) {
            setSelectedIndex(tgtindex);
        }
        
        //I have a component in all tabs (jlabel with an X to close the tab) and when i move a tab the component disappear.
        //pointed out by Daniel Dario Morales Salas
        setTabComponentAt(tgtindex, tab);
    }
    
    public Rectangle getDropLineRect() {
        DropLocation loc = getDropLocation();
        if (loc == null || !loc.isDropable()) {
            return null;
        }

        int index = loc.getIndex();
        if (index < 0) {
            lineRect.setRect(0, 0, 0, 0);
            return null;
        }
        
        boolean isZero = index==0;
        Rectangle r = getBoundsAt(isZero?0:index-1);
        if (getTabPlacement() == TOP || getTabPlacement() == BOTTOM) {
            lineRect.setRect(
                r.x - LINEWIDTH / 2 + r.width * (isZero ? 0 : 1),
                r.y,
                LINEWIDTH,
                r.height
            );
        } else {
            lineRect.setRect(
                r.x,
                r.y - LINEWIDTH / 2 + r.height * (isZero ? 0 : 1),
                r.width,
                LINEWIDTH
            );
        }
        
        return lineRect;
    }
    
    public Rectangle getTabAreaBounds() {
        Rectangle tabbedRect = getBounds();
        int xx = tabbedRect.x;
        int yy = tabbedRect.y;
        
        Component c = getSelectedComponent();
        if (c == null) {
            return tabbedRect;
        }
        
        Rectangle compRect = getSelectedComponent().getBounds();
        int tabPlacement = getTabPlacement();
        
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
        
        int gestureMotionThreshold = DragSource.getDragThreshold();
        
        private void repaintDropLocation(DropLocation loc) {
            Component c = getRootPane().getGlassPane();
            
            if (c instanceof GhostGlassPane) {
                GhostGlassPane glassPane = (GhostGlassPane) c;
                glassPane.setTargetTabbedPane(DnDTabbedPane.this);
                glassPane.repaint();
            }
        }
        
        // PropertyChangeListener
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            
            if ("dropLocation".equals(propertyName)) {
                repaintDropLocation(getDropLocation());
            }
        }
        
        // MouseListener
        @Override
        public void mousePressed(MouseEvent e) {
            DnDTabbedPane src = (DnDTabbedPane)e.getSource();
            if (src.getTabCount() <= 1) {
                startPt = null;
                return;
            }
            
            Point tabPt = e.getPoint();
            int idx = src.indexAtLocation(tabPt.x, tabPt.y);
            
            //disabled tab, null component problem.
            //pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
            boolean flag = idx < 0 || !src.isEnabledAt(idx) || src.getComponentAt(idx) == null;
            startPt = flag ? null : tabPt;
        }
        
        @Override
        public void mouseDragged(MouseEvent e)  {
            Point tabPt = e.getPoint();
            
            if (startPt != null && Math.sqrt(Math.pow(tabPt.x - startPt.x, 2) + Math.pow(tabPt.y - startPt.y, 2)) > gestureMotionThreshold) {
                DnDTabbedPane src = (DnDTabbedPane) e.getSource();
                TransferHandler th = src.getTransferHandler();
                dragTabIndex = src.indexAtLocation(tabPt.x, tabPt.y);
                
                th.exportAsDrag(src, e, TransferHandler.MOVE);
                lineRect.setRect(0, 0, 0, 0);
                src.getRootPane().getGlassPane().setVisible(true);
                src.setDropLocationLocal(new DropLocation(tabPt, -1), null, true);
                startPt = null;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Point tabPt = e.getPoint();
            DnDTabbedPane src = (DnDTabbedPane) e.getSource();
            
            int i = src.indexAtLocation(tabPt.x, tabPt.y);
            
            if (-1 < i && e.getButton() == MouseEvent.BUTTON2) {
                ActionCloseTabResult.perform(i);
            }
        }
    }
}

