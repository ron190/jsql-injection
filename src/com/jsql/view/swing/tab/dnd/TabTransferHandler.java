package com.jsql.view.swing.tab.dnd;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.io.IOException;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import com.jsql.view.swing.tab.AdapterRightTabbedPane;

@SuppressWarnings("serial")
public class TabTransferHandler extends TransferHandler {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(TabTransferHandler.class);

    private final DataFlavor localObjectFlavor;
    
    public TabTransferHandler() {
        localObjectFlavor = new ActivationDataFlavor(AdapterRightTabbedPane.class, DataFlavor.javaJVMLocalObjectMimeType, "RightPaneAdapter");
    }
    
    private DnDTabbedPane srcDnDTabbedPane = null;
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof DnDTabbedPane) {
            srcDnDTabbedPane = (DnDTabbedPane) c;
        }
        return new DataHandler(c, localObjectFlavor.getMimeType());
    }
    
    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDrop() || !support.isDataFlavorSupported(localObjectFlavor)) {
            return false;
        }
        support.setDropAction(MOVE);
        DropLocation tdl = support.getDropLocation();
        Point pt = tdl.getDropPoint();
        DnDTabbedPane target = (DnDTabbedPane) support.getComponent();
        target.autoScrollTest(pt);
        DnDTabbedPane.DropLocation dl = target.dropLocationForPointLocal(pt);
        int idx = dl.getIndex();
        boolean isDropable = false;

        if (target == srcDnDTabbedPane) {
            isDropable = target.getTabAreaBounds().contains(pt) && idx >= 0 && idx != target.dragTabIndex && idx != target.dragTabIndex + 1;
        } else {
            if (srcDnDTabbedPane != null && target != srcDnDTabbedPane.getComponentAt(srcDnDTabbedPane.dragTabIndex)) {
                isDropable = target.getTabAreaBounds().contains(pt) && idx >= 0;
            }
        }
        
        glassPane.setVisible(false);
        target.getRootPane().setGlassPane(glassPane);
        //Bug ID: 6700748 Cursor flickering during D&D when using CellRendererPane with validation
        //http://bugs.sun.com/view_bug.do?bug_id=6700748
        glassPane.setCursor(isDropable ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        glassPane.setVisible(true);
        target.setCursor(isDropable ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);

        if (isDropable) {
            support.setShowDropLocation(true);
            dl.setDropable(true);
            target.setDropLocationLocal(dl, null, true);
            return true;
        } else {
            support.setShowDropLocation(false);
            dl.setDropable(false);
            target.setDropLocationLocal(dl, null, false);
            return false;
        }
    }

    private static GhostGlassPane glassPane;
    
    @Override
    public int getSourceActions(JComponent c) {
        DnDTabbedPane src = (DnDTabbedPane) c;
        if (glassPane == null) {
            glassPane = new GhostGlassPane(src);
            c.getRootPane().setGlassPane(glassPane);
        }
        
        if (src.dragTabIndex < 0) {
            return NONE;
        }

        c.getRootPane().getGlassPane().setVisible(true);
        return MOVE;
    }
    
    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        DnDTabbedPane target = (DnDTabbedPane) support.getComponent();
        DnDTabbedPane.DropLocation dl = target.getDropLocation();
        try {
            DnDTabbedPane src = (DnDTabbedPane) support.getTransferable().getTransferData(localObjectFlavor);
            int index = dl.getIndex();
            if (target == src) {
                src.convertTab(src.dragTabIndex, index);
            } else {
                src.exportTab(src.dragTabIndex, target, index);
            }
            return true;
        } catch (UnsupportedFlavorException e) {
            LOGGER.error(e, e);
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
        return false;
    }
    
    @Override protected void exportDone(JComponent c, Transferable data, int action) {
        DnDTabbedPane src = (DnDTabbedPane) c;
        src.setDropLocationLocal(null, null, false);
        src.repaint();
        glassPane.setVisible(false);
        src.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
