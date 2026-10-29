
package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Player {

    // The student's current position.
    private float x;
    private float y;

    // The student's size and speed.
    private static final float SIZE = 50f;
    private static final float SPEED = 200f;

    // Constructor: creates a player at a starting position.
    public Player(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Update the student's movement every frame.
    public void updateMovement(float deltaTime) {

        float movement = SPEED * deltaTime;

        // Move right.
        if (Gdx.input.isKeyPressed(Input.Keys.D) ||
            Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += movement;
        }

        // Move left.
        if (Gdx.input.isKeyPressed(Input.Keys.A) ||
            Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= movement;
        }

        // Move up.
        if (Gdx.input.isKeyPressed(Input.Keys.W) ||
            Gdx.input.isKeyPressed(Input.Keys.UP)) {
            y += movement;
        }

        // Move down.
        if (Gdx.input.isKeyPressed(Input.Keys.S) ||
            Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y -= movement;
        }

        // Prevent the player from leaving the window.
        keepInsideWindow(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
    }

    // Keep the player inside the specified window size.
    public void keepInsideWindow(int width, int height) {

        float maxX = Math.max(0f, width - SIZE);
        float maxY = Math.max(0f, height - SIZE);

        x = MathUtils.clamp(x, 0f, maxX);
        y = MathUtils.clamp(y, 0f, maxY);
    }

    // Draw the player using the supplied ShapeRenderer.
    public void draw(ShapeRenderer renderer) {

        renderer.setColor(Color.BLUE);
        renderer.rect(x, y, SIZE, SIZE);
    }
}
