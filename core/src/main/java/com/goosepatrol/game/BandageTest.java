
package com.goosepatrol.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BandageTest {

    public static void main(String[] args) {

        // Create dummy animation frames for testing.
        TextureRegion[][] frames = new TextureRegion[4][4];

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                frames[row][col] = new TextureRegion();
            }
        }

        // Create a test player.
        Player player = new Player(0, 0, frames);

        // Create inventory and add two bandages.
        Inventory inventory = new Inventory();
        inventory.addItem(new Bandage(), 2);

        System.out.println("Health before: " + player.getHealth());
        System.out.println("Bandages before: " + inventory.getQuantity("Bandage"));

        // Simulate taking damage.
        player.takeDamage(2);

        System.out.println("Health after damage: " + player.getHealth());

        // Use a bandage from inventory.
        boolean used = inventory.useBandage(player);

        System.out.println("Bandage used: " + used);
        System.out.println("Health after healing: " + player.getHealth());
        System.out.println("Bandages remaining: " + inventory.getQuantity("Bandage"));
        // Test using a bandage when the player is already at full health.
        System.out.println("--- Full health test ---");

        boolean usedAgain = inventory.useBandage(player);

        System.out.println("Bandage used again: " + usedAgain);
        System.out.println("Health: " + player.getHealth());
        System.out.println("Bandages remaining: " + inventory.getQuantity("Bandage"));
    }
}

