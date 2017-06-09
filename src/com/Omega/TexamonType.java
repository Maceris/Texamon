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

public enum TexamonType {

	// TODO localize this stuff
	AQWHIRL("Aqwhirl", "A hypnotic turtle that lives in creeks and ponds.",
		MoveSet.WATER, TexamonType.def(Move.BOIL), Type.WATER, 90, 85, 95, 70),
	BIRDERP("Birderp",
		"An annoying bird that flocks wherever people go, begging for food.",
		MoveSet.NORMAL, TexamonType.def(Move.STOMP), Type.NORMAL, 40, 5, 35, 55),
	BOLROCK("Bolrock",
		"A stone-like creature that disguises itself as a boulder.",
		MoveSet.STONE, TexamonType.def(Move.ROCK_TOSS), Type.STONE, 85, 135,
		130, 25),
	C("C", "A glitch in the matrix (trilogy).", new Move[] {Move.M},
		TexamonType.def(Move.M), Type.GLITCH, 178, 90, 90, 90),
	COALDRA("Coaldra",
		"A small dragon which enjoys roasting food with its fiery breath.",
		MoveSet.FIRE, TexamonType.def(Move.MELT), Type.FIRE, 110, 123, 65, 65),
	MAGMUK("Magmuk",
		"A vicious (and viscous) blob of living lava that lives in volcanoes.",
		MoveSet.FIRE, TexamonType.def(Move.MELT), Type.FIRE, 105, 105, 120, 50),
	PLOGGY("Ploggy", "A small frog with a flower on its back.", MoveSet.PLANT,
		TexamonType.def(Move.ENTANGLE), Type.PLANT, 75, 75, 95, 113),
	ROXER("Roxer", "A boxing stone-type with strong stone fists.",
		MoveSet.STONE, TexamonType.def(Move.ROCK_TOSS), Type.STONE, 55, 95,
		115, 35),
	SUNFLO("Sunflo",
		"A flower that attacks predators with its long sharp leaves.",
		MoveSet.PLANT, TexamonType.def(Move.ENTANGLE), Type.PLANT, 80, 105, 65,
		90),
	TELECAT("Telecat",
		"A fearful cat that has learned to teleport away from battle.",
		MoveSet.NORMAL, TexamonType.def(Move.STOMP), Type.NORMAL, 55, 50, 45,
		90),
	WATTLE("Wattle",
		"A cute turtle that likes to sit in water at passing people.",
		MoveSet.WATER, TexamonType.def(Move.BOIL), Type.WATER, 95, 100, 85, 65);

	private static Move[] def(Move one) {
		return TexamonType.def(one, Move.BLANK, Move.BLANK, Move.BLANK);
	}

	private static Move[] def(Move one, Move two, Move three, Move four) {
		return new Move[] {one, two, three, four};
	}

	/**
	 * Returns the texamon with the given name, or C if none is found.
	 *
	 * @param typeName the name of the texamon
	 * @return the texamon with that name
	 */
	public static TexamonType fromType(String typeName) {
		for (TexamonType t : TexamonType.values()) {
			if (t.getName().equals(typeName)) {
				return t;
			}
		}
		return C;
	}

	private String fullName;
	private String description;
	private Type theType;
	private Move[] learnableMoves;
	private Move[] defaultMoves;
	private int hitPoints;
	private int baseATK;
	private int baseDEF;

	private int baseSPD;

	private TexamonType(String name, String descr, Move[] learnable,
		Move[] defMoves, Type type, int hp, int atk, int def, int spd) {
		this.fullName = name;
		this.description = descr;
		this.learnableMoves = learnable;
		this.defaultMoves = defMoves;
		this.theType = type;
		this.hitPoints = hp;
		this.baseATK = atk;
		this.baseDEF = def;
		this.baseSPD = spd;
	}

	public boolean canlearn(Move move) {
		for (Move m : this.learnableMoves) {
			if (move.equals(m)) {
				return true;
			}
		}
		return false;
	}

	public int getATK() {
		return this.baseATK;
	}

	public int getDEF() {
		return this.baseDEF;
	}

	public Move[] getDefaultMoves() {
		return this.defaultMoves;
	}

	public String getDescr() {
		return this.description;
	}

	public int getHP() {
		return this.hitPoints;
	}

	public String getName() {
		return this.fullName;
	}

	public int getSPD() {
		return this.baseSPD;
	}

	public Type getType() {
		return this.theType;
	}
}
