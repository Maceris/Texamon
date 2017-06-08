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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import com.Omega.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A window on the screen. These can nest and are aligned, sized, and positioned
 * relative to the parent (another window, or the whole canvas for top level
 * windows).
 *
 * @author Ches Burks
 *
 */
public class IkWindow {

	/**
	 * The style to use if a window does not have a style specifically set.
	 */
	private static WindowStyle defaultStyle =
		new WindowStyle(WindowStyle.defStyle, WindowStyle.defBorder,
			WindowStyle.defColorScheme);

	/**
	 * Returns an actual reference to the window default style. This should not
	 * be modified without proper thread safety in mind as it could cause
	 * unpredictable results otherwise.
	 *
	 * @return the current default window style object
	 */
	protected static WindowStyle getDefaultStyle() {
		return IkWindow.defaultStyle;
	}

	/**
	 * Returns a copy of the current default window style. Modifying the
	 * returned object will not change the actual style of windows as that must
	 * be done via the {@link #setDefaultStyle(WindowStyle)} method.
	 *
	 * @return a deep clone of the window style object, or null if there is none
	 */
	public static WindowStyle getDefaultStyleClone() {
		WindowStyle clone;
		if (IkWindow.defaultStyle == null) {
			/*
			 * Prevents null pointer exceptions from accessing data members
			 */
			clone = null;
		}
		else {
			synchronized (IkWindow.defaultStyle) {
				clone =
					new WindowStyle(IkWindow.defaultStyle.style,
						IkWindow.defaultStyle.border,
						IkWindow.defaultStyle.colorScheme);
			}
		}
		return clone;
	}

	/**
	 * Sets the current (static for all windows) default window style. This is
	 * synchronized. Windows with styles set will use those over the default.
	 *
	 * @param newStyle the style to use for windows
	 */
	public static void setDefaultStyle(WindowStyle newStyle) {
		synchronized (IkWindow.defaultStyle) {
			synchronized (newStyle) {
				IkWindow.defaultStyle.border = newStyle.border;
				IkWindow.defaultStyle.colorScheme = newStyle.colorScheme;
				IkWindow.defaultStyle.style = newStyle.style;
			}
		}
	}

	/**
	 * The style to use in drawing the window. If it is null, the default style
	 * is used. If not null, it overrides the default.
	 */
	private WindowStyle currentStyle;

	/**
	 * How far from the alignment edge the window should be placed. The x and y
	 * values are floats representing the percentage of the parent's respective
	 * width or height to be shifted by. These should be between 0.0f and 1.0f,
	 * otherwise it will be very glitchy unless you really know what you are
	 * doing.
	 */
	protected Point localDisplace;

	/**
	 * The displacement from the parent as measured from the northwest corner of
	 * this window. This is used for calculating real screen coordinates, and
	 * recalculated whenever the window is changed or dirtied.
	 */
	private Point localDisplaceNW;

	/**
	 * The real screen displacement of the window, not local. If drawn on a
	 * canvas, this would be the displacement to properly place the window on
	 * the canvas using the canvas dimensions.
	 */
	private Point realDisplacement;

	/**
	 * The true screen width of the window. This is a percentage of the canvas
	 * this is drawn on. Should be positive and <= 1.0f, where 1 is the entire
	 * canvas. X represents width, and Y represents height.
	 */
	private Point realScale;

	/**
	 * Where in the parent should this window snap to, or have displacement
	 * measured from
	 */
	protected Alignment align;

	/**
	 * The percent of the parent window's width to take up. Should be positive
	 * and <= 1.0f, where 1 is the entire window. X represents width, and Y
	 * represents height.
	 */
	protected Point scale;

	/**
	 * The parent window, or null if there is no parent window. A null parent
	 * means this window is a root.
	 */
	protected IkWindow parent;

	/**
	 * True if the window has been changed and needs to be recalculated
	 */
	protected boolean dirty;

	/**
	 * Whether or not it should be drawn on the screen
	 */
	protected boolean visible;

