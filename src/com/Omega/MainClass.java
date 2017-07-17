/*******************************************************************************
 * Copyright (C) 2016, 2017 David Burks
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import com.Omega.event.EventHandler;
import com.Omega.event.EventManager;
import com.Omega.event.Listener;
import com.Omega.menus.Alignment;
import com.Omega.menus.EvtDown;
import com.Omega.menus.EvtMove;
import com.Omega.menus.EvtUp;
import com.Omega.menus.IkWindow;
import com.Omega.menus.IkWindowListener;
import com.Omega.menus.Menu;
import com.Omega.menus.MenuItem;
import com.Omega.menus.WindowDrawer.Border;
import com.Omega.menus.WindowDrawer.ColorScheme;
import com.Omega.menus.WindowDrawer.WinStyle;
import com.Omega.menus.WindowManager;
import com.Omega.menus.WindowStyle;
import com.Omega.util.DuplicateEntry;
import com.Omega.util.IntegerTree;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import tiled.core.Map;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeMap;

/**
 * The main class, houses the activity.
 *
 * @author Ches Burks
 *
 */
public class MainClass extends Activity implements Runnable {

	private static class MyView extends View //
	{
		/**
		 * Create a bitmap based on the data in the map structure.
		 *
		 * @param m data structure describing the TileMap
		 * @param c application context
		 * @param startLayer index of the first layer to render
		 * @param endLayer index of the last layer to render
		 * @return bitmap of the map
		 */
		public static Bitmap createBitmap(Map m, Context c, int startLayer,
			int endLayer) {

			try {
				AssetManager assetManager = c.getAssets();

				// Create a bitmap of the size of the map.
				// Straight up creating a bitmap of arbitrary size is huge in
				// memory, but this is
				// sufficient for small demo purposes.

				// In a production engine, map size should either be restricted,
				// or the map should be loaded to memory on the fly.
				Bitmap mapImage =
					Bitmap.createBitmap(m.getWidth() * m.getTileWidth(),
						m.getHeight() * m.getTileHeight(),
						Bitmap.Config.ARGB_8888);

				// Load all tilesets that are used into memory. Again, not
				// very efficient, but loading the image, dereferencing, and
				// running
				// the gc for each image is not a fast or good option.
				// Still, a better way is forthcoming if I can think of one.
				Bitmap[] tilesets = new Bitmap[m.getTileSets().size()];

				for (int i = 0; i < tilesets.length; i++) {
					tilesets[i] =
						BitmapFactory.decodeStream(assetManager.open(m
							.getTileSets().get(i).getTilebmpFile()
							.replaceAll("^/", "")));
				}

				// Create a Canvas reference to our map Bitmap
				// so that we can blit to it.

				Canvas mapCanvas = new Canvas(mapImage);

				// Loop through all layers and x and y-positions
				// to render all the tiles.

				// Later I'll add in an option for specifying which layers
				// to display, in case some hold invisible or meta-tiles.

				int currentGID;
				Long localGID = null;
				Rect source = new Rect(0, 0, 0, 0);
				Rect dest = new Rect(0, 0, 0, 0);
				Tile t;
				Paint paint = new Paint();
				for (int i = startLayer; i < endLayer; i++) {

					for (int j = 0; j < m.getLayers().get(i).getHeight(); j++) {

						for (int k = 0; k < m.getLayers().get(i).getWidth(); k++) {

							t = ((TileLayer) m.getLayer(i)).getTileAt(k, j);
							if (t == null) {// it doesnt store blank tiles.
								continue;
							}
							currentGID = t.getGid();
							// debug
							// if (localGID == null) Log.d("GID",
							// "Read problem");

							int currentFirstGID;
							for (int l = m.getTileSets().size() - 1; i >= 0; i--) {
								currentFirstGID =
									m.getTileSets().get(l).getFirstGid();
								if (currentFirstGID <= currentGID) {
									localGID =
										Long.valueOf(currentGID
											- currentFirstGID);
									break;
								}
							}

							// The row number is the number of tiles wide the
							// image
							// is divided by
							// the tile number

							// Check that this space isn't buggy or undefined,
							// and
							// if everything's fine, blit to the current x, y
							// position
							if (localGID != null) {
								source.top = 0;
								source.left = 0;
								source.bottom = t.getHeight();
								source.right = t.getWidth();

								dest.top = j * m.getTileHeight();
								dest.left = k * m.getTileWidth();
								dest.bottom = dest.top + m.getTileHeight();
								dest.right = dest.left + m.getTileWidth();

								mapCanvas.drawBitmap(t.getImage(), source,
									dest, paint);
							}

						}

					}

				}

				return mapImage;

			}
			catch (IOException e) {
				// In case the tilemap files are missing
				e.printStackTrace(System.err);
			}

			// In case the image didn't load properly
			return null;
		}

		IntegerTree idTree;

		boolean doLogging = false;
		boolean firstTime; // indicates that this is the beginning of a run

		int CANVAS_WIDTH = -1;
		int CANVAS_HEIGHT = -1;

		boolean canvasSizeDirty = true;

		GameData gameData;

		MovableObject player;

		private SecureRandom rand;

		AlertDialog.Builder comingsoonbuilder = new AlertDialog.Builder(
			this.getContext());

		AlertDialog comingsoonalert = this.comingsoonbuilder.create();

		AlertDialog.Builder errorbuilder = new AlertDialog.Builder(
			this.getContext());

		AlertDialog erroralert;

		Context thisContext;
		int winWidth = 0;

		int winHeight = 0;

		final int TILES_PER_SCREEN = 11;

		final int COUNT_MAX = 10;

		int count = this.COUNT_MAX;// TODO remove this
		Rect mapSource = new Rect();

		Rect mapDest = new Rect();

