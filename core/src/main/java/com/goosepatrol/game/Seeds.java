package com.goosepatrol.game;

public class Seeds extends Item {
    public Seeds() {
        super(ItemType.SEEDS, "Seeds", "Distracts geese");
    }

    public void use() {
        System.out.println("Seeds used. Goose distraction will be connected later.");
    }

    @Override
    public boolean applyEffect(Player player) {
        if (player == null || player.isDefeated()) return false;
        use();
        return true;
    }
}
