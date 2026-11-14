
package com.goosepatrol.game;

public enum ItemType {

    SEEDS(true),
    AIR_HORN(true),
    BANDAGE(false),
    SAUSAGE_ROLL(false);

    // Whether this item allows walking during use.
    private final boolean canMoveWhileUsing;

    // Constructor for each enum value.
    ItemType(boolean canMoveWhileUsing) {
        this.canMoveWhileUsing = canMoveWhileUsing;
    }

    // Return the item's movement rule.
    public boolean canMoveWhileUsing() {
        return canMoveWhileUsing;
    }
}


