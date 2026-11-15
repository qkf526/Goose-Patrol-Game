
package com.goosepatrol.game;

/**
 * Represents a Bandage item that restores the player's health.
 * Each successful use restores up to two health points,
 * without exceeding the player's maximum health.
 */
public class Bandage extends Item {
    private final int healingAmount = 2;

    /**
     * Creates a Bandage item with its type,
     * name, and healing description.
     */
    public Bandage() {
        super(ItemType.BANDAGE, "Bandage", "Restores 2 health points");
    }

    /**
     * Attempts to heal the player using a Bandage.
     * Restores two health points if the player is alive
     * and has less than maximum health.
     *
     * @param player the player receiving the healing effect
     * @return true if the healing action was performed,
     *         otherwise false if the player is null,
     *         defeated, or already at maximum health
     */
    public boolean use(Player player) {
        if (player == null || player.isDefeated() ||
            player.getHealth() >= player.getMaxHealth()) return false;
        player.heal(healingAmount);
        return true;
    }

    /**
     * Applies the Bandage's healing effect to the player
     * by calling the use method.
     *
     * @param player the player receiving the healing effect
     * @return true if the Bandage was successfully used,
     *         otherwise false
     */
    @Override
    public boolean applyEffect(Player player) {
        return use(player);
    }
}
