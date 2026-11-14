package com.goosepatrol.game;

public class AirHorn extends Item {
    private final float effectRadius = 100f;

    public AirHorn() {
        super(ItemType.AIR_HORN, "Air Horn", "Scares nearby geese away");
    }

    //distance calculation for Goose.
    public boolean isGooseInRange(float playerX, float playerY,
                                  float gooseX, float gooseY) {
        float dx = gooseX - playerX;
        float dy = gooseY - playerY;
        return dx * dx + dy * dy <= effectRadius * effectRadius;
    }

    @Override
    public boolean applyEffect(Player player) {
        if (player == null || player.isDefeated()) return false;
        System.out.println("Air Horn used. Goose scare effect will be connected later.");
        return true;
    }
}
