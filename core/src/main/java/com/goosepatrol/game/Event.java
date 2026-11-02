package com.goosepatrol.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Event {

    // The event's position 
    private float x;
    private float y;

    // How close the student must be to trigger the event.
    private float range;

    // The dialog shown when the student enters the event range.
    private String dialogText;

    // The item given to the student (placeholder cuz idk).
    private String rewardItem;

    // Whether the student is currently inside the event range.
    private boolean playerInRange;

    // Whether the student has already received the item from this event.
    private boolean itemGiven;

    // The font used for the dialog.
    private BitmapFont dialogFont;

    // Constructor: creates an event at a position with a trigger range, dialog and item.
    public Event(float x, float y, float range, String dialogText, String rewardItem) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.dialogText = dialogText;
        this.rewardItem = rewardItem;
        this.playerInRange = false;
        this.itemGiven = false;
        this.dialogFont = new BitmapFont(); // Initialise the default font for drawing dialog.
    }

    // Check every frame whether the student has entered the event range.
    public void checkPlayerInRange(float playerX, float playerY) {

        float distanceX = playerX - x; // Horizontal distance between the student and the event.
        float distanceY = playerY - y; // Vertical distance between the student and the event.
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY); // Pythagoras to get the straight-line distance.

        playerInRange = distance <= range; // The student is in range if they are close enough to the event.

        // Give the student the item the first time they enter the range.
        if (playerInRange && !itemGiven) {
            giveItem(); // Give the student the item.
            itemGiven = true; // Make sure the item is only given once.
        }
    }

    // Give the student the reward item.
    private void giveItem() {
        // placeholder
        System.out.println("The student received: " + rewardItem);
    }

    // Draw the dialog while the student is inside the event range.
    public void drawDialog(SpriteBatch batch) {

        if (playerInRange) {
            dialogFont.draw(batch, dialogText, x - range, y + range); // Draw the dialog text just above the event area.
        }
    }

    // Release resources when the event is no longer needed.
    public void dispose() {
        dialogFont.dispose(); // Release the dialog font resources.
    }
}