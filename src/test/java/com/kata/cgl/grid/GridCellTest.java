package com.kata.cgl.grid;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.kata.cgl.grid.GridCell;
import com.kata.cgl.grid.GridCellState;
import com.kata.cgl.grid.GridCoordinate;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class GridCellTest {

    int standardGridColumnCount = 8;
    int standardGridRowCount = 6;

    @Test
    public void defaultGridCellInitialization() {
        Integer xCoordinate = 1;
        Integer yCoordinate = 2;

        GridCell gridCell = new GridCell(new GridCoordinate(xCoordinate, yCoordinate), standardGridRowCount, standardGridColumnCount);

        assertEquals(xCoordinate, gridCell.getxCoordinate());
        assertEquals(yCoordinate, gridCell.getyCoordinate());
        assertEquals(GridCellState.DEAD, gridCell.getGridCellState());
    }

    @Test
    public void gridCellInitializationWithCellState() {
        Integer xCoordinate = 1;
        Integer yCoordinate = 2;
        GridCellState expectedGrillCellState = GridCellState.ALIVE;

        GridCell gridCell = new GridCell(new GridCoordinate(xCoordinate, yCoordinate), standardGridRowCount, standardGridColumnCount, expectedGrillCellState);

        assertEquals(xCoordinate, gridCell.getxCoordinate());
        assertEquals(yCoordinate, gridCell.getyCoordinate());
        assertEquals(expectedGrillCellState, gridCell.getGridCellState());
    }

    @Test
    public void killChangesGridStateToDead() {
        GridCell gridCell = new GridCell(new GridCoordinate(1, 2), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE);
        assertEquals(GridCellState.ALIVE, gridCell.getGridCellState());

        gridCell.kill();
        assertEquals(GridCellState.DEAD, gridCell.getGridCellState());
    }

    @Test
    public void breatheLifeChangesGridStateToAlive() {
        GridCell gridCell = new GridCell(new GridCoordinate(1, 2), standardGridRowCount, standardGridColumnCount, GridCellState.DEAD);
        assertEquals(GridCellState.DEAD, gridCell.getGridCellState());

        gridCell.breatheLife();
        assertEquals(GridCellState.ALIVE, gridCell.getGridCellState());
    }

}