	/**
	 * The paint to use for drawing the body of this window
	 */
	private Paint mainPaint;

	/**
	 * The paint to use for drawing the border of this window
	 */
	private Paint borderPaint;

	/**
	 * A list of sub-windows this window contains
	 */
	private ArrayList<IkWindow> children;

	/**
	 * Called when the window is interacted with. Can reference data in the
	 * windows state object.
	 */
	private IWindowCallback callbackFunc;

	/**
	 * Contains references to outside objects required by the callback function.
	 */
	private Map<Object, Object> stateObj;

	private boolean beingDestroyed;

	/**
	 * Creates a default window and initializes internal variables.
	 */
	public IkWindow() {
		this.visible = true;
		this.localDisplace = new Point();
		this.localDisplaceNW = new Point();
		this.realDisplacement = new Point();
		this.realScale = new Point();
		this.align = Alignment.NORTH_WEST;
		this.scale = new Point(0.0f, 0.0f);
		this.mainPaint = new Paint();
		this.borderPaint = new Paint();
		this.mainPaint.setColor(Color.WHITE);
		this.mainPaint.setStyle(Style.FILL);
		this.borderPaint.setColor(Color.LTGRAY);
		this.borderPaint.setStyle(Style.STROKE);
		this.borderPaint.setStrokeCap(Cap.SQUARE);
		this.borderPaint.setStrokeJoin(Join.MITER);
		this.borderPaint.setStrokeWidth(4);
		this.children = new ArrayList<IkWindow>();
		this.callbackFunc = null;
		this.beingDestroyed = false;
		this.dirty();
	}

	/**
	 * Constructs a new Window with displacement, alignment, and size as given.
	 * These are Points with x and y values given as floats representing decimal
	 * percentage of the parent window's size.
	 *
	 * @param displacement the percent of the parent to displace away from
	 *            whatever edge it is aligned to
	 * @param alignment what part of the parent should the displacement be
	 *            measured from
	 * @param widthAndHeight the width and height of this window as a percent of
	 *            the parents respective width and height
	 */
	public IkWindow(final Point displacement, final Alignment alignment,
		final Point widthAndHeight) {
		this();
		this.localDisplace.set(displacement.x, displacement.y);
		this.align = alignment;
		this.scale.x = widthAndHeight.x;
		this.scale.y = widthAndHeight.y;
	}

	/**
	 * Adds a child to the window, sets the child to have this as a parent, and
	 * then dirties this window (and thus children).
	 *
	 * @param item the child to add to the window
	 */
	public synchronized void addChild(IkWindow item) {
		this.children.add(item);
		if (item.parent != this) {
			item.setParent(this);
		}
		this.dirty();
	}

	/**
	 * Returns true if this window contains the given point, given a certain
	 * screen width and height.
	 *
	 * @param scrWidth the width of the screen/canvas the window is in
	 * @param scrHeight the height of the screen/canvas the window is in
	 * @param p the point to check for
	 * @return true if the window contains the point, otherwise false
	 */
	public synchronized boolean containsPoint(int scrWidth, int scrHeight,
		Point p) {
		RectF actualRect =
			new RectF(this.getActualDisplaceX() * scrWidth,
				this.getActualDisplaceY() * scrHeight,
				(this.getActualDisplaceX() + this.getActualWidth()) * scrWidth,
				(this.getActualDisplaceY() + this.getActualHeight())
					* scrHeight);
		return actualRect.contains(p.x, p.y);
	}

	/**
	 * Destroys self and children, orphans itself, and cleans up references in
	 * the state object.
	 */
	public synchronized void destroy() {
		if (this.beingDestroyed) { // to prevent cycles
			return;
		}
		this.beingDestroyed = true;
		for (IkWindow item : this.children) {
			item.destroy();
		}
		this.children.clear();

		this.orphanSelf();

		// clean up references
		this.stateObj.clear();
		this.callbackFunc = null;
		this.stateObj = null;

		this.beingDestroyed = false;
	}

