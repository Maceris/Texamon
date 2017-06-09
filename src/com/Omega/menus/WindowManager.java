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

import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import com.Omega.Point;
import com.Omega.event.EventManager;
import com.Omega.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * Handles window depths and allows for drawing of registered windows. All
 * methods should be thread-safe.
 *
 * @author Ches Burks
 *
 */
public class WindowManager implements Listener {

	/**
	 * A mapping of depths to windows on that height
	 */
	private static SparseArray<ArrayList<IkWindow>> windowTable =
		new SparseArray<ArrayList<IkWindow>>();
	private static HashMap<IkWindow, Integer> heightMap =
		new HashMap<IkWindow, Integer>();
	private static HashMap<String, IkWindow> nameMap =
		new HashMap<String, IkWindow>();
	private static HashMap<IkWindow, ArrayList<Listener>> listenerMap =
		new HashMap<IkWindow, ArrayList<Listener>>();

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
		if (canvas == null) {
			throw new NullPointerException();
		}
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
	 * Returns the window mapped to the given name. If none exists, it returns
	 * null.
	 *
	 * @param name The name to look for
	 * @return the window that has the given name
	 */
	public static IkWindow getByName(final String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		synchronized (WindowManager.nameMap) {
			return WindowManager.nameMap.get(name);
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
		if (window == null) {
			throw new NullPointerException();
		}
		synchronized (WindowManager.heightMap) {
			if (WindowManager.heightMap.containsKey(window)) {
				return WindowManager.heightMap.get(window);
			}
		}
		return WindowManager.ERROR_HEIGHT;
	}

	/**
	 * Returns the name that is mapped to the given window. If none exists, it
	 * returns null. Note that this is slow because it must check each name to
	 * see if it is assigned to the given window, and there isn't really a
	 * guarantee that a window won't have multiple names, so this is just the
	 * first one it finds (assume its a random from the set of possible names,
	 * which is hopefully only 1 name).
	 *
	 * @param name The window to look for
	 * @return the name that maps to that window if it exists, or null.
	 */
	public static String getName(final IkWindow name) {
		if (name == null) {
			throw new NullPointerException();
		}
		synchronized (WindowManager.nameMap) {
			for (String s : WindowManager.nameMap.keySet()) {
				if (WindowManager.nameMap.get(s) == name) {
					return s;
				}
			}
			return null;
		}
	}

	/**
	 * Find the window that contains the point touched with the highest height.
	 * If the window has an action associated with being clicked, that action is
	 * performed.
	 *
	 * @param scrWidth the width of the screen/canvas the window is in, in
	 *            pixels.
	 * @param scrHeight the height of the screen/canvas the window is in, in
	 *            pixels.
	 * @param p the point to check for on the screen, as an (x, y) pair
	 *            representing the pixels from the top left corner of the
	 *            screen.
	 * @param action The motion event to process, used to determine whether what
	 *            kind of event (down, up, drag, etc) is going on. These are the
	 *            constants in {@link MotionEvent}.
	 */
	public static void handleMotionEvent(final int scrWidth,
		final int scrHeight, final Point p, final int action) {
		if (p == null) {
			throw new NullPointerException();
		}

		// For firing the evtUp even if not above a window
		boolean eventUpFired = false;

		// loop from highest height value down to lowest
		HEIGHT_LOOP:
		for (int curIndex = WindowManager.windowTable.size() - 1; curIndex >= 0; --curIndex) {
			for (IkWindow win : WindowManager.windowTable.valueAt(curIndex)) {
				if (!win.isVisible()) {
					continue;
				}
				if (win.containsPoint(scrWidth, scrHeight, p)) {

					switch (action) {
						case MotionEvent.ACTION_DOWN:
							EventManager.getInstance().fireEvent(
								new EvtDown(win));
							break;
						case MotionEvent.ACTION_MOVE:
							// this case might bottleneck
							EventManager.getInstance().fireEvent(
								new EvtMove(win));
							break;
						case MotionEvent.ACTION_UP:
							EventManager.getInstance()
								.fireEvent(new EvtUp(win));
							eventUpFired = true;
							break;
						default:
							Log.i("Texamon.WindowManager",
								"Unknown motionevent passed to motion event handler.");
							break;
					}
					if (win.consumesTouches()) {
						break HEIGHT_LOOP;
					}
				}
			}
		}
		if (action == MotionEvent.ACTION_UP && !eventUpFired) {
			EventManager.getInstance().fireEvent(new EvtUp(null));
		}
	}

	/**
	 * Registers an event listener for a given window with the event manager,
	 * and also keeps track of it internally so that the listeners can be
	 * cleaned up when the window is no longer used.
	 *
	 * @param win the window to add a listener for
	 * @param listener the listener to use
	 */
	public static void registerListener(IkWindow win, Listener listener) {
		if (win == null) {
			throw new NullPointerException();
		}
		if (listener == null) {
			throw new NullPointerException();
		}
		synchronized (WindowManager.listenerMap) {
			if (!WindowManager.listenerMap.containsKey(win)) {
				WindowManager.listenerMap.put(win, new ArrayList<Listener>());
			}
			ArrayList<Listener> list = WindowManager.listenerMap.get(win);
			synchronized (list) {
				if (list.contains(listener)) {
					// The window already exists somewhere
					// TODO decide what to do here
					Log.w("Texamon.WindowManager",
						"Registering a listener that already exists for a window");
				}
				else {
					list.add(listener);
				}
			}
		}
		EventManager.getInstance().registerEventListeners(listener);
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
		if (window == null) {
			throw new NullPointerException();
		}
		synchronized (WindowManager.heightMap) {
			if (WindowManager.heightMap.containsKey(window)) {
				// The window already exists somewhere
				// TODO decide what to do here
				Log.w("Texamon.WindowManager",
					"Registering a window that already exists");
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
		if (window == null) {
			throw new NullPointerException();
		}
		if (name == null) {
			throw new NullPointerException();
		}
		boolean containsKey = false;
		synchronized (WindowManager.heightMap) {
			containsKey = WindowManager.heightMap.containsKey(window);
		}
		if (containsKey) {
			// The window already exists somewhere
			// TODO decide what to do here
			Log.w("Texamon.WindowManager", "Registering a window (" + name
				+ ") that already exists");
		}
		else {
			WindowManager.registerWin(window, height);
			synchronized (WindowManager.nameMap) {
				if (WindowManager.nameMap.containsKey(name)) {
					// that name is already registered
					// TODO decide what to do here
					Log.w("Texamon.WindowManager", "Registering a window ("
						+ name + ") that already exists");
				}
				else {
					WindowManager.nameMap.put(name, window);
				}
			}
		}
	}

	/**
	 * Updates the height of the window if it currently exists, or sets it if it
	 * does not.
	 *
	 * @param window the window to modify
	 * @param height the new height to use
	 */
	public static void setHeight(final IkWindow window, final int height) {
		if (window == null) {
			throw new NullPointerException();
		}
		synchronized (WindowManager.heightMap) {
			synchronized (WindowManager.windowTable) {
				ArrayList<IkWindow> heightEntry = null;
				if (WindowManager.heightMap.containsKey(window)) {
					final int oldHeight;
					// get the height of the window, swap in the new one

					oldHeight = WindowManager.heightMap.get(window);
					WindowManager.heightMap.put(window, height);

					// grab the height entry map for the old height
					heightEntry = WindowManager.windowTable.get(oldHeight);
					if (heightEntry == null) {
						// the old height doesn't exist, undo changes to height
						// map
						WindowManager.heightMap.put(window, oldHeight);
						return;
					}

					synchronized (heightEntry) {
						// remove the old mapping
						heightEntry.remove(window);
					}
				}
				else {
					synchronized (WindowManager.heightMap) {
						WindowManager.heightMap.put(window, height);
					}
				}
				// grab the height entry map for the new height
				heightEntry = null;
				heightEntry = WindowManager.windowTable.get(height);
				if (heightEntry == null) {
					heightEntry = new ArrayList<IkWindow>();
					WindowManager.windowTable.put(height, heightEntry);
				}
				synchronized (heightEntry) {
					// remove the old mapping
					heightEntry.add(window);
				}
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
		if (window == null) {
			throw new NullPointerException();
		}
		if (name == null) {
			throw new NullPointerException();
		}
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
	 * Unregisters and clears up all windows.
	 */
	public static void unregisterAll() {
		synchronized (WindowManager.windowTable) {
			HashSet<IkWindow> allWindows = new HashSet<IkWindow>();
			for (int i = 0; i < WindowManager.windowTable.size(); ++i) {
				allWindows.addAll(WindowManager.windowTable.valueAt(i));
			}
			for (IkWindow win : allWindows) {
				WindowManager.unregisterWin(win);
				win.delete();
			}
		}
	}

	/**
	 * Unregisters listeners with the event manager that are assigned to the
	 * given window, and removes them from the internal listener tracking
	 * system.
	 *
	 * @param win the window to remove and unregister listeners for
	 */
	public static void unregisterListeners(IkWindow win) {
		if (win == null) {
			throw new NullPointerException();
		}
		EventManager mgr = EventManager.getInstance();
		synchronized (WindowManager.listenerMap) {
			if (!WindowManager.listenerMap.containsKey(win)) {
				Log.w("Texamon.WindowManager",
					"Unregistering listeners for a window without any");
				return;
			}
			ArrayList<Listener> list = WindowManager.listenerMap.get(win);
			synchronized (list) {
				for (Listener l : list) {
					mgr.unregisterEventListeners(l);
				}
				list.clear();
			}
			WindowManager.listenerMap.remove(win);
		}
	}

	/**
	 * Unregisters the given window
	 *
	 * @param window the window to unregister
	 */
	public static void unregisterWin(final IkWindow window) {
		if (window == null) {
			throw new NullPointerException();
		}
		synchronized (WindowManager.heightMap) {
			synchronized (WindowManager.windowTable) {
				if (WindowManager.heightMap.containsKey(window)) {
					int height;
					// get the height of the window
					height = WindowManager.heightMap.get(window);

					// grab the height entry map for the given height
					ArrayList<IkWindow> heightEntry = null;
					heightEntry = WindowManager.windowTable.get(height);
					if (heightEntry != null) {
						synchronized (heightEntry) {
							// remove the window from the height table
							heightEntry.remove(window);
						}
					}
					// remove the window from the height map
					WindowManager.heightMap.remove(window);
				}
			}
		}
		// remove the window from the name map
		synchronized (WindowManager.nameMap) {
			WindowManager.nameMap.values().remove(window);
		}

		WindowManager.unregisterListeners(window);
	}

}
