package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class StatueEvent {

    // The centre of the Long Boi statue on the campus map.
    private static final float STATUE_X = 205f;
    private static final float STATUE_Y = 160f;

    // The spot beside the statue where the student can use it (on the grass to the left of the plinth).
    private static final float TRIGGER_X = 137f;
    private static final float TRIGGER_Y = 110f;

    // How close the student must be to use the statue.
    private static final float INTERACT_RANGE = 50f;

    // The point the pop-up sits above (the top of the goose's head).
    private static final float POPUP_X = 205f;
    private static final float POPUP_Y = 235f;

    // The key the student presses to use the statue.
    private static final int INTERACT_KEY = Input.Keys.E;

    // How long the "HONK!" message stays on screen, in seconds.
    private static final float HONK_MESSAGE_TIME = 2.5f;

    // The short description shown in the pop-up.
    private static final String DESCRIPTION =
        "Long Boi Statue\n" +
        "A statue resembling to the campus's most famous bird. Rumor has it his honk still scares geese away.";

    // Whether the student is close enough to use the statue.
    private boolean playerInRange;

    // Whether the statue has already been used this match.
    private boolean used;

    // How long the "HONK!" message has left on screen, and how many geese it scared.
    private float honkMessageTimer;
    private int geeseScared;

    // The box used to draw the pop-up.
    private Dialog popup;

    // Constructor: the statue starts ready to use.
    public StatueEvent() {
        this.playerInRange = false;
        this.used = false;
        this.honkMessageTimer = 0f;
        this.geeseScared = 0;
        this.popup = new Dialog();
    }

    // Check every frame whether the student is close, and honk when they press E. Only called on campus.
    public void update(Player player, GooseWaveManager gooseWaves) {

        // Count down the "HONK!" message.
        if (honkMessageTimer > 0f) {
            honkMessageTimer = Math.max(0f, honkMessageTimer - Gdx.graphics.getDeltaTime());
        }

        float distanceX = player.getCentreX() - TRIGGER_X; // Horizontal distance between the student and the trigger spot.
        float distanceY = player.getCentreY() - TRIGGER_Y; // Vertical distance between the student and the trigger spot.
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY); // Pythagoras to get the straight-line distance.

        playerInRange = distance <= INTERACT_RANGE; // The student is in range if they are close enough to the statue.

        // Honk when the student presses E next to the statue, once per match.
        if (playerInRange && !used && Gdx.input.isKeyJustPressed(INTERACT_KEY)) {
            geeseScared = gooseWaves.useStatueHonk(STATUE_X, STATUE_Y); // Scare away every goose in range.
            used = true;
            honkMessageTimer = HONK_MESSAGE_TIME;
        }
    }

    // Draw the pop-up while the student is close, or the "HONK!" message just after using it.
    public void draw(SpriteBatch batch) {

        if (honkMessageTimer > 0f) {
            popup.draw(batch, POPUP_X, POPUP_Y, "HONK! " + geeseScared + " geese fled.", null);
        } else if (playerInRange && !used) {
            popup.draw(batch, POPUP_X, POPUP_Y, DESCRIPTION, "Press E to honk");
        } else if (playerInRange) {
            popup.draw(batch, POPUP_X, POPUP_Y, DESCRIPTION, "Long Boi has already honked this match.");
        }
    }

    // Put the statue back to how it starts, so it can be used again in a new match.
    public void reset() {
        playerInRange = false;
        used = false;
        honkMessageTimer = 0f;
        geeseScared = 0;
    }

    // Free up memory when the game closes.
    public void dispose() {
        popup.dispose();
    }
}