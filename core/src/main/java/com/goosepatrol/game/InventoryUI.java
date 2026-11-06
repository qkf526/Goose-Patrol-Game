
package com.goosepatrol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class InventoryUI implements Disposable{

    private final Inventory inventory;

    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private ScreenViewport viewport;

    public InventoryUI(Inventory inventory) {

        this.inventory = inventory;

        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();

        viewport = new ScreenViewport();

        viewport.update(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            true
        );
    }

public void draw() {

    // Use screen coordinates instead of map coordinates.
    viewport.apply();

    batch.setProjectionMatrix(viewport.getCamera().combined);
    shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

    float gap = 10;
    float margin = 20;

    int slotCount = inventory.getSize();

    if (slotCount == 0) {
        return;
    }

    float screenWidth = viewport.getWorldWidth();
    float availableWidth = screenWidth - 2 * margin;

    float slotSize = Math.max(1, Math.min(
        100,
        (availableWidth - (slotCount - 1) * gap) / slotCount
    ));

    float totalWidth = slotCount * slotSize
        + Math.max(0, slotCount - 1) * gap;

    float startX = screenWidth - totalWidth - margin;
    float startY = 30;

    // Draw slot borders.
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

    for (int i = 0; i < slotCount; i++) {

        InventorySlot slot = inventory.getSlot(i);

        if (slot == null) {
            continue;
        }

        float x = startX + i * (slotSize + gap);

        if (inventory.getSelectedSlot() == slot) {
            shapeRenderer.setColor(Color.YELLOW);
        } else {
            shapeRenderer.setColor(Color.WHITE);
        }

        shapeRenderer.rect(x, startY, slotSize, slotSize);
    }

    shapeRenderer.end();

    // Draw item names and quantities.
    batch.begin();
    font.setColor(Color.WHITE);

    for (int i = 0; i < slotCount; i++) {

        InventorySlot slot = inventory.getSlot(i);

        if (slot == null) {
            continue;
        }

        float x = startX + i * (slotSize + gap);

        String itemName = slot.getItem().getName();

        if (itemName.equals("Flashlight")) {
            itemName = "Light";
        } else if (itemName.equals("Bandage")) {
            itemName = "Heal";
        } else if (itemName.equals("Air Horn")) {
            itemName = "Horn";
        }

        if (slotSize >= 65) {
            font.draw(batch, itemName, x + 5, startY + slotSize - 35);
        }

        font.draw(batch, "x" + slot.getQuantity(),
            x + 5, startY + 25);

        font.draw(batch, "" + (i + 1),
            x + 5, startY + slotSize - 5);
    }

    batch.end();
}

public void resize(int width, int height) {
    viewport.update(width, height, true);
}

@Override
public void dispose() {
    batch.dispose();
    font.dispose();
    shapeRenderer.dispose();
}


}
