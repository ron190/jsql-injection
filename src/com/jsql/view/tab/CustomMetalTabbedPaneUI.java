package com.jsql.view.tab;

import java.awt.Graphics;

import javax.swing.plaf.metal.MetalTabbedPaneUI;

public class CustomMetalTabbedPaneUI extends MetalTabbedPaneUI {
	@Override protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {}
	@Override protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {}
	@Override protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {}
	@Override protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {}
	@Override protected int calculateMaxTabHeight(int tabPlacement) { return 22; }
}
