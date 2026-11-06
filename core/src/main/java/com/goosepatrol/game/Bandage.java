package com.goosepatrol.game;

public class Bandage extends Item {

    private final int healingAmount;

    public Bandage() {
        super("Bandage", "Restores player health");
        healingAmount = 30;
    }

    public int heal(int currentHealth, int maxHealth) {
        if (maxHealth <= 0 || currentHealth < 0 || currentHealth >= maxHealth) {
            return currentHealth;
        }

        return Math.min(currentHealth + healingAmount, maxHealth);
    }
}