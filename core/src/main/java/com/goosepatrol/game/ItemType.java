
package com.goosepatrol.game;

/**
 * Defines the available item types in the game.
 * Each item type specifies whether the player
 * can move while using the item.
 */
public enum ItemType {

    /** Seeds allow the player to move while using them. */
    SEEDS(true),

    /** The Air Horn allows the player to move while using it. */
    AIR_HORN(true),

    /** Bandages prevent movement while being used. */
    BANDAGE(false),

    /** Sausage Rolls prevent movement while being used. */
    SAUSAGE_ROLL(false);

    // Whether this item allows walking during use.
    private final boolean canMoveWhileUsing;

    /**
     * Creates an item type with its movement rule.
     *
     * @param canMoveWhileUsing true if the player can move
     *                          while using this item, otherwise false
     */
    ItemType(boolean canMoveWhileUsing) {
        this.canMoveWhileUsing = canMoveWhileUsing;
    }

    /**
     * Checks whether the player can move while using this item.
     *
     * @return true if movement is allowed during item use,
     *         otherwise false
     */
    public boolean canMoveWhileUsing() {
        return canMoveWhileUsing;
    }
}
