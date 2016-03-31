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
 * Represents a single square on a {@link Map map}.
 *
 * @author Ches Burks
 *
 */
public class Tile {
	public TileType tileType;

	/**
	 * Returns the ID of the tile type.
	 *
	 * @return The ID
	 */
	public int getID() {
		return this.tileType.getID();
	}

	/**
	 * Returns the name of the tile type (same as the texture name).
	 *
	 * @return The tiles name
	 */
	public String getName() {
		return this.tileType.getName();
	}

	/**
	 * Returns the {@link TileType type} of the tile.
	 *
	 * @return The type of the tile
	 */
	public TileType getTileType() {
		return this.tileType;
	}

	/**
	 * Returns true if the tile causes collision.
	 *
	 * @return True if it is solid, false otherwise
	 */
	public boolean isSolid() {
		return this.tileType.isSolid();
	}

	/**
	 * Construct a tile with the given {@link TileType type}.
	 *
	 * @param type The type of the tile
	 */
	public void setTileType(TileType type) {
		this.tileType = type;
	}
}
