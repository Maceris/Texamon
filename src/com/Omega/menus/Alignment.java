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
package com.Omega.menus;

/**
 * The area of a parent window or screen that a window should call the origin
 * with respect to displacement. Centered alignments ignore displacement in
 * whatever axis/axes they are centered on.
 *
 * @author Ches Burks
 *
 */
public enum Alignment {
	/**
	 * Aligned to the north of the parent, centered horizontally.
	 */
	NORTH,
	/**
	 * Aligned to the east of the parent, centered vertically.
	 */
	EAST,
	/**
	 * Aligned to the south of the parent, centered horizontally.
	 */
	SOUTH,
	/**
	 * Aligned to the west of the parent, centered vertically.
	 */
	WEST,
	/**
	 * Aligned to the north-east corner of the parent.
	 */
	NORTH_EAST,
	/**
	 * Aligned to the north-west corner of the parent.
	 */
	NORTH_WEST,
	/**
	 * Aligned to the south-east corner of the parent.
	 */
	SOUTH_EAST,
	/**
	 * Aligned to the south-west corner of the parent.
	 */
	SOUTH_WEST,
	/**
	 * Aligned in the center of the parent.
	 */
	CENTER;
}
