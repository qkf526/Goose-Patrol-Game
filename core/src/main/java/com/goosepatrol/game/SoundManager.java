package com.goosepatrol.game;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

/**
 * Shared audio service for Goose Patrol.
 */
public class SoundManager implements Disposable {

    private final Map<String, Sound> effects = new HashMap<>();
    private final Music backgroundMusic;
    private final Music footsteps;

    public SoundManager() {
        // The team's existing recordings.
        load("goose1",      "goose.mp3");
        load("goose2",      "goose2.wav");
        // goose3.mp3 is 14 seconds; use a short excerpt to avoid overlap.
        load("goose_flock", "goose_flock_short.wav");
        load("game_over",   "gameover.wav");
        load("air_horn",    "airhorn_fixed.wav");
        load("seeds",       "seeds.wav");
        load("bandage",     "bandage.wav");
        load("hurt",        "hurt.mp3");
        load("equip",       "equip.mp3");
        load("victory",     "victory.wav");
        load("menu_click",  "ui click.ogg");

        // Prototype replacements for recordings not supplied by the team.
        load("sausage_roll",   "sausage_roll.wav");
        load("statue_honk",    "statue_honk.wav");
        load("door",           "door.wav");
        load("event_complete", "event_complete.wav");
        load("wave_start",     "wave_start.wav");
        load("countdown_tick", "countdown_tick.wav");
        load("time_up",        "time_up.wav");

        // Music streams longer recordings rather than loading them as effects.
        backgroundMusic = Gdx.audio.newMusic(file("roa-music-walk-around(chosic.com).mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.15f);

        footsteps = Gdx.audio.newMusic(file("footsteps.mp3"));
        footsteps.setLooping(true);
        footsteps.setVolume(0.15f);
    }

    /** Resolve internal sound path and report a clear error if installation is incomplete. */
    private FileHandle file(String filename) {
        FileHandle handle = Gdx.files.internal("sounds/" + filename);
        if (!handle.exists()) {
            throw new IllegalStateException("Missing Goose Patrol audio: assets/sounds/" + filename);
        }
        return handle;
    }

    private void load(String key, String filename) {
        effects.put(key, Gdx.audio.newSound(file(filename)));
    }

    private void play(String key, float volume) {
        Sound effect = effects.get(key);
        if (effect != null) effect.play(volume);
    }

    /** Start BGM only when it is not already playing. */
    public void startBackgroundMusic() {
        if (!backgroundMusic.isPlaying()) backgroundMusic.play();
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic.isPlaying()) backgroundMusic.stop();
    }

    /** Start/stop looping footsteps; call stopFootsteps() in GameplayScreen.hide(). */
    public void playFootsteps() {
        if (!footsteps.isPlaying()) footsteps.play();
    }

    public void stopFootsteps() {
        if (footsteps.isPlaying()) footsteps.stop();
    }

    /** Play a single-goose or shorter flock call based on how many are nearby. */
    public void playGoose(int nearbyGeese) {
        if (nearbyGeese <= 0) return;

        String key;
        if (nearbyGeese >= 4 && MathUtils.randomBoolean(0.7f)) {
            key = "goose_flock";
        } else if (nearbyGeese >= 2 && MathUtils.randomBoolean(0.2f)) {
            key = "goose_flock";
        } else {
            key = MathUtils.randomBoolean() ? "goose1" : "goose2";
        }

        Sound sound = effects.get(key);
        if (sound != null) {
            float volume = key.equals("goose_flock") ? 0.17f : 0.25f;
            sound.play(volume, MathUtils.random(0.9f, 1.1f), 0f);
        }
    }
    //Methods to play game sounds
    public void playAirHorn()       { play("air_horn", 0.32f); }
    public void playSeeds()         { play("seeds", 0.25f); }
    public void playBandage()       { play("bandage", 0.30f); }
    public void playSausageRoll()   { play("sausage_roll", 0.34f); }
    public void playHurt()          { play("hurt", 0.28f); }
    public void playEquip()         { play("equip", 0.25f); }
    public void playGameOver()      { play("game_over", 0.60f); }
    public void playTimeUp()        { play("time_up", 0.50f); }
    public void playVictory()       { play("victory", 0.40f); }
    public void playMenuMove()      { play("menu_click", 0.20f); }
    public void playMenuSelect()    { play("menu_click", 0.30f); }
    public void playClick()         { playMenuSelect(); } // Compatibility with older branch code.
    public void playDoor()          { play("door", 0.35f); }
    public void playStatueHonk()    { play("statue_honk", 0.50f); }
    public void playEventComplete() { play("event_complete", 0.42f); }
    public void playWaveStart()     { play("wave_start", 0.40f); }
    public void playCountdownTick() { play("countdown_tick", 0.30f); }

    @Override
    public void dispose() {
        stopFootsteps();
        stopBackgroundMusic();
        footsteps.dispose();
        backgroundMusic.dispose();
        for (Sound effect : effects.values()) effect.dispose();
        effects.clear();
    }
}
