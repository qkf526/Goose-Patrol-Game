package com.goosepatrol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class EventArea {

    // Whether the student is in the event area
    private boolean inEventArea;

    // The dialog and item event placed inside the event area.
    private Event itemEvent;

    // The texture for the event sprite (placeholder)
    private Texture eventSprite;

    // The size of the event sprite.
    private static final float SPRITE_SIZE = 48f;

    // The position of the event sprite on the main map that takes the student into the event area.
    private static final float ENTRANCE_X = 500f;
    private static final float ENTRANCE_Y = 200f;

    // The position of the event sprite in the event area that takes the student back to the main map.
    private static final float EXIT_X = 40f;
    private static final float EXIT_Y = 40f;

    // The squares the student has to step on to use each sprite.
    private Rectangle entranceBounds;
    private Rectangle exitBounds;

    // Constructor: starts the student on the main map and creates the event and the event sprite.
    public EventArea() {
        this.inEventArea = false;
        this.itemEvent = new Event(400, 300, 60, "You found something!", "PLACEHOLDER_ITEM_1");
        this.eventSprite = loadEventSprite(); // Load the event sprite.
        this.entranceBounds = new Rectangle(ENTRANCE_X, ENTRANCE_Y, SPRITE_SIZE, SPRITE_SIZE);
        this.exitBounds = new Rectangle(EXIT_X, EXIT_Y, SPRITE_SIZE, SPRITE_SIZE);
    }

    // Load the texture for the event sprite.
    private Texture loadEventSprite() {

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.YELLOW);
        pixmap.fill();
        Texture placeholder = new Texture(pixmap);
        pixmap.dispose();
        return placeholder;
    }

    // Move the student between maps and run the event. Called every frame.
    public void update(Player player) {

        // If the student steps on the sprite on the main map, go to the event area.
        if (!inEventArea && player.getFeetBounds().overlaps(entranceBounds)) {
            inEventArea = true;
            player.setX(EXIT_X + SPRITE_SIZE + 100f); // Put the student away from the exit sprite so they don't go straight back.
            return;
        }

        // If the student steps on the sprite in the event area, go back to the main map.
        if (inEventArea && player.getFeetBounds().overlaps(exitBounds)) {
            inEventArea = false;
            player.setX(ENTRANCE_X - 100f); // Put the student to the left of the entrance sprite so they don't go straight back.
            return;
        }

        // In the event area, check if the student is close to the event.
        if (inEventArea) {
            itemEvent.checkPlayerInRange(player.getCentreX(), player.getCentreY()); // Use the middle of the student.
        }
    }

    // Fill the screen with the colour of the current map.
    public void drawBackground() {
 
        if (inEventArea) {
            ScreenUtils.clear(0.3f, 0.5f, 0.7f, 1f); // Blue for the event area.
        } else {
            ScreenUtils.clear(0.4f, 0.7f, 0.4f, 1f); // Green for the main map.
        }
    }
 
    // Draw the sprite for the current map.
    public void draw(SpriteBatch batch) {
 
        if (inEventArea) {
            batch.draw(eventSprite, EXIT_X, EXIT_Y, SPRITE_SIZE, SPRITE_SIZE); // The way back to the main map.
            itemEvent.drawDialog(batch); // if the student is close for the message
        } else {
            batch.draw(eventSprite, ENTRANCE_X, ENTRANCE_Y, SPRITE_SIZE, SPRITE_SIZE); // The way into the event area.
        }
    }
 
    // optimisation for memory
    public void dispose() {
        itemEvent.dispose();
        eventSprite.dispose();
    }
}