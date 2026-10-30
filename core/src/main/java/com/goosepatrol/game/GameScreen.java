package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    private final GoosePatrolGame game;

    /**
     * wtv is in create method goes here
     */
    public GameScreen(GoosePatrolGame game)
    {
        this.game = game;
    }

    /**
     * splits each frame into update and draw (logic and graphics) to keep code organised
     */
    @Override
    public void render(float delta)
    {
        update(delta);
        draw();
    }


    /**
     * game logic - nothing is drawn
     *
     * later add: movement + collisions + input handling
     * @param delta
     */
    private void update(float delta)
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
        {
            //current standin for losing game
            // if click space - game over
            //condition will change
            game.setScreen(new GameOverScreen(game));
        }
    }

    /**
     * drawing only
     */
    private void draw()
    {
        ScreenUtils.clear(0f,0f,0f,1f); // black bg7

        game.batch.begin();
        game.font.draw(game.batch, "GAME (press SPACE for game over", 100, 300);
        game.batch.end();
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


    /**
     * new game screen each time so old one is abandoned when we leave
     * disposing stops resources leakingh
     * safe rn bc dispose is empty
     * will matter when screen owns tectures or renderers
     */
    @Override
    public void hide()
    {
        dispose();
    }

    /**
     * free everything this screen created
     * textures + sounds + ShapeRenderers
     * do not free game.batch or game.font - belong to GoosePatrolGame
     */
    @Override
    public void dispose()
    {
    }
}
