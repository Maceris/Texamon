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

public class Rectangle {
	private float xPos;
	private float yPos;
	private float w;
	private float h;

	public Rectangle(float x, float y, float width, float height) {
		this.xPos = x;
		this.yPos = y;
		this.w = width;
		this.h = height;
	}

	public boolean contains(float x, float y) {
		if (this.isInsideOf(x, y, this)) {
			return true;
		}
		return false;
	}

	public boolean contains(Point point) {
		if (this.isInsideOf(point.x, point.y, this)) {
			return true;
		}
		return false;
	}

	public boolean intersects(Rectangle other) {
		// see if this Rectangle intersects other
		if (this.isInsideOf(this.xPos, this.yPos, other)) {
			return true;
		}
		if (this.isInsideOf(this.xPos + this.w, this.yPos, other)) {
			return true;
		}
		if (this.isInsideOf(this.xPos, this.yPos, other)) {
			return true;
		}
		if (this.isInsideOf(this.xPos + this.w, this.yPos + this.w, other)) {
			return true;
		}
		return false;
	}

	public boolean isInsideOf(float x, float y, Rectangle other) {
		if (x >= other.xPos && x <= other.xPos + other.w && y >= other.yPos
			&& y <= other.yPos + other.h) {
			return true;
		}
		return false;
	}

}