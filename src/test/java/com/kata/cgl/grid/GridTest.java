package com.kata.cgl.grid;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import com.kata.cgl.exception.GridRowIndexOutOfBoundsException;

@RunWith(SpringRunner.class)
public class GridTest {

    @Mock
    private Grid partialMockGrid;

    int standardGridColumnCount = 8;
    int standardGridRowCount = 6;
    int expectedStandardGridCellCount = standardGridColumnCount * standardGridRowCount;

    private Grid getEmptyStandardGridMock() {
        return new Grid(standardGridRowCount, standardGridColumnCount);
    }

    private List<GridCell> getMockGridCells() {
        List<GridCell> gridCells = new ArrayList<>();

        for (int xCoordinate=0; xCoordinate < standardGridColumnCount; xCoordinate++) {
            for (int yCoordinate=0; yCoordinate < standardGridRowCount; yCoordinate++) {
                GridCellState gridCellState = yCoordinate % 2 == 0 ? GridCellState.ALIVE : GridCellState.DEAD;
                gridCells.add(new GridCell(new GridCoordinate(xCoordinate, yCoordinate), standardGridRowCount, standardGridColumnCount, gridCellState));
            }
        }

        return gridCells;
    }

    @Test
    public void emptyGridInitialization() {
        Grid emptyGrid = getEmptyStandardGridMock();
        assertEquals(standardGridRowCount, emptyGrid.getRowCount());
        assertEquals(standardGridColumnCount, emptyGrid.getColumnCount());
        assertEquals(expectedStandardGridCellCount, emptyGrid.getCellCount());

        for (GridCell gridCell : emptyGrid.retrieveGridCells()) {
            assertTrue(gridCell.getxCoordinate() >= 0);
            assertTrue(gridCell.getxCoordinate() < standardGridColumnCount);
            assertTrue(gridCell.getyCoordinate() >= 0);
            assertTrue(gridCell.getyCoordinate() < standardGridRowCount);
        }
    }

