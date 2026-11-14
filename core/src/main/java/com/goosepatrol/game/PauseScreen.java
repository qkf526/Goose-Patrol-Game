
package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PauseScreen extends ScreenAdapter {

    private static final float WIDTH = 1448f;
    private static final float HEIGHT = 1086f;

    private static final int OPTION_COUNT = 4;

    // Arrow position and size.
    private static final float ARROW_X = 14f;
    private static final float ARROW_SIZE = 45f;

    // Button centre positions in the artwork.
    private static final float[] BUTTON_Y = {
        768f, // Resume
        646f, // Restart
        519f, // Tutorial
        392f  // End Game
    };

    private final GoosePatrolGame game;
    private final SpriteBatch batch;
    private final FitViewport viewport;

    private final Texture background;
    private final Texture selectionArrow;

    private int selectedOption = 0;

    public PauseScreen(GoosePatrolGame game) {

        this.game = game;

        batch = new SpriteBatch();

        viewport = new FitViewport(
            WIDTH, HEIGHT
        );

        background = new Texture(
            Gdx.files.internal("menu/pause_menu.png")
        );

        background.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        selectionArrow = MenuArrow.create();
    }


    @Override
    public void show() {

        // Default to Resume when opening Pause.
        selectedOption = 0;
    }

    @Override
    public void render(float delta) {

        if (handleInput()) {
            return;
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        batch.setProjectionMatrix(
            viewport.getCamera().combined
        );

        batch.begin();

        // Draw the complete static Pause Menu image.
        batch.draw(
            background,
            0f,
            0f,
            WIDTH,
            HEIGHT
        );

        // Move arrow to the selected button.
        float centreY = BUTTON_Y[selectedOption];

        batch.draw(
            selectionArrow,
            ARROW_X,
            centreY - ARROW_SIZE / 2f,
            ARROW_SIZE,
            ARROW_SIZE
        );

        batch.end();
    }

    private boolean handleInput() {

        // ESC immediately resumes the game.
        if (Gdx.input.isKeyJustPressed(
            Input.Keys.ESCAPE)) {
            game.getSounds().playMenuSelect();
            game.resumeGame();
            return true;
        }

        // Move the selection upwards.
        if (Gdx.input.isKeyJustPressed(
            Input.Keys.UP)) {

            selectedOption =
                (selectedOption - 1 + OPTION_COUNT)
                % OPTION_COUNT;
            game.getSounds().playMenuMove();
        }

        // Move the selection downwards.
        if (Gdx.input.isKeyJustPressed(
            Input.Keys.DOWN)) {

            selectedOption =
                (selectedOption + 1)
                % OPTION_COUNT;
            game.getSounds().playMenuMove();
        }

        // Select the highlighted option.
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(
                Input.Keys.NUMPAD_ENTER)) {

            game.getSounds().playMenuSelect();
            switch (selectedOption) {

                case 0:
                    // Resume existing gameplay.
                    game.resumeGame();
                    return true;

                case 1:
                    // Restart the entire match.
                    game.startNewGame();
                    return true;

                case 2:
                    game.openTutorial();
                    return true;

                case 3:
                    // End the match and return to menu.
                    game.endGameToMenu();
                    return true;
            }
        }

        return false;
    }


    @Override
    public void resize(int width, int height) {

        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {

        batch.dispose();
        background.dispose();
        selectionArrow.dispose();
    }

}
