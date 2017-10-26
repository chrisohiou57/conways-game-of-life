package com.kata.cgl.grid;

public enum GridCellState {
    DEAD,
    ALIVE;

	public static GridCellState fromString(String s) {
		if ("DEAD".equalsIgnoreCase(s)) {
			return DEAD;
		} else if ("ALIVE".equalsIgnoreCase(s)) {
			return ALIVE;
		}
		
		return null;
    }
    
}