    @Test
    public void gridInitializationFromGridCells() {
        List<GridCell> gridCells = getMockGridCells();
        Grid grid = new Grid(standardGridRowCount, standardGridColumnCount, gridCells);

        for (int i=0; i < gridCells.size(); i++) {
            GridCell originalGridCell = gridCells.get(i);
            grid.retrieveGridCells().get(i).equals(originalGridCell);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void gridCellsFixedAfterEmptyGridInitialization() {
        Grid emptyGrid = getEmptyStandardGridMock();
        emptyGrid.retrieveGridCells().remove(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void gridCellsFixedAfterInitializationFromGridCells() {
        List<GridCell> gridCells = getMockGridCells();
        Grid grid = new Grid(standardGridRowCount, standardGridColumnCount, gridCells);
        grid.retrieveGridCells().remove(0);
    }

    @Test
    public void buildGridRowUsesYCoordinateAsRowIndex() throws GridRowIndexOutOfBoundsException {
        Grid standardGrid = getEmptyStandardGridMock();

        for (int y=0; y < standardGridRowCount; y++) {
            final int rowIndex = y;
            GridRow gridRow = standardGrid.buildGridRow(rowIndex);

            boolean gridCellWithIncorrectYCoordinateFound = gridRow
                    .getRowCells()
                    .stream()
                    .anyMatch(gridRowCell -> !gridRowCell.getyCoordinate().equals(rowIndex));

            assertFalse(gridCellWithIncorrectYCoordinateFound);
        }
    }

    @Test(expected = GridRowIndexOutOfBoundsException.class)
    public void buildingGridRowWithInvalidIndexReturnsEmptyList() throws GridRowIndexOutOfBoundsException {
        int lowerOutOfBoundsIndex = -1;
        int upperOutOfBoundsIndex = standardGridRowCount;

        Grid standardGrid = getEmptyStandardGridMock();
        standardGrid.buildGridRow(lowerOutOfBoundsIndex);
        standardGrid.buildGridRow(upperOutOfBoundsIndex);
    }

    @Test
    public void buildGridRowsHandlesAllRows() throws GridRowIndexOutOfBoundsException {
        Grid standardGrid = getEmptyStandardGridMock();
        List<GridRow> gridRows = standardGrid.getGridRows();
        assertEquals(standardGridRowCount, gridRows.size());
    }

    @Test
    public void buildGridRowOrdersCellsByXCoordinateAscending() throws GridRowIndexOutOfBoundsException {
        Grid standardGrid = getEmptyStandardGridMock();

        for (int y=0; y < standardGridRowCount; y++) {
            Integer correctXCoordinate = 0;
            GridRow gridRow = standardGrid.buildGridRow(y);

            for (GridCell gridCell : gridRow.getRowCells()) {
                assertEquals(correctXCoordinate, gridCell.getxCoordinate());
                correctXCoordinate++;
            }
        }
    }

    @Test
    public void getGridCoordinates() {
        Grid standardGrid = getEmptyStandardGridMock();
        List<GridCoordinate> allGridCoordinates = standardGrid.retrieveGridCoordinates();
        assertEquals(expectedStandardGridCellCount, allGridCoordinates.size());

        int gridCoordinateIndex = 0;
        for (int x=0; x < standardGridColumnCount; x++) {
            for (int y=0; y < standardGridRowCount; y++) {
                GridCoordinate gridCoordinate = allGridCoordinates.get(gridCoordinateIndex);
                assertEquals(x, gridCoordinate.getxCoordinate().intValue());
                assertEquals(y, gridCoordinate.getyCoordinate().intValue());
                gridCoordinateIndex++;
            }
        }
    }

    @Test
    public void findGridCellNeighborsReturnsThreeOrFiveNeighborsForBoundaryCells() {
        Grid standardGrid = getEmptyStandardGridMock();
        List<GridCell> allGridCells = standardGrid.retrieveGridCells();
        List<GridCell> allBoundaryCells = allGridCells.stream().filter(agc -> agc.isBoundaryCell()).collect(Collectors.toList());

        allBoundaryCells.stream().forEach(bc -> {
            List<GridCell> neighborGridCells = standardGrid.findGridCellNeighbors(bc);
            if (bc.isCornerCell()) {
                assertEquals(3, neighborGridCells.size());
            } else {
                assertEquals(5, neighborGridCells.size());
            }
        });
    }

    @Test
    public void findGridCellNeighborsReturnsEightNeighborsForNonBoundaryCells(){
        Grid standardGrid = getEmptyStandardGridMock();
        List<GridCell> allGridCells = standardGrid.retrieveGridCells();
        List<GridCell> allNonBoundaryCells = allGridCells.stream().filter(agc -> !agc.isBoundaryCell()).collect(Collectors.toList());

        allNonBoundaryCells.stream().forEach(nbc ->{
            assertEquals(8, standardGrid.findGridCellNeighbors(nbc).size());
        });
    }

    @Test
    public void findLiveGridCellNeighborsOnlyReturnsAliveGridCells() {
        List<GridCell> aliveAndDeadGridCellNeighbors = Arrays.asList(
                new GridCell(new GridCoordinate(0,1), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,2), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,3), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,4), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,5), standardGridRowCount, standardGridColumnCount, GridCellState.DEAD),
                new GridCell(new GridCoordinate(0,6), standardGridRowCount, standardGridColumnCount, GridCellState.DEAD),
                new GridCell(new GridCoordinate(0,7), standardGridRowCount, standardGridColumnCount, GridCellState.DEAD),
                new GridCell(new GridCoordinate(0,8), standardGridRowCount, standardGridColumnCount, GridCellState.DEAD)
        );

        Grid mockGrid = Mockito.mock(Grid.class);
        Mockito.when(mockGrid.findGridCellNeighbors(Mockito.any())).thenReturn(aliveAndDeadGridCellNeighbors);
        Mockito.when(mockGrid.findLiveGridCellNeighbors(Mockito.any())).thenCallRealMethod();
        GridCell anyGridCell = new GridCell(new GridCoordinate(0,0), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE);

        List<GridCell> liveGridCells = mockGrid.findLiveGridCellNeighbors(anyGridCell);
        assertTrue(liveGridCells.stream().noneMatch(lgc -> GridCellState.DEAD == lgc.getGridCellState()));
    }

