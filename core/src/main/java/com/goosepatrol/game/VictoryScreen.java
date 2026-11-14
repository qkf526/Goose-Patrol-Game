
package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class VictoryScreen extends ScreenAdapter {

    // Dimensions of the Victory artwork.
    private static final float WIDTH = 1448f;
    private static final float HEIGHT = 1086f;

    private static final int OPTION_COUNT = 2;

    // Gold selection arrow.
    private static final float ARROW_X = 391f;
    private static final float ARROW_SIZE = 50f;

    // Button centres: Play Again, Main Menu.
    private static final float[] BUTTON_Y = {
        388f,
        270f
    };

    // Right edge of our statistics values.
    private static final float RESULTS_RIGHT_X = 1040f;

    private final GoosePatrolGame game;

    private final SpriteBatch batch;
    private final FitViewport viewport;

    private final Texture background;
    private final Texture selectionArrow;

    // Used to draw and measure our statistics.
    private final BitmapFont font;
    private final GlyphLayout layout;

    // Results captured when the player won.
    private final int secondsRemaining;
    private final int energyPercentage;
    private final int itemsUsed;
    private final int eventsTriggered;

    // 0 = Play Again, 1 = Main Menu.
    private int selectedOption = 0;

    public VictoryScreen(
        GoosePatrolGame game,
        int secondsRemaining,
        int energyPercentage,
        int itemsUsed,
        int eventsTriggered
    ) {

        this.game = game;

        // Store the final statistics.
        this.secondsRemaining = secondsRemaining;
        this.energyPercentage = energyPercentage;
        this.itemsUsed = itemsUsed;
        this.eventsTriggered = eventsTriggered;

        // Create our graphics objects.
        batch = new SpriteBatch();

        viewport = new FitViewport(
            WIDTH,
            HEIGHT
        );

        // Load the Victory artwork.
        background = new Texture(
            Gdx.files.internal("menu/victory.png")
        );

        background.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        // Reuse our existing menu arrow.
        selectionArrow = MenuArrow.create();

        // Font for dynamic statistics.
        font = new BitmapFont();
        font.getData().setScale(3.7f);
        font.setColor(Color.WHITE);

        // Measures text dimensions.
        layout = new GlyphLayout();
    }


    private void drawResult(
        String text,
        float y
    ) {

        // Measure the text's width.
        layout.setText(font, text);

        // Right-align its final position.
        float x = RESULTS_RIGHT_X - layout.width;

        // Draw a dark shadow.
        font.setColor(Color.BLACK);

        font.draw(
            batch,
            text,
            x + 2f,
            y - 2f
        );

        // Draw the actual white text.
        font.setColor(Color.WHITE);

        font.draw(
            batch,
            text,
            x,
            y
        );
    }


    private void drawStatistics() {

        // Convert seconds to MM:SS format.
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;

        String timeText = String.format(
            "%d:%02d",
            minutes,
            seconds
        );

        String energyText = energyPercentage + "%";

        String itemsText = String.valueOf(itemsUsed);

        String eventsText = String.valueOf(eventsTriggered);

        // Place the values beside the artwork's labels.
        drawResult(timeText, 825f);
        drawResult(energyText, 750f);
        drawResult(itemsText, 666f);
        drawResult(eventsText, 585f);
    }


    private boolean handleInput() {

        // Move the arrow upwards.
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {

            selectedOption =
                (selectedOption - 1 + OPTION_COUNT)
                % OPTION_COUNT;
            game.getSounds().playMenuMove();
        }

        // Move the arrow downwards.
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
                    // Play Again: start a new match.
                    game.startNewGame();
                    return true;

                case 1:
                    // Return to Main Menu.
                    game.endGameToMenu();
                    return true;
            }
        }

        return false;
    }


    @Override
    public void show() {

        // Play Again is selected by default.
        selectedOption = 0;
    }

    @Override
    public void render(float delta) {

        if (handleInput()) {
            return;
        }

        // Clear the window.
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Apply the viewport.
        viewport.apply();

        batch.setProjectionMatrix(
            viewport.getCamera().combined
        );

        batch.begin();

        // Draw the original Victory artwork.
        batch.draw(
            background,
            0f,
            0f,
            WIDTH,
            HEIGHT
        );

        // Draw the final match statistics.
        drawStatistics();

        // Move the arrow to the selected button.
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

    @Override
    public void resize(int width, int height) {

        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {

        batch.dispose();
        background.dispose();
        selectionArrow.dispose();
        font.dispose();
    }


}
