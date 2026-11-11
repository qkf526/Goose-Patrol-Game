package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * The gameplay screen. Everything that used to live in GoosePatrolGame
 * (player, map, camera, inventory, geese, waves, HUDs) lives here now.
 *
 * ESC opens PauseScreen. When the wave manager reports a win or a loss the
 * game goes to GameOverScreen. Restarting means menu -> new GameScreen.
 */
public class GameScreen implements Screen {

    private final GoosePatrolGame game;   // shared batch + font, and setScreen()

    // Player
    private Player player;
    private Texture playerWalkSheet;
    private Texture playerHurtSheet;

    // Item textures, kept so they can be disposed
    private final Array<Texture> itemTextures = new Array<>();

    // Camera
    private static final float VIEW_WIDTH = 794f;
    private static final float VIEW_HEIGHT = 600f;
    private OrthographicCamera camera;
    private FitViewport viewport;

    // Map
    private TiledMap campusMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private CollisionMap collisions;
    private int mapWidth;
    private int mapHeight;

    // Health HUD
    private Texture heartIcon;
    private Texture hudPanel;
    private BitmapFont healthFont;

    // Inventory, geese and waves
    private InventoryController inventoryController;
    private GooseWaveManager gooseWaves;
    private GooseHUD gooseHUD;
    private GooseAnimations gooseAnimations;
    private Texture seedTexture;

    // ---------------------------------------------------------------------
    // Setup (this is the old create())
    // ---------------------------------------------------------------------
    public GameScreen(GoosePatrolGame game) {
        this.game = game;

        // Health HUD
        heartIcon = new Texture(Gdx.files.internal("ui/heart_full.png"));
        heartIcon.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        healthFont = new BitmapFont();
        healthFont.getData().setScale(1.5f);
        healthFont.setColor(Color.WHITE);
        hudPanel = new Texture(Gdx.files.internal("ui/hud_panel.png"));
        hudPanel.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Player and animations
        playerWalkSheet = new Texture(Gdx.files.internal("character/student_walk.png"));
        playerHurtSheet = new Texture(Gdx.files.internal("character/student_hurt.png"));
        playerWalkSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        playerHurtSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        TextureRegion[][] walkFrames = TextureRegion.split(playerWalkSheet, 300, 300);
        TextureRegion[][] hurtFrames = TextureRegion.split(playerHurtSheet, 300, 300);

        player = new Player(185f, 470f, walkFrames);
        player.setHurtAnimations(hurtFrames);

        // Item animations (must exist before the InventoryController equips slot 0)
        registerItem(ItemType.SEEDS, "seeds", 313, true);
        registerItem(ItemType.AIR_HORN, "air_horn", 313, true);
        registerItem(ItemType.BANDAGE, "bandage", 313, false);
        registerItem(ItemType.SAUSAGE_ROLL, "sausage_roll", 313, false);

        inventoryController = new InventoryController(player);

        // Map
        campusMap = new TmxMapLoader().load("maps/campus.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(campusMap);
        collisions = new CollisionMap(campusMap);

        int columns = campusMap.getProperties().get("width", Integer.class);
        int rows = campusMap.getProperties().get("height", Integer.class);
        int tileWidth = campusMap.getProperties().get("tilewidth", Integer.class);
        int tileHeight = campusMap.getProperties().get("tileheight", Integer.class);
        mapWidth = columns * tileWidth;
        mapHeight = rows * tileHeight;

        // Camera
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEW_WIDTH, VIEW_HEIGHT, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Geese, waves and HUD
        gooseWaves = new GooseWaveManager(mapWidth, mapHeight);
        gooseHUD = new GooseHUD();
        gooseAnimations = new GooseAnimations();
        seedTexture = new Texture(Gdx.files.internal("effects/seeds_patch.png"));
        seedTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // The wave manager starts in its own PAUSED state. Our PauseScreen
        // replaces that, so switch it to PLAYING straight away.
        gooseWaves.togglePause();
    }

    // ---------------------------------------------------------------------
    // Per-frame
    // ---------------------------------------------------------------------
    @Override
    public void render(float delta) {

        // PAUSE: hand over to PauseScreen. 'this' lets it return to this exact
        // game (same player, geese, timer) when Resume is pressed.
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
            return;
        }

        update(delta);

        // WIN / LOSE: leave for good, so dispose first.
        if (gooseWaves.isFinished()) {
            boolean won = gooseWaves.getState() == GooseWaveManager.GameState.WON;
            dispose();

            if (won) {
                game.setScreen(new VictoryScreen(game));
            } else {
                game.setScreen(new GameOverScreen(game));
            }
            return;
        }

        draw();
    }

