package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

// This class is responsible for rendering the player's inventory on the screen.
public class InventoryUI implements Disposable {
    private final Inventory inventory;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapes;
    private final ScreenViewport viewport;

    public InventoryUI(Inventory inventory) {
        this.inventory = inventory;
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(0.78f);
        shapes = new ShapeRenderer();
        viewport = new ScreenViewport();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    public void draw() {
        int count = inventory.getSize();
        if (count == 0) return;
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapes.setProjectionMatrix(viewport.getCamera().combined);


        // Calculate scaling relative to an 800x480 window.
        float scale = Math.min(viewport.getWorldWidth() / 800f,viewport.getWorldHeight() / 480f);

        // Prevent the inventory becoming too small or huge.
        scale = Math.max(0.75f,Math.min(scale, 2.5f));

        // Scale the inventory dimensions.
        float slotSize = 60f * scale;
        float gap = 8f * scale;
        font.getData().setScale(0.9f * scale);

        // Calculate total inventory width.
        float totalWidth = count * slotSize + (count - 1) * gap;

        // Centre the inventory horizontally.
        float startX = (viewport.getWorldWidth() - totalWidth) / 2f;

        // Position it above the bottom edge.
        float startY = 16f * scale;



        shapes.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < count; i++) {

            InventorySlot slot = inventory.getSlot(i);

            float x = startX + i * (slotSize + gap);

            // Check whether this slot is selected.
            boolean selected =
                inventory.getSelectedSlot() == slot;

            // Make the selected border thicker.
            float borderThickness = selected
                ? 4f * scale
                : 1f * scale;

            // Gold for selected, grey for other slots.
            shapes.setColor(
                selected
                    ? Color.GOLD
                    : Color.LIGHT_GRAY
            );

            // Draw a slightly larger rectangle behind the slot.
            shapes.rect(
                x - borderThickness,
                startY - borderThickness,
                slotSize + borderThickness * 2f,
                slotSize + borderThickness * 2f
            );

            // Redraw the slot's dark interior.
            shapes.setColor(
                slot.getQuantity() > 0
                    ? new Color(0.08f, 0.12f, 0.17f, 0.90f)
                    : new Color(0.12f, 0.12f, 0.12f, 0.85f)
            );

            shapes.rect(
                x,
                startY,
                slotSize,
                slotSize
            );
        }

        shapes.end();


        shapes.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < count; i++) {
            InventorySlot slot = inventory.getSlot(i);
            float x = startX + i * (slotSize + gap);
            shapes.setColor(inventory.getSelectedSlot() == slot ? Color.YELLOW : Color.LIGHT_GRAY);
            shapes.rect(x, startY, slotSize, slotSize);
        }
        shapes.end();

        batch.begin();

        for (int i = 0; i < count; i++) {

            InventorySlot slot = inventory.getSlot(i);
            float x = startX + i * (slotSize + gap);

            String label = slot.getItem().getName();

            // Shorten longer item names for the HUD.
            if ("Air Horn".equals(label)) {
                label = "Horn";
            }

            if ("Sausage Roll".equals(label)) {
                label = "Roll";
            }

            if ("Bandage".equals(label)) {
                label = "Bandage";
            }


            font.setColor(slot.getQuantity() > 0? Color.WHITE: Color.GRAY);

            // Draw the slot number.
            font.draw(batch,String.valueOf(i + 1),x + 5f * scale,startY + slotSize - 5f * scale);

            // Draw the item name.
            // Always display the item name.
            font.draw(batch,label,x + 6f * scale,startY + slotSize * 0.58f);

            // Draw the item quantity.
            font.draw(batch,"x" + slot.getQuantity(),x + 40f * scale,startY + 17f * scale);
        }

        font.setColor(Color.WHITE);

        batch.end();

    }

    public void resize(int width, int height) { 
        viewport.update(width, height, true); 
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapes.dispose();
    }
}
