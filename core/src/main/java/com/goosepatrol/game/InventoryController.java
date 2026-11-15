package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manages the player's inventory and handles input for selecting
 * and using items during gameplay.
 * It also updates and displays the inventory interface.
 */
public class InventoryController implements Disposable {
    private final Player player;
    private final Inventory inventory;
    private final InventoryUI ui;

    /**
     * Creates an inventory controller for the specified player.
     * Initialises the inventory with starting items and selects
     * the first available inventory slot.
     *
     * @param player the player who owns and uses the inventory
     */

    public InventoryController(Player player) {
        this.player = player;
        this.inventory = new Inventory();

        // Preload the player's inventory with some items.
        inventory.addItem(new Seeds(), 20);
        inventory.addItem(new AirHorn(), 10);
        inventory.addItem(new Bandage(), 7);
        inventory.addItem(new SausageRoll(), 0);

        this.ui = new InventoryUI(inventory);
        select(0); // Match the highlighted first slot to the player's equipped animation.
    }

    /**
     * Returns the inventory managed by this controller.
     *
     * @return the player's inventory
     */
    public Inventory getInventory() { return inventory; }

    /**
     * Attempts to equip the item in the specified inventory slot.
     * The selection is only updated if the slot contains an item
     * and the player successfully equips it.
     *
     * @param index the index of the inventory slot to select
     */
    private void select(int index) {
        InventorySlot slot = inventory.getSlot(index);
        if (slot == null || slot.getQuantity() == 0) return;
        ItemType type = slot.getItem().getType();
        if (type == null) return;

        // Equip first; only update UI selection if Player accepts the change.
        if (player.equipItem(type)) inventory.selectSlot(index);
    }

     /**
     * Handles keyboard input for selecting and using inventory items.
     * Number keys 1-4 select items, 0 unequips the current item,
     * and Space starts using the equipped item.
     * Healing items cannot be used when the player has full health.
     */

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) select(0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) select(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) select(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) select(3);

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) player.unequipItem();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            ItemType type = player.getEquippedItem();
            if (type == null || inventory.getQuantity(type) <= 0) return;

            // Healing items can't be wasted when already at maximum health.
            if ((type == ItemType.BANDAGE || type == ItemType.SAUSAGE_ROLL) &&
                player.getHealth() >= player.getMaxHealth()) {
                System.out.println("Health already full. Healing item not used.");
                return;
            }
            player.startUsingItem();
        }
    }

     /**
     * Processes an item after its use animation has finished.
     * Applies the item's effect and removes one item from the inventory
     * if the action is successful. The item is unequipped when its
     * remaining quantity reaches zero.
     * This method should be called after player.update() each frame.
     *
     * @return the type of item successfully used, or null if
     *         no item use was completed or applied
     */


    public ItemType updateAfterPlayer() {

        // Get the item whose animation just finished.
        ItemType finished = player.consumeCompletedItemUse();

        // No completed action this frame.
        if (finished == null) {
            return null;
        }

        // Apply the item's existing effect and
        // consume one item from the inventory.
        boolean applied = inventory.completeUse(
            finished,
            player
        );

        if (!applied) {
            return null;
        }

        // Put the item away when its stock reaches zero.
        if (inventory.getQuantity(finished) == 0) {
            player.unequipItem();
        }

        // Report which item was successfully used.
        return finished;
    }

        /**
     * Draws the inventory interface on the screen.
     */
    public void draw() { ui.draw(); }

    /**
     * Updates the inventory interface when the window is resized.
     *
     * @param width the new window width in pixels
     * @param height the new window height in pixels
     */
    public void resize(int width, int height) { ui.resize(width, height); }

    /**
     * Releases resources used by the inventory interface.
     */
    @Override public void dispose() { ui.dispose(); }
}
