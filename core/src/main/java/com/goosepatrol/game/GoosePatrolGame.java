package com.goosepatrol.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Screen manager. Owns only the resources every screen shares.
 * All gameplay lives in GameScreen.
 *
 * render() and resize() are deliberately NOT overridden: Game's own versions
 * forward them to whichever screen is current.
 */
public class GoosePatrolGame extends Game {

    // Shared by every screen. Screens borrow these and must never dispose them.
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);

        setScreen(new MenuScreen(this));   // first screen the player sees
    }

    @Override
    public void dispose() {
        // Game does not dispose the current screen for us.
        if (getScreen() != null) {
            getScreen().dispose();
        }
        batch.dispose();
        font.dispose();
    }
}
