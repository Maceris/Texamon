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
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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

import davidiserovich.TMXLoader.TMXLoader;
import davidiserovich.TMXLoader.TileMapData;

public class MainClass extends Activity implements Runnable {

	private static class MyView extends View //
	{
		class GreatBall extends baseItem {
			int type = 12;// TODO remove this
		}

		class NormalBall extends baseItem {
			int type = 8;// TODO remove this
		}

		IntegerTree idTree;
		boolean dragging; // are we dragging some object
		float startMouseDragX;
		float startMouseDragY;
		boolean doLogging = false;

		boolean firstTime; // indicates that this is the beginning of a run
		boolean menudown;

		int CANVAS_WIDTH = -1;
		int CANVAS_HEIGHT = -1;
		boolean canvasSizeDirty = true;

		GameData gameData;

		int drawdelay;
		MovableObject map;
		MovableObject player;

		MovableObject battleScreen;
		MovableObject teamplaceholder;
		MovableObject enemyplaceholder;
		MovableObject insideplayer;
		MovableObject loadButton;
		MovableObject menureturnbutton;

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

		MovableObject exitdoor;

		MovableObject healer;

		MovableObject startButton;

		Context thisContext;

		int winWidth = 0;
		int winHeight = 0;

		Bitmap mapImg;// TODO remove this

		public MyView(Context context, GameData newData) {

			super(context);

			this.gameData = newData;

			this.rand = new Random();

			// TODO remove this

			TileMapData mapData = TMXLoader.readTMX("aurage.tmx", context);
			mapImg = TMXLoader.createBitmap(mapData, context, 0, 6);

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
					WindowManager.BASE_HEIGHT);

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
					WindowManager.getHeight(this.wStartButton) + 1);
			WindowManager.registerWin(this.wBag,
					WindowManager.getHeight(this.wStart) + 1);
			WindowManager.registerWin(this.wTexamon,
					WindowManager.BASE_HEIGHT + 1);

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

			WindowManager.registerWin(this.wDPad, WindowManager.BASE_HEIGHT);

			this.idTree = new IntegerTree();

			this.drawdelay = 0;

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