		public MyView(Context context, GameData newData) {

			super(context);

			this.gameData = newData;

			this.rand = new SecureRandom();
			// throw away
			this.rand.nextInt();

			TMXMapReader reader = new TMXMapReader();
			Map aurageMap = null;

			try {
				aurageMap =
					reader.readMap(context.getAssets().open("aurage.tmx"),
						context);
			}
			catch (Exception e) {
				e.printStackTrace(System.err);
				System.err.println(reader.getError());
			}
			if (aurageMap == null) {
				System.err.println("Null map");
				System.exit(-1);
				return;// can't reach this, but gets rid of null ptr warnings
			}
			Bitmap mapImg = MyView.createBitmap(aurageMap, context, 0, 8);
			if (mapImg == null) {
				System.err.println("Null map image");
				System.exit(-1);
			}
			this.gameData.setCurrentMap(aurageMap);
			this.gameData.setCurMapImg(mapImg);

			this.gameData.setxPos(aurageMap.getWidth() / 2);
			this.gameData.setyPos(aurageMap.getHeight() / 2);

			this.thisContext = context;
			this.firstTime = true;

			this.gameData.setState(GameState.MAIN_MENU);
			// TODO have a main menu

			// creating windows
			// WindowManager.registerWin(, WindowManager.BASE_HEIGHT, "");
			WindowManager.registerWin(new IkWindow(new Point(0.05f, 0.05f),
				Alignment.NORTH_EAST, new Point(0.1f, 0.05f)),
				WindowManager.BASE_HEIGHT, "Window.StartButton");
			WindowManager.registerWin(new IkWindow(new Point(0.05f, 0.05f),
				Alignment.NORTH_EAST, new Point(0.5f, 0.3f)),
				WindowManager.BASE_HEIGHT, "Window.Start");
			WindowManager.registerWin(new IkWindow(new Point(0.0f, 0.00f),
				Alignment.NORTH_EAST, new Point(1.0f, 1.0f)),
				WindowManager.BASE_HEIGHT, "Window.Texamon");
			WindowManager.registerWin(new IkWindow(new Point(-1.0f, 0.05f),
				Alignment.WEST, new Point(1.0f, 0.5f)),
				WindowManager.BASE_HEIGHT, "Window.Bag");

			WindowManager.registerWin(new IkWindow(new Point(0.05f, 0.05f),
				Alignment.SOUTH_WEST, new Point(0.3f, 0.3f)),
				WindowManager.BASE_HEIGHT, "Window.DPad");
			final float ONE_THIRD = 1.0f / 3.0f;
			WindowManager.registerWin(new IkWindow(new Point(0.0f, 0.0f),
				Alignment.NORTH, new Point(ONE_THIRD, ONE_THIRD)),
				WindowManager.BASE_HEIGHT, "Window.DPad.Up");
			WindowManager.registerWin(new IkWindow(new Point(0.0f, 0.0f),
				Alignment.EAST, new Point(ONE_THIRD, ONE_THIRD)),
				WindowManager.BASE_HEIGHT, "Window.DPad.Right");
			WindowManager.registerWin(new IkWindow(new Point(0.0f, 0.0f),
				Alignment.SOUTH, new Point(ONE_THIRD, ONE_THIRD)),
				WindowManager.BASE_HEIGHT, "Window.DPad.Down");
			WindowManager.registerWin(new IkWindow(new Point(0.0f, 0.0f),
				Alignment.WEST, new Point(ONE_THIRD, ONE_THIRD)),
				WindowManager.BASE_HEIGHT, "Window.DPad.Left");
			WindowManager.registerWin(new IkWindow(new Point(0.0f, 0.0f),
				Alignment.CENTER, new Point(ONE_THIRD, ONE_THIRD)),
				WindowManager.BASE_HEIGHT, "Window.DPad.Center");

			WindowManager.registerWin(new Menu(1, 1),
				WindowManager.BASE_HEIGHT, "Menu.StartButton");
			WindowManager.registerWin(new MenuItem("Start"),
				WindowManager.BASE_HEIGHT, "MenuItem.StartButton.Start");

			WindowManager.registerWin(new Menu(1, 6),
				WindowManager.BASE_HEIGHT, "Menu.Start");
			WindowManager.registerWin(new MenuItem("Texamon"),
				WindowManager.BASE_HEIGHT, "MenuItem.Start.Texamon");
			WindowManager.registerWin(new MenuItem("Items"),
				WindowManager.BASE_HEIGHT, "MenuItem.Start.Items");
			WindowManager.registerWin(new MenuItem("Player"),
				WindowManager.BASE_HEIGHT, "MenuItem.Start.Player");
			WindowManager.registerWin(new MenuItem("Save"),
				WindowManager.BASE_HEIGHT, "MenuItem.Start.Save");
			WindowManager.registerWin(new MenuItem("Options"),
				WindowManager.BASE_HEIGHT, "MenuItem.Start.Options");
			WindowManager.registerWin(new MenuItem("Exit"),
				WindowManager.BASE_HEIGHT, "MenuItem.Start.Exit");

			WindowManager.registerWin(new Menu(1, 6),
				WindowManager.BASE_HEIGHT, "Menu.Bag");

			IkWindow wStart = WindowManager.getByName("Window.Start");
			IkWindow wBag = WindowManager.getByName("Window.Bag");
			IkWindow wTexamon = WindowManager.getByName("Window.Texamon");
			IkWindow wStartButton =
				WindowManager.getByName("Window.StartButton");
			IkWindow wDPad = WindowManager.getByName("Window.DPad");
			IkWindow wDPadUp = WindowManager.getByName("Window.DPad.Up");
			IkWindow wDPadRight = WindowManager.getByName("Window.DPad.Right");
			IkWindow wDPadDown = WindowManager.getByName("Window.DPad.Down");
			IkWindow wDPadLeft = WindowManager.getByName("Window.DPad.Left");
			IkWindow wDPadCenter =
				WindowManager.getByName("Window.DPad.Center");
			Menu mStartButton =
				(Menu) WindowManager.getByName("Menu.StartButton");
			Menu mStart = (Menu) WindowManager.getByName("Menu.Start");
			Menu mBag = (Menu) WindowManager.getByName("Menu.Bag");
			MenuItem menuButton =
				(MenuItem) WindowManager
					.getByName("MenuItem.StartButton.Start");
			MenuItem miTexamon =
				(MenuItem) WindowManager.getByName("MenuItem.Start.Texamon");
			MenuItem miItems =
				(MenuItem) WindowManager.getByName("MenuItem.Start.Items");
			MenuItem miPlayer =
				(MenuItem) WindowManager.getByName("MenuItem.Start.Player");
			MenuItem miSave =
				(MenuItem) WindowManager.getByName("MenuItem.Start.Save");
			MenuItem miOptions =
				(MenuItem) WindowManager.getByName("MenuItem.Start.Options");
			MenuItem exitButton =
				(MenuItem) WindowManager.getByName("MenuItem.Start.Exit");

			wStartButton.setConsumeTouches(false);

			WindowManager.registerListener(menuButton, new Listener() {
				@EventHandler
				public void onEvtDown(EvtDown event) {
					if (!event.getTarget().equals(
						WindowManager.getByName("MenuItem.StartButton.Start"))) {
						return;
					}
					(WindowManager.getByName("Window.Start")).setVisible(true);
					(WindowManager.getByName("Window.StartButton"))
						.setVisible(false);
				}
			});

			// menuButton is a child in the startMenu
			mStartButton.addChild(menuButton);
			// and startMenu is a child of the wStartButton window
			wStartButton.addChild(mStartButton);

			WindowManager.registerListener(exitButton, new Listener() {
				@EventHandler
				public void onEvtDown(EvtDown event) {
					if (!event.getTarget().equals(
						WindowManager.getByName("MenuItem.Start.Exit"))) {
						return;
					}
					(WindowManager.getByName("Window.Start")).setVisible(false);
					(WindowManager.getByName("Window.StartButton"))
						.setVisible(true);
				}
			});

			mStart.addChild(miTexamon);
			mStart.addChild(miItems);
			mStart.addChild(miPlayer);
			mStart.addChild(miSave);
			mStart.addChild(miOptions);
			mStart.addChild(exitButton);
			wStart.addChild(mStart);

			mBag.addChild(new MenuItem("Small Potion"));
			mBag.addChild(new MenuItem("Medium Potion"));
			mBag.addChild(new MenuItem("Large Potion"));
			mBag.addChild(new MenuItem("Full Potion"));
			mBag.addChild(new MenuItem("TBall"));
			mBag.addChild(new MenuItem("Great TBall"));
			wBag.addChild(mBag);
			wStart.addChild(wBag);

			wStart.setVisible(false);
			wBag.setVisible(false);
			wTexamon.setVisible(false);

			// format dpad
			wDPad.setStyle(new WindowStyle(WinStyle.EMPTY_SQUARE,
				Border.BORDERLESS, ColorScheme.DARK));
			WindowStyle buttonScheme =
				new WindowStyle(WinStyle.SQUARE, Border.BORDERLESS,
					ColorScheme.DARK);
			WindowStyle pressedScheme =
				new WindowStyle(WinStyle.SQUARE, Border.BORDERLESS,
					ColorScheme.LIGHT);

			wDPadUp.setStyle(buttonScheme);
			wDPadRight.setStyle(buttonScheme);
			wDPadDown.setStyle(buttonScheme);
			wDPadLeft.setStyle(buttonScheme);
			wDPadCenter.setStyle(buttonScheme);

			HashMap<Object, Object> dPadState = new HashMap<Object, Object>();
			dPadState.put("gameData", this.gameData);
			dPadState.put("up", wDPadUp);
			dPadState.put("right", wDPadRight);
			dPadState.put("down", wDPadDown);
			dPadState.put("left", wDPadLeft);
			dPadState.put("pressed", pressedScheme);
			dPadState.put("unpressed", buttonScheme);

			dPadState.put("owner", wDPadUp);
			WindowManager.registerListener(wDPadUp, new IkWindowListener(
				dPadState) {
				@EventHandler
				public void onEvtDown(EvtDown event) {

					if (!this.hasStateObject()) {
						Log.e("Texamon.MainClass",
							"D-Pad button state object is null",
							new NullPointerException());
						return;
					}

					java.util.Map<Object, Object> state = this.getStateObject();

					if (!event.getTarget().equals(state.get("owner"))) {
						return;
					}
					IkWindow up = (IkWindow) state.get("up");
					IkWindow right = (IkWindow) state.get("right");
					IkWindow down = (IkWindow) state.get("down");
					IkWindow left = (IkWindow) state.get("left");
					GameData data = (GameData) state.get("gameData");
					WindowStyle pressed = (WindowStyle) state.get("pressed");
					WindowStyle unpressed =
						(WindowStyle) state.get("unpressed");
					synchronized (data) {
						if (data.isMoveRight()) {
							right.setStyle(unpressed);
						}
						else if (data.isMoveDown()) {
							down.setStyle(unpressed);
						}
						else if (data.isMoveLeft()) {
							left.setStyle(unpressed);
						}
						data.moveUp();
						up.setStyle(pressed);
					}
				}

				@EventHandler
				public void onEvtMove(EvtMove event) {
					if (!this.hasStateObject()) {
						Log.e("Texamon.MainClass",
							"D-Pad button state object is null",
							new NullPointerException());
						return;
					}

					java.util.Map<Object, Object> state = this.getStateObject();

					if (!event.getTarget().equals(state.get("owner"))) {
						return;
					}

					IkWindow up = (IkWindow) state.get("up");
					IkWindow right = (IkWindow) state.get("right");
					IkWindow down = (IkWindow) state.get("down");
					IkWindow left = (IkWindow) state.get("left");
					GameData data = (GameData) state.get("gameData");
					WindowStyle pressed = (WindowStyle) state.get("pressed");
					WindowStyle unpressed =
						(WindowStyle) state.get("unpressed");

					synchronized (data) {
						if (data.isMoveRight()) {
							right.setStyle(unpressed);
						}
						else if (data.isMoveDown()) {
							down.setStyle(unpressed);
						}
						else if (data.isMoveLeft()) {
							left.setStyle(unpressed);
						}
						data.moveUp();
						up.setStyle(pressed);
					}
				}

			});
			dPadState.remove("owner");

			dPadState.put("owner", wDPadRight);
			WindowManager.registerListener(wDPadRight, new IkWindowListener(
				dPadState) {
				@EventHandler
				public void onEvtDown(EvtDown event) {
					if (!this.hasStateObject()) {
						Log.e("Texamon.MainClass",
							"D-Pad button state object is null",
							new NullPointerException());
						return;
					}

					java.util.Map<Object, Object> state = this.getStateObject();

					if (!event.getTarget().equals(state.get("owner"))) {
						return;
					}
					IkWindow up = (IkWindow) state.get("up");
					IkWindow right = (IkWindow) state.get("right");
					IkWindow down = (IkWindow) state.get("down");
					IkWindow left = (IkWindow) state.get("left");
					GameData data = (GameData) state.get("gameData");
					WindowStyle pressed = (WindowStyle) state.get("pressed");
					WindowStyle unpressed =
						(WindowStyle) state.get("unpressed");

					synchronized (data) {
						if (data.isMoveUp()) {
							up.setStyle(unpressed);
						}
						else if (data.isMoveDown()) {
							down.setStyle(unpressed);
						}
						else if (data.isMoveLeft()) {
							left.setStyle(unpressed);
						}
						data.moveRight();
						right.setStyle(pressed);
					}
				}

				@EventHandler
				public void onEvtMove(EvtMove event) {
					if (!this.hasStateObject()) {
						Log.e("Texamon.MainClass",
							"D-Pad button state object is null",
							new NullPointerException());
						return;
					}

					java.util.Map<Object, Object> state = this.getStateObject();

					if (!event.getTarget().equals(state.get("owner"))) {
						return;
					}

					IkWindow up = (IkWindow) state.get("up");
					IkWindow right = (IkWindow) state.get("right");
					IkWindow down = (IkWindow) state.get("down");
					IkWindow left = (IkWindow) state.get("left");
					GameData data = (GameData) state.get("gameData");
					WindowStyle pressed = (WindowStyle) state.get("pressed");
					WindowStyle unpressed =
						(WindowStyle) state.get("unpressed");

					synchronized (data) {
						if (data.isMoveUp()) {
							up.setStyle(unpressed);
						}
						else if (data.isMoveDown()) {
							down.setStyle(unpressed);
						}
						else if (data.isMoveLeft()) {
							left.setStyle(unpressed);
						}
						data.moveRight();
						right.setStyle(pressed);
					}
				}

			});
			dPadState.remove("owner");

			dPadState.put("owner", wDPadDown);
			WindowManager.registerListener(wDPadDown, new IkWindowListener(
				dPadState) {
				@EventHandler
				public void onEvtDown(EvtDown event) {
					if (!this.hasStateObject()) {
						Log.e("Texamon.MainClass",
							"D-Pad button state object is null",
							new NullPointerException());
						return;
					}

					java.util.Map<Object, Object> state = this.getStateObject();

					if (!event.getTarget().equals(state.get("owner"))) {
						return;
					}
					IkWindow up = (IkWindow) state.get("up");
					IkWindow right = (IkWindow) state.get("right");
					IkWindow down = (IkWindow) state.get("down");
					IkWindow left = (IkWindow) state.get("left");
					GameData data = (GameData) state.get("gameData");
					WindowStyle pressed = (WindowStyle) state.get("pressed");
					WindowStyle unpressed =
						(WindowStyle) state.get("unpressed");

					synchronized (data) {
						if (data.isMoveUp()) {
							up.setStyle(unpressed);
						}
						else if (data.isMoveRight()) {
							right.setStyle(unpressed);
						}
						else if (data.isMoveLeft()) {
							left.setStyle(unpressed);
						}
						data.moveDown();
						down.setStyle(pressed);
					}
				}

				@EventHandler
				public void onEvtMove(EvtMove event) {
					if (!this.hasStateObject()) {
						Log.e("Texamon.MainClass",
							"D-Pad button state object is null",
							new NullPointerException());
						return;
					}

					java.util.Map<Object, Object> state = this.getStateObject();

					if (!event.getTarget().equals(state.get("owner"))) {
						return;
					}

					IkWindow up = (IkWindow) state.get("up");
					IkWindow right = (IkWindow) state.get("right");
					IkWindow down = (IkWindow) state.get("down");
					IkWindow left = (IkWindow) state.get("left");
					GameData data = (GameData) state.get("gameData");
					WindowStyle pressed = (WindowStyle) state.get("pressed");
					WindowStyle unpressed =
						(WindowStyle) state.get("unpressed");

					synchronized (data) {
						if (data.isMoveUp()) {
							up.setStyle(unpressed);
						}
						else if (data.isMoveRight()) {
							right.setStyle(unpressed);
						}
						else if (data.isMoveLeft()) {
							left.setStyle(unpressed);
						}
						data.moveDown();
						down.setStyle(pressed);
					}
				}

			});
			dPadState.remove("owner");

			dPadState.put("owner", wDPadLeft);
			WindowManager.registerListener(wDPadLeft, new IkWindowListener(
				dPadState) {
				@EventHandler
				public void onEvtDown(EvtDown event) {
					if (!this.hasStateObject()) {
						Log.e("Texamon.MainClass",
							"D-Pad button state object is null",
							new NullPointerException());
						return;
					}

					java.util.Map<Object, Object> state = this.getStateObject();

					if (!event.getTarget().equals(state.get("owner"))) {
						return;
					}
					IkWindow up = (IkWindow) state.get("up");
					IkWindow right = (IkWindow) state.get("right");
					IkWindow down = (IkWindow) state.get("down");
					IkWindow left = (IkWindow) state.get("left");
					GameData data = (GameData) state.get("gameData");
					WindowStyle pressed = (WindowStyle) state.get("pressed");
					WindowStyle unpressed =
						(WindowStyle) state.get("unpressed");

					synchronized (data) {
						if (data.isMoveUp()) {
							up.setStyle(unpressed);
						}
						else if (data.isMoveRight()) {
							right.setStyle(unpressed);
						}
						else if (data.isMoveDown()) {
							down.setStyle(unpressed);
						}
						data.moveLeft();
						left.setStyle(pressed);
					}
				}

				@EventHandler
				public void onEvtMove(EvtMove event) {
					if (!this.hasStateObject()) {
						Log.e("Texamon.MainClass",
							"D-Pad button state object is null",
							new NullPointerException());
						return;
					}

					java.util.Map<Object, Object> state = this.getStateObject();

					if (!event.getTarget().equals(state.get("owner"))) {
						return;
					}

					IkWindow up = (IkWindow) state.get("up");
					IkWindow right = (IkWindow) state.get("right");
					IkWindow down = (IkWindow) state.get("down");
					IkWindow left = (IkWindow) state.get("left");
					GameData data = (GameData) state.get("gameData");
					WindowStyle pressed = (WindowStyle) state.get("pressed");
					WindowStyle unpressed =
						(WindowStyle) state.get("unpressed");

					synchronized (data) {
						if (data.isMoveUp()) {
							up.setStyle(unpressed);
						}
						else if (data.isMoveRight()) {
							right.setStyle(unpressed);
						}
						else if (data.isMoveDown()) {
							down.setStyle(unpressed);
						}
						data.moveLeft();
						left.setStyle(pressed);
					}
				}

			});
			dPadState.remove("owner");

			dPadState.put("owner", wDPadCenter);
			WindowManager.registerListener(wDPadCenter, new IkWindowListener(
				dPadState) {
				@EventHandler
				public void onEvtDown(EvtDown event) {
					if (!this.hasStateObject()) {
						Log.e("Texamon.MainClass",
							"D-Pad button state object is null",
							new NullPointerException());
						return;
					}

					java.util.Map<Object, Object> state = this.getStateObject();

					if (!event.getTarget().equals(state.get("owner"))) {
						return;
					}
					IkWindow up = (IkWindow) state.get("up");
					IkWindow right = (IkWindow) state.get("right");
					IkWindow down = (IkWindow) state.get("down");
					IkWindow left = (IkWindow) state.get("left");
					GameData data = (GameData) state.get("gameData");
					WindowStyle unpressed =
						(WindowStyle) state.get("unpressed");

					synchronized (data) {
						if (data.isMoveUp()) {
							up.setStyle(unpressed);
						}
						else if (data.isMoveRight()) {
							right.setStyle(unpressed);
						}
						else if (data.isMoveDown()) {
							down.setStyle(unpressed);
						}
						else if (data.isMoveLeft()) {
							left.setStyle(unpressed);
						}
						data.moveNone();
					}
				}

				@EventHandler
				public void onEvtMove(EvtMove event) {
					if (!this.hasStateObject()) {
						Log.e("Texamon.MainClass",
							"D-Pad button state object is null",
							new NullPointerException());
						return;
					}

					java.util.Map<Object, Object> state = this.getStateObject();

					if (!event.getTarget().equals(state.get("owner"))) {
						return;
					}

					IkWindow up = (IkWindow) state.get("up");
					IkWindow right = (IkWindow) state.get("right");
					IkWindow down = (IkWindow) state.get("down");
					IkWindow left = (IkWindow) state.get("left");
					GameData data = (GameData) state.get("gameData");
					WindowStyle unpressed =
						(WindowStyle) state.get("unpressed");

					synchronized (data) {
						if (data.isMoveUp()) {
							up.setStyle(unpressed);
						}
						else if (data.isMoveRight()) {
							right.setStyle(unpressed);
						}
						else if (data.isMoveDown()) {
							down.setStyle(unpressed);
						}
						else if (data.isMoveLeft()) {
							left.setStyle(unpressed);
						}
						data.moveNone();
					}
				}

				@EventHandler
				public void onEvtUp(@SuppressWarnings("unused") EvtUp event) {
					if (!this.hasStateObject()) {
						Log.e("Texamon.MainClass",
							"D-Pad button state object is null",
							new NullPointerException());
						return;
					}

					java.util.Map<Object, Object> state = this.getStateObject();
					IkWindow up = (IkWindow) state.get("up");
					IkWindow right = (IkWindow) state.get("right");
					IkWindow down = (IkWindow) state.get("down");
					IkWindow left = (IkWindow) state.get("left");
					GameData data = (GameData) state.get("gameData");
					WindowStyle unpressed =
						(WindowStyle) state.get("unpressed");

					synchronized (data) {
						if (data.isMoveUp()) {
							up.setStyle(unpressed);
						}
						else if (data.isMoveRight()) {
							right.setStyle(unpressed);
						}
						else if (data.isMoveDown()) {
							down.setStyle(unpressed);
						}
						else if (data.isMoveLeft()) {
							left.setStyle(unpressed);
						}
						data.moveNone();
					}
				}

			});
			dPadState.remove("owner");

			// put together dpad
			wDPad.addChild(wDPadUp);
			wDPad.addChild(wDPadRight);
			wDPad.addChild(wDPadDown);
			wDPad.addChild(wDPadLeft);
			wDPad.addChild(wDPadCenter);

			this.idTree = new IntegerTree();

			this.comingsoonbuilder.setMessage("Coming soon!");
			this.comingsoonbuilder.setNeutralButton(":(",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						MyView.this.comingsoonalert.dismiss();
					}
				});
			this.comingsoonalert = this.comingsoonbuilder.create();

