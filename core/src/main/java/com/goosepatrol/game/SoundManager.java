
package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

public class SoundManager implements Disposable {

    // Store three different goose sounds
    private Sound[] gooseSounds;
    private Sound gameOverSound;
    private Sound airHornSound;
    private Sound seedsSound;
    private Sound bandageSound;
    private Sound hurtSound;
    private Music footstepsMusic;
    private Sound equipSound;
    private Sound victorySound;

    // Load sound effects
    public SoundManager() {

        gooseSounds = new Sound[3];

        gooseSounds[0] = Gdx.audio.newSound(
            Gdx.files.internal("sounds/goose.mp3")
        );

        gooseSounds[1] = Gdx.audio.newSound(
            Gdx.files.internal("sounds/goose2.wav")
        );

        gooseSounds[2] = Gdx.audio.newSound(
            Gdx.files.internal("sounds/goose3.mp3")
        );

        gameOverSound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/gameover.wav")
        );
        airHornSound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/airhorn_fixed.wav")
        );
        seedsSound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/seeds.wav")
        );
        bandageSound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/bandage.wav")
        );
        hurtSound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/hurt.mp3")
        );
        footstepsMusic = Gdx.audio.newMusic(
            Gdx.files.internal("sounds/footsteps.mp3")
        );

        footstepsMusic.setLooping(true);
        footstepsMusic.setVolume(0.15f);

        equipSound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/equip.mp3")
        );
        victorySound = Gdx.audio.newSound(
            Gdx.files.internal("sounds/victory.wav")
        );


    }

    
public void playGoose(int nearbyGeese) {

    int soundIndex;

    if (nearbyGeese == 1) {
        // One goose: only play individual honks
        soundIndex = MathUtils.random(0, 1);

    } else if (nearbyGeese <= 3) {
        // Small group: 20% chance of flock sound
        if (MathUtils.randomBoolean(0.2f)) {
            soundIndex = 2;
        } else {
            soundIndex = MathUtils.random(0, 1);
        }

    } else {
        // Large group: 70% chance of flock sound
        if (MathUtils.randomBoolean(0.7f)) {
            soundIndex = 2;
        } else {
            soundIndex = MathUtils.random(0, 1);
        }
    }
    System.out.println(
        "Nearby geese: " + nearbyGeese
        + " | Sound index: " + soundIndex
    );

    Sound selectedSound = gooseSounds[soundIndex];

    float randomPitch = MathUtils.random(0.9f, 1.1f);

    float volume = (soundIndex == 2) ? 0.15f : 0.25f;

    selectedSound.play(volume, randomPitch, 0f);
    }


    // Play the game over sound
    public void playGameOver() {
        gameOverSound.play(0.6f);
    }

    public void playAirHorn(){
        airHornSound.play(0.3f);
    }
    public void playSeeds() {
        seedsSound.play(0.2f);
    }
    public void playBandage() {
        bandageSound.play(0.25f);
    }
    public void playHurt() {
        hurtSound.play(0.25f);
    }

    public void playFootsteps() {
        if (!footstepsMusic.isPlaying()) {
            footstepsMusic.play();
        }
    }

    public void stopFootsteps() {
        if (footstepsMusic.isPlaying()) {
            footstepsMusic.stop();
        }
    }
    public void playEquip() {
        equipSound.play(0.25f);
    }
    public void playVictory() {
        victorySound.play(0.35f);
    }




    // Release audio resources
    @Override
    public void dispose() {

        for (Sound sound : gooseSounds) {
            sound.dispose();
        }

        gameOverSound.dispose();
        airHornSound.dispose();
        seedsSound.dispose();
        bandageSound.dispose();
        hurtSound.dispose();
        footstepsMusic.dispose();
        equipSound.dispose();
        victorySound.dispose();
    }
}
