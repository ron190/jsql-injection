package com.jsql.mvc.view.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.jsql.mvc.view.component.popup.JPopupTableMenu;


public class TablePanel extends JPanel {
    private static final long serialVersionUID = 4505998197469263100L;
    
    public JTable newJTable;
    
    public void selectTable(){
        newJTable.selectAll();
    }
    
    public void copyTable(){
        ActionEvent nev = new ActionEvent(newJTable, ActionEvent.ACTION_PERFORMED, "copy");
        newJTable.getActionMap().get(nev.getActionCommand()).actionPerformed(nev);
    }
    
    public TablePanel(String[][] data, String[] columnNames, JTabbedPane newJTabbedPane){
        super(new GridLayout(1,0));
        
        newJTable = new JTable(data, columnNames){ 
            private static final long serialVersionUID = 4221305668526115726L;

            public boolean isCellEditable(int row,int column){  
                return false;  
            }  
        };
        newJTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        newJTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        newJTable.setColumnSelectionAllowed(true);
        newJTable.setRowSelectionAllowed(true);
        newJTable.setCellSelectionEnabled(true);
        newJTable.setGridColor(Color.LIGHT_GRAY);
        
        final TableCellRenderer tcrOs = newJTable.getTableHeader().getDefaultRenderer();
        newJTable.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, 
                    Object value, boolean isSelected, boolean hasFocus, 
                    int row, int column) {
                JLabel lbl = (JLabel) tcrOs.getTableCellRendererComponent(table, " "+value+" ", isSelected, hasFocus, row, column);
                lbl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(0, 5, 0, 5)));
                return lbl;
            }
        });
        
        newJTable.getColumnModel().getColumn(0).setResizable(false);
        newJTable.getColumnModel().getColumn(0).setPreferredWidth(34);
        newJTable.getColumnModel().getColumn(0).setMinWidth(34);
        newJTable.getColumnModel().getColumn(0).setMaxWidth(34);
        
        newJTable.getColumnModel().getColumn(1).setResizable(false);
        newJTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        newJTable.getColumnModel().getColumn(1).setMinWidth(70);
        newJTable.getColumnModel().getColumn(1).setMaxWidth(70);

        DefaultTableCellRenderer centerHorizontalAlignment = new CenterRenderer();
        newJTable.getColumnModel().getColumn(0).setCellRenderer(centerHorizontalAlignment);
        newJTable.getColumnModel().getColumn(1).setCellRenderer(centerHorizontalAlignment);
        
        newJTable.getTableHeader().setReorderingAllowed(false);
        
        newJTable.setComponentPopupMenu(new JPopupTableMenu(newJTable));
        newJTable.setAutoCreateRowSorter(true);
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        URL url = getClass().getResource("/com/jsql/images/excel.png");

        Image image = null;
        try {
            image = ImageIO.read(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        newJTable.setCursor(toolkit.createCustomCursor(image, new Point(12, 12),"Hand"));
        newJTable.addMouseListener( new MouseAdapter(){
            public void mousePressed( MouseEvent e ){
                newJTable.requestFocusInWindow();
                if ( SwingUtilities.isRightMouseButton( e ) ){
                    Point p = e.getPoint();
         
                    // get the row index that contains that coordinate
                    int rowNumber = newJTable.rowAtPoint( p );
                    int colNumber = newJTable.columnAtPoint( p );
                    // Get the ListSelectionModel of the JTable
                    DefaultListSelectionModel  model = (DefaultListSelectionModel) newJTable.getSelectionModel();
                    DefaultListSelectionModel  model2 = (DefaultListSelectionModel) newJTable.getColumnModel().getSelectionModel();
         
                    model.moveLeadSelectionIndex(rowNumber);
                    model2.moveLeadSelectionIndex(colNumber);
                }
            }
        });
        
        newJTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null);
        newJTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), null);
        
        Set<KeyStroke> forward = new HashSet(newJTable.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forward.add(KeyStroke.getKeyStroke("TAB"));
        newJTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);
        Set<KeyStroke> backward = new HashSet(newJTable.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        newJTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);
        
        TableColumnAdjuster columnAdjuster = new TableColumnAdjuster(newJTable);
        columnAdjuster.adjustColumns();

        JScrollPane scroller = new JScrollPane(newJTable);
        
        scroller.setColumnHeader(new JViewport() {
            private static final long serialVersionUID = 3600474852945594435L;

            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = 32;
                return d;
            }
        });

        new FixedColumnTable(1, scroller);
        this.add(scroller);
    }
    
    private class CenterRenderer extends DefaultTableCellRenderer{
        private static final long serialVersionUID = -3624608585496119576L;

        public CenterRenderer(){
            this.setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {  
            setBackground(new Color(230,230,230));
            setText(value+"");  
            return this;  
        }  
    }
}

