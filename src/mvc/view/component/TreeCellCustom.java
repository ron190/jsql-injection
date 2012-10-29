package mvc.view.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

class NodeImageObserver implements ImageObserver {
	JTree tree;
	DefaultTreeModel model;
	TreeNode node;

	NodeImageObserver(JTree tree, TreeNode node) {
		this.tree = tree;
		this.model = (DefaultTreeModel) tree.getModel();
		this.node = node;
	}

	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
		if ((flags & (FRAMEBITS | ALLBITS)) != 0) {
			TreePath path = new TreePath(model.getPathToRoot(node));
			Rectangle rect = tree.getPathBounds(path);
			if (rect != null) {
				tree.repaint(rect);
			}
		}
		return (flags & (ALLBITS | ABORT)) == 0;
	}
}

public class TreeCellCustom extends JPanel {	
	private static final long serialVersionUID = -5833890081484609705L;
	
	private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
	
	public JLabel icon = new JLabel();
	public JLabel label = new JLabel();
	public JLabel loader = new JLabel();
	public JCheckBox checkBox = new JCheckBox();
	public JButton button = new JButton(new ImageIcon(getClass().getResource("/images/play.png")));
	public JButton buttonPause = new JButton(new ImageIcon(getClass().getResource("/images/pause.png")));
	public JProgressBar progressBar = new JProgressBar();
	
	public TreeCellCustom(JTree tree, TreeNode currentNode){
		    checkBox.setFont( new Font(checkBox.getFont().getName(),Font.PLAIN|Font.ITALIC,checkBox.getFont().getSize()) );
		       label.setFont( new Font(label.getFont().getName(),Font.PLAIN,label.getFont().getSize()) );
	 	      button.setFont( new Font(button.getFont().getName(),Font.PLAIN,button.getFont().getSize()) );
	 	 buttonPause.setFont( new Font(buttonPause.getFont().getName(),Font.PLAIN,buttonPause.getFont().getSize()) );
	 	 
	 	ImageIcon iconLoader = new ImageIcon(getClass().getResource("/images/loader.gif"));
	 	iconLoader.setImageObserver(new NodeImageObserver(tree, currentNode));
	 	loader.setIcon(iconLoader);

		progressBar.setPreferredSize(new Dimension(16, 9));
		progressBar.setBorder(BorderFactory.createLineBorder(new Color(56,164,47)));
		progressBar.setForeground(new Color(158,210,152));
		progressBar.setBackground(Color.WHITE);
		progressBar.setUI(new BasicProgressBarUI());
		label.setOpaque(true);

		label.setBorder(new RoundedCornerBorder(6,2,false));
		button.setBorder(new RoundedCornerBorder(0,0,true));
		buttonPause.setBorder(new RoundedCornerBorder(0,0,true));
		
	    checkBox.setBackground(defaultRenderer.getBackground());
		this.setBackground(defaultRenderer.getBackground());
		
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
		this.add(icon);
		this.add(loader);
		this.add(progressBar);
		this.add(label);
		this.add(checkBox);
		this.add(button);
		this.add(buttonPause);
		
		progressBar.setVisible(false);
		   checkBox.setVisible(false);
			 button.setVisible(false);
		buttonPause.setVisible(false);
			 loader.setVisible(false);
			  label.setVisible(false);
			   icon.setVisible(false);
	}
}
