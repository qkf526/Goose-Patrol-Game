package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

/**
 * Main menu: PLAY, TUTORIAL, QUIT. Keyboard and mouse.
 * */
public class MenuScreen extends UiScreen {

    /**
     * creates buttons on main menu
     */
    private final ButtonMenu menu = new ButtonMenu(
        new String[] {"PLAY", "TUTORIAL", "QUIT"},
        272f, 340f, 256f, 44f, 22f);

    public MenuScreen(GoosePatrolGame game) {
        super(game);
    }

    /**
     *
     * @param delta time since last frames
     */
    @Override
    protected void update(float delta) {
        switch (menu.update(mouseX, mouseY)) {
            case 0:
                game.setScreen(new GameScreen(game));
                break;
            case 1:
                game.setScreen(new TutorialScreen(game, this));
                break;
            case 2:
                Gdx.app.exit();
                break;
            default:
                break;
        }

        // keep the old shortcut
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            game.setScreen(new TutorialScreen(game, this));
        }
    }

    /**
     * draws text and images
     */
    @Override
    protected void draw() {
        background(".png", 0f);

        textCentered(game.titleFont, "GOOSEAGEDDON", W / 2f, 560f, GOLD);
        textCentered(game.uiFont, "DEFEAT THE FLOCK. BEAT THE CLOCK. CATCH THE BUS!",
            W / 2f, 485f, Color.WHITE);

        image(game.texture("ui/menu_student.png"), 90f, 220f, 120f, 150f);
        image(game.texture("ui/menu_geese.png"), 590f, 150f, 150f, 180f);

        menu.draw(this, true);

        text(game.uiFont, "TEAM 8 - COHORT 4", 30f, 50f, Color.WHITE);
        textRight(game.smallFont, "ENTER   SELECT OPTION", W - 30f, 85f, Color.WHITE);
        textRight(game.smallFont, "UP/DOWN   NAVIGATE MENU", W - 30f, 55f, Color.WHITE);
    }
}
