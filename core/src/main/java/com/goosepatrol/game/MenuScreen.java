package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * implements screen promises to include all methods listed in screen interface
 * can be empty!!
 */
public class MenuScreen implements Screen {
    private final GoosePatrolGame game; // final means reference never changes after constructor

    /**
     * no create method -> constructor(game) does it
     * @param game
     */
    public MenuScreen(GoosePatrolGame game)
    {
        this.game = game;
    }

    /**
     * called every frame while screen is active
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta)
    {
        // wipes previous screen to prevemt smearing
        ScreenUtils.clear(1f, 0.33f,0.78f,1f); // pink!!


        // creates on screen text
        game.batch.begin();
        game.font.draw(game.batch, "MAIN MENU", 100, 300);
        game.font.draw(game.batch, "Click to Start", 100, 240);
        game.batch.end();

        // justTouched() true for only one frame - action happens once not every frame
        if (Gdx.input.justTouched())
        {
            game.setScreen(new GameScreen(game)); // makes new game screen so every play starts new
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
