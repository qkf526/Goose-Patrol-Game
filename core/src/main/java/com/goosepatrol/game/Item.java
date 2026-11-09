package com.goosepatrol.game;

/** Inventory data and the effect performed when a completed use succeeds. */
public class Item {
    private final ItemType type;
    private final String name;
    private final String description;

    public Item(ItemType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public Item(String name, String description) {
        this(null, name, description);
    }

    public ItemType getType() { return type; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    /** True only when this item produced a valid effect and may be consumed. */
    public boolean applyEffect(Player player) { return false; }
}
