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

/**
 * A structure, such as a house or road, made of tiles. Structures are immutable
 * so once their internal data cannot be changed.
 *
 * @author Ches Burks
 *
 */
public class Structure {
	private final int width;
	private final int height;
	private final Tile[][] tiles;

	/**
	 * Constructs a new {@link Structure}.
	 */
	public Structure() {
		this.width = 0;
		this.height = 0;
		this.tiles = new Tile[0][0];
	}

	/**
	 * Sets the tiles and width/height variables using the given array of
	 * {@link Tile tiles}. The tiles are copied to an internal array.
	 *
	 * @param tilesToUse The array to use
	 */
	public Structure(final Tile[][] tilesToUse) {
		int w = 0;
		int y, x;

		this.tiles = tilesToUse;
		this.height = tilesToUse.length;

		// find the maximum width, assume it is square
		for (y = 0; y < tilesToUse.length; y++) {
			if (tilesToUse[y].length > w) {
				w = tilesToUse[y].length;
			}
		}
		this.width = w;

		for (y = 0; y < this.height; y++) {
			for (x = 0; x < w; x++) {
				this.tiles[y][x] = new Tile();
				if (tilesToUse[y][x] != null) {
					this.tiles[y][x]
							.setTileType(tilesToUse[y][x].getTileType());
				}
				else {
					this.tiles[y][x].setTileType(TileType.ERROR);
				}
			}
		}
	}

	/**
	 * Returns the height of the structure.
	 *
	 * @return The height
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns the {@link Tile tile} at the given location.
	 *
	 * @param x The x value
	 * @param y The y value
	 * @return The tile at the given position
	 */
	public Tile getTileAt(int x, int y) {

		return this.tiles[y][x];
	}

	/**
	 * Returns the array of {@link Tile tiles}.
	 *
	 * @return The array
	 */
	public Tile[][] getTiles() {
		return this.tiles;
	}

	/**
	 * Returns the width of the structure.
	 *
	 * @return The width
	 */
	public int getWidth() {
		return this.width;
	}

}
