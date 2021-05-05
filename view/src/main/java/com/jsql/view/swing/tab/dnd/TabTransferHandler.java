package com.jsql.view.swing.tab.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

@SuppressWarnings("serial")
public class TabTransferHandler extends TransferHandler {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    protected final DataFlavor localObjectFlavor;
    
    protected DnDTabbedPane source;

    public TabTransferHandler() {
        
        super();
        
        this.localObjectFlavor = new DataFlavor(DnDTabData.class, "DnDTabData");
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        
        if (c instanceof DnDTabbedPane) {
            
            this.source = (DnDTabbedPane) c;
        }

        return new Transferable() {
            
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                
                return new DataFlavor[] {TabTransferHandler.this.localObjectFlavor};
            }
            
            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                
                return Objects.equals(TabTransferHandler.this.localObjectFlavor, flavor);
            }
            
            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                
                if (this.isDataFlavorSupported(flavor)) {
                    
                    return new DnDTabData(TabTransferHandler.this.source);
                    
                } else {
                    
                    throw new UnsupportedFlavorException(flavor);
                }
            }
        };
    }
    
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        
        if (!support.isDrop() || !support.isDataFlavorSupported(this.localObjectFlavor)) {
            return false;
        }
        
        support.setDropAction(TransferHandler.MOVE);
        var tdl = support.getDropLocation();
        var pt = tdl.getDropPoint();
        
        DnDTabbedPane target = (DnDTabbedPane) support.getComponent();
        
        target.autoScrollTest(pt);
        DnDTabbedPane.DnDDropLocation dl = target.dropLocationForPointDnD(pt);
        int idx = dl.getIndex();

        var isDroppable = false;
        boolean isAreaContains = target.getTabAreaBounds().contains(pt) && idx >= 0;
        
        if (target.equals(this.source)) {
            
            isDroppable = isAreaContains && idx != target.dragTabIndex && idx != target.dragTabIndex + 1;
            
        } else {
            
            isDroppable = Optional.ofNullable(this.source).map(c -> !c.isAncestorOf(target)).orElse(false) && isAreaContains;
        }

        // [JDK-6700748] Cursor flickering during D&D when using CellRendererPane with validation - Java Bug System
        // https://bugs.openjdk.java.net/browse/JDK-6700748
        Cursor cursor = isDroppable ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop;
        Component glassPane = target.getRootPane().getGlassPane();
        glassPane.setCursor(cursor);
        target.setCursor(cursor);

        support.setShowDropLocation(isDroppable);
        dl.setDroppable(isDroppable);
        target.setDropLocation(dl, isDroppable);
        return isDroppable;
    }
    
    private BufferedImage makeDragTabImage(DnDTabbedPane tabbedPane) {
        
        Rectangle rect = tabbedPane.getBoundsAt(tabbedPane.dragTabIndex);
        var image = new BufferedImage(tabbedPane.getWidth(), tabbedPane.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g2 = image.createGraphics();
        tabbedPane.paint(g2);
        g2.dispose();
        
        if (rect.x < 0) {
            
            rect.translate(-rect.x, 0);
        }
        
        if (rect.y < 0) {
            
            rect.translate(0, -rect.y);
        }
        
        if (rect.x + rect.width > image.getWidth()) {
            
            rect.width = image.getWidth() - rect.x;
        }
        
        if (rect.y + rect.height > image.getHeight()) {
            
            rect.height = image.getHeight() - rect.y;
        }
        
        return image.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        
        if (c instanceof DnDTabbedPane) {
            
            DnDTabbedPane src = (DnDTabbedPane) c;
            c.getRootPane().setGlassPane(new GhostGlassPane(src));
            
            if (src.dragTabIndex < 0) {
                
                return TransferHandler.NONE;
            }
            
            this.setDragImage(this.makeDragTabImage(src));
            c.getRootPane().getGlassPane().setVisible(true);
            
            return TransferHandler.MOVE;
        }
        
        return TransferHandler.NONE;
    }
    
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        
        if (!this.canImport(support)) {
            
            return false;
        }

        DnDTabbedPane target = (DnDTabbedPane) support.getComponent();
        DnDTabbedPane.DnDDropLocation dl = target.getDropLocation();
        
        try {
            DnDTabData data = (DnDTabData) support.getTransferable().getTransferData(this.localObjectFlavor);
            DnDTabbedPane src = data.tabbedPane;
            int index = dl.getIndex();
            
            if (target.equals(src)) {
                
                src.convertTab(src.dragTabIndex, index);
                
            } else {
                
                src.exportTab(src.dragTabIndex, target, index);
            }
            
            return true;
            
        } catch (UnsupportedFlavorException | IOException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
        
        return false;
    }
    
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        
        DnDTabbedPane src = (DnDTabbedPane) c;
        
        src.getRootPane().getGlassPane().setVisible(false);
        src.setDropLocation(null, false);
        src.repaint();
        src.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}