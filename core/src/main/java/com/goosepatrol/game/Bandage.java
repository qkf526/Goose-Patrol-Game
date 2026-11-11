package com.goosepatrol.game;

public class Bandage extends Item {
    private final int healingAmount = 2;

    public Bandage() {
        super(ItemType.BANDAGE, "Bandage", "Restores 2 health points");
    }

    // ***
    public boolean use(Player player) {
        if (player == null || player.isDefeated() ||
            player.getHealth() >= player.getMaxHealth()) return false;
        player.heal(healingAmount);
        return true;
    }

    @Override
    public boolean applyEffect(Player player) {
        return use(player);
    }
}