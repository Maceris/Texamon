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

import tiled.core.Map;
import tiled.core.TileLayer;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;

/**
 * Information about the current game
 *
 * @author Ches Burks
 *
 */
@SuppressLint("RtlHardcoded")
public class GameData {
	private enum Direction {
		NONE,
		UP,
		RIGHT,
		DOWN,
		LEFT;
	}

	private String playerName;
	private GameState state;
	private ArrayList<?> gymsBeaten;
	private int xPos;
	private int yPos;
	private Map currentMap;
	private Party team;
	private Bitmap curMapImg;

	private Direction movement;

	/**
	 * Constructs a new game data object with default values
	 */
	public GameData() {
		this.playerName = "Name";
		this.state = GameState.MAIN_MENU;
		this.gymsBeaten = new ArrayList<Integer>();
		this.xPos = 0;
		this.yPos = 0;
		this.currentMap = new Map(0, 0);
		this.team = new Party();
		this.movement = Direction.NONE;
	}

	/**
	 * Returns the current maps bitmap representation.
	 *
	 * @return the bitmap representing the current map
	 */
	public synchronized Bitmap getCurMapImg() {
		return this.curMapImg;
	}

	/**
	 * Returns the current map
	 *
	 * @return the current map
	 */
	public synchronized Map getCurrentMap() {
		return this.currentMap;
	}

	/**
	 * Returns the array of gyms beaten in this game
	 *
	 * @return the array of beaten gyms
	 */
	public synchronized ArrayList<?> getGymsBeaten() {
		return this.gymsBeaten;
	}

	/**
	 * Returns the number of gyms beaten in this game
	 *
	 * @return the number of gyms beaten
	 */
	public synchronized int getNumGymsBeaten() {
		return this.gymsBeaten.size();
	}

	/**
	 * Returns the name of the player
	 *
	 * @return the playerName
	 */
	public synchronized String getPlayerName() {
		return this.playerName;
	}

	/**
	 * Returns the current game state
	 *
	 * @return the state
	 */
	public synchronized GameState getState() {
		return this.state;
	}

	/**
	 * Returns the party
	 *
	 * @return the team
	 */
	public synchronized Party getTeam() {
		return this.team;
	}

	/**
	 * Returns the players current x position on the map
	 *
	 * @return the x position
	 */
	public synchronized int getxPos() {
		return this.xPos;
	}

	/**
	 * Returns the players current y position on the map
	 *
	 * @return the yPos
	 */
	public synchronized int getyPos() {
		return this.yPos;
	}

	/**
	 * Returns true if the player is inside a tile with plants on it
	 *
	 * @return true if the player is in plants, false otherwise
	 */
	public synchronized boolean isInGrass() {
		int newx = this.xPos;
		int newy = this.yPos;

		if (this.currentMap.getLayerCount() >= 7) {
			if (((TileLayer) this.currentMap.getLayer(2)).getTileAt(newx, newy) != null) {
				// plants
				return true;
			}
		}
		else {
			System.err.println("Map does not have 7+ layers");
		}
		return false;
	}

	/**
	 * Returns true if the movement direction was last set to down.
	 *
	 * @return if the player is trying to move down
	 */
	public boolean isMoveDown() {
		synchronized (this.movement) {
			return this.movement == Direction.DOWN;
		}
	}

	/**
	 * Returns true if the movement direction was last set to left.
	 *
	 * @return if the player is trying to move left
	 */
	public boolean isMoveLeft() {
		synchronized (this.movement) {
			return this.movement == Direction.LEFT;
		}
	}

	/**
	 * Returns true if the movement direction was last set to stationary.
	 *
	 * @return if the player is not moving
	 */
	public boolean isMoveNone() {
		synchronized (this.movement) {
			return this.movement == Direction.NONE;
		}
	}

	/**
	 * Returns true if the movement direction was last set to right.
	 *
	 * @return if the player is trying to move right
	 */
	public boolean isMoveRight() {
		synchronized (this.movement) {
			return this.movement == Direction.RIGHT;
		}
	}

