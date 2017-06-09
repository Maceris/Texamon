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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import java.util.ArrayList;

public class MovableObject {
	// direction to move
	// 0 to 360
	private int direction;
	private int theSpeed;

	// current location of object
	private float x1;
	private float y1;
	private float mWidth;
	private float mHeight;

	// amount of time on the screen
	private float theTime;

	// speed of object

	private int speedX;
	private int speedY;

	private boolean isDown;

	// list of possible images to use
	private ArrayList<Bitmap> imageList;

	// current image being displayed
	private Bitmap currentImage;

	// filename of current image being displayed
	private String currentFilename;
	int currentDrawableId;

	// list of filenames for the images (jpg, ...)
	private String[] imageFilenames;

	private Resources resources;

	/**
	 * Constructs a new movable object.
	 *
	 * @param filename the name of the file to use as the sprite
	 * @param drawableId the drawable id for the object
	 * @param x the x position
	 * @param y the y position
	 * @param objWidth the width
	 * @param objHeight the height
	 * @param res the resource to use
	 */
	public MovableObject(String filename, int drawableId, float x, float y,
		float objWidth, float objHeight, Resources res) {
		this.direction = 0; // Location.NORTH;
		this.theSpeed = 3;
		this.x1 = x;
		this.y1 = y;
		this.mWidth = objWidth;
		this.mHeight = objHeight;
		this.theTime = 0;
		this.speedX = 3;
		this.speedY = 3;
		this.imageList = null;
		this.currentImage = null;
		this.imageFilenames = null;
		this.currentDrawableId = drawableId;
		this.resources = res;
		this.setCurrentFilename(filename);
		if (this.currentImage != null) {
			this.currentImage =
				Bitmap.createScaledBitmap(this.currentImage, (int) objWidth,
					(int) objHeight, true);
		}
	}

	public boolean containsPoint(Point point) {
		if (this.getRect().contains(point)) {
			return true;
		}
		return false;
	}

	public double degreesToRadians(double degrees) {
		return degrees * Math.PI / 180;
	}

	public void draw(Canvas g) {
		// change me back
		if (this.currentImage != null) {
			g.drawBitmap(this.currentImage, this.x1, this.y1, null);
		}
	}

	public void draw(Canvas g, int width, int height) {
		// change me back
		if (this.currentImage != null) {
			g.drawBitmap(this.currentImage, new Rect(0, 0, (int) this.mWidth,
				(int) this.mHeight), new Rect((int) this.x1, (int) this.y1,
				(int) this.x1 + width, (int) this.y1 + height), null);
		}
	}

	public void draw2(Canvas g) {
		// change me back
		if (this.currentImage != null) {
			Matrix mat = new Matrix();
			mat.postRotate(this.direction + 90);
			Bitmap bMapRotate =
				Bitmap.createBitmap(this.currentImage, 0, 0,
					this.currentImage.getWidth(),
					this.currentImage.getHeight(), mat, true);

			g.drawBitmap(bMapRotate, this.x1, this.y1, null);
		}
	}

	public String getCurrentFilename() {
		return this.currentFilename;
	}

	public int getDirection() {
		return this.direction;
	}

	public boolean getDown() {
		return this.isDown;
	}

	public int getFace(MovableObject other) {
		float thisX = this.getX();
		float otherX = other.getX();
		float thisY = this.getY();
		float otherY = other.getY();
		boolean left = false;
		boolean up = false;
		boolean down = false;
		boolean right = false;
		if (thisX < otherX) {
			left = true;
		}
		else if (thisX > otherX) {
			right = true;
		}
		if (thisY < otherY) {
			up = true;
		}
		else if (thisY > otherY) {
			down = true;
		}
		if (up && !left && !right) {
			return 1;
		}
		if (down && !left && !right) {
			return 3;
		}
		if (right && !up && !down) {
			return 2;
		}
		if (left && !up && !down) {
			return 3;
		}
		return 0;

	}

	public float getHeight() {
		return this.mHeight;
	}

	public Point getPoint() {
		return new Point(this.getX(), this.getY());
	}

	public Rectangle getRect() {
		return new Rectangle(this.getX(), this.getY(), this.getWidth(),
			this.getHeight());
	}

	private Resources getResources() {
		return this.resources;
	}

	public int getSpeed() {
		return this.theSpeed;
	}

	public float getTime() {
		return this.theTime;
	}

	public float getWidth() {
		return this.mWidth;
	}

	public float getX() {
		return this.x1;
	}

	public float getY() {
		return this.y1;
	}

