package com.goosepatrol.game;

import com.badlogic.gdx.graphics.Color;

/**
 * shown when player loses
 * 2 causes - energy runs out or clock runs out
 *
 * BUTTONS: try again to restart
 *          main menu to return to main menu
 */
public class GameOverScreen extends UiScreen {

    // true if time ran out, false if energy ran out
    private final boolean timeUp;

    // try again button index 0
    //main menu index 1
    private final ButtonMenu menu = new ButtonMenu(
        new String[] {"TRY AGAIN", "MAIN MENU"},
        275f, 340f, 250f, 44f, 16f);


    /**
     * cerates game over screen
     *
     * @param game the game - for screen changes and shared batch
     * @param timeUp true if time ran out, false if energy ran out
     */
    public GameOverScreen(GoosePatrolGame game, boolean timeUp) {
        super(game);
        this.timeUp = timeUp;
    }


    /**
     * handles buttons
     * try again switcehs to new game screen
     * main menu switches to new main menu
     *
     * @param delta seconds since last frame
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
     * draws bg, game over heading, and panel containing loss condition
     */
    @Override
    protected void draw() {
        background(".png", 0.35f);
        textCentered(game.titleFont, "GAME OVER", W / 2f, 530f, RED);

        panel(160f, 190f, 480f, 230f);
        textCentered(game.uiFont, timeUp ? "TIME'S UP!" : "THE GEESE GOT YOU",
            W / 2f, 395f, Color.WHITE);

        menu.draw(this, false);

        image(game.texture("ui/menu_geese.png"), 600f, 200f, 130f, 150f);
    }
}
