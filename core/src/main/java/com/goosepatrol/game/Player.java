
package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;


public class Player {

    // The student's current position.
    private float x;
    private float y;

    // The student's size and speed.
    private static final float SIZE = 48f;
    private static final float SPEED = 200f;

    // The size of the rectangle representing the student's feet for collision detection.
    private static final float FEET_WIDTH = 24f;
    private static final float FEET_HEIGHT = 12f; 

    // Duration of each frame in seconds.
    private static final float FRAME_DURATION = 0.17f; 

    // The student's possible movement directions.
    private enum Direction {UP, DOWN, LEFT, RIGHT}

    // The direction the player is facing.
    private Direction facing = Direction.DOWN;

    // Whether the player is currently moving.
    private boolean moving = false;

    // Time spent playing the current animation.
    private float animationTime = 0f;

    // Walking animations for each direction.
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkDown;
    private Animation<TextureRegion> walkLeft;
    private Animation<TextureRegion> walkRight;

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

    // Update the student's movement every frame.
    public void updateMovement(float deltaTime, CollisionMap collisions) {

        float dx = 0f; // Change in x-coordinate.
        float dy = 0f; // Change in y-coordinate.

        // Move right.
        if (Gdx.input.isKeyPressed(Input.Keys.D) ||
            Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            dx += 1f;
        }

        // Move left.
        if (Gdx.input.isKeyPressed(Input.Keys.A) ||
            Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            dx -= 1f;
        }

        // Move up.
        if (Gdx.input.isKeyPressed(Input.Keys.W) ||
            Gdx.input.isKeyPressed(Input.Keys.UP)) {
            dy += 1f;
        }

        // Move down.
        if (Gdx.input.isKeyPressed(Input.Keys.S) ||
            Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            dy -= 1f;
        }

        boolean movementRequested = (dx != 0f || dy != 0f); // Check if the player is moving.

        // Normalize the movement vector and update the player's position and facing direction if moving.
        if (movementRequested) {

            // Update the facing direction based on the movement vector.
            if (dx != 0f) {
                if (dx > 0f) {
                    facing = Direction.RIGHT;
                } else {
                    facing = Direction.LEFT;
                }
            } else if (dy != 0f) {
                if (dy > 0f) {
                    facing = Direction.UP;
                } else {
                    facing = Direction.DOWN;
                }
            }

            // Calculate movement vector length.
            float length = (float) Math.sqrt(dx * dx + dy * dy);

            // Move at a consistent speed.
            float movementX = (dx / length) * SPEED * deltaTime;
            float movementY = (dy / length) * SPEED * deltaTime;

            float oldX = x; // Store the old x-coordinate for collision checking.
            float oldY = y; // Store the old y-coordinate for collision checking.

            moveWithCollisions(movementX, movementY, collisions); // Move the player with collision detection.

            moving = (x != oldX || y != oldY); // Check if the player actually moved after collision detection.

            // Advance animation time while moving.
            animationTime += deltaTime;

        } else {
            moving = false; // Player is not moving.
        }

        if (moving) {
            animationTime += deltaTime; // Advance animation time while moving.
        } else {
            animationTime = 0f; // Reset animation time when standing still.
        }

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

    // Draw the player character using the provided sprite batch and the current animation frame.
    public void draw(SpriteBatch batch) {
        Animation<TextureRegion> animation = getWalkingAnimation(); // Get the current walking animation based on the facing direction.

        TextureRegion currentFrame; // The current frame of the animation to draw.

        if (moving) {
            currentFrame = animation.getKeyFrame(animationTime, true); // Get the current frame of the walking animation if moving.
        } else {
            currentFrame = animation.getKeyFrames()[0]; // Show the first frame of the walking animation if standing still.
        }
        batch.draw(currentFrame, x, y, SIZE, SIZE); // Draw the current frame at the player's position with the specified size.
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

}
