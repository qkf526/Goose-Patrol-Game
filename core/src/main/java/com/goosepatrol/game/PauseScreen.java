package com.goosepatrol.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * pause menu, shown over a running game
 *
 * keeps paused game in memory so clicking resume returns to same poingt in match
 */
public class PauseScreen extends UiScreen {

    /**
     * paused game, kept alive so resume can return to it unchanged
     */
    private final GameScreen previous;

    /**
     * resume index 0, restart 1, tutorial 2, end game 2
     */
    private final ButtonMenu menu = new ButtonMenu(
        new String[] {"RESUME", "RESTART", "TUTORIAL", "END GAME"},
        40f, 470f, 240f, 44f, 28f);

    /**
     * creates screen
     * @param game
     * @param previous allows game to resume - game not disposed of while pasued
     */
    public PauseScreen(GoosePatrolGame game, GameScreen previous) {
        super(game);
        this.previous = previous;
    }

    /**
     * handles esc and menu buttons
     *
     * old game disposed of in restart and endgame
     * not disposed of for resume and tutorial
     * @param delta seconds since last frame
     */
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

    /**
     * draws background, heading, buttons w esc hit and picture on left
     * draws map and inventory panels
     */
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

    /**
     * draws campus overview, scaled to fit w/o stretching and centred
     *
     * @param x left edge of box
     * @param y bottom edge of box
     * @param boxW width of box
     * @param boxH height of box
     */
    private void drawMap(float x, float y, float boxW, float boxH) {
        Texture map = game.texture("ui/campus_overview.png");
        if (map == null) return;

        float scale = Math.min(boxW / map.getWidth(), boxH / map.getHeight());
        float w = map.getWidth() * scale;
        float h = map.getHeight() * scale;
        image(map, x + (boxW - w) / 2f, y + (boxH - h) / 2f, w, h);
    }

    /**
     * draws a box per inventory slot - reads item count from paused game
     * box shows item icon, slot number, name and quantity
     * gold border on chosen slot
     */
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
