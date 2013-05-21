package com.jsql.mvc.view.component;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.border.AbstractBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;

public class CustomJList<StringObject> extends JList {
    JList<StringObject> myList;
    DefaultListModel<StringObject> listModel = null;

    public class StringObject{
        String internalString;
        public StringObject(String newString){
            internalString = newString;
        }

        @Override
        public String toString() {
            return internalString;
        }
    }

    List<String> defaultList;
    
    public CustomJList() {
        this(new ArrayList<String>());
    }
    
    public CustomJList(List<String> rootPath){
        defaultList = rootPath;

        listModel = new DefaultListModel<StringObject>();
        

        for(String path: rootPath)
            listModel.addElement(new StringObject(path));

        myList = this;
        this.setModel(listModel);
        
        final int[] mouseOver = {-1};
        
        myList.addMouseListener(new MouseAdapter() {
            public void maybeShowPopup(final MouseEvent e) {
                if (e.isPopupTrigger()){
                    JList tree = (JList)e.getSource();
                        
                    JPopupMenu tablePopupMenu = new JPopupMenu();
                    
                    JMenuItem mnImport = new JMenuItem("Import...");
                    JMenuItem mnExport = new JMenuItem("Export...");
                    JMenuItem mnCut = new JMenuItem("Cut");
                    JMenuItem mnCopy = new JMenuItem("Copy");
                    JMenuItem mnPaste = new JMenuItem("Paste");
                    JMenuItem mnDelete = new JMenuItem("Delete");
                    JMenuItem mnNew = new JMenuItem("New Value(s)...");
                    JMenuItem mnDefault = new JMenuItem("Restore default");
                    JMenuItem mnSelectAll = new JMenuItem("Select All");
                    
                    //Create a file chooser
                    final JFileChooser importFileDialog = new JFileChooser();
                    importFileDialog.setDialogTitle("Import a list of file paths");
                    importFileDialog.setMultiSelectionEnabled(true);
                    
                    mnNew.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            JPanel l = new JPanel(new BorderLayout());
                            JTextArea x = new JTextArea(6, 50);
                            l.add(new JLabel("Add new value(s) to the list:"), BorderLayout.NORTH);
                            l.add(new JScrollPane(x));
                              
//                            int result = JOptionPane.showConfirmDialog(myList.getTopLevelAncestor(),  
//                                    l,  
//                                    "Add Value(s)",  
//                                    JOptionPane.YES_NO_OPTION);
                            
                            int result = JOptionPane.showOptionDialog(myList.getTopLevelAncestor(), 
                                    l, 
                                    "Add Value(s)", 
                                    JOptionPane.OK_CANCEL_OPTION, 
                                    JOptionPane.QUESTION_MESSAGE, 
                                    null, 
                                    new String[]{"Ok", "Cancel"}, // this is the array
                                    "Cancel");
                            
                            if(!x.getText().equals("") && result == JOptionPane.YES_OPTION){
                                int y = 0;
                                if(myList.getSelectedIndex() > 0)
                                    y = myList.getSelectedIndex();
                                
                                int a = y;
                                for(String c: x.getText().split("\\n"))
                                    if(!c.equals(""))
                                        ((DefaultListModel<StringObject>)myList.getModel()).add(y++, new StringObject(c));
                                
                                myList.setSelectionInterval(a, y-1);
                                myList.scrollRectToVisible(
                                        myList.getCellBounds(
                                                myList.getMinSelectionIndex(), 
                                                myList.getMaxSelectionIndex()
                                                )
                                        );
                                
                                x.setText(null);
                            }
                        }
                    });
                    
                    mnImport.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            int returnVal = importFileDialog.showOpenDialog(myList.getTopLevelAncestor());
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                processFileDropPaste(Arrays.asList(importFileDialog.getSelectedFiles()), myList.locationToIndex(e.getPoint()));
                            }
                        }
                    });
                    
                    mnCopy.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Action a = myList.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
                            if (a != null) {
                                a.actionPerformed(new ActionEvent(myList,
                                                                  ActionEvent.ACTION_PERFORMED,
                                                                  null));
                            }
                        }
                    });

                    mnCut.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Action a = myList.getActionMap().get(TransferHandler.getCutAction().getValue(Action.NAME));
                            if (a != null) {
                                a.actionPerformed(new ActionEvent(myList,
                                                                  ActionEvent.ACTION_PERFORMED,
                                                                  null));
                            }
                        }
                    });
                    
                    mnPaste.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Action a = myList.getActionMap().get(TransferHandler.getPasteAction().getValue(Action.NAME));
                            if (a != null) {
                                a.actionPerformed(new ActionEvent(myList,
                                                                  ActionEvent.ACTION_PERFORMED,
                                                                  null));
                            }
                        }
                    });
                    
                    mnDelete.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            remove();
                        }
                    });
                    
                    mnExport.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            try {
                                final JFileChooser importFileDialog = new JFileChooser(){
                                    @Override
                                    public void approveSelection(){
                                        File f = getSelectedFile();
                                        if(getDialogType() == SAVE_DIALOG)
                                        if(f.exists()){
                                            int result = JOptionPane.showConfirmDialog(this,getSelectedFile().getName() + " already exists.\nDo you want to replace it?","Confirm Export",
                                                    JOptionPane.YES_NO_OPTION);
                                            switch(result){
                                                case JOptionPane.YES_OPTION:
                                                    super.approveSelection();
                                                    return;
                                                case JOptionPane.NO_OPTION:
                                                    return;
                                                case JOptionPane.CLOSED_OPTION:
                                                    return;
                                                case JOptionPane.CANCEL_OPTION:
                                                    cancelSelection();
                                                    return;
                                            }
                                        }else{
                                            super.approveSelection();
                                        }
                                    }
                                };
                                importFileDialog.setDialogTitle("Export list to a file");
                                int returnVal = importFileDialog.showSaveDialog(myList.getTopLevelAncestor());
                                if (returnVal != JFileChooser.APPROVE_OPTION) return;
                                    
                                PrintStream out = new PrintStream(new FileOutputStream(importFileDialog.getSelectedFile()));
                                int len = myList.getModel().getSize();
                                for(int i = 0; i < len; i++) {
                                   out.println( myList.getModel().getElementAt(i).toString() );
                                }
                                out.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    
                    mnDefault.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            listModel.clear();
                            for(String path: defaultList)
                                listModel.addElement(new StringObject(path));
                        }
                    });

                    mnSelectAll.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            int start = 0;
                            int end = myList.getModel().getSize() - 1;
                            if (end >= 0) {
                                myList.setSelectionInterval(start, end);
                            }
                        }
                    });

                    tablePopupMenu.add(mnNew);
                    tablePopupMenu.add(new JSeparator());
                    tablePopupMenu.add(mnCut);
                    tablePopupMenu.add(mnCopy);
                    tablePopupMenu.add(mnPaste);
                    tablePopupMenu.add(mnDelete);
                    tablePopupMenu.add(new JSeparator());
                    tablePopupMenu.add(mnSelectAll);
                    tablePopupMenu.add(new JSeparator());
                    tablePopupMenu.add(mnImport);
                    tablePopupMenu.add(mnExport);
                    tablePopupMenu.add(new JSeparator());
                    tablePopupMenu.add(mnDefault);
                    
                    tablePopupMenu.show(tree, e.getX(), e.getY());
                }
            }
            
            @Override public void mousePressed(MouseEvent e) {
                if ( SwingUtilities.isRightMouseButton(e) ){
                    int y = myList.locationToIndex(e.getPoint());
                    boolean containsIndex = false;
                    for(int i: myList.getSelectedIndices())
                        if(i == y){
                            containsIndex = true;
                            break;
                        }
                    if(!containsIndex)
                        myList.setSelectedIndex(y);
                }
                maybeShowPopup(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }
            
            public void mouseExited(MouseEvent e) {
                mouseOver[0] = -1;
                myList.repaint();
            }
        });

        /**
         * Transform Cut, selects next value 
         */
        ActionMap treeMap = myList.getActionMap();
        treeMap.put(TransferHandler.getCutAction().getValue(Action.NAME), new AbstractAction() {
            private static final long serialVersionUID = -8154421201183457014L;

            @Override public void actionPerformed(ActionEvent e) {
                if(myList.getSelectedValuesList().isEmpty()) return;
                
                List<StringObject> selectedValues = myList.getSelectedValuesList();
                List<StringObject> siblings = new ArrayList<StringObject>();
                for(StringObject value:selectedValues){
                    int valueIndex = listModel.indexOf(value);

                    if(valueIndex < listModel.size()-1)
                        siblings.add(listModel.get(valueIndex+1));
                    else if(valueIndex > 0)
                        siblings.add(listModel.get(valueIndex-1));
                }

                TransferHandler.getCutAction().actionPerformed(e);
                for(StringObject sibling:siblings)
                    myList.setSelectedValue(sibling, true);
            }

        });

        treeMap.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        treeMap.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

        class ComplexCellRenderer implements ListCellRenderer {
            protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            public Component getListCellRendererComponent(JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);

                renderer.setFont(new Font("Segoe UI", Font.PLAIN, renderer.getFont().getSize()));

                if(isSelected&&myList.isFocusOwner())
                    renderer.setBackground(new Color(211,230,255));
                else if(mouseOver[0] == index)
                    renderer.setBackground(new Color(237,245,255));
                else if(isSelected&&!myList.isFocusOwner())
                    renderer.setBackground(new Color(248,249,249));
                else
                    renderer.setBackground(Color.WHITE);
                              
                if(isSelected&&myList.isFocusOwner())
                    renderer.setBorder(new LineBorder(new Color(132,172,221), 1, true));
                else if(mouseOver[0] == index)
                    renderer.setBorder(new LineBorder(new Color(185,215,252), 1, true));
                else if(isSelected&&!myList.isFocusOwner())
                    renderer.setBorder(new LineBorder(new Color(218,218,218), 1, true));
                else if(cellHasFocus)
                    renderer.setBorder(BorderFactory.createCompoundBorder( new AbstractBorder() {
                        @Override
                        public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
                            Graphics2D gg = (Graphics2D) g;
                            gg.setColor(Color.GRAY);
                            gg.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1}, 0));
                            gg.drawRect(x, y, w - 1, h - 1);
                        }
                    },BorderFactory.createEmptyBorder(0, 1, 0, 0)));
                else
                    renderer.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

                return renderer;
            }
        }
        ListCellRenderer renderer = new ComplexCellRenderer();
        myList.setCellRenderer(renderer);

        /**
         * Allows color change when list loses/gains focus
         */
        myList.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent arg0) {
                myList.repaint();
            }
            @Override
            public void focusGained(FocusEvent arg0) {
                myList.repaint();
            }
        });

        myList.setDragEnabled(true);
        myList.setDropMode(DropMode.INSERT);

        
        /**
         * Allows deleting values
         */
        myList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if (arg0.getKeyCode() == KeyEvent.VK_DELETE)
                    remove();
            }
        });

        /**
         * Set Drag and Drop
         */
        myList.setTransferHandler(new TransferHandler(null){
            private static final long serialVersionUID = -1029596387333896827L;

            // Export
            @Override
            public int getSourceActions(JComponent c) {
                return TransferHandler.COPY_OR_MOVE;
            }

            List<StringObject> dragPaths = null;

            @Override
            protected Transferable createTransferable(JComponent c) {
                JList<StringObject> list = (JList<StringObject>)c;
                dragPaths = list.getSelectedValuesList();

                StringBuffer buff = new StringBuffer();
                for(StringObject t: dragPaths)
                    buff.append(t+"\n");

                return new StringSelection(buff.toString());
            }  

            @Override
            protected void exportDone(JComponent c, Transferable data, int action) {
                if (action == TransferHandler.MOVE) {
                    JList<StringObject> list = (JList<StringObject>)c;
                    DefaultListModel<StringObject> model = (DefaultListModel<StringObject>)list.getModel();
                    for(StringObject t: dragPaths)
                        model.remove(model.indexOf(t));

                    dragPaths = null;
                }
            }

            //Import
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.stringFlavor) || 
                        support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }

                JList<StringObject> list = (JList<StringObject>)support.getComponent();
                DefaultListModel<StringObject> model = (DefaultListModel<StringObject>)list.getModel();
                if (support.isDrop()) { //This is a drop
                    if (support.isDataFlavorSupported(DataFlavor.stringFlavor)){
                        JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
                        int childIndex = dl.getIndex(); 

                        List<Integer> selectAfterDrop = new ArrayList<Integer>();

                        if(dragPaths != null && dragPaths.size()>0){ // DnD from list
                            for(StringObject value: dragPaths)
                                if(!value.toString().equals("")){  
                                    StringObject newValue = new StringObject(value.toString()); //! FUUuu
                                    selectAfterDrop.add(childIndex);
                                    model.add(childIndex++, newValue);
                                } 
                        }else{ // DnD from outside
                            try {
                                String importString = (String) (support.getTransferable().getTransferData(DataFlavor.stringFlavor));
                                for(String value: importString.split("\\n"))
                                    if(!value.equals("")){
                                        selectAfterDrop.add(childIndex);
                                        model.add(childIndex++, new StringObject(value));
                                    }
                            } catch (UnsupportedFlavorException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        //array is the Integer array
                        int[] selectedIndices = new int[selectAfterDrop.size()];
                        int i=0;
                        for (Integer integer: selectAfterDrop) {
                            selectedIndices[i] = integer.intValue();
                            i++;
                        }
                        list.setSelectedIndices(selectedIndices);
                    }else if(support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
                        int childIndex = dl.getIndex(); 
                        
                        try {
                            processFileDropPaste((List<File>)support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor),
                                    childIndex  
                                    );
                        } catch (UnsupportedFlavorException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else { //This is a paste
                    Transferable transferableFromClipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                    if (transferableFromClipboard != null)
                        if(transferableFromClipboard.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                            try {
                                String clipboardText = (String) transferableFromClipboard.getTransferData(DataFlavor.stringFlavor);

                                int y = 0;
                                if(list.getSelectedIndex() > 0)
                                    y = list.getSelectedIndex();
                                list.clearSelection();

                                List<Integer> k = new ArrayList<Integer>();
                                for(String f: clipboardText.split("\\n"))
                                    if(!f.equals("")){
                                        StringObject c = new StringObject(f);
                                        k.add(y);
                                        model.add(y++, c);
                                    }
                                int[] array2 = new int[k.size()];
                                int i=0;
                                for (Integer integer : k) {
                                    array2[i] = integer.intValue();
                                    i++;
                                }
                                list.setSelectedIndices(array2);
                                list.scrollRectToVisible(
                                        list.getCellBounds(
                                                list.getMinSelectionIndex(), 
                                                list.getMaxSelectionIndex()
                                                )
                                        );
                            } catch (UnsupportedFlavorException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else if(transferableFromClipboard.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            try {
                                int y = 0;
                                if(list.getSelectedIndex() > 0)
                                    y = list.getSelectedIndex();
                                list.clearSelection();
                                
                                System.out.println(transferableFromClipboard.getTransferData(DataFlavor.javaFileListFlavor));
                                processFileDropPaste((List<File>)transferableFromClipboard.getTransferData(DataFlavor.javaFileListFlavor),
                                        y);
                                
                            } catch (UnsupportedFlavorException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                }

                return true;
            }
        });
    }

    void remove(){
        if(myList.getSelectedValuesList().isEmpty())return;

        List<StringObject> selectedValues = myList.getSelectedValuesList();
        for(StringObject i:selectedValues){
            int l = listModel.indexOf(i);
            listModel.removeElement(i);
            if(l == listModel.getSize())
                myList.setSelectedIndex(l-1);
            else
                myList.setSelectedIndex(l);
        }
        if(myList.getMinSelectionIndex() > -1 && myList.getMaxSelectionIndex() > -1)
            myList.scrollRectToVisible(
                    myList.getCellBounds(
                            myList.getMinSelectionIndex(), 
                            myList.getMaxSelectionIndex()
                            )
                    );
    }

    void processFileDropPaste(List<File> filesToImport, int _position){
        final DefaultListModel<StringObject> listModel = (DefaultListModel<StringObject>) myList.getModel();

        int position = _position;
        int childIndex = position;
        System.out.println(filesToImport);
        if(filesToImport.size() == 0) return;
        try {
            if( true ) {
                for( Iterator<File> it = filesToImport.iterator(); it.hasNext(); ) {
                    File fileToImport = it.next();

                    if(Files.probeContentType(fileToImport.toPath())==null || !Files.probeContentType(fileToImport.toPath()).equals("text/plain")){
                        JOptionPane.showMessageDialog(myList.getTopLevelAncestor(),
                                "Unsupported file format.\nPlease import only text/plain files.",
                                "Import Error",
                                JOptionPane.ERROR_MESSAGE,
                                new ImageIcon(getClass().getResource("/com/jsql/images/118458_36028_32_delete_error_icon.png")));
                        return ;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] options = {"Replace", "Add", "Cancel"};
        int n = JOptionPane.showOptionDialog(myList.getTopLevelAncestor(),
                "Replace list or add to current location?",
                "Import file",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);

        if (n == JOptionPane.YES_OPTION || n == JOptionPane.NO_OPTION) {

            if (n == JOptionPane.YES_OPTION) {
                listModel.clear();
                for( Iterator<File> it = (filesToImport).iterator(); it.hasNext(); ) {
                    BufferedReader in;
                    try {
                        in = new BufferedReader(new FileReader(it.next()));
                        String line;
                        while((line = in.readLine()) != null)
                            if(!line.equals(""))
                                listModel.addElement(new StringObject(line));

                        if(listModel.size()>0)
                            myList.setSelectionInterval(0, listModel.size()-1);

                        myList.scrollRectToVisible(
                                myList.getCellBounds(
                                        myList.getMinSelectionIndex(), 
                                        myList.getMaxSelectionIndex()
                                        )
                                );
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (n == JOptionPane.NO_OPTION) {
                for( Iterator<File> it = (filesToImport).iterator(); it.hasNext(); ) {
                    BufferedReader in;
                    try {
                        in = new BufferedReader(new FileReader(it.next()));
                        String line;

                        while((line = in.readLine()) != null)
                            if(!line.equals(""))
                                listModel.add(childIndex++, new StringObject(line));

                        if(listModel.size()>0)
                            myList.setSelectionInterval(position, childIndex-1);

                        myList.scrollRectToVisible(
                                myList.getCellBounds(
                                        myList.getMinSelectionIndex(), 
                                        myList.getMaxSelectionIndex()
                                        )
                                );
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } 
        }
    }
}