	/**
	 * Returns true if the movement direction was last set to up.
	 *
	 * @return if the player is trying to move up
	 */
	public boolean isMoveUp() {
		synchronized (this.movement) {
			return this.movement == Direction.UP;
		}
	}

	/**
	 * Sets the current movement direction to down. The movement can be only in
	 * one direction at a time, including not moving at all.
	 */
	public void moveDown() {
		synchronized (this.movement) {
			this.movement = Direction.DOWN;
		}
	}

	/**
	 * Sets the current movement direction to left. The movement can be only in
	 * one direction at a time, including not moving at all.
	 */
	public void moveLeft() {
		synchronized (this.movement) {
			this.movement = Direction.LEFT;
		}
	}

	/**
	 * Sets the current movement direction stationary, or not moving in any
	 * direction. The movement can be only in one direction at a time, including
	 * not moving at all.
	 */
	public void moveNone() {
		synchronized (this.movement) {
			this.movement = Direction.NONE;
		}
	}

	/**
	 * Sets the current movement direction to right. The movement can be only in
	 * one direction at a time, including not moving at all.
	 */
	public void moveRight() {
		synchronized (this.movement) {
			this.movement = Direction.RIGHT;
		}
	}

	/**
	 * Sets the current movement direction to up. The movement can be only in
	 * one direction at a time, including not moving at all.
	 */
	public void moveUp() {
		synchronized (this.movement) {
			this.movement = Direction.UP;
		}
	}

	/**
	 * Sets the current map image to the supplied one. This should be done
	 * whenever the map is changed.
	 *
	 * @param newMapImg the new bitmap to display
	 */
	public synchronized void setCurMapImg(Bitmap newMapImg) {
		this.curMapImg = newMapImg;
	}

	/**
	 * Sets the current map
	 *
	 * @param newMap the current map to set
	 */
	public synchronized void setCurrentMap(Map newMap) {
		this.currentMap = newMap;
	}

	/**
	 * Sets the array of gyms beaten for this game
	 *
	 * @param newGymsBeaten the array of beaten gyms
	 */
	public synchronized void setGymsBeaten(ArrayList<?> newGymsBeaten) {
		this.gymsBeaten = newGymsBeaten;
	}

	/**
	 * Sets the name of the player
	 *
	 * @param newName the playerName to set
	 */
	public synchronized void setPlayerName(String newName) {
		this.playerName = newName;
	}

	/**
	 * Sets the new state of the game
	 *
	 * @param newState the state to set
	 */
	public synchronized void setState(GameState newState) {
		this.state = newState;
	}

	/**
	 * Sets the new team
	 *
	 * @param newTeam the team to set
	 */
	public synchronized void setTeam(Party newTeam) {
		this.team = newTeam;
	}

	/**
	 * Sets the players current x position on the map
	 *
	 * @param x the x position to set
	 */
	public synchronized void setxPos(int x) {
		this.xPos = x;
	}

	/**
	 * Sets the players current x position on the map
	 *
	 * @param y the y position to set
	 */
	public synchronized void setyPos(int y) {
		this.yPos = y;
	}

	/**
	 * Returns true if the player will run into a solid object if they move in
	 * their current direction.
	 *
	 * @return true if the next movement would be into a solid object, false
	 *         otherwise
	 */
	public synchronized boolean willCollide() {
		int newx = this.xPos;
		int newy = this.yPos;
		switch (this.movement) {
		case DOWN:
			++newy;
			break;
		case LEFT:
			--newx;
			break;
		case NONE:
			return false;
		case RIGHT:
			++newx;
			break;
		case UP:
			--newy;
			break;
		default:
			return false;
		}
		if (this.currentMap.getLayerCount() >= 8) {
			if (((TileLayer) this.currentMap.getLayer(1)).getTileAt(newx, newy) != null) {
				// buildings
				return true;
			}
			if (((TileLayer) this.currentMap.getLayer(6)).getTileAt(newx, newy) != null) {
				// fences
				return true;
			}
		}
		else {
			System.err.println("Map does not have 8+ layers");
		}
		return false;
	}

}
