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

public class Battle {
	private static Random rand = new Random();

	public static int calcDamage(Monster attacker, Monster defender, Move move) {
		float lvl = attacker.getCurrentLVL();
		float basepower = move.getPower();
		float atk = attacker.getTexamon().getATK();
		float def = defender.getTexamon().getDEF();
		float ch = 1; // modify this
		float r = (100 - Battle.rand.getIntBetween(0, 15));
		double stab = 1; // modify this
		double type1 = 1; // modify this
		int damagedone = 0;
		if (attacker.getTexamon().getType().equals(move.getType())) {
			stab = 1.5;
		}
		type1 =
				Battle.getTypeDamage(attacker.getTexamon().getType(), defender
						.getTexamon().getType());
		int rd = Battle.rand.getIntBetween(0, 400);
		if (rd == 7) {
			ch = 2;
		}
		damagedone =
				(int) (((((((((((((lvl * 2)) / 5)) + 2) * (((basepower * atk) / 50))) / def) + 2) * ((ch
						* r / 100)))) * stab)) * type1);
		return damagedone;
	}

	public static double getTypeDamage(Type type1, Type type2) {
		double dam = 1;
		if (type1.equals(Type.WATER)) {
			if (type2.equals(Type.WATER)) {
				dam = .5;
			}
			else if (type2.equals(Type.FIRE)) {
				dam = 2;
			}
			else if (type2.equals(Type.PLANT)) {
				dam = .5;
			}
			else if (type2.equals(Type.STONE)) {
				dam = 2;
			}
			else if (type2.equals(Type.GLITCH)) {
				dam = .5;
			}
		}
		else if (type1.equals(Type.FIRE)) {
			if (type2.equals(Type.WATER)) {
				dam = .5;
			}
			else if (type2.equals(Type.FIRE)) {
				dam = .5;
			}
			else if (type2.equals(Type.PLANT)) {
				dam = 2;
			}
			else if (type2.equals(Type.STONE)) {
				dam = .5;
			}
			else if (type2.equals(Type.GLITCH)) {
				dam = .5;
			}
		}
		else if (type1.equals(Type.PLANT)) {
			if (type2.equals(Type.WATER)) {
				dam = 2;
			}
			else if (type2.equals(Type.FIRE)) {
				dam = .5;
			}
			else if (type2.equals(Type.PLANT)) {
				dam = .5;
			}
			else if (type2.equals(Type.STONE)) {
				dam = 2;
			}
			else if (type2.equals(Type.GLITCH)) {
				dam = .5;
			}
		}
		else if (type1.equals(Type.STONE)) {
			if (type2.equals(Type.WATER)) {
				dam = .5;
			}
			else if (type2.equals(Type.FIRE)) {
				dam = 2;
			}
			else if (type2.equals(Type.PLANT)) {
				dam = .5;
			}
			else if (type2.equals(Type.STONE)) {
				dam = .5;
			}
			else if (type2.equals(Type.GLITCH)) {
				dam = .5;
			}
		}
		else if (type1.equals(Type.NORMAL)) {
			if (type2.equals(Type.STONE)) {
				dam = .5;
			}
			else if (type2.equals(Type.NORMAL)) {
				dam = .5;
			}
			else if (type2.equals(Type.GLITCH)) {
				dam = .5;
			}
		}
		else if (type1.equals(Type.GLITCH)) {
			if (type2.equals(Type.WATER)) {
				dam = 2;
			}
			else if (type2.equals(Type.FIRE)) {
				dam = 2;
			}
			else if (type2.equals(Type.PLANT)) {
				dam = 2;
			}
			else if (type2.equals(Type.STONE)) {
				dam = 2;
			}
			else if (type2.equals(Type.NORMAL)) {
				dam = 2;
			}
			else if (type2.equals(Type.GLITCH)) {
				dam = 0;
			}

		}
		return dam;
	}

	private boolean isPlayerBattle;

	private TexamonType curEnemyTexamon;

	private TexamonType curPlayerTexamon;
}
