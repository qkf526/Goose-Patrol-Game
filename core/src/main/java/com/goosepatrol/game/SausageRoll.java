
package com.goosepatrol.game;

/**
 * Represents a Sausage Roll item that restores the player's
 * health to its maximum value when successfully used.
 */
public class SausageRoll extends Item {

    /**
     * Creates a Sausage Roll item with its type,
     * name, and description.
     */
    public SausageRoll() {
        super(ItemType.SAUSAGE_ROLL, "Sausage Roll", "Restores health to maximum");
    }

    /**
     * Applies the Sausage Roll's healing effect to the player.
     * Restores the player's health to maximum if the player
     * is alive and has less than full health.
     *
     * @param player the player receiving the healing effect
     * @return true if the player was successfully healed,
     *         otherwise false if the player is null,
     *         defeated, or already at maximum health
     */
    @Override
    public boolean applyEffect(Player player) {
        if (player == null || player.isDefeated() ||
            player.getHealth() >= player.getMaxHealth()) return false;
        player.heal(player.getMaxHealth());
        return true;
    }
}
