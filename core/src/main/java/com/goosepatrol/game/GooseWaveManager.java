package com.goosepatrol.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/** Four waves, a 5-minute timer, item responses, and win/loss state. */
public class GooseWaveManager {

    public enum GameState { PAUSED, PLAYING, WON, LOST }

    public static final float GAME_DURATION = 300f; // Five minutes.


    // Number of geese in each wave.
    private static final int[] WAVE_SIZES = {
        5, 7, 10, 13, 15
    };

    // Automatically determine the number of waves.
    public static final int TOTAL_WAVES = WAVE_SIZES.length;

    // Automatically calculate the total geese.
    public static final int TOTAL_GEESE = calculateTotalGeese();

    private static int calculateTotalGeese() {

        int total = 0;

        for (int amount : WAVE_SIZES) {
            total += amount;
        }

        return total;
    }

    public static final float BETWEEN_WAVES = 10f;

    private static final int SEEDS_MAX_TARGETS = 2;
    private static final float SEEDS_RANGE = 240f;
    private static final int HORN_MAX_TARGETS = 3;
    private static final float HORN_RANGE = 185f;

    private static final float STATUE_RANGE = 450f; //For Long Boi statue event

    // Candidate spawn positions around the lake.
    //
    // These are WORLD coordinates, measured from
    // the bottom-left of the campus map.
    //
    // The positions are on the northern lake shore
    // and nearby grass/path areas, not in the water.

    private static final float[][] SPAWN_POINTS = {

        // Northern shoreline.
        {350f, 335f},
        {430f, 335f},
        {530f, 335f},
        {630f, 335f},
        {730f, 335f},
        {830f, 335f},
        {930f, 335f},
        {1030f, 335f},
        {1130f, 335f},

        // Slightly further inland.
        {380f, 355f},
        {480f, 355f},
        {580f, 355f},
        {680f, 355f},
        {780f, 355f},
        {880f, 355f},
        {980f, 355f},
        {1080f, 355f},

        // Grass farther from the shoreline.
        {350f, 415f},
        {530f, 415f},
        {710f, 415f},
        {880f, 415f},
        {1080f, 415f},

        // Path beside the lake.
        {425f, 300f},
        {520f, 300f},
        {620f, 300f},
        {720f, 300f},
        {820f, 300f},
        {920f, 300f},
        {1020f, 300f}
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

    public GameState getState() { return state; } //get current playing state
    public boolean isPlaying() { return state == GameState.PLAYING; } //Check if game is being actively played
    public boolean isFinished() { return state == GameState.WON || state == GameState.LOST; } //Check if the game has finished
    public int getCurrentWave() { return nextWave; } //Get the current wave number
    public int getActiveGeese() { return geese.size; } //Get the current number of active geese
    public int getDispersedCount() { return dispersed; } //Get the current number of dispersed geese
    public float getElapsedSeconds() { return elapsed; } //Get the time in seconds that has passed
    public int getSecondsRemaining() { return Math.max(0, (int) Math.ceil(GAME_DURATION - elapsed)); } //Get the number of seconds remaning
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

    public void update(
        float delta,
        Player player,
        CollisionMap collisions
    ) {
        update(delta, player, collisions, false);
    }

    // Method to update the game state and implement wave cooldown countdown
    public void update( float delta, Player player, CollisionMap collisions, boolean playerIndoors) {

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

        // When the player is inside Greggs,
        // the gameplay timer continues,
        // but geese remain outside.
        if (playerIndoors) {

            // Let the between-wave cooldown continue.
            if (isBetweenWaves()) {

                nextWaveCountdown = Math.max(
                    0f,
                    nextWaveCountdown - dt
                );
            }

            return;
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


    public int useStatueHonk(
        float statueX,
        float statueY
    ) {

        if (!isPlaying()) {
            return 0;
        }

        return affectClosest(
            statueX,
            statueY,
            STATUE_RANGE,
            Integer.MAX_VALUE,
            true,
            statueX,
            statueY
        );
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

    //Method to count the number of nearby geese for sound purposes
    public int countNearbyGeese(Player player, float range) {

        Rectangle feet = player.getFeetBounds();

        float px = feet.x + feet.width / 2f;
        float py = feet.y + feet.height / 2f;

        int count = 0;

        for (Goose goose : geese) {

            // Only count geese actively chasing or pecking.
            if (goose.getState() != Goose.State.CHASING &&
                goose.getState() != Goose.State.PECKING) {
                continue;
            }

            float dx = goose.getX() - px;
            float dy = goose.getY() - py;

            if (dx * dx + dy * dy <= range * range) {
                count++;
            }
        }

        return count;
    }

}
