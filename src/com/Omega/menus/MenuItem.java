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
package com.Omega.menus;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * A textual item in a menu. This can have an associated action.
 * 
 * @author Ches Burks
 *
 */
public class MenuItem extends IkWindow {
	private String text;
	private Paint textPaint;
	private final Paint defPaint;
	private boolean dirty2;

	/**
	 * Creates a new menu item with default text.
	 */
	public MenuItem() {
		this.defPaint = new Paint();
		this.textPaint = new Paint();
		this.textPaint.setColor(Color.BLACK);
		this.text = "Text";
		this.dirty();
	}

	/**
	 * Creates a new menu item with supplied text.
	 * 
	 * @param theText The text to display for this item.
	 */
	public MenuItem(final String theText) {
		this();
		this.text = theText;
		this.dirty();
	}

	@Override
	public synchronized void dirty() {
		this.dirty = true;
		this.dirty2 = true;
	}

	@Override
	public synchronized void draw(Canvas c) {
		if (this.dirty) {
			this.recalculate();
		}
		if (this.dirty2) {
			this.recalculateTextSize(c);
		}
		if (!this.isVisible()) {
			return;
		}
		c.drawText(
			this.text,
			this.getActualDisplaceX() * c.getWidth(),
			this.getActualDisplaceY() * c.getHeight() + 0.9f
				* this.getActualHeight() * c.getHeight(), this.textPaint);
	}

	/**
	 * Returns the parent of the menu item, which is a menu.
	 * 
	 * @return The menu that contains this item.
	 */
	public synchronized Menu getParent() {
		if (!(this.parent instanceof Menu)) {
			this.parent.removeChild(this);
		}
		return (Menu) this.parent;
	}

	/**
	 * Returns the text that is displayed for this item.
	 * 
	 * @return The associated text.
	 */
	public synchronized String getText() {
		return this.text;
	}

	/**
	 * Returns true if this item has a parent, false if the parent is null.
	 * 
	 * @return true if this item has a parent, false otherwise.
	 */
	public synchronized boolean hasParent() {
		return this.parent != null;
	}

	/**
	 * Calculate how large text should be on the given canvas.
	 * 
	 * @param c The canvas to calculate size based on.
	 */
	protected synchronized void recalculateTextSize(Canvas c) {
		Rect bounds = new Rect();
		this.defPaint.getTextBounds(this.text, 0, this.text.length(), bounds);

		Rect bound = this.getBoundingRect(c);

		float mod1 = bound.width() / this.defPaint.measureText(this.text);
		float mod2 = (bound.height() * 1.0f) / bounds.height();
		this.textPaint.setTextSize((mod1 <= mod2 ? mod1 : mod2)
			* this.defPaint.getTextSize());
		this.dirty2 = false;
	}

	@Override
	public synchronized void setParent(IkWindow newParent) {
		if (!(newParent instanceof Menu)) {
			return;
		}

		this.parent = newParent;
		updateHeights();
		this.dirty();
	}

	/**
	 * Sets the text to display for this item.
	 * 
	 * @param newText The text to use for this item.
	 */
	public synchronized void setText(final String newText) {
		this.text = newText;
		this.dirty();
	}
}
