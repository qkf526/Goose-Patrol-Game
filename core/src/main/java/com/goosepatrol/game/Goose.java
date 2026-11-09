package com.goosepatrol.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/** A single goose, including its simple behaviour and peck attack. */
public class Goose {

    public enum State {
        CHASING, GOING_TO_SEEDS, EATING, FLEEING, GONE
    }

    private static final float CHASE_SPEED = 115f;
    private static final float SEED_SPEED = 130f;
    private static final float FLEE_SPEED = 190f;
    private static final float PECK_RANGE = 34f;
    private static final float PECK_INTERVAL = 1.35f;
    private static final float EATING_TIME = 2.6f;
    private static final float FLEE_TIME = 2.8f;
    private static final float SPRITE_SIZE = 48f;

    // x and y are the centre of the goose's feet on the ground.
    private float x;
    private float y;
    private State state = State.CHASING;
    private float age = 0f;
    private float attackCooldown = 0f;
    private float stateTimer = 0f;
    private float seedX, seedY;
    private float fleeDX, fleeDY;
    private final Rectangle feet = new Rectangle();

    public Goose(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public State getState() { return state; }
    public boolean isGone() { return state == State.GONE; }
    public boolean canBeAffectedByItem() { return state == State.CHASING; }

    public Rectangle getFeetBounds() {
        return feet.set(x - 11f, y - 5f, 22f, 10f);
    }

    /** This goose heads towards seeds, eats, and then leaves. */
    public void attractToSeeds(float targetX, float targetY) {
        if (!canBeAffectedByItem()) return;
        seedX = targetX;
        seedY = targetY;
        state = State.GOING_TO_SEEDS;
        stateTimer = 3.5f; // Failsafe if an obstacle blocks the route.
    }

    /** A horn frightens the goose into fleeing straight away. */
    public void frighten(float sourceX, float sourceY) {
        if (!canBeAffectedByItem()) return;
        startFleeing(sourceX, sourceY);
    }

    private void startFleeing(float sourceX, float sourceY) {
        state = State.FLEEING;
        stateTimer = FLEE_TIME;
        float dx = x - sourceX;
        float dy = y - sourceY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance < 0.001f) { dx = 1f; dy = 0f; distance = 1f; }
        fleeDX = dx / distance;
        fleeDY = dy / distance;
    }

    public void update(float delta, Player player, CollisionMap collisions,
                       float mapWidth, float mapHeight) {
        if (state == State.GONE) return;

        // Movement is capped during a frame hitch; the game timer is not capped.
        delta = Math.min(0.05f, Math.max(0f, delta));
        age += delta;
        attackCooldown = Math.max(0f, attackCooldown - delta);

        switch (state) {
            case CHASING:
                Rectangle playerFeet = player.getFeetBounds();
                float targetX = playerFeet.x + playerFeet.width / 2f;
                float targetY = playerFeet.y + playerFeet.height / 2f;
                float distance = distanceTo(targetX, targetY);

                if (distance > PECK_RANGE - 4f) {
                    moveTowards(targetX, targetY, CHASE_SPEED * delta,
                        collisions, mapWidth, mapHeight);
                }
                if (distanceTo(targetX, targetY) <= PECK_RANGE && attackCooldown <= 0f) {
                    attackCooldown = PECK_INTERVAL;
                    if (player.takeDamage(1)) {
                        player.applyKnockbackFrom(x, y);
                    }
                }
                break;

            case GOING_TO_SEEDS:
                stateTimer -= delta;
                moveTowards(seedX, seedY, SEED_SPEED * delta,
                    collisions, mapWidth, mapHeight);
                if (distanceTo(seedX, seedY) < 16f || stateTimer <= 0f) {
                    state = State.EATING;
                    stateTimer = EATING_TIME;
                }
                break;

            case EATING:
                // Goose remains by the seeds for a short eating animation.
                stateTimer -= delta;
                if (stateTimer <= 0f) {
                    Rectangle ground = player.getFeetBounds();
                    startFleeing(ground.x + ground.width / 2f,
                                 ground.y + ground.height / 2f);
                }
                break;

            case FLEEING:
                stateTimer -= delta;
                moveBy(fleeDX * FLEE_SPEED * delta,
                       fleeDY * FLEE_SPEED * delta,
                       collisions, mapWidth, mapHeight);
                // Even if scenery blocks the exit, the goose eventually leaves.
                if (stateTimer <= 0f) state = State.GONE;
                break;

            case GONE:
                break;
        }
    }

    private float distanceTo(float targetX, float targetY) {
        float dx = targetX - x;
        float dy = targetY - y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void moveTowards(float targetX, float targetY, float maximumStep,
                             CollisionMap collisions, float mapWidth, float mapHeight) {
        float dx = targetX - x;
        float dy = targetY - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length < 0.001f) return;
        float step = Math.min(maximumStep, length);
        moveBy(dx / length * step, dy / length * step, collisions, mapWidth, mapHeight);
    }

    /** Same axis-by-axis approach as the Player's collision movement. */
    private void moveBy(float dx, float dy, CollisionMap collisions,
                        float mapWidth, float mapHeight) {
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(dx), Math.abs(dy)) / 4f));
        float stepX = dx / steps;
        float stepY = dy / steps;

        for (int i = 0; i < steps; i++) {
            float oldX = x;
            x += stepX;
            if (outsideMap(mapWidth, mapHeight) || collisions.isBlocked(getFeetBounds())) {
                x = oldX;
            }
            float oldY = y;
            y += stepY;
            if (outsideMap(mapWidth, mapHeight) || collisions.isBlocked(getFeetBounds())) {
                y = oldY;
            }
        }
    }

    private boolean outsideMap(float mapWidth, float mapHeight) {
        return x < 14f || x > mapWidth - 14f || y < 12f || y > mapHeight - 12f;
    }

    /** Draws a temporary pixel-art goose. Replace texture later with animation frames. */
    public void draw(SpriteBatch batch, Texture gooseTexture) {
        if (state == State.GONE) return;
        float bob = 0f;
        if (state == State.EATING) bob = (float) Math.sin(age * 17f) * 3f;
        else if (state == State.CHASING || state == State.GOING_TO_SEEDS)
            bob = (float) Math.sin(age * 11f) * 1.3f;
        batch.draw(gooseTexture,
            x - SPRITE_SIZE / 2f,
            y - 8f + bob,
            SPRITE_SIZE, SPRITE_SIZE);
    }
}

