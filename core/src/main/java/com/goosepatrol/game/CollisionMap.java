
package com.goosepatrol.game;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class CollisionMap {

    // An array to store all the blocked rectangles.
    private final Array<Rectangle> obstacles = new Array<>();

    // The constructor that receives our loaded campus map..
    public CollisionMap(TiledMap map) {

        // Gets the collision layer.
        MapLayer collisionLayer = map.getLayers().get("Collisions");

        // Stops if the layer does not exist and throws an exception.
        if (collisionLayer == null) {
            throw new IllegalStateException("Missing Collisions object layer in maps/campus.tmx");
        }

        // Read every rectangle from the layer.
        for (RectangleMapObject object : collisionLayer.getObjects().getByType(RectangleMapObject.class)) {
            obstacles.add(object.getRectangle());
        }
    }

    // Check whether the player's feet touch an obstacle.
    public boolean isBlocked(Rectangle playerFeet) {

        for (Rectangle obstacle : obstacles) {

            if (playerFeet.overlaps(obstacle)) {
                return true;
            }
        }

        // No obstacles were overlapping.
        return false;
    }

    // Return the number of obstacles we loaded.
    public int getObstacleCount() {
        return obstacles.size;
    }
}