	/**
	 * Sets the dirty flag for this window and also calls dirty on all children.
	 * This signifies the window needs to be recalculated.
	 */
	public synchronized void dirty() {
		this.dirty = true;
		for (IkWindow item : this.children) {
			item.dirty();
		}
	}

	/**
	 * Draws the window on the given canvas. Also draws all children (on top)
	 * recursively.
	 *
	 * @param c the canvas to draw on
	 */
	public synchronized void draw(Canvas c) {
		if (this.dirty) {
			this.recalculate();
		}
		if (!this.visible) {
			return;
		}
		WindowDrawer.drawWindow(c, this, this.getStyle());
		for (IkWindow item : this.children) {
			item.draw(c);
		}
	}

	/**
	 * Actually calls/runs the callbacks action method, if this window has one.
	 */
	public void executeAction() {
		if (this.callbackFunc != null) {
			this.callbackFunc.action(getStateObject());
		}
	}

	/**
	 * Returns the actual displacement value of the window as it would be used
	 * on a canvas as a decimal percentage of the whole, from the left side of
	 * the window.
	 *
	 * @return the width, which should be >=0 and <=1.0 but is not guaranteed to
	 *         be
	 */
	public synchronized float getActualDisplaceX() {
		if (this.dirty) {
			this.recalculate();
		}
		return this.realDisplacement.x;
	}

	/**
	 * Returns the actual displacement value of the window as it would be used
	 * on a canvas as a decimal percentage of the whole, from the top of the
	 * window.
	 *
	 * @return the width, which should be >=0 and <=1.0 but is not guaranteed to
	 *         be
	 */
	public synchronized float getActualDisplaceY() {
		if (this.dirty) {
			this.recalculate();
		}
		return this.realDisplacement.y;
	}

	/**
	 * Returns the actual scale value of the window as it would be used on a
	 * canvas.
	 *
	 * @return the width, which should be >=0 and <=1.0 but is not guaranteed to
	 *         be
	 */
	public synchronized float getActualHeight() {
		if (this.dirty) {
			this.recalculate();
		}
		return this.realScale.y;
	}

	/**
	 * Returns the actual scale value of the window as it would be used on a
	 * canvas.
	 *
	 * @return the width, which should be >=0 and <=1.0 but is not guaranteed to
	 *         be
	 */
	public synchronized float getActualWidth() {
		if (this.dirty) {
			this.recalculate();
		}
		return this.realScale.x;
	}

	/**
	 * Returns a rectangle representing the actual size and location of this
	 * window should it be mapped onto the given canvas, using actual pixels.
	 *
	 * @param c the canvas to map values to
	 * @return the rectangle representing where this would be located on the
	 *         canvas
	 */
	public synchronized Rect getBoundingRect(Canvas c) {
		if (this.dirty) {
			this.recalculate();
		}
		Rect bounds = new Rect();
		float x = this.getActualDisplaceX();
		float y = this.getActualDisplaceY();
		float w = this.getActualWidth();
		float h = this.getActualHeight();
		bounds.set((int) (x * c.getWidth()), (int) (y * c.getHeight()),
			(int) ((x + w) * c.getWidth()), (int) ((y + h) * c.getHeight()));
		return bounds;
	}

	/**
	 * Returns the number of children belonging to this window.
	 *
	 * @return the size of the children list
	 */
	public synchronized int getChildCount() {
		return this.children.size();
	}

	/**
	 * Returns the float value representing the decimal percentage of the parent
	 * window's height that this window takes up.
	 *
	 * @return the local height
	 */
	public synchronized float getLocalHeight() {
		return this.scale.y;
	}

	/**
	 * Returns the float value representing the decimal percentage of the parent
	 * window's width that this window takes up.
	 *
	 * @return the local width
	 */
	public synchronized float getLocalWidth() {
		return this.scale.x;
	}

