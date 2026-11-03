
package com.goosepatrol.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class GoosePatrolGame extends ApplicationAdapter {

    // The student character
    private Player player;

    // Object used to draw textures and sprites.
    private SpriteBatch batch;

    // The texture for the player character to load the sprite sheet.
    private Texture playerSheet;

    // The width and height of the camera view used to view the game world.
    private static final float VIEW_WIDTH = 794f; 
    private static final float VIEW_HEIGHT = 600f; 

    // The campus map and its renderer for displaying the tiled map.
    private TiledMap campusMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    // The camera and viewport for controlling the view of the game world.
    private OrthographicCamera camera;
    private FitViewport viewport;

    // The width and height of entire map.
    private int mapWidth;
    private int mapHeight;

    // Called when the application is first created. Used to initialize game resources.
    @Override
    public void create() {

        // LOAD CHARACTER
        batch = new SpriteBatch(); // Initialise the sprite batch for drawing textures.

        playerSheet = new Texture(Gdx.files.internal("student_sheet.png")); // Load the sprite sheet for the player character.

        playerSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // Set the texture filter to nearest for pixelated look.

        TextureRegion[][] frames = TextureRegion.split(playerSheet, 16, 16); // Split the sprite sheet into individual frames of size 16x16pixels.

        player = new Player(185f, 470f, frames); // Create a new player character at the starting position (185, 470).

        // LOAD MAP
        campusMap = new TmxMapLoader().load("maps/campus.tmx"); // Load the campus map from the TMX file.
        mapRenderer = new OrthogonalTiledMapRenderer(campusMap); // Create a renderer for the campus map.

        // READ MAP DIMENSIONS
        int columns = campusMap.getProperties().get("width", Integer.class); // Get the number of columns in the map.
        int rows = campusMap.getProperties().get("height", Integer.class); // Get the number of rows in the map.
        int tileWidth = campusMap.getProperties().get("tilewidth", Integer.class); // Get the width of each tile in pixels.
        int tileHeight = campusMap.getProperties().get("tileheight", Integer.class); // Get the height of each tile in pixels.

        // Calculate the total width and height of the map in pixels.
        mapWidth = columns * tileWidth;
        mapHeight = rows * tileHeight;

        // SETUP CAMERA
        camera = new OrthographicCamera(); // Create a new orthographic camera for 2D rendering.
        viewport = new FitViewport(VIEW_WIDTH, VIEW_HEIGHT, camera); // Create a viewport to maintain aspect ratio and fit the camera view to the window size.
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Update the viewport with the current window size.

        System.out.println("Campus loaded: " + mapWidth + "x" + mapHeight + " pixels"); // Print the dimensions of the loaded campus map to the console.
    }

    // Called every frame to update the game state and render the screen.
    @Override
    public void render() {
        
        float deltaTime = Gdx.graphics.getDeltaTime(); // Calculate the time since the last frame.

        player.updateMovement(deltaTime); // Update the student's movement based on input.

        player.keepInsideMap(mapWidth, mapHeight); // Ensure the player stays within the map boundaries.

        // MAKE CAMERA FOLLOW PLAYER
        float halfWidth = viewport.getWorldWidth() / 2f; // Calculate half the width of the camera view.
        float halfHeight = viewport.getWorldHeight() / 2f; // Calculate half the height of the camera view.

        float cameraX = MathUtils.clamp(player.getCentreX(), halfWidth, mapWidth - halfWidth); // Clamp the camera's x-coordinate to follow the player while staying within map bounds.
        float cameraY = MathUtils.clamp(player.getCentreY(), halfHeight, mapHeight - halfHeight); // Clamp the camera's y-coordinate to follow the player while staying within map bounds.

        camera.position.set(cameraX, cameraY, 0f); // Set the camera's position to follow the player.
        camera.update(); // Update the camera to apply the new position.

        // DRAW CAMPUS MAP
        ScreenUtils.clear(0.12f, 0.16f, 0.20f, 1f); // Clear the screen with a dark blue color before drawing the map and player.
        viewport.apply(); // Apply the viewport to ensure the camera's view is correctly set.

        mapRenderer.setView(camera); // Set the map renderer to use the camera's view.
        mapRenderer.render(); // Render the campus map.

        // DRAW PLAYER

        batch.setProjectionMatrix(camera.combined); // Set the sprite batch to use the camera's combined projection and view matrix.

        batch.begin(); // Begin drawing textures.

        player.draw(batch); // Draw the player character using the sprite batch.

        batch.end(); // End drawing textures.
    }


    // Handle window resizing events.
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height); // Update the viewport with the new window size.
    }

    // Release resources when the game is closed.
    @Override
    public void dispose() {
        
        batch.dispose(); // Release the sprite batch resources.
        playerSheet.dispose(); // Release the player sprite sheet resources.

        mapRenderer.dispose(); // Release the map renderer resources.
        campusMap.dispose(); // Release the campus map resources.
    }
}
