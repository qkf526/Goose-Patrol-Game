package com.goosepatrol.game;

public class InventorySlot {

    private final Item item;
    private int quantity;

    public InventorySlot(Item item, int quantity){
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem(){
        return item;
    }
    
    public int getQuantity(){
        return quantity;
    }

    public void addQuantity(int amount){
        if (amount > 0){
            quantity += amount;
        }
    }

    public boolean consumeOne(){
        if (quantity <= 0){
            return false;
        }
        quantity--;
        return true;
    }
}
