
package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

public class SoundManager implements Disposable {

    private Sound gooseSound;
    private Sound gameOverSound;

    // Load sound effects
    public SoundManager() {
        gooseSound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/goose.mp3")
        );
    
        gameOverSound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/gameover.wav")
        );
    }

    // Play the goose honk
    public void playGoose() {
        gooseSound.play(0.5f);

    }
    //play the gameover sound
    public void playGameOver(){
        gameOverSound.play(0.6f);
    }

    // Release audio resources
    @Override
    public void dispose() {
        gooseSound.dispose();
        gameOverSound.dispose();
    }
}
