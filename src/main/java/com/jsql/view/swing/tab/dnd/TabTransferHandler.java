package com.jsql.view.swing.tab.dnd;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import com.jsql.view.swing.tab.TabResults;

@SuppressWarnings("serial")
public class TabTransferHandler extends TransferHandler {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    private final DataFlavor localObjectFlavor;
    
    private TabbedPaneDnD srcDnDTabbedPane = null;
    
    private PanelGhostGlass glassPane;
    
    public TabTransferHandler() {
        this.localObjectFlavor = new ActivationDataFlavor(TabResults.class, DataFlavor.javaJVMLocalObjectMimeType, "RightPaneAdapter");
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof TabbedPaneDnD) {
            this.srcDnDTabbedPane = (TabbedPaneDnD) c;
        }
        return new DataHandler(c, this.localObjectFlavor.getMimeType());
    }
    
    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDrop() || !support.isDataFlavorSupported(this.localObjectFlavor)) {
            return false;
        }
        
        support.setDropAction(MOVE);
        DropLocation tdl = support.getDropLocation();
        Point pt = tdl.getDropPoint();
        TabbedPaneDnD target = (TabbedPaneDnD) support.getComponent();
        target.autoScrollTest(pt);
        TabbedPaneDnD.DropLocationDnD dl = target.dropLocationForPointLocal(pt);
        int idx = dl.getIndex();
        boolean isDropable = false;

        if (target == this.srcDnDTabbedPane) {
            isDropable =
                target.getTabAreaBounds().contains(pt) &&
                idx >= 0 &&
                idx != target.getDragTabIndex() &&
                idx != target.getDragTabIndex() + 1
            ;
        } else {
            if (
                this.srcDnDTabbedPane != null &&
                target != this.srcDnDTabbedPane.getComponentAt(this.srcDnDTabbedPane.getDragTabIndex())
            ) {
                isDropable = target.getTabAreaBounds().contains(pt) && idx >= 0;
            }
        }
        
        this.glassPane.setVisible(false);
        target.getRootPane().setGlassPane(this.glassPane);
        
        //Bug ID: 6700748 Cursor flickering during D&D when using CellRendererPane with validation
        //http://bugs.sun.com/view_bug.do?bug_id=6700748
        this.glassPane.setCursor(isDropable ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        this.glassPane.setVisible(true);
        target.setCursor(isDropable ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);

        if (isDropable) {
            support.setShowDropLocation(true);
            dl.setDropable(true);
            target.setDropLocationLocal(dl, true);
            return true;
        } else {
            support.setShowDropLocation(false);
            dl.setDropable(false);
            target.setDropLocationLocal(dl, false);
            return false;
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        TabbedPaneDnD src = (TabbedPaneDnD) c;
        if (this.glassPane == null) {
            this.glassPane = new PanelGhostGlass(src);
            c.getRootPane().setGlassPane(this.glassPane);
        }
        
        if (src.getDragTabIndex() < 0) {
            return NONE;
        }

        c.getRootPane().getGlassPane().setVisible(true);
        return MOVE;
    }
    
    @Override
    public boolean importData(TransferSupport support) {
        if (!this.canImport(support)) {
            return false;
        }

        TabbedPaneDnD target = (TabbedPaneDnD) support.getComponent();
        TabbedPaneDnD.DropLocationDnD dl = target.getDropLocation();
        try {
            TabbedPaneDnD src = (TabbedPaneDnD) support.getTransferable().getTransferData(this.localObjectFlavor);
            int index = dl.getIndex();
            if (target == src) {
                src.convertTab(src.getDragTabIndex(), index);
            } else {
                src.exportTab(src.getDragTabIndex(), target, index);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
    
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        TabbedPaneDnD src = (TabbedPaneDnD) c;
        src.setDropLocationLocal(null, false);
        src.repaint();
        this.glassPane.setVisible(false);
        src.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
}
