package com.kata.cgl.grid;

public class GridCell {

    private GridCoordinate gridCoordinate;
    private GridCellState gridCellState;
    private int gridRowCount;
    private int gridColumnCount;

    private int leftBoundaryXCoordinate;
    private int rightBoundaryXCoordinate;
    private int lowerBoundaryYCoordinate;
    private int upperBoundaryYCoordinate;

    public GridCell(){}

    public GridCell(GridCoordinate gridCoordinate, int gridRowCount, int gridColumnCount) {
        this.gridCoordinate = gridCoordinate;
        this.gridCellState = GridCellState.DEAD;
        this.gridRowCount = gridRowCount;
        this.gridColumnCount = gridColumnCount;
        this.leftBoundaryXCoordinate = 0;
        this.rightBoundaryXCoordinate = gridColumnCount - 1;
        this.lowerBoundaryYCoordinate = 0;
        this.upperBoundaryYCoordinate = gridRowCount - 1;
    }

    public GridCell(GridCoordinate gridCoordinate, int gridRowCount, int gridColumnCount, GridCellState gridCellState) {
        this.gridCoordinate = gridCoordinate;
        this.gridCellState = gridCellState;
        this.gridRowCount = gridRowCount;
        this.gridColumnCount = gridColumnCount;
    }

    public boolean isBoundaryCell() {
        int xCoordinate = getxCoordinate();
        int yCoordinate = getyCoordinate();

        boolean isLeftBoundaryCell = xCoordinate == leftBoundaryXCoordinate;
        boolean isRightBoundaryCell = xCoordinate == rightBoundaryXCoordinate;
        boolean isLowerBoundaryCell = yCoordinate == lowerBoundaryYCoordinate;
        boolean isUpperBoundaryCell = yCoordinate == upperBoundaryYCoordinate;

        return isLeftBoundaryCell || isRightBoundaryCell || isLowerBoundaryCell || isUpperBoundaryCell;
    }

    public boolean isCornerCell() {
        int xCoordinate = getxCoordinate();
        int yCoordinate = getyCoordinate();

        boolean hasYBoundaryCoordinate = yCoordinate == lowerBoundaryYCoordinate || yCoordinate == upperBoundaryYCoordinate;
        boolean hasXBoundaryCoordinate = xCoordinate == leftBoundaryXCoordinate || xCoordinate == rightBoundaryXCoordinate;

        return hasXBoundaryCoordinate && hasYBoundaryCoordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GridCell gridCell = (GridCell) o;

        return gridCoordinate.equals(gridCell.gridCoordinate);
    }

    @Override
    public int hashCode() {
        return gridCoordinate.hashCode();
    }

    @Override
    public String toString() {
        return getxCoordinate() + ":" + getyCoordinate();
    }

    public void kill() {
        this.gridCellState = GridCellState.DEAD;
    }

    public void breatheLife() {
        this.gridCellState = GridCellState.ALIVE;
    }

    public GridCoordinate getGridCoordinate() {
        return gridCoordinate;
    }

    public Integer getxCoordinate() {
        return gridCoordinate.getxCoordinate();
    }

    public Integer getyCoordinate() {
        return gridCoordinate.getyCoordinate();
    }

    public GridCellState getGridCellState() {
        return gridCellState;
    }

	public int getGridRowCount() {
		return gridRowCount;
	}
	
	public int getGridColumnCount()	{
		return gridColumnCount;
	}

	public void setGridRowCount(int gridRowCount) {
		this.gridRowCount = gridRowCount;
	}

	public void setGridCoordinate(GridCoordinate gridCoordinate) {
		this.gridCoordinate = gridCoordinate;
	}

	public void setGridCellState(GridCellState gridCellState) {
		this.gridCellState = gridCellState;
	}

	public void setGridColumnCount(int gridColumnCount) {
		this.gridColumnCount = gridColumnCount;
	}

}
