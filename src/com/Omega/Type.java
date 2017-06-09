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
 * The type of a Texamon, defining what moves it can learn, what moves it is
 * effective or weak against, and what moves it gets bonus damage for using.
 *
 * @author Ches Burks
 *
 */
public enum Type {
	/**
	 * The glitch type, not typically assigned to anything but exists as a hacky
	 * way to you can tell there is a problem and possible easter egg.
	 */
	GLITCH("glitch"),
	/**
	 * The water type.
	 */
	WATER("water"),
	/**
	 * The plant type.
	 */
	PLANT("plant"),
	/**
	 * The default, or normal, type.
	 */
	NORMAL("normal"),
	/**
	 * The stone type.
	 */
	STONE("stone"),
	/**
	 * The fire type.
	 */
	FIRE("fire");

	private final String typeName;

	private Type(String name) {
		this.typeName = name;
	}

	/**
	 * Returns a reasonable human readable name for the type.
	 *
	 * @return The type name.
	 */
	public String getName() {
		return this.typeName;
	}
}
