package mvc.view.component;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeNodeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 6713145837575127059L;

	private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object nodeRenderer,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		
		Component returnValue = null;
		
		if ((nodeRenderer != null) && (nodeRenderer instanceof DefaultMutableTreeNode)) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
			if(currentNode != null){
				Object userObject = currentNode.getUserObject();
				if(userObject instanceof TreeNodeModel<?>){
					TreeCellCustom c = new TreeCellCustom(tree,currentNode);
					TreeNodeModel<?> dataModel = (TreeNodeModel<?>) userObject;
	
					if(dataModel.isColumn()){
						c.checkBox.setText(dataModel+"");
						c.checkBox.setSelected(dataModel.isSelected);
						c.checkBox.setVisible(true);
						
					}else if(dataModel.isTable() || dataModel.isDatabase()){					
						c.label.setText(dataModel+"");
						c.label.setVisible(true);
						c.icon.setVisible(true);
						
						if(dataModel.isTable())
							if(leaf){
					      		c.icon.setIcon(new ImageIcon(getClass().getResource("/images/table_go.png")));
					      	}else if(expanded){
					      		c.icon.setIcon(new ImageIcon(getClass().getResource("/images/table.png")));
					      	}else{
					      		c.icon.setIcon(new ImageIcon(getClass().getResource("/images/table.png")));
					      	}
						else
							if(leaf){
					      		c.icon.setIcon(new ImageIcon(getClass().getResource("/images/database_go.png")));
					      	}else if(expanded){
					      		c.icon.setIcon(new ImageIcon(getClass().getResource("/images/database.png")));
					      	}else{
					      		c.icon.setIcon(new ImageIcon(getClass().getResource("/images/database.png")));
					      	}
				      	
						if(selected){
							c.label.setBackground(this.getBackgroundSelectionColor());
						}else{
							c.label.setBackground(this.getBackgroundNonSelectionColor());
						}
						
						if(dataModel.hasChildSelected){
							c.button.setVisible(true);
						}
						
						if(dataModel.isRunning){
							c.button.setVisible(true);
							c.button.setIcon(new ImageIcon(getClass().getResource("/images/stop.png")));
							c.buttonPause.setVisible(true);
						}
						
						if(dataModel.interruptable != null && dataModel.interruptable.suspendFlag == true){
							c.buttonPause.setIcon(new ImageIcon(getClass().getResource("/images/resume.png")));
						}
						
						if(dataModel.hasProgress){
							int dataCount = dataModel.dataObject.getCount();
							c.progressBar.setMaximum(dataCount);
							c.progressBar.setValue(dataModel.childUpgradeCount);
							c.progressBar.setVisible(true);
							c.icon.setVisible(false);
						}else if(dataModel.hasIndeterminatedProgress){
							c.loader.setVisible(true);
							c.icon.setVisible(false);
						}
						
					}
					returnValue = c;
				}
			}
		}
		if (returnValue == null) {
			returnValue = defaultRenderer.getTreeCellRendererComponent(tree, nodeRenderer, selected, expanded,
					leaf, row, hasFocus);
		}
		return returnValue;
	}
}