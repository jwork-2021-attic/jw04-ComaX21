/*
 * Copyright (C) 2015 Aeranythe Echosong
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package screen;

import world.*;
import asciiPanel.AsciiPanel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aeranythe Echosong
 */
public class PlayScreen implements Screen {

    private World world;
    private Creature player;
    private int screenWidth;
    private int screenHeight;
    private List<String> messages;
    private List<String> oldMessages;

    public PlayScreen() {
        this.screenWidth = 40;
        this.screenHeight = 40;
        createMazeWorld();
        this.messages = new ArrayList<String>();
        this.oldMessages = new ArrayList<String>();

        CreatureFactory creatureFactory = new CreatureFactory(this.world);
        createCreatures(creatureFactory);
    }

    private void createCreatures(CreatureFactory creatureFactory) {
        this.player = creatureFactory.newMazePlayer(this.messages);

        /*for (int i = 0; i < 8; i++) {
            creatureFactory.newFungus();
        }*/
    }

    private void createMazeWorld() {
        world = new WorldBuilder(40, 40).makeMaze().build();
    }

    private void displayTiles(AsciiPanel terminal) {
        // Show terrain
        for (int x = 0; x < screenWidth; x++) {
            for (int y = 0; y < screenHeight; y++) {
                //terminal.write(world.glyph(x, y), x, y, world.color(x, y));
                if (player.canSee(x, y)) {
                    terminal.write(world.glyph(x, y), x, y, world.color(x, y));
                } else {
                    terminal.write(world.glyph(x, y), x, y, Color.DARK_GRAY);
                }
            }
        }
        //Show end
        terminal.write((char) 3, this.world.width() - 1, this.world.height() - 1, Color.red);
        // Show creatures
        for (Creature creature : world.getCreatures()) {
            if (player.canSee(creature.x(), creature.y())) {
                terminal.write(creature.glyph(), creature.x(), creature.y(), creature.color());
            }
        }
        // Creatures can choose their next action now
        world.update();
    }

    private void displayMessages(AsciiPanel terminal, List<String> messages) {
        for (int i = 0; i < messages.size(); i++) {
            terminal.write(messages.get(i), 1, 51);
        }
        this.oldMessages.addAll(messages);
        messages.clear();
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        // Terrain and creatures
        displayTiles(terminal);
        // Player
        terminal.write(player.glyph(), player.x(), player.y(), player.color());
        // Stats
        //String stats = String.format("%3d/%3d hp", player.hp(), player.maxHP());
        //terminal.write(stats, 1, 50);
        // Messages
        //displayMessages(terminal, this.messages);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        boolean result = false;
        switch (key.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                result = player.moveBy(-1, 0);
                break;
            case KeyEvent.VK_RIGHT:
                result = player.moveBy(1, 0);
                break;
            case KeyEvent.VK_UP:
                result = player.moveBy(0, -1);
                break;
            case KeyEvent.VK_DOWN:
                result = player.moveBy(0, 1);
                break;
        }
        if(result)
            return new WinScreen();
        else
            return this;
    }


}
