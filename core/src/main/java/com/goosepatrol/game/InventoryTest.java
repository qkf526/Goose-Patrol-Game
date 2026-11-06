package com.goosepatrol.game;

public class InventoryTest {

    public static void main(String[] args) {

        Inventory inventory = new Inventory();

        Item seeds = new Item("Seeds", "Distracts geese");
        Item flashlight = new Item("Flashlight", "Scares geese away");

        inventory.addItem(seeds, 5);
        inventory.addItem(flashlight, 1);

        System.out.println("Inventory slots: " + inventory.getSize());
        System.out.println("Seeds before: " + inventory.getQuantity("Seeds"));

        inventory.useItem("Seeds");

        System.out.println("Seeds after: " + inventory.getQuantity("Seeds"));

        inventory.addItem(seeds, 3);

        System.out.println("Seeds after pickup: " + inventory.getQuantity("Seeds"));

        InventorySlot firstSlot = inventory.getSlot(0);

        if (firstSlot != null) {
            System.out.println("First slot: " + firstSlot.getItem().getName());
        }

        Seeds testSeeds = new Seeds();

        System.out.println("Item name: " + testSeeds.getName());

        System.out.println(
            "Item description: " + testSeeds.getDescription()
);

        testSeeds.use();

        Flashlight flashlightItem = new Flashlight();

        System.out.println(
            "Flashlight initially: " + flashlightItem.isOn()
);

        flashlightItem.toggle();

        System.out.println(
            "Flashlight after toggle: " + flashlightItem.isOn()
);

        flashlightItem.toggle();

        System.out.println(
            "Flashlight after second toggle: " + flashlightItem.isOn()
);

        Bandage bandage = new Bandage();

        int playerHealth = 40;
        int maxHealth = 100;

        System.out.println("Health before: " + playerHealth);

        playerHealth = bandage.heal(playerHealth, maxHealth);

        System.out.println("Health after: " + playerHealth);

        playerHealth = bandage.heal(90, maxHealth);

        System.out.println("Health near maximum: " + playerHealth);

        playerHealth = bandage.heal(100, maxHealth);
 
        System.out.println("Health when full: " + playerHealth);

        AirHorn airHorn = new AirHorn();

boolean goose1 = airHorn.isGooseInRange(
    0f, 0f, 30f, 40f
);

boolean goose2 = airHorn.isGooseInRange(
    0f, 0f, 200f, 200f
);

boolean goose3 = airHorn.isGooseInRange(
    0f, 0f, 60f, 80f
);

System.out.println("Goose 1 in range: " + goose1);
System.out.println("Goose 2 in range: " + goose2);
System.out.println("Goose 3 in range: " + goose3);

Inventory selectionTest = new Inventory();

selectionTest.addItem(new Seeds(), 5);
selectionTest.addItem(new Flashlight(), 1);
selectionTest.addItem(new Bandage(), 2);

System.out.println(
    "Initially selected: "
    + selectionTest.getSelectedSlot().getItem().getName()
);

selectionTest.selectSlot(1);

System.out.println(
    "After selecting slot 1: "
    + selectionTest.getSelectedSlot().getItem().getName()
);

selectionTest.selectSlot(2);

System.out.println(
    "After selecting slot 2: "
    + selectionTest.getSelectedSlot().getItem().getName()
);

boolean valid = selectionTest.selectSlot(10);

System.out.println("Invalid selection accepted: " + valid);

    }
    
}