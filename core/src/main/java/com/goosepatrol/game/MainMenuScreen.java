
package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;


/**
 * main menu
 * <p>
 * class only draws image and moves gold arrow beside highligted opton
 * options: play, tutorial, quit
 * keys: up/down, enter
 * <p>
 * screen doesnt change screens itself.
 * asks {@link GoosePatrolGame} to start new match, open tutorial, and plays menu sounds
 * quit closes application directly
 * <p>
 * all positions are in co ordinates ({@code MENU_WIDTH} by {@code MENU_HEIGHT},
 * origin bottom left)
 */
public class MainMenuScreen extends ScreenAdapter {

    // Dimensions of main menu artwork.
    private static final float MENU_WIDTH = 1448f;
    private static final float MENU_HEIGHT = 1086f;

    // There are three selectable buttons.
    private static final int OPTION_COUNT = 3;

    // The arrow's size and horizontal position.
    private static final float ARROW_SIZE = 50f;
    private static final float ARROW_X = 412f;

    // Vertical centres of Play, Tutorial and Quit.
    private static final float[] BUTTON_Y = {
        565f,
        440f,
        317f
    };

    private final GoosePatrolGame game;

    private final SpriteBatch batch;
    private final FitViewport viewport;

    private final Texture background;
    private final Texture selectionArrow;

    // 0 = Play, 1 = Tutorial, 2 = Quit.
    private int selectedOption = 0;

    /**
     * creates main menu: has its own batch and viewport, the artwork and the
     * selection arrow.
     *
     * @param game the game, used to start a match, open the tutorial and play
     *             menu sounds
     */
    public MainMenuScreen(GoosePatrolGame game) {

        this.game = game;

        // Our menu has its own SpriteBatch.
        batch = new SpriteBatch();

        // Maintain the design's 4:3 aspect ratio.
        viewport = new FitViewport(
            MENU_WIDTH,
            MENU_HEIGHT
        );

        // Load the background designed.
        background = new Texture(
            Gdx.files.internal("menu/main_menu.png")
        );

        // Keep pixel-art edges sharp.
        background.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        // Create movable selection arrow.
        selectionArrow = MenuArrow.create();
    }

    /**
     * reads the keyboard frame. up/down move highlight
     * (wrapping at ends)
     * <p>
     * enter activates the highlighted option:
     * play starts new match, tutorial opens tutorial and quit closes
     * application.
     *
     * @return true if an option was activated, meaning screen is
     *         changing and this frame shouldnt be drawn; false otherwise
     */
    private boolean handleInput() {

        // Move selection one button upwards.
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {

            selectedOption =
                (selectedOption - 1 + OPTION_COUNT)
                % OPTION_COUNT;
            game.getSounds().playMenuMove();
        }

        // Move selection one button downwards.
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {

            selectedOption =
                (selectedOption + 1)
                % OPTION_COUNT;
            game.getSounds().playMenuMove();
        }

        // Activate the currently selected button.
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(
                Input.Keys.NUMPAD_ENTER)) {
            game.getSounds().playMenuSelect();
            switch (selectedOption) {

                case 0:
                    // PLAY: start a new match.
                    game.startNewGame();
                    return true;

                case 1:
                    game.openTutorial();
                    return true;

                case 2:
                    // QUIT: close the application.
                    Gdx.app.exit();
                    return true;
            }
        }

        return false;
    }


    /**
     * called when screen becomes current. resets highlight to play,
     * so menu opens on same option
     */
    @Override
    public void show() {

        // Select Play whenever the menu opens.
        selectedOption = 0;
    }

    /**
     * runs one frame: handes input, draws artwork and arrow beside highlighter option
     * nothing is drawn if input is switched to another screen
     *
     * @param delta time in seconds since the last render.
     */
    @Override
    public void render(float delta) {

        // Handle keyboard controls first.
        if (handleInput()) {
            return;
        }

        // Clear the window to black.
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Apply our menu's viewport.
        viewport.apply();

        batch.setProjectionMatrix(
            viewport.getCamera().combined
        );

        batch.begin();

        // Draw the complete Main Menu background.
        batch.draw(
            background,
            0f,
            0f,
            MENU_WIDTH,
            MENU_HEIGHT
        );

        // Calculate the selected button's centre.
        float centreY = BUTTON_Y[selectedOption];

        // Draw the arrow beside the selected button.
        batch.draw(
            selectionArrow,
            ARROW_X,
            centreY - ARROW_SIZE / 2f,
            ARROW_SIZE,
            ARROW_SIZE
        );

        batch.end();
    }

    /**
     * called when window changes size. rescales artwork
     *
     * @param width new window width
     * @param height new window height
     */
    @Override
    public void resize(int width, int height) {

        viewport.update(width, height, true);
    }

    /**
     * frees batch, artwork and arrow (this screen owns)
     */
    @Override
    public void dispose() {

        batch.dispose();
        background.dispose();
        selectionArrow.dispose();
    }



}
