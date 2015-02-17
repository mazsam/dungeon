/*
 * Copyright (C) 2015 Bernardo Sulzbach
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
 */

package org.dungeon.game;

import org.dungeon.gui.GameWindow;
import org.dungeon.util.Constants;

/**
 * WorldMap class that represents an ASCII map from the surroundings of the player.
 * <p/>
 * Created by Bernardo on 17/02/2015.
 */
public class WorldMap {

  // TODO '?' to what the hero does not know yet.
  // TODO '~' to what the world hasn't generated yet.
  // TODO colors (to avoid problems such as Swamp and Savannah both starting with 'S' and to make the map prettier).
  private String map;

  /**
   * Constructs a map based on the position of the Hero in the World.
   * <p/>
   * The hero is represented with an "at" sign ('@') at the center of the map.
   */
  public WorldMap(World world, Point heroPosition) {
    int rows = GameWindow.ROWS - 1;
    int cols = Constants.COLS;
    int initX = heroPosition.getX() - (cols - 1) / 2;
    int lastX = initX + cols - 1;
    int initY = heroPosition.getY() + (rows - 1) / 2;
    int lastY = initY - rows + 1;
    // Add 1 to account for newlines.
    StringBuilder builder = new StringBuilder((cols + 1) * rows);
    for (int curY = initY; curY >= lastY; curY--) {
      for (int curX = initX; curX <= lastX; curX++) {
        Point currentPosition = new Point(curX, curY);
        if (currentPosition.equals(heroPosition)) {
          builder.append('@');
        } else if (world.hasLocation(currentPosition)) {
          builder.append(world.getLocation(currentPosition).getName().charAt(0));
        } else {
          builder.append('?');
        }
      }
      builder.append('\n');
    }
    map = builder.toString();
  }

  @Override
  public String toString() {
    return map;
  }

}
