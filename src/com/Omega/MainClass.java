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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import tiled.core.Map;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.Omega.menus.Alignment;
import com.Omega.menus.IWindowCallback;
import com.Omega.menus.IkWindow;
import com.Omega.menus.Menu;
import com.Omega.menus.MenuItem;
import com.Omega.menus.WindowDrawer.Border;
import com.Omega.menus.WindowDrawer.ColorScheme;
import com.Omega.menus.WindowDrawer.WinStyle;
import com.Omega.menus.WindowManager;
import com.Omega.menus.WindowStyle;
import com.Omega.util.DuplicateEntry;
import com.Omega.util.IntegerTree;
import com.Omega.util.Random;

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
		boolean dragging; // are we dragging some object
		float startMouseDragX;
		float startMouseDragY;

		boolean doLogging = false;
		boolean firstTime; // indicates that this is the beginning of a run

		int CANVAS_WIDTH = -1;
		int CANVAS_HEIGHT = -1;

		boolean canvasSizeDirty = true;

		GameData gameData;

		MovableObject player;
		IkWindow wStart;
		IkWindow wBag;
		IkWindow wTexamon;
		IkWindow wStartButton;
		IkWindow wDPad;
		IkWindow wDPadUp;
		IkWindow wDPadRight;
		IkWindow wDPadDown;
		IkWindow wDPadLeft;
		IkWindow wDpadCenter;
		Menu mStart;
		Menu mBag;
		Menu mTexamon;
		MenuItem menuButton;

		MenuItem exitButton;

		private Random rand;

		AlertDialog.Builder comingsoonbuilder = new AlertDialog.Builder(
				this.getContext());

		AlertDialog comingsoonalert = this.comingsoonbuilder.create();

		AlertDialog.Builder errorbuilder = new AlertDialog.Builder(
				this.getContext());

		AlertDialog erroralert;

		MovableObject startButton;

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

			this.rand = new Random();

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

			this.wStartButton =
					new IkWindow(new Point(0.05f, 0.05f), Alignment.NORTH_EAST,
							new Point(0.1f, 0.05f));
			Menu startMenu = new Menu(1, 1);
			this.menuButton = new MenuItem("Start");
			this.menuButton.setCallback(new IWindowCallback() {
				@Override
				public void action() {
					MyView.this.wStart.setVisible(true);
					MyView.this.wStartButton.setVisible(false);
				}
			});
			startMenu.addChild(this.menuButton);
			this.wStartButton.addChild(startMenu);
			this.exitButton = new MenuItem("Exit");
			this.exitButton.setCallback(new IWindowCallback() {
				@Override
				public void action() {
					MyView.this.wStart.setVisible(false);
					MyView.this.wStartButton.setVisible(true);
				}
			});

			WindowManager.registerWin(this.wStartButton,
					WindowManager.BASE_HEIGHT, "StartButton");

			this.wStart =
					new IkWindow(new Point(0.05f, 0.05f), Alignment.NORTH_EAST,
							new Point(0.5f, 0.3f));
			this.mStart = new Menu(1, 6);
			this.mStart.addChild(new MenuItem("Texamon"));
			this.mStart.addChild(new MenuItem("Items"));
			this.mStart.addChild(new MenuItem("Player"));
			this.mStart.addChild(new MenuItem("Save"));
			this.mStart.addChild(new MenuItem("Options"));
			this.mStart.addChild(this.exitButton);
			this.wStart.addChild(this.mStart);

			this.wTexamon =
					new IkWindow(new Point(0.0f, 0.00f), Alignment.NORTH_EAST,
							new Point(1.0f, 1.0f));
			this.wBag =
					new IkWindow(new Point(-1.0f, 0.05f), Alignment.WEST,
							new Point(1.0f, 0.5f));
			this.mBag = new Menu(1, 6);
			this.mBag.addChild(new MenuItem("Small Potion"));
			this.mBag.addChild(new MenuItem("Medium Potion"));
			this.mBag.addChild(new MenuItem("Large Potion"));
			this.mBag.addChild(new MenuItem("Full Potion"));
			this.mBag.addChild(new MenuItem("TBall"));
			this.mBag.addChild(new MenuItem("Great TBall"));
			this.wBag.addChild(this.mBag);
			this.wStart.addChild(this.wBag);

			this.wStart.setVisible(false);
			this.wBag.setVisible(false);
			this.wTexamon.setVisible(false);

			WindowManager.registerWin(this.wStart,
					WindowManager.getHeight(this.wStartButton) + 1,
					"StartWindow");
			WindowManager.registerWin(this.wBag,
					WindowManager.getHeight(this.wStart) + 1, "BagWindow");
			WindowManager.registerWin(this.wTexamon,
					WindowManager.BASE_HEIGHT + 1, "TexamonWindow");

			// create buttons
			this.wDPad =
					new IkWindow(new Point(0.05f, 0.05f), Alignment.SOUTH_WEST,
							new Point(0.3f, 0.3f));
			this.wDPadUp =
					new IkWindow(new Point(0.0f, 0.0f), Alignment.NORTH,
							new Point(0.33f, 0.33f));
			this.wDPadRight =
					new IkWindow(new Point(0.0f, 0.0f), Alignment.EAST,
							new Point(0.33f, 0.33f));
			this.wDPadDown =
					new IkWindow(new Point(0.0f, 0.0f), Alignment.SOUTH,
							new Point(0.33f, 0.33f));
			this.wDPadLeft =
					new IkWindow(new Point(0.0f, 0.0f), Alignment.WEST,
							new Point(0.33f, 0.33f));
			this.wDpadCenter =
					new IkWindow(new Point(0.0f, 0.0f), Alignment.CENTER,
							new Point(0.33f, 0.33f));
			// format dpad
			this.wDPad.setStyle(new WindowStyle(WinStyle.EMPTY_SQUARE,
					Border.BORDERLESS, ColorScheme.DARK));
			final WindowStyle buttonScheme =
					new WindowStyle(WinStyle.SQUARE, Border.BORDERLESS,
							ColorScheme.DARK);
			final WindowStyle pressedScheme =
					new WindowStyle(WinStyle.SQUARE, Border.BORDERLESS,
							ColorScheme.LIGHT);

			this.wDPadUp.setStyle(buttonScheme);
			this.wDPadRight.setStyle(buttonScheme);
			this.wDPadDown.setStyle(buttonScheme);
			this.wDPadLeft.setStyle(buttonScheme);
			this.wDpadCenter.setStyle(buttonScheme);

			this.wDPadUp.setCallback(new IWindowCallback() {

				@Override
				public void action() {
					if (MyView.this.gameData.isMoveUp()) {
						MyView.this.wDPadUp.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveRight()) {
						MyView.this.wDPadRight.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveDown()) {
						MyView.this.wDPadDown.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveLeft()) {
						MyView.this.wDPadLeft.setStyle(buttonScheme);
					}
					MyView.this.gameData.moveUp();
					MyView.this.wDPadUp.setStyle(pressedScheme);

				}
			});
			this.wDPadRight.setCallback(new IWindowCallback() {
				@Override
				public void action() {
					if (MyView.this.gameData.isMoveUp()) {
						MyView.this.wDPadUp.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveRight()) {
						MyView.this.wDPadRight.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveDown()) {
						MyView.this.wDPadDown.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveLeft()) {
						MyView.this.wDPadLeft.setStyle(buttonScheme);
					}
					MyView.this.gameData.moveRight();
					MyView.this.wDPadRight.setStyle(pressedScheme);
				}
			});
			this.wDPadDown.setCallback(new IWindowCallback() {

				@Override
				public void action() {
					if (MyView.this.gameData.isMoveUp()) {
						MyView.this.wDPadUp.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveRight()) {
						MyView.this.wDPadRight.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveDown()) {
						MyView.this.wDPadDown.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveLeft()) {
						MyView.this.wDPadLeft.setStyle(buttonScheme);
					}
					MyView.this.gameData.moveDown();
					MyView.this.wDPadDown.setStyle(pressedScheme);
				}
			});
			this.wDPadLeft.setCallback(new IWindowCallback() {

				@Override
				public void action() {
					if (MyView.this.gameData.isMoveUp()) {
						MyView.this.wDPadUp.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveRight()) {
						MyView.this.wDPadRight.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveDown()) {
						MyView.this.wDPadDown.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveLeft()) {
						MyView.this.wDPadLeft.setStyle(buttonScheme);
					}
					MyView.this.gameData.moveLeft();
					MyView.this.wDPadLeft.setStyle(pressedScheme);
				}
			});
			this.wDpadCenter.setCallback(new IWindowCallback() {

				@Override
				public void action() {
					if (MyView.this.gameData.isMoveUp()) {
						MyView.this.wDPadUp.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveRight()) {
						MyView.this.wDPadRight.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveDown()) {
						MyView.this.wDPadDown.setStyle(buttonScheme);
					}
					else if (MyView.this.gameData.isMoveLeft()) {
						MyView.this.wDPadLeft.setStyle(buttonScheme);
					}
					MyView.this.gameData.moveNone();
				}
			});

			// put together dpad
			this.wDPad.addChild(this.wDPadUp);
			this.wDPad.addChild(this.wDPadRight);
			this.wDPad.addChild(this.wDPadDown);
			this.wDPad.addChild(this.wDPadLeft);
			this.wDPad.addChild(this.wDpadCenter);

			WindowManager.registerWin(this.wDPad, WindowManager.BASE_HEIGHT,
					"DPad");

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
			int c = this.rand.getIntBetween(1, 28);
			if (c == 16) {
				return true;
			}
			return false;
		}

		private void drawMenus(Canvas canvas) {
			this.wStart.draw(canvas);
			WindowManager.drawWindows(canvas);
			// TODO draw menus

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

			if (this.startButton != null) {
				this.startButton.setX(this.winWidth / 2
						- this.startButton.getWidth() / 2);
				this.startButton.setY(this.winHeight / 2);
				this.startButton.draw(canvas);
			}

		} // end of public void firstTimeScreen(Canvas canvas)

		public void gameOverScreen(Canvas canvas) {
			// set the background color to black
			canvas.drawColor(Color.BLACK);

			this.myDrawText(canvas, "Game Over", 32, this.winWidth / 2, 100,
					Color.RED, 1);
			this.myDrawText(canvas, "Touch the Android to Begin", 16,
					this.winWidth / 2, 125, Color.GREEN, 1);
			this.myDrawText(canvas, "Wins: " + "  Losses: ", 16,
					this.winWidth / 2, 200, Color.RED, 1);

			if (this.startButton != null) {
				this.startButton.setX(this.winWidth / 2
						- this.startButton.getWidth() / 2);
				this.startButton.setY(280);
				this.startButton.draw(canvas);
			}

			// draw all of your objects
		} // end of public void gameOverScreen(Canvas canvas)

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
			this.startButton =
					new MovableObject("start.png", R.drawable.start,
							(this.winWidth / 2) - (this.winWidth / 4),
							(this.winHeight / 2) - (this.winHeight / 16),
							this.winWidth / 2, this.winHeight / 8,
							this.getResources());
			this.player =
					new MovableObject("player.png", R.drawable.player,
							(this.winWidth / 2) - 3, (this.winHeight / 2) - 3,
							7, 7, this.getResources());
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

				this.mapSource
						.set((this.gameData.getxPos() - (this.TILES_PER_SCREEN / 2))
								* this.gameData.getCurrentMap().getTileWidth(),
								(this.gameData.getyPos() - (this.TILES_PER_SCREEN / 2))
										* this.gameData.getCurrentMap()
												.getTileHeight(),
								(this.gameData.getxPos() + (this.TILES_PER_SCREEN / 2))
										* this.gameData.getCurrentMap()
												.getTileWidth(),
								(this.gameData.getyPos() + (this.TILES_PER_SCREEN / 2))
										* this.gameData.getCurrentMap()
												.getTileHeight());
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
			// TODO Save the state and restore here
			super.onRestoreInstanceState(state1);
			this.canvasSizeDirty = true;
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

			// e.ACTION_MOVE
			// a finger touches the screen
			if (e.getAction() == MotionEvent.ACTION_DOWN) {

				if (this.doLogging) {
					Log.i("tag", "inside ACTION_DOWN x=" + x + "  y=" + y);
				}
				this.startMouseDragX = x;
				this.startMouseDragY = y;

				switch (this.gameData.getState()) {
				case INGAME:
					if (this.wDPadUp.containsPoint(this.CANVAS_WIDTH,
							this.CANVAS_HEIGHT, new Point(x, y))) {
						this.wDPadUp.executeAction();
					}
					if (this.wDPadDown.containsPoint(this.CANVAS_WIDTH,
							this.CANVAS_HEIGHT, new Point(x, y))) {
						this.wDPadDown.executeAction();
					}
					if (this.wDPadLeft.containsPoint(this.CANVAS_WIDTH,
							this.CANVAS_HEIGHT, new Point(x, y))) {
						this.wDPadLeft.executeAction();
					}
					if (this.wDPadRight.containsPoint(this.CANVAS_WIDTH,
							this.CANVAS_HEIGHT, new Point(x, y))) {
						this.wDPadRight.executeAction();
					}
					if (this.menuButton.isVisible()
							&& this.menuButton.containsPoint(this.CANVAS_WIDTH,
									this.CANVAS_HEIGHT, new Point(x, y))) {
						this.menuButton.executeAction();
					}
					if (this.wStart.isVisible()
							&& this.exitButton.containsPoint(this.CANVAS_WIDTH,
									this.CANVAS_HEIGHT, new Point(x, y))) {
						this.exitButton.executeAction();
					}
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
				/*
				 * 
				 * if (isNear(aurage.getX(),aurage.getY(),(int)x,(int)y)){
				 * alert.setMessage(aurage.getSignText()); alert.setButton("Ok",
				 * new DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface dialog, int id) { alert.dismiss(); b2
				 * = true; } }); alert.show(); }
				 */

			}
			else if (e.getAction() == MotionEvent.ACTION_MOVE) {
				// the finger is moving
				float xPos = x;
				float yPos = y;

				if (this.doLogging) {
					Log.i("tag", "inside ACTION_MOVE x=" + xPos + "  y=" + yPos);
				}

				boolean allowDragging = true;
				// set your boundaries so you don't move your object off the
				// screen
				if (xPos < 20) {
					allowDragging = false;
				}
				else if (xPos >= this.winWidth - 20) {
					allowDragging = false;
				}
				if (yPos < 20) {
					allowDragging = false;
				}
				else if (yPos >= this.winHeight - 20) {
					allowDragging = false;
				}

				if (this.dragging && allowDragging) {
					// now move your object if you have an object to drag
					// remember that the xPos and yPos is the new position of
					// where the finger is, not the upper left hand corner of
					// the object

				}

				if (this.gameData.isMoveUp()) {
					if (!this.wDPadUp.containsPoint(this.CANVAS_WIDTH,
							this.CANVAS_HEIGHT, new Point(x, y))) {
						this.wDpadCenter.executeAction();
					}
				}
				else {
					if (this.wDPadUp.containsPoint(this.CANVAS_WIDTH,
							this.CANVAS_HEIGHT, new Point(x, y))) {
						this.wDPadUp.executeAction();
					}
				}
				if (this.gameData.isMoveDown()) {
					if (!this.wDPadDown.containsPoint(this.CANVAS_WIDTH,
							this.CANVAS_HEIGHT, new Point(x, y))) {
						this.wDpadCenter.executeAction();
					}
				}
				else {
					if (this.wDPadDown.containsPoint(this.CANVAS_WIDTH,
							this.CANVAS_HEIGHT, new Point(x, y))) {
						this.wDPadDown.executeAction();
					}
				}

				if (this.gameData.isMoveLeft()) {
					if (!this.wDPadLeft.containsPoint(this.CANVAS_WIDTH,
							this.CANVAS_HEIGHT, new Point(x, y))) {
						this.wDpadCenter.executeAction();
					}
				}
				else {
					if (this.wDPadLeft.containsPoint(this.CANVAS_WIDTH,
							this.CANVAS_HEIGHT, new Point(x, y))) {
						this.wDPadLeft.executeAction();
					}
				}
				if (this.gameData.isMoveRight()) {
					if (!this.wDPadRight.containsPoint(this.CANVAS_WIDTH,
							this.CANVAS_HEIGHT, new Point(x, y))) {
						this.wDpadCenter.executeAction();
					}
				}
				else {
					if (this.wDPadRight.containsPoint(this.CANVAS_WIDTH,
							this.CANVAS_HEIGHT, new Point(x, y))) {
						this.wDPadRight.executeAction();
					}
				}

				if (this.wDpadCenter.containsPoint(this.CANVAS_WIDTH,
						this.CANVAS_HEIGHT, new Point(x, y))) {
					this.wDpadCenter.executeAction();
				}

			}
			else if (e.getAction() == MotionEvent.ACTION_UP) {
				// the finger is lifted
				if (this.dragging) {
					// you might want to move your object back to the
					// start of the drag if for some reason you
					// you decide your object should not be placed there
					// for example in checkers if your checker cannot
					// be moved to the new position because it is an illegal
					// move
					// startMouseDragX and startMouseDragY
					// dragObjectToNewPosition(ship,startMouseDragX,
					// startMouseDragY, x, y);
				}

				this.dragging = false;
				this.startMouseDragX = -1;
				this.startMouseDragY = -1;

				// if they have pressed the start button
				// then we will start a new game
				if (this.gameData.getState() == GameState.MAIN_MENU) {
					if (this.startButton.getRect().contains(x, y)) {
						this.gameData.setState(GameState.INGAME);

						this.startButton.setX(-200);
					}
				}
				if (!this.gameData.isMoveNone()) {
					this.wDpadCenter.executeAction();
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
	}

	static String savedata;
	MyView theView;
	Thread myThread;
	int threadDelay;

	GameData gameData;

	boolean doLogging = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.setTitle("Texamon");
		this.threadDelay = 10;

		this.gameData = new GameData();
		this.theView = new MyView(this, this.gameData);

		this.setContentView(this.theView); // don't use R.layout.main); use our
											// own
		// theView

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

		super.onStop();
		if (this.doLogging) {
			Log.i("tag", "onStop is called");
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
			Log.i("tag", "run is stopping");
		}

	}

}