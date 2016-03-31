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

import java.util.ArrayList;

import com.Omega.util.SaveUtil;

public class Party implements Saveable {
	public static final int MAX_MEMBERS = 6;
	private ArrayList<Monster> members;

	public Party() {
		this.members = new ArrayList<Monster>();
	}

	@Override
	public void fromText(String text) {
		String[] parts = SaveUtil.split(text);
		int i = 0;
		int count = Integer.valueOf(parts[i]);
		this.members.clear();
		Monster tmpMonster;
		for (int j = 0; j < count; ++j) {
			++i;
			tmpMonster = new Monster(TexamonType.C);
			tmpMonster.fromText(parts[i]);
		}
	}

	public int getSize() {
		synchronized (members) {
			return this.members.size();
		}
	}

	public boolean isFull() {
		synchronized (members) {
			return this.members.size() == Party.MAX_MEMBERS;
		}
	}

	/**
	 * Returns the monster at the given index, or null if it is an invalid index
	 * 
	 * @param index the index of the monster to get
	 * @return the monster at that slot, or null if none exists
	 */
	public Monster get(final int index) {
		if (index < 0) {
			return null;
		}
		synchronized (members) {
			if (index > this.members.size()) {
				return null;
			}
			return members.get(index);
		}
	}

	public void set(final int index, final Monster member) {
		if (index < 0 || index > MAX_MEMBERS) {
			return;
		}
		synchronized (members) {
			this.members.set(index, member);
		}
	}

	/**
	 * Adds the given monster to the team. Returns true if the monster was
	 * added, or false if the team was full or there was some error.
	 * 
	 * @param toAdd the monster to add
	 * @return true if successful, or false otherwise
	 */
	public boolean add(Monster toAdd) {
		if (isFull()) {
			return false;
		}
		synchronized (members) {
			try {
				this.members.add(toAdd);
			}
			catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		String out = "";
		synchronized (members) {
			out += this.members.size();
		}

		final int size;
		synchronized (members) {
			size = this.members.size();
		}
		Monster member;
		for (int i = 0; i < size; ++i) {
			out += Saveable.DELIMITER;
			out += Saveable.NEST_BEGIN;
			synchronized (members) {
				member = this.members.get(i);
			}
			out += member.toString();
			out += Saveable.NEST_END;
		}

		return out;
	}

}
