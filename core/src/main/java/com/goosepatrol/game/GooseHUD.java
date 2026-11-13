package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

// This class displays the HUD for the timer, wave count and geese number information
public class GooseHUD implements Disposable {
    private final ScreenViewport viewport = new ScreenViewport();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont timerFont = new BitmapFont();
    private final BitmapFont waveFont = new BitmapFont();
    private final ShapeRenderer shapes = new ShapeRenderer();

    public GooseHUD() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    // Method to draw the panels and the timer, and wave information
    public void draw(GooseWaveManager waves) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapes.setProjectionMatrix(viewport.getCamera().combined);

        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();
        float scale = Math.max(0.8f, Math.min(2f,Math.min(width / 800f, height / 480f)));
        float panelWidth = 245f * scale;
        float panelHeight = 70f * scale;
        float panelX = width - panelWidth - 14f * scale;
        float panelY = height - panelHeight - 0.2f * scale;

        float timerPanelWidth = 90f * scale;
        float timerPanelHeight = 40f * scale;
        float timerPanelX = (width - timerPanelWidth) / 2f;
        float timerPanelY = height - timerPanelHeight - 0.2f * scale;

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.07f, 0.11f, 0.16f, 1f);
        shapes.rect(panelX, panelY, panelWidth, panelHeight);
        shapes.setColor(0.07f, 0.11f, 0.16f, 1f);
        shapes.rect(timerPanelX, timerPanelY, timerPanelWidth, timerPanelHeight);
        shapes.end();

        int seconds = waves.getSecondsRemaining();
        String time = String.format("%d:%02d", seconds / 60, seconds % 60);
        timerFont.getData().setScale(2.3f * scale);
        timerFont.setColor(Color.WHITE);
        waveFont.getData().setScale(1.2f * scale);
        waveFont.setColor(Color.WHITE);

        batch.begin();
        float x = panelX + 12f * scale;
        float top = panelY + panelHeight - 10f * scale;

        float timerX = timerPanelX + 14f * scale;
        float timerTop = timerPanelY + timerPanelHeight - 8f * scale;

        timerFont.draw(batch, time, timerX, timerTop);
        waveFont.draw(batch, "WAVE  " + waves.getCurrentWave() + "/" + GooseWaveManager.TOTAL_WAVES, x, top - 5f * scale);
        String statusText;

        if (waves.isBetweenWaves()) {
            statusText = "NEXT WAVE IN " + waves.getNextWaveSeconds() + "s";
        } else {
            statusText = "GEESE " + waves.getActiveGeese() + " | OUT " + waves.getDispersedCount() + "/" + GooseWaveManager.TOTAL_GEESE;
        }

        waveFont.draw( batch, statusText, x, top - 30f * scale);
        batch.end();
    }
    
    // Method that disposes the resources used
    @Override
    public void dispose() {
        shapes.dispose();
        timerFont.dispose();
        waveFont.dispose();
        batch.dispose();
    }
}
