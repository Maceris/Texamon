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
package com.Omega.menus;

import com.Omega.menus.WindowDrawer.Border;
import com.Omega.menus.WindowDrawer.ColorScheme;
import com.Omega.menus.WindowDrawer.WinStyle;

/**
 * Contains information needed to draw a window. These are just publicly
 * accessable variables and therefore is inherently thread-unsafe.
 *
 * @author Ches Burks
 *
 */
public class WindowStyle {
	public static final WinStyle defStyle = WinStyle.SQUARE;
	public static final Border defBorder = Border.LINE;
	public static final ColorScheme defColorScheme = ColorScheme.LIGHT;

	public WinStyle style;
	public Border border;
	public ColorScheme colorScheme;

	/**
	 * Constructs a new window style object with the given parameters.
	 *
	 * @param st the WinStyle for the window
	 * @param bo the Border of the window
	 * @param cs the ColorScheme for the window
	 */
	public WindowStyle(WinStyle st, Border bo, ColorScheme cs) {
		this.style = st;
		this.border = bo;
		this.colorScheme = cs;
	}
}
