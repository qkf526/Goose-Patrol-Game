package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

public class BGMSound implements Disposable {
    private Music bgMusic;

    public BGMSound() {
        bgMusic = Gdx.audio.newMusic(
            Gdx.files.internal("sounds/roa-music-walk-around(chosic.com).mp3")
        );
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.3f);
        bgMusic.play();
    }

    @Override
    public void dispose() {
        bgMusic.dispose();
    }
}