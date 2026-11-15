
package com.goosepatrol.game;

/**
 * Represents an item stored in the player's inventory.
 * Stores the item's type, name, and description.
 * Provides a method for applying an item effect to the player.
 */
public class Item {
    private final ItemType type;
    private final String name;
    private final String description;

    /**
     * Creates an item with the specified type, name, and description.
     *
     * @param type the type of the item
     * @param name the name of the item
     * @param description a description of the item
     */
    public Item(ItemType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    /**
     * Creates an item without a specified item type.
     *
     * @param name the name of the item
     * @param description a description of the item
     */
    public Item(String name, String description) {
        this(null, name, description);
    }

    /**
     * Returns the type of this item.
     *
     * @return the item type, or null if no type was specified
     */
    public ItemType getType() { return type; }

    /**
     * Returns the name of this item.
     *
     * @return the item's name
     */
    public String getName() { return name; }

    /**
     * Returns the description of this item.
     *
     * @return the item's description
     */
    public String getDescription() { return description; }

    /**
     * Attempts to apply this item's effect to the player.
     * The base implementation returns false without applying
     * an effect. Subclasses can override this method to
     * provide their own item behaviour.
     *
     * @param player the player affected by the item
     * @return false in the base implementation
     */
    public boolean applyEffect(Player player) { return false; }
}
