package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

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
 
    // The key the student presses to talk and to move to the next line.
    private static final int INTERACT_KEY = Input.Keys.E;

    // The item given to the student (placeholder cuz idk).
    private Item rewardItem;

    // Whether the student is currently inside the event range.
    private boolean playerInRange;

    // Whether the student has already received the item from this event.
    private boolean itemGiven;

    // The font used for the dialog.
    private BitmapFont dialogFont;

    // The point the chat box sits above (for example, the top of a character's head).
    private float speakerX;
    private float speakerY;
 
    // The size and spacing of the chat box.
    private static final float BOX_WIDTH = 320f;
    private static final float BOX_PADDING = 10f;
    private static final float BOX_BORDER = 2f;
 
    // The colours of the chat box, matching the game's HUD panels.
    private static final Color BOX_COLOUR = new Color(0.07f, 0.11f, 0.16f, 0.92f);
    private static final Color BORDER_COLOUR = Color.GOLD;
    private static final Color HINT_COLOUR = Color.LIGHT_GRAY;
 
    // A plain white square, stretched and tinted to draw the chat box.
    private Texture boxTexture;
 
    // Measures the dialog text so the chat box can fit around it.
    private GlyphLayout textLayout;
 

    // Constructor: creates an event at a position with a trigger range, dialog and item.
    public Event(float x, float y, float range, String[] dialogLines, Item rewardItem) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.dialogLines = dialogLines;
        this.rewardItem = rewardItem;
        this.currentLine = 0;
        this.dialogActive = false;
        this.playerInRange = false;
        this.itemGiven = false;
        this.dialogFont = new BitmapFont(); // Initialise the default font for drawing dialog.

        // By default the chat box sits just above the event area.
        this.speakerX = x;
        this.speakerY = y + range;
 
        // Make a white square to draw the chat box with.
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.boxTexture = new Texture(pixmap);
        pixmap.dispose(); // The pixmap is no longer needed once the texture is made.
 
        this.textLayout = new GlyphLayout();
    }

    // Set the point the chat box sits above, such as the top of the speaker's head.
    public void setSpeakerPosition(float speakerX, float speakerY) {
        this.speakerX = speakerX;
        this.speakerY = speakerY;
    }

    // Check every frame whether the student is in range, and let them talk using the E key.
    public void checkPlayerInRange(float playerX, float playerY, Inventory inventory) {

        float distanceX = playerX - x; // Horizontal distance between the student and the event.
        float distanceY = playerY - y; // Vertical distance between the student and the event.
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY); // Pythagoras to get the straight-line distance.

        playerInRange = distance <= range; // The student is in range if they are close enough to the event.

        // Close the dialog if the student walks away.
        if (!playerInRange) {
            dialogActive = false;
            return;
        }

        // Only react on the frame the E key is pressed.
        if (!Gdx.input.isKeyJustPressed(INTERACT_KEY)) {
            return;
        }

        if (!dialogActive) {
            // Start the dialog from the first line.
            dialogActive = true;
            currentLine = 0;
        } else {
            // Show the next line.
            currentLine++;

            // Close the dialog after the last line.
            if (currentLine >= dialogLines.length) {
                dialogActive = false;

                // Give the student the item the first time they finish the dialog.
                if (!itemGiven) {
                    giveItem(inventory);
                    itemGiven = true; // Make sure the item is only given once.
                }
            }
        }
    }
 
    // Give the student the reward item by adding it to their inventory.
    private void giveItem(Inventory inventory) {
        inventory.addItem(rewardItem, 1); // Add one of the item to the student's inventory.
    }
 
    // Draw the chat box above the speaker while the student is in range.
    public void drawDialog(SpriteBatch batch) {

        // Nothing to show when the student is not close enough.
        if (!playerInRange) {
            return;
        }

        // Show the current line while talking, or a prompt before the student starts.
        String text;
        String hint;

        if (dialogActive) {
            text = dialogLines[currentLine];
            hint = "Press E to continue";
        } else {
            text = "Press E to talk";
            hint = null;
        }

        // Measure the text, wrapping it onto new lines to fit inside the box.
        float textWidth = BOX_WIDTH - BOX_PADDING * 2f;
        textLayout.setText(dialogFont, text, Color.WHITE, textWidth, Align.left, true);

        // Make the box tall enough for the text, plus a line for the hint.
        float hintHeight = (hint != null) ? 20f : 0f;
        float boxHeight = textLayout.height + hintHeight + BOX_PADDING * 2f;

        // Centre the box above the speaker.
        float boxX = speakerX - BOX_WIDTH / 2f;
        float boxY = speakerY + 10f;

        // Draw the gold border, then the dark box on top of it.
        batch.setColor(BORDER_COLOUR);
        batch.draw(boxTexture, boxX - BOX_BORDER, boxY - BOX_BORDER, BOX_WIDTH + BOX_BORDER * 2f, boxHeight + BOX_BORDER * 2f);
        batch.setColor(BOX_COLOUR);
        batch.draw(boxTexture, boxX, boxY, BOX_WIDTH, boxHeight);
        batch.setColor(Color.WHITE); // Reset the colour so other sprites draw normally.

        // Draw the dialog text at the top of the box.
        dialogFont.draw(batch, textLayout, boxX + BOX_PADDING, boxY + boxHeight - BOX_PADDING);

        // Draw the hint at the bottom of the box.
        if (hint != null) {
            dialogFont.setColor(HINT_COLOUR);
            dialogFont.draw(batch, hint, boxX + BOX_PADDING, boxY + BOX_PADDING + 12f);
            dialogFont.setColor(Color.WHITE);
        }
    }
    
    public boolean isCompleted() {
        return itemGiven;
    }

    //restarts the event, so the item can be given again in a new match.
    public void reset() {
        currentLine = 0;
        dialogActive = false;
        playerInRange = false;
        itemGiven = false;
    }
 
    // Release resources when the event is no longer needed.
    public void dispose() {
        dialogFont.dispose(); // Release the dialog font resources.
        boxTexture.dispose(); // Release the chat box texture.
    }
}