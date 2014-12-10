/* 
 * Copyright (C) 2014 Bernardo Sulzbach
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
import org.dungeon.help.Help;
import org.dungeon.io.DLogger;
import org.dungeon.io.IO;
import org.dungeon.io.Loader;
import org.dungeon.utils.Math;
import org.dungeon.utils.SystemInfo;
import org.dungeon.utils.Utils;

public class Game {

    private static int turnLength = 0;
    private static boolean configurationsChanged = false;

    private static GameWindow gameWindow;
    private static GameState gameState;

    public static void main(String[] args) {
        Help.initialize();
        DLogger.initialize();
        GameData.loadGameData();
        gameWindow = new GameWindow();
        gameState = Loader.loadGame(null);
    }

    public static GameWindow getGameWindow() {
        return gameWindow;
    }

    public static GameState getGameState() {
        return gameState;
    }

    // Renders a turn based on an input string.
    public static void renderTurn(IssuedCommand issuedCommand) {
        // Clears the text pane.
        getGameWindow().clearTextPane();
        processInput(issuedCommand);
        if (gameState.getHero().isDead()) {
            IO.writeString("You died.");
            // After the player's death, just prompt to load the default save file.
            gameState = Loader.loadGame(null);
        } else {
            // Advance the campaign's world date.
            gameState.getWorld().rollDate(turnLength);
            // Refresh the campaign state.
            Engine.refresh();
            // After a turn that consumed time, the campaign is not saved anymore.
            if (turnLength != 0 || configurationsChanged) {
                gameState.setSaved(false);
            }
        }
    }

    // Processes the player input.
    private static void processInput(IssuedCommand issuedCommand) {
        gameState.getCommandHistory().addCommand(issuedCommand);
        gameState.getStatistics().addCommand(issuedCommand);
        // Reset the turn variables.
        turnLength = 0;
        configurationsChanged = false;
        if (issuedCommand.firstTokenEquals("rest")) {
            turnLength = gameState.getHero().rest();
        } else if (issuedCommand.firstTokenEquals("sleep")) {
            turnLength = gameState.getHero().sleep();
        } else if (issuedCommand.firstTokenEquals("look") || issuedCommand.firstTokenEquals("peek")) {
            gameState.getHero().look();
        } else if (issuedCommand.firstTokenEquals("inventory") || issuedCommand.firstTokenEquals("items")) {
            gameState.getHero().printInventory();
        } else if (issuedCommand.firstTokenEquals("loot") || issuedCommand.firstTokenEquals("pick")) {
            gameState.getHero().pickItem(issuedCommand);
            turnLength = 120;
        } else if (issuedCommand.firstTokenEquals("equip")) {
            gameState.getHero().parseEquip(issuedCommand);
        } else if (issuedCommand.firstTokenEquals("unequip")) {
            gameState.getHero().unequipWeapon();
        } else if (issuedCommand.firstTokenEquals("eat") || issuedCommand.firstTokenEquals("devour")) {
            gameState.getHero().eatItem(issuedCommand);
            turnLength = 120;
        } else if (issuedCommand.firstTokenEquals("walk") || issuedCommand.firstTokenEquals("go")) {
            turnLength = Engine.parseHeroWalk(issuedCommand);
        } else if (issuedCommand.firstTokenEquals("drop")) {
            gameState.getHero().dropItem(issuedCommand);
        } else if (issuedCommand.firstTokenEquals("destroy") || issuedCommand.firstTokenEquals("crash")) {
            gameState.getHero().destroyItem(issuedCommand);
            turnLength = 120;
        } else if (issuedCommand.firstTokenEquals("status")) {
            gameState.getHero().printAllStatus();
        } else if (issuedCommand.firstTokenEquals("hero") || issuedCommand.firstTokenEquals("me")) {
            gameState.getHero().printHeroStatus();
        } else if (issuedCommand.firstTokenEquals("age")) {
            gameState.getHero().printAge();
        } else if (issuedCommand.firstTokenEquals("weapon")) {
            gameState.getHero().printWeaponStatus();
        } else if (issuedCommand.firstTokenEquals("kill") || issuedCommand.firstTokenEquals("attack")) {
            turnLength = gameState.getHero().attackTarget(issuedCommand);
        } else if (issuedCommand.firstTokenEquals("statistics")) {
            gameState.printGameStatistics();
        } else if (issuedCommand.firstTokenEquals("achievements")) {
            gameState.printUnlockedAchievements();
        } else if (issuedCommand.firstTokenEquals("spawns")) {
            gameState.getWorld().printSpawnCounters();
        } else if (issuedCommand.firstTokenEquals("time") || issuedCommand.firstTokenEquals("date")) {
            turnLength = gameState.getHero().printDateAndTime();
        } else if (issuedCommand.firstTokenEquals("system")) {
            SystemInfo.printSystemInfo();
        } else if (issuedCommand.firstTokenEquals("help") || issuedCommand.firstTokenEquals("?")) {
            Help.printHelp(issuedCommand);
        } else if (issuedCommand.firstTokenEquals("commands")) {
            Help.printCommandList(issuedCommand);
        } else if (issuedCommand.firstTokenEquals("save")) {
            Loader.saveGame(gameState, issuedCommand);
        } else if (issuedCommand.firstTokenEquals("load")) {
            GameState loadedGameState = Loader.loadGame(issuedCommand);
            if (loadedGameState != null) {
                gameState = loadedGameState;
            }
        } else if (issuedCommand.firstTokenEquals("quit") || issuedCommand.firstTokenEquals("exit")) {
            Game.exit();
        } else if (issuedCommand.firstTokenEquals("license") || issuedCommand.firstTokenEquals("copyright")) {
            Utils.printLicense();
        } else if (issuedCommand.firstTokenEquals("fibonacci")) {
            Math.fibonacci(issuedCommand);
        } else if (issuedCommand.firstTokenEquals("hint") || issuedCommand.firstTokenEquals("tip")) {
            gameState.printNextHint();
        } else if (issuedCommand.firstTokenEquals("poem")) {
            gameState.printNextPoem();
        } else if (issuedCommand.firstTokenEquals("version")) {
            Utils.printVersion();
        } else if (issuedCommand.firstTokenEquals("debug")) {
            DebugTools.parseDebugCommand(issuedCommand);
        } else if (issuedCommand.firstTokenEquals("config")) {
            configurationsChanged = ConfigTools.parseConfigCommand(issuedCommand);
        } else {
            // The user issued a command, but it was not recognized.
            Utils.printInvalidCommandMessage(issuedCommand.getFirstToken());
        }
    }

    // Exits the game, prompting the user if the current state should be saved if it is not already saved.
    public static void exit() {
        if (!gameState.isSaved()) {
            Loader.saveGame(gameState);
        }
        DLogger.info("Exited with no problems.");
        System.exit(0);
    }

}