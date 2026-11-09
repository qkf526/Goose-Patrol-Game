
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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;

public class GoosePatrolGame extends ApplicationAdapter {

    // The student character
    private Player player;

    // Object used to draw textures and sprites.
    private SpriteBatch batch;

    // The texture for the player character to load the walk and hurt sprite sheet.
    private Texture playerWalkSheet;
    private Texture playerHurtSheet;

    // Stores all item textures for cleanup.
    private final Array<Texture> itemTextures = new Array<>();

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

    // The Health HUD.
    private Texture heartIcon;
    private BitmapFont healthFont;
    private Texture hudPanel;

    // The inventory controller for managing the player's inventory and item usage.
    private InventoryController inventoryController;

    // The GooseWaveManager for managing the waves of geese and game state.
    private GooseWaveManager gooseWaves;
    private GooseHUD gooseHUD;
    private Texture gooseTexture;
    private Texture seedTexture;

    // Called when the application is first created. Used to initialize game resources.
    @Override
    public void create() {
        
        batch = new SpriteBatch(); // Initialise the sprite batch for drawing textures.

        //HEART ICON AND HEALTH FONT
        heartIcon = new Texture(Gdx.files.internal("ui/heart_full.png")); // Load the heart image.
        heartIcon.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);  // Keep the pixel-art heart sharp.
        healthFont = new BitmapFont(); // Create the font for displaying health.
        healthFont.getData().setScale(1.5f); // Make the text a little larger.
        healthFont.setColor(Color.WHITE); // Set the text colour to white.
        hudPanel = new Texture(Gdx.files.internal("ui/hud_panel.png")); // Load the HUD panel image.
        hudPanel.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // Keep the pixel-art HUD panel sharp.

        // LOAD CHARACTER and ANIMATIONS
        playerWalkSheet = new Texture(Gdx.files.internal("character/student_walk.png")); // Load the sprite sheet for the player character.
        playerHurtSheet = new Texture(Gdx.files.internal("character/student_hurt.png")); // Load the sprite sheet for the player character's hurt animation.

        playerWalkSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // Set the texture filter to nearest for pixelated look.
        playerHurtSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // Set the texture filter to nearest for pixelated look.

        TextureRegion[][] walkFrames = TextureRegion.split(playerWalkSheet, 300, 300); // Split the walk sprite sheet into individual frames of size 16x16pixels.
        TextureRegion[][] hurtFrames = TextureRegion.split(playerHurtSheet, 300, 300); // Split the hurt sprite sheet into individual frames of size 16x16pixels.

        player = new Player(185f, 470f, walkFrames); // Create a new player character at the starting position (185, 470).
        player.setHurtAnimations(hurtFrames); // Set the hurt animations for the player character.


        // Register animation sets for all four items.
        // These frame sizes assume 300x300 cells.
        registerItem(ItemType.SEEDS, "seeds", 313, true);
        registerItem(ItemType.AIR_HORN, "air_horn", 313, true);
        registerItem(ItemType.BANDAGE, "bandage", 313, false);
        registerItem(ItemType.SAUSAGE_ROLL, "sausage_roll", 313, false);

        inventoryController = new InventoryController(player); // Create an inventory controller for the player.


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

        //LOAD GOOSE, WAVE MANAGER AND HUD
        gooseWaves = new GooseWaveManager(mapWidth,mapHeight );
        gooseHUD = new GooseHUD();

        // Load temporary goose graphics.
        gooseTexture = new Texture(Gdx.files.internal("character/goose.png"));
        seedTexture = new Texture(Gdx.files.internal("effects/seeds_patch.png"));

