package mvc.view.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class TablePanel extends JPanel {
	private static final long serialVersionUID = 4505998197469263100L;
	
	private JTable newJTable;
	
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
				JLabel lbl = (JLabel) tcrOs.getTableCellRendererComponent(table, 
						value, isSelected, hasFocus, row, column);
				lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY));
				return lbl;
			}
		});
		
		newJTable.getColumnModel().getColumn(0).setResizable(false);
		newJTable.getColumnModel().getColumn(0).setPreferredWidth(34);
		newJTable.getColumnModel().getColumn(0).setMinWidth(34);
		newJTable.getColumnModel().getColumn(0).setMaxWidth(34);
		
		newJTable.getColumnModel().getColumn(1).setResizable(false);
		newJTable.getColumnModel().getColumn(1).setPreferredWidth(60);
		newJTable.getColumnModel().getColumn(1).setMinWidth(60);
		newJTable.getColumnModel().getColumn(1).setMaxWidth(60);

		DefaultTableCellRenderer centerHorizontalAlignment = new CenterRenderer();
		newJTable.getColumnModel().getColumn(0).setCellRenderer(centerHorizontalAlignment);
		newJTable.getColumnModel().getColumn(1).setCellRenderer(centerHorizontalAlignment);
				
		newJTable.getTableHeader().setReorderingAllowed(false);
		
		TableColumnAdjuster tca = new TableColumnAdjuster(newJTable);
		tca.adjustColumns();

		JScrollPane scroll = new JScrollPane(newJTable);
		scroll.setColumnHeader(new JViewport() {
			private static final long serialVersionUID = 3600474852945594435L;

			@Override public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				d.height = 32;
				return d;
			}
		});
		this.add(scroll);
	}
	
	private class CenterRenderer extends DefaultTableCellRenderer{
		private static final long serialVersionUID = -3624608585496119576L;

		public CenterRenderer(){
			this.setHorizontalAlignment(JLabel.CENTER);
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {  
		    setBackground(table.getTableHeader().getBackground());  
		    setFont(new Font(getFont().getName(),Font.PLAIN,table.getTableHeader().getFont().getSize()));
		    setText(value+"");  
			return this;  
		}  
	}
}

