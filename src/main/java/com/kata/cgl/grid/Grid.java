package com.kata.cgl.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kata.cgl.exception.GridRowIndexOutOfBoundsException;

public class Grid {

    private int columnCount;
    private int rowCount;
    private List<GridCell> gridCells = new ArrayList<>();

    private static final int UNDER_POPULATION_THRESHOLD = 2;
    private static final int OVER_POPULATION_THRESHOLD = 3;
    private static final int GROWTH_FACTOR = 3;

    public Grid(int rowCount, int columnCount) {
        this.columnCount = columnCount;
        this.rowCount = rowCount;
        initializeEmptyGridCells(rowCount, columnCount);
    }

    public Grid(int rowCount, int columnCount, List<GridCell> gridCells) {
        this.columnCount = columnCount;
        this.rowCount = rowCount;
        this.gridCells = Collections.unmodifiableList(gridCells);
    }

    private void initializeEmptyGridCells(int rowCount, int columnCount) {
        List<GridCell> generatedGridCells = new ArrayList<>();

        for (int xCoordinate=0; xCoordinate < columnCount; xCoordinate++) {
            for (int yCoordinate=0; yCoordinate < rowCount; yCoordinate++) {
                generatedGridCells.add(new GridCell(new GridCoordinate(xCoordinate,yCoordinate), rowCount, columnCount));
            }
        }

        this.gridCells = Collections.unmodifiableList(generatedGridCells);
    }

    public Grid buildNextGenerationGrid() {
        List<GridCell> nextGenerationGridCells = new ArrayList<>();

        gridCells.stream().forEach(currentGenGridCell -> {
            GridCellState nextGenCellState = determineNextGenerationState(currentGenGridCell);
            GridCell nextGenGridCell = new GridCell(currentGenGridCell.getGridCoordinate(), rowCount, columnCount, nextGenCellState);
            nextGenerationGridCells.add(nextGenGridCell);
        });

        return new Grid(this.rowCount, this.columnCount, nextGenerationGridCells);
    }

    public GridCellState determineNextGenerationState(GridCell gridCell) {
        List<GridCell> liveGridCells = findLiveGridCellNeighbors(gridCell);
        long aliveNeighborCount = liveGridCells.size();

        switch (gridCell.getGridCellState()) {
            case ALIVE:
                if (aliveNeighborCount < UNDER_POPULATION_THRESHOLD || aliveNeighborCount > OVER_POPULATION_THRESHOLD) {
                    return GridCellState.DEAD;
                }

                return GridCellState.ALIVE;
            default: // DEAD
                if (aliveNeighborCount == GROWTH_FACTOR) {
                    return GridCellState.ALIVE;
                }

                return GridCellState.DEAD;
        }
    }

    public List<GridCell> findLiveGridCellNeighbors(GridCell gridCell) {
        List<GridCell> gridCellNeighbors = findGridCellNeighbors(gridCell);
        return gridCellNeighbors.stream().filter(gcn -> GridCellState.ALIVE == gcn.getGridCellState()).collect(Collectors.toList());
    }

    public List<GridRow> getGridRows() throws GridRowIndexOutOfBoundsException {
        List<GridRow> gridRows = new ArrayList<>();

        for (int i=0; i < rowCount; i++) {
            gridRows.add(buildGridRow(i));
        }

        return gridRows;
    }

    public GridRow buildGridRow(int rowIndex) throws GridRowIndexOutOfBoundsException {
        if (rowIndex < 0 || rowIndex > rowCount - 1) {
            throw new GridRowIndexOutOfBoundsException();
        }

        List<GridCell> gridRowCells = gridCells.stream().filter(gc -> gc.getyCoordinate().equals(rowIndex)).collect(Collectors.toList());
        gridRowCells.sort((grc1, grc2) -> grc1.getxCoordinate().compareTo(grc2.getxCoordinate()));
        return new GridRow(gridRowCells);
    }

    public int getCellCount() {
        return gridCells.size();
    }

    public List<GridCoordinate> retrieveGridCoordinates() {
        return retrieveGridCells().stream().map(GridCell::getGridCoordinate).collect(Collectors.toList());
    }

    public List<GridCell> findGridCellNeighbors(GridCell gridCell) {
        List<GridCoordinate> neighborGridCoordinates = findNeighborGridCoordinates(gridCell.getGridCoordinate());
        List<GridCell> allGridCells = retrieveGridCells();

        return allGridCells
                .parallelStream()
                .filter(gc -> neighborGridCoordinates.contains(gc.getGridCoordinate()))
                .collect(Collectors.toList());
    }

