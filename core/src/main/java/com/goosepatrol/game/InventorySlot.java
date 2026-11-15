
package com.goosepatrol.game;

/**
 * Represents a single slot in the player's inventory.
 * Each slot stores an item and its remaining quantity.
 */
public class InventorySlot {
    private final Item item;
    private int quantity;

    /**
     * Creates an inventory slot with the specified item and quantity.
     *
     * @param item the item stored in this slot
     * @param quantity the initial quantity of the item
     * @throws IllegalArgumentException if the item is null
     *         or the quantity is negative
     */
    public InventorySlot(Item item, int quantity) {
        if (item == null || quantity < 0) {
            throw new IllegalArgumentException("Invalid inventory slot");
        }
        this.item = item;
        this.quantity = quantity;
    }

    /**
     * Returns the item stored in this slot.
     *
     * @return the item associated with this slot
     */
    public Item getItem() { return item; }

    /**
     * Returns the remaining quantity of the item.
     *
     * @return the number of items currently in this slot
     */
    public int getQuantity() { return quantity; }

    /**
     * Increases the quantity of the item in this slot.
     * Only positive amounts are added.
     *
     * @param amount the number of items to add
     */
    public void addQuantity(int amount) {
        if (amount > 0) quantity += amount;
    }

    /**
     * Attempts to consume one item from this slot.
     * The quantity is decreased by one if an item is available.
     *
     * @return true if an item was successfully consumed,
     *         otherwise false if the slot is empty
     */
    public boolean consumeOne() {
        if (quantity <= 0) return false;
        quantity--;
        return true;
    }
}
