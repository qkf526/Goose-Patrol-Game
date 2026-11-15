
package com.goosepatrol.game;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Displays the player's inventory interface, including item icons,
 * selection numbers, remaining quantities, and highlighted slots.
 * Undiscovered Sausage Rolls are displayed as question marks.
 * The interface scales according to the game window size.
 */
public class InventoryUI implements Disposable {

    private final Inventory inventory;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final GlyphLayout quantityLayout;
    private final ShapeRenderer shapes;
    private final ScreenViewport viewport;

    // Use existing item names to find the corresponding icons.
    private final Map<String, Texture> itemIcons = new HashMap<>();

    /**
     * Creates the inventory interface for the specified inventory.
     * Initialises rendering resources, sets up the viewport,
     * and loads the item icons.
     *
     * @param inventory the inventory to display
     */
    public InventoryUI(Inventory inventory) {

        this.inventory = inventory;

        batch = new SpriteBatch();
        font = new BitmapFont();
        quantityLayout = new GlyphLayout();
        shapes = new ShapeRenderer();

        viewport = new ScreenViewport();

        viewport.update(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            true
        );

        // Load each icon only once.
        itemIcons.put(
            "Seeds",
            loadIcon("ui/items/seeds.png")
        );

        itemIcons.put(
            "Air Horn",
            loadIcon("ui/items/air_horn.png")
        );

        itemIcons.put(
            "Bandage",
            loadIcon("ui/items/bandage.png")
        );

        itemIcons.put(
            "Sausage Roll",
            loadIcon("ui/items/sausage_roll.png")
        );
    }

    /**
     * Loads a pixel-art item icon from the specified asset path.
     * Uses nearest-neighbour filtering to preserve sharp pixels.
     *
     * @param path the internal asset path of the icon
     * @return the loaded texture
     * @throws IllegalArgumentException if the icon file does not exist
     */
    private Texture loadIcon(String path) {

        if (!Gdx.files.internal(path).exists()) {

            throw new IllegalArgumentException(
                "Inventory icon not found: " + path
                + " (put it inside your project's assets folder)"
            );
        }

        Texture texture = new Texture(
            Gdx.files.internal(path)
        );

        texture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        return texture;
    }