	/**
	 * Returns the parents actual displacement, which is determined by recursing
	 * up the tree of windows. If the parent is null (that is, this is a root
	 * window), returns 0.0f so as to not change calculations (it measures from
	 * (0,0)).
	 *
	 * @return the parents actual displace percentage, or 0 if there is no
	 *         parent
	 */
	private synchronized float getParentRealDisplaceX() {
		if (this.parent == null) {
			return 0.0f; // so calculations aren't affected
		}
		return this.parent.getActualDisplaceX();
	}

	/**
	 * Returns the parents actual displacement, which is determined by recursing
	 * up the tree of windows. If the parent is null (that is, this is a root
	 * window), returns 0.0f so as to not change calculations (it measures from
	 * (0,0)).
	 *
	 * @return the parents actual displace percentage, or 0 if there is no
	 *         parent
	 */
	private synchronized float getParentRealDisplaceY() {
		if (this.parent == null) {
			return 0.0f; // so calculations aren't affected
		}
		return this.parent.getActualDisplaceY();
	}

	/**
	 * Returns the parents actual height, which is determined by recursing up
	 * the tree of windows. If the parent is null (that is, this is a root
	 * window), returns 1.0f so as to not change calculations.
	 *
	 * @return the parents actual height percentage, or 1 if there is no parent
	 */
	private synchronized float getParentRealHeight() {
		if (this.parent == null) {
			return 1.0f;// so calculations aren't affected
		}
		return this.parent.getActualHeight();
	}

	/**
	 * Returns the parents actual width, which is determined by recursing up the
	 * tree of windows. If the parent is null (that is, this is a root window),
	 * returns 1.0f so as to not change calculations.
	 *
	 * @return the parents actual width percentage, or 1 if there is no parent
	 */
	private synchronized float getParentRealWidth() {
		if (this.parent == null) {
			return 1.0f;// so calculations aren't affected
		}
		return this.parent.getActualWidth();
	}

	/**
	 * Returns the state object for this window. This is used to store named
	 * references to objects that are useful to the window callback. It is left
	 * generic so any key value pair system may be used, but it is suggested to
	 * map meaningful strings to object references.
	 *
	 * @return the state object for the window, or null if none exists.
	 */
	protected synchronized Map<Object, Object> getStateObject() {
		return this.stateObj;
	}

	/**
	 * Returns an actual reference to the current window style. If one does not
	 * exist, the default style is returned instead. This should not be modified
	 * without proper thread safety in mind as it could cause unpredictable
	 * results otherwise.
	 *
	 * @return the current window style object, or if null, the default style
	 */
	protected WindowStyle getStyle() {
		if (this.currentStyle == null) {
			return IkWindow.getDefaultStyle();
		}
		return this.currentStyle;
	}

	/**
	 * Returns a clone of the current style. If there is no current style (it is
	 * null), then returns null. Modifying the returned object will not change
	 * the actual style of windows as that must be done via the
	 * {@link #setStyle(WindowStyle)} method.
	 *
	 * @return the a copy of the current style or null if there is none
	 */
	public WindowStyle getStyleClone() {
		WindowStyle clone;
		if (this.currentStyle == null) {
			clone = null;
		}
		else {
			synchronized (this.currentStyle) {
				clone =
					new WindowStyle(this.currentStyle.style,
						this.currentStyle.border, this.currentStyle.colorScheme);
			}
		}
		return clone;
	}

	/**
	 * Returns true if this window has a callback function (that is, the
	 * callback object is not null)
	 *
	 * @return true if this window has a callback method assigned
	 */
	public boolean hasCallback() {
		return this.callbackFunc != null;
	}

	/**
	 * Returns true if this window has the given child as one of its children
	 *
	 * @param child the child to look for
	 * @return true if this window is the child's direct parent, false otherwise
	 */
	public synchronized boolean hasChild(final IkWindow child) {
		return this.children.contains(child);
	}

	/**
	 * Returns true if there is a state object for this window. That is, if
	 * {@link #getStateObject()} will return a non-null value. It does not
	 * ensure that the map is populated.
	 *
	 * @return true if there is a state object, false if the state object is
	 *         null.
	 */
	public synchronized boolean hasStateObject() {
		return this.stateObj != null;
	}

