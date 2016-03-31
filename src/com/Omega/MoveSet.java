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

public class MoveSet {
	public static final Move[] WATER = new Move[] {Move.BOIL, Move.GUILLATINE,
			Move.FIN_SLICE, Move.HYDRO_BLAST, Move.TSUNAMI};

	public static final Move[] NORMAL = new Move[] {Move.STOMP, Move.CRUNCH,
			Move.QUICK_SLASH, Move.PUNCH, Move.JUDGEMENT};

	public static final Move[] STONE = new Move[] {Move.ROCK_TOSS,
			Move.SAND_TOMB, Move.LAND_SLIDE, Move.ROCK_ROLL, Move.MONOLITH};

	public static final Move[] FIRE = new Move[] {Move.MELT, Move.MAGMA_SEAL,
			Move.PYRE, Move.FLAMETHROWER, Move.INFERNO};

	public static final Move[] PLANT =
			new Move[] {Move.LIFE_DRAIN, Move.ENTANGLE, Move.CHLORO_BLAST,
					Move.VINE_STRIKE, Move.LEAF_SLASH};
}
