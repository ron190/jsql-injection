package com.jsql.mvc.view.component;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class CustomJTabbedPane extends JTabbedPane {
	public CustomJTabbedPane(boolean showMenu){
		this();
		
		if(showMenu)
			this.addMouseListener(new TabSelectionMouseHandler());
	}
	
	public CustomJTabbedPane(){
		super();
		
		this.addMouseWheelListener(new TabbedPaneMouseWheelScroller());
	}
}

class TabSelectionMouseHandler extends MouseAdapter {
	public void mouseClicked(MouseEvent e) {
		// we only look at the right button
		if(SwingUtilities.isRightMouseButton(e)) {
			JTabbedPane tabPane = (JTabbedPane)e.getSource();
			JPopupMenu menu = new JPopupMenu();
			
			int tabCount = tabPane.getTabCount();
			for(int i = 0; i < tabCount; i++) {
				menu.add(new SelectTabAction(tabPane, i));
			}
			
			menu.show(tabPane, e.getX(), e.getY());
		}
	}
}

class TabbedPaneMouseWheelScroller implements MouseWheelListener {
	public void mouseWheelMoved(MouseWheelEvent e) {
		JTabbedPane tabPane = (JTabbedPane)e.getSource();
		int dir = e.getWheelRotation();
		int selIndex = tabPane.getSelectedIndex();
		int maxIndex = tabPane.getTabCount()-1;
		if((selIndex == 0 && dir < 0) || (selIndex == maxIndex && dir > 0)) {
			selIndex = maxIndex - selIndex;
		} else {
			selIndex += dir;
		}
		if(0 <= selIndex && selIndex < tabPane.getTabCount())
			tabPane.setSelectedIndex(selIndex);
	}
}

class SelectTabAction extends AbstractAction {
	private JTabbedPane tabPane;
	private int index;
	
	public SelectTabAction(JTabbedPane tabPane, int index) {
		super(tabPane.getTitleAt(index), tabPane.getIconAt(index));
		
		this.tabPane = tabPane;
		this.index   = index;
	}
	
	public void actionPerformed(ActionEvent e) {
		tabPane.setSelectedIndex(index);
	}
}