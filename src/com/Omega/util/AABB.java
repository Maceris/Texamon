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
package com.Omega.util;

/**
 * A 2d Axis Aligned Bounding Box for various purposes. These are not
 * threadsafe.
 *
 * @author Ches Burks
 *
 */
public class AABB {
	public double xMin;
	public double yMin;
	public double xMax;
	public double yMax;

	public AABB(final AABB other) {
		this.xMin = other.xMin;
		this.yMin = other.yMin;
		this.xMax = other.xMax;
		this.yMax = other.yMax;
	}

	/**
	 * Construct a new AABB with the given values.
	 *
	 * @param xmin The minimum x value
	 * @param ymin The minimum y value
	 * @param xmax The maximum x value
	 * @param ymax The maximum y value
	 */
	public AABB(final double xmin, final double ymin, final double xmax,
			final double ymax) {
		this.xMin = xmin;
		this.yMin = ymin;
		this.xMax = xmax;
		this.yMax = ymax;
	}

	/**
	 * Contracts the AABB by the given amount in both directions.
	 *
	 * @param x Distance to contract x by
	 * @param y Distance to contract y by
	 *
	 * @return The same AABB
	 */
	public AABB contract(double x, double y) {
		this.xMin += x;
		this.yMin += y;
		this.xMax -= x;
		this.yMax -= y;
		return this;
	}

	/**
	 * Copy the values from the other AABB to this AABB.
	 *
	 * @param other The AABB to copy values from
	 */
	public void copyValues(AABB other) {
		this.xMin = other.xMin;
		this.yMin = other.yMin;
		this.xMax = other.xMax;
		this.yMax = other.yMax;
	}

	/**
	 * Expands the AABB by the given amount in both directions.
	 *
	 * @param x Distance to expand x by
	 * @param y Distance to expand y by
	 *
	 * @return The same AABB
	 */
	public AABB expand(double x, double y) {
		this.xMin -= x;
		this.yMin -= y;
		this.xMax += x;
		this.yMax += y;
		return this;
	}

	/**
	 * Returns true if the two AABB's intersect, false otherwise. They may share
	 * edges.
	 *
	 * @param other The other AABB to test collision with
	 *
	 * @return True if they intersect, false otherwise
	 */
	public boolean intersectsWith(AABB other) {
		return other.xMax >= this.xMin && other.xMin <= this.xMax ? other.yMax >= this.yMin
				&& other.yMin <= this.yMax
				: false;
	}

	/**
	 * Returns true if this is inside the other AABB, false otherwise. They may
	 * share edges.
	 *
	 * @param other The other AABB to test collision with
	 *
	 * @return True if this is completely inside the other, false otherwise
	 */
	public boolean isInside(AABB other) {
		return other.xMax >= this.xMax && other.xMin <= this.xMin ? other.yMax >= this.yMax
				&& other.yMin <= this.yMin
				: false;
	}

	/**
	 * Sets the given values.
	 *
	 * @param xmin The minimum x value
	 * @param ymin The minimum y value
	 * @param xmax The maximum x value
	 * @param ymax The maximum y value
	 * @return itself
	 */
	public AABB setBounds(double xmin, double ymin, double xmax, double ymax) {
		this.xMin = xmin;
		this.yMin = ymin;
		this.xMax = xmax;
		this.yMax = ymax;
		return this;
	}
}
