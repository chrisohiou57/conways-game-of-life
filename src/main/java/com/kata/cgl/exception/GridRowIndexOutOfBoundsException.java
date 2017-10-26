package com.kata.cgl.exception;

public class GridRowIndexOutOfBoundsException extends Exception {

	private static final long serialVersionUID = 1L;

	public GridRowIndexOutOfBoundsException() {
        super("The the row index you provided is out of bounds for the grid dimensions");
    }

}
