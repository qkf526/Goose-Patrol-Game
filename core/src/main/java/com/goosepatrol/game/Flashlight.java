package com.goosepatrol.game;

public class Flashlight extends Item {
    private boolean isOn;
    public Flashlight(){
        super("Flashlight", "Scares geese away");
        isOn = false;
    }

    public void toggle(){
        isOn =! isOn;
    }

    public boolean isOn(){
        return isOn;
    }
    
}
