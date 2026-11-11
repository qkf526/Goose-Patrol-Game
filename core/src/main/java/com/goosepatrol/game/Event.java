package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Event {

    // The event's position 
    private float x;
    private float y;

    // How close the student must be to trigger the event.
    private float range;

    // The dialog shown when the student enters the event range.
    private String[] dialogLines;
 
    // Which line of dialog is currently showing (0 is the first line).
    private int currentLine;
 
    // True while the dialog is showing on screen.
    private boolean dialogActive;
 
 
    // How long each line of dialog stays on screen, in seconds.
    private static final float LINE_DURATION = 3f;
 
    // How long the current line has been showing, in seconds.
    private float lineTimer;
 
    // The item given to the student (placeholder cuz idk).
    private String rewardItem;

    // Whether the student is currently inside the event range.
    private boolean playerInRange;

    // Whether the student has already received the item from this event.
    private boolean itemGiven;

    // The font used for the dialog.
    private BitmapFont dialogFont;

    // Constructor: creates an event at a position with a trigger range, dialog and item.
    public Event(float x, float y, float range, String[] dialogLines, String rewardItem) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.dialogLines = dialogLines;
        this.rewardItem = rewardItem;
        this.currentLine = 0;
        this.lineTimer = 0f;
        this.dialogActive = false;
        this.playerInRange = false;
        this.itemGiven = false;
        this.dialogFont = new BitmapFont(); // Initialise the default font for drawing dialog.
    }

    // Check every frame whether the student is in range, and move through the dialog.
    public void checkPlayerInRange(float playerX, float playerY) {
 
        boolean wasInRange = playerInRange; // Remember whether the student was in range last frame.
 
        float distanceX = playerX - x; // Horizontal distance between the student and the event.
        float distanceY = playerY - y; // Vertical distance between the student and the event.
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY); // Pythagoras to get the straight-line distance.
 
        playerInRange = distance <= range; // The student is in range if they are close enough to the event.
 
        // Start the dialog from the first line when the student walks into range.
        if (playerInRange && !wasInRange) {
            dialogActive = true;
            currentLine = 0;
            lineTimer = 0f;
        }
 
        // Close the dialog if the student walks away.
        if (!playerInRange) {
            dialogActive = false;
        }
 
        // Count how long the current line has been showing.
        if (dialogActive) {
            lineTimer += Gdx.graphics.getDeltaTime(); // Add the time since the last frame.
        }
 
        // Show the next line once the current line has been showing for 3 seconds.
        if (dialogActive && lineTimer >= LINE_DURATION) {
            currentLine++;
            lineTimer = 0f; // Start timing the new line from zero.
 
            // Close the dialog after the last line.
            if (currentLine >= dialogLines.length) {
                dialogActive = false;
 
                // Give the student the item the first time they finish the dialog.
                if (!itemGiven) {
                    giveItem();
                    itemGiven = true; // Make sure the item is only given once.
                }
            }
        }
    }
 
    // Give the student the reward item.
    private void giveItem() {
        // Placeholder.
        System.out.println("The student received: " + rewardItem);
    }
 
    // Draw the current line of dialog while the dialog is showing.
    public void drawDialog(SpriteBatch batch) {
 
        if (dialogActive) {
            dialogFont.draw(batch, dialogLines[currentLine], x - range, y + range); // Draw the current line just above the event area.
        }
    }
 
    // Release resources when the event is no longer needed.
    public void dispose() {
        dialogFont.dispose(); // Release the dialog font resources.
    }
}