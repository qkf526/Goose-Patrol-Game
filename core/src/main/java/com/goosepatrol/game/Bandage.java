package com.goosepatrol.game;

public class Bandage extends Item {

    private final int healingAmount = 2;

    public Bandage() {
        super("Bandage", "Restores 2 health points");
    }

    public boolean use(Player player) {

        if (player == null) {
            return false;
        }

        if (player.isDefeated()) {
            return false;
        }

        if (player.getHealth() >= player.getMaxHealth()) {
            return false;
        }

        player.heal(healingAmount);
        return true;
    }
}
