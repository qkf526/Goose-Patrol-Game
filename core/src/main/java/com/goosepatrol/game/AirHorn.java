
package com.goosepatrol.game;

/**
 * Represents an Air Horn item used to scare nearby geese away.
 * Provides a method to check whether a goose is within
 * the item's effect radius.
 */
public class AirHorn extends Item {
    private final float effectRadius = 100f;

    /**
     * Creates an Air Horn item with its type,
     * name, and description.
     */
    public AirHorn() {
        super(ItemType.AIR_HORN, "Air Horn", "Scares nearby geese away");
    }

    /**
     * Checks whether a goose is within the Air Horn's effect radius.
     * Calculates the squared distance between the player and the goose
     * and compares it with the squared effect radius.
     *
     * @param playerX the player's horizontal position
     * @param playerY the player's vertical position
     * @param gooseX the goose's horizontal position
     * @param gooseY the goose's vertical position
     * @return true if the goose is within the effect radius,
     *         otherwise false
     */
    public boolean isGooseInRange(float playerX, float playerY,
                                  float gooseX, float gooseY) {
        float dx = gooseX - playerX;
        float dy = gooseY - playerY;
        return dx * dx + dy * dy <= effectRadius * effectRadius;
    }

    /**
     * Attempts to apply the Air Horn item's effect.
     * Currently prints a message indicating that the goose
     * scare effect will be connected later.
     *
     * @param player the player using the Air Horn
     * @return true if the player exists and has not been defeated,
     *         otherwise false
     */
    @Override
    public boolean applyEffect(Player player) {
        if (player == null || player.isDefeated()) return false;
        return true;
    }
}