	/**
	 * Returns the internal index of the given child.
	 *
	 * @param i the item to look for
	 * @return the index of the item or -1 if it is not found
	 */
	public synchronized int indexOf(MenuItem i) {
		return this.children.indexOf(i);
	}

	/**
	 * Returns true if this window should be drawn on the screen
	 *
	 * @return true if this window is visible
	 */
	public synchronized boolean isVisible() {
		return this.visible;
	}

	/**
	 * Removes the parent and if there is a parent, removes this from the
	 * parent. Sets the local dimensions to the actual dimensions so that it
	 * will render in the same place it was before.
	 */
	public synchronized void orphanSelf() {
		float x, y, sx, sy;
		x = this.getActualDisplaceX();
		y = this.getActualDisplaceY();
		sx = this.getActualWidth();
		sy = this.getActualHeight();
		this.localDisplace.set(x, y);
		this.scale.set(sx, sy);
		IkWindow win = this.parent;
		this.parent = null;// so this does not recurse until crashing
		if (win != null) {
			win.removeChild(this);
		}
	}

	/**
	 * Recalculates all real displacements and scale, first recursing to the top
	 * dirty window and then trickling down until it has recalculated this and
	 * everything above it.
	 */
	protected synchronized void recalculate() {
		if (!this.dirty) {
			return;
		}

		if (this.parent != null) {
			this.parent.recalculate();
		}

		this.recalculateLocalDisplaceNW();

		this.realDisplacement.x =
			this.localDisplaceNW.x * this.getParentRealWidth()
				+ this.getParentRealDisplaceX();
		this.realDisplacement.y =
			this.localDisplaceNW.y * this.getParentRealHeight()
				+ this.getParentRealDisplaceY();
		this.realScale.y = this.scale.y * this.getParentRealHeight();
		this.realScale.x = this.scale.x * this.getParentRealWidth();

		this.dirty = false;
	}

	/**
	 * Calculates what the displacement would be, should it be from the
	 * northwest corner. This is used for screen displacement.
	 */
	private synchronized void recalculateLocalDisplaceNW() {
		float xDispl;
		float yDispl;

		switch (this.align) {
			case CENTER:
				xDispl = (1 - this.scale.x) / 2;
				yDispl = (1 - this.scale.y) / 2;
				break;
			case EAST:
				xDispl = 1 - (this.localDisplace.x + this.scale.x);
				yDispl = (1 - this.scale.y) / 2;
				break;
			case NORTH:
				xDispl = (1 - this.scale.x) / 2;
				yDispl = this.localDisplace.y;
				break;
			case NORTH_EAST:
				xDispl = 1 - (this.localDisplace.x + this.scale.x);
				yDispl = this.localDisplace.y;
				break;
			case NORTH_WEST:
				xDispl = this.localDisplace.x;
				yDispl = this.localDisplace.y;
				break;
			case SOUTH:
				xDispl = (1 - this.scale.x) / 2;
				yDispl = 1 - (this.localDisplace.y + this.scale.y);
				break;
			case SOUTH_EAST:
				xDispl = 1 - (this.localDisplace.x + this.scale.x);
				yDispl = 1 - (this.localDisplace.y + this.scale.y);
				break;
			case SOUTH_WEST:
				xDispl = this.localDisplace.x;
				yDispl = 1 - (this.localDisplace.y + this.scale.y);
				break;
			case WEST:
				xDispl = this.localDisplace.x;
				yDispl = (1 - this.scale.y) / 2;
				break;
			default:
				// just default to upper left corner
				xDispl = this.localDisplace.x;
				yDispl = this.localDisplace.y;
				break;
		}

		this.localDisplaceNW.set(xDispl, yDispl);
	}

