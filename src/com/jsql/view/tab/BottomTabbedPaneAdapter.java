package com.jsql.view.tab;

import javax.swing.ImageIcon;

import com.jsql.view.GUIMediator;
import com.jsql.view.component.JScrollPanePixelBorder;

@SuppressWarnings("serial")
public class BottomTabbedPaneAdapter extends MouseTabbedPane {
	
	public void insertChunkTab(){
		GUIMediator.bottom().insertTab(
    		"Chunk", 
    		new ImageIcon(getClass().getResource("/com/jsql/view/images/chunk.gif")), 
    		new JScrollPanePixelBorder(1,1,0,0,GUIMediator.gui().chunks), 
    		"Hexadecimal data recovered",
    		1
		);
	}
	
	public void insertBinaryTab(){
		this.insertTab(
			"Binary", 
			new ImageIcon(getClass().getResource("/com/jsql/view/images/binary.gif")), 
			new JScrollPanePixelBorder(1,1,0,0,GUIMediator.gui().binaryArea), 
			"Time/Blind bytes", 
			1 + (GUIMediator.menubar().chunk.isSelected() ? 1 : 0)
		);
	}
	
	public void insertNetworkTab(){
		this.insertTab(
    		"Network", 
    		new ImageIcon(getClass().getResource("/com/jsql/view/images/header.gif")), 
    		GUIMediator.gui().network, 
    		"URL calls information", 
    		GUIMediator.bottom().getTabCount() - (GUIMediator.menubar().javaDebug.isSelected() ? 1 : 0) 
		);
	}
	
	public void insertJavaDebugTab(){
		GUIMediator.bottom().insertTab(
    		"Java", 
    		new ImageIcon(getClass().getResource("/com/jsql/view/images/cup.png")), 
    		new JScrollPanePixelBorder(1,1,0,0,GUIMediator.gui().javaDebug), 
    		"Java console", 
    		GUIMediator.bottom().getTabCount()
		);
	}
	
}