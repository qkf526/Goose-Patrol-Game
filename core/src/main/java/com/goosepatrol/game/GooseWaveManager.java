package com.goosepatrol.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/** Four waves, a 5-minute timer, item responses, and win/loss state. */
public class GooseWaveManager {

    public enum GameState { PAUSED, PLAYING, WON, LOST }

    public static final float GAME_DURATION = 300f; // Five minutes.
    public static final int TOTAL_WAVES = 4;

    private static final int[] WAVE_SIZES = {3, 5, 7, 10};
    public static final int TOTAL_GEESE = 25;
    public static final float BETWEEN_WAVES = 10f;

    private static final int SEEDS_MAX_TARGETS = 2;
    private static final float SEEDS_RANGE = 240f;
    private static final int HORN_MAX_TARGETS = 3;
    private static final float HORN_RANGE = 185f;

    // The Long Boi statue scares away every goose within this distance of the statue.
    private static final float STATUE_RANGE = 450f;


    // Candidate positions chosen on open ground/pathways of the campus map.
    // Coordinates are libGDX world coordinates (bottom-left origin).
    private static final float[][] SPAWN_POINTS = {
        {420f, 510f}, {1080f, 510f}, {705f, 725f}, {555f, 360f},
        {360f, 600f}, {1030f, 630f}, {775f, 445f}, {900f, 520f},
        {520f, 675f}, {1160f, 550f}, {600f, 410f}, {850f, 665f},
        {470f, 510f}, {1120f, 600f}, {720f, 700f}, {570f, 385f},
        {450f, 650f}, {990f, 590f}, {810f, 455f}, {940f, 540f},
        {540f, 705f}, {1180f, 500f}, {630f, 440f}, {860f, 690f},
        {420f, 440f}, {1020f, 470f}, {760f, 640f}, {390f, 710f},
        {1130f, 700f}, {950f, 400f}, {620f, 600f}, {460f, 390f}
    };

    private static final class SeedPatch {
        float x, y, secondsRemaining;
        SeedPatch(float x, float y) {
            this.x = x;
            this.y = y;
            secondsRemaining = 6f;
        }
    }

    private final Array<Goose> geese = new Array<>();
    private final Array<SeedPatch> seedPatches = new Array<>();
    private final float mapWidth;
    private final float mapHeight;
    private GameState state = GameState.PAUSED;
    private float elapsed = 0f;
    private int nextWave = 0;
    private int dispersed = 0;
    private float nextWaveCountdown = -1f; // -1 means there is no inter-wave countdown.