        // Preserve sharp pixel-art edges.
        gooseTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        seedTexture.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);

        System.out.println("Campus loaded: " + mapWidth + "x" + mapHeight + " pixels"); // Print the dimensions of the loaded campus map to the console.
    }

    // Called every frame to update the game state and render the screen.
    @Override
    public void render() {
        
        float deltaTime = Gdx.graphics.getDeltaTime(); // Calculate the time since the last frame.

        inventoryController.handleInput(); // Handle input for item selection and usage.


        // ENTER starts/resumes; ESC pauses/resumes.
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

            gooseWaves.togglePause();
        }

        // Restart a completed match.
        if (gooseWaves.isFinished() &&
            Gdx.input.isKeyJustPressed(Input.Keys.R)) {

            restartMatch();
        }

        // Only update gameplay while playing.
        if (gooseWaves.isPlaying()) {

            inventoryController.handleInput();

            player.update(deltaTime, collisions);

            // Which item was successfully used?
            ItemType used =
                inventoryController.updateAfterPlayer();

            if (used == ItemType.SEEDS ||
                used == ItemType.AIR_HORN) {

                // Calculate the player's ground position.
                com.badlogic.gdx.math.Rectangle feet =
                    player.getFeetBounds();

                float groundX =
                    feet.x + feet.width / 2f;

                float groundY =
                    feet.y + feet.height / 2f;

                if (used == ItemType.SEEDS) {
                    gooseWaves.useSeeds(groundX, groundY);
                } else {
                    gooseWaves.useAirHorn(groundX, groundY);
                }
            }

            // Update the clock, geese and waves.
            gooseWaves.update(
                deltaTime,
                player,
                collisions
            );
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


        batch.begin();

        com.badlogic.gdx.math.Rectangle feet =
            player.getFeetBounds();

        float playerGroundY =
            feet.y + feet.height / 2f;

        // Draw seeds on the ground.
        gooseWaves.drawSeedPatches(batch, seedTexture);

        // Draw geese behind the player.
        gooseWaves.drawGeeseBehindPlayer(
            batch,
            gooseTexture,
            playerGroundY
        );

        // Draw the student.
        player.draw(batch);

        // Draw geese in front of the player.
        gooseWaves.drawGeeseInFrontOfPlayer(
            batch,
            gooseTexture,
            playerGroundY
        );

        batch.end();

        drawHealthHUD(); // Draw the player's health on the HUD.

        inventoryController.draw();

        gooseHUD.draw(gooseWaves); // Draw the goose wave HUD.
    }


    private TextureRegion[][] loadItemFrames(String path, int frameSize) {

        // Load the image file.
        Texture texture = new Texture(Gdx.files.internal(path) ); 

        // Keep enlarged pixel art sharp.
        texture.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);

        // Remember this texture for disposal.
        itemTextures.add(texture);

        // Split into equally sized frames.
        return TextureRegion.split(texture, frameSize, frameSize);
    }

    // Register an item with its animation set and give it to the player.
    private void registerItem(ItemType item, String filenamePrefix, int frameSize, boolean hasMovingUseAnimation) {

        String basePath = "character/items/" + filenamePrefix;

        // Load holding frames.
        TextureRegion[][] holdingFrames = loadItemFrames(basePath + "_hold.png",frameSize);

        // Load standing-use frames.
        TextureRegion[][] standingFrames = loadItemFrames(basePath + "_use_still.png",frameSize);

        // Load moving-use frames when needed.
        TextureRegion[][] movingFrames = null;

        if (hasMovingUseAnimation) {
            movingFrames = loadItemFrames(basePath + "_use_walk.png",frameSize);
        }

        // Create the animation set.
        ItemAnimationSet animations = new ItemAnimationSet(holdingFrames, standingFrames, movingFrames);

        // Give the animations to our Player.
        player.registerItemAnimations(item,animations);
    }

    // Draw the player's health on the HUD and the item counter below it.
    private void drawHealthHUD() {

        // Position the HUD near the top-left corner of the camera's visible area.
        float hudX = camera.position.x - viewport.getWorldWidth() / 2f + 20f;
        float hudY = camera.position.y + viewport.getWorldHeight() / 2f - 48f;

        // Create the health text.
        String healthText = "HP: " + player.getHealth() + "/" + player.getMaxHealth();

        // Use the same camera as our campus.
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(hudPanel, hudX - 10f, hudY - 37f, 160, 100);
        batch.draw(heartIcon,hudX,hudY,28f,28f);
        healthFont.setColor(Color.WHITE);
        healthFont.draw(batch,healthText, hudX + 36f,hudY + 22f);

        // Draw the item counter below the health HUD.
        int itemsUsed = inventoryController.getInventory().getTotalItemsUsed();
        String itemsUsedText = "Items used: " + itemsUsed;
        healthFont.setColor(Color.WHITE);
        healthFont.draw(batch,itemsUsedText,hudX,hudY - 10f);

        batch.end();
    }

    private void restartMatch() {
        gooseWaves.reset();
        player.resetToStart(185f, 470f);

        // Recreate the initial stock and clear the items-used counter.
        inventoryController.dispose();
        inventoryController = new InventoryController(player);
}

    // Handle window resizing events.
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height); // Update the viewport with the new window size.
        inventoryController.resize(width, height); // Update the inventory controller with the new window size.
        if (gooseHUD != null) gooseHUD.resize(width, height);
    }

    // Release resources when the game is closed.
    @Override
    public void dispose() {
        
        batch.dispose(); // Release the sprite batch resources.
        playerWalkSheet.dispose(); // Release the player sprite sheet resources.
        playerHurtSheet.dispose(); // Release the hurt sprite sheet resources.

        mapRenderer.dispose(); // Release the map renderer resources.
        campusMap.dispose(); // Release the campus map resources.

        for (Texture texture : itemTextures) {
            texture.dispose();
        }

        heartIcon.dispose(); // Release the heart icon texture resources.
        healthFont.dispose(); // Release the font resources.
        hudPanel.dispose(); // Release the HUD panel texture resources.
        inventoryController.dispose(); // Release the inventory controller resources.

        gooseHUD.dispose();
        gooseTexture.dispose();
        seedTexture.dispose();
    }
}
