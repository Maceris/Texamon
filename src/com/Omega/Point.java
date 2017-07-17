/*******************************************************************************
 * Copyright (C) 2016, 2017 David Burks
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
 *
 * A 2d point with float values
 *
 * @author Ches Burks
 *
 */
public class Point {
	/**
	 * The x value
	 */
	public float x;
	/**
	 * The y value
	 */
	public float y;

	/**
	 * Constructs a new Point with x and y initialized to 0.
	 */
	public Point() {
		this.x = 0;
		this.y = 0;
	}

	/**
	 * Constructs a new Point with given initial values
	 *
	 * @param x2 the initial x value
	 * @param y2 the initial y value
	 */
	public Point(final float x2, final float y2) {
		this.x = x2;
		this.y = y2;
	}

	/**
	 * Sets the x and y values
	 *
	 * @param x2 the new x value
	 * @param y2 the new y value
	 */
	public void set(final float x2, final float y2) {
		this.x = x2;
		this.y = y2;
	}

}