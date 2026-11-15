
package com.goosepatrol.game;

/**
 * Represents a Seeds item used to distract geese.
 * Provides an item use method and checks whether
 * the player can successfully use the item.
 */
public class Seeds extends Item {

    /**
     * Creates a Seeds item with its type,
     * name, and description.
     */
    public Seeds() {
        super(ItemType.SEEDS, "Seeds", "Distracts geese");
    }

    /**
     * Attempts to apply the Seeds item's effect.
     * Calls the use method if the player exists
     * and has not been defeated.
     *
     * @param player the player using the Seeds item
     * @return true if the use action was performed,
     *         otherwise false if the player is null or defeated
     */
    @Override
    public boolean applyEffect(Player player) {
        if (player == null || player.isDefeated()) return false;
        return true;
    }
}