    @Test
    public void findUpperNeighborCoordinateExcludesInvalidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int anyValidXCoordinate = 0;
        int upperBoundaryYCoordinate = standardGridRowCount - 1;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findUpperNeighborCoordinate(anyValidXCoordinate, upperBoundaryYCoordinate);
        assertFalse(potentialGridCell.isPresent());
    }

    @Test
    public void findUpperNeighborCoordinateIncludesValidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int anyValidXCoordinate = 0;
        int nonUpperBoundaryYCoordinate = standardGridRowCount - 2;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findUpperNeighborCoordinate(anyValidXCoordinate, nonUpperBoundaryYCoordinate);
        assertTrue(potentialGridCell.isPresent());

        GridCoordinate neighborCoordinate = potentialGridCell.get();
        assertTrue(anyValidXCoordinate == neighborCoordinate.getxCoordinate());
        assertTrue(nonUpperBoundaryYCoordinate + 1 == neighborCoordinate.getyCoordinate());
    }

    @Test
    public void findUpperRightNeighborCoordinateExcludesInvalidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int anyValidXCoordinate = 0;
        int upperBoundaryYCoordinate = standardGridRowCount - 1;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findUpperRightNeighborCoordinate(anyValidXCoordinate, upperBoundaryYCoordinate);
        assertFalse(potentialGridCell.isPresent());

        int rightBoundaryXCoordinate = standardGridColumnCount - 1;
        int anyValidYCoordinate = 0;

        potentialGridCell = standardGrid.findUpperRightNeighborCoordinate(rightBoundaryXCoordinate, anyValidYCoordinate);
        assertFalse(potentialGridCell.isPresent());
    }

    @Test
    public void findUpperRightNeighborCoordinateIncludesValidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int anyValidXCoordinate = 0;
        int nonUpperBoundaryYCoordinate = standardGridRowCount - 2;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findUpperRightNeighborCoordinate(anyValidXCoordinate, nonUpperBoundaryYCoordinate);
        assertTrue(potentialGridCell.isPresent());

        GridCoordinate neighborCoordinate = potentialGridCell.get();
        assertTrue(anyValidXCoordinate + 1 == neighborCoordinate.getxCoordinate());
        assertTrue(nonUpperBoundaryYCoordinate + 1 == neighborCoordinate.getyCoordinate());

        int nonRightBoundaryXCoordinate = standardGridColumnCount - 2;
        int anyValidYCoordinate = 0;

        potentialGridCell = standardGrid.findUpperRightNeighborCoordinate(nonRightBoundaryXCoordinate, anyValidYCoordinate);
        assertTrue(potentialGridCell.isPresent());

        neighborCoordinate = potentialGridCell.get();
        assertTrue(nonRightBoundaryXCoordinate + 1 == neighborCoordinate.getxCoordinate());
        assertTrue(anyValidYCoordinate + 1 == neighborCoordinate.getyCoordinate());
    }

    @Test
    public void findRightNeighborCoordinateExcludesInvalidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int rightBoundaryXCoordinate = standardGridColumnCount - 1;
        int anyValidYCoordinate = 0;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findRightNeighborCoordinate(rightBoundaryXCoordinate, anyValidYCoordinate);
        assertFalse(potentialGridCell.isPresent());
    }

    @Test
    public void findRightNeighborCoordinateIncludesValidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int nonRightBoundaryXCoordinate = standardGridColumnCount - 2;
        int anyValidYCoordinate = 0;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findRightNeighborCoordinate(nonRightBoundaryXCoordinate, anyValidYCoordinate);
        assertTrue(potentialGridCell.isPresent());

        GridCoordinate neighborCoordinate = potentialGridCell.get();
        assertTrue(potentialGridCell.isPresent());
        assertTrue(nonRightBoundaryXCoordinate + 1 == neighborCoordinate.getxCoordinate());
        assertTrue(anyValidYCoordinate == neighborCoordinate.getyCoordinate());
    }

    @Test
    public void findLowerRightNeighborCoordinateExcludesInvalidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int anyValidXCoordinate = 0;
        int lowerBoundaryYCoordinate = 0;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findLowerRightNeighborCoordinate(anyValidXCoordinate, lowerBoundaryYCoordinate);
        assertFalse(potentialGridCell.isPresent());

        int rightBoundaryXCoordinate = standardGridColumnCount - 1;
        int anyValidYCoordinate = 1;

        potentialGridCell = standardGrid.findLowerRightNeighborCoordinate(rightBoundaryXCoordinate, anyValidYCoordinate);
        assertFalse(potentialGridCell.isPresent());
    }

    @Test
    public void findLowerRightNeighborCoordinateIncludesValidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int nonRightBoundaryXCoordinate = standardGridColumnCount - 2;
        int nonLowerBoundaryYCoordinate = 1;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findLowerRightNeighborCoordinate(nonRightBoundaryXCoordinate, nonLowerBoundaryYCoordinate);
        assertTrue(potentialGridCell.isPresent());

        GridCoordinate neighborCoordinate = potentialGridCell.get();
        assertTrue(nonRightBoundaryXCoordinate + 1 == neighborCoordinate.getxCoordinate());
        assertTrue(nonLowerBoundaryYCoordinate - 1 == neighborCoordinate.getyCoordinate());
    }

    @Test
    public void findLowerNeighborCoordinateExcludesInvalidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int anyValidXCoordinate = 0;
        int lowerBoundaryYCoordinate = 0;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findLowerNeighborCoordinate(anyValidXCoordinate, lowerBoundaryYCoordinate);
        assertFalse(potentialGridCell.isPresent());
    }

    @Test
    public void findLowerNeighborCoordinateIncludesValidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int anyValidXCoordinate = 0;
        int nonLowerBoundaryYCoordinate = 1;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findLowerNeighborCoordinate(anyValidXCoordinate, nonLowerBoundaryYCoordinate);
        GridCoordinate neighborCoordinate = potentialGridCell.get();
        assertTrue(potentialGridCell.isPresent());
        assertTrue(anyValidXCoordinate == neighborCoordinate.getxCoordinate());
        assertTrue(nonLowerBoundaryYCoordinate - 1 == neighborCoordinate.getyCoordinate());
    }

    @Test
    public void findLowerLeftNeighborCoordinateExcludesInvalidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int anyValidXCoordinate = 1;
        int lowerBoundaryYCoordinate = 0;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findLowerLeftNeighborCoordinate(anyValidXCoordinate, lowerBoundaryYCoordinate);
        assertFalse(potentialGridCell.isPresent());

        int leftBoundaryXCoordinate = 0;
        int anyValidYCoordinate = 1;

        potentialGridCell = standardGrid.findLowerLeftNeighborCoordinate(leftBoundaryXCoordinate, anyValidYCoordinate);
        assertFalse(potentialGridCell.isPresent());
    }

    @Test
    public void findLowerLeftNeighborCoordinateIncludesValidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int nonLeftBoundaryXCoordinate = 1;
        int nonLowerBoundaryYCoordinate = 1;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findLowerLeftNeighborCoordinate(nonLeftBoundaryXCoordinate, nonLowerBoundaryYCoordinate);
        assertTrue(potentialGridCell.isPresent());

        GridCoordinate neighborCoordinate = potentialGridCell.get();
        assertTrue(nonLeftBoundaryXCoordinate - 1 == neighborCoordinate.getxCoordinate());
        assertTrue(nonLowerBoundaryYCoordinate - 1 == neighborCoordinate.getyCoordinate());
    }

    @Test
    public void findLeftNeighborCoordinateExcludesInvalidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int leftBoundaryXCoordinate = 0;
        int anyValidYCoordinate = 0;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findLeftNeighborCoordinate(leftBoundaryXCoordinate, anyValidYCoordinate);
        assertFalse(potentialGridCell.isPresent());
    }

    @Test
    public void findLeftNeighborCoordinateIncludesValidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int nonLeftBoundaryXCoordinate = 1;
        int anyValidYCoordinate = 0;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findLeftNeighborCoordinate(nonLeftBoundaryXCoordinate, anyValidYCoordinate);
        assertTrue(potentialGridCell.isPresent());

        GridCoordinate neighborCoordinate = potentialGridCell.get();
        assertTrue(potentialGridCell.isPresent());
        assertTrue(nonLeftBoundaryXCoordinate - 1 == neighborCoordinate.getxCoordinate());
        assertTrue(anyValidYCoordinate == neighborCoordinate.getyCoordinate());
    }

    @Test
    public void findUpperLeftNeighborCoordinateExcludesInvalidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int anyValidXCoordinate = 0;
        int upperBoundaryYCoordinate = standardGridRowCount - 1;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findUpperLeftNeighborCoordinate(anyValidXCoordinate, upperBoundaryYCoordinate);
        assertFalse(potentialGridCell.isPresent());

        int leftBoundaryXCoordinate = 0;
        int anyValidYCoordinate = 0;

        potentialGridCell = standardGrid.findUpperLeftNeighborCoordinate(leftBoundaryXCoordinate, anyValidYCoordinate);
        assertFalse(potentialGridCell.isPresent());
    }

    @Test
    public void findUpperLeftNeighborCoordinateIncludesValidCoordinate() {
        Grid standardGrid = getEmptyStandardGridMock();

        int anyValidXCoordinate = 1;
        int nonUpperBoundaryYCoordinate = standardGridRowCount - 2;

        Optional<GridCoordinate> potentialGridCell = standardGrid.findUpperLeftNeighborCoordinate(anyValidXCoordinate, nonUpperBoundaryYCoordinate);
        assertTrue(potentialGridCell.isPresent());

        GridCoordinate neighborCoordinate = potentialGridCell.get();
        assertTrue(anyValidXCoordinate - 1 == neighborCoordinate.getxCoordinate());
        assertTrue(nonUpperBoundaryYCoordinate + 1 == neighborCoordinate.getyCoordinate());

        int nonLeftBoundaryXCoordinate = 1;
        int anyValidYCoordinate = 0;

        potentialGridCell = standardGrid.findUpperLeftNeighborCoordinate(nonLeftBoundaryXCoordinate, anyValidYCoordinate);
        assertTrue(potentialGridCell.isPresent());

        neighborCoordinate = potentialGridCell.get();
        assertTrue(nonLeftBoundaryXCoordinate - 1 == neighborCoordinate.getxCoordinate());
        assertTrue(anyValidYCoordinate + 1 == neighborCoordinate.getyCoordinate());
    }

    @Test
    public void isValidCoordinateConsidersOutOfBoundsCoordinatesInvalid() {
        Grid standardGrid = getEmptyStandardGridMock();
        int leftBoundaryOutOfBoundsXCoordinate = -1;
        int rightBoundaryOutOfBoundsXCoordinate = standardGridColumnCount + 1;
        int upperBoundaryOutOfBoundsYCoordinate = standardGridRowCount + 1;
        int lowerBoundaryOutOfBoundsYCoorindate = -1;
        int anyValidXCoordinate = 1;
        int anyValidYCoordinate = 1;

        assertFalse(standardGrid.isValidCoordinate(leftBoundaryOutOfBoundsXCoordinate, anyValidYCoordinate));
        assertFalse(standardGrid.isValidCoordinate(rightBoundaryOutOfBoundsXCoordinate, anyValidYCoordinate));
        assertFalse(standardGrid.isValidCoordinate(anyValidXCoordinate, upperBoundaryOutOfBoundsYCoordinate));
        assertFalse(standardGrid.isValidCoordinate(anyValidXCoordinate, lowerBoundaryOutOfBoundsYCoorindate));
    }

    @Test
    public void isValidCoordinateConsidersInBoundsCoordinatesValid() {
        Grid standardGrid = getEmptyStandardGridMock();
        int anyValidXCoordinate = 0;
        int anyValidYCoordinate = 0;
        assertTrue(standardGrid.isValidCoordinate(anyValidXCoordinate, anyValidYCoordinate));
    }

    @Test
    public void findNeighborGridCoordinatesRemovesOptionalCoordinatesThatAreNotPresent() {
        Grid standardGrid = getEmptyStandardGridMock();
        List<GridCoordinate> neighborGridCoordinates = standardGrid.findNeighborGridCoordinates(new GridCoordinate(0, 0));
        assertEquals(3, neighborGridCoordinates.size());
    }

    @Test
    public void determineNextGenerationStateDoesNotKillWhenAliveNeighborsIsTwoOrThree() {
        List<GridCell> threeAliveNeighbors = Arrays.asList(
                new GridCell(new GridCoordinate(0,1), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,2), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,3), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE)
        );

        List<GridCell> twoAliveNeighbors = Arrays.asList(
                new GridCell(new GridCoordinate(0,1), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,2), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE)
        );

        Mockito.when(partialMockGrid.findLiveGridCellNeighbors(Mockito.any())).thenReturn(threeAliveNeighbors).thenReturn(twoAliveNeighbors);
        Mockito.when(partialMockGrid.determineNextGenerationState(Mockito.any())).thenCallRealMethod();
        GridCell anyGridCell = new GridCell(new GridCoordinate(0,0), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE);

        GridCellState nextGenCellStateForThreeAliveNeighbors = partialMockGrid.determineNextGenerationState(anyGridCell);
        assertEquals(GridCellState.ALIVE, nextGenCellStateForThreeAliveNeighbors);

        GridCellState nextGenCellStateForTwoAliveNeighbors = partialMockGrid.determineNextGenerationState(anyGridCell);
        assertEquals(GridCellState.ALIVE, nextGenCellStateForTwoAliveNeighbors);
    }

    @Test
    public void determineNextGenerationStateKillsWhenUnderpopulated() {
        List<GridCell> oneAliveNeighbor = Arrays.asList(
                new GridCell(new GridCoordinate(0,1), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE)
        );

        Mockito.when(partialMockGrid.findLiveGridCellNeighbors(Mockito.any())).thenReturn(oneAliveNeighbor);
        Mockito.when(partialMockGrid.determineNextGenerationState(Mockito.any())).thenCallRealMethod();
        GridCell anyGridCell = new GridCell(new GridCoordinate(0,0), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE);

        GridCellState nextGenCellStateForThreeAliveNeighbors = partialMockGrid.determineNextGenerationState(anyGridCell);
        assertEquals(GridCellState.DEAD, nextGenCellStateForThreeAliveNeighbors);
    }

    @Test
    public void determineNextGenerationStateKillsWhenOverpopulated() {
        List<GridCell> fourAliveNeighbors = Arrays.asList(
                new GridCell(new GridCoordinate(0,1), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,2), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,3), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,4), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE)
        );

        Mockito.when(partialMockGrid.findLiveGridCellNeighbors(Mockito.any())).thenReturn(fourAliveNeighbors);
        Mockito.when(partialMockGrid.determineNextGenerationState(Mockito.any())).thenCallRealMethod();
        GridCell anyGridCell = new GridCell(new GridCoordinate(0,0), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE);

        GridCellState nextGenCellStateForThreeAliveNeighbors = partialMockGrid.determineNextGenerationState(anyGridCell);
        assertEquals(GridCellState.DEAD, nextGenCellStateForThreeAliveNeighbors);
    }

    @Test
    public void determineNextGenerationStateCreatesLifeForThreeAliveNeighbors() {
        List<GridCell> threeAliveNeighbors = Arrays.asList(
                new GridCell(new GridCoordinate(0,1), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,2), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,3), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE)
        );

        Mockito.when(partialMockGrid.findLiveGridCellNeighbors(Mockito.any())).thenReturn(threeAliveNeighbors);
        Mockito.when(partialMockGrid.determineNextGenerationState(Mockito.any())).thenCallRealMethod();
        GridCell anyGridCell = new GridCell(new GridCoordinate(0,0), standardGridRowCount, standardGridColumnCount, GridCellState.DEAD);

        GridCellState nextGenCellStateForThreeAliveNeighbors = partialMockGrid.determineNextGenerationState(anyGridCell);
        assertEquals(GridCellState.ALIVE, nextGenCellStateForThreeAliveNeighbors);
    }

    @Test
    public void determineNextGenerationStateDoesNotCreatesLifeWhenThereAreNotThreeAliveNeighbors() {
        List<GridCell> twoAliveNeighbors = Arrays.asList(
                new GridCell(new GridCoordinate(0,1), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,2), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE)
        );

        List<GridCell> fourAliveNeighbors = Arrays.asList(
                new GridCell(new GridCoordinate(0,1), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,2), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,3), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE),
                new GridCell(new GridCoordinate(0,4), standardGridRowCount, standardGridColumnCount, GridCellState.ALIVE)
        );

        Mockito.when(partialMockGrid.findLiveGridCellNeighbors(Mockito.any())).thenReturn(twoAliveNeighbors).thenReturn(fourAliveNeighbors);
        Mockito.when(partialMockGrid.determineNextGenerationState(Mockito.any())).thenCallRealMethod();
        GridCell anyGridCell = new GridCell(new GridCoordinate(0,0), standardGridRowCount, standardGridColumnCount, GridCellState.DEAD);

        GridCellState nextGenCellStateForTwoAliveNeighbors = partialMockGrid.determineNextGenerationState(anyGridCell);
        assertEquals(GridCellState.DEAD, nextGenCellStateForTwoAliveNeighbors);

        GridCellState nextGenCellStateForFourliveNeighbors = partialMockGrid.determineNextGenerationState(anyGridCell);
        assertEquals(GridCellState.DEAD, nextGenCellStateForFourliveNeighbors);
    }

    @Test
    public void buildNextGenerationGrid() {
        Grid standardGrid = getEmptyStandardGridMock();
        Grid nextGenerationGrid = standardGrid.buildNextGenerationGrid();

        assertEquals(standardGrid.retrieveGridCells().size(), nextGenerationGrid.retrieveGridCells().size());
        nextGenerationGrid.retrieveGridCells().stream().forEach(gc -> assertTrue(standardGrid.retrieveGridCells().contains(gc) && gc.getGridCellState() != null));
        assertFalse(standardGrid == nextGenerationGrid);
    }

}