    public List<GridCoordinate> findNeighborGridCoordinates(GridCoordinate gridCoordinate) {
        int xCoordinate = gridCoordinate.getxCoordinate();
        int yCoordinate = gridCoordinate.getyCoordinate();

        List<Optional<GridCoordinate>> allPotentialNeighborCoordinates = Arrays.asList(
                findUpperNeighborCoordinate(xCoordinate, yCoordinate),
                findUpperRightNeighborCoordinate(xCoordinate, yCoordinate),
                findRightNeighborCoordinate(xCoordinate, yCoordinate),
                findLowerRightNeighborCoordinate(xCoordinate, yCoordinate),
                findLowerNeighborCoordinate(xCoordinate, yCoordinate),
                findLowerLeftNeighborCoordinate(xCoordinate, yCoordinate),
                findLeftNeighborCoordinate(xCoordinate, yCoordinate),
                findUpperLeftNeighborCoordinate(xCoordinate, yCoordinate)
        );

        return allPotentialNeighborCoordinates
                .stream()
                .filter(neighborCoordinate -> neighborCoordinate.isPresent())
                .map(validCoordinates -> validCoordinates.get())
                .collect(Collectors.toList());
    }

    public Optional<GridCoordinate> findUpperNeighborCoordinate(int xCoordinate, int yCoordinate) {
        int neighborXCoordinate = xCoordinate;
        int neighborYCoordinate = yCoordinate + 1;

        if (isValidCoordinate(neighborXCoordinate, neighborYCoordinate)) {
            return Optional.of(new GridCoordinate(neighborXCoordinate, neighborYCoordinate));
        } else {
            return Optional.empty();
        }
    }

    public Optional<GridCoordinate> findUpperRightNeighborCoordinate(int xCoordinate, int yCoordinate) {
        int neighborXCoordinate = xCoordinate + 1;
        int neighborYCoordinate = yCoordinate + 1;

        if (isValidCoordinate(neighborXCoordinate, neighborYCoordinate)) {
            return Optional.of(new GridCoordinate(neighborXCoordinate, neighborYCoordinate));
        } else {
            return Optional.empty();
        }
    }

    public Optional<GridCoordinate> findRightNeighborCoordinate(int xCoordinate, int yCoordinate) {
        int neighborXCoordinate = xCoordinate + 1;
        int neighborYCoordinate = yCoordinate;

        if (isValidCoordinate(neighborXCoordinate, neighborYCoordinate)) {
            return Optional.of(new GridCoordinate(neighborXCoordinate, neighborYCoordinate));
        } else {
            return Optional.empty();
        }
    }

    public Optional<GridCoordinate> findLowerRightNeighborCoordinate(int xCoordinate, int yCoordinate) {
        int neighborXCoordinate = xCoordinate + 1;
        int neighborYCoordinate = yCoordinate - 1;

        if (isValidCoordinate(neighborXCoordinate, neighborYCoordinate)) {
            return Optional.of(new GridCoordinate(neighborXCoordinate, neighborYCoordinate));
        } else {
            return Optional.empty();
        }
    }

    public Optional<GridCoordinate> findLowerNeighborCoordinate(int xCoordinate, int yCoordinate) {
        int neighborXCoordinate = xCoordinate;
        int neighborYCoordinate = yCoordinate - 1;

        if (isValidCoordinate(neighborXCoordinate, neighborYCoordinate)) {
            return Optional.of(new GridCoordinate(neighborXCoordinate, neighborYCoordinate));
        } else {
            return Optional.empty();
        }
    }

    public Optional<GridCoordinate> findLowerLeftNeighborCoordinate(int xCoordinate, int yCoordinate) {
        int neighborXCoordinate = xCoordinate - 1;
        int neighborYCoordinate = yCoordinate - 1;

        if (isValidCoordinate(neighborXCoordinate, neighborYCoordinate)) {
            return Optional.of(new GridCoordinate(neighborXCoordinate, neighborYCoordinate));
        } else {
            return Optional.empty();
        }
    }

    public Optional<GridCoordinate> findLeftNeighborCoordinate(int xCoordinate, int yCoordinate) {
        int neighborXCoordinate = xCoordinate - 1;
        int neighborYCoordinate = yCoordinate;

        if (isValidCoordinate(neighborXCoordinate, neighborYCoordinate)) {
            return Optional.of(new GridCoordinate(neighborXCoordinate, neighborYCoordinate));
        } else {
            return Optional.empty();
        }
    }

    public Optional<GridCoordinate> findUpperLeftNeighborCoordinate(int xCoordinate, int yCoordinate) {
        int neighborXCoordinate = xCoordinate - 1;
        int neighborYCoordinate = yCoordinate + 1;

        if (isValidCoordinate(neighborXCoordinate, neighborYCoordinate)) {
            return Optional.of(new GridCoordinate(neighborXCoordinate, neighborYCoordinate));
        } else {
            return Optional.empty();
        }
    }

    public boolean isValidCoordinate(int xCoordinate, int yCoordinate) {
        boolean isValidXCoordinate = xCoordinate >= 0 && xCoordinate < columnCount;
        boolean isValidYCoordinate = yCoordinate >=0 && yCoordinate < rowCount;
        return isValidXCoordinate && isValidYCoordinate;
    }

    public List<GridCell> retrieveGridCells() {
        return gridCells;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getRowCount() {
        return rowCount;
    }

}
