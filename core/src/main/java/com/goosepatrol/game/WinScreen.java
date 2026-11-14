package com.goosepatrol.game;

import com.badlogic.gdx.graphics.Color;

/** screen shown when all geese defeated
 * lists results of match
 */
public class WinScreen extends UiScreen {

    private final String[][] rows;

    // play again index 0, main menu index 1
    private final ButtonMenu menu = new ButtonMenu(
        new String[] {"PLAY AGAIN", "MAIN MENU"},
        275f, 275f, 250f, 44f, 16f);

    /**
     * creates win screen
     * @param game
     * @param secondsLeft seconds remaining on clock
     * @param health players energy at end
     * @param maxHealth maximum energy
     * @param itemsUsed total num of items used
     */
    public WinScreen(GoosePatrolGame game, int secondsLeft, int health, int maxHealth, int itemsUsed) {
        super(game);
        rows = new String[][] {
            {"TIME LEFT", String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60)},
            {"ENERGY LEFT", health + "/" + maxHealth},
            {"ITEMS USED", String.valueOf(itemsUsed)}
        };
    }

    /**
     * handles buttons
     *
     * @param delta time since last frame
     */
    @Override
    protected void update(float delta) {
        switch (menu.update(mouseX, mouseY)) {
            case 0:
                game.setScreen(new GameScreen(game));
                break;
            case 1:
                game.setScreen(new MenuScreen(game));
                break;
            default:
                break;
        }
    }

    /**
     * draws background, heading, panel
     */
    @Override
    protected void draw() {
        background(".png", 0.35f);

        textCentered(game.titleFont, "GEESE DISPERSED!", W / 2f, 560f, GOLD);

        panel(180f, 120f, 440f, 350f);
        float top = 440f;
        for (String[] row : rows) {
            text(game.uiFont, row[0], 215f, top, Color.WHITE);
            textRight(game.uiFont, row[1], 585f, top, GOLD);
            top -= 38f;
        }
        rule(205f, 292f, 390f);

        menu.draw(this, false);

        image(game.texture("ui/menu_student.png"), 40f, 170f, 100f, 130f);
    }
}
