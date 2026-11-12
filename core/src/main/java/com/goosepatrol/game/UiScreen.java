package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Base class for the menu-style screens (main menu, pause, win, game over).
 *
 * Every subclass lays itself out on a fixed 800x600 canvas. A FitViewport
 * scales that canvas to the window, so layouts never move when the window is
 * resized, and mouse positions are converted into the same canvas
 * coordinates. Origin is the BOTTOM-left; text "top" values are the top edge
 * of the text.
 *
 * Subclasses implement update() (input and screen changes) and draw().
 */
public abstract class UiScreen implements Screen {

    public static final float W = 800f;
    public static final float H = 600f;

    public static final Color GOLD = new Color(0.98f, 0.78f, 0.20f, 1f);
    public static final Color PANEL = new Color(0.07f, 0.13f, 0.36f, 1f);
    public static final Color BUTTON = new Color(0.10f, 0.20f, 0.52f, 1f);
    public static final Color BUTTON_SELECTED = new Color(0.20f, 0.34f, 0.72f, 1f);
    public static final Color RED = new Color(0.93f, 0.25f, 0.22f, 1f);

    protected final GoosePatrolGame game;
    protected final FitViewport viewport;

    private final OrthographicCamera camera = new OrthographicCamera();
    private final GlyphLayout layout = new GlyphLayout();
    private final Vector2 mouse = new Vector2();

    /** Mouse position in canvas coordinates, refreshed every frame. */
    protected float mouseX;
    protected float mouseY;

    protected UiScreen(GoosePatrolGame game) {
        this.game = game;
        this.viewport = new FitViewport(W, H, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    /** Handle input here. May call game.setScreen(...). */
    protected abstract void update(float delta);

    /** Draw the screen here using the helpers below. */
    protected abstract void draw();

    @Override
    public final void render(float delta) {
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.shapes.setProjectionMatrix(camera.combined);

        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouse);
        mouseX = mouse.x;
        mouseY = mouse.y;

        update(delta);
        if (game.getScreen() != this) return;   // update() switched screens

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        draw();
    }

    @Override
    public void resize(int width, int height)
    {
        viewport.update(width, height, true);
    }

    @Override
    public void show()
    {

    }
    @Override
    public void pause()
    {

    }
    @Override
    public void resume()
    {

    }
    @Override
    public void hide()
    {

    }
    @Override
    public void dispose()
    {

    }   // screens own nothing; the game owns the shared resources

    // ------------------------------------------------------------------
    // Drawing helpers. Each one opens and closes its own begin()/end().
    // Call them in order: background, panels, buttons, text/images.
    // ------------------------------------------------------------------

    /** Full-canvas picture (or a flat colour if the art isn't there yet), optionally darkened. */
    protected void background(String path, float dim) {
        Texture bg = game.texture(path);
        if (bg != null) {
            game.batch.begin();
            game.batch.draw(bg, 0f, 0f, W, H);
            game.batch.end();
        } else {
            game.shapes.begin(ShapeRenderer.ShapeType.Filled);
            game.shapes.setColor(0.13f, 0.22f, 0.15f, 1f);
            game.shapes.rect(0f, 0f, W, H);
            game.shapes.end();
        }

        if (dim > 0f) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            game.shapes.begin(ShapeRenderer.ShapeType.Filled);
            game.shapes.setColor(0f, 0f, 0f, dim);
            game.shapes.rect(0f, 0f, W, H);
            game.shapes.end();
        }
    }

    /** Navy rectangle with a gold border. */
    protected void panel(float x, float y, float w, float h) {
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(GOLD);
        game.shapes.rect(x, y, w, h);
        game.shapes.setColor(PANEL);
        game.shapes.rect(x + 3f, y + 3f, w - 6f, h - 6f);
        game.shapes.end();
    }

    /** Thin gold horizontal line. */
    protected void rule(float x, float y, float w) {
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(GOLD);
        game.shapes.rect(x, y, w, 2f);
        game.shapes.end();
    }

    protected void drawButton(Rectangle r, String label, boolean selected) {
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(GOLD);
        game.shapes.rect(r.x, r.y, r.width, r.height);
        game.shapes.setColor(selected ? BUTTON_SELECTED : BUTTON);
        game.shapes.rect(r.x + 3f, r.y + 3f, r.width - 6f, r.height - 6f);
        game.shapes.end();

        BitmapFont f = game.uiFont;
        layout.setText(f, label);
        text(f, label,
            r.x + (r.width - layout.width) / 2f,
            r.y + (r.height + layout.height) / 2f,
            Color.WHITE);
    }

    protected void text(BitmapFont font, String s, float x, float top, Color color) {
        font.setColor(color);
        game.batch.begin();
        font.draw(game.batch, s, x, top);
        game.batch.end();
    }

    protected void textCentered(BitmapFont font, String s, float centerX, float top, Color color) {
        layout.setText(font, s);
        text(font, s, centerX - layout.width / 2f, top, color);
    }

    protected void textRight(BitmapFont font, String s, float rightX, float top, Color color) {
        layout.setText(font, s);
        text(font, s, rightX - layout.width, top, color);
    }

    /** Draws a texture; does nothing if it is null (art not added yet). */
    protected void image(Texture t, float x, float y, float w, float h) {
        image(t, x, y, w, h, Color.WHITE);
    }

    protected void image(Texture t, float x, float y, float w, float h, Color tint) {
        if (t == null) return;
        game.batch.setColor(tint);
        game.batch.begin();
        game.batch.draw(t, x, y, w, h);
        game.batch.end();
        game.batch.setColor(Color.WHITE);
    }
}
