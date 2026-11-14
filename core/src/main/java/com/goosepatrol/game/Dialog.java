package com.goosepatrol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;


public class Dialog {

    // The size and spacing of the box.
    private static final float BOX_WIDTH = 320f;
    private static final float BOX_PADDING = 10f;
    private static final float BOX_BORDER = 2f;

    // The colours of the box, matching the game's HUD panels.
    private static final Color BOX_COLOUR = new Color(0.07f, 0.11f, 0.16f, 0.92f);
    private static final Color BORDER_COLOUR = Color.GOLD;
    private static final Color HINT_COLOUR = Color.LIGHT_GRAY;

    // The font used for the text in the box.
    private BitmapFont font;

    // A plain white square, stretched and tinted to draw the box.
    private Texture boxTexture;

    // Measures the text so the box can fit around it.
    private GlyphLayout textLayout;

    // Constructor: creates the font and the white square used to draw the box.
    public Dialog() {
        this.font = new BitmapFont();

        // Make a white square to draw the box with.
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.boxTexture = new Texture(pixmap);
        pixmap.dispose(); // The pixmap is no longer needed once the texture is made.

        this.textLayout = new GlyphLayout();
    }

    // Draw a box centred above a point, with the text at the top and an optional hint at the bottom.
    public void draw(SpriteBatch batch, float aboveX, float aboveY, String text, String hint) {

        // Measure the text, wrapping it onto new lines to fit inside the box.
        float textWidth = BOX_WIDTH - BOX_PADDING * 2f;
        textLayout.setText(font, text, Color.WHITE, textWidth, Align.left, true);

        // Make the box tall enough for the text, plus a line for the hint.
        float hintHeight = (hint != null) ? 20f : 0f;
        float boxHeight = textLayout.height + hintHeight + BOX_PADDING * 2f;

        // Centre the box above the point.
        float boxX = aboveX - BOX_WIDTH / 2f;
        float boxY = aboveY + 10f;

        // Draw the gold border, then the dark box on top of it.
        batch.setColor(BORDER_COLOUR);
        batch.draw(boxTexture, boxX - BOX_BORDER, boxY - BOX_BORDER, BOX_WIDTH + BOX_BORDER * 2f, boxHeight + BOX_BORDER * 2f);
        batch.setColor(BOX_COLOUR);
        batch.draw(boxTexture, boxX, boxY, BOX_WIDTH, boxHeight);
        batch.setColor(Color.WHITE); // Reset the colour so other sprites draw normally.

        // Draw the text at the top of the box.
        font.draw(batch, textLayout, boxX + BOX_PADDING, boxY + boxHeight - BOX_PADDING);

        // Draw the hint at the bottom of the box.
        if (hint != null) {
            font.setColor(HINT_COLOUR);
            font.draw(batch, hint, boxX + BOX_PADDING, boxY + BOX_PADDING + 12f);
            font.setColor(Color.WHITE);
        }
    }

    // Free up memory when the game closes.
    public void dispose() {
        font.dispose();
        boxTexture.dispose();
    }
}