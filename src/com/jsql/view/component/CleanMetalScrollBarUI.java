package com.jsql.view.component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollBarUI;

public class CleanMetalScrollBarUI extends MetalScrollBarUI {

	@Override
	protected JButton createDecreaseButton(int orientation) {
		return createZeroButton();
	}
	
	@Override
	protected JButton createIncreaseButton(int orientation) {
		return createZeroButton();
	}
	
	protected JButton createZeroButton() {
		JButton button = new JButton("zero button");
		Dimension zeroDim = new Dimension();
		button.setPreferredSize(zeroDim);
		button.setMinimumSize(zeroDim);
		button.setMaximumSize(zeroDim);
		return button;
	}
	
	@Override 
	protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
		
	}
	
	@Override
	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(12, 12);
	}
	
	public static ComponentUI createUI(JComponent c){
		return new CleanMetalScrollBarUI();
	}

//	private Image imageThumb, imageTrack;
//
//	public CustomMetalScrollBarUI() {
//		imageThumb = FauxImage.create(32, 32, GUITools.SELECTION_BACKGROUND);
//		imageTrack = FauxImage.create(32, 32, Color.lightGray);
//	}

//	@Override
//	protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
//		g.setColor(Color.red);
//		((Graphics2D) g).drawImage(imageThumb,
//				r.x, r.y, r.width, r.height, null);
//	}

//	private static class FauxImage {
//		static public Image create(int w, int h, Color c) {
//			BufferedImage bi = new BufferedImage(
//					w, h, BufferedImage.TYPE_INT_ARGB);
//			Graphics2D g2d = bi.createGraphics();
//			g2d.setPaint(c);
//			g2d.fillRect(0, 0, w, h);
//			g2d.dispose();
//			return bi;
//		}
//	}
}