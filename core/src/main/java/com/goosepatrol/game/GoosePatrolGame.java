
package com.goosepatrol.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;


public class GoosePatrolGame extends ApplicationAdapter {

    // Object used to draw simple shapes.
    private ShapeRenderer shapeRenderer;

    // The student character
    private Player player;

    @Override
    public void create() {
        // Initialise the shape drawing object.
        shapeRenderer = new ShapeRenderer();

        player = new Player(150f, 120f);

        System.out.println("Goose Patrol has started!");
    }

    // Called every frame to update the game state and render the screen.
    @Override
    public void render() {
        // Calculate the time since the last frame.
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Update the student's movement based on input.
        player.updateMovement(deltaTime);

        // Draw a green background.
        ScreenUtils.clear(0.4f, 0.7f, 0.4f, 1f);

        // Begin drawing filled shapes.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the student character.
        player.draw(shapeRenderer);

        // Finish drawing.
        shapeRenderer.end();
    }


    // Handle window resizing events.
    @Override
    public void resize(int width, int height) {
        // Update drawing coordinates if the window is resized.
        shapeRenderer.setProjectionMatrix(
            new Matrix4().setToOrtho2D(0, 0, width, height)
        );
        // Ensure the player stays within the new window size.
        player.keepInsideWindow(width, height);
    }

    // Release resources when the game is closed.
    @Override
    public void dispose() {
        // Release the drawing resources.
        shapeRenderer.dispose();
    }
}
