package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;

/**
 * A vertical list of buttons that works with the keyboard (UP/DOWN or W/S to
 * move, ENTER to choose) and the mouse (hover to highlight, click to choose).
 */
public class ButtonMenu {

    private final String[] labels;
    private final Rectangle[] bounds;
    private final GlyphLayout layout = new GlyphLayout();
    private int selected = 0;

    /** topY is the TOP edge of the first button; the rest stack downwards. */
    public ButtonMenu(String[] labels, float x, float topY, float width, float height, float gap) {
        this.labels = labels;
        this.bounds = new Rectangle[labels.length];
        for (int i = 0; i < labels.length; i++) {
            float y = topY - height - i * (height + gap);
            bounds[i] = new Rectangle(x, y, width, height);
        }
    }

    /** Returns the index of the button chosen this frame, or -1 if none. */
    public int update(float mouseX, float mouseY) {
        int n = labels.length;

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            selected = (selected + 1) % n;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            selected = (selected + n - 1) % n;
        }

        // Hover only changes the selection when the mouse actually moves,
        // otherwise a resting cursor would fight the keyboard.
        if (Gdx.input.getDeltaX() != 0 || Gdx.input.getDeltaY() != 0) {
            for (int i = 0; i < n; i++) {
                if (bounds[i].contains(mouseX, mouseY)) selected = i;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            return selected;
        }

        if (Gdx.input.justTouched()) {
            for (int i = 0; i < n; i++) {
                if (bounds[i].contains(mouseX, mouseY)) {
                    selected = i;
                    return i;
                }
            }
        }
        return -1;
    }

    public void draw(UiScreen screen, boolean showArrow) {
        for (int i = 0; i < labels.length; i++) {
            screen.drawButton(bounds[i], labels[i], i == selected);
        }

        if (showArrow) {
            Rectangle r = bounds[selected];
            layout.setText(screen.game.uiFont, ">");
            screen.text(screen.game.uiFont, ">",
                r.x - 36f, r.y + (r.height + layout.height) / 2f, UiScreen.GOLD);
        }
    }
}
