
package com.goosepatrol.game;

import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

// This class manages the animations for the goose character, including walking, pecking, eating, and fleeing. It loads the appropriate sprite sheets and provides methods to retrieve the correct animation frame based on the goose's state and elapsed time.
public class GooseAnimations implements Disposable {

    private static final int FRAME_SIZE = 64;

    private final Array<Texture> textures = new Array<>();

    private final EnumMap<Goose.State, Animation<TextureRegion>> animations = new EnumMap<>(Goose.State.class);

    // Initialize the goose animations by loading the sprite sheets for each state and creating the corresponding Animation objects.
    public GooseAnimations() {

        load( Goose.State.CHASING, "goose/goose_walk.png", 0.15f);
        load( Goose.State.PECKING,"goose/goose_peck.png",0.10f);
        load( Goose.State.EATING, "goose/goose_eat.png", 0.18f);
        load( Goose.State.FLEEING, "goose/goose_flee.png", 0.10f);
    }

    // Load a sprite sheet for a specific goose state and create an Animation object. The sprite sheet is expected to be 256x64 pixels, with 4 frames arranged horizontally.
    private void load( Goose.State state, String path,float frameDuration) {

        Texture texture = new Texture( Gdx.files.internal(path) );

        // Check the expected 4-frame layout.
        if (texture.getWidth() != 256 || texture.getHeight() != 64) {
            texture.dispose();
            throw new IllegalArgumentException( "Expected 256x64 goose sheet: " + path );
        }

        texture.setFilter( Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        textures.add(texture);
        TextureRegion[] frames = TextureRegion.split( texture, FRAME_SIZE, FRAME_SIZE)[0];
        animations.put( state, new Animation<>(frameDuration, frames));
    }

    // Get the appropriate animation frame for the goose based on its current state and the elapsed time. The method handles looping for most animations, except for pecking, which plays once.
    public TextureRegion getFrame( Goose.State state, float time) {
        // If the goose is gone, return null to indicate no animation should be displayed.
        if (state == Goose.State.GONE) {
            return null;
        }

        // Geese approaching seeds still walk.
        Goose.State animationState = state == Goose.State.GOING_TO_SEEDS ? Goose.State.CHASING : state;
        Animation<TextureRegion> animation = animations.get(animationState);

        // Peck plays once. Other animations loop.
        boolean loop = animationState != Goose.State.PECKING;
        return animation.getKeyFrame(time, loop);
    }

    // Dispose of all loaded textures to free up resources when the animations are no longer needed.
    @Override
    public void dispose() {

        for (Texture texture : textures) {
            texture.dispose();
        }
    }
}
