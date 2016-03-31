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

public class Monster implements Saveable {
	private TexamonType theType;
	private int xp;

	private Move[] moves;

	private String nickname;
	private String location;
	private int UID;
	private int currentHP;
	private int currentLVL;

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

	public int getCurrentHP() {
		return this.currentHP;
	}

	public int getCurrentLVL() {
		return this.currentLVL;
	}

	public String getLocation() {
		return this.location;
	}

	public int getMaxATK() {
		return (this.theType.getATK() + (this.currentLVL / 4));
	}

	public int getMaxDEF() {
		return (this.theType.getDEF() + (this.currentLVL / 4));
	}

	public int getMaxHP() {
		return (this.theType.getHP() + (this.currentLVL / 4));
	}

	public int getMaxSPD() {
		return (this.theType.getSPD() + (this.currentLVL / 4));
	}

	public Move getMove(int index) {
		return this.moves[index];
	}

	public String getNickName() {
		return this.nickname;
	}

	public int getPercentXP() {
		int x = this.xp;
		int y = this.currentLVL * this.currentLVL;
		int z = 0;
		int b = 100 * x;
		z = b / y;
		return z;
	}

	public TexamonType getTexamon() {
		return this.theType;
	}

	public TexamonType getType() {
		return this.theType;
	}

	public int getUID() {
		return this.UID;
	}

	public int getXP() {
		return this.xp;
	}

	public void setCurrentHP(int health) {
		this.currentHP = health;
	}

	public void setCurrentLVL(int level) {
		this.currentLVL = level;
	}

	public void setLocation(String loc) {
		this.location = loc;
	}

	public boolean setMove(int index, Move move) {
		if (this.theType.canlearn(move)) {
			this.moves[index] = move;
			return true;
		}
		return false;

	}

	public void setNickName(String nick) {
		this.nickname = nick;
	}

	public void setType(TexamonType type) {
		this.theType = type;
	}

	public void setUID(int number) {
		this.UID = number;
	}

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