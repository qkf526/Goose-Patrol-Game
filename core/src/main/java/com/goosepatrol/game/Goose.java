package com.goosepatrol.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

// A class that creates a goose enemy that chases the player, pecks them, and can be distracted by seeds or frightened by a horn.
public class Goose {
    // The goose's current state, which determines its behavior and animation.
    public enum State {CHASING, PECKING, GOING_TO_SEEDS, EATING, FLEEING, GONE} 
    
    // Constants defining the goose's behavior, such as movement speeds, attack range, and timing for various actions.
    private static final float CHASE_SPEED = 110f;
    private static final float SEED_SPEED = 130f;
    private static final float FLEE_SPEED = 190f;
    private static final float PECK_RANGE = 40f;
    private static final float PECK_INTERVAL = 1.35f;
    private static final float EATING_TIME = 2.6f;
    private static final float FLEE_TIME = 2.8f;
    private static final float SPRITE_SIZE = 48f;
    private static final float PECK_DURATION = 0.40f;
    private static final float PECK_IMPACT_TIME = 0.18f;

    // x and y are the centre of the goose's feet on the ground.
    private float x;
    private float y;
    private State state = State.CHASING;
    private float attackCooldown = 0f;
    private float stateTimer = 0f;
    private float seedX, seedY;
    private float fleeDX, fleeDY;
    private float animationTime = 0f;
    private boolean facingLeft = false;
    private boolean peckImpactDone = false;
    private final Rectangle feet = new Rectangle();

    public Goose(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() { return x; } // Get the x-coordinate of the goose's feet.
    public float getY() { return y; } // Get the y-coordinate of the goose's feet.
    public State getState() { return state; } // Get the current state of the goose.
    public boolean isGone() { return state == State.GONE; } // Check if the goose has left the map and is no longer active.
    // Check if the goose can be affected by items like seeds or horns. Only CHASING and PECKING states are affected.
    public boolean canBeAffectedByItem() {
        return state == State.CHASING || state == State.PECKING;
    }

    // Get the collision rectangle representing the goose's feet, which is used for collision detection with the player and the environment.
    public Rectangle getFeetBounds() {
        return feet.set(x - 11f, y - 5f, 22f, 10f);
    }

    // This goose is attracted to seeds dropped by the player. It will move towards the seeds, eat them, and then flee from the player.
    public void attractToSeeds(float targetX, float targetY) {
        if (!canBeAffectedByItem()) return;
        seedX = targetX;
        seedY = targetY;
        changeState(State.GOING_TO_SEEDS);
        stateTimer = 3.5f; // Failsafe if an obstacle blocks the route.
    }

    // This goose flees from a source point, such as a horn, for a set duration before leaving the map.
    public void frighten(float sourceX, float sourceY) {
        if (!canBeAffectedByItem()) return;
        startFleeing(sourceX, sourceY);
    }

    // Start fleeing in the opposite direction from a source point, such as a horn. The goose will continue fleeing for a set duration before leaving the map.
    private void startFleeing(float sourceX, float sourceY) {
        changeState(State.FLEEING);
        stateTimer = FLEE_TIME;
        float dx = x - sourceX;
        float dy = y - sourceY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance < 0.001f) { dx = 1f; dy = 0f; distance = 1f; }
        fleeDX = dx / distance;
        fleeDY = dy / distance;
    }

