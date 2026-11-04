package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class ControlsScreen implements Screen {
    private final GoosePatrolGame game;
    private final Screen previous;
    private final ShapeRenderer shape;
    private final Button backButton;

    public ControlsScreen(GoosePatrolGame game, Screen previous)
    {
        this.game = game;
        this.previous = previous;
        this.shape = new ShapeRenderer();

        float w = 300;
        float h = 60;
        float x = (Gdx.graphics.getWidth() - w) / 2f;
        backButton = new Button("Back", x, 80, w, h);
    }

    @Override
    public void render(float delta)
    {
        ScreenUtils.clear(0.1f, 0.15f, 0.1f, 1f);

        float mousex = Gdx.input.getX();
        float mousey = Gdx.graphics.getHeight() - Gdx.input.getY();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        backButton.drawShape(shape, backButton.contains(mousex, mousey));
        shape.end();

        game.batch.begin();
        game.font.draw(game.batch, "CONTROLS", 100, 440);
        game.font.draw(game.batch, "MOVEMENT - WASD", 100, 380);
        game.font.draw(game.batch, "ESC - pause / back", 100, 330);
        backButton.drawText(game.batch, game.font);
        game.batch.end();

        if (Gdx.input.justTouched() && backButton.contains(mousex, mousey))
        {
            dispose();
            game.setScreen(previous);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            dispose();
            game.setScreen(previous);
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
        shape.dispose();
    }
}




