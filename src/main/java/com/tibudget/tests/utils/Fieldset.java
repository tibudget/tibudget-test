package com.tibudget.tests.utils;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class Fieldset implements Comparable<Fieldset> {
	List<InputElement> elements;
	JComponent separator;
	boolean displayed;
	String name;

	public Fieldset(String name) {
		super();
		this.name = name;
		this.displayed = true;
		this.elements = new ArrayList<InputElement>();
	}

	@Override
	public int compareTo(Fieldset o) {
		return o != null ? getMinOrder() - o.getMinOrder() : -1;
	}

	@Override
	public int hashCode() {
		return this.name != null ? this.name.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this.name != null && obj != null && obj instanceof Fieldset) {
			return this.name.equals(((Fieldset) obj).getName());
		}
		return super.equals(obj);
	}

	public List<InputElement> getElements() {
		return this.elements;
	}

	public int getMinOrder() {
		int min = Integer.MAX_VALUE;
		if (this.elements != null) {
			for (InputElement element : this.elements) {
				min = Math.min(min, element.order);
			}
		}
		return min;
	}

	public String getName() {
		return this.name;
	}

	public boolean isDisplayed() {
		return this.displayed;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}

	public JComponent getSeparator() {
		return separator;
	}

	public void setSeparator(JComponent separator) {
		this.separator = separator;
	}
}