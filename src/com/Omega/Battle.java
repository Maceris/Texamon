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

import com.Omega.util.Random;

/**
 * State information and helper methods for a battle that is taking place.
 *
 * @author Ches Burks
 *
 */
public class Battle {
	/**
	 * The RNG for battles
	 */
	private static Random rand = new Random();

	/**
	 * Calculates the damage done to an opponent based on stats of both parties,
	 * move information, type advantages, etc.
	 *
	 * @param attacker the monster using the move
	 * @param defender the monster the move is used on
	 * @param move the move that is used
	 * @return how much damage should be done to the defender
	 */
	public static int calcDamage(final Monster attacker,
		final Monster defender, final Move move) {
		float lvl = attacker.getCurrentLVL();
		float basepower = move.getPower();
		float atk = attacker.getSpecies().getATK();
		float def = defender.getSpecies().getDEF();
		float ch = 1; // modify this
		float r = 100 - Battle.rand.getIntBetween(0, 15);
		double stab = 1; // modify this
		double type1 = 1; // modify this
		int damagedone = 0;
		if (attacker.getSpecies().getType().equals(move.getType())) {
			stab = 1.5;
		}
		type1 =
			Battle.getTypeDamage(attacker.getSpecies().getType(), defender
				.getSpecies().getType());
		int rd = Battle.rand.getIntBetween(0, 400);
		if (rd == 7) {
			ch = 2;
		}
		damagedone =
			(int) (((((((lvl * 2) / 5) + 2) * ((basepower * atk) / 50)) / def) + 2)
				* ((ch * r) / 100) * stab * type1);
		return damagedone;
	}

	/**
	 * Returns true if the given monster was caught using either a normal ball
	 * or great ball.
	 *
	 * @param foe The monster to try catching
	 * @param isGreatBall true for a great ball, false for a normal one
	 * @return true if the monster was caught, false if it was not
	 */
	public static boolean didBallCatch(final Monster foe,
		final boolean isGreatBall) {
		float randOne;
		// great ball is 12
		// normal is 8

		if (isGreatBall) {
			randOne = Battle.rand.getIntBetween(0, 150);
		}
		else {
			randOne = Battle.rand.getIntBetween(0, 255);
		}
		if (randOne <= 75) {
			float randTwo = Battle.rand.getIntBetween(0, 255);
			float f;
			f =
				(foe.getMaxHP() * 255) / (isGreatBall ? 12 : 8)
					/ (foe.getCurrentHP() / 4);
			if (f >= randTwo) {
				return true;
			}
			return false;
		}
		return false;

	}

	/**
	 * Returns the effectiveness of the attacking type against the defending as
	 * a damage multiplier (1, .5, or 2) applied to damage done from the
	 * attacking type to the defending one.
	 *
	 * @param attacking the attacking type
	 * @param defending the defending type
	 * @return the damage multiplier
	 */
	public static double getTypeDamage(final Type attacking,
		final Type defending) {
		double dam = 1;
		if (attacking.equals(Type.WATER)) {
			if (defending.equals(Type.FIRE)) {
				dam = 2;
			}
			else if (defending.equals(Type.PLANT)) {
				dam = .5;
			}
			else if (defending.equals(Type.STONE)) {
				dam = 2;
			}
			else if (defending.equals(Type.GLITCH)) {
				dam = .5;
			}
		}
		else if (attacking.equals(Type.FIRE)) {
			if (defending.equals(Type.WATER)) {
				dam = .5;
			}
			else if (defending.equals(Type.PLANT)) {
				dam = 2;
			}
			else if (defending.equals(Type.STONE)) {
				dam = .5;
			}
			else if (defending.equals(Type.GLITCH)) {
				dam = .5;
			}
		}
		else if (attacking.equals(Type.PLANT)) {
			if (defending.equals(Type.WATER)) {
				dam = 2;
			}
			else if (defending.equals(Type.FIRE)) {
				dam = .5;
			}
			else if (defending.equals(Type.STONE)) {
				dam = 2;
			}
			else if (defending.equals(Type.GLITCH)) {
				dam = .5;
			}
		}
		else if (attacking.equals(Type.STONE)) {
			if (defending.equals(Type.WATER)) {
				dam = .5;
			}
			else if (defending.equals(Type.FIRE)) {
				dam = 2;
			}
			else if (defending.equals(Type.PLANT)) {
				dam = .5;
			}
			else if (defending.equals(Type.GLITCH)) {
				dam = .5;
			}
		}
		else if (attacking.equals(Type.NORMAL)) {
			if (defending.equals(Type.STONE)) {
				dam = .5;
			}
			else if (defending.equals(Type.GLITCH)) {
				dam = .5;
			}
		}
		else if (attacking.equals(Type.GLITCH)) {
			if (defending.equals(Type.WATER)) {
				dam = 2;
			}
			else if (defending.equals(Type.FIRE)) {
				dam = 2;
			}
			else if (defending.equals(Type.PLANT)) {
				dam = 2;
			}
			else if (defending.equals(Type.STONE)) {
				dam = 2;
			}
			else if (defending.equals(Type.NORMAL)) {
				dam = 2;
			}
		}
		return dam;
	}

	private boolean isPlayerBattle;

	private TexamonType curEnemyTexamon;

	private TexamonType curPlayerTexamon;
}