		public void aurage(Canvas canvas) {
			// TODO don't. Just... don't.
			if (this.gameData.isMoveUp()
					&& (!this.aurageIsBlocked((int) this.player.getX() + 3,
							(int) this.player.getY() - 1) && !(this.player
							.getY() <= (this.map.getY() + 48)))) {
				this.map.setY(this.map.getY() + 1);

				this.player.setCurrentFilename("player.png");
				this.player.setImageDID(R.drawable.player);
				if (this.isInGrass((int) this.player.getX() + 3,
						(int) this.player.getY() + 3)) {
					if (this.didEncounter() && !this.teamgone()) {
						this.gameData.setState(GameState.BATTLE);
						this.gameData.moveNone();
					}
				}
				if (this.auragedoor(
						(int) (this.player.getX() + (this.player.getWidth() / 2)),
						(int) this.player.getY()) == 1) {
					this.map.setCurrentFilename("temphouse.png");
					this.map.setImageDID(R.drawable.temphouse);
					this.map.setX(this.winWidth / 2 - 150);
					this.map.setY(this.winHeight / 2 - 150);
					this.map.setWidth(300);
					this.map.setHeight(300);
					this.insideplayer.setX(this.winWidth / 2);
					this.insideplayer.setY(this.winHeight / 2);
					this.exitdoor.setWidth(this.map.getWidth() / 3);
					this.exitdoor.setHeight(this.exitdoor.getWidth() / 2);
					this.exitdoor.setX(this.winWidth / 2 - 25);
					this.exitdoor.setY(this.map.getY() + this.map.getHeight()
							- this.exitdoor.getHeight());
				}
				if (this.auragedoor(
						(int) (this.player.getX() + (this.player.getWidth() / 2)),
						(int) this.player.getY()) == 2) {
					this.map.setCurrentFilename("temphouse.png");
					this.map.setImageDID(R.drawable.temphouse);
					this.map.setX(this.winWidth / 2 - 150);
					this.map.setY(this.winHeight / 2 - 150);
					this.map.setWidth(300);
					this.map.setHeight(300);
					this.insideplayer.setX(this.winWidth / 2);
					this.insideplayer.setY(this.winHeight / 2);
					this.exitdoor.setWidth(this.map.getWidth() / 3);
					this.exitdoor.setHeight(this.exitdoor.getWidth() / 2);
					this.exitdoor.setX(this.winWidth / 2 - 25);
					this.exitdoor.setY(this.map.getY() + this.map.getHeight()
							- this.exitdoor.getHeight());
				}
				if (this.auragedoor(
						(int) (this.player.getX() + (this.player.getWidth() / 2)),
						(int) this.player.getY()) == 3) {
					this.map.setCurrentFilename("hospitalinside.png");
					this.map.setImageDID(R.drawable.hospitalinside);
					this.map.setX(this.winWidth / 2 - 150);
					this.map.setY(this.winHeight / 2 - 150);
					this.map.setWidth(300);
					this.map.setHeight(300);
					this.insideplayer.setX(this.winWidth / 2);
					this.insideplayer.setY(this.winHeight / 2);
					this.exitdoor.setWidth(this.map.getWidth() / 3);
					this.exitdoor.setHeight(this.exitdoor.getWidth() / 2);
					this.exitdoor.setX(this.winWidth / 2 - 25);
					this.exitdoor.setY(this.map.getY() + this.map.getHeight()
							- this.exitdoor.getHeight());
					this.healer.setX(this.map.getX()
							+ ((5 * this.map.getWidth()) / 162));
					this.healer.setY(this.map.getY()
							+ ((32 * this.map.getHeight()) / 146));
					this.healer.setWidth((this.map.getWidth() * 14) / 162);
					this.healer.setHeight((this.map.getHeight() * 17) / 146);
				}
				if (this.auragedoor(
						(int) (this.player.getX() + (this.player.getWidth() / 2)),
						(int) this.player.getY()) == 4) {
					this.map.setCurrentFilename("temphouse.png");
					this.map.setImageDID(R.drawable.temphouse);
					this.map.setX(this.winWidth / 2 - 150);
					this.map.setY(this.winHeight / 2 - 150);
					this.map.setWidth(300);
					this.map.setHeight(300);
					this.insideplayer.setX(this.winWidth / 2);
					this.insideplayer.setY(this.winHeight / 2);
					this.exitdoor.setWidth(this.map.getWidth() / 3);
					this.exitdoor.setHeight(this.exitdoor.getWidth() / 2);
					this.exitdoor.setX(this.winWidth / 2 - 25);
					this.exitdoor.setY(this.map.getY() + this.map.getHeight()
							- this.exitdoor.getHeight());
				}
				// TODO remove duplicated code
				// TODO handle moving differently
			}
			if (this.gameData.isMoveDown()
					&& (!this.aurageIsBlocked((int) this.player.getX() + 3,
							(int) this.player.getY() + 8) && !(this.player
							.getY() >= (this.map.getY() + this.map.getHeight() - 224)))) {
				// if (!blocked)
				this.map.setY(this.map.getY() - 1);
				this.player.setCurrentFilename("playerd.png");
				this.player.setImageDID(R.drawable.playerd);
				if (this.isInGrass((int) this.player.getX() + 3,
						(int) this.player.getY() + 3)) {
					if (this.didEncounter() && !this.teamgone()) {
						this.gameData.setState(GameState.BATTLE);
						this.gameData.moveNone();
					}
				}

			}
			if (this.gameData.isMoveLeft()
					&& (!this.aurageIsBlocked((int) this.player.getX() - 1,
							(int) this.player.getY() + 3) && !(this.player
							.getX() <= (this.map.getX() + 210)))) {
				// if (!blocked)
				this.map.setX(this.map.getX() + 1);

				this.player.setCurrentFilename("playelr.png");
				this.player.setImageDID(R.drawable.playerl);
				if (this.isInGrass((int) this.player.getX() + 3,
						(int) this.player.getY() + 3)) {
					if (this.didEncounter() && !this.teamgone()) {
						this.gameData.setState(GameState.BATTLE);
						this.gameData.moveNone();
					}
				}

			}
			if (this.gameData.isMoveRight()
					&& (!this.aurageIsBlocked((int) this.player.getX() + 8,
							(int) this.player.getY() + 3) && !(this.player
							.getX() >= (this.map.getX() + this.map.getWidth() - 248)))) {
				// if (!blocked)
				this.map.setX(this.map.getX() - 1);

				this.player.setCurrentFilename("playerr.png");
				this.player.setImageDID(R.drawable.playerr);
				if (this.isInGrass((int) this.player.getX() + 3,
						(int) this.player.getY() + 3)) {
					if (this.didEncounter() && !this.teamgone()) {
						this.gameData.setState(GameState.BATTLE);
						this.gameData.moveNone();
					}
				}
			}

			// blocked = false;

			this.map.draw(canvas);
			canvas.drawBitmap(mapImg, 0, 0, null);
			this.player.draw(canvas);

			this.drawMenus(canvas);
		}

