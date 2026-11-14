
package com.goosepatrol.game;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class GameplayScreen extends ScreenAdapter {


    private final GoosePatrolGame game;

    private boolean initialized = false;

    public GameplayScreen(GoosePatrolGame game) {
        this.game = game;
    }

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
    private Texture boltIcon;
    private BitmapFont energyFont;
    private ScreenViewport healthHudViewport;
    private ShapeRenderer healthHudShapes;

    // counts the number of events triggered
    private int eventsTriggered;

    // The inventory controller for managing the player's inventory and item usage.
    private InventoryController inventoryController;

    // The GooseWaveManager for managing the waves of geese and game state.
    private GooseWaveManager gooseWaves;
    private GooseHUD gooseHUD;
    private GooseAnimations gooseAnimations;
    private Texture seedTexture;

    // Handles Greggs interior and cashier interaction.
    private EventArea eventArea;

    // Handles the Long Boi statue interaction.
    private StatueEvent statueEvent;


    // Starting countdown duration.
    private static final float START_COUNTDOWN = 5f;

    // Is the countdown currently active?
    private boolean countdownActive = false;

    // Seconds remaining.
    private float countdownRemaining = 0f;

    // Graphics used to draw the countdown.
    private ScreenViewport countdownViewport;
    private BitmapFont countdownFont;
    private Texture countdownShade;

    private final GlyphLayout countdownLayout = new GlyphLayout();

    // Called when the application is first created. Used to initialize game resources.
    @Override
    public void show() {

        if (initialized) {
            return;
        }
        
        batch = new SpriteBatch(); // Initialise the sprite batch for drawing textures.


        // Create a viewport for our countdown overlay.
        countdownViewport = new ScreenViewport();

        countdownViewport.update(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            true
        );

        // Create the countdown font.
        countdownFont = new BitmapFont();

        // Create a tiny white texture.
        // We'll tint it black when drawing a dark overlay.
        Pixmap pixel = new Pixmap(
            1, 1, Pixmap.Format.RGBA8888
        );

        pixel.setColor(Color.WHITE);
        pixel.fill();

        countdownShade = new Texture(pixel);

        pixel.dispose();


        //HEART ICON AND ENERGY FONT
        boltIcon = new Texture(Gdx.files.internal("ui/bolt.png")); // Load the heart image.
        boltIcon.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);  // Keep the pixel-art heart sharp.
        energyFont = new BitmapFont(); // Create the font for displaying health.
        energyFont.getData().setScale(1.5f); // Make the text a little larger.
        energyFont.setColor(Color.WHITE); // Set the text colour to white.
        healthHudViewport = new ScreenViewport();
        healthHudViewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),true);
        healthHudShapes = new ShapeRenderer();


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
        gooseAnimations = new GooseAnimations();
        seedTexture = new Texture(Gdx.files.internal("effects/seeds_patch.png"));

        // Preserve sharp pixel-art edges.
        seedTexture.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);

        System.out.println("Campus loaded: " + mapWidth + "x" + mapHeight + " pixels"); // Print the dimensions of the loaded campus map to the console.

        eventArea = new EventArea();
        statueEvent = new StatueEvent();

        initialized = true;
    }

    // Called every frame to update the game state and render the screen.
    @Override
    public void render(float delta) {
        
        float deltaTime = delta; // Calculate the time since the last frame.

        // ESC opens the Pause Menu.
        if (Gdx.input.isKeyJustPressed(
            Input.Keys.ESCAPE)) {

            game.pauseGame();
            return;
        }

        // Remember if this frame began during countdown.
        boolean wasCountingDown = countdownActive;

        // Update our five-second countdown.
        if (countdownActive) {

            countdownRemaining = Math.max(
                0f,
                countdownRemaining - Math.max(0f, delta)
            );

            // Countdown finished.
            if (countdownRemaining <= 0f) {

                countdownActive = false;

                // Start the actual game only now.
                if (!gooseWaves.isPlaying() &&
                    !gooseWaves.isFinished()) {

                    gooseWaves.togglePause();
                }
            }
        }


        if (gooseWaves.isPlaying() && !wasCountingDown) {

            inventoryController.handleInput();

            // Use the correct collisions for the current map.
            CollisionMap activeCollisions =
                eventArea.isInEventArea()
                    ? eventArea.getShopCollisions()
                    : collisions;

            // Update the Player.
            player.update(deltaTime, activeCollisions);

            // Handle entering/leaving Greggs and cashier dialogue.
            eventArea.update(
                player,
                inventoryController.getInventory()
            );

            // Long Boi is only accessible outdoors.
            if (!eventArea.isInEventArea()) {
                statueEvent.update(player, gooseWaves);
            }

            // Count completed events.
            eventsTriggered =
                (eventArea.hasTriggered() ? 1 : 0)
                + (statueEvent.hasTriggered() ? 1 : 0);

            // Existing item animation completion.
            ItemType used =
                inventoryController.updateAfterPlayer();

            // Seeds and Air Horn only affect outdoor geese.
            if (!eventArea.isInEventArea() &&
                (used == ItemType.SEEDS ||
                used == ItemType.AIR_HORN)) {

                Rectangle feet = player.getFeetBounds();

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

            // Keep the timer running indoors,
            // but update geese only while outdoors.
            gooseWaves.update(
                deltaTime,
                player,
                collisions,
                eventArea.isInEventArea()
            );
        }


        // Check whether the match has been lost.
        if (gooseWaves.getState()
            == GooseWaveManager.GameState.LOST) {

            // Decide which defeat screen to show.
            GameOverScreen.Reason reason;

            if (player.isDefeated()) {

                // The player has run out of Energy.
                reason = GameOverScreen.Reason.OUT_OF_ENERGY;

            } else {

                // The five-minute time limit expired.
                reason = GameOverScreen.Reason.TIME_UP;
            }

            // Switch to the appropriate Game Over screen.
            game.showGameOver(reason);

            // Stop rendering the old GameplayScreen.
            return;
        }

        // Check whether the player has won.
        if (gooseWaves.getState()
            == GooseWaveManager.GameState.WON) {

            // Retrieve remaining gameplay time.
            int timeLeft =
                gooseWaves.getSecondsRemaining();

            // Calculate remaining Energy as a percentage.
            int energyLeft = Math.round(
                player.getHealth() * 100f
                / player.getMaxHealth()
            );

            // Get the number of items successfully used.
            int totalItemsUsed = inventoryController
                .getInventory()
                .getTotalItemsUsed();

            // Display the Victory screen.
            game.showVictory(
                timeLeft,
                energyLeft,
                totalItemsUsed,
                eventsTriggered
            );

            // Stop rendering the Gameplay screen.
            return;
        }

        int currentWidth = eventArea.isInEventArea()
            ? EventArea.SHOP_WIDTH
            : mapWidth;

        int currentHeight = eventArea.isInEventArea()
            ? EventArea.SHOP_HEIGHT
            : mapHeight;

        player.keepInsideMap(currentWidth, currentHeight);

        // MAKE CAMERA FOLLOW PLAYER

        if (eventArea.isInEventArea()) {

            // Show the entire Greggs interior.
            camera.zoom = 0.8f;

            // Keep the camera centred inside the shop.
            camera.position.set(
                EventArea.SHOP_WIDTH / 2f,
                EventArea.SHOP_HEIGHT / 2f,
                0f
            );

        } else {

            // Restore normal campus camera zoom.
            camera.zoom = 1f;

            float halfWidth =
                viewport.getWorldWidth() / 2f;

            float halfHeight =
                viewport.getWorldHeight() / 2f;

            float cameraX = MathUtils.clamp(
                player.getCentreX(),
                halfWidth,
                mapWidth - halfWidth
            );

            float cameraY = MathUtils.clamp(
                player.getCentreY(),
                halfHeight,
                mapHeight - halfHeight
            );

            camera.position.set(cameraX, cameraY, 0f);
        }

        camera.update();


        // DRAW CAMPUS MAP AND GREGGS MAP

        // Clear the screen before drawing either map.
        ScreenUtils.clear(0.12f, 0.16f, 0.20f, 1f);

        // Apply the gameplay viewport.
        viewport.apply();

        // Make sure the camera has its latest position and zoom.
        camera.update();

        // Select which map to draw.
        if (eventArea.isInEventArea()) {

            // The player is inside Greggs.
            eventArea.getShopRenderer().setView(camera);
            eventArea.getShopRenderer().render();

        } else {

            // The player is outside on campus.
            mapRenderer.setView(camera);
            mapRenderer.render();
        }

        // DRAW PLAYER

        batch.setProjectionMatrix(camera.combined); // Set the sprite batch to use the camera's combined projection and view matrix.

        batch.begin();

        Rectangle feet = player.getFeetBounds();

        float playerGroundY = feet.y + feet.height / 2f;

        // Draw geese and Seeds only on campus.
        if (!eventArea.isInEventArea()) {

            gooseWaves.drawSeedPatches(
                batch,
                seedTexture
            );

            gooseWaves.drawGeeseBehindPlayer(
                batch,
                gooseAnimations,
                playerGroundY
            );
        }

        // Draw the student in either environment.
        player.draw(batch);

        if (!eventArea.isInEventArea()) {

            gooseWaves.drawGeeseInFrontOfPlayer(
                batch,
                gooseAnimations,
                playerGroundY
            );
        }

        // Show Greggs entrance, exit and cashier dialogue.
        eventArea.drawDialog(batch);

        // Show Long Boi's interaction pop-up outdoors.
        if (!eventArea.isInEventArea()) {
            statueEvent.draw(batch);
        }

        batch.end();

        drawHealthHUD(); // Draw the player's health on the HUD.

        inventoryController.draw();

        gooseHUD.draw(gooseWaves); // Draw the goose wave HUD.

        drawCountdownOverlay(); //Draws the countdown
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


    private void drawHealthHUD() {

        healthHudViewport.apply();

        float width = healthHudViewport.getWorldWidth();
        float height = healthHudViewport.getWorldHeight();

        float scale = getUIScale(width, height);

        // Top-left panel dimensions.
        float panelX = 100f * scale;
        float panelWidth = 260f * scale;
        float panelHeight = 126f * scale;

        float panelY = height - 1f * scale - panelHeight;
        float panelTop = panelY + panelHeight;

        // Position the pause hint directly below the Energy panel.
        float hintX = panelX;
        float hintY = panelY - 42f * scale;


        // Use the HUD's camera for shapes.
        healthHudShapes.setProjectionMatrix(
            healthHudViewport.getCamera().combined
        );

        healthHudShapes.begin(ShapeRenderer.ShapeType.Filled);

        // Dark interior.
        healthHudShapes.setColor(
            0.07f, 0.11f, 0.16f, 1f
        );

        healthHudShapes.rect(
            panelX,
            panelY,
            panelWidth,
            panelHeight
        );

        // Small dark background for the pause instruction.
        healthHudShapes.rect(
            hintX,
            hintY,
            panelWidth,
            34f * scale
        );


        healthHudShapes.end();

        // Draw the heart and text above the rectangles.
        batch.setProjectionMatrix(
            healthHudViewport.getCamera().combined
        );

        batch.begin();
        batch.setColor(Color.WHITE);

        // Bolt icon.
        batch.draw(
            boltIcon,
            panelX + 14f * scale,
            panelTop - 50f * scale,
            25f * scale,
            25f * scale
        );

        // Display remaining Energy.
        energyFont.getData().setScale(2f * scale);
        energyFont.setColor(Color.WHITE);


        int energyPercentage = Math.round(
            player.getHealth() * 100f
            / player.getMaxHealth()
        );

        String energyText =
            "Energy: " + energyPercentage + "%";


        energyFont.draw(
            batch,
            energyText,
            panelX + 48f * scale,
            panelTop - 23f * scale
        );

        // Smaller text for the two counters.
        energyFont.getData().setScale(2f * scale);

        int itemsUsed = inventoryController
            .getInventory()
            .getTotalItemsUsed();

        energyFont.draw(
            batch,
            "Items used: " + itemsUsed,
            panelX + 15f * scale,
            panelTop - 60f * scale
        );

        energyFont.draw(
            batch,
            "Events: " + eventsTriggered + "/2",
            panelX + 15f * scale,
            panelTop - 99f * scale
        );

        // Pause instruction at the bottom-left.
        energyFont.getData().setScale(1f * scale);

        energyFont.draw(
            batch,
            "Press ESC to pause",
            hintX + 10f * scale,
            hintY + 23f * scale
        );

        batch.end();
    }


    public void startFromMainMenu() {

        // The screen has already been initialised
        // by setScreen() before this method runs.

        if (gooseWaves != null &&
            !gooseWaves.isPlaying() &&
            !gooseWaves.isFinished()) {

            // Start the timer and waves.
            gooseWaves.togglePause();
        }
    }

    public void beginStartCountdown() {

        // Reset the starting countdown.
        countdownRemaining = START_COUNTDOWN;

        // Prevent gameplay until countdown ends.
        countdownActive = true;
    }

    private void drawCountdownText(
        String text,
        float scale,
        float centreY,
        Color color
    ) {

        countdownFont.getData().setScale(scale);
        countdownFont.setColor(color);

        // Measure the displayed text.
        countdownLayout.setText(
            countdownFont,
            text
        );

        // Calculate the horizontal centre.
        float centreX =
            countdownViewport.getWorldWidth() / 2f;

        // Draw the text centred.
        countdownFont.draw(
            batch,
            countdownLayout,
            centreX - countdownLayout.width / 2f,
            centreY + countdownLayout.height / 2f
        );
    }


    private void drawCountdownOverlay() {

        if (!countdownActive) {
            return;
        }

        countdownViewport.apply();

        batch.setProjectionMatrix(
            countdownViewport.getCamera().combined
        );

        float width = countdownViewport.getWorldWidth();
        float height = countdownViewport.getWorldHeight();
        float scale = getUIScale(width, height);

        // Convert the remaining seconds into a number.
        int number = (int) Math.ceil(countdownRemaining);

        batch.begin();

        // Darken the gameplay background slightly.
        batch.setColor(0f, 0f, 0f, 0.55f);

        batch.draw(
            countdownShade,
            0f,
            0f,
            width,
            height
        );

        batch.setColor(Color.WHITE);


        // Heading above the number.
        drawCountdownText(
            "GET READY!",
            3f * scale,
            height / 2f + 120f * scale,
            Color.WHITE
        );

        // Large countdown number.
        drawCountdownText(
            String.valueOf(number),
            13f * scale,
            height / 2f - 15f * scale,
            Color.GOLD
        );

        batch.end();
    }

    private float getUIScale(float width, float height) {

        // Scale relative to a 1280x960 window.
        float scale = Math.min(
            width / 1280f,
            height / 960f
        );

        // Limit extreme resizing.
        return Math.max(
            0.6f,
            Math.min(scale, 2f)
        );
    }


    // Handle window resizing events.
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height); // Update the viewport with the new window size.
        inventoryController.resize(width, height); // Update the inventory controller with the new window size.
        if (gooseHUD != null) gooseHUD.resize(width, height);
        if (countdownViewport != null) {
            countdownViewport.update(
                width, height, true
            );
        }
        if (healthHudViewport != null) {
            healthHudViewport.update(width, height, true);
        }
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

        boltIcon.dispose(); // Release the heart icon texture resources.
        energyFont.dispose(); // Release the font resources.
        healthHudShapes.dispose();
        inventoryController.dispose(); // Release the inventory controller resources.

        gooseHUD.dispose();
        gooseAnimations.dispose();
        seedTexture.dispose();

        countdownFont.dispose();
        countdownShade.dispose();

        if (eventArea != null) {
            eventArea.dispose();
        }

        if (statueEvent != null) {
            statueEvent.dispose();
        }
    }
}
