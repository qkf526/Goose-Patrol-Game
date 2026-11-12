package com.goosepatrol.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * Screen manager. Owns everything the screens share: one SpriteBatch, one
 * ShapeRenderer, the fonts, and a texture cache. Screens borrow these and
 * must never dispose them.
 *
 * render() and resize() are deliberately NOT overridden: Game forwards them
 * to whichever screen is current.
 */
public class GoosePatrolGame extends Game {

    public SpriteBatch batch;
    public ShapeRenderer shapes;

    public BitmapFont font;        // legacy font (ControlsScreen still uses it)
    public BitmapFont uiFont;      // buttons and body text
    public BitmapFont titleFont;   // big headings
    public BitmapFont smallFont;   // hints, quantities, item names

    private final ObjectMap<String, Texture> textures = new ObjectMap<>();
    private final ObjectSet<String> missing = new ObjectSet<>();

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();

        font = new BitmapFont();
        font.getData().setScale(2f);

        // Each loads a pixel font from assets/ui if it exists, otherwise falls
        // back to libGDX's built-in font at the given scale.
        uiFont = makeFont(1.6f);
        titleFont = makeFont(4.5f);
        smallFont = makeFont(1.1f);

        setScreen(new MenuScreen(this));
    }

    private BitmapFont makeFont(float scale) {
        BitmapFont f = new BitmapFont();
        f.getData().setScale(scale);
        return f;
    }
    /**
     * Returns a shared, cached texture, or null if the file doesn't exist yet.
     * Callers must null-check and must NOT dispose the result.
     */
    public Texture texture(String path) {
        Texture cached = textures.get(path);
        if (cached != null) return cached;
        if (missing.contains(path)) return null;

        if (!Gdx.files.internal(path).exists()) {
            missing.add(path);        // remember, so we don't hit the disk every frame
            return null;
        }
        Texture t = new Texture(Gdx.files.internal(path));
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        textures.put(path, t);
        return t;
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        batch.dispose();
        shapes.dispose();
        font.dispose();
        uiFont.dispose();
        titleFont.dispose();
        smallFont.dispose();
        for (Texture t : textures.values()) {
            t.dispose();
        }
    }
}
