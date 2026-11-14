
package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameOverScreen extends ScreenAdapter {

    /**
     * reason match was lost, decides what artwork is shown
     */
    public enum Reason {
        OUT_OF_ENERGY,
        TIME_UP
    }

    private static final float WIDTH = 1448f;
    private static final float HEIGHT = 1086f;

    private static final int OPTION_COUNT = 2;

    // Arrow position beside the buttons.
    private static final float ARROW_X = 435f;
    private static final float ARROW_SIZE = 50f;

    /**
     * centre of each button in artwork (vertical)
      */
    private static final float[] BUTTON_Y = {
        486f, // Try Again
        384f  // Main Menu
    };

    private final GoosePatrolGame game;

    private final SpriteBatch batch;
    private final FitViewport viewport;

    private final Texture background;
    private final Texture selectionArrow;

    private int selectedOption = 0;


    /**
     * creates game over screen and loads artwork that matches why match was lost
     * @param game
     * @param reason
     */
    public GameOverScreen(
        GoosePatrolGame game,
        Reason reason
    ) {

        this.game = game;

        batch = new SpriteBatch();

        viewport = new FitViewport(
            WIDTH,
            HEIGHT
        );

        // Choose the correct artwork.
        String imagePath;

        if (reason == Reason.OUT_OF_ENERGY) {
            imagePath = "menu/game_over_energy.png";
        } else {
            imagePath = "menu/game_over_time.png";
        }

        background = new Texture(
            Gdx.files.internal(imagePath)
        );

        background.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        // Reuse the arrow from MenuArrow class.
        selectionArrow = MenuArrow.create();
    }


    /**
     * reads keybpard for frame. esc resumes, up/down move highlight and enter activates it:
     * resume returns to match, restart restarts match, tutoial opens tutorial screen and end game abandons match
     *
     * @return true if input leaving screen, meaning frame should not be drawn, false otherwise
     */
    private boolean handleInput() {

        // Move selection upwards.
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {

            selectedOption =
                (selectedOption - 1 + OPTION_COUNT)
                % OPTION_COUNT;
            game.getSounds().playMenuMove();
        }

        // Move selection downwards.
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {

            selectedOption =
                (selectedOption + 1)
                % OPTION_COUNT;
            game.getSounds().playMenuMove();
        }

        // Activate the selected button.
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(
                Input.Keys.NUMPAD_ENTER)) {

            game.getSounds().playMenuSelect();
            switch (selectedOption) {

                case 0:
                    // Try Again: create a fresh match.
                    game.startNewGame();
                    return true;

                case 1:
                    // Return to the Main Menu.
                    game.endGameToMenu();
                    return true;
            }
        }

        return false;
    }


    /**
     * called when screen becomes current
     */
    @Override
    public void show() {

        // Select Try Again by default.
        selectedOption = 0;
    }

    /**
     * runs 1 frame, handles input, draws artwork and arrow.
     * nothing drawn if input swtched to another screen
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {

        // Read keyboard input.
        // Stop rendering if we change screens.
        if (handleInput()) {
            return;
        }

        // Clear the window.
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Apply our 4:3 viewport.
        viewport.apply();

        batch.setProjectionMatrix(
            viewport.getCamera().combined
        );

        batch.begin();

        // Draw the selected Game Over artwork.
        batch.draw(
            background,
            0f,
            0f,
            WIDTH,
            HEIGHT
        );

        // Find the selected button's vertical centre.
        float centreY = BUTTON_Y[selectedOption];

        // Draw the movable arrow.
        batch.draw(
            selectionArrow,
            ARROW_X,
            centreY - ARROW_SIZE / 2f,
            ARROW_SIZE,
            ARROW_SIZE
        );

        batch.end();
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
