
package com.goosepatrol.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GoosePatrolGame extends ApplicationAdapter {

    // The student character
    private Player player;

    // The player's inventory
    private Inventory inventory;
    private InventoryUI inventoryUI;

    // Object used to draw textures and sprites.
    private SpriteBatch batch;

    // The texture for the player character to load the walk and hurt sprite sheet.
    private Texture playerWalkSheet;
    private Texture playerHurtSheet;

    // The width and height of the camera view used to view the game world.
    private static final float VIEW_WIDTH = 794f; 
    private static final float VIEW_HEIGHT = 600f; 

    // The campus map and its renderer for displaying the tiled map and the collision map for detecting obstacles.
    private TiledMap campusMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private CollisionMap collisions;

    // The camera and viewport for controlling the view of the game world.
    private OrthographicCamera camera;
    private FitViewport viewport;

    // The width and height of entire map.
    private int mapWidth;
    private int mapHeight;

    // Called when the application is first created. Used to initialize game resources.
    @Override
    public void create() {

        // LOAD CHARACTER and ANIMATIONS
        batch = new SpriteBatch(); // Initialise the sprite batch for drawing textures.

        playerWalkSheet = new Texture(Gdx.files.internal("character/student_walk.png")); // Load the sprite sheet for the player character.
        playerHurtSheet = new Texture(Gdx.files.internal("character/student_hurt.png")); // Load the sprite sheet for the player character's hurt animation.

        playerWalkSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // Set the texture filter to nearest for pixelated look.
        playerHurtSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // Set the texture filter to nearest for pixelated look.

        TextureRegion[][] walkFrames = TextureRegion.split(playerWalkSheet, 300, 300); // Split the walk sprite sheet into individual frames of size 16x16pixels.
        TextureRegion[][] hurtFrames = TextureRegion.split(playerHurtSheet, 300, 300); // Split the hurt sprite sheet into individual frames of size 16x16pixels.

        player = new Player(185f, 470f, walkFrames); // Create a new player character at the starting position (185, 470).
        player.setHurtAnimations(hurtFrames); // Set the hurt animations for the player character.

        // Create the player's inventory
        inventory = new Inventory();
        //Add starting items
        inventory.addItem(new Seeds(), 5);
        inventory.addItem(new Flashlight(), 1);
        inventory.addItem(new Bandage(), 2);
        inventory.addItem(new AirHorn(), 1);
        inventoryUI = new InventoryUI(inventory);
        // Use the mouse wheel to switch inventory slots.
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean scrolled(float amountX, float amountY) {

                if (amountY > 0) {
                    inventory.scrollSelection(1);
                } else if (amountY < 0) {
                    inventory.scrollSelection(-1);
                }

                return true;
            }
        });

        // LOAD MAP
        campusMap = new TmxMapLoader().load("maps/campus.tmx"); // Load the campus map from the TMX file.
        mapRenderer = new OrthogonalTiledMapRenderer(campusMap); // Create a renderer for the campus map.
        collisions = new CollisionMap(campusMap); // Create a collision map for detecting obstacles in the campus map.
        System.out.println("Collision zones loaded: " + collisions.getObstacleCount()); // Print the number of collision zones loaded from the map to the console.

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

        // Select inventory slots using number keys 1-4.

        // Select inventory slots using number keys 1-4.
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            inventory.selectSlot(0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            inventory.selectSlot(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            inventory.selectSlot(2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            inventory.selectSlot(3);
        }

        // Use the currently selected inventory item.
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {

            InventorySlot selectedSlot = inventory.getSelectedSlot();

            if (selectedSlot != null) {

                String itemName = selectedSlot.getItem().getName();

                if (itemName.equals("Bandage")) {

                    boolean used = inventory.useBandage(player);

                    if (used) {
                        System.out.println("Selected Bandage used!");
                    } else {
                        System.out.println("Cannot use Bandage.");
                    }

                    System.out.println("Current health: " + player.getHealth());
                    System.out.println("Bandages remaining: "
                        + inventory.getQuantity("Bandage"));

                } else if (selectedSlot.getItem() instanceof Flashlight){
                    Flashlight flashlight = (Flashlight) selectedSlot.getItem();
                    flashlight.toggle();
                    if (flashlight.isOn()){
                        System.out.println("Flashlight ON");
                    }else{
                        System.out.println("Flashlight OFF");
                    }

} else if (selectedSlot.getItem() instanceof Seeds) {

    Seeds seeds = (Seeds) selectedSlot.getItem();

    if (selectedSlot.getQuantity() > 0) {

        seeds.use();

        inventory.useItem("Seeds");

        System.out.println("Seeds remaining: "
            + inventory.getQuantity("Seeds"));

    } else {
        System.out.println("No Seeds remaining!");
    }

} else {
    System.out.println(itemName
        + " is not connected to gameplay yet.");
}

            }
        }



        player.updateMovement(deltaTime, collisions); // Update the student's movement based on input.


        // TEMPORARY TEST: Simulate a goose peck.
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {

            // Try to apply one point of damage.
            boolean damageApplied = player.takeDamage(1);

            if (damageApplied) {

                // Simulate a goose standing left of the player.
                float attackerX = player.getCentreX() - 48f;
                float attackerY = player.getCentreY();

                // Push the student away from the simulated goose.
                player.applyKnockbackFrom(attackerX, attackerY);
                System.out.println("Goose peck! HP: " + player.getHealth());
            }
        }


        // TEMPORARY TEST: Heal the player.
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {

            player.heal(1);

            System.out.println("Healed by 1 point. Current health: " + player.getHealth());
        }

        // BANDAGE TEMPORARY HEAL TEMPORARY TEST: Heal the player using bandage.
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {

            boolean used = inventory.useBandage(player);

        if (used) {
            System.out.println("Bandage used successfully!");
        } else {
            System.out.println("Cannot use Bandage.");
        }

        System.out.println("Current health: " + player.getHealth());
        System.out.println("Bandages remaining: "
            + inventory.getQuantity("Bandage"));
}



        // SAUSAGE ROLL HEAL TEMPORARY TEST: Heal the player using sausage roll.
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {

            player.resetHealth();
            System.out.println("Sausage roll consumed! Health restored to maximum.");
            System.out.println("Current health: " + player.getHealth());
        }

        // TEMPORARY TEST: Restart after defeat.
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {

            if (!player.isDefeated()) {
                System.out.println("Player is not defeated. Can't reset health.");
            } else {
                player.resetHealth();
                System.out.println("Player health reset!");
            }
        }

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

        inventoryUI.draw(); // Draw the inventory UI on the top of the campus map.
    }


    // Handle window resizing events.
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height); // Update the viewport with the new window size.
        inventoryUI.resize(width, height);
    }

    // Release resources when the game is closed.
    @Override
    public void dispose() {
        
        batch.dispose(); // Release the sprite batch resources.
        playerWalkSheet.dispose(); // Release the player sprite sheet resources.
        playerHurtSheet.dispose(); // Release the hurt sprite sheet resources.

        mapRenderer.dispose(); // Release the map renderer resources.
        campusMap.dispose(); // Release the campus map resources.
        inventoryUI.dispose();
    }
}