	/**
	 * Removes the given child from this window. If the child recognizes this as
	 * its parent, it will orphan itself. If this is overridden be sure
	 * {@link #orphanSelf()} and {@link #removeChild(IkWindow)} do not
	 * recursively call each other infinitely.
	 *
	 * @param item the item to remove
	 */
	public synchronized void removeChild(IkWindow item) {
		this.children.remove(item);
		// so this does not recurse until crashing
		if (item != null && item.parent != null) {
			item.orphanSelf();
		}
	}

	/**
	 * Sets the alignment of the window to the given one. This determines from
	 * where the window is displaced in the parent.
	 *
	 * @param newAlignment the new alignment to use
	 */
	public synchronized void setAlignment(final Alignment newAlignment) {
		this.align = newAlignment;
		this.dirty();
	}

	/**
	 * Gives this window a callback method. This has a method that performs some
	 * action when {@link #executeAction()} is called. Setting a null callback
	 * will essentially remove the callback from the window.
	 *
	 * @param callback the method to execute when this window is interacted with
	 */
	public void setCallback(IWindowCallback callback) {
		this.callbackFunc = callback;
	}

	/**
	 * Sets the local displacement of a window, as a percentage of x and y.
	 * Depending on the alignment of the window in its parent, one or both of
	 * these values may not be used as it may be centered and thus ignore the
	 * value. Flags the window as dirty.
	 *
	 * @param displace the displacement to use
	 */
	public synchronized void setDisplacement(final Point displace) {
		this.localDisplace.set(displace.x, displace.y);
		this.dirty();
	}

	/**
	 * Sets the local height percentage and flags the window as dirty.
	 *
	 * @param height the new height of the window as a decimal percentage of the
	 *            parent
	 */
	public synchronized void setLocalHeight(final float height) {
		this.scale.y = height;
		this.dirty();
	}

	/**
	 * Sets the local width percentage and flags the window as dirty.
	 *
	 * @param width the new width of the window as a decimal percentage of the
	 *            parent
	 */
	public synchronized void setLocalWidth(final float width) {
		this.scale.x = width;
		this.dirty();
	}

	/**
	 * Sets the new parent of this window and adds this to the new parent's
	 * children.
	 *
	 * @param newParent the new parent of this window
	 */
	public synchronized void setParent(final IkWindow newParent) {
		this.parent = newParent;
		if (!this.parent.hasChild(this)) {
			this.parent.addChild(this);
		}
		this.dirty();
	}

	/**
	 * Sets the state object to the supplied one. This copies values from the
	 * passed map into a new internal map that will have the same type as the
	 * passed one if possible, defaulting to a {@link HashMap}, to allow for
	 * different map types if desired.
	 *
	 * If there is an existing state object, it is cleared and replaced with the
	 * new one.
	 * 
	 * If you pass a null, this will simply remove the state object.
	 *
	 * @param newStateObject the new state object to use for this window.
	 */
	@SuppressWarnings("unchecked")
	public synchronized void setStateObject(
		final Map<Object, Object> newStateObject) {
		if (this.stateObj != null) {
			this.stateObj.clear();
			this.stateObj = null;
		}
		if (newStateObject == null) {
			this.stateObj = null;
			return;
		}
		try {
			this.stateObj = newStateObject.getClass().newInstance();
		}
		catch (InstantiationException e) {
			Log.w("Texamon.IkWindow", e);
		}
		catch (IllegalAccessException e) {
			Log.w("Texamon.IkWindow", e);
		}

		if (this.stateObj == null) { // fall through case
			this.stateObj = new HashMap<Object, Object>();
		}

		this.stateObj.putAll(newStateObject);
	}

	/**
	 * Sets the windows current style, overriding the default style
	 *
	 * @param newStyle the new style to set
	 */
	public void setStyle(WindowStyle newStyle) {
		this.currentStyle = newStyle;
	}

	/**
	 * Sets the visibility of this window. If a window is invisible, it and any
	 * children will not be drawn.
	 *
	 * @param isVisible true if the window should be visible or false if not
	 */
	public synchronized void setVisible(final boolean isVisible) {
		this.visible = isVisible;
	}

}
