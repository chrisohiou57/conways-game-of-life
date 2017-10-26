var selectors = {
	initialStateGridTable : '#initialStateGridTable',
	initialStateGridTableBody : '#initialStateGridTableBody',
	startGameButton : '#startGameBtn',
	columnCountInput : '#columnCount',
	rowCountInput : '#rowCount',
	allGridCells : '[data-x-coordinate]',
	manualGenerationGridButton : '#manualGenerationGridButton',
	autoGenerationGridButton : '#autoGenerationGridButton',
	generationCount : '#generationCount',
	selectedGenerationModeRadio : 'input[name=generationMode]:checked',
	gridControlDisplay : '#gridControlDisplay',
	errorContainer : '#errorContainer'
}

var DEAD_CELL = "DEAD";
var LIVE_CELL = "ALIVE";
var runContinuously = false;

$(document).ready(function(){
	setupStartGameClickHandler();
	registerManualGenerationGridButtonClickHandler();
	registerAutoGenerationGridButtonClickHandler();
});

function registerManualGenerationGridButtonClickHandler() {
	$(selectors.manualGenerationGridButton).on('click', function(){
		transitionInitialStateGridToNextGeneration();
	});
}

function registerAutoGenerationGridButtonClickHandler() {
	$(selectors.autoGenerationGridButton).on('click', function(){
		if (runContinuously === true) {
			$(selectors.autoGenerationGridButton).val('Load Generations Continuously');
			runContinuously = false;
		} else {
			$(selectors.autoGenerationGridButton).val('Stop Running');
			runContinuously = true;
			runConstantly();
		}
	});
}

function runConstantly() {
	if (runContinuously === true) {
		setTimeout(function(){
			transitionInitialStateGridToNextGeneration();
			runConstantly();
		}, 500);
	}
}

function setupStartGameClickHandler() {
	$(selectors.startGameButton).on('click', function(){
		runContinuously = false;
		$(selectors.errorContainer).hide();
		
		var errors = validateGameSettings();
		if (errors && errors.length > 0) {
			$(selectors.errorContainer).html(errors.join('<br/>'));
			$(selectors.errorContainer).show();
		} else {
			var columnCount = $(selectors.columnCountInput).val();
			var rowCount = $(selectors.rowCountInput).val();
			
			loadInitialStateGrid(columnCount, rowCount);
			resetGenerationCount();
			$(selectors.autoGenerationGridButton).val('Load Generations Continuously');
		}
	});
}

function validateGameSettings() {
	var columnCount = $(selectors.columnCountInput).val();
	var rowCount = $(selectors.rowCountInput).val();
	var generationMode = $(selectors.selectedGenerationModeRadio).val();
	
	var errors = [];
	if (!columnCount || columnCount <= 0) {
		errors.push('You must select a column count greater than 0');
	}
	if (!rowCount || rowCount <= 0) {
		errors.push('You must select a row count greater than 0');
	}
	if (!generationMode) {
		errors.push('You must select a generation strategy');
	}
	
	return errors;
}

function registerGridCellClickHandlers() {
	$(selectors.allGridCells).on('click', function(event){
		var gridCell = event.target;
		var gridCellState = $(gridCell).data('grid-cell-state');
		
		if (gridCellState === DEAD_CELL) {
			$(gridCell).addClass('alive');
			$(gridCell).data('grid-cell-state', LIVE_CELL);
		} else {
			$(gridCell).removeClass('alive');
			$(gridCell).data('grid-cell-state', DEAD_CELL);
		}
	});
}

function unregisterGridCellClickHandlers() {
	$(selectors.allGridCells).off('click');
}

function loadInitialStateGrid(columnCount, rowCount) {
	var url = '/grid?columnCount=' + columnCount + '&rowCount=' + rowCount;
	
	$.get(url, function(data) {
		buildGridFromGridRows(data.gridRows, selectors.initialStateGridTableBody);
		registerGridCellClickHandlers();
		
		var generationMode = $(selectors.selectedGenerationModeRadio).val();
		if (generationMode === 'manual') {
			$(selectors.autoGenerationGridButton).hide();
			$(selectors.manualGenerationGridButton).show();
		} else {
			$(selectors.manualGenerationGridButton).hide();
			$(selectors.autoGenerationGridButton).show();
		}
		
		$(selectors.gridControlDisplay).show();
		$(selectors.generationCount).data('generation-count', 1)
	});
}

function buildGridFromGridRows(gridRows, tableBodySelector) {
	$(tableBodySelector).empty();
	
	$.each(gridRows, function(index, gridRow){
		var gridCellMarkup = '<tr>';
		
		$.each(gridRow.rowCells, function(index, rowCell){
			gridCellMarkup += '<td ';
			
			if (rowCell.gridCellState === LIVE_CELL) {
				gridCellMarkup += 'class="alive" ';
			}
			
			gridCellMarkup += '<td data-grid-cell-state="' + rowCell.gridCellState + '" data-x-coordinate="' + rowCell.xCoordinate + '" data-y-coordinate="' + rowCell.yCoordinate + '">&nbsp;</td>';
		});
		
		gridCellMarkup += '</tr>';
		$(gridCellMarkup).prependTo(tableBodySelector);
	});
}

function transitionInitialStateGridToNextGeneration() {
	unregisterGridCellClickHandlers();
	var initialStateGridCellData = retrieveInitialStateGridCellData();
	
	$.ajax({
		url:'/grid',
		type: 'POST',
		data: JSON.stringify(initialStateGridCellData),
		contentType: 'application/json; charset=utf-8',
		dataType: "json",
		success: function(data){
			buildGridFromGridRows(data.gridRows, selectors.initialStateGridTableBody);
			incrementGenerationCount();
		}
	});
}

function resetGenerationCount() {
	var generationCount = '1';
	$(selectors.generationCount).html(generationCount);
	$(selectors.generationCount).data('generation-count', generationCount);
}

function incrementGenerationCount() {
	var generationCount = parseInt($(selectors.generationCount).data('generation-count'));
	generationCount++;
	$(selectors.generationCount).html(generationCount);
	$(selectors.generationCount).data('generation-count', generationCount);
}

function retrieveInitialStateGridCellData() {
	var columnCount = $(selectors.columnCountInput).val();
	var rowCount = $(selectors.rowCountInput).val();
	var gridCells = $(selectors.initialStateGridTable).find('td');
	var initialStateGridData = [];
	
	$.each(gridCells, function(index, gridCell){
		var xCoordinate = $(gridCell).data('x-coordinate');
		var yCoordinate = $(gridCell).data('y-coordinate');
		
		initialStateGridData.push({
			gridCoordinate : {
				xCoordinate : xCoordinate,
				yCoordinate : yCoordinate
			},
			gridCellState : $(gridCell).data('grid-cell-state')
		});
	});
	
	return {
		rowCount : rowCount,
		columnCount : columnCount,
		gridCellForms : initialStateGridData
	}
}