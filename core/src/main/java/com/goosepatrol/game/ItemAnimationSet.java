
package com.goosepatrol.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class ItemAnimationSet {

    // Duration of an entire item-use action.
    public static final float USE_DURATION = 0.48f;

    // Duration of each holding-walk frame.
    private static final float HOLD_FRAME_DURATION = 0.16f;

    // Animations stored in direction order:
    // DOWN, UP, LEFT, RIGHT.
    private final Array<Animation<TextureRegion>> holding =
        new Array<>();

    private final Array<Animation<TextureRegion>> useStanding =
        new Array<>();

    private final Array<Animation<TextureRegion>> useWalking =
        new Array<>();

    public ItemAnimationSet(
        TextureRegion[][] holdingFrames,
        TextureRegion[][] standingFrames,
        TextureRegion[][] walkingFrames
    ) {

        validateFrames(holdingFrames);
        validateFrames(standingFrames);

        // Healing items may not have moving-use frames.
        if (walkingFrames == null) {
            walkingFrames = standingFrames;
        }

        validateFrames(walkingFrames);

        // Create animations for all four directions.
        for (int column = 0; column < 4; column++) {

            holding.add(
                createAnimation(
                    holdingFrames,
                    column,
                    HOLD_FRAME_DURATION
                )
            );

            useStanding.add(
                createAnimation(
                    standingFrames,
                    column,
                    USE_DURATION / standingFrames.length
                )
            );

            useWalking.add(
                createAnimation(
                    walkingFrames,
                    column,
                    USE_DURATION / walkingFrames.length
                )
            );
        }
    }

    // Check that frames use the expected layout.
    private void validateFrames(TextureRegion[][] frames) {

        if (frames.length == 0) {
            throw new IllegalArgumentException(
                "Animation must contain at least one row."
            );
        }

        for (TextureRegion[] row : frames) {
            if (row.length != 4) {
                throw new IllegalArgumentException(
                    "Animation must have four direction columns."
                );
            }
        }
    }

    // Build one directional animation.
    private Animation<TextureRegion> createAnimation(
        TextureRegion[][] frames,
        int column,
        float frameDuration
    ) {

        Array<TextureRegion> sequence =
            new Array<>(TextureRegion.class);

        for (TextureRegion[] row : frames) {
            sequence.add(row[column]);
        }

        return new Animation<>(
            frameDuration,
            sequence
        );
    }

    // Show a standing pose or holding-walk animation.
    public TextureRegion getHoldingFrame(
        int direction,
        boolean moving,
        float animationTime
    ) {

        Animation<TextureRegion> animation =
            holding.get(direction);

        if (moving) {
            return animation.getKeyFrame(
                animationTime,
                true
            );
        }

        return animation.getKeyFrames()[0];
    }

    // Select stationary or moving item-use animation.
    public TextureRegion getUsingFrame(
        int direction,
        boolean moving,
        float useTime
    ) {

        Animation<TextureRegion> animation;

        if (moving) {
            animation = useWalking.get(direction);
        } else {
            animation = useStanding.get(direction);
        }

        // Item-use animations play once, not continuously.
        return animation.getKeyFrame(useTime, false);
    }
}
