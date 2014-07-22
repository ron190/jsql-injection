package com.jsql.view.panel;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class ButtonAddressBar extends JButton{
	public ButtonAddressBar() {
		this.setPreferredSize(new Dimension(18,16));
		this.setBorder(null);
		this.setOpaque(false);
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);
		this.setRolloverEnabled(true); // turn on before rollovers work
		this.setIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/arrowDefault.png")));
		this.setRolloverIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/arrowRollover.png")));
		this.setPressedIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/arrowPressed.png")));
	}

	public String state = "Connect";
	
	public void setInjectionReady(){
		state = "Connect";
		this.setEnabled(true);
		this.setRolloverEnabled(true); // turn on before rollovers work
		this.setIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/arrowDefault.png")));
		this.setRolloverIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/arrowRollover.png")));
		this.setPressedIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/arrowPressed.png")));
	}
	
	public void setInjectionRunning(){
		state = "Stop";
		this.setEnabled(true);
		this.setRolloverEnabled(true); // turn on before rollovers work
		this.setIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/stopDefault.png")));
		this.setRolloverIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/stopRollover.png")));
		this.setPressedIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/stopPressed.png")));
	}
	
	public void setInjectionStopping(){
		state = "Stopping...";
		this.setRolloverEnabled(false); // turn on before rollovers work
		this.setIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/spinner.gif")));
		this.setEnabled(false);
	}
}
