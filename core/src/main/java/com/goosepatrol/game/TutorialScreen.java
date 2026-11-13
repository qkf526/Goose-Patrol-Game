package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Six-page tutorial, one page at a time.
 *
 * Opened from the main menu or the pause screen. 'previous' is whichever
 * screen opened it. SKIP and ESC go back to it. The last button starts the
 * game if we came from the menu, otherwise it returns to the pause screen.
 *
 * Keys: RIGHT / ENTER = next, LEFT = back, ESC = leave.
 */
public class TutorialScreen extends UiScreen {

    private static final int PAGES = 6;

    // The mockups mention hidden events and an E key. No such feature exists
    // yet, so that text stays hidden. Set to true once it does.
    private static final boolean EVENTS = false;

    private static final String[] TITLES = {
        "WELCOME!", "MOVING", "WATCH OUT FOR GEESE", "ITEMS", "YOUR SCREEN", "READY?"
    };

    private final Screen previous;
    private final Rectangle backBounds = new Rectangle(85f, 80f, 150f, 40f);
    private final Rectangle nextBounds = new Rectangle(565f, 80f, 150f, 40f);
    private final Rectangle skipBounds = new Rectangle(615f, 505f, 100f, 26f);
    private final GlyphLayout layout = new GlyphLayout();
    private int page = 0;

    public TutorialScreen(GoosePatrolGame game, Screen previous) {
        super(game);
        this.previous = previous;
    }

