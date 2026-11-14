package com.goosepatrol.game;

import java.util.ArrayList;


public class Inventory {
    private final ArrayList<InventorySlot> slots = new ArrayList<>();
    private int selectedIndex = 0;
    private int totalItemsUsed = 0;
    private boolean sausageRollDiscovered = false;


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

    public boolean isSausageRollDiscovered() {
        return sausageRollDiscovered;
    }


    private boolean sameItem(Item a, Item b) {
        return a.getType() != null && b.getType() != null
            ? a.getType() == b.getType()
            : a.getName().equals(b.getName());
    }

    public InventorySlot getSlot(int index) {
        return index >= 0 && index < slots.size() ? slots.get(index) : null;
    }

    public InventorySlot getSlot(ItemType type) {
        if (type == null) return null;
        for (InventorySlot slot : slots) {
            if (slot.getItem().getType() == type) return slot;
        }
        return null;
    }

    public int getSize() { return slots.size(); }

    public int getQuantity(String name) {
        for (InventorySlot slot : slots) {
            if (slot.getItem().getName().equals(name)) return slot.getQuantity();
        }
        return 0;
    }

    public int getQuantity(ItemType type) {
        InventorySlot slot = getSlot(type);
        return slot == null ? 0 : slot.getQuantity();
    }

    // Legacy method
    public boolean useItem(String name) {
        for (InventorySlot slot : slots) {
            if (slot.getItem().getName().equals(name)) return slot.consumeOne();
        }
        return false;
    }

    public boolean consumeOne(ItemType type) {
        InventorySlot slot = getSlot(type);
        return slot != null && slot.consumeOne();
    }

    /** Only called on successful completion of Player's use animation. */
    public boolean completeUse(ItemType type, Player player) {
        InventorySlot slot = getSlot(type);
        if (slot == null || slot.getQuantity() <= 0) return false;
        if (!slot.getItem().applyEffect(player)) return false;
        // A successful effect consumes one item exactly once.
        if (!slot.consumeOne()) return false;
        totalItemsUsed++;
        return true;
    }

    public int getTotalItemsUsed() { return totalItemsUsed; }

    public boolean selectSlot(int index) {
        if (index < 0 || index >= slots.size()) return false;
        selectedIndex = index;
        return true;
    }

    public InventorySlot getSelectedSlot() { return getSlot(selectedIndex); }

    public void scrollSelection(int direction) {
        if (slots.isEmpty()) return;
        if (direction > 0) selectedIndex = (selectedIndex + 1) % slots.size();
        else if (direction < 0) selectedIndex = (selectedIndex - 1 + slots.size()) % slots.size();
    }

    public boolean useBandage(Player player) {
        if (getQuantity("Bandage") <= 0) return false;
        Bandage bandage = new Bandage();
        return bandage.use(player) && useItem("Bandage");
    }
}
