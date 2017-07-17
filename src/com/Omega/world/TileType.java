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
package com.Omega.world;

/**
 * An enum of different types of tiles and the associated id.
 *
 * @author Ches Burks
 *
 */
public enum TileType {
	GRASS(0, "grass"),
	SAND(1, "sand"),
	FLOWER(2, "flower"),
	GRASS_LEDGE(3, "grassLedge", true),
	GRASS_TALL(4, "tallGrass"),
	WATER(5, "water"),
	WALL(6, "walls", true),
	LEDGE_N(7, "ledgeN", true),
	LEDGE_NW(8, "ledgeNW", true),
	LEDGE_NE(9, "ledgeNE", true),
	LEDGE_E(10, "ledgeE", true),
	LEDGE_W(11, "ledgeW", true),
	DOOR_E(12, "doorE"),
	DOOR_W(13, "doorW"),
	STAIRS_N(14, "stairsN"),
	STAIRS_E(15, "stairsE"),
	STAIRS_W(16, "stairsW"),
	HOSPITAL_ROOF_NW(17, "hospitalRNW", true),
	HOSPITAL_ROOF_NE(18, "hospitalRNE", true),
	HOSPITAL_ROOF_N(19, "hospitalRN", true),
	HOSPITAL_ROOF_W(20, "hospitalRW", true),
	HOSPITAL_ROOF_E(21, "hospitalRE", true),
	HOSPITAL_ROOF_S(22, "hospitalRS", true),
	HOSPITAL_ROOF_SE(23, "hospitalRSE", true),
	HOSPITAL_ROOF_SW(24, "hospitalRSW", true),
	HOSPITAL_WALL(25, "hospitalW", true),
	HOSPITAL_WALL_E(26, "hospitalWE", true),
	HOSPITAL_WALL_W(27, "hospitalWW", true),
	HOSPITAL_DOOR(28, "hospitalDoor"),
	HOUSE_ROOF_NW(29, "houseRNW", true),
	HOUSE_ROOF_N(30, "houseRN", true),
	HOUSE_ROOF_NE(31, "houseRNE", true),
	HOUSE_ROOF_W(32, "houseRW", true),
	HOUSE_ROOF_S(33, "houseRS", true),
	HOUSE_ROOF_SE(34, "houseRSE", true),
	HOUSE_ROOF_SW(35, "houseRSW", true),
	HOUSE_WALL_E(36, "houseWE", true),
	HOUSE_WALL_W(37, "houseWW", true),
	HOUSE_DOOR(38, "houseDoor"),
	MUD(39, "mud"),
	ROCK(40, "rock", true),
	ROCK_N(41, "rockN", true),
	ROCK_E(42, "rockE", true),
	ROCK_S(43, "rockS", true),
	ROCK_W(44, "rockW", true),
	ROCK_SW(45, "rockSW", true),
	ROCK_NW(46, "rockNW", true),
	ROCK_SE(47, "rockSE", true),
	ROCK_NE(48, "rockNE", true),
	SIGN(49, "sign", true),
	SMALL_TREE(50, "smallTree", true),
	TREE_UP_LEFT(51, "treeUL", true),
	TREE_UP_RIGHT(52, "treeUR", true),
	TREE_MID_LEFT(53, "treeML", true),
	TREE_MID_RIGHT(54, "treeMR", true),
	TREE_LOW_LEFT(55, "treeLL", true),
	TREE_LOW_RIGHT(56, "treeLR", true),
	ERROR(-1, "error");

	private int tileID;
	private boolean isSolid;
	private String name;

	/**
	 * Constructs a new {@link TileType} with the given ID. The tile is not
	 * solid. Solid tiles need to use the
	 * {@link #TileType(int, String, boolean)} constructor to set the solidity
	 * to true.
	 *
	 * @param id The ID to use
	 * @param tileName The name of the tile
	 */
	private TileType(int id, String tileName) {
		this.tileID = id;
		this.isSolid = false;
		this.name = tileName;
	}

	/**
	 * Constructs a new {@link TileType} with the given ID. The tile may also be
	 * solid.
	 *
	 * @param id The ID to use
	 * @param tileName The name of the tile
	 * @param solid If the tile causes collision
	 */
	private TileType(int id, String tileName, boolean solid) {
		this.tileID = id;
		this.isSolid = solid;
		this.name = tileName;
	}

	/**
	 * Returns the ID of the tile.
	 *
	 * @return The ID
	 */
	public int getID() {
		return this.tileID;
	}

	/**
	 * Returns the name of the tile (same as the texture name).
	 *
	 * @return The tiles name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns true if the tile causes collision.
	 *
	 * @return True if it is solid, false otherwise
	 */
	public boolean isSolid() {
		return this.isSolid;
	}

}
