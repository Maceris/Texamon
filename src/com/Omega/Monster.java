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

import com.Omega.util.SaveUtil;

/**
 * A monster in game, with level, moves, HP etc.
 *
 * @author Ches Burks
 *
 */
public class Monster implements Saveable {
	/**
	 * The maximum number of moves a monster may have ( {@value} )
	 */
	public static final int MAX_MOVES = 4;
	/**
	 * The maximum level that a monster may attain ( {@value} )
	 */
	public static final int MAX_LEVEL = 100;

	private TexamonType theType;

	private int xp;

	private Move[] moves;
	private int[] movePPs;

	private String nickname;
	private String location;
	private int UID;
	private int currentHP;
	private int currentLVL;

	/**
	 * Constructs a new monster of the given type
	 *
	 * @param type the species of monster
	 */
	public Monster(TexamonType type) {
		this.theType = type;
		this.moves = type.getDefaultMoves();
	}

	@Override
	public void fromText(String text) {
		String[] parts = SaveUtil.split(text);
		int i = 0;
		this.theType = TexamonType.fromType(parts[i]);
		++i;
		this.nickname = parts[i];
		++i;
		this.location = parts[i];
		++i;
		this.UID = Integer.valueOf(parts[i]);
		++i;
		this.moves = new Move[Integer.valueOf(parts[i])];
		++i;
		for (int j = 0; j < this.moves.length; ++j) {
			this.moves[j] = Move.fromString(parts[i]);
			++i;
		}
		this.currentHP = Integer.valueOf(parts[i]);
		++i;
		this.currentLVL = Integer.valueOf(parts[i]);
	}

	/**
	 * Returns the monster's current hit points
	 *
	 * @return the HP of the monster
	 */
	public int getCurrentHP() {
		return this.currentHP;
	}

	/**
	 * Returns the monster's current level
	 *
	 * @return the level of the monster
	 */
	public int getCurrentLVL() {
		return this.currentLVL;
	}

	/**
	 * Returns the location of the monster.
	 *
	 * @return the monster's location
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * Returns the attack of the monster modified by the level.
	 *
	 * @return the base attack for this monster
	 */
	public int getMaxATK() {
		return (this.theType.getATK() + (this.currentLVL / 4));
	}

	/**
	 * Returns the defense of the monster modified by the level.
	 *
	 * @return the base defense for this monster
	 */
	public int getMaxDEF() {
		return (this.theType.getDEF() + (this.currentLVL / 4));
	}

	/**
	 * Returns the HP for this monster modified by the level.
	 *
	 * @return the base HP for this monster
	 */
	public int getMaxHP() {
		return (this.theType.getHP() + (this.currentLVL / 4));
	}

	/**
	 * Returns the speed for this monster modified by the level.
	 *
	 * @return the base speed for this monster
	 */
	public int getMaxSPD() {
		return (this.theType.getSPD() + (this.currentLVL / 4));
	}

	/**
	 * Returns the move at the specified index. This should be between 0 and
	 * {@link Monster#MAX_MOVES}
	 *
	 * @param index the index of the move
	 * @return the move at that index, or null if there is not one there
	 */
	public Move getMove(int index) {
		return this.moves[index];
	}

	/**
	 * Returns this monster's nickname.
	 *
	 * @return the nickname of this monster
	 */
	public String getNickName() {
		return this.nickname;
	}

	/**
	 * Returns the percent of the way the monster is to leveling, as an int from
	 * 0 to 100 inclusive.
	 *
	 * @return the percent of the way from the current level to the next
	 */
	public int getPercentXP() {
		final float z = (100f * this.xp) / (this.currentLVL * this.currentLVL);
		return Math.round(z);
	}

	/**
	 * Returns the {@link TexamonType species} of this monster.
	 *
	 * @return the type of the monster
	 */
	public TexamonType getSpecies() {
		return this.theType;
	}

	/**
	 * Returns the unique ID of this monster. By definition this should be
	 * unique to the monster.
	 *
	 * @return the UID of the monster
	 */
	public int getUID() {
		return this.UID;
	}

	/**
	 * Returns the monster's current experience points.
	 *
	 * @return the current XP of the monster
	 */
	public int getXP() {
		return this.xp;
	}

	/**
	 * Sets the health of the monster to the specified value. This is not
	 * checked for bounds.
	 *
	 * @param health the new health of the monster
	 */
	public void setCurrentHP(int health) {
		this.currentHP = health;
	}

	/**
	 * Sets the current level of the monster to the specified value. If not
	 * between 0 and {@link Monster#MAX_LEVEL} it does not actually set the
	 * value.
	 *
	 * @param level the new level of the monster
	 */
	public void setCurrentLVL(int level) {
		if (level < 0) {
			return;
		}
		if (level > Monster.MAX_LEVEL) {
			return;
		}
		this.currentLVL = level;
	}

	/**
	 * Sets the current location of the monster.
	 *
	 * @param loc the new location
	 */
	public void setLocation(String loc) {
		this.location = loc;
	}

	/**
	 * Sets the move at the given index to the given move. If the index is
	 * invalid or the monster cannot learn that move, it returns false. If the
	 * move was set, returns true.
	 *
	 * @param index the index of the move
	 * @param move the move to learn
	 * @return true on success, false if there was a problem and it was not set
	 */
	public boolean setMove(int index, Move move) {
		if (index < 0 || index >= this.moves.length) {
			return false;
		}
		if (this.theType.canlearn(move)) {
			this.moves[index] = move;
			return true;
		}
		return false;

	}

	/**
	 * Sets the current nickname of the monster.
	 *
	 * @param nick the new nickname
	 */
	public void setNickName(String nick) {
		this.nickname = nick;
	}

	/**
	 * Sets the current type of the monster to the given one. This changes the
	 * species so really it shouldn't ever be used.
	 *
	 * @param species the new species of the monster
	 */
	public void setSpecies(TexamonType species) {
		this.theType = species;
	}

	/**
	 * Sets the unique ID value of the monster. No checking takes place so it is
	 * up to the caller to ensure the number is actually unique to this monster.
	 *
	 * @param number the new UID of this monster
	 */
	public void setUID(int number) {
		this.UID = number;
	}

	/**
	 * Sets the experience points of the monster. No value checking takes place,
	 * and the monster will not automatically level up.
	 *
	 * @param newXP the new XP value
	 */
	public void setXP(int newXP) {
		this.xp = newXP;
	}

	@Override
	public String toString() {
		String out = "";
		out += this.theType.getName();
		out += Saveable.DELIMITER;
		out += SaveUtil.escape(this.nickname);
		out += Saveable.DELIMITER;
		out += this.location;
		out += Saveable.DELIMITER;
		out += this.UID;
		out += Saveable.DELIMITER;
		out += this.moves.length;
		out += Saveable.DELIMITER;
		for (int i = 0; i < this.moves.length; ++i) {
			out += this.moves[i].getName();
			out += Saveable.DELIMITER;
		}
		out += this.currentHP;
		out += Saveable.DELIMITER;
		out += this.currentLVL;
		out += Saveable.DELIMITER;
		return out;
	}
}