
package com.goosepatrol.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class GoosePatrolGame extends ApplicationAdapter {

    // The student character
    private Player player;

    // Object used to draw textures and sprites.
    private SpriteBatch batch;

    // The texture for the player character to load the sprite sheet.
    private Texture playerSheet;

    // The texture region for the player character to select a specific frame.
    private TextureRegion playerFrame;

    @Override
    public void create() {

        batch = new SpriteBatch(); // Initialise the sprite batch for drawing textures.

        playerSheet = new Texture(Gdx.files.internal("student_sheet.png")); // Load the sprite sheet for the player character.

        playerSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // Set the texture filter to nearest for pixelated look.

        playerFrame = new TextureRegion(playerSheet, 0, 0, 16, 16); // Create a texture region to select the first frame of the sprite sheet.

        player = new Player(150, 120); // Create a new player character at the starting position (150, 120).

        System.out.println("Goose Patrol has started!");
    }

    // Called every frame to update the game state and render the screen.
    @Override
    public void render() {
        
        float deltaTime = Gdx.graphics.getDeltaTime(); // Calculate the time since the last frame.

        player.updateMovement(deltaTime); // Update the student's movement based on input.

        ScreenUtils.clear(0.4f, 0.7f, 0.4f, 1f); // Draw a green background.

        batch.begin(); // Begin drawing textures.

        player.draw(batch, playerFrame); // Draw the player character using the sprite batch and the selected texture region.

        batch.end(); // End drawing textures.
    }


    // Handle window resizing events.
    @Override
    public void resize(int width, int height) {
        // Update drawing coordinates if the window is resized.
        batch.setProjectionMatrix(
            new Matrix4().setToOrtho2D(0, 0, width, height)
        );
        
        player.keepInsideWindow(width, height); // Ensure the player stays within the new window size.
    }

    // Release resources when the game is closed.
    @Override
    public void dispose() {
        
        batch.dispose(); // Release the sprite batch resources.
        playerSheet.dispose(); // Release the player sprite sheet resources.
    }
}
