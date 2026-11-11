
package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;
import java.util.EnumMap;


public class Player {

    // The student's current position.
    private float x;
    private float y;

    // The student's size and speed.
    private static final float SIZE = 78.5f;
    private static final float SPEED = 200f;

    // The size of the rectangle representing the student's feet for collision detection.
    private static final float FEET_WIDTH = 24f;
    private static final float FEET_HEIGHT = 12f; 

    private static final int MAX_HEALTH = 10; // The maximum health of the player.
    private static final float INVINCIBILITY_DURATION = 1f; // Duration of invincibility after taking damage in seconds.
    private int health = MAX_HEALTH; // The current health of the player.
    private float invincibilityTimer = 0f; // Timer to track invincibility duration after taking damage.

    // How quickly the player is pushed away.
    private static final float KNOCKBACK_SPEED = 150f;

    // How long the push lasts, in seconds.
    private static final float KNOCKBACK_DURATION = 0.16f;

    // Current knockback velocity.
    private float knockbackVelocityX = 0f;
    private float knockbackVelocityY = 0f;

    // Time remaining for the knockback movement.
    private float knockbackTimeRemaining = 0f;


    // Duration of each frame in seconds.
    private static final float FRAME_DURATION = 0.14f; 

    // The student's possible movement directions.
    private enum Direction {UP, DOWN, LEFT, RIGHT}

    // The direction the player is facing.
    private Direction facing = Direction.DOWN;

    // Whether the player is currently moving.
    private boolean moving = false;

    // Time spent playing the current animation.
    private float animationTime = 0f;

    // Duration of each frame, total duration, and timers for the hurt animation.
    private static final float HURT_FRAME_DURATION = 0.12f;
    private static final float HURT_DURATION = 0.36f;
    private float hurtTimer = 0f;
    private float hurtAnimationTime = 0f;

    // Hurt animations for each direction.
    private Animation<TextureRegion> hurtUp;
    private Animation<TextureRegion> hurtDown;
    private Animation<TextureRegion> hurtLeft;
    private Animation<TextureRegion> hurtRight;

    // Walking animations for each direction.
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkDown;
    private Animation<TextureRegion> walkLeft;
    private Animation<TextureRegion> walkRight;


    // Stores the animations for each item type.
    private final EnumMap<ItemType, ItemAnimationSet>
        itemAnimations = new EnumMap<>(ItemType.class);

    // Which item the student currently holds.
    // null means no item is equipped.
    private ItemType equippedItem = null;

    // Whether an item-use action is playing.
    private boolean usingItem = false;

    // Time elapsed during the current item-use action.
    private float itemUseTime = 0f;

    // The item that was just used and completed its action.
    private ItemType completedItemUse = null;

    // Constructor: creates a player at a starting position.
    public Player(float x, float y, TextureRegion[][] frames) {
        this.x = x;
        this.y = y;

        // Initialize the walking animations for each direction.
        walkDown = createWalkAnimation(frames, 0);
        walkUp = createWalkAnimation(frames, 1);
        walkLeft = createWalkAnimation(frames, 2);
        walkRight = createWalkAnimation(frames, 3);
    }

    // Create a walking animation for a specific direction using the provided frames.
    private Animation<TextureRegion> createWalkAnimation(TextureRegion[][] frames, int column) {
        return new Animation<>(FRAME_DURATION, frames[0][column], frames[1][column], frames[2][column], frames[3][column]);
    }

