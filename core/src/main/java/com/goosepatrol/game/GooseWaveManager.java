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
    public static final int GEESE_PER_WAVE = 4;
    public static final int TOTAL_GEESE = TOTAL_WAVES * GEESE_PER_WAVE;

    private static final float[] WAVE_STARTS = {0f, 65f, 130f, 195f};
    private static final int SEEDS_MAX_TARGETS = 2;
    private static final float SEEDS_RANGE = 240f;
    private static final int HORN_MAX_TARGETS = 3;
    private static final float HORN_RANGE = 185f;

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
    }

    public void update(float delta, Player player, CollisionMap collisions) {
        if (state != GameState.PLAYING) return;

        // The clock accumulates real elapsed time, not clamped movement time.
        elapsed += Math.max(0f, delta);

        while (nextWave < TOTAL_WAVES && elapsed >= WAVE_STARTS[nextWave]) {
            spawnWave(nextWave, player, collisions);
            nextWave++;
        }

        for (int i = geese.size - 1; i >= 0; i--) {
            Goose goose = geese.get(i);
            goose.update(delta, player, collisions, mapWidth, mapHeight);
            if (goose.isGone()) {
                geese.removeIndex(i);
                dispersed++;
            }
        }

        for (int i = seedPatches.size - 1; i >= 0; i--) {
            SeedPatch patch = seedPatches.get(i);
            patch.secondsRemaining -= delta;
            if (patch.secondsRemaining <= 0f) seedPatches.removeIndex(i);
        }

        if (player.isDefeated() || elapsed >= GAME_DURATION) {
            state = GameState.LOST;
        } else if (nextWave == TOTAL_WAVES && geese.size == 0) {
            state = GameState.WON;
        }
    }

    private void spawnWave(int waveNumber, Player player, CollisionMap collisions) {
        float playerX = player.getCentreX();
        float playerY = player.getCentreY();
        int spawned = 0;
        for (int i = 0; i < GEESE_PER_WAVE; i++) {
            // Rotate spawn points between waves to vary the approach directions.
            int initialIndex = (waveNumber * 3 + i * 2) % SPAWN_POINTS.length;
            for (int tryIndex = 0; tryIndex < SPAWN_POINTS.length; tryIndex++) {
                float[] point = SPAWN_POINTS[(initialIndex + tryIndex) % SPAWN_POINTS.length];
                if (isValidSpawn(point[0], point[1], playerX, playerY, collisions)
                    && !isSpawnOccupied(point[0], point[1])) {
                    geese.add(new Goose(point[0], point[1]));
                    spawned++;
                    break;
                }
            }
        }
        // Warn if all four couldn't spawn: map collisions may have changed.
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

    private boolean isValidSpawn(float x, float y, float playerX, float playerY,
                                 CollisionMap collisions) {
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
        return affectClosest(playerGroundX, playerGroundY,
            SEEDS_RANGE, SEEDS_MAX_TARGETS, false, seedX, seedY);
    }

    /** Called once when an Air Horn-use animation completes successfully. */
    public int useAirHorn(float playerGroundX, float playerGroundY) {
        if (state != GameState.PLAYING) return 0;
        return affectClosest(playerGroundX, playerGroundY,
            HORN_RANGE, HORN_MAX_TARGETS, true, playerGroundX, playerGroundY);
    }

    private int affectClosest(float playerX, float playerY, float radius,
                              int limit, boolean horn, float seedX, float seedY) {
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
            batch.draw(seedTexture, patch.x - 12f, patch.y - 7f, 24f, 16f);
        }
    }

    /** Basic depth ordering relative to the player's ground position. */
    public void drawGeeseBehindPlayer(SpriteBatch batch, Texture gooseTexture, float playerGroundY) {
        for (Goose goose : geese) {
            if (goose.getY() >= playerGroundY) goose.draw(batch, gooseTexture);
        }
    }

    public void drawGeeseInFrontOfPlayer(SpriteBatch batch, Texture gooseTexture, float playerGroundY) {
        for (Goose goose : geese) {
            if (goose.getY() < playerGroundY) goose.draw(batch, gooseTexture);
        }
    }
}

