/*
 * Copyright (c) 2018 youcef debbah (youcef-kun@hotmail.fr)
 *
 * This file is part of cervex.
 *
 * cervex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cervex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cervex.  If not, see <http://www.gnu.org/licenses/>.
 *
 * created on 2018/12/08
 * @header
 */

package com.rhcloud.cervex_jsoftware95.util;

/**
 * This class contains a attributes that are needed to construct a personalized
 * string representation of data structure.
 * 
 * @author youcef debbah
 * @since 1.8
 * 
 */

public class Style {

	/**
	 * Some standard style that can be used to initialize a style object.
	 * <p>
	 * for example:
	 * 
	 * <pre>
	 * Style myStyle = new Style(WellDefined.EMPTY);
	 * </pre>
	 *
	 */

	public enum WellDefined {
		TRACE, DEFAULT, EMPTY
	}

	public enum Type {
		ARRAY, ITERABLE, MAP, ENUMERATION
	}

	public enum Token {
		SEPARATOR, PREFIX, SUFFIX
	}

	private String[][] attributes = new String[Type.values().length][Token.values().length];
	private String mapkeyValueSeparator;
	private String nullValue;
	private boolean labeled;

	public Style() {
		this(WellDefined.DEFAULT);
	}

	public Style(WellDefined type) {
		switch (type) {
		case TRACE:
			setAttribute(Token.PREFIX, "{");
			setAttribute(Token.SEPARATOR, ", ");
			setAttribute(Token.SUFFIX, "}");
			setMapkeyValueSeparator("->");
			setLabeled(true);
			break;
		case DEFAULT:
			setAttribute(Token.PREFIX, "");
			setAttribute(Token.SEPARATOR, ", ");
			setAttribute(Token.SUFFIX, "");
			setMapkeyValueSeparator(": ");
			setLabeled(false);
			break;
		case EMPTY:
			setAttribute("");
			setMapkeyValueSeparator("");
			setLabeled(false);
			break;
		default:
			assert true;
		}
	}

	public void setAttribute(Type type, Token token, String attribute) {
		attributes[type.ordinal()][token.ordinal()] = attribute;
	}

	public void setAttribute(Type type, String attribute) {
		for (int i = 0; i < Token.values().length; i++) {
			attributes[type.ordinal()][i] = attribute;
		}
	}

	public void setAttribute(Token token, String attribute) {
		for (int i = 0; i < Type.values().length; i++) {
			attributes[i][token.ordinal()] = attribute;
		}
	}

	public void setAttribute(String attribute) {
		for (int i = 0; i < Type.values().length; i++) {
			for (int j = 0; j < Token.values().length; j++) {
				attributes[i][j] = attribute;
			}
		}
	}

	public String getAttribute(Type type, Token token) {
		return attributes[type.ordinal()][token.ordinal()];
	}

	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	public String getNullValue() {
		return nullValue;
	}

	/**
	 * Returns the string that separate map key from map value.
	 * 
	 * @return map key/map value separator.
	 */

	public String getMapkeyValueSeparator() {
		return mapkeyValueSeparator;
	}

	/**
	 * Sets the string that separate map key from map value.
	 * 
	 * @param mapkeyValueSeparator
	 *            The new map key/map value separator.
	 */

	public void setMapkeyValueSeparator(String mapkeyValueSeparator) {
		this.mapkeyValueSeparator = mapkeyValueSeparator;
	}

	/**
	 * Set whatever prefix the result string with the class name.
	 * 
	 * @param labeled
	 *            add class name or not
	 */
	public void setLabeled(boolean labeled) {
		this.labeled = labeled;
	}

	/**
	 * Returns true if you should add class name at the start of result string
	 * 
	 * @return whatever you should add class name at the start of result string
	 */

	public boolean isLabeled() {
		return labeled;
	}
}
