package com.kata.cgl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kata.cgl.grid.Grid;
import com.kata.cgl.ui.GridForm;

@Controller
public class ConwaysGameOfLifeController {

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String loadEmptyGrid(Model model) {
        model.addAttribute("emptyGrid", new Grid(6,8));
        return "conwaysGameOfLife";
    }
    
    @RequestMapping(value = "/grid", method=RequestMethod.GET)
    @ResponseBody
    public Grid emptyGrid(@RequestParam int columnCount, @RequestParam int rowCount) {
    	return new Grid(rowCount, columnCount);
    }
    
    @RequestMapping(value = "/grid", method=RequestMethod.POST, produces="application/json")
    @ResponseBody
    public Grid transitionToNextGeneration(@RequestBody GridForm gridForm) {
    	Grid initialStateGrid = new Grid(gridForm.getRowCount(), gridForm.getColumnCount(), gridForm.getGridCellForms());
    	Grid nextGenerationGrid = initialStateGrid.buildNextGenerationGrid();
    	return nextGenerationGrid;
    }

}
