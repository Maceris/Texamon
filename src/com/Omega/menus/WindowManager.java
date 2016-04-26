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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.graphics.Canvas;
import android.util.SparseArray;

/**
 * Handles window depths and allows for drawing of registered windows. All
 * methods should be thread-safe.
 *
 * @author Ches Burks
 *
 */
public class WindowManager {

	/**
	 * A mapping of depths to windows on that height
	 */
	private static SparseArray<ArrayList<IkWindow>> windowTable =
			new SparseArray<ArrayList<IkWindow>>();
	private static HashMap<IkWindow, Integer> heightMap =
			new HashMap<IkWindow, Integer>();
	private static HashMap<String, IkWindow> nameMap =
			new HashMap<String, IkWindow>();

	/**
	 * The default/base height for windows.
	 */
	public static final int BASE_HEIGHT = 0;

	/**
	 * A height representing an invalid or non-existent height
	 */
	public static final int ERROR_HEIGHT = Integer.MIN_VALUE;

	/**
	 * Draw all windows on the given canvas in order.
	 *
	 * @param canvas The canvas to draw on
	 */
	public static void drawWindows(Canvas canvas) {
		int curHeight;
		final int size;
		synchronized (WindowManager.windowTable) {
			size = WindowManager.windowTable.size();
		}
		ArrayList<IkWindow> curLayer;
		IkWindow curWindow;
		int curLayerSize;
		int i;
		for (curHeight = 0; curHeight < size; ++curHeight) {
			synchronized (WindowManager.windowTable) {
				curLayer = WindowManager.windowTable.valueAt(curHeight);
			}
			synchronized (curLayer) {
				curLayerSize = curLayer.size();
			}
			for (i = 0; i < curLayerSize; ++i) {
				synchronized (curLayer) {
					curWindow = curLayer.get(i);
				}
				curWindow.draw(canvas);
			}
		}
	}

	/**
	 * Registers a window with a given height. <b>Note:</b> Windows should have
	 * a name associated with them, as otherwise they will be basically
	 * anonymous, and cannot be searched for by name.
	 * 
	 * @param window The window to register
	 * @param height the windows height
	 */
	public static void registerWin(final IkWindow window, final int height) {
		synchronized (WindowManager.heightMap) {
			if (WindowManager.heightMap.containsKey(window)) {
				// The window already exists somewhere
				// TODO decide what to do here
			}
			else {
				// The window is not registered yet
				WindowManager.heightMap.put(window, height);
				ArrayList<IkWindow> heightEntry = null;
				synchronized (WindowManager.windowTable) {
					heightEntry = WindowManager.windowTable.get(height);
					if (heightEntry == null) {
						// that height doesn't exist, so create one
						heightEntry = new ArrayList<IkWindow>();
						WindowManager.windowTable.put(height, heightEntry);
					}
					// now there is an entry
					synchronized (heightEntry) {
						// add the window to the window table
						heightEntry.add(window);
					}
				}
			}
		}
	}

	/**
	 * Registers a window with a given height and name. The name should be
	 * unique to the window.
	 *
	 * @param window The window to register
	 * @param height the windows height
	 * @param name The name of the window
	 */
	public static void registerWin(final IkWindow window, final int height,
			final String name) {
		boolean containsKey = false;
		synchronized (WindowManager.heightMap) {
			containsKey = WindowManager.heightMap.containsKey(window);
		}
		if (containsKey) {
			// The window already exists somewhere
			// TODO decide what to do here
		}
		else {
			WindowManager.registerWin(window, height);
			synchronized (WindowManager.nameMap) {
				if (WindowManager.nameMap.containsKey(name)) {
					// that name is already registered
					// TODO decide what to do here
				}
				else {
					WindowManager.nameMap.put(name, window);
				}
			}
		}
	}