    // ------------------------------------------------------------------
    // Input
    // ------------------------------------------------------------------
    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(previous);
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            next();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && page > 0) {
            page--;
        }

        if (Gdx.input.justTouched()) {
            if (skipBounds.contains(mouseX, mouseY)) {
                game.setScreen(previous);
            } else if (page > 0 && backBounds.contains(mouseX, mouseY)) {
                page--;
            } else if (nextBounds.contains(mouseX, mouseY)) {
                next();
            }
        }
    }

    private void next() {
        if (page < PAGES - 1) {
            page++;
        } else if (previous instanceof MenuScreen) {
            game.setScreen(new GameScreen(game));   // "START!"
        } else {
            game.setScreen(previous);               // "DONE"
        }
    }

    // ------------------------------------------------------------------
    // Drawing
    // ------------------------------------------------------------------
    @Override
    protected void draw() {
        background("ui/menu_blur.png", 0.45f);
        panel(60f, 60f, 680f, 480f);

        text(game.smallFont, "TUTORIAL " + (page + 1) + "/" + PAGES, 85f, 525f, Color.LIGHT_GRAY);
        textRight(game.smallFont, "SKIP >", 715f, 525f,
            skipBounds.contains(mouseX, mouseY) ? Color.WHITE : GOLD);

        textCentered(game.titleFont, TITLES[page], W / 2f, 500f, GOLD);

        switch (page) {
            case 0: drawWelcome(); break;
            case 1: drawMoving(); break;
            case 2: drawGeese(); break;
            case 3: drawItems(); break;
            case 4: drawScreenTour(); break;
            default: drawReady(); break;
        }

        drawDots();

        if (page > 0) {
            drawButton(backBounds, "< BACK", backBounds.contains(mouseX, mouseY));
        }
        String last = previous instanceof MenuScreen ? "START!" : "DONE";
        drawButton(nextBounds, page == PAGES - 1 ? last : "NEXT >",
            nextBounds.contains(mouseX, mouseY));
    }

    private void drawWelcome() {
        image(game.texture("ui/menu_student.png"), 250f, 340f, 70f, 90f);
        image(game.texture("ui/clock.png"), 365f, 345f, 70f, 70f);
        image(game.texture("ui/menu_geese.png"), 470f, 340f, 80f, 96f);

        body(260f, "DISPERSE EVERY WAVE OF GEESE", Color.WHITE);
        body(230f, "BEFORE THE TIMER RUNS OUT.", Color.WHITE);
        body(200f, "USE YOUR ITEMS TO SEE THEM OFF.", Color.WHITE);
        if (EVENTS) {
            body(170f, "THERE ARE HIDDEN EVENTS TO FIND, TOO!", Color.WHITE);
        }
    }

    private void drawMoving() {
        keycap(285f, 405f, 36f, 30f, "W");
        keycap(245f, 369f, 36f, 30f, "A");
        keycap(285f, 369f, 36f, 30f, "S");
        keycap(325f, 369f, 36f, 30f, "D");
        textCentered(game.smallFont, "MOVE", 303f, 358f, Color.WHITE);

        image(game.texture("ui/menu_student.png"), 395f, 345f, 60f, 85f);

        keycap(500f, 387f, 64f, 30f, "ESC");
        textCentered(game.smallFont, "PAUSE", 532f, 358f, Color.WHITE);

        body(260f, "WALK AROUND WITH W A S D.", Color.WHITE);
        body(225f, "PRESS ESC TO PAUSE AND RESUME.", GOLD);
    }

    private void drawGeese() {
        image(game.texture("ui/menu_geese.png"), 200f, 340f, 80f, 96f);
        image(game.texture("ui/menu_student.png"), 330f, 340f, 60f, 90f);

        // Energy bar, same look as the in-game meter, shown at 60%
        float barX = 430f;
        float barY = 384f;
        float barW = 160f;
        float barH = 24f;
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(GOLD);
        game.shapes.rect(barX, barY, barW, barH);
        game.shapes.setColor(0.03f, 0.05f, 0.08f, 1f);
        game.shapes.rect(barX + 2f, barY + 2f, barW - 4f, barH - 4f);
        game.shapes.setColor(0.25f, 0.80f, 0.30f, 1f);
        game.shapes.rect(barX + 2f, barY + 2f, (barW - 4f) * 0.6f, barH - 4f);
        game.shapes.end();
        textCentered(game.smallFont, "ENERGY", barX + barW / 2f, 372f, Color.WHITE);

        body(260f, "THE GEESE COME IN WAVES AND PECK.", Color.WHITE);
        body(230f, "EACH PECK DRAINS YOUR ENERGY.", Color.WHITE);
        body(200f, "LOSE ALL YOUR ENERGY AND IT'S OVER.", Color.WHITE);
    }

    private void drawItems() {
        String[] names = {"SEEDS", "AIR HORN", "BANDAGE", "SAUSAGE ROLL"};
        String[] effects = {"DISTRACTS GEESE", "SCARES GEESE", "RESTORES 2 ENERGY", "FULL ENERGY"};
        ItemType[] types = {ItemType.SEEDS, ItemType.AIR_HORN, ItemType.BANDAGE, ItemType.SAUSAGE_ROLL};

        for (int i = 0; i < 4; i++) {
            float cx = 175f + i * 150f;
            keycap(cx - 14f, 400f, 28f, 28f, String.valueOf(i + 1));
            image(game.texture("ui/icons/" + types[i].name().toLowerCase() + ".png"),
                cx - 28f, 338f, 56f, 56f);
            textCentered(game.smallFont, names[i], cx, 332f, Color.WHITE);
            textCentered(game.smallFont, effects[i], cx, 314f, GOLD);
        }

        keycap(350f, 255f, 100f, 28f, "SPACE");
        body(235f, "PRESS 1, 2, 3 OR 4 TO PICK AN ITEM.", Color.WHITE);
        body(205f, "THEN PRESS SPACE TO USE IT.", Color.WHITE);
    }

    private void drawScreenTour() {
        // Frame for a gameplay screenshot (assets/ui/tutorial_hud.png, optional)
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(GOLD);
        game.shapes.rect(238f, 288f, 324f, 154f);
        game.shapes.setColor(0.13f, 0.25f, 0.15f, 1f);
        game.shapes.rect(240f, 290f, 320f, 150f);
        game.shapes.end();
        image(game.texture("ui/tutorial_hud.png"), 240f, 290f, 320f, 150f);

        text(game.smallFont, "TIMER", 85f, 425f, Color.WHITE);
        text(game.smallFont, "ENERGY", 85f, 400f, Color.WHITE);
        text(game.smallFont, "ITEMS", 85f, 315f, Color.WHITE);
        textRight(game.smallFont, "PAUSE", 715f, 425f, Color.WHITE);

        body(260f, "KEEP AN EYE ON THE TIMER.", Color.WHITE);
        body(230f, "YOUR ENERGY AND YOUR ITEMS.", Color.WHITE);
    }

    private void drawReady() {
        float y = 410f;
        keyRow(y, new String[] {"W", "A", "S", "D"}, 32f, "MOVE");
        y -= 40f;
        keyRow(y, new String[] {"1", "2", "3", "4"}, 32f, "PICK ITEM");
        y -= 40f;
        keyRow(y, new String[] {"SPACE"}, 100f, "USE ITEM");
        y -= 40f;
        if (EVENTS) {
            keyRow(y, new String[] {"E"}, 32f, "TRIGGER EVENT");
            y -= 40f;
        }
        keyRow(y, new String[] {"ESC"}, 64f, "PAUSE / RESUME");

        body(210f, EVENTS ? "FIND THE HIDDEN EVENTS. GOOD LUCK!" : "GOOD LUCK!", GOLD);
    }

    // ------------------------------------------------------------------
    // Small helpers
    // ------------------------------------------------------------------
    private void body(float top, String line, Color color) {
        textCentered(game.uiFont, line, W / 2f, top, color);
    }

    private void keyRow(float y, String[] keys, float keyW, String label) {
        for (int i = 0; i < keys.length; i++) {
            keycap(240f + i * (keyW + 4f), y, keyW, 30f, keys[i]);
        }
        layout.setText(game.uiFont, label);
        text(game.uiFont, label, 470f, y + (30f + layout.height) / 2f, Color.WHITE);
    }

    private void keycap(float x, float y, float w, float h, String label) {
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(Color.DARK_GRAY);
        game.shapes.rect(x, y, w, h);
        game.shapes.setColor(Color.LIGHT_GRAY);
        game.shapes.rect(x + 2f, y + 2f, w - 4f, h - 4f);
        game.shapes.end();

        layout.setText(game.smallFont, label);
        text(game.smallFont, label,
            x + (w - layout.width) / 2f, y + (h + layout.height) / 2f, Color.BLACK);
    }

    private void drawDots() {
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < PAGES; i++) {
            game.shapes.setColor(i == page ? GOLD : Color.GRAY);
            game.shapes.rect(400f - PAGES * 9f + i * 18f + 4f, 96f, 8f, 8f);
        }
        game.shapes.end();
    }
}
