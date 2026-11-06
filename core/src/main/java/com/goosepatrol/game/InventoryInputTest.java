
package com.goosepatrol.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InventoryInputTest extends ApplicationAdapter {

    private Inventory inventory;

    @Override
    public void create() {
        inventory = new Inventory();

        inventory.addItem(new Seeds(), 5);
        inventory.addItem(new Flashlight(), 1);
        inventory.addItem(new Bandage(), 2);
        inventory.addItem(new AirHorn(), 1);

        System.out.println("Inventory ready!");

        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean scrolled(float amountX, float amountY) {

                if (amountY > 0) {
                    inventory.scrollSelection(1);
                } else if (amountY < 0) {
                    inventory.scrollSelection(-1);
                }

                InventorySlot selected = inventory.getSelectedSlot();

                if (selected != null) {
                    System.out.println(
                        "Scrolled to: " + selected.getItem().getName()
                    );
                }

                return true;
            }
        });
    }

    @Override
    public void render() {

        boolean changed = false;

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            changed = inventory.selectSlot(0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            changed = inventory.selectSlot(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            changed = inventory.selectSlot(2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            changed = inventory.selectSlot(3);
        }

        if (changed) {
            InventorySlot selected = inventory.getSelectedSlot();

            if (selected != null) {
                System.out.println(
                    "Selected item: " + selected.getItem().getName()
                );
            }
        }
    }
}

