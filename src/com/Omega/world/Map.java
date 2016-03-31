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
 * A set of {@link Tile tiles} representing a map.
 *
 * @author Ches Burks
 *
 */
public class Map {
	private Tile[][] tiles;// arranged as [height][width]
	private int width;
	private int height;

	/**
	 * Construct a new Map with the given width and height.
	 *
	 * @param w The width
	 * @param h The height
	 */
	public Map(final int w, final int h) {
		this.tiles = new Tile[h][w];
		this.width = w;
		this.height = h;
		this.initTileArray();
	}

	/**
	 * Adds the structure to the map. This will replace anything already placed.
	 * This methods automatically repopulates the collision boxes after placing.
	 *
	 * @param struct The structure to add
	 * @param x The x position to start at
	 * @param y The y position to start at
	 */
	public void addStructure(final Structure struct, final int x, final int y) {
		int i, j;
		for (j = 0; j < struct.getHeight(); j++) {
			for (i = 0; i < struct.getWidth(); i++) {
				this.setTile(i + x, j + y, struct.getTileAt(i, j).getTileType());
			}
		}
	}

	/**
	 * Returns true if the given location is a valid position on the map, false
	 * otherwise
	 *
	 * @param xPos The x position
	 * @param yPos The y position
	 * @return true if the point exists
	 */
	public boolean containsPoint(final int xPos, final int yPos) {
		return (xPos >= 0 && xPos < this.width && yPos >= 0 && yPos < this.width);
	}

	/**
	 * Returns true if the given x value is a valid x in the map.
	 *
	 * @param xPos The x position
	 * @return true if the x exists
	 */
	public boolean containsX(final int xPos) {
		return (xPos >= 0 && xPos < this.width);
	}

	/**
	 * Returns true if the given y value is a valid y in the map.
	 *
	 * @param yPos The y position
	 * @return true if the y exists
	 */
	public boolean containsY(final int yPos) {
		return (yPos >= 0 && yPos < this.width);
	}

	/**
	 * Returns the maps height.
	 *
	 * @return The height
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns the {@link Tile tile} at the specified position.
	 *
	 * @param x The x position
	 * @param y The y position
	 * @return The tile
	 */
	public Tile getTile(final int x, final int y) {
		if (this.tiles[y][x] == null) {
			Tile tile = new Tile();
			tile.setTileType(TileType.ERROR);
			return tile;
		}
		return this.tiles[y][x];
	}

	/**
	 * Returns the maps width.
	 *
	 * @return The width
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Initializes the array of tiles with blank tiles.
	 */
	private void initTileArray() {
		int x;
		int y;
		for (x = 0; x < this.width; x++) {
			for (y = 0; y < this.height; y++) {
				this.tiles[y][x] = new Tile();
			}
		}
	}

	/**
	 * Set all {@link Tile tiles} in the map to the specified {@link TileType
	 * type}.
	 *
	 * @param type The type to set
	 */
	public void setAllTiles(TileType type) {
		int x;
		int y;
		for (x = 0; x < this.width; x++) {
			for (y = 0; y < this.height; y++) {
				this.setTile(x, y, type);
			}
		}
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
	public int setTile(final int x, final int y, final TileType type) {
		if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
			return -1;
		}
		this.tiles[y][x].setTileType(type);
		return 0;
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
	public int setTiles(final int x, final int y, final int w, final int h,
			final TileType type) {
		if (x < 0 || y < 0 || x > this.width || y > this.height) {
			// check x and y first to catch invalid coords early
			return -1;
		}
		int i;
		int j;
		final int stopI = x + w;
		final int stopJ = y + h;
		if (stopI >= this.width || stopJ >= this.height) {
			// check that the box does not go out of bounds
			return -1;
		}

		for (j = y; j < stopJ; ++j) {
			for (i = x; i < stopI; ++i) {
				this.tiles[j][i].setTileType(type);
			}
		}
		return 0;
	}
}