		public int auragedoor(int x, int y) {
			int p = (int) this.map.getX();
			int i = (int) this.map.getY();

			if (x >= p + 276) {
				if (y >= i + 50) {
					if (x <= p + 317) {
						if (y <= i + 71) {
							if (!this.aurageIsBlocked(x, y)) {
								return 1;
							}
						}
					}
				}
			}
			if (x >= p + 225) {
				if (y >= i + 284) {
					if (x <= p + 266) {
						if (y <= i + 304) {
							if (!this.aurageIsBlocked(x, y)) {
								return 2;
							}
						}
					}
				}
			}
			if (x >= p + 333) {
				if (y >= i + 276) {
					if (x <= p + 374) {
						if (y <= i + 304) {
							if (!this.aurageIsBlocked(x, y)) {
								return 3;
							}
						}
					}
				}
			}
			if (x >= p + 402) {
				if (y >= i + 374) {
					if (x <= p + 443) {
						if (y <= i + 394) {
							if (!this.aurageIsBlocked(x, y)) {
								return 4;
							}
						}
					}
				}
			}
			return 0;

		}

		public boolean aurageIsBlocked(int x, int y) {
			int p = (int) this.map.getX();
			int i = (int) this.map.getY();
			// *****************************************************************
			// HOUSE 1
			if (x >= p + 276) {
				if (y >= i + 50) {
					if (x <= p + 283) {
						if (y <= i + 71) {
							return true;
						}
					}
				}
			}
			if (x >= p + 276) {
				if (y >= i + 50) {
					if (x <= p + 317) {
						if (y <= i + 61) {
							return true;
						}
					}
				}
			}
			if (x >= p + 290) {
				if (y >= i + 50) {
					if (x <= p + 317) {
						if (y <= i + 71) {
							return true;
						}
					}
				}
			}
			if (x >= p + 317) {
				if (y >= i + 64) {
					if (x <= p + 323) {
						if (y <= i + 71) {
							return true;
						}
					}
				}
			}
			// *******************************************************************HOUSE
			// 2
			if (x >= p + 225) {
				if (y >= i + 284) {
					if (x <= p + 232) {
						if (y <= i + 304) {
							return true;
						}
					}
				}
			}
			if (x >= p + 225) {
				if (y >= i + 284) {
					if (x <= p + 266) {
						if (y <= i + 294) {
							return true;
						}
					}
				}
			}
			if (x >= p + 240) {
				if (y >= i + 284) {
					if (x <= p + 266) {
						if (y <= i + 304) {
							return true;
						}
					}
				}
			}
			if (x >= p + 266) {
				if (y >= i + 298) {
					if (x <= p + 272) {
						if (y <= i + 304) {
							return true;
						}
					}
				}
			}
			// *******************************************************************THIRD
			// HOUSE
			if (x >= p + 333) {
				if (y >= i + 276) {
					if (x <= p + 343) {
						if (y <= i + 304) {
							return true;
						}
					}
				}
			}
			if (x >= p + 333) {
				if (y >= i + 276) {
					if (x <= p + 374) {
						if (y <= i + 294) {
							return true;
						}
					}
				}
			}
			if (x >= p + 350) {
				if (y >= i + 276) {
					if (x <= p + 374) {
						if (y <= i + 304) {
							return true;
						}
					}
				}
			}
			if (x >= p + 374) {
				if (y >= i + 298) {
					if (x <= p + 381) {
						if (y <= i + 304) {
							return true;
						}
					}
				}
			}
			// *******************************************************************FOURTH
			// HOUSE
			if (x >= p + 402) {
				if (y >= i + 374) {
					if (x <= p + 409) {
						if (y <= i + 394) {
							return true;
						}
					}
				}
			}
			if (x >= p + 402) {
				if (y >= i + 374) {
					if (x <= p + 443) {
						if (y <= i + 384) {
							return true;
						}
					}
				}
			}
			if (x >= p + 417) {
				if (y >= i + 374) {
					if (x <= p + 443) {
						if (y <= i + 394) {
							return true;
						}
					}
				}
			}
			if (x >= p + 443) {
				if (y >= i + 387) {
					if (x <= p + 449) {
						if (y <= i + 394) {
							return true;
						}
					}
				}
			}
			// *******************************************************************
			if (x >= p + 297) {
				if (y >= i + 219) {
					if (x <= p + 303) {
						if (y <= i + 225) {
							return true;
						}
					}
				}
			}
			return false;
		}

