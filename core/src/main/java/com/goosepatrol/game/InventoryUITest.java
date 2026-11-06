
package com.goosepatrol.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class InventoryUITest extends ApplicationAdapter {

    private SpriteBatch batch;
    private BitmapFont font;
    private Inventory inventory;
    private ShapeRenderer shapeRenderer;
    private ScreenViewport viewport;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        viewport = new ScreenViewport();
        viewport.update(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
    true
);

        inventory = new Inventory();

        inventory.addItem(new Seeds(), 5);
        inventory.addItem(new Flashlight(), 1);
        inventory.addItem(new Bandage(), 2);
        inventory.addItem(new AirHorn(), 1);
        Gdx.input.setInputProcessor(new InputAdapter() {

    @Override
    public boolean scrolled(float amountX, float amountY) {

        if (amountY > 0) {
            inventory.scrollSelection(1);
        } else if (amountY < 0) {
            inventory.scrollSelection(-1);
        }

        return true;
    }
});
    }


@Override
public void render() {

    // 1. Keyboard selection
    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
        inventory.selectSlot(0);
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
        inventory.selectSlot(1);
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
        inventory.selectSlot(2);
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
        inventory.selectSlot(3);
    }

    // 2. Clear background
    viewport.apply();

batch.setProjectionMatrix(
    viewport.getCamera().combined
);

shapeRenderer.setProjectionMatrix(
    viewport.getCamera().combined
);
    Gdx.gl.glClearColor(0.1f, 0.15f, 0.2f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // 3. Inventory layout
    
float gap = 10;
float margin = 20;

int slotCount = inventory.getSize();

// Get the width from the viewport
float screenWidth = viewport.getWorldWidth();

float availableWidth = screenWidth - 2 * margin;

float slotSize = 100;

if (slotCount > 0) {
    slotSize = Math.min(
        100,
        (availableWidth - (slotCount - 1) * gap) / slotCount
    );
}

float totalWidth = slotCount * slotSize
    + Math.max(0, slotCount - 1) * gap;

float startX = screenWidth - totalWidth - margin;
float startY = 30;

    // 4. Draw slot borders
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

    for (int i = 0; i < inventory.getSize(); i++) {

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

    // 5. Draw item names and quantities
    batch.begin();
    font.setColor(Color.WHITE);

    for (int i = 0; i < inventory.getSize(); i++) {

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
            font.draw(batch, itemName, x + 5, startY + 65);
        }

        font.draw(batch, "x" + slot.getQuantity(),
            x + 5, startY + 25);

        font.draw(batch, "" + (i + 1),
            x + 5, startY + 95);
    }

    batch.end();
}

@Override
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