	public boolean intersects(MovableObject other) {
		if (this.getRect().intersects(other.getRect())) {
			return true;
		}
		return false;
	}

	public boolean intersects(Rectangle rect) {
		if (this.getRect().intersects(rect)) {
			return true;
		}
		return false;
	}

	public boolean isinsde(MovableObject other) {
		float a = this.getX();
		float b = this.getY();
		float c = this.getX() + this.getWidth();
		float d = this.getY() + this.getHeight();
		float e = other.getX();
		float f = other.getY();
		float g = other.getX() + other.getWidth();
		float h = other.getY() + other.getHeight();
		if ((a > e) && (c < g) && (b > f) && (d < h)) {
			return true;
		}
		return false;

	}

	public void moveDown() {
		this.setY(this.getY() + this.speedY);
	}

	public void moveLeft() {
		this.setX(this.getX() - this.speedX);
	}

	public void moveRight() {
		this.setX(this.getX() + this.speedX);
	}

	public void moveTo(float x, float y) {
		this.setX(x);
		this.setY(y);
	}

	// moves toward the other object using the speed
	// uses similar triangles
	public void moveTowards(MovableObject other) {
		double sideX = Math.abs(other.getX() - this.getX());
		double sideY = Math.abs(other.getY() - this.getY());
		double d = Math.sqrt(sideX * sideX + sideY * sideY);

		int speedX1 = (int) Math.round((this.getSpeed() * sideX) / d);
		int speedY1 = (int) Math.round((this.getSpeed() * sideY) / d);

		if (this.getX() < other.getX()) {
			this.setX(this.getX() + speedX1);
		}
		else if (this.getX() > other.getX()) {
			this.setX(this.getX() - speedX1);
		}

		if (this.getY() < other.getY()) {
			this.setY(this.getY() + speedY1);
		}
		else if (this.getY() > other.getY()) {
			this.setY(this.getY() - speedY1);
		}

	}

	public void moveUp() {
		this.setY(this.getY() - this.speedY);
	}

	public void setCurrentFilename(String filename) {
		this.currentFilename = filename;
		this.setImage();
	}

	public void setCurrentFilenamePosition(int position) {
		if (this.imageFilenames == null) {
			return;
		}
		if (position < this.imageFilenames.length && position > -1) {
			this.setCurrentFilename(this.imageFilenames[position]);
			this.setImage();
		}
		else {
			this.setCurrentFilename("");
			this.currentImage = null;
		}
	}

	public void setDirection(int direction1) {
		this.direction = direction1;
		this.direction = this.direction % 360;
	}

	public void setDownFalse() {
		this.isDown = false;
	}

	public void setDownTrue() {
		this.isDown = true;
	}

	public void setHeight(float height1) {
		this.mHeight = height1;
		this.setImage();
	}

	private void setImage() {
		if (this.getCurrentFilename() != null
			&& !this.getCurrentFilename().equals("")) {
			try {
				// change me back
				this.currentImage =
					BitmapFactory.decodeResource(this.getResources(),
						this.currentDrawableId);
				this.currentImage =
					Bitmap.createScaledBitmap(this.currentImage,
						(int) this.mWidth, (int) this.mHeight, false);

				// System.out.println("toolkit ok");
			}
			catch (Exception e) {
				this.currentImage = null;
				this.setCurrentFilename("");
				// System.out.println("error getImage with toolkit");
			}
		}

	}

	public void setImageDID(int drawabd) {
		this.currentDrawableId = drawabd;
		this.setImage();
	}

	public void setImageFilenames(String[] filenames) {
		this.imageFilenames = filenames;
		this.imageList = new ArrayList<Bitmap>();
		for (int i = 0; i < filenames.length; i++) {
			try {
				// change me back
				Bitmap pic =
					BitmapFactory.decodeResource(this.getResources(),
						this.currentDrawableId);
				pic =
					Bitmap.createScaledBitmap(pic, (int) this.mWidth,
						(int) this.mHeight, false);
				this.imageList.add(pic);
			}
			catch (Exception e) {
			}
		}
		if (filenames.length > 0) {
			this.setCurrentFilename(filenames[0]);
			this.setImage();
		}
	}

	public void setSpeed(int speed) {
		this.theSpeed = speed;
	}

	public void setTime(float time) {
		this.theTime = time;
	}

	public void setWidth(float width1) {
		this.mWidth = width1;
		this.setImage();
	}

	public void setX(float x) {
		this.x1 = x;
	}

	public void setY(float y) {
		this.y1 = y;
	}

	public void turnRight() {
		this.setDirection(this.getDirection() + 90);
	}

} // end of class MovableObject