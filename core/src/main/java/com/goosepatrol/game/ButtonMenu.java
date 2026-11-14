package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;

/**
 * vertical list of buttons - works w/ the keyboard (UP/DOWN or W/S to
 * move, ENTER to choose) and mouse (hover to highlight, click to choose).
 */
public class ButtonMenu {

    private final String[] labels;
    private final Rectangle[] bounds;
    private final GlyphLayout layout = new GlyphLayout();
    private int selected = 0;

    /**
     *
     * @param labels button labels
     * @param x left edge of buttons
     * @param topY top edge of first button
     * @param width width of buttons
     * @param height height of buttons
     * @param gap gap between buttoms
     */
    public ButtonMenu(String[] labels, float x, float topY, float width, float height, float gap) {
        this.labels = labels;
        this.bounds = new Rectangle[labels.length];
        for (int i = 0; i < labels.length; i++) {
            float y = topY - height - i * (height + gap);
            bounds[i] = new Rectangle(x, y, width, height);
        }
    }

    /**
     * up/down or w/s moves highlight + hovering only changes highlight when mouse actually moves so doesngt interfere w/ keyboard
     * @param mouseX mouse position of x
     * @param mouseY mouse position of y
     * @return index of chosen button or -1 if none chosen
     */
    public int update(float mouseX, float mouseY) {
        int n = labels.length;

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            selected = (selected + 1) % n;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            selected = (selected + n - 1) % n;
        }

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

    /**
     *
     * @param screen screen to draw on
     * @param showArrow if true, draws > as selector to left of selected button
     */
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