    // Method to handle some gameplay animation logic
    public void update(float deltaTime, CollisionMap collisions) {

        // 1. Update temporary invincibility.
        if (invincibilityTimer > 0f) {

            invincibilityTimer = Math.max(
                0f,
                invincibilityTimer - deltaTime
            );
        }

        // 2. Hurt reaction has priority.
        if (isHurting()) {

            hurtAnimationTime += deltaTime;

            // Apply the existing knockback behaviour.
            if (knockbackTimeRemaining > 0f) {

                float stepTime = Math.min(
                    deltaTime,
                    knockbackTimeRemaining
                );

                moveWithCollisions(
                    knockbackVelocityX * stepTime,
                    knockbackVelocityY * stepTime,
                    collisions
                );

                knockbackTimeRemaining = Math.max(
                    0f,
                    knockbackTimeRemaining - stepTime
                );
            }

            hurtTimer = Math.max(
                0f,
                hurtTimer - deltaTime
            );

            moving = false;
            animationTime = 0f;

            return;
        }

        // 3. Defeated players cannot act.
        if (isDefeated()) {

            moving = false;
            animationTime = 0f;

            return;
        }

        // 4. Handle the current item-use action.
        if (usingItem) {

            // Only some items permit movement.
            if (equippedItem.canMoveWhileUsing()) {

                updatePlayerMovement(deltaTime, collisions);

            } else {

                // Healing animations lock ordinary movement.
                moving = false;
                animationTime = 0f;
            }

            // Advance the use animation.
            itemUseTime += deltaTime;

            // Complete the action after its duration.
            if (itemUseTime >= ItemAnimationSet.USE_DURATION) {

                completedItemUse = equippedItem;

                usingItem = false;
                itemUseTime = 0f;
            }

            return;
        }

        // 5. Normal movement when no item is being used.
        updatePlayerMovement(deltaTime, collisions);
    }


    // Get the walking animation based on the player's facing direction.
    private Animation <TextureRegion> getWalkingAnimation() {
        switch (facing) {
            case UP:
                return walkUp;
            case LEFT:
                return walkLeft;
            case RIGHT:
                return walkRight;
            default:
                return walkDown; // Default to facing down.
        }
    }

    // Keep the player inside the window bounds based on the provided map width and height.
    public void keepInsideMap(int mapWidth, int mapHeight) {

        float maxX = Math.max(0f, mapWidth - SIZE); // Calculate the maximum x-coordinate to keep the player inside the window.
        float maxY = Math.max(0f, mapHeight - SIZE); // Calculate the maximum y-coordinate to keep the player inside the window.

        x = MathUtils.clamp(x, 0f, maxX); // Clamp the x-coordinate to stay within the window bounds.
        y = MathUtils.clamp(y, 0f, maxY); // Clamp the y-coordinate to stay within the window bounds.
    }


    public void draw(SpriteBatch batch) {

        TextureRegion currentFrame;

        // Always begin with the normal drawing colour.
        batch.setColor(Color.WHITE);

        // PRIORITY 1: Hurt reaction.
        if (isHurting()) {

            currentFrame = getHurtAnimation().getKeyFrame(
                hurtAnimationTime,
                false
            );

            // Red tint for damage.
            batch.setColor(1f, 0.6f, 0.6f, 1f);

        }

        // PRIORITY 2: Defeated.
        else if (isDefeated()) {

            currentFrame = getWalkingAnimation()
                .getKeyFrames()[0];

        }

        // PRIORITY 3: Using an item.
        else if (usingItem) {

            ItemAnimationSet animations =
                itemAnimations.get(equippedItem);

            currentFrame = animations.getUsingFrame(
                getFacingColumn(),
                moving,
                itemUseTime
            );

        }

        // PRIORITY 4: Holding an item.
        else if (equippedItem != null) {

            ItemAnimationSet animations =
                itemAnimations.get(equippedItem);

            currentFrame = animations.getHoldingFrame(
                getFacingColumn(),
                moving,
                animationTime
            );

        }

        // PRIORITY 5: Ordinary walking or idle.
        else {

            Animation<TextureRegion> animation =
                getWalkingAnimation();

            if (moving) {

                currentFrame = animation.getKeyFrame(
                    animationTime,
                    true
                );

            } else {

                currentFrame = animation.getKeyFrames()[0];
            }
        }

        // Draw the selected frame.
        batch.draw(
            currentFrame,
            x,
            y,
            SIZE,
            SIZE
        );

        // Restore the SpriteBatch colour.
        batch.setColor(Color.WHITE);
    }


    // Get the x-coordinate of the player's center.
    public float getCentreX() {
        return x + SIZE / 2f; 
    }

    // Get the y-coordinate of the player's center.
    public float getCentreY() {
        return y + SIZE /2f;
    }

