package com.kata.cgl.ui;

import java.util.List;

import com.kata.cgl.grid.GridCell;

public class GridForm {
	
	Integer columnCount;
	Integer rowCount;
	List<GridCell> gridCellForms;
	
	public Integer getColumnCount() {
		return columnCount;
	}
	public void setColumnCount(Integer columnCount) {
		this.columnCount = columnCount;
	}
	public Integer getRowCount() {
		return rowCount;
	}
	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}
	public List<GridCell> getGridCellForms() {
		return gridCellForms;
	}
	public void setGridCellForms(List<GridCell> gridCellForms) {
		this.gridCellForms = gridCellForms;
	}
	
}
