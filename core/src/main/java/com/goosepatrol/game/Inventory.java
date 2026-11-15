package com.goosepatrol.game;

import java.util.ArrayList;

/**
 * Manages the player's inventory, including item storage,
 * quantities, selection, and item usage.
 * Also tracks the number of items used and whether
 * the secret Sausage Roll item has been discovered.
 */
public class Inventory {
    private final ArrayList<InventorySlot> slots = new ArrayList<>();
    private int selectedIndex = 0;
    private int totalItemsUsed = 0;
    private boolean sausageRollDiscovered = false;

     /**
     * Adds a specified quantity of an item to the inventory.
     * If the item already exists, its quantity is increased.
     * Otherwise, a new inventory slot is created.
     * Receiving a Sausage Roll reveals the secret item.
     *
     * @param item the item to add to the inventory
     * @param quantity the number of items to add
     */
    public void addItem(Item item, int quantity) {

        // Negative quantities are invalid,
        // but zero is allowed for an empty slot.
        if (item == null || quantity < 0) {
            return;
        }

        // Reveal the secret item when the player
        // actually receives a Sausage Roll.
        if (item.getType() == ItemType.SAUSAGE_ROLL
            && quantity > 0) {

            sausageRollDiscovered = true;
        }

        // If this item already has a slot,
        // increase its quantity instead.
        for (InventorySlot slot : slots) {

            if (sameItem(slot.getItem(), item)) {

                slot.addQuantity(quantity);
                return;
            }
        }

        // Otherwise create a new slot.
        // The initial Sausage Roll slot has quantity 0.
        slots.add(new InventorySlot(item, quantity));
    }

     /**
     * Checks whether the secret Sausage Roll item
     * has been discovered by the player.
     *
     * @return true if the Sausage Roll has been discovered,
     *         otherwise false
     */

    public boolean isSausageRollDiscovered() {
        return sausageRollDiscovered;
    }
     /**
     * Checks whether two items represent the same item type.
     * Compares their item types when available,
     * or their names otherwise.
     *
     * @param a the first item to compare
     * @param b the second item to compare
     * @return true if both items represent the same item,
     *         otherwise false
     */
    private boolean sameItem(Item a, Item b) {
        return a.getType() != null && b.getType() != null
            ? a.getType() == b.getType()
            : a.getName().equals(b.getName());
    }

        /**
     * Retrieves an inventory slot by its index.
     *
     * @param index the index of the requested slot
     * @return the inventory slot, or null if the index is invalid
     */
    public InventorySlot getSlot(int index) {
        return index >= 0 && index < slots.size() ? slots.get(index) : null;
    }

        /**
     * Retrieves the inventory slot containing the specified item type.
     *
     * @param type the type of item to find
     * @return the matching inventory slot, or null if not found
     */
    public InventorySlot getSlot(ItemType type) {
        if (type == null) return null;
        for (InventorySlot slot : slots) {
            if (slot.getItem().getType() == type) return slot;
        }
        return null;
    }

    /**
     * Returns the number of inventory slots.
     *
     * @return the total number of slots
     */
    public int getSize() { return slots.size(); }

    public int getQuantity(String name) {
        for (InventorySlot slot : slots) {
            if (slot.getItem().getName().equals(name)) return slot.getQuantity();
        }
        return 0;
    }

    /**
     * Returns the available quantity of a specified item type.
     *
     * @param type the type of item
     * @return the available quantity, or zero if not found
     */
    public int getQuantity(ItemType type) {
        InventorySlot slot = getSlot(type);
        return slot == null ? 0 : slot.getQuantity();
    }

        /**
     * Attempts to consume one item identified by its name.
     * This is a legacy method for item consumption.
     *
     * @param name the name of the item to consume
     * @return true if one item was consumed, otherwise false
     */
    public boolean useItem(String name) {
        for (InventorySlot slot : slots) {
            if (slot.getItem().getName().equals(name)) return slot.consumeOne();
        }
        return false;
    }
    /**
     * Attempts to consume one item of the specified type.
     *
     * @param type the type of item to consume
     * @return true if an item was consumed, otherwise false
     */
    public boolean consumeOne(ItemType type) {
        InventorySlot slot = getSlot(type);
        return slot != null && slot.consumeOne();
    }
    /**
     * Completes an item use after the player's use animation finishes.
     * Applies the item's effect and consumes one item if successful.
     * Increments the total number of items used.
     *
     * @param type the type of item being used
     * @param player the player receiving or performing the item effect
     * @return true if the effect was applied and an item was consumed,
     *         otherwise false
     */
    public boolean completeUse(ItemType type, Player player) {
        InventorySlot slot = getSlot(type);
        if (slot == null || slot.getQuantity() <= 0) return false;
        if (!slot.getItem().applyEffect(player)) return false;
        // A successful effect consumes one item exactly once.
        if (!slot.consumeOne()) return false;
        totalItemsUsed++;
        return true;
    }

        /**
     * Returns the total number of items successfully used.
     *
     * @return the number of items used during gameplay
     */
    public int getTotalItemsUsed() { return totalItemsUsed; }


    /**
     * Selects an inventory slot by its index.
     *
     * @param index the index of the slot to select
     * @return true if the slot was selected,
     *         otherwise false if the index is invalid
     */
    public boolean selectSlot(int index) {
        if (index < 0 || index >= slots.size()) return false;
        selectedIndex = index;
        return true;
    }

    /**
     * Returns the currently selected inventory slot.
     *
     * @return the selected inventory slot, or null if invalid
     */
    public InventorySlot getSelectedSlot() { return getSlot(selectedIndex); }

    /**
     * Moves the inventory selection forward or backward.
     * The selection wraps around when reaching either end.
     *
     * @param direction a positive value to move forward,
     *                  or a negative value to move backward
     */
    public void scrollSelection(int direction) {
        if (slots.isEmpty()) return;
        if (direction > 0) selectedIndex = (selectedIndex + 1) % slots.size();
        else if (direction < 0) selectedIndex = (selectedIndex - 1 + slots.size()) % slots.size();
    }

     /**
     * Attempts to use a Bandage to heal the player.
     * A Bandage is consumed only if the healing succeeds.
     *
     * @param player the player to heal
     * @return true if the Bandage was successfully used,
     *         otherwise false
     */
    public boolean useBandage(Player player) {
        if (getQuantity("Bandage") <= 0) return false;
        Bandage bandage = new Bandage();
        return bandage.use(player) && useItem("Bandage");
    }
}
