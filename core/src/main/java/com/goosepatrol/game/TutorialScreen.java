
package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * whole screen is one image, drawn and waits for player ot leave
 */
public class TutorialScreen extends ScreenAdapter {

    private static final float WIDTH = 1448f;
    private static final float HEIGHT = 1086f;

    private final GoosePatrolGame game;
    private final SpriteBatch batch;
    private final FitViewport viewport;
    private final Texture background;

    /**
     * creates tutorial screen: its own batch and viewport, and artwork
     *
     * @param game game, used to return to previous screen and play menu sound
     */
    public TutorialScreen(GoosePatrolGame game) {

        this.game = game;

        batch = new SpriteBatch();

        viewport = new FitViewport(WIDTH, HEIGHT);

        background = new Texture(
            Gdx.files.internal("menu/tutorial.png")
        );

        background.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );
    }

    /**
     * allows return to previous screen
     *
     * @param delta time in seconds since the last render.
     */
    @Override
    public void render(float delta) {

        // Enter or Escape returns to the previous screen.
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(
                Input.Keys.NUMPAD_ENTER)) {

            game.backFromTutorial();
            game.getSounds().playMenuSelect();
            return;
        }

        // Clear the screen.
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        batch.setProjectionMatrix(
            viewport.getCamera().combined
        );

        batch.begin();

        batch.draw(
            background,
            0f,
            0f,
            WIDTH,
            HEIGHT
        );

        batch.end();
    }

    /**
     * called when window chanegs size, rescales image to fit
     * @param width new width
     * @param height new height
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * frees batch and artwork
     */
    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
    }
}
