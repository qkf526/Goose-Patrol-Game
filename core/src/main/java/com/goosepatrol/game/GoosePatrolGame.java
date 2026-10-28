
package com.goosepatrol.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;


public class GoosePatrolGame extends ApplicationAdapter {

    // Object used to draw simple shapes.
    private ShapeRenderer shapeRenderer;

    // Student's position on the screen.
    private float playerX = 200f;
    private float playerY = 120f;

    // Width and height of the student.
    private static final float PLAYER_SIZE = 80f;
    // Speed at which the student moves across the screen.
    private static final float PLAYER_SPEED = 200f;

    @Override
    public void create() {
        // Initialise the shape drawing object.
        shapeRenderer = new ShapeRenderer();

        System.out.println("Goose Patrol has started!");
    }

    // Called every frame to update the game state and render the screen.
    @Override
    public void render() {

        float deltaTime = Gdx.graphics.getDeltaTime();
        handleInput(deltaTime);
        keepPlayerInsideWindow();

        // Draw a green background.
        ScreenUtils.clear(0.4f, 0.7f, 0.4f, 1f);

        // Begin drawing filled shapes.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Set the student's colour to blue.
        shapeRenderer.setColor(Color.BLUE);

        // Draw the student as a rectangle.
        shapeRenderer.rect(
            playerX,
            playerY,
            PLAYER_SIZE,
            PLAYER_SIZE
        );

        // Finish drawing.
        shapeRenderer.end();
    }

    // Keep the student inside the window.
    private void handleInput(float deltaTime) {
        float movement = PLAYER_SPEED * deltaTime;  // Calculate the distance to move based on speed and time.
        // Move up
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerX += movement;
        }
        // Move down
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerX -= movement;
        }
        // Move left
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerY += movement;
        }
        // Move right
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerY -= movement;
        }
    }

    // Prevent the student from leaving the window.
    private void keepPlayerInsideWindow() {

        playerX = MathUtils.clamp(
            playerX,
            0f,
            Gdx.graphics.getWidth() - PLAYER_SIZE
        );

        playerY = MathUtils.clamp(
            playerY,
            0f,
            Gdx.graphics.getHeight() - PLAYER_SIZE
        );
    }

    // Handle window resizing events.
    @Override
    public void resize(int width, int height) {
        // Update drawing coordinates if the window is resized.
        shapeRenderer.setProjectionMatrix(
            new Matrix4().setToOrtho2D(0, 0, width, height)
        );

        keepPlayerInsideWindow();
    }
    // Release resources when the game is closed.
    @Override
    public void dispose() {
        // Release the drawing resources.
        shapeRenderer.dispose();
    }
}