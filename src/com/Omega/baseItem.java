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

public class baseItem {
	String name;
	String descr;
	int type;
	boolean isPotion;

	public String getDescr() {
		return this.descr;
	}

	public String getName() {
		return this.name;
	}

	public int getType() {
		return this.type;
	}

	public boolean itIsAPotion() {
		return this.isPotion;
	}

}