			this.errorbuilder.setNeutralButton("ok",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						MyView.this.erroralert.dismiss();
					}
				});
			this.erroralert = this.errorbuilder.create();

			this.gameData.getTeam().add(
				this.createChar("Coaldra", "Coaldra", "hand"));
			this.gameData.getTeam().get(0).setCurrentLVL(1);
			this.gameData.getTeam().get(0).setMove(1, Move.INFERNO);
			// TODO remove this
			// for now/testing just jump into the game
			this.gameData.setState(GameState.INGAME);
		}

		public Monster createChar(String type, String name, String locat) {
			int id;
			id = this.idTree.getSmallestUnusedInt();
			try {
				this.idTree.insert(id);
			}
			catch (DuplicateEntry e) {
				e.printStackTrace();
			}
			Monster toCreate = new Monster(TexamonType.fromType(type));
			toCreate.setCurrentHP(toCreate.getMaxHP());
			toCreate.setLocation(locat);
			toCreate.setUID(id);
			toCreate.setNickName(name);
			return toCreate;
		}

		public boolean didEncounter() {
			// create a number on [0, 29)
			int c = this.rand.nextInt(29);
			// just a random magic number between 0 and 29.
			if (c == 2) {
				return true;
			}
			return false;
		}

		private void drawMenus(Canvas canvas) {
			WindowManager.drawWindows(canvas);
		}

		// call this from the onDraw method
		public void firstTimeScreen(Canvas canvas) {
			// set the background color to black
			canvas.drawColor(Color.BLACK);

			// since we do not have access to the canvas in the onCreate event
			// we will grab the screen dimensions here and save them
			this.winWidth = canvas.getWidth();
			this.winHeight = canvas.getHeight() - 60;

			this.myDrawText(canvas, "Texamon", 26, this.winWidth / 2,
				this.winHeight / 4, Color.RED, 1);
			// TODO start menu

		}

		public void gameOverScreen(Canvas canvas) {
			// set the background color to black
			canvas.drawColor(Color.BLACK);

			this.myDrawText(canvas, "Game Over", 32, this.winWidth / 2, 100,
				Color.RED, 1);
			this.myDrawText(canvas, "Touch the Android to Begin", 16,
				this.winWidth / 2, 125, Color.GREEN, 1);
			this.myDrawText(canvas, "Wins: " + "  Losses: ", 16,
				this.winWidth / 2, 200, Color.RED, 1);
			// TODO game over screen
			// draw all of your objects
		}

		public void load() {

			String temp = MainClass.savedata;

			String[] tempparts = new String[146];
			try {
				tempparts = temp.split(",");
				String[] team1 = new String[21];
				String[] team2 = new String[21];
				String[] team3 = new String[21];
				String[] team4 = new String[21];
				String[] team5 = new String[21];
				String[] team6 = new String[21];

				if (tempparts[14] != null && tempparts[14] != "") {
					for (int i = 0; i < 21; i++) {
						team1[i] = tempparts[14 + i];
					}
				}
				if (tempparts[14] != null && tempparts[36] != "") {
					for (int i = 0; i < 21; i++) {
						team2[i] = tempparts[36 + i];
					}
				}
				if (tempparts[14] != null && tempparts[58] != "") {
					for (int i = 0; i < 21; i++) {
						team3[i] = tempparts[58 + i];
					}
				}
				if (tempparts[14] != null && tempparts[80] != "") {
					for (int i = 0; i < 21; i++) {
						team4[i] = tempparts[80 + i];
					}
				}
				if (tempparts[14] != null && tempparts[102] != "") {
					for (int i = 0; i < 21; i++) {
						team5[i] = tempparts[102 + i];
					}
				}
				if (tempparts[14] != null && tempparts[124] != "") {
					for (int i = 0; i < 21; i++) {
						team6[i] = tempparts[124 + i];
					}
				}

				this.stringtotexamon(team1, 0);
				this.stringtotexamon(team2, 1);
				this.stringtotexamon(team3, 2);
				this.stringtotexamon(team4, 3);
				this.stringtotexamon(team5, 4);
				this.stringtotexamon(team6, 5);
			}
			catch (Exception e) {
				this.erroralert.setMessage("ERROR OCCURED! error " + e
					+ "\n at line" + e.getStackTrace()[2].getLineNumber());
				this.erroralert.show();
				e.printStackTrace();
			}

		}

		private void myDrawText(Canvas canvas, String toDraw, int size,
			float x, float y, int color, int alignment) {
			Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
			paintText.setColor(color);
			Typeface typeface =
				Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
			paintText.setTextSize(size);
			paintText.setTypeface(typeface); // Sans Serif is the default

			if (alignment == 1) {
				paintText.setTextAlign(Paint.Align.CENTER);
			}

			canvas.drawText(toDraw, x, y, paintText);

		}

		@Override
		public void onAttachedToWindow() {
			DisplayMetrics metrics = new DisplayMetrics();
			((Activity) this.thisContext).getWindowManager()
				.getDefaultDisplay().getMetrics(metrics);

			this.winHeight = metrics.heightPixels;
			this.winWidth = metrics.widthPixels;
			this.player =
				new MovableObject("player.png", R.drawable.player,
					(this.winWidth / 2) - 3, (this.winHeight / 2) - 3, 7, 7,
					this.getResources());
		}

		// this method will get called every threadDelay (20) ms
		@Override
		protected void onDraw(Canvas canvas) {
			try {
				if (this.canvasSizeDirty) {
					this.CANVAS_WIDTH = canvas.getWidth();
					this.CANVAS_HEIGHT = canvas.getHeight();
					this.canvasSizeDirty = false;
				}
				if (this.gameData.getState() == GameState.GAME_OVER) {
					this.gameOverScreen(canvas);
					return;
				}

				if (this.firstTime) {
					this.firstTimeScreen(canvas);
				}

				// set the background color to green
				canvas.drawColor(Color.GREEN);

				if (this.gameData.isMoveUp()) {
					if (!this.gameData.willCollide()) {
						if (this.count <= 0) {
							this.gameData.setyPos(this.gameData.getyPos() - 1);
							this.count = this.COUNT_MAX;
						}
					}
					this.player.setCurrentFilename("player.png");
					this.player.setImageDID(R.drawable.player);

					// TODO handle moving differently
				}
				if (this.gameData.isMoveDown()) {
					if (!this.gameData.willCollide()) {
						if (this.count <= 0) {
							this.gameData.setyPos(this.gameData.getyPos() + 1);
							this.count = this.COUNT_MAX;
						}
					}

					this.player.setCurrentFilename("playerd.png");
					this.player.setImageDID(R.drawable.playerd);

				}
				if (this.gameData.isMoveLeft()) {
					if (!this.gameData.willCollide()) {
						if (this.count <= 0) {
							this.gameData.setxPos(this.gameData.getxPos() - 1);
							this.count = this.COUNT_MAX;
						}
					}

					this.player.setCurrentFilename("playerl.png");
					this.player.setImageDID(R.drawable.playerl);

				}
				if (this.gameData.isMoveRight()) {
					if (!this.gameData.willCollide()) {
						if (this.count <= 0) {
							this.gameData.setxPos(this.gameData.getxPos() + 1);
							this.count = this.COUNT_MAX;
						}
					}
					this.player.setCurrentFilename("playerr.png");
					this.player.setImageDID(R.drawable.playerr);
				}

				if (this.gameData.isInGrass()) {
					if (this.didEncounter() && !this.teamgone()) {
						this.gameData.setState(GameState.BATTLE);
						this.gameData.moveNone();
					}
				}
				if (this.count > 0) {
					--this.count;
				}

				this.mapSource.set(
					(this.gameData.getxPos() - (this.TILES_PER_SCREEN / 2))
						* this.gameData.getCurrentMap().getTileWidth(),
					(this.gameData.getyPos() - (this.TILES_PER_SCREEN / 2))
						* this.gameData.getCurrentMap().getTileHeight(),
					(this.gameData.getxPos() + (this.TILES_PER_SCREEN / 2))
						* this.gameData.getCurrentMap().getTileWidth(),
					(this.gameData.getyPos() + (this.TILES_PER_SCREEN / 2))
						* this.gameData.getCurrentMap().getTileHeight());
				this.mapDest.set(0, 0, canvas.getWidth(), canvas.getHeight());
				canvas.drawBitmap(this.gameData.getCurMapImg(), this.mapSource,
					this.mapDest, null);

				this.player.draw(canvas, canvas.getWidth()
					/ this.TILES_PER_SCREEN, canvas.getHeight()
					/ this.TILES_PER_SCREEN);

				this.drawMenus(canvas);

				if (this.gameData.getState() == GameState.BATTLE) {
					/* NOP */;
				}

			}
			catch (Exception e) {
				this.erroralert.setMessage("ERROR OCCURED! error " + e
					+ "\n at line" + e.getStackTrace()[2].getLineNumber());
				this.erroralert.show();
				e.printStackTrace();
			}
		}

		@Override
		protected void onRestoreInstanceState(Parcelable state1) {
			// TODO restore the state
			super.onRestoreInstanceState(state1);
			this.canvasSizeDirty = true;
		}

		@Override
		protected Parcelable onSaveInstanceState() {
			// TODO save the state
			return super.onSaveInstanceState();
		}

		// use this if you want to move an object with the finger
		@Override
		public boolean onTouchEvent(MotionEvent e) {
			float x = e.getX();
			float y = e.getY();

			if (this.canvasSizeDirty) {
				System.err.println("Not handling event because "
					+ "canvas size is dirty");
				return false;
			}

			WindowManager.handleMotionEvent(this.CANVAS_WIDTH,
				this.CANVAS_HEIGHT, new Point(x, y), e.getAction());

			// e.ACTION_MOVE
			// a finger touches the screen
			if (e.getAction() == MotionEvent.ACTION_DOWN) {

				if (this.doLogging) {
					Log.i("tag", "inside ACTION_DOWN x=" + x + "  y=" + y);
				}

				// buttons

				switch (this.gameData.getState()) {
					case INGAME:
						break;
					case BATTLE:
						// TODO battle
					case GAME_OVER:
						break;
					case MAIN_MENU:
						break;
					default:
						break;
				}

			}
			else if (e.getAction() == MotionEvent.ACTION_MOVE) {
				// the finger is moving
				float xPos = x;
				float yPos = y;

				if (this.doLogging) {
					Log.i("tag", "inside ACTION_MOVE x=" + xPos + "  y=" + yPos);
				}

			}
			else if (e.getAction() == MotionEvent.ACTION_UP) {

				// if they have pressed the start button
				// then we will start a new game
				if (this.gameData.getState() == GameState.MAIN_MENU) {
					// TODO start game
				}
			}

			return true; // let the OS know that we have handled the event

		} // end of public boolean onTouchEvent(MotionEvent e)

		public void stringtotexamon(String[] basestring, int pos) {
			this.gameData.getTeam().set(pos,
				this.createChar(basestring[20], basestring[20], "hand"));
			this.gameData.getTeam().get(pos)
				.setCurrentHP(Integer.decode(basestring[0]));
			this.gameData.getTeam().get(pos)
				.setCurrentLVL(Integer.decode(basestring[2]));
			this.gameData.getTeam().get(pos).setLocation(basestring[4]);
			this.gameData.getTeam().get(pos)
				.setMove(0, Move.fromString(basestring[6]));
			this.gameData.getTeam().get(pos)
				.setMove(1, Move.fromString(basestring[8]));
			this.gameData.getTeam().get(pos)
				.setMove(2, Move.fromString(basestring[10]));
			this.gameData.getTeam().get(pos)
				.setMove(3, Move.fromString(basestring[12]));
			this.gameData.getTeam().get(pos).setNickName(basestring[14]);
			this.gameData.getTeam().get(pos)
				.setUID(Integer.decode(basestring[16]));
			this.gameData.getTeam().get(pos)
				.setXP(Integer.decode(basestring[18]));

		}

		public boolean teamgone() {
			for (int i = 0; i < this.gameData.getTeam().getSize(); i++) {
				if ((this.gameData.getTeam().get(i) != null)
					&& this.gameData.getTeam().get(i).getCurrentHP() > 0) {
					return false;
				}
			}
			return true;
		}

		public java.util.Map<String, String> xmlLoad(final String file,
			final String[] requiredValues) {
			// https://www.tutorialspoint.com/android/android_xml_parsers.htm
			FileInputStream fis;

			java.util.Map<String, String> values =
				new TreeMap<String, String>();

			/*
			 * Using a hashmap to store the requested values so checking if a
			 * value is in the set is O(1) not O(n), which reduces the
			 * complexity of loading from the xml from O(n*m) to O(m) where n=#
			 * of values to load and m=# of xml entries.
			 */
			HashSet<String> reqVals =
				new HashSet<String>(requiredValues.length);
			for (String s : requiredValues) {
				reqVals.add(s);
			}

			try {
				fis = this.thisContext.openFileInput(file);
			}
			catch (FileNotFoundException e) {
				Log.e("Texamon.MainClass", "Error opening savefile", e);
				return values;
			}

			XmlPullParserFactory xmlFactoryObject;
			try {
				xmlFactoryObject = XmlPullParserFactory.newInstance();
			}
			catch (XmlPullParserException e) {
				Log.e("Texamon.MainClass",
					"Error creating xml parser for savefile loading", e);
				return values;
			}
			XmlPullParser myParser;
			try {
				myParser = xmlFactoryObject.newPullParser();
			}
			catch (XmlPullParserException e) {
				Log.e("Texamon.MainClass", "Error creating xml parser", e);
				return values;
			}

			try {
				myParser.setInput(fis, null);
			}
			catch (XmlPullParserException e) {
				Log.e("Texamon.MainClass",
					"Error assigning savefile to xml parser", e);
				return values;
			}

			int event;
			try {
				event = myParser.getEventType();
			}
			catch (XmlPullParserException e) {
				Log.e("Texamon.MainClass", "Error reading xml", e);
				return values;
			}
			while (event != XmlPullParser.END_DOCUMENT) {
				String name = myParser.getName();
				switch (event) {
					case XmlPullParser.START_TAG:
						break;
					case XmlPullParser.END_TAG:
						break;
					case XmlPullParser.TEXT:
						if (reqVals.contains(name)) {
							values.put(name,
								myParser.getAttributeValue(null, "value"));
						}
						break;
				}
				try {
					event = myParser.next();
				}
				catch (XmlPullParserException e) {
					Log.e("Texamon.MainClass",
						"Parser error fetching xml element", e);
					return values;
				}
				catch (IOException e) {
					Log.e("Texamon.MainClass", "IO error fetching xml element",
						e);
					return values;
				}
			}

			return values;
		}

		public void xmlSave(final String file,
			final java.util.Map<String, String> valuesToSave) {

			FileOutputStream fos;

			/*
			 * using a TreeMap so that the XML tags will be in alphabetical
			 * order.
			 */
			TreeMap<String, String> values = new TreeMap<String, String>();
			values.putAll(valuesToSave);

			try {
				/*
				 * Replace existing files because appending does not make sense
				 * for XML documents.
				 */
				fos =
					this.thisContext.openFileOutput(file, Context.MODE_PRIVATE);
			}
			catch (FileNotFoundException e) {
				Log.e("Texamon.MainClass", "Error creating savefile", e);
				return;
			}

			XmlSerializer serializer = Xml.newSerializer();
			try {
				serializer.setOutput(fos, "UTF-8");
			}
			catch (IllegalArgumentException e) {
				Log.e("Texamon.MainClass", "Error creating xml serializer", e);
				return;
			}
			catch (IllegalStateException e) {
				Log.e("Texamon.MainClass", "Error creating xml serializer", e);
				return;
			}
			catch (IOException e) {
				Log.e("Texamon.MainClass", "Error creating xml serializer", e);
				return;
			}
			try {
				serializer.startDocument(null, Boolean.valueOf(true));
				serializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);

				NavigableSet<String> valuesKeySet = values.navigableKeySet();

				// store all the values in xml.
				for (Iterator<String> iter = valuesKeySet.iterator(); iter
					.hasNext();) {
					String key = iter.next();

					serializer.startTag(null, key);

					serializer.text(values.get(key));

					serializer.endTag(null, key);
				}
				serializer.endDocument();

				serializer.flush();
			}
			catch (IOException e) {
				Log.e("Texamon.MainClass", "IO error writing xml document", e);
				return;
			}
			catch (Exception e) {
				Log.e("Texamon.MainClass", "Error writing xml document", e);
				return;
			}

			try {
				fos.close();
			}
			catch (IOException e) {
				Log.e("Texamon.MainClass", "Error closing savefile", e);
				return;
			}
		}
	}

	private static String savedata;
	private MyView theView;
	private Thread myThread;
	private int threadDelay;

	private GameData gameData;

	private boolean doLogging = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.setTitle("Texamon");
		this.threadDelay = 10;

		this.gameData = new GameData();
		this.theView = new MyView(this, this.gameData);

		this.setContentView(this.theView);

		this.myThread = new Thread(this);
		this.myThread.start();

		FileInputStream fIn = null;
		InputStreamReader isr = null;
		try {
			fIn = this.openFileInput("save");
		}
		catch (Exception e) {
			System.out.println("error");
			return;
		}
		isr = new InputStreamReader(fIn);
		char[] tempr = new char[0];
		String temprsd;
		try {
			isr.read(tempr);
			if (tempr.length > 0) {// tempr is read into so it is not null here
				temprsd = tempr.toString();
				MainClass.savedata = temprsd;
			}
			isr.close();
			fIn.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onStop() {

		EventManager.destoryInstance();
		super.onStop();
		if (this.doLogging) {
			Log.i("Texamon.MainClass", "onStop is called");
		}
		this.myThread.interrupt();
		this.myThread = null;

		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;
		try {
			fOut = this.openFileOutput("save", Context.MODE_PRIVATE);
		}
		catch (Exception e) {
			System.out.println("error making fOut.");
			return;
		}
		osw = new OutputStreamWriter(fOut);
		try {
			osw.write(MainClass.savedata);
			osw.close();
			fOut.close();
		}
		catch (Exception e) {

		}

	}

	@Override
	public void run() {
		while (this.myThread == Thread.currentThread()) {
			try {
				Thread.sleep(this.threadDelay);
				// theView.invalidate(); don't use this unless you are on the
				// main thread
				this.theView.postInvalidate();
			}
			catch (Exception e) {
				break;
			}

		}
		if (this.doLogging) {
			Log.i("Texamon.MainClass", "run is stopping");
		}

	}

}