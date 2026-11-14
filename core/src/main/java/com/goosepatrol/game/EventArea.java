package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;


public class EventArea {

    // Whether the student is in the event area
    private boolean inEventArea;

    // The dialog and item event placed inside the event area.
    private Event itemEvent;

    // The Greggs shop map, the renderer that draws it, and its walls and counters.
    private TiledMap shopMap;
    private OrthogonalTiledMapRenderer shopRenderer;
    private CollisionMap shopCollisions;
 
    // The size of the Greggs shop map, in pixels.
    public static final int SHOP_WIDTH = 640;
    public static final int SHOP_HEIGHT = 480;
 
    // The Greggs front door on the campus map. Walking into it takes the student inside.
    private static final float ENTRANCE_X = 615f;
    private static final float ENTRANCE_Y = 755f;
    private static final float ENTRANCE_WIDTH = 60f;
    private static final float ENTRANCE_HEIGHT = 30f;
 
    // Where the student appears on campus after leaving: on the path in front of the Greggs door.
    private static final float CAMPUS_RETURN_X = 606f;
    private static final float CAMPUS_RETURN_Y = 690f;
 
    // The sliding doors at the bottom of the shop. Walking into them takes the student back outside.
    private static final float DOOR_X = 280f;
    private static final float DOOR_Y = 0f;
    private static final float DOOR_WIDTH = 80f;
    private static final float DOOR_HEIGHT = 20f;
 
    // Where the student appears in the shop: on the doormat, just inside the sliding doors.
    private static final float ARRIVAL_X = 281f;
    private static final float ARRIVAL_Y = 25f;
 
    // The areas the student has to step on to enter or leave.
    private Rectangle entranceBounds;
    private Rectangle doorBounds;

    // The key the student presses to go through a door.
    private static final int INTERACT_KEY = Input.Keys.E;

        // Whether the student is standing at a door they can use.
    private boolean atEntrance;
    private boolean atExit;
 
    // The font used for the door prompts.
    private BitmapFont promptFont;
 
    // Constructor: starts the student on the main map and creates the event and the event sprite.
    public EventArea() {
        this.inEventArea = false;

        // The dialog for this event. Each line shows for 3 seconds.
        String[] firstEventDialog = {
            "Welcome, Greggs is celebrating its 87th anniversary.",
            "Wow your our first customer today, I wonder where everyone else is?",
            "well please take our new anniversary sausage roll as a sample."
        };

        // The event is placed on the floor tiles in front of the cashier, so the dialog starts when the student walks up to the counter.
        this.itemEvent = new Event(442, 300, 45, firstEventDialog, new SausageRoll());
        this.itemEvent.setSpeakerPosition(225, 255); // Show the chat box above the cashier's head.
 
        // Load the shop map, the same way the campus map is loaded.
        this.shopMap = new TmxMapLoader().load("maps/greggs.tmx");
        this.shopRenderer = new OrthogonalTiledMapRenderer(shopMap);
        this.shopCollisions = new CollisionMap(shopMap);
 
        this.entranceBounds = new Rectangle(ENTRANCE_X, ENTRANCE_Y, ENTRANCE_WIDTH, ENTRANCE_HEIGHT);
        this.doorBounds = new Rectangle(DOOR_X, DOOR_Y, DOOR_WIDTH, DOOR_HEIGHT);

        // Create the font for Greggs entrance and exit prompts.
        this.promptFont = new BitmapFont();

        // Use black text for better visibility.
        this.promptFont.setColor(Color.BLACK);

        // Enlarge the text by 40%.
        this.promptFont.getData().setScale(1.4f);

    }
    

    // Move the student between campus and the shop, and run the event. Called every frame.
    public void update(Player player, Inventory inventory) {
 
        // Check whether the student is standing at the Greggs door or the sliding doors.
        atEntrance = !inEventArea && player.getFeetBounds().overlaps(entranceBounds);
        atExit = inEventArea && player.getFeetBounds().overlaps(doorBounds);
 
        boolean interactPressed = Gdx.input.isKeyJustPressed(INTERACT_KEY); // True on the frame E is pressed.
 
        // If the student presses E at the Greggs door on campus, go into the shop.
        if (atEntrance && interactPressed) {
            inEventArea = true;
            atEntrance = false;
            player.setX(ARRIVAL_X); // Put the student on the doormat, just inside the sliding doors.
            player.setY(ARRIVAL_Y);
            return;
        }
 
        // If the student presses E at the sliding doors, go back to campus.
        if (atExit && interactPressed) {
            inEventArea = false;
            atExit = false;
            player.setX(CAMPUS_RETURN_X); // Put the student on the path in front of the Greggs door.
            player.setY(CAMPUS_RETURN_Y);
            return;
        }
 
        // In the shop, check if the student is close to the cashier.
        if (inEventArea) {
            itemEvent.checkPlayerInRange(player.getCentreX(), player.getCentreY(), inventory); // Use the middle of the student.
        }
    }
 
    // Return true while the student is inside the shop.
    public boolean isInEventArea() {
        return inEventArea;
    }
 
    // Get the shop's walls and counters so the student cannot walk through them.
    public CollisionMap getShopCollisions() {
        return shopCollisions;
    }
 
    // Get the renderer that draws the shop map.
    public OrthogonalTiledMapRenderer getShopRenderer() {
        return shopRenderer;
    }
 
    // Draw the door prompts, and the cashier dialog when the student is in the shop.
    public void drawDialog(SpriteBatch batch) {
 
        if (atEntrance) {
            promptFont.draw(batch, "Press E to enter Greggs", ENTRANCE_X - 50f, ENTRANCE_Y - 5); // Show the prompt just above the Greggs door.
        }
 
        if (atExit) {
            promptFont.draw(batch, "Press E to leave", DOOR_X + 30f, DOOR_Y + 130f); // Show the prompt just above the sliding doors.
        }
 
        if (inEventArea) {
            itemEvent.drawDialog(batch); // Draw the cashier dialog if the student is close.
        }
    }

    public boolean hasTriggered() {
        return itemEvent.isCompleted();
    }

    // restart event area back for a new match.
    public void reset() {
        System.out.println("TEST: EventArea reset"); // TEMPORARY TEST
        inEventArea = false; // The student starts the new match on campus.
        atEntrance = false;
        atExit = false;
        itemEvent.reset(); // Let the cashier give the sausage roll again.
    }
 
    // Free up memory when the game closes.
    public void dispose() {
        itemEvent.dispose();
        promptFont.dispose();
        shopRenderer.dispose();
        shopMap.dispose();
    }
}