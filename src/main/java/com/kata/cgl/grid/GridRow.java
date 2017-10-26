package com.kata.cgl.grid;

import java.util.List;

public class GridRow {

    private List<GridCell> gridRowCells;

    public GridRow(List<GridCell> gridRowCells) {
        this.gridRowCells = gridRowCells;
    }

    public List<GridCell> getRowCells() {
        return gridRowCells;
    }

}
