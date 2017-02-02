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

/**
 * A group of monsters with a set size.
 * 
 * @author Ches Burks
 *
 */
public class Party implements Saveable {
	/**
	 * The maximum number of members that a party may contain ( {@value} )
	 */
	public static final int MAX_MEMBERS = 6;
	private ArrayList<Monster> members;

	/**
	 * Constructs a new empty party
	 */
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

	/**
	 * Returns the number of members currently in the party.
	 * 
	 * @return the size of the members list
	 */
	public int getSize() {
		synchronized (this.members) {
			return this.members.size();
		}
	}

	/**
	 * Returns true if the number of members is the maximum number that a team
	 * may hold.
	 * 
	 * @see #MAX_MEMBERS
	 * 
	 * @return true if the party is of max size, false otherwise
	 */
	public boolean isFull() {
		synchronized (this.members) {
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
		synchronized (this.members) {
			if (index > this.members.size()) {
				return null;
			}
			return this.members.get(index);
		}
	}

	/**
	 * Sets the monster at the given index to the specified member. If the index
	 * is not valid (i.e. negative, or above the max index), null is returned
	 * and no change is made.
	 * 
	 * @param index the index to set or replace
	 * @param member the new member at the set index
	 * @return null or the member that was in that index before if it replaced
	 *         one
	 */
	public Monster set(final int index, final Monster member) {
		if (index < 0 || index > Party.MAX_MEMBERS) {
			return null;
		}
		Monster old = null;
		synchronized (this.members) {
			if (this.members.get(index) != null) {
				old = this.members.get(index);
			}
			this.members.set(index, member);
		}
		return old;
	}

	/**
	 * Adds the given monster to the team. Returns true if the monster was
	 * added, or false if the team was full or there was some error.
	 *
	 * @param toAdd the monster to add
	 * @return true if successful, or false otherwise
	 */
	public boolean add(Monster toAdd) {
		if (this.isFull()) {
			return false;
		}
		synchronized (this.members) {
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
		synchronized (this.members) {
			out += this.members.size();
		}

		final int size;
		synchronized (this.members) {
			size = this.members.size();
		}
		Monster member;
		for (int i = 0; i < size; ++i) {
			out += Saveable.DELIMITER;
			out += Saveable.NEST_BEGIN;
			synchronized (this.members) {
				member = this.members.get(i);
			}
			out += member.toString();
			out += Saveable.NEST_END;
		}

		return out;
	}

}
