package com.goosepatrol.game;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * extending game gives access to setScreen()
 *
 * setScreen() allows us to swao between diff screen objects
 */
public class GoosePatrolGame extends Game
{
    // used by every screen
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f); // enlarge text
        setScreen(new MenuScreen(this)); //this passes game to screen so screen can reach methods and vars + first screen

    }

    /**
     * runs once application closes - game doesn't dispose current screen for us so is done here
     * manually release batch and font too
     */
    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }

        batch.dispose();
        font.dispose();
    }


}
