package com.goosepatrol.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Pause menu with a map and a live inventory panel.
 * Keeps the running GameScreen alive so Resume continues exactly where the
 * game stopped.
 */
public class PauseScreen extends UiScreen {

    private final GameScreen previous;

    private final ButtonMenu menu = new ButtonMenu(
        new String[] {"RESUME", "RESTART", "TUTORIAL", "END GAME"},
        40f, 470f, 240f, 44f, 28f);

    public PauseScreen(GoosePatrolGame game, GameScreen previous) {
        super(game);
        this.previous = previous;
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(previous);
            return;
        }

        switch (menu.update(mouseX, mouseY)) {
            case 0:   // RESUME: same GameScreen, nothing lost
                game.setScreen(previous);
                break;
            case 1:   // RESTART: throw the old game away and start a fresh one
                previous.dispose();
                game.setScreen(new GameScreen(game));
                break;
            case 2:   // TUTORIAL: come back here afterwards
                game.setScreen(new TutorialScreen(game, this));
                break;
            case 3:   // END GAME: abandon the run
                previous.dispose();
                game.setScreen(new MenuScreen(game));
                break;
            default:
                break;
        }
    }

    @Override
    protected void draw() {
        background(".png", 0.35f);

        text(game.titleFont, "PAUSED", 40f, 570f, GOLD);
        menu.draw(this, false);
        textCentered(game.smallFont, "ESC TO RESUME", 160f, 195f, Color.WHITE);
        image(game.texture("ui/menu_student.png"), 100f, 30f, 80f, 110f);

        // Map panel
        panel(320f, 215f, 450f, 355f);
        text(game.uiFont, "MAP", 336f, 558f, GOLD);
        drawMap(336f, 225f, 418f, 290f);

        // Inventory panel
        panel(320f, 40f, 450f, 155f);
        text(game.uiFont, "INVENTORY", 336f, 184f, GOLD);
        drawInventory();
    }

    // Fits the overview picture inside the box without stretching it.
    private void drawMap(float x, float y, float boxW, float boxH) {
        Texture map = game.texture("ui/campus_overview.png");
        if (map == null) return;

        float scale = Math.min(boxW / map.getWidth(), boxH / map.getHeight());
        float w = map.getWidth() * scale;
        float h = map.getHeight() * scale;
        image(map, x + (boxW - w) / 2f, y + (boxH - h) / 2f, w, h);
    }

    private void drawInventory() {
        Inventory inventory = previous.getInventory();

        for (int i = 0; i < inventory.getSize(); i++) {
            InventorySlot slot = inventory.getSlot(i);
            boolean selected = inventory.getSelectedSlot() == slot;
            boolean empty = slot.getQuantity() <= 0;

            float x = 345f + i * 108f;
            float y = 80f;
            float size = 60f;

            // slot box
            ShapeRenderer s = game.shapes;
            s.begin(ShapeRenderer.ShapeType.Filled);
            s.setColor(selected ? GOLD : Color.GRAY);
            s.rect(x, y, size, size);
            s.setColor(0.05f, 0.09f, 0.24f, 1f);
            s.rect(x + 3f, y + 3f, size - 6f, size - 6f);
            s.end();

            // icon (falls back to the first letter until the art exists)
            ItemType type = slot.getItem().getType();
            Texture icon = type == null ? null
                : game.texture("ui/icons/" + type.name().toLowerCase() + ".png");
            if (icon != null) {
                image(icon, x + 8f, y + 8f, size - 16f, size - 16f,
                    empty ? Color.DARK_GRAY : Color.WHITE);
            } else {
                textCentered(game.uiFont, slot.getItem().getName().substring(0, 1),
                    x + size / 2f, y + 42f, empty ? Color.GRAY : Color.WHITE);
            }

            // slot number, quantity, name
            text(game.smallFont, String.valueOf(i + 1), x + 6f, y + size - 4f, Color.WHITE);
            textRight(game.smallFont, "x" + slot.getQuantity(), x + size - 5f, y + 20f, Color.WHITE);
            textCentered(game.smallFont, slot.getItem().getName().toUpperCase(),
                x + size / 2f, 74f, Color.WHITE);
        }
    }
}
