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

import java.util.ArrayList;

import android.graphics.Canvas;

import com.Omega.Point;

public class Menu extends IkWindow {

	private int w;
	private int h;
	private int size;
	private ArrayList<MenuItem> items;

	/**
	 * Constructs a menu that has space for width*height items.
	 * 
	 * @param width How many columns the menu should have
	 * @param height How many rows the menu should have
	 */
	public Menu(final int width, final int height) {
		this.w = width;
		this.h = height;
		this.size = this.w * this.h;
		this.items = new ArrayList<MenuItem>(this.size);
		// take up all of parent by default
		this.scale.set(1.0f, 1.0f);
		this.localDisplace.set(0.0f, 0.0f);
		this.align = Alignment.NORTH_WEST;
		this.dirty();
	}

	/**
	 * Adds a child if and only if it is a menu item. Increase the height
	 * (number of rows) if the menu is full.
	 */
	@Override
	public synchronized void addChild(IkWindow item) {
		if (!(item instanceof MenuItem)) {
			System.err.println("item " + item.getClass()
					+ " rejected from menu due to type");
			// reject it if it's not a menu item
			return;
		}
		if (this.items.size() >= this.size) {
			this.setHeight(this.h + 1);
		}
		this.items.add((MenuItem) item);
		item.setParent(this);
		this.dirty();
	}

	@Override
	public synchronized void dirty() {
		this.dirty = true;
		if (this.items != null) {
			for (MenuItem item : this.items) {
				item.dirty();
			}
		}
	}

	@Override
	public synchronized void draw(Canvas c) {
		if (this.dirty) {
			this.recalculate();
		}
		if (!this.visible) {
			return;
		}
		for (IkWindow item : this.items) {
			item.draw(c);
		}
	}

	@Override
	public synchronized int getChildCount() {
		return this.items.size();
	}

	/**
	 * Returns the number of rows in the menu
	 *
	 * @return the number of rows
	 */
	public synchronized int getHeight() {
		return this.h;
	}

	/**
	 * Returns the number of columns in the menu
	 *
	 * @return the number of columns
	 */
	public synchronized int getWidth() {
		return this.w;
	}

	@Override
	protected synchronized void recalculate() {
		if (!this.dirty) {
			return;
		}
		super.recalculate();
		this.recalculateItems();
	}

	protected synchronized void recalculateItems() {
		int i;
		int j;
		for (j = 0; j < this.h; ++j) {
			for (i = 0; i < this.w; ++i) {
				this.items.get((j) * this.w + i).setLocalHeight(
						(1.0f / this.h) * 0.80f);
				this.items.get((j) * this.w + i).setLocalWidth(
						(1.0f / this.w) * 0.80f);
				this.items.get((j) * this.w + i).setDisplacement(
						new Point((1.0f / this.w) * i + 0.1f / this.w,
								(1.0f / this.h) * j + 0.1f / this.h));
				this.items.get((j) * this.w + i).setAlignment(
						Alignment.NORTH_WEST);
			}
		}
	}

	public synchronized void setHeight(final int height) {
		this.h = height;
		this.size = this.w * this.h;
		this.dirty();
	}

	public synchronized void setWidth(final int width) {
		this.w = width;
		this.size = this.w * this.h;
		this.dirty();
	}

}
