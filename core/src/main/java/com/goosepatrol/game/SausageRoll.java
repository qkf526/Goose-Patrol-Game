package com.goosepatrol.game;

public class SausageRoll extends Item {
    public SausageRoll() {
        super(ItemType.SAUSAGE_ROLL, "Sausage Roll", "Restores health to maximum");
    }

    @Override
    public boolean applyEffect(Player player) {
        if (player == null || player.isDefeated() ||
            player.getHealth() >= player.getMaxHealth()) return false;
        player.heal(player.getMaxHealth());
        return true;
    }
}
