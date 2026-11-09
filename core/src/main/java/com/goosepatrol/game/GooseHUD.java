package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/** Wave/timer/status overlay, kept separate from the Player's heart HUD. */
public class GooseHUD implements Disposable {
    private final ScreenViewport viewport = new ScreenViewport();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final ShapeRenderer shapes = new ShapeRenderer();

    public GooseHUD() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void draw(GooseWaveManager waves) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapes.setProjectionMatrix(viewport.getCamera().combined);

        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();
        float scale = Math.max(0.8f, Math.min(2f,
            Math.min(width / 800f, height / 480f)));
        float panelWidth = 245f * scale;
        float panelHeight = 91f * scale;
        float panelX = width - panelWidth - 14f * scale;
        float panelY = height - panelHeight - 14f * scale;

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.07f, 0.11f, 0.16f, 1f);
        shapes.rect(panelX, panelY, panelWidth, panelHeight);
        shapes.end();

        int seconds = waves.getSecondsRemaining();
        String time = String.format("%d:%02d", seconds / 60, seconds % 60);
        font.getData().setScale(1.12f * scale);
        font.setColor(Color.WHITE);
        batch.begin();
        float x = panelX + 12f * scale;
        float top = panelY + panelHeight - 10f * scale;
        font.draw(batch, "TIME  " + time, x, top);
        font.draw(batch, "WAVE  " + waves.getCurrentWave() + "/" + GooseWaveManager.TOTAL_WAVES,
            x, top - 26f * scale);
        font.draw(batch, "GEESE  " + waves.getActiveGeese() + "  |  OUT  " +
            waves.getDispersedCount() + "/" + GooseWaveManager.TOTAL_GEESE,
            x, top - 52f * scale);
        batch.end();

        if (waves.getState() != GooseWaveManager.GameState.PLAYING) {
            drawMessage(waves, width, height, scale);
        }
    }

    private void drawMessage(GooseWaveManager waves, float width, float height, float scale) {
        float panelW = Math.min(width - 24f, 405f * scale);
        float panelH = 94f * scale;
        float x = (width - panelW) / 2f;
        float y = (height - panelH) / 2f;
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.07f, 0.11f, 0.16f, 1f);
        shapes.rect(x, y, panelW, panelH);
        shapes.end();

        String headline;
        String instruction;
        if (waves.getState() == GooseWaveManager.GameState.PAUSED) {
            headline = "GOOSE PATROL";
            instruction = "ENTER: start/resume    ESC: pause";
        } else if (waves.getState() == GooseWaveManager.GameState.WON) {
            headline = "ALL GEESE DISPERSED!";
            instruction = "You made it to the bus!  R: restart";
        } else {
            headline = "GAME OVER";
            instruction = "Out of health or time.  R: restart";
        }
        font.getData().setScale(1.3f * scale);
        font.setColor(Color.WHITE);
        batch.begin();
        font.draw(batch, headline, x + 14f * scale, y + 65f * scale);
        font.getData().setScale(0.95f * scale);
        font.draw(batch, instruction, x + 14f * scale, y + 29f * scale);
        batch.end();
    }

    @Override
    public void dispose() {
        shapes.dispose();
        font.dispose();
        batch.dispose();
    }
}