    // Update the goose's behavior based on its current state, the player's position, and the elapsed time since the last update. This method handles movement, state transitions, and interactions with the player.
    public void update(float delta, Player player, CollisionMap collisions,float mapWidth, float mapHeight) {
        if (state == State.GONE) return;

        // Movement is capped during a frame hitch; the game timer is not capped.
        delta = Math.min(0.05f, Math.max(0f, delta));
        animationTime += delta;
        attackCooldown = Math.max(0f, attackCooldown - delta);

        switch (state) {

            case CHASING: {

                // Get the player's feet collision rectangle.
                Rectangle playerFeet = player.getFeetBounds();

                // Calculate the centre of the player's feet.
                float targetX =playerFeet.x + playerFeet.width / 2f;
                float targetY =playerFeet.y + playerFeet.height / 2f;

                // Move towards the player when too far away.
                if (distanceTo(targetX, targetY) > PECK_RANGE - 5f) {
                    moveTowards(targetX, targetY, CHASE_SPEED * delta, collisions, mapWidth, mapHeight);
                }

                // Check the distance after attempting movement.
                float distance = distanceTo(targetX, targetY);

                // Begin pecking if the player is close enough.
                if (distance <= PECK_RANGE && attackCooldown <= 0f) {

                    attackCooldown = PECK_INTERVAL;

                    // Prepare a fresh attack animation.
                    peckImpactDone = false;
                    stateTimer = PECK_DURATION;

                    // Enter the PECKING state.
                    changeState(State.PECKING);

                    System.out.println( "Goose entered PECKING state!");
                }

                break;
            }

            case PECKING: {

                // Reduce the time remaining in the attack.
                stateTimer -= delta;

                // Apply the peck at the impact moment.
                if (!peckImpactDone && animationTime >= PECK_IMPACT_TIME) {

                    peckImpactDone = true;

                    // Get the player's ground position.
                    Rectangle feet = player.getFeetBounds();

                    float playerX = feet.x + feet.width / 2f;

                    float playerY = feet.y + feet.height / 2f;

                    // Recheck the distance before dealing damage.
                    if (distanceTo(playerX, playerY) <= PECK_RANGE + 10f) {

                        boolean damaged = player.takeDamage(1);

                        if (damaged) {

                            player.applyKnockbackFrom(x, y);

                            System.out.println( "Goose peck successful! HP: " + player.getHealth());
                        }
                    }
                }

                // Return to chasing after the attack ends.
                if (stateTimer <= 0f) {

                    changeState(State.CHASING);
                }

                break;
            }

            case GOING_TO_SEEDS:
                stateTimer -= delta;
                moveTowards(seedX, seedY, SEED_SPEED * delta, collisions, mapWidth, mapHeight);
                if (distanceTo(seedX, seedY) < 16f || stateTimer <= 0f) {
                    changeState(State.EATING);
                    stateTimer = EATING_TIME;
                }
                break;

            case EATING:
                // Goose remains by the seeds for a short eating animation.
                stateTimer -= delta;
                if (stateTimer <= 0f) {
                    Rectangle ground = player.getFeetBounds();
                    changeState(State.FLEEING);
                    startFleeing(ground.x + ground.width / 2f, ground.y + ground.height / 2f);
                }
                break;

            case FLEEING:
                stateTimer -= delta;
                moveBy(fleeDX * FLEE_SPEED * delta, fleeDY * FLEE_SPEED * delta, collisions, mapWidth, mapHeight);
                // Even if scenery blocks the exit, the goose eventually leaves.
                if (stateTimer <= 0f) changeState(State.GONE);
                break;

            case GONE:
                break;
        }
    }

    // Calculate the distance from the goose to a target position using the Euclidean distance formula. This method is used to determine how far the goose is from the player or seeds.
    private float distanceTo(float targetX, float targetY) {
        float dx = targetX - x;
        float dy = targetY - y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    // Move the goose towards a target position, but not exceeding a maximum step size. This method ensures smooth movement and prevents overshooting the target.
    private void moveTowards(float targetX, float targetY, float maximumStep,CollisionMap collisions, float mapWidth, float mapHeight) {
        float dx = targetX - x;
        float dy = targetY - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length < 0.001f) return;
        float step = Math.min(maximumStep, length);
        moveBy(dx / length * step, dy / length * step, collisions, mapWidth, mapHeight);
    }

    // Move the goose by a specified amount, checking for collisions and map boundaries. If a collision occurs, the goose will not move into the blocked area.
    private void moveBy(float dx, float dy, CollisionMap collisions,float mapWidth, float mapHeight) {
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(dx), Math.abs(dy)) / 4f));
        float stepX = dx / steps;
        float stepY = dy / steps;
        if (Math.abs(dx) > 0.5f) {
            facingLeft = dx < 0f;
        }

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

    // Check if the goose is outside the map boundaries, with a small margin to prevent it from getting stuck at the edges.
    private boolean outsideMap(float mapWidth, float mapHeight) {
        return x < 14f || x > mapWidth - 14f || y < 12f || y > mapHeight - 12f;
    }

    // Draw the goose at its current position, facing left or right, using the appropriate animation frame.
    public void draw(SpriteBatch batch, GooseAnimations animations) {

        if (state == State.GONE) {
            return;
        }

        TextureRegion frame = animations.getFrame(state,animationTime);

        batch.draw(
            frame,
            x - SPRITE_SIZE / 2f,
            y - 8f,
            SPRITE_SIZE / 2f,
            SPRITE_SIZE / 2f,
            SPRITE_SIZE,
            SPRITE_SIZE,
            facingLeft ? -1f : 1f,
            1f,
            0f
        );
    }

    // Change the goose's state and reset the animation timer for the new state.
    private void changeState(State nextState) {

        state = nextState;
        // Restart the new state's animation.
        animationTime = 0f;
    }
}