    // Game logic only
    private void update(float delta) {

        if (gooseWaves.isPlaying()) {

            inventoryController.handleInput();
            player.update(delta, collisions);

            // Which item (if any) finished its use animation this frame?
            ItemType used = inventoryController.updateAfterPlayer();

            if (used == ItemType.SEEDS || used == ItemType.AIR_HORN) {
                Rectangle feet = player.getFeetBounds();
                float groundX = feet.x + feet.width / 2f;
                float groundY = feet.y + feet.height / 2f;

                if (used == ItemType.SEEDS) {
                    gooseWaves.useSeeds(groundX, groundY);
                } else {
                    gooseWaves.useAirHorn(groundX, groundY);
                }
            }

            gooseWaves.update(delta, player, collisions);
        }

        player.keepInsideMap(mapWidth, mapHeight);

        // Camera follows the player, clamped to the map
        float halfWidth = viewport.getWorldWidth() / 2f;
        float halfHeight = viewport.getWorldHeight() / 2f;
        float cameraX = MathUtils.clamp(player.getCentreX(), halfWidth, mapWidth - halfWidth);
        float cameraY = MathUtils.clamp(player.getCentreY(), halfHeight, mapHeight - halfHeight);
        camera.position.set(cameraX, cameraY, 0f);
        camera.update();
    }

    // Drawing only
    private void draw() {
        ScreenUtils.clear(0.12f, 0.16f, 0.20f, 1f);
        viewport.apply();

        mapRenderer.setView(camera);
        mapRenderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        Rectangle feet = player.getFeetBounds();
        float playerGroundY = feet.y + feet.height / 2f;

        gooseWaves.drawSeedPatches(game.batch, seedTexture);
        gooseWaves.drawGeeseBehindPlayer(game.batch, gooseAnimations, playerGroundY);
        player.draw(game.batch);
        gooseWaves.drawGeeseInFrontOfPlayer(game.batch, gooseAnimations, playerGroundY);

        game.batch.end();

        drawHealthHUD();
        inventoryController.draw();
        gooseHUD.draw(gooseWaves);
    }

    // Health and items-used counter, anchored to the top-left of the camera view
    private void drawHealthHUD() {
        float hudX = camera.position.x - viewport.getWorldWidth() / 2f + 20f;
        float hudY = camera.position.y + viewport.getWorldHeight() / 2f - 48f;

        String healthText = "HP: " + player.getHealth() + "/" + player.getMaxHealth();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(hudPanel, hudX - 10f, hudY - 37f, 160, 100);
        game.batch.draw(heartIcon, hudX, hudY, 28f, 28f);
        healthFont.setColor(Color.WHITE);
        healthFont.draw(game.batch, healthText, hudX + 36f, hudY + 22f);

        int itemsUsed = inventoryController.getInventory().getTotalItemsUsed();
        healthFont.draw(game.batch, "Items used: " + itemsUsed, hudX, hudY - 10f);
        game.batch.end();
    }

    // ---------------------------------------------------------------------
    // Item loading helpers
    // ---------------------------------------------------------------------
    private TextureRegion[][] loadItemFrames(String path, int frameSize) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        itemTextures.add(texture);   // remembered for disposal
        return TextureRegion.split(texture, frameSize, frameSize);
    }

    private void registerItem(ItemType item, String filenamePrefix, int frameSize,
                              boolean hasMovingUseAnimation) {

        String basePath = "character/items/" + filenamePrefix;

        TextureRegion[][] holdingFrames = loadItemFrames(basePath + "_hold.png", frameSize);
        TextureRegion[][] standingFrames = loadItemFrames(basePath + "_use_still.png", frameSize);

        TextureRegion[][] movingFrames = null;
        if (hasMovingUseAnimation) {
            movingFrames = loadItemFrames(basePath + "_use_walk.png", frameSize);
        }

        player.registerItemAnimations(item,
            new ItemAnimationSet(holdingFrames, standingFrames, movingFrames));
    }

    // ---------------------------------------------------------------------
    // Screen lifecycle
    // ---------------------------------------------------------------------
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        inventoryController.resize(width, height);
        gooseHUD.resize(width, height);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}

    // Runs whenever another screen takes over (pause, game over, ...).
    // It must NOT dispose anything, or Resume would crash. It only puts the
    // shared batch and GL viewport back to plain full-window settings, because
    // the camera and viewports used above would otherwise leak into the next
    // screen and misplace its text.
    @Override
    public void hide() {
        Gdx.gl.glViewport(0, 0,
            Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        game.batch.setProjectionMatrix(new Matrix4().setToOrtho2D(
            0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    // Call ONLY when leaving the game for good (game over, quit to menu).
    // Frees everything this screen created. game.batch and game.font are NOT
    // freed here; they belong to GoosePatrolGame.
    @Override
    public void dispose() {
        playerWalkSheet.dispose();
        playerHurtSheet.dispose();
        for (Texture texture : itemTextures) {
            texture.dispose();
        }
        heartIcon.dispose();
        hudPanel.dispose();
        seedTexture.dispose();
        healthFont.dispose();

        gooseAnimations.dispose();
        gooseHUD.dispose();
        inventoryController.dispose();

        mapRenderer.dispose();
        campusMap.dispose();
    }
}