    public GooseWaveManager(float mapWidth, float mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public GameState getState() { return state; }
    public boolean isPlaying() { return state == GameState.PLAYING; }
    public boolean isFinished() { return state == GameState.WON || state == GameState.LOST; }
    public int getCurrentWave() { return nextWave; }
    public int getActiveGeese() { return geese.size; }
    public int getDispersedCount() { return dispersed; }
    public float getElapsedSeconds() { return elapsed; }
    public int getSecondsRemaining() { return Math.max(0, (int) Math.ceil(GAME_DURATION - elapsed)); }
    public boolean isBetweenWaves() {
    return nextWaveCountdown >= 0f;
    }
    public int getNextWaveSeconds() {
        if (!isBetweenWaves()) {
            return 0;
        }

    return (int) Math.ceil(nextWaveCountdown);
}

    public void togglePause() {
        if (state == GameState.PAUSED) state = GameState.PLAYING;
        else if (state == GameState.PLAYING) state = GameState.PAUSED;
    }

    /** Used after the player loses or wins. New game starts paused as required. */
    public void reset() {
        geese.clear();
        seedPatches.clear();
        elapsed = 0f;
        nextWave = 0;
        dispersed = 0;
        state = GameState.PAUSED;
        nextWaveCountdown = -1f;
    }

    // Method to update the game state and implement wave cooldown countdown
    public void update( float delta, Player player, CollisionMap collisions ) {
        update(delta, player, collisions, false); // The student is outside.
    }

    // Update the game state. While the student is indoors (for example in Greggs),
    // only the clock and the break between waves keep running: geese wait outside and no new wave spawns.
    public void update( float delta, Player player, CollisionMap collisions, boolean playerIndoors ) {

        if (state != GameState.PLAYING) {
            return;
        }

        float dt = Math.max(0f, delta);

        // Update the overall five-minute timer.
        elapsed += dt;

        // Check whether the game is over.
        if (elapsed >= GAME_DURATION || player.isDefeated()) {
            state = GameState.LOST;
            return;
        }

        // While the student is indoors, the geese cannot follow them and no wave spawns.
        if (playerIndoors) {

            // Keep the break between waves counting down; the next wave spawns once the student is back outside.
            if (isBetweenWaves()) {
                nextWaveCountdown = Math.max(0f, nextWaveCountdown - dt);
            }

            return;
        }

        // Start the first wave.
        if (nextWave == 0) {

            spawnWave(nextWave, player, collisions);
            nextWave++;

        } else if (isBetweenWaves()) {

            // Reduce the inter-wave cooldown.
            nextWaveCountdown = Math.max( 0f, nextWaveCountdown - dt);

            // Start the next wave when cooldown ends.
            if (nextWaveCountdown <= 0f) {
                spawnWave(nextWave, player, collisions);
                nextWave++;
                nextWaveCountdown = -1f;
            }
        }

        // Update each goose.
        for (int i = geese.size - 1; i >= 0; i--) {

            Goose goose = geese.get(i);
            goose.update( dt, player, collisions, mapWidth,mapHeight);

            // Remove geese that have finished fleeing.
            if (goose.isGone()) {
                geese.removeIndex(i);
                dispersed++;
            }
        }

        // Update existing seed patches.
        for (int i = seedPatches.size - 1; i >= 0; i--) {

            SeedPatch patch = seedPatches.get(i);
            patch.secondsRemaining -= dt;

            if (patch.secondsRemaining <= 0f) {
                seedPatches.removeIndex(i);
            }
        }

        // Check for defeat after goose attacks.
        if (player.isDefeated()) {

            state = GameState.LOST;

        } else if ( nextWave == TOTAL_WAVES && geese.size == 0) {

            // All four waves have been completed.
            state = GameState.WON;

        } else if ( geese.size == 0 && nextWave < TOTAL_WAVES && !isBetweenWaves()) {

            // Current wave is cleared.
            // Begin a ten-second break.
            nextWaveCountdown = BETWEEN_WAVES;
        }
    }

    //Method to spawn waves of geese
    private void spawnWave(int waveNumber, Player player, CollisionMap collisions) {
        float playerX = player.getCentreX();
        float playerY = player.getCentreY();
        int spawned = 0;
        for (int i = 0; i < WAVE_SIZES[waveNumber]; i++) {
            // Rotate spawn points between waves to vary the approach directions.
            int initialIndex = (waveNumber * 3 + i * 2) % SPAWN_POINTS.length;
            for (int tryIndex = 0; tryIndex < SPAWN_POINTS.length; tryIndex++) {
                float[] point = SPAWN_POINTS[(initialIndex + tryIndex) % SPAWN_POINTS.length];
                if (isValidSpawn(point[0], point[1], playerX, playerY, collisions) && !isSpawnOccupied(point[0], point[1])) {
                    geese.add(new Goose(point[0], point[1]));
                    spawned++;
                    break;
                }
            }
        }
        // Warn if all geese couldn't spawn: map collisions may have changed.
        System.out.println("Wave " + (waveNumber + 1) + " spawned " + spawned + " geese.");
    }

    private boolean isSpawnOccupied(float x, float y) {
        for (Goose goose : geese) {
            float dx = goose.getX() - x;
            float dy = goose.getY() - y;
            if (dx * dx + dy * dy < 32f * 32f) return true;
        }
        return false;
    }

    // Check if spawn location is valid
    private boolean isValidSpawn(float x, float y, float playerX, float playerY,CollisionMap collisions) {
        if (x < 20f || y < 20f || x > mapWidth - 20f || y > mapHeight - 20f) return false;
        float dx = x - playerX;
        float dy = y - playerY;
        if (dx * dx + dy * dy < 150f * 150f) return false;
        return !collisions.isBlocked(new Rectangle(x - 11f, y - 5f, 22f, 10f));
    }

    /** Called once when a Seeds-use animation completes successfully. */
    public int useSeeds(float playerGroundX, float playerGroundY) {
        if (state != GameState.PLAYING) return 0;
        // Show one seed patch where the student scatters seeds.
        float seedX = playerGroundX + 24f;
        float seedY = playerGroundY;
        seedPatches.add(new SeedPatch(seedX, seedY));
        return affectClosest(playerGroundX, playerGroundY, SEEDS_RANGE, SEEDS_MAX_TARGETS, false, seedX, seedY);
    }

    /** Called once when an Air Horn-use animation completes successfully. */
    public int useAirHorn(float playerGroundX, float playerGroundY) {
        if (state != GameState.PLAYING) return 0;
        return affectClosest(playerGroundX, playerGroundY, HORN_RANGE, HORN_MAX_TARGETS, true, playerGroundX, playerGroundY);
    }

    /** Called once when the student uses the Long Boi statue: frightens every goose within the statue's range. */
    public int useStatueHonk(float statueX, float statueY) {
        if (state != GameState.PLAYING) return 0;
        return affectClosest(statueX, statueY, STATUE_RANGE, Integer.MAX_VALUE, true, statueX, statueY);
    }

    //Method to make sure the closest geese to an item are the one affected, so one use of the item doesn't disperse all geese of the wave
    private int affectClosest(float playerX, float playerY, float radius,int limit, boolean horn, float seedX, float seedY) {
        int affected = 0;
        Array<Goose> chosen = new Array<>();
        float radius2 = radius * radius;

        while (affected < limit) {
            Goose closest = null;
            float bestDistance2 = radius2;
            for (Goose goose : geese) {
                if (!goose.canBeAffectedByItem() || chosen.contains(goose, true)) continue;
                float dx = goose.getX() - playerX;
                float dy = goose.getY() - playerY;
                float distance2 = dx * dx + dy * dy;
                if (distance2 <= bestDistance2) {
                    bestDistance2 = distance2;
                    closest = goose;
                }
            }
            if (closest == null) break;
            chosen.add(closest);
            if (horn) closest.frighten(playerX, playerY);
            else closest.attractToSeeds(seedX, seedY);
            affected++;
        }
        System.out.println((horn ? "Air Horn" : "Seeds") + " affected " + affected + " geese.");
        return affected;
    }

    /** Draw seed patches before actors, so geese can stand on them. */
    public void drawSeedPatches(SpriteBatch batch, Texture seedTexture) {
        for (SeedPatch patch : seedPatches) {
            batch.draw(seedTexture, patch.x - 8f, patch.y - 7f, 24f, 16f);
        }
    }

    /** Basic depth ordering relative to the player's ground position. */
    public void drawGeeseBehindPlayer(SpriteBatch batch, GooseAnimations animations, float playerGroundY) {
        for (Goose goose : geese) {
            if (goose.getY() >= playerGroundY) goose.draw(batch, animations);
        }
    }
    //Method to draw geese in front of player for depth ordering
    public void drawGeeseInFrontOfPlayer(SpriteBatch batch, GooseAnimations animations, float playerGroundY) {
        for (Goose goose : geese) {
            if (goose.getY() < playerGroundY) goose.draw(batch, animations);
        }
    }
}
