/*******************************************************************************
 * Copyright (C) 2016 David Burks
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.Omega;

/**
 * Something that can be stored in and loaded from a file.
 *
 * @author Ches Burks
 *
 */
public interface Saveable {

	/**
	 * The beginning of a nested object.
	 */
	public static final String NEST_BEGIN = "{";
	/**
	 * The end of a nested object.
	 */
	public static final String NEST_END = "}";
	/**
	 * Splits different parts.
	 */
	public static final String DELIMITER = "/";
	/**
	 * Used to escape characters.
	 */
	public static final String ESCAPE = "+";

	/**
	 * Creates an object based on the given text.
	 *
	 * @param text The text to convert from.
	 */
	public void fromText(final String text);
}
