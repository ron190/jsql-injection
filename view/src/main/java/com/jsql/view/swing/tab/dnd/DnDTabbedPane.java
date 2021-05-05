package com.jsql.view.swing.tab.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

import com.jsql.view.swing.action.ActionCloseTabResult;
import com.jsql.view.swing.ui.CustomMetalTabbedPaneUI;
import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
public class DnDTabbedPane extends JTabbedPane {
    
    private static final int SCROLL_SIZE = 20; // Test
    private static final int BUTTON_SIZE = 30; // 30 is magic number of scroll button size
    private static final int LINE_WIDTH = 3;
    private static final Rectangle RECT_BACKWARD = new Rectangle();
    private static final Rectangle RECT_FORWARD = new Rectangle();
    protected static final Rectangle RECT_LINE = new Rectangle();
    protected int dragTabIndex = -1;
    private transient DnDDropLocation dropLocation;

    public static final class DnDDropLocation extends TransferHandler.DropLocation {
        
        private final int index;
        private boolean dropable = true;
        
        protected DnDDropLocation(Point p, int index) {
            super(p);
            this.index = index;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public void setDroppable(boolean flag) {
            this.dropable = flag;
        }
        
        public boolean isDroppable() {
            return this.dropable;
        }
    }
    
    private void clickArrowButton(String actionKey) {
        
        JButton scrollForwardButton = null;
        JButton scrollBackwardButton = null;
        
        for (Component c: this.getComponents()) {
            
            if (c instanceof JButton) {
                
                if (scrollForwardButton == null) {
                    
                    scrollForwardButton = (JButton) c;
                    
                } else if (scrollBackwardButton == null) {
                    
                    scrollBackwardButton = (JButton) c;
                }
            }
        }
        
        JButton button = "scrollTabsForwardAction".equals(actionKey) ? scrollForwardButton : scrollBackwardButton;
    
        Optional
        .ofNullable(button)
        .filter(JButton::isEnabled)
        .ifPresent(JButton::doClick);
    }
    
    public void autoScrollTest(Point pt) {
        
        Rectangle r = this.getTabAreaBounds();
        
        if (isTopBottomTabPlacement(this.getTabPlacement())) {
            
            RECT_BACKWARD.setBounds(r.x, r.y, SCROLL_SIZE, r.height);
            RECT_FORWARD.setBounds(r.x + r.width - SCROLL_SIZE - BUTTON_SIZE, r.y, SCROLL_SIZE + BUTTON_SIZE, r.height);
            
        } else {
            
            RECT_BACKWARD.setBounds(r.x, r.y, r.width, SCROLL_SIZE);
            RECT_FORWARD.setBounds(r.x, r.y + r.height - SCROLL_SIZE - BUTTON_SIZE, r.width, SCROLL_SIZE + BUTTON_SIZE);
        }
        
        if (RECT_BACKWARD.contains(pt)) {
            
            this.clickArrowButton("scrollTabsBackwardAction");
            
        } else if (RECT_FORWARD.contains(pt)) {
            
            this.clickArrowButton("scrollTabsForwardAction");
        }
    }
    
    protected DnDTabbedPane() {
        
        super();
        
        // UIManager.put() is not enough
        this.setUI(new CustomMetalTabbedPaneUI());
        this.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));
        
