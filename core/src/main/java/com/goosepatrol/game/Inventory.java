package com.goosepatrol.game;

import java.util.ArrayList;

public class Inventory {

    private final ArrayList<InventorySlot> slots;
    private int selectedIndex;

    public Inventory() {
        slots = new ArrayList<>();
        selectedIndex = 0;
    }

    public void addItem(Item item, int quantity) {
        if (item == null || quantity <= 0) {
            return;
        }

        for (InventorySlot slot : slots) {
            if (slot.getItem().getName().equals(item.getName())) {
                slot.addQuantity(quantity);
                return;
            }
        }

        slots.add(new InventorySlot(item, quantity));
    }

    public InventorySlot getSlot(int index) {
        if (index < 0 || index >= slots.size()) {
            return null;
        }

        return slots.get(index);
    }

    public int getSize() {
        return slots.size();
    }

    public int getQuantity(String itemName) {
        for (InventorySlot slot : slots) {
            if (slot.getItem().getName().equals(itemName)) {
                return slot.getQuantity();
            }
        }

        return 0;
    }

    public boolean useItem(String itemName) {
        for (InventorySlot slot : slots) {
            if (slot.getItem().getName().equals(itemName)) {
                return slot.consumeOne();
            }
        }

        return false;
    }

    public boolean selectSlot(int index){
        if (index < 0 || index >= slots.size()){
            return false;
        }
        selectedIndex = index;
        return true;
    }

    public InventorySlot getSelectedSlot(){
        return getSlot(selectedIndex);
    }

    public void scrollSelection(int direction){
        if (slots.isEmpty()){
            return;
        }
        if (direction > 0){
            selectedIndex = (selectedIndex + 1) % slots.size();
        }else if (direction < 0){
            selectedIndex = 
                (selectedIndex - 1 + slots.size()) % slots.size();
        }
    }
    public boolean useBandage(Player player){
        if (player == null){
            return false;
        }
        if (getQuantity("Bandage") <= 0){
            return false;
        }
        Bandage bandage = new Bandage();
        if (!bandage.use(player)){
            return false;
        }
        return useItem("Bandage");
    }
}