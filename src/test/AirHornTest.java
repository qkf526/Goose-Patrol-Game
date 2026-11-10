package com.goosepatrol.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AirHornTest {
    @Test
    void testGooseInRange() {
        AirHorn airHorn = new AirHorn();    
        boolean inRange = airHorn.isGooseInRange(0f, 0f, 50f, 50f);
        assertTrue(inRange, "Goose in range should return true"); 
    }

    @Test
    void testGooseOutOfRange() {
        AirHorn airHorn = new AirHorn();
        boolean inRange = airHorn.isGooseInRange(0f, 0f, 150f, 150f);
        assertFalse(inRange, "Goose out of range should return false");
    }
}