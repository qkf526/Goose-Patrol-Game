package com.goosepatrol.game;

public class AirHorn extends Item {

    private final float effectRadius;

    public AirHorn() {
        super("Air Horn", "Scares nearby geese away");
        effectRadius = 100f;
    }

    public boolean isGooseInRange(
        float playerX,
        float playerY,
        float gooseX,
        float gooseY
    ) {
        float distanceX = gooseX - playerX;
        float distanceY = gooseY - playerY;

        float distance = (float) Math.sqrt(
            distanceX * distanceX +
            distanceY * distanceY
        );

        return distance <= effectRadius;
    }
}