    // Get the rectangle representing the player's feet for collision detection.
    public Rectangle getFeetBounds() {
        float feetX = x + (SIZE - FEET_WIDTH) / 2f; // Calculate the x-coordinate of the feet rectangle.
        float feetY = y + 2f; // Calculate the y-coordinate of the feet rectangle, slightly above the bottom of the player sprite.
        return new Rectangle(feetX, feetY, FEET_WIDTH, FEET_HEIGHT); // Return a new rectangle representing the player's feet.
    }

    // Move the player with collision detection against the provided CollisionMap.
    private void moveWithCollisions(float moveX, float moveY, CollisionMap collisions) {

        // Split large movements into smaller steps.
        float largestMove = Math.max(Math.abs(moveX),Math.abs(moveY));

        int steps = Math.max(1,(int) Math.ceil(largestMove / 4f));

        float stepX = moveX / steps;
        float stepY = moveY / steps;

        for (int i = 0; i < steps; i++) {

            // First try horizontal movement.
            float oldX = x;
            x += stepX;

            if (collisions.isBlocked(getFeetBounds())) {
                x = oldX;
            }

            // Then try vertical movement.
            float oldY = y;
            y += stepY;

            if (collisions.isBlocked(getFeetBounds())) {
                y = oldY;
            }
        }
    }

    // Method to take damage and reduce the player's health, returning true if damage was taken, false otherwise.
    public boolean takeDamage(int amount) {

        if (amount <= 0 || isInvincible() || isDefeated()) {
            return false; // No damage taken if the amount is non-positive or the player is invulnerable.
        }

        // Cancel an item-use animation after a successful hit.
        if (usingItem) {
            usingItem = false;
            itemUseTime = 0f;
        }

        health = Math.max(0, health - amount); // Reduce health by the damage amount, ensuring it doesn't go below 0.
        invincibilityTimer = INVINCIBILITY_DURATION; // Start the invincibility timer after taking damage.
        hurtTimer = HURT_DURATION; // Start the hurt animation timer after taking damage.
        hurtAnimationTime = 0f; // Reset the hurt animation time to start the hurt animation from the beginning.

        return true; // Damage was successfully taken.
    }


    // Restore health without exceeding the maximum.
    public void heal(int amount) {

        // Ignore invalid healing or healing after defeat.
        if (amount <= 0 || isDefeated()) {
            return;
        }

        health = Math.min(MAX_HEALTH, health + amount); // Increase health by the healing amount, ensuring it doesn't exceed the maximum.
    }

    // Reset the player's health to maximum and clear any temporary states like invincibility or knockback.
    public void resetHealth() {

        health = MAX_HEALTH;
        invincibilityTimer = 0f;
        completedItemUse = null; // Clear the completed item use state.

        // Reset hurt animation state.
        hurtTimer = 0f;
        hurtAnimationTime = 0f;

        // Reset knockback state.
        knockbackTimeRemaining = 0f;
        knockbackVelocityX = 0f;
        knockbackVelocityY = 0f;

        // Restore normal standing animation.
        moving = false;
        animationTime = 0f;

        usingItem = false;
        itemUseTime = 0f;
    }


    // Return true while temporary protection is active.
    public boolean isInvincible() {
        return invincibilityTimer > 0f;
    }

    // Return true when the player has no health.
    public boolean isDefeated() {
        return health == 0;
    }

    // Return the player's current health.
    public int getHealth() {
        return health;
    }

    // Return the maximum permitted health.
    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    // Set the hurt animations for each direction using the provided frames.
    public void setHurtAnimations(TextureRegion[][] frames) {
        hurtDown = createHurtAnimation(frames, 0);
        hurtUp = createHurtAnimation(frames, 1);
        hurtLeft = createHurtAnimation(frames, 2);
        hurtRight = createHurtAnimation(frames, 3);
    }

    // Create a hurt animation for a specific direction using the provided frames.
    private Animation<TextureRegion> createHurtAnimation(TextureRegion[][] frames, int column) {
        return new Animation<>(HURT_FRAME_DURATION, frames[0][column], frames[1][column], frames[2][column]);
    }

    // Get the hurt animation based on the player's facing direction.
    private Animation<TextureRegion> getHurtAnimation() {
        switch (facing) {
            case UP:
                return hurtUp;
            case LEFT:
                return hurtLeft;
            case RIGHT:
                return hurtRight;
            default:
                return hurtDown; // Default to facing down.
        }
    }

