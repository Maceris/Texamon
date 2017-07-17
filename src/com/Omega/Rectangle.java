package com.Omega;

@Deprecated
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