        var h = new Handler();
        this.addMouseListener(h);
        this.addMouseMotionListener(h);
        this.addPropertyChangeListener(h);
    }
    
    public DnDDropLocation dropLocationForPointDnD(Point p) {
        
        for (var i = 0; i < this.getTabCount(); i++) {
            
            if (this.getBoundsAt(i).contains(p)) {
                
                return new DnDDropLocation(p, i);
            }
        }
        
        if (this.getTabAreaBounds().contains(p)) {
            
            return new DnDDropLocation(p, this.getTabCount());
        }
        
        return new DnDDropLocation(p, -1);
    }
    
    public Object setDropLocation(TransferHandler.DropLocation location, boolean forDrop) {
        
        DnDDropLocation old = this.dropLocation;
        
        if (Objects.isNull(location) || !forDrop) {
            
            this.dropLocation = new DnDDropLocation(new Point(), -1);
            
        } else if (location instanceof DnDDropLocation) {
            
            this.dropLocation = (DnDDropLocation) location;
        }
        
        this.firePropertyChange("dropLocation", old, this.dropLocation);
        
        return null;
    }
    
    public void exportTab(int dragIndex, JTabbedPane target, int targetIndex) {
        
        var cmp = this.getComponentAt(dragIndex);
        var tab = this.getTabComponentAt(dragIndex);
        String title = this.getTitleAt(dragIndex);
        var icon = this.getIconAt(dragIndex);
        String tip = this.getToolTipTextAt(dragIndex);
        boolean isEnabled = this.isEnabledAt(dragIndex);
        
        this.remove(dragIndex);
        target.insertTab(title, icon, cmp, tip, targetIndex);
        target.setEnabledAt(targetIndex, isEnabled);

        target.setTabComponentAt(targetIndex, tab);
        target.setSelectedIndex(targetIndex);
        
        if (tab instanceof JComponent) {
            
            ((JComponent) tab).scrollRectToVisible(tab.getBounds());
        }
    }
    
    public void convertTab(int prev, int next) {
        
        var cmp = this.getComponentAt(prev);
        var tab = this.getTabComponentAt(prev);
        String title = this.getTitleAt(prev);
        var icon = this.getIconAt(prev);
        String tip = this.getToolTipTextAt(prev);
        boolean isEnabled = this.isEnabledAt(prev);
        int tgtindex = prev > next ? next : next - 1;
        
        this.remove(prev);
        this.insertTab(title, icon, cmp, tip, tgtindex);
        this.setEnabledAt(tgtindex, isEnabled);
        
        // When you drag'n'drop a disabled tab, it finishes enabled and selected.
        // pointed out by dlorde
        if (isEnabled) {
            
            this.setSelectedIndex(tgtindex);
        }
        
        // I have a component in all tabs (jlabel with an X to close the tab) and when i move a tab the component disappear.
        // pointed out by Daniel Dario Morales Salas
        this.setTabComponentAt(tgtindex, tab);
    }
    
    public Optional<Rectangle> getDropLineRect() {
        
        int index =
            Optional
            .ofNullable(this.getDropLocation())
            .filter(DnDDropLocation::isDroppable)
            .map(DnDDropLocation::getIndex)
            .orElse(-1);
        
        if (index < 0) {
            
            RECT_LINE.setBounds(0, 0, 0, 0);
            
            return Optional.empty();
        }
        
        int a = Math.min(index, 1);
        Rectangle r = this.getBoundsAt(a * (index - 1));
        
        if (isTopBottomTabPlacement(this.getTabPlacement())) {
            
            RECT_LINE.setBounds(r.x - LINE_WIDTH / 2 + r.width * a, r.y, LINE_WIDTH, r.height);
            
        } else {
            
            RECT_LINE.setBounds(r.x, r.y - LINE_WIDTH / 2 + r.height * a, r.width, LINE_WIDTH);
        }
        
        return Optional.of(RECT_LINE);
    }
    
    public Rectangle getTabAreaBounds() {
        
        Rectangle tabbedRect = this.getBounds();
        int xx = tabbedRect.x;
        int yy = tabbedRect.y;
        
        Rectangle compRect = Optional.ofNullable(this.getSelectedComponent()).map(Component::getBounds).orElseGet(Rectangle::new);
        int tabPlacement = this.getTabPlacement();
        
        if (isTopBottomTabPlacement(tabPlacement)) {
            
            tabbedRect.height = tabbedRect.height - compRect.height;
            
            if (tabPlacement == BOTTOM) {
                
                tabbedRect.y += compRect.y + compRect.height;
            }
            
        } else {
            
            tabbedRect.width = tabbedRect.width - compRect.width;
            
            if (tabPlacement == RIGHT) {
                
                tabbedRect.x += compRect.x + compRect.width;
            }
        }
        
        tabbedRect.translate(-xx, -yy);
        
        return tabbedRect;
    }

    public static boolean isTopBottomTabPlacement(int tabPlacement) {
        
        return tabPlacement == SwingConstants.TOP || tabPlacement == SwingConstants.BOTTOM;
    }

    private class Handler extends MouseAdapter implements PropertyChangeListener { // , BeforeDrag
        
        private Point startPt;
        private final int gestureMotionThreshold = DragSource.getDragThreshold();

        private void repaintDropLocation() {
            
            Component c = DnDTabbedPane.this.getRootPane().getGlassPane();
            
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
                
                this.repaintDropLocation();
            }
        }
        
        // MouseListener
        @Override
        public void mousePressed(MouseEvent e) {
            
            DnDTabbedPane src = (DnDTabbedPane) e.getComponent();
            boolean isOnlyOneTab = src.getTabCount() <= 1;
            
            if (isOnlyOneTab) {
                
                this.startPt = null;
                return;
            }
            
            var tabPt = e.getPoint();
            int idx = src.indexAtLocation(tabPt.x, tabPt.y);
            
            // disabled tab, null component problem.
            // pointed out by daryl. NullPointerException: i.e. addTab("Tab", null)
            boolean flag = idx < 0 || !src.isEnabledAt(idx) || Objects.isNull(src.getComponentAt(idx));
            
            this.startPt = flag ? null : tabPt;
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            
            var tabPt = e.getPoint();
            
            if (Objects.nonNull(this.startPt) && this.startPt.distance(tabPt) > this.gestureMotionThreshold) {
                
                DnDTabbedPane src = (DnDTabbedPane) e.getComponent();
                var th = src.getTransferHandler();
                DnDTabbedPane.this.dragTabIndex = src.indexAtLocation(tabPt.x, tabPt.y);
                
                // Unhandled NoClassDefFoundError #56620: Could not initialize class java.awt.dnd.DragSource
                th.exportAsDrag(src, e, TransferHandler.MOVE);
                
                RECT_LINE.setBounds(0, 0, 0, 0);
                src.getRootPane().getGlassPane().setVisible(true);
                src.setDropLocation(new DnDDropLocation(tabPt, -1), true);
                
                this.startPt = null;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            
            var tabPt = e.getPoint();
            JTabbedPane src = (JTabbedPane) e.getSource();
            
            int i = src.indexAtLocation(tabPt.x, tabPt.y);
            
            if (-1 < i && e.getButton() == MouseEvent.BUTTON2) {
                
                ActionCloseTabResult.perform(i);
            }
        }
    }
    
    public final DnDDropLocation getDropLocation() {
        return this.dropLocation;
    }
}