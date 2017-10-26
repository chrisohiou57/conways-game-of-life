package com.kata.cgl.grid;

public class GridCoordinate {

    private Integer xCoordinate;
    private Integer yCoordinate;

    public GridCoordinate(){}
    
    public GridCoordinate(Integer xCoordinate, Integer yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GridCoordinate that = (GridCoordinate) o;

        if (!xCoordinate.equals(that.xCoordinate)) return false;
        return yCoordinate.equals(that.yCoordinate);
    }

    @Override
    public int hashCode() {
        int result = xCoordinate.hashCode();
        result = 31 * result + yCoordinate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return xCoordinate + ":" + yCoordinate;
    }

    public Integer getxCoordinate() {
        return xCoordinate;
    }

    public Integer getyCoordinate() {
        return yCoordinate;
    }

	public void setxCoordinate(Integer xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public void setyCoordinate(Integer yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

}
