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

import android.util.Log;
import com.Omega.event.Listener;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that has a window and state object that can be used to create
 * anonymous subclasses for registering with the event manager. This is done so
 * that the subclasses have access to the state object in its listener methods.
 * 
 * @author Ches Burks
 *
 */
public abstract class IkWindowListener implements Listener {

	/**
	 * Contains references to outside objects required by the callback function.
	 */
	private Map<Object, Object> state;

	/**
	 * Creates a new listener that has access to the supplied named pointers.
	 * This copies values from the passed map into a new internal map that will
	 * have the same type as the passed one if possible, defaulting to a
	 * {@link HashMap}, to allow for different map types if desired.
	 * 
	 * @param stateObj the map containing the map
	 * @see #setStateObject(Map)
	 */
	public IkWindowListener(Map<Object, Object> stateObj) {
		setStateObject(stateObj);
	}

	/**
	 * Returns the state object for this window. This is used to store named
	 * references to objects that are useful to the window callback. It is left
	 * generic so any key value pair system may be used, but it is suggested to
	 * map meaningful strings to object references.
	 *
	 * @return the state object for the window, or null if none exists.
	 */
	protected synchronized Map<Object, Object> getStateObject() {
		return this.state;
	}

	/**
	 * Returns true if there is a state object for this window. That is, if
	 * {@link #getStateObject()} will return a non-null value. It does not
	 * ensure that the map is populated.
	 *
	 * @return true if there is a state object, false if the state object is
	 *         null.
	 */
	protected synchronized boolean hasStateObject() {
		return this.state != null;
	}

	/**
	 * Sets the state object to the supplied one. This copies values from the
	 * passed map into a new internal map that will have the same type as the
	 * passed one if possible, defaulting to a {@link HashMap}, to allow for
	 * different map types if desired.
	 *
	 * If there is an existing state object, it is cleared and replaced with the
	 * new one.
	 *
	 * If you pass a null, this will simply remove the state object.
	 *
	 * @param newStateObject the new state object to use for this window.
	 */
	@SuppressWarnings("unchecked")
	protected synchronized void setStateObject(
		final Map<Object, Object> newStateObject) {
		if (this.state != null) {
			this.state.clear();
			this.state = null;
		}
		if (newStateObject == null) {
			this.state = null;
			return;
		}
		try {
			this.state = newStateObject.getClass().newInstance();
		}
		catch (InstantiationException e) {
			Log.w("Texamon.IkWindow", e);
		}
		catch (IllegalAccessException e) {
			Log.w("Texamon.IkWindow", e);
		}

		if (this.state == null) { // fall through case
			this.state = new HashMap<Object, Object>();
		}

		this.state.putAll(newStateObject);
	}
}
