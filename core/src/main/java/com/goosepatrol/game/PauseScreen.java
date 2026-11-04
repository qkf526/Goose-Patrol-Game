package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class PauseScreen implements Screen {
    private final GoosePatrolGame game;
    private final Screen previous;
    private final ShapeRenderer shape;
    private final Button resumeButton;      // returns paused game to exactly where it was
    private final Button restartButton;     // restarts paused game, and takes to main menu
    private final Button quitButton;        // closes application
    private final Button controlsButton;    // opens controls

    public PauseScreen(GoosePatrolGame game, Screen previous) {
        this.game = game;
        this.previous = previous;
        this.shape = new ShapeRenderer();

        // centre buttons horizontally
        float w = 300;
        float h = 100;
        float x = (Gdx.graphics.getWidth() - w) / 2f;
        resumeButton = new Button("Resume", x, 340, w, h);
        controlsButton = new Button("Controls", x, 260, w, h);
        restartButton = new Button("Main Menu", x, 180, w, h);
        quitButton = new Button("Quit", x, 100, w, h);

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        float mousex = Gdx.input.getX();
        float mousey = Gdx.graphics.getHeight() - Gdx.input.getY();


        // shapes
        shape.begin(ShapeRenderer.ShapeType.Filled);
        resumeButton.drawShape(shape, resumeButton.contains(mousex, mousey));
        restartButton.drawShape(shape, restartButton.contains(mousex, mousey));
        quitButton.drawShape(shape, quitButton.contains(mousex, mousey));
        controlsButton.drawShape(shape, controlsButton.contains(mousex, mousey));
        shape.end();

        // text
        game.batch.begin();
        game.font.draw(game.batch, "PAUSED", 100, 400);
        resumeButton.drawText(game.batch, game.font);
        restartButton.drawText(game.batch, game.font);
        quitButton.drawText(game.batch, game.font);
        controlsButton.drawText(game.batch, game.font);
        game.batch.end();

        if (Gdx.input.justTouched()) {
            // back to same game
            if (resumeButton.contains(mousex, mousey) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                dispose();
                game.setScreen(previous);
            }
            // restart game
            else if (restartButton.contains(mousex, mousey)) {
                previous.dispose();
                dispose();
                game.setScreen(new MenuScreen(game));
            } else if (quitButton.contains(mousex, mousey)) {
                Gdx.app.exit();
            } else if (controlsButton.contains(mousex, mousey)) {
                game.setScreen(new ControlsScreen(game, this));

            }
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

    @Override
    public void hide()
    {
    }

    @Override
    public void dispose()
    {
        shape.dispose();
    }
}
