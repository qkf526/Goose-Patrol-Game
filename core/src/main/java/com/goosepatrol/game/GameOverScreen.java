package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen implements Screen {
    private final GoosePatrolGame game;

    public GameOverScreen(GoosePatrolGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.4f, 0.05f, 0.05f, 1f);

        game.batch.begin();
        game.font.draw(game.batch, "GAME OVER", 100, 300);
        game.font.draw(game.batch, "Click to return to menu", 100, 240);
        game.batch.end();

        // completes loop - menu to game to game over to menu
        if (Gdx.input.justTouched()) {
            game.setScreen(new MenuScreen(game));
        }
    }

    // all required by implements screen but not needed yet - empty

    /**
     * screen became active
     */
    @Override
    public void show()
    {
    }

    /**
     * window size changed
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height)
    {
    }


    /**
     * app lost focus
     */
    @Override
    public void pause()
    {
    }


    /**
     * app regained focus
     */
    @Override
    public void resume()
    {
    }


    /**
     * another screen replaced current
     */
    @Override
    public void hide()
    {
    }


    /**
     * nothing owned so nothing to free yet
     */
    @Override
    public void dispose()
    {
    }
}

