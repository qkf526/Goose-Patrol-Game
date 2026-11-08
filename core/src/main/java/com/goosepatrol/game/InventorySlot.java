package com.goosepatrol.game;

/** One slot stores one item type and its remaining quantity. */
public class InventorySlot {
    private final Item item;
    private int quantity;

    public InventorySlot(Item item, int quantity) {
        if (item == null || quantity < 0) {
            throw new IllegalArgumentException("Invalid inventory slot");
        }
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() { return item; }
    public int getQuantity() { return quantity; }

    public void addQuantity(int amount) {
        if (amount > 0) quantity += amount;
    }

    public boolean consumeOne() {
        if (quantity <= 0) return false;
        quantity--;
        return true;
    }
}
