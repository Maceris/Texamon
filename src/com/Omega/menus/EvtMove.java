/*******************************************************************************
 * Copyright (C) 2017  David Burks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.Omega.menus;

import com.Omega.event.Event;

/**
 * A finger or stylus was moved while down and above a window.
 * 
 * @author Ches Burks
 *
 */
public class EvtMove extends Event {

	private final IkWindow win;

	/**
	 * Creates a new {@link EvtMove} for the target window.
	 * 
	 * @param target the window that the move happened above.
	 */
	public EvtMove(IkWindow target) {
		this.win = target;
	}

	/**
	 * Returns the window that was touched.
	 * 
	 * @return the window that was touched.
	 */
	public IkWindow getTarget() {
		return this.win;
	}

}