    /**
     * Draws the inventory interface on the screen.
     * Displays item slots, selection borders, icons, key numbers,
     * and remaining quantities.
     * Shows a question mark for an undiscovered Sausage Roll.
     * Adjusts the layout and text size to fit the window.
     */
    public void draw() {

        int count = inventory.getSize();

        if (count == 0) {
            return;
        }

        viewport.apply();

        batch.setProjectionMatrix(
            viewport.getCamera().combined
        );

        shapes.setProjectionMatrix(
            viewport.getCamera().combined
        );

        // Calculate scaling relative to an 800x480 window.
        float scale = Math.min(
            viewport.getWorldWidth() / 800f,
            viewport.getWorldHeight() / 480f
        );

        // Prevent extreme scaling.
        scale = Math.max(
            0.75f,
            Math.min(scale, 2.5f)
        );

        // Inventory dimensions.
        float slotSize = 50f * scale;
        float gap = 8f * scale;
        float startY = 16f * scale;

        // Calculate the inventory's total width.
        float totalWidth =
            count * slotSize + (count - 1) * gap;

        // Centre the inventory horizontally.
        float startX =
            (viewport.getWorldWidth() - totalWidth) / 2f;

        // Scale the text.
        font.getData().setScale(0.9f * scale);

        // Allow transparent backgrounds.
        Gdx.gl.glEnable(GL20.GL_BLEND);

        Gdx.gl.glBlendFunc(
            GL20.GL_SRC_ALPHA,
            GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        // DRAW SLOT BACKGROUNDS AND BORDERS.
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < count; i++) {

            InventorySlot slot = inventory.getSlot(i);

            float x = startX + i * (slotSize + gap);

            // Check if the slot is selected.
            boolean selected =
                inventory.getSelectedSlot() == slot;

            // Selected items have a thicker gold outline.
            float borderThickness =
                (selected ? 4f : 1f) * scale;

            shapes.setColor(
                selected ? Color.GOLD : Color.LIGHT_GRAY
            );

            // Draw the border.
            shapes.rect(
                x - borderThickness,
                startY - borderThickness,
                slotSize + borderThickness * 2f,
                slotSize + borderThickness * 2f
            );

            // Draw the dark slot interior.
            if (slot.getQuantity() > 0) {

                shapes.setColor(
                    0.08f, 0.12f, 0.17f, 0.90f
                );

            } else {

                shapes.setColor(
                    0.12f, 0.12f, 0.12f, 0.85f
                );
            }

            shapes.rect(
                x,
                startY,
                slotSize,
                slotSize
            );
        }

        shapes.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // DRAW ITEM ICONS, NUMBERS AND QUANTITIES.
        batch.begin();

        for (int i = 0; i < count; i++) {

            InventorySlot slot = inventory.getSlot(i);

            float x = startX + i * (slotSize + gap);

            boolean hasStock = slot.getQuantity() > 0;

            // Retrieve the current item's icon.
            String itemName = slot.getItem().getName();

            Texture icon = itemIcons.get(itemName);

            boolean isSausageRoll = slot.getItem().getType() == ItemType.SAUSAGE_ROLL;

            boolean hiddenSausage =isSausageRoll && !inventory.isSausageRollDiscovered();

            // Make text grey if the slot is empty.
            font.setColor(
                hasStock ? Color.WHITE : Color.GRAY
            );

            // Draw the item selection number.
            font.draw(
                batch,
                String.valueOf(i + 1),
                x + 5f * scale,
                startY + slotSize - 5f * scale
            );


            if (hiddenSausage) {

                // Draw a gold question mark while the item
                // has not been discovered.
                font.getData().setScale(2.3f * scale);
                font.setColor(Color.GOLD);

                quantityLayout.setText(font, "?");

                float questionX =
                    x + (slotSize - quantityLayout.width) / 2f;

                float questionY =
                    startY + slotSize * 0.70f;

                font.draw(
                    batch,
                    "?",
                    questionX,
                    questionY
                );

                // Restore the normal inventory font size.
                font.getData().setScale(0.9f * scale);
                font.setColor(Color.GRAY);

            } else if (icon != null) {

                // Display the real item icon.
                float iconSize = slotSize * 0.48f;

                float iconX =
                    x + (slotSize - iconSize) / 2f;

                float iconY =
                    startY + slotSize * 0.29f;

                // Fade items that have no stock remaining.
                float opacity = hasStock ? 1f : 0.35f;

                batch.setColor(1f, 1f, 1f, opacity);

                batch.draw(
                    icon,
                    iconX,
                    iconY,
                    iconSize,
                    iconSize
                );

                batch.setColor(Color.WHITE);

            } else {

                // Fallback if an icon is missing.
                font.draw(
                    batch,
                    itemName,
                    x + 5f * scale,
                    startY + slotSize * 0.54f
                );
            }


            // Display remaining quantity.
            String quantityText = isSausageRoll
                ? slot.getQuantity() + "x"
                : "x" + slot.getQuantity();

            // Measure text to right-align it.
            quantityLayout.setText(font, quantityText);

            font.draw(
                batch,
                quantityLayout,
                x + slotSize - 5f * scale
                    - quantityLayout.width,
                startY + 16f * scale
            );
        }

        font.setColor(Color.WHITE);
        batch.setColor(Color.WHITE);

        batch.end();
    }

    /**
     * Updates the inventory viewport when the game window is resized.
     *
     * @param width the new window width in pixels
     * @param height the new window height in pixels
     */
    public void resize(int width, int height) {

        viewport.update(width, height, true);
    }

    /**
     * Releases the graphics resources used by the inventory interface,
     * including item textures, the sprite batch, font, and shape renderer.
     */
    @Override
    public void dispose() {

        // Dispose of the four item textures.
        for (Texture icon : itemIcons.values()) {
            icon.dispose();
        }

        itemIcons.clear();

        batch.dispose();
        font.dispose();
        shapes.dispose();
    }
}
