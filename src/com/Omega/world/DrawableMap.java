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
package com.Omega.world;

import com.Omega.GameData;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class DrawableMap extends Map {

	boolean dirty;

	private static final float TILES_PER_SHORT_SIDE = 8.0f;

	public DrawableMap(int w, int h) {
		super(w, h);
		dirty = true;
	}

	private Bitmap drawableImg;

	public void draw(Canvas c, final GameData gameData) {

		final int width = c.getWidth();
		final int height = c.getHeight();
		final int shorter = width <= height ? width : height;
		final int tileSide = (int) (shorter / TILES_PER_SHORT_SIDE);

		if (dirty) {
			drawableImg =
					Bitmap.createBitmap(tileSide * getWidth(), tileSide
							* getHeight(), Bitmap.Config.ARGB_8888);
		}
		c.drawBitmap(drawableImg, new Rect(gameData.getxPos() * tileSide,
				gameData.getyPos() * tileSide, width, height), new Rect(0, 0,
				width, height), null);
	}

	/**
	 * Adds the structure to the map. This will replace anything already placed.
	 * This methods automatically repopulates the collision boxes after placing.
	 *
	 * @param struct The structure to add
	 * @param x The x position to start at
	 * @param y The y position to start at
	 */
	@Override
	public void addStructure(final Structure struct, final int x, final int y) {
		super.addStructure(struct, x, y);
		dirty = true;
	}

	/**
	 * Returns the {@link Tile tile} at the specified position.
	 *
	 * @param x The x position
	 * @param y The y position
	 * @return The tile
	 */
	@Override
	public Tile getTile(final int x, final int y) {
		Tile ret = super.getTile(x, y);
		dirty = true;
		return ret;
	}

	/**
	 * Set all {@link Tile tiles} in the map to the specified {@link TileType
	 * type}.
	 *
	 * @param type The type to set
	 */
	@Override
	public void setAllTiles(TileType type) {
		super.setAllTiles(type);
		dirty = true;
	}

	/**
	 * Set the specified {@link Tile tile} to the specified {@link TileType
	 * type}.
	 *
	 * @param x The tiles x position
	 * @param y The tiles y position
	 * @param type The type to set
	 * @return 0 if it was successful, -1 if the coordinates were invalid
	 */
	@Override
	public int setTile(final int x, final int y, final TileType type) {
		int ret = super.setTile(x, y, type);
		dirty = true;
		return ret;
	}

	/**
	 * Sets a w by h block of tiles in the map with an upper left coordinate at
	 * (x,y) to the specified block. The width and height are the total width of
	 * the block of changed tiles, so a block with size 1x1 will only change one
	 * tile, and a 2x2 will change three tiles besides the corner.
	 *
	 * If the width and
	 *
	 * @param x The upper left corner's x position
	 * @param y The upper left corner's y position
	 * @param w The width of the block of tiles to set
	 * @param h The height of the block of tiles to set
	 * @param type The type to set
	 * @return 0 if successful, -1 if the bounds were invalid
	 */
	@Override
	public int setTiles(final int x, final int y, final int w, final int h,
			final TileType type) {
		int ret = super.setTiles(x, y, w, h, type);
		dirty = true;
		return ret;
	}

}
