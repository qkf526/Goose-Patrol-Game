package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Disposable;

// This class manages the player's inventory and handles input for selecting and using items.
public class InventoryController implements Disposable {
    private final Player player;
    private final Inventory inventory;
    private final InventoryUI ui;

    public InventoryController(Player player) {
        this.player = player;
        this.inventory = new Inventory();

        // Preload the player's inventory with some items.
        inventory.addItem(new Seeds(), 6);
        inventory.addItem(new AirHorn(), 3);
        inventory.addItem(new Bandage(), 2);
        inventory.addItem(new SausageRoll(), 2);

        this.ui = new InventoryUI(inventory);
        select(0); // Match the highlighted first slot to the player's equipped animation.
    }

    public Inventory getInventory() { return inventory; }

    private void select(int index) {
        InventorySlot slot = inventory.getSlot(index);
        if (slot == null || slot.getQuantity() == 0) return;
        ItemType type = slot.getItem().getType();
        if (type == null) return;

        // Equip first; only update UI selection if Player accepts the change.
        if (player.equipItem(type)) inventory.selectSlot(index);
    }

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

    /** Call immediately AFTER player.update(), once per game frame. */

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


    public void draw() { ui.draw(); }
    public void resize(int width, int height) { ui.resize(width, height); }
    @Override public void dispose() { ui.dispose(); }
}