		public void battleScreen(Canvas canvas) {
			// TODO completely redo everything
			try {
				this.battleScreen.draw(canvas);
				this.enemyplaceholder.draw(canvas);
				this.teamplaceholder.draw(canvas);
			}
			catch (Exception e) {
				this.erroralert.setMessage("ERROR OCCURED! error " + e
						+ "\n at line" + e.getStackTrace()[2].getLineNumber());
				this.erroralert.show();
				e.printStackTrace();
			}

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

		public boolean didBallCatch(Monster foe, baseItem theBall) {
			boolean isGreatBall;
			float randOne;
			if (theBall.getType() == 12) {
				isGreatBall = true;
			}
			else {
				isGreatBall = false;
			}

			if (isGreatBall) {
				randOne = this.rand.getIntBetween(0, 150);
			}
			else {
				randOne = this.rand.getIntBetween(0, 255);
			}
			if (randOne <= 75) {
				float randTwo = this.rand.getIntBetween(0, 255);
				float f;
				f =
						(((foe.getMaxHP() * 255) / theBall.getType()) / (foe
								.getCurrentHP() / 4));
				if (f >= randTwo) {
					return true;
				}
				return false;
			}
			return false;

		}

		public boolean didEncounter() {
			int c = this.rand.getIntBetween(1, 28);
			if (c == 16) {
				return true;
			}
			return false;
		}

		public void dragObjectToNewPosition(MovableObject obj, float startX,
				float startY, float xPos, float yPos) {
			float distanceX;
			float distanceY;
			float newX = obj.getX();
			float newY = obj.getY();
			boolean objMoved = false;

			if (xPos < this.startMouseDragX) {
				distanceX = this.startMouseDragX - xPos;
				newX = obj.getX() - distanceX;
				objMoved = true;
			}
			else if (xPos > this.startMouseDragX) {
				distanceX = xPos - this.startMouseDragX;
				newX = obj.getX() + distanceX;
				objMoved = true;
			}

			if (yPos < this.startMouseDragY) {
				distanceY = this.startMouseDragY - yPos;
				newY = obj.getY() - distanceY;
				objMoved = true;
			}
			else if (yPos > this.startMouseDragY) {
				distanceY = yPos - this.startMouseDragY;
				newY = obj.getY() + distanceY;
				objMoved = true;
			}

			if (objMoved) {
				if (newX < 0) {
					newX = 0;
				}
				if (newX + obj.getWidth() >= this.winWidth - 1) {
					newX = this.winWidth - obj.getWidth() - 1;
				}
				if (newY < 0) {
					newY = 0;
				}
				if (newY + obj.getHeight() >= this.winHeight - 1) {
					newY = this.winHeight - obj.getHeight() - 1;
				}
				obj.setX(newX);
				obj.setY(newY);
				this.startMouseDragX = xPos;
				this.startMouseDragY = yPos;
			} // end of if (objMoved)
		} // end of public void dragObjectToNewPosition

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
			if (this.loadButton != null) {
				this.loadButton.setX(this.winWidth / 2
						- this.loadButton.getWidth() / 2);
				this.loadButton.setY(this.winHeight / 4 * 3);
				this.loadButton.draw(canvas);
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

		public MovableObject getTexamonMO(String name, int x, int y) {
			String file = "p_" + name.toLowerCase(Locale.US) + ".jpg";
			MovableObject t =
					new MovableObject(file, R.drawable.p_c, x, y, 50, 50,
							this.getResources());
			if (name.equalsIgnoreCase("C")) {
				t =
						new MovableObject(file, R.drawable.p_c, x, y, 50, 50,
								this.getResources());
			}
			else if (name.equalsIgnoreCase("Wattle")) {
				t =
						new MovableObject(file, R.drawable.p_wattle, x, y, 50,
								50, this.getResources());
			}
			else if (name.equalsIgnoreCase("Coaldra")) {
				t =
						new MovableObject(file, R.drawable.p_coaldra, x, y, 50,
								50, this.getResources());
			}
			else if (name.equalsIgnoreCase("Ploggy")) {
				t =
						new MovableObject(file, R.drawable.p_ploggy, x, y, 50,
								50, this.getResources());
			}
			else if (name.equalsIgnoreCase("Bolrock")) {
				t =
						new MovableObject(file, R.drawable.p_bolrock, x, y, 50,
								50, this.getResources());
			}
			else if (name.equalsIgnoreCase("Birderp")) {
				t =
						new MovableObject(file, R.drawable.p_birderp, x, y, 50,
								50, this.getResources());
			}
			else if (name.equalsIgnoreCase("Aqwhirl")) {
				t =
						new MovableObject(file, R.drawable.p_aqwhirl, x, y, 50,
								50, this.getResources());
			}
			else if (name.equalsIgnoreCase("Sunflo")) {
				t =
						new MovableObject(file, R.drawable.p_sunflo, x, y, 50,
								50, this.getResources());
			}
			else if (name.equalsIgnoreCase("Telecat")) {
				t =
						new MovableObject(file, R.drawable.p_telecat, x, y, 50,
								50, this.getResources());
			}
			else if (name.equalsIgnoreCase("Roxer")) {
				t =
						new MovableObject(file, R.drawable.p_roxer, x, y, 50,
								50, this.getResources());
			}
			else if (name.equalsIgnoreCase("Magmuk")) {
				t =
						new MovableObject(file, R.drawable.p_magmuk, x, y, 50,
								50, this.getResources());
			}
			return t;
		}

		public void insidehospital(Canvas canvas) {

			if (this.gameData.isMoveUp()
					&& (this.insideplayer.isinsde(this.map))) {
				// if (!blocked)
				this.insideplayer.setY(this.insideplayer.getY() - 1);
				if (!this.insideplayer.isinsde(this.map)) {
					this.insideplayer.setY(this.insideplayer.getY() + 1);
				}
				this.insideplayer.setCurrentFilename("insideplayer.png");
				this.insideplayer.setImageDID(R.drawable.insideplayer);

			}

			if (this.gameData.isMoveDown()
					&& (this.insideplayer.isinsde(this.map))) {
				// if (!blocked)
				this.insideplayer.setY(this.insideplayer.getY() + 1);
				if (!this.insideplayer.isinsde(this.map)) {
					this.insideplayer.setY(this.insideplayer.getY() - 1);
				}
				this.insideplayer.setCurrentFilename("insideplayerd.png");
				this.insideplayer.setImageDID(R.drawable.insideplayerd);

			}
			if (this.gameData.isMoveLeft()
					&& (this.insideplayer.isinsde(this.map))) {
				// if (!blocked)
				this.insideplayer.setX(this.insideplayer.getX() - 1);
				if (!this.insideplayer.isinsde(this.map)) {
					this.insideplayer.setX(this.insideplayer.getX() + 1);
				}
				this.insideplayer.setCurrentFilename("insideplayelr.png");
				this.insideplayer.setImageDID(R.drawable.insideplayerl);

			}
			if (this.gameData.isMoveRight()
					&& (this.insideplayer.isinsde(this.map))) {
				// if (!blocked)
				this.insideplayer.setX(this.insideplayer.getX() + 1);
				if (!this.insideplayer.isinsde(this.map)) {
					this.insideplayer.setX(this.insideplayer.getX() - 1);
				}
				this.insideplayer.setCurrentFilename("insideplayerr.png");
				this.insideplayer.setImageDID(R.drawable.insideplayerr);

			}

			// blocked = false;
			if (this.drawdelay == 2) {
				this.drawdelay = 3;
			}
			if (this.drawdelay == 1) {
				this.drawdelay = 2;
			}
			if (this.drawdelay == 0) {
				this.drawdelay = 1;
			}

			if (this.map.getCurrentFilename().equalsIgnoreCase(
					"hospitalinside.png")) {
				this.map.setCurrentFilename("hospitalinside2.png");
				this.map.setImageDID(R.drawable.hospitalinside2);
				this.drawdelay = 0;
			}
			if (this.drawdelay == 3) {
				this.map.setCurrentFilename("hospitalinside.png");
				this.map.setImageDID(R.drawable.hospitalinside);
			}

			this.map.draw(canvas);
			this.exitdoor.draw(canvas);
			this.insideplayer.draw(canvas);

			this.drawMenus(canvas);

			if (this.insideplayer.intersects(this.exitdoor)) {
				this.map.setCurrentFilename("aurage.png");
				this.map.setImageDID(R.drawable.aurage);
				this.map.setX(this.winWidth / 2 - 350);
				this.map.setY(this.winHeight / 2 - 350);
				this.map.setWidth(700);
				this.map.setHeight(700);
				return;
			}
			if (this.insideplayer.intersects(this.healer)) {
				for (int i = 0; i <= this.gameData.getTeam().getSize() - 1; i++) {
					this.gameData
							.getTeam()
							.get(i)
							.setCurrentHP(
									this.gameData.getTeam().get(i).getMaxHP());
				}
				this.myDrawText(canvas, "HEALING", 10, this.winWidth / 2,
						this.winHeight / 2, Color.GREEN, 1);
			}
		}

		public void insidehouse(Canvas canvas) {

			if (this.gameData.isMoveUp()
					&& (this.insideplayer.isinsde(this.map))) {
				// if (!blocked)
				this.insideplayer.setY(this.insideplayer.getY() - 1);
				if (!this.insideplayer.isinsde(this.map)) {
					this.insideplayer.setY(this.insideplayer.getY() + 1);
				}
				this.insideplayer.setCurrentFilename("insideplayer.png");
				this.insideplayer.setImageDID(R.drawable.insideplayer);

			}

			if (this.gameData.isMoveDown()
					&& (this.insideplayer.isinsde(this.map))) {
				// if (!blocked)
				this.insideplayer.setY(this.insideplayer.getY() + 1);
				if (!this.insideplayer.isinsde(this.map)) {
					this.insideplayer.setY(this.insideplayer.getY() - 1);
				}
				this.insideplayer.setCurrentFilename("insideplayerd.png");
				this.insideplayer.setImageDID(R.drawable.insideplayerd);

			}
			if (this.gameData.isMoveLeft()
					&& (this.insideplayer.isinsde(this.map))) {
				// if (!blocked)
				this.insideplayer.setX(this.insideplayer.getX() - 1);
				if (!this.insideplayer.isinsde(this.map)) {
					this.insideplayer.setX(this.insideplayer.getX() + 1);
				}
				this.insideplayer.setCurrentFilename("insideplayelr.png");
				this.insideplayer.setImageDID(R.drawable.insideplayerl);

			}
			if (this.gameData.isMoveRight()
					&& (this.insideplayer.isinsde(this.map))) {
				// if (!blocked)
				this.insideplayer.setX(this.insideplayer.getX() + 1);
				if (!this.insideplayer.isinsde(this.map)) {
					this.insideplayer.setX(this.insideplayer.getX() - 1);
				}
				this.insideplayer.setCurrentFilename("insideplayerr.png");
				this.insideplayer.setImageDID(R.drawable.insideplayerr);

			}

			// blocked = false;

			this.map.draw(canvas);
			this.exitdoor.draw(canvas);
			this.insideplayer.draw(canvas);

			this.drawMenus(canvas);

			if (this.insideplayer.intersects(this.exitdoor)) {
				this.map.setCurrentFilename("aurage.png");
				this.map.setImageDID(R.drawable.aurage);
				this.map.setX(this.winWidth / 2 - 350);
				this.map.setY(this.winHeight / 2 - 350);
				this.map.setWidth(700);
				this.map.setHeight(700);
				return;
			}
		}

		public boolean isInGrass(int x, int y) {
			int p = (int) this.map.getX();
			int i = (int) this.map.getY();
			if (x >= p + 209) {
				if (y >= i + 439) {
					if (x <= p + 293) {
						if (y <= i + 483) {
							return true;
						}
					}
				}
			}
			return false;
		}

		public boolean isInsideOf(float gx1, float gy1, MovableObject object) {
			if (gx1 >= object.getX()
					&& gx1 <= object.getX() + object.getWidth()
					&& gy1 >= object.getY()
					&& gy1 <= object.getY() + object.getHeight()) {
				return true;
			}
			return false;
		}

		public boolean isNear(int x, int y, int x2, int y2, int radius) {
			if ((x < x2 + radius) && (x >= x - radius) && (y <= y2 + radius)
					&& (y >= y2 - radius)) {
				return true;
			}
			return false;
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

				this.map.setCurrentFilename(tempparts[0]);
				this.map.setX(Integer.decode(tempparts[2]));
				this.map.setY(Integer.decode(tempparts[4]));
				this.map.setWidth(Integer.decode(tempparts[6]));
				this.map.setHeight(Integer.decode(tempparts[8]));
				this.insideplayer.setX(Integer.decode(tempparts[10]));
				this.insideplayer.setY(Integer.decode(tempparts[12]));

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

		private void myDrawText(Canvas canvas, String text, int size, float x,
				float y, int color, int alignment) {
			Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
			paintText.setColor(color);
			Typeface typeface =
					Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
			paintText.setTextSize(size);
			paintText.setTypeface(typeface); // Sans Serif is the default

			if (alignment == 1) {
				paintText.setTextAlign(Paint.Align.CENTER);
			}

			canvas.drawText(text, x, y, paintText);

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
			this.loadButton =
					new MovableObject("load.png", R.drawable.load,
							(this.winWidth / 2) - (this.winWidth / 4),
							(this.winHeight * 3 / 4) - (this.winHeight / 16),
							this.winWidth / 2, this.winHeight / 8,
							this.getResources());
			this.battleScreen =
					new MovableObject("battlegui2.png", R.drawable.battlegui2,
							0, 0, this.winWidth, this.winHeight,
							this.getResources());
			this.map =
					new MovableObject("aurage.png", R.drawable.aurage,
							this.winWidth / 2 - 350, this.winHeight / 2 - 350,
							700, 700, this.getResources());
			this.player =
					new MovableObject("player.png", R.drawable.player,
							(this.winWidth / 2) - 3, (this.winHeight / 2) - 3,
							7, 7, this.getResources());
			this.menureturnbutton =
					new MovableObject("menuupbutton.png",
							R.drawable.menuupbutton, 10, this.winHeight - 60,
							50, 50, this.getResources());
			this.insideplayer =
					new MovableObject("insideplayer.png",
							R.drawable.insideplayer, 50, 50, 50, 50,
							this.getResources());
			this.exitdoor =
					new MovableObject("exitdoor.png", R.drawable.exitdoor, 50,
							25, 10, 10, this.getResources());
			this.healer =
					new MovableObject("blank.png", R.drawable.blank, 5, 32, 14,
							17, this.getResources());
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

				if (this.map.getCurrentFilename()
						.equalsIgnoreCase("aurage.png")) {
					this.aurage(canvas);
				}
				if (this.map.getCurrentFilename().equalsIgnoreCase(
						"temphouse.png")) {
					this.insidehouse(canvas);
				}
				if (this.map.getCurrentFilename().equalsIgnoreCase(
						"hospitalinside.png")
						|| this.map.getCurrentFilename().equalsIgnoreCase(
								"hospitalinside2.png")) {
					this.insidehospital(canvas);
				}

				if (this.gameData.getState() == GameState.BATTLE) {
					this.battleScreen(canvas);
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
					if (this.menudown
							&& this.menureturnbutton.containsPoint(new Point(x,
									y))) {
						this.menudown = false;

					}
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
						this.loadButton.setX(-200);
					}
					if (this.loadButton.getRect().contains(x, y)) {
						this.gameData.setState(GameState.INGAME);
						this.load();
						this.startButton.setX(-200);
						this.loadButton.setX(-200);
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

		public String[] texamontostring(Monster thetexamon) {
			String[] info = new String[21];
			info[0] = "" + thetexamon.getCurrentHP();
			info[1] = ",";
			info[2] = "" + thetexamon.getCurrentLVL();
			info[3] = ",";
			info[4] = thetexamon.getLocation();
			info[5] = ",";
			info[6] = thetexamon.getMove(0).getName();
			info[7] = ",";
			info[8] = thetexamon.getMove(1).getName();
			info[9] = ",";
			info[10] = thetexamon.getMove(2).getName();
			info[11] = ",";
			info[12] = thetexamon.getMove(3).getName();
			info[13] = ",";
			info[14] = thetexamon.getNickName();
			info[15] = ",";
			info[16] = "" + thetexamon.getUID();
			info[17] = ",";
			info[18] = "" + thetexamon.getXP();
			info[19] = ",";
			info[20] = thetexamon.getTexamon().getName();
			return info;
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