    // Return true if the player is currently in the hurt state.
    public boolean isHurting() {
        return hurtTimer > 0f;
    }

    // Apply knockback to the player based on the attacker's position.
    public void applyKnockbackFrom(float attackerX, float attackerY) {

        // Direction from the attacker to the player.
        float dx = getCentreX() - attackerX;
        float dy = getCentreY() - attackerY;

        // Calculate the direction's length.
        float length = (float) Math.sqrt(
            dx * dx + dy * dy
        );

        // Handle an attacker at the same position.
        if (length < 0.001f) {
            dx = 1f;
            dy = 0f;
            length = 1f;
        }

        // Normalise and apply knockback speed.
        knockbackVelocityX = dx / length * KNOCKBACK_SPEED;
        knockbackVelocityY = dy / length * KNOCKBACK_SPEED;

        // Start the knockback timer.
        knockbackTimeRemaining = KNOCKBACK_DURATION;
    }

    public void registerItemAnimations(ItemType type, ItemAnimationSet animations) {
        itemAnimations.put(type, animations);
    }

    
    // Equip a particular item.
    public boolean equipItem(ItemType type) {

        // Equipment cannot change during these states.
        if (isHurting() || isDefeated() || usingItem) {
            return false;
        }

        // The item must have loaded animations.
        if (!itemAnimations.containsKey(type)) {
            return false;
        }

        equippedItem = type;
        animationTime = 0f;

        return true;
    }

    // Put away the currently equipped item.
    public void unequipItem() {

        if (isHurting() || isDefeated() || usingItem) {
            return;
        }

        equippedItem = null;
        animationTime = 0f;
    }

    // Start using the equipped item.
    public boolean startUsingItem() {

        if (equippedItem == null || usingItem || isHurting() || isDefeated()) {
            return false;
        }

        usingItem = true;
        itemUseTime = 0f;

        return true;
    }

    public ItemType getEquippedItem() {
        return equippedItem;
    }

    public boolean isUsingItem() {
        return usingItem;
    }



    private void updatePlayerMovement(float deltaTime, CollisionMap collisions) {

        float dx = 0f;
        float dy = 0f;

        // Horizontal movement input.
        if (Gdx.input.isKeyPressed(Input.Keys.D) ||
            Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            dx += 1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) ||
            Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            dx -= 1f;
        }

        // Vertical movement input.
        if (Gdx.input.isKeyPressed(Input.Keys.W) ||
            Gdx.input.isKeyPressed(Input.Keys.UP)) {
            dy += 1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S) ||
            Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            dy -= 1f;
        }

        // No direction requested.
        if (dx == 0f && dy == 0f) {
            moving = false;
            animationTime = 0f;
            return;
        }

        // Update facing direction.
        if (dx > 0f) {
            facing = Direction.RIGHT;
        } else if (dx < 0f) {
            facing = Direction.LEFT;
        } else if (dy > 0f) {
            facing = Direction.UP;
        } else {
            facing = Direction.DOWN;
        }

        // Normalise diagonal movement.
        float length = (float) Math.sqrt(
            dx * dx + dy * dy
        );

        float moveX = (dx / length) * SPEED * deltaTime;
        float moveY = (dy / length) * SPEED * deltaTime;

        // Remember the previous position.
        float oldX = x;
        float oldY = y;

        // Reuse Lesson 8 collision detection.
        moveWithCollisions(
            moveX,
            moveY,
            collisions
        );

        // Did the player actually move?
        moving = (x != oldX || y != oldY);

        if (moving) {
            animationTime += deltaTime;
        } else {
            animationTime = 0f;
        }
    }

    private int getFacingColumn() {

        switch (facing) {

            case UP:
                return 1;

            case LEFT:
                return 2;

            case RIGHT:
                return 3;

            default:
                return 0; // DOWN
        }
    }

    // Return the item that was just used and completed its action, or null if no item use has completed.
    public ItemType consumeCompletedItemUse() {

        ItemType finished = completedItemUse;

        completedItemUse = null;

        return finished;
    }

    public void resetToStart(float startX, float startY) {
        resetHealth();                  // Already resets hurt and knockback.
        x = startX;
        y = startY;
        equippedItem = null;
    }


}
