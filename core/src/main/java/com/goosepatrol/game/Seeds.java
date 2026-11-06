package com.goosepatrol.game;

public class Seeds extends Item {
    public Seeds(){
        super("Seeds", "Distracts geese");
    }

    public void use(){
        System.out.println("Seeds used! The geese are distracted.");
    }
    
}
