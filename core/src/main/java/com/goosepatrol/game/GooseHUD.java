package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * In-game HUD, drawn in screen space.
 *
 * Top-left, stacked: timer, energy meter, items-used and events counters.
 * Top-right: pause button.
 */
public class GooseHUD implements Disposable {

    /** Number of hidden events in the game (shown as "0/2"). */
    public static final int TOTAL_EVENTS = 2;

    private static final Color PANEL = new Color(0.07f, 0.11f, 0.16f, 1f);
    private static final Color METER_BACK = new Color(0.03f, 0.05f, 0.08f, 1f);
    private static final Color ENERGY_HIGH = new Color(0.25f, 0.80f, 0.30f, 1f);
    private static final Color ENERGY_MID = new Color(0.95f, 0.75f, 0.15f, 1f);
    private static final Color ENERGY_LOW = new Color(0.90f, 0.22f, 0.20f, 1f);

    private final ScreenViewport viewport = new ScreenViewport();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont timerFont = new BitmapFont();
    private final BitmapFont infoFont = new BitmapFont();
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final GlyphLayout layout = new GlyphLayout();
    private final Rectangle pauseBounds = new Rectangle();
    private final Texture clockIcon = loadIfPresent("ui/clock.png");   // optional

    public GooseHUD() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private static Texture loadIfPresent(String path) {
        if (!Gdx.files.internal(path).exists()) return null;
        Texture t = new Texture(Gdx.files.internal(path));
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return t;
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /** True on the frame the pause button is clicked. */
    public boolean isPauseClicked() {
        if (!Gdx.input.justTouched()) return false;
        Vector2 p = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        return pauseBounds.contains(p.x, p.y);
    }

    public void draw(GooseWaveManager waves, int health, int maxHealth,
                     int itemsUsed, int eventsFound) {

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapes.setProjectionMatrix(viewport.getCamera().combined);

        float width = viewport.getWorldWidth();
        float height = viewport.getWorldHeight();
        float scale = Math.max(0.8f, Math.min(2f, Math.min(width / 800f, height / 480f)));

        // ---- layout: three stacked panels in the top-left ----
        float margin = 14f * scale;
        float gap = 6f * scale;
        float panelW = 160f * scale;
        float x = margin;

        float timerH = 40f * scale;
        float timerY = height - margin - timerH;

        float meterH = 24f * scale;
        float meterY = timerY - gap - meterH;

        float countersH = 50f * scale;
        float countersY = meterY - gap - countersH;

        float btn = 44f * scale;
        pauseBounds.set(width - btn - margin, height - btn - margin, btn, btn);

        // ---- energy ----
        float fraction = maxHealth > 0
            ? MathUtils.clamp(health / (float) maxHealth, 0f, 1f)
            : 0f;
        Color energyColor = fraction > 0.5f ? ENERGY_HIGH
            : (fraction > 0.25f ? ENERGY_MID : ENERGY_LOW);

        // ---- shapes ----
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        shapes.setColor(PANEL);
        shapes.rect(x, timerY, panelW, timerH);

        float border = 2f * scale;
        shapes.setColor(Color.GOLD);
        shapes.rect(x, meterY, panelW, meterH);
        shapes.setColor(METER_BACK);
        shapes.rect(x + border, meterY + border, panelW - 2f * border, meterH - 2f * border);
        shapes.setColor(energyColor);
        shapes.rect(x + border, meterY + border, (panelW - 2f * border) * fraction, meterH - 2f * border);

        shapes.setColor(PANEL);
        shapes.rect(x, countersY, panelW, countersH);

        // pause button
        shapes.setColor(Color.GOLD);
        shapes.rect(pauseBounds.x, pauseBounds.y, btn, btn);
        shapes.setColor(0.10f, 0.20f, 0.52f, 1f);
        shapes.rect(pauseBounds.x + 3f, pauseBounds.y + 3f, btn - 6f, btn - 6f);
        shapes.setColor(Color.WHITE);
        shapes.rect(pauseBounds.x + btn * 0.30f, pauseBounds.y + btn * 0.27f, btn * 0.16f, btn * 0.46f);
        shapes.rect(pauseBounds.x + btn * 0.54f, pauseBounds.y + btn * 0.27f, btn * 0.16f, btn * 0.46f);

        shapes.end();

        // ---- text ----
        int seconds = waves.getSecondsRemaining();
        String time = String.format("%d:%02d", seconds / 60, seconds % 60);

        timerFont.getData().setScale(2.3f * scale);
        timerFont.setColor(Color.WHITE);
        infoFont.getData().setScale(1.0f * scale);
        infoFont.setColor(Color.WHITE);

        batch.begin();

        // timer
        if (clockIcon != null) {
            batch.draw(clockIcon, x + 8f * scale, timerY + 6f * scale, 28f * scale, 28f * scale);
        }
        timerFont.draw(batch, time, x + 50f * scale, timerY + timerH - 8f * scale);

        // energy label, centred on the bar
        String label = "ENERGY  " + health + "/" + maxHealth;
        layout.setText(infoFont, label);
        infoFont.draw(batch, label,
            x + (panelW - layout.width) / 2f,
            meterY + (meterH + layout.height) / 2f);

        // counters
        float textX = x + 8f * scale;
        float rightX = x + panelW - 8f * scale;
        float line1 = countersY + countersH - 8f * scale;
        float line2 = line1 - 20f * scale;

        infoFont.draw(batch, "ITEMS USED", textX, line1);
        drawRight(String.valueOf(itemsUsed), rightX, line1);
        infoFont.draw(batch, "EVENTS", textX, line2);
        drawRight(eventsFound + "/" + TOTAL_EVENTS, rightX, line2);

        batch.end();
    }

    private void drawRight(String s, float rightX, float top) {
        layout.setText(infoFont, s);
        infoFont.draw(batch, s, rightX - layout.width, top);
    }

    @Override
    public void dispose() {
        shapes.dispose();
        timerFont.dispose();
        infoFont.dispose();
        batch.dispose();
        if (clockIcon != null) clockIcon.dispose();
    }
}
