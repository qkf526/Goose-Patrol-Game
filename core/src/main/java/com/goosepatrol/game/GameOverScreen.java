package com.goosepatrol.game;

import com.badlogic.gdx.graphics.Color;

/** Shown when the player loses. Two variants: out of health, or out of time. */
public class GameOverScreen extends UiScreen {

    private final boolean timeUp;

    private final ButtonMenu menu = new ButtonMenu(
        new String[] {"TRY AGAIN", "MAIN MENU"},
        275f, 340f, 250f, 44f, 16f);

    public GameOverScreen(GoosePatrolGame game, boolean timeUp) {
        super(game);
        this.timeUp = timeUp;
    }

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
