package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

/** Shown when every wave has been cleared before time or health runs out. */
public class VictoryScreen implements Screen {

    private final GoosePatrolGame game;

    public VictoryScreen(GoosePatrolGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.35f, 0.15f, 1f);   // dark green

        game.batch.begin();
        game.font.draw(game.batch, "ALL GEESE DISPERSED!", 100, 320);
        game.font.draw(game.batch, "You made it to the bus!", 100, 260);
        game.font.draw(game.batch, "Click to return to menu", 100, 200);
        game.batch.end();

        if (Gdx.input.justTouched()) {
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override
    public void show()
    {
    }

    @Override
    public void resize(int width, int height)
    {
    }

    @Override
    public void pause()
    {
    }

    @Override
    public void resume()
    {
    }

    @Override
    public void hide()
    {
    }

    @Override
    public void dispose()
    {
    }
}