	/**
	 * Sets the height of the window if it currently exists
	 *
	 * @param window the window to modify
	 * @param height the new height to use
	 */
	public static void setHeight(final IkWindow window, final int height) {
		boolean containsKey = false;
		synchronized (WindowManager.heightMap) {
			containsKey = WindowManager.heightMap.containsKey(window);
		}
		if (!containsKey) {
			return;
		}
		final int oldHeight;
		// get the height of the window, swap in the new one
		synchronized (WindowManager.heightMap) {
			oldHeight = WindowManager.heightMap.get(window);
			WindowManager.heightMap.put(window, height);
		}

		// grab the height entry map for the old height
		ArrayList<IkWindow> heightEntry = null;
		synchronized (WindowManager.windowTable) {
			heightEntry = WindowManager.windowTable.get(oldHeight);
		}
		if (heightEntry == null) {
			// the old height doesn't exist, undo changes to height map
			synchronized (WindowManager.heightMap) {
				WindowManager.heightMap.put(window, oldHeight);
			}
			return;
		}

		synchronized (WindowManager.windowTable) {
			synchronized (heightEntry) {
				// remove the old mapping
				heightEntry.remove(window);
			}
		}
		// grab the height entry map for the new height
		heightEntry = null;
		synchronized (WindowManager.windowTable) {
			heightEntry = WindowManager.windowTable.get(height);
		}
		if (heightEntry == null) {
			synchronized (WindowManager.windowTable) {
				heightEntry = new ArrayList<IkWindow>();
				WindowManager.windowTable.put(height, heightEntry);
			}
		}

		synchronized (WindowManager.windowTable) {
			synchronized (heightEntry) {
				// remove the old mapping
				heightEntry.add(window);
			}
		}
	}

	/**
	 * Sets the name of the window if it currently exists
	 *
	 * @param window the window to modify
	 * @param name the new name to use
	 */
	public static void setName(final IkWindow window, final String name) {
		boolean containsKey = false;
		synchronized (WindowManager.heightMap) {
			containsKey = WindowManager.heightMap.containsKey(window);
		}
		if (!containsKey) {
			return;
		}
		String oldName = null;
		synchronized (WindowManager.nameMap) {
			for (Entry<String, IkWindow> entry : WindowManager.nameMap
					.entrySet()) {
				if (entry.getValue() == window) {
					// only should be assigned once
					oldName = entry.getKey();
					break;
				}
			}
			if (oldName != null) {
				WindowManager.nameMap.remove(oldName);
			}
			WindowManager.nameMap.put(name, window);
		}
	}

	/**
	 * Returns the height of the given window, or {@link #ERROR_HEIGHT} if that
	 * window is not registered.
	 *
	 * @param window the window to check for
	 * @return the height of the window or {@link #ERROR_HEIGHT}
	 */
	public static int getHeight(final IkWindow window) {
		synchronized (WindowManager.heightMap) {
			if (WindowManager.heightMap.containsKey(window)) {
				return WindowManager.heightMap.get(window);
			}
		}
		return WindowManager.ERROR_HEIGHT;
	}

	/**
	 * Returns the window mapped to the given name. If none exists, it returns
	 * null.
	 *
	 * @param name The name to look for
	 * @return the window that has the given name
	 */
	public static IkWindow getByName(final String name) {
		synchronized (WindowManager.nameMap) {
			return WindowManager.nameMap.get(name);
		}
	}

	/**
	 * Unregisters the given window
	 *
	 * @param window the window to unregister
	 */
	public static void unregisterWin(final IkWindow window) {
		boolean containsKey = false;
		synchronized (WindowManager.heightMap) {
			containsKey = WindowManager.heightMap.containsKey(window);
		}
		if (!containsKey) {
			return;
		}

		int height;
		// get the height of the window
		synchronized (WindowManager.heightMap) {
			height = WindowManager.heightMap.get(window);
		}

		// grab the height entry map for the given height
		ArrayList<IkWindow> heightEntry = null;
		synchronized (WindowManager.windowTable) {
			heightEntry = WindowManager.windowTable.get(height);
		}
		if (heightEntry == null) {
			// that height doesn't exist
			return;
		}
		// now there is an entry
		synchronized (WindowManager.windowTable) {
			synchronized (heightEntry) {
				// add the window to the window table
				heightEntry.remove(window);
			}
		}
		// remove the window from the height map
		synchronized (WindowManager.heightMap) {
			WindowManager.heightMap.remove(window);
		}
		// remove the window from the name map
		synchronized (WindowManager.nameMap) {
			WindowManager.nameMap.values().remove(window);
		}
	}
}
