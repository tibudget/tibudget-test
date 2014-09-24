package com.tibudget.tests.utils;

import java.awt.Component;

import javax.swing.JLabel;

public class InputElement implements Comparable<InputElement> {
	int order = -1;
	JLabel label;
	JLabel tooltip;
	Component component;

	public InputElement(Component component) {
		super();
		this.component = component;
	}

	@Override
	public int compareTo(InputElement o) {
		return getOrder() - o.getOrder();
	}

	public int getOrder() {
		return this.order;
	}

	public Component getComponent() {
		return this.component;
	}

	public JLabel getLabel() {
		return label;
	}

	public void setLabel(JLabel label) {
		this.label = label;
	}

	public JLabel getTooltip() {
		return tooltip;
	}

	public void setTooltip(JLabel tooltip) {
		this.tooltip = tooltip;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}