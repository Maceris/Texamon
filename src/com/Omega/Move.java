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

public enum Move {
	BLANK("--", "", Type.GLITCH, 0, 0, 0),
	BOIL("Boil", "", Type.WATER, 20, 65, 100),
	CHLORO_BLAST("Chloro Blast", "", Type.PLANT, 10, 120, 100),
	CRUNCH("Crunch", "", Type.NORMAL, 10, 50, 100),
	ENTANGLE("Entangle", "", Type.PLANT, 25, 55, 95),
	FIN_SLICE("Fin Slice", "", Type.WATER, 15, 95, 100),
	FLAMETHROWER("Flamethrower", "", Type.FIRE, 15, 95, 100),
	GUILLATINE("Gillatine", "", Type.WATER, 25, 40, 100),
	HYDRO_BLAST("Hydro Blast", "", Type.WATER, 10, 80, 100),
	INFERNO("Inferno", "", Type.FIRE, 5, 120, 85),
	JUDGEMENT("Judgement", "", Type.NORMAL, 5, 150, 90),
	LAND_SLIDE("Land Slide", "", Type.STONE, 5, 100, 80),
	LEAF_SLASH("Leaf Slash", "", Type.PLANT, 5, 150, 90),
	LIFE_DRAIN("Life Drain", "", Type.PLANT, 15, 35, 100),
	M("M", "", Type.GLITCH, 50, 180, 100),
	MAGMA_SEAL("Magma Seal", "", Type.FIRE, 20, 50, 100),
	MELT("Melt", "", Type.FIRE, 25, 40, 100),
	MONOLITH("Monolith", "", Type.STONE, 5, 150, 80),
	PUNCH("Punch", "", Type.NORMAL, 10, 100, 100),
	PYRE("Pyre", "", Type.FIRE, 5, 100, 100),
	QUICK_SLASH("Quick Slash", "", Type.NORMAL, 10, 80, 100),
	ROCK_ROLL("Rock Roll", "", Type.STONE, 15, 90, 100),
	ROCK_TOSS("Rock Toss", "", Type.STONE, 10, 25, 100),
	SAND_TOMB("Sand Tomb", "", Type.STONE, 20, 70, 100),
	STOMP("Stomp", "", Type.NORMAL, 35, 40, 100),
	TSUNAMI("Tsunami", "", Type.WATER, 5, 120, 80),
	VINE_STRIKE("Vine Strike", "", Type.PLANT, 15, 90, 100);

	public static Move fromString(String text) {
		if (text != null) {
			for (Move move : Move.values()) {
				if (text.equalsIgnoreCase(move.getName())) {
					return move;
				}
			}
		}
		return null;
	}

	private int moveACC;
	private String moveDescr;
	private String moveName;
	private int movePower;
	private int movePP;

	private Type moveType;

	private Move(String name, String descr, Type type, int pp, int power,
			int acc) {
		this.moveName = name;
		this.moveDescr = descr;
		this.moveType = type;
		this.movePP = pp;
		this.movePower = power;
		this.moveACC = acc;
	}

	public int getAcc() {
		return this.moveACC;
	}

	public String getDescr() {
		return this.moveDescr;
	}

	public String getName() {
		return this.moveName;
	}

	public int getPower() {
		return this.movePower;
	}

	public int getPP() {
		return this.movePP;
	}

	public Type getType() {
		return this.moveType;
	}
}
