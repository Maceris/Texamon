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

/**
 * Moves that can be learned and used by monsters.
 *
 * @author Ches Burks
 *
 */
public enum Move {
	/**
	 * A lack of move
	 */
	BLANK("--", "", Type.GLITCH, 0, 0, 0),
	/**
	 * Boil
	 */
	BOIL("Boil", "", Type.WATER, 20, 65, 100),
	/**
	 * Chloro Blast
	 */
	CHLORO_BLAST("Chloro Blast", "", Type.PLANT, 10, 120, 100),
	/**
	 * Crunch
	 */
	CRUNCH("Crunch", "", Type.NORMAL, 10, 50, 100),
	/**
	 * Entangle
	 */
	ENTANGLE("Entangle", "", Type.PLANT, 25, 55, 95),
	/**
	 * Fin Slice
	 */
	FIN_SLICE("Fin Slice", "", Type.WATER, 15, 95, 100),
	/**
	 * Flamethrower
	 */
	FLAMETHROWER("Flamethrower", "", Type.FIRE, 15, 95, 100),
	/**
	 * Gillatine
	 */
	GUILLATINE("Gillatine", "", Type.WATER, 25, 40, 100),
	/**
	 * Hydro Blast
	 */
	HYDRO_BLAST("Hydro Blast", "", Type.WATER, 10, 80, 100),
	/**
	 * Inferno
	 */
	INFERNO("Inferno", "", Type.FIRE, 5, 120, 85),
	/**
	 * Judgement
	 */
	JUDGEMENT("Judgement", "", Type.NORMAL, 5, 150, 90),
	/**
	 * Land Slide
	 */
	LAND_SLIDE("Land Slide", "", Type.STONE, 5, 100, 80),
	/**
	 * Leaf Slash
	 */
	LEAF_SLASH("Leaf Slash", "", Type.PLANT, 5, 150, 90),
	/**
	 * Life Drain
	 *
	 */
	LIFE_DRAIN("Life Drain", "", Type.PLANT, 15, 35, 100),
	/**
	 * A glitch move
	 */
	M("M", "", Type.GLITCH, 50, 180, 100),
	/**
	 * Magma Seal
	 */
	MAGMA_SEAL("Magma Seal", "", Type.FIRE, 20, 50, 100),
	/**
	 * Melt
	 */
	MELT("Melt", "", Type.FIRE, 25, 40, 100),
	/**
	 * Monolith
	 */
	MONOLITH("Monolith", "", Type.STONE, 5, 150, 80),
	/**
	 * Punch
	 */
	PUNCH("Punch", "", Type.NORMAL, 10, 100, 100),
	/**
	 * Pyre
	 */
	PYRE("Pyre", "", Type.FIRE, 5, 100, 100),
	/**
	 * Quick Slash
	 */
	QUICK_SLASH("Quick Slash", "", Type.NORMAL, 10, 80, 100),
	/**
	 * Rock Roll
	 */
	ROCK_ROLL("Rock Roll", "", Type.STONE, 15, 90, 100),
	/**
	 * Rock Toss
	 */
	ROCK_TOSS("Rock Toss", "", Type.STONE, 10, 25, 100),
	/**
	 * Sand Tomb
	 */
	SAND_TOMB("Sand Tomb", "", Type.STONE, 20, 70, 100),
	/**
	 * Stomp
	 */
	STOMP("Stomp", "", Type.NORMAL, 35, 40, 100),
	/**
	 * Tsunami
	 */
	TSUNAMI("Tsunami", "", Type.WATER, 5, 120, 80),
	/**
	 * Vine Strike
	 */
	VINE_STRIKE("Vine Strike", "", Type.PLANT, 15, 90, 100);

	/**
	 * Gets the move corresponding to given move name. Case is ignored.
	 *
	 * @param text the move name
	 * @return the move with that name, or null if no move is found
	 */
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

	/**
	 * Returns the base accuracy of this move
	 *
	 * @return the accuracy
	 */
	public int getAcc() {
		return this.moveACC;
	}

	/**
	 * Returns the plain description of this move
	 *
	 * @return the description
	 */
	public String getDescr() {
		return this.moveDescr;
	}

	/**
	 * Returns the plain name of the move
	 *
	 * @return the name
	 */
	public String getName() {
		return this.moveName;
	}

	/**
	 * Returns the base power of the move
	 *
	 * @return the power
	 */
	public int getPower() {
		return this.movePower;
	}

	/**
	 * Returns the base Power Points of the move
	 *
	 * @return the PP
	 */
	public int getPP() {
		return this.movePP;
	}

	/**
	 * Returns the {@link Type type} of the move
	 *
	 * @return the type
	 */
	public Type getType() {
		return this.moveType;
	}
}
