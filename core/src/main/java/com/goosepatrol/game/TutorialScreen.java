
package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class TutorialScreen extends ScreenAdapter {

    private static final float WIDTH = 1448f;
    private static final float HEIGHT = 1086f;

    private final GoosePatrolGame game;
    private final SpriteBatch batch;
    private final FitViewport viewport;
    private final Texture background;

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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
    }
}
