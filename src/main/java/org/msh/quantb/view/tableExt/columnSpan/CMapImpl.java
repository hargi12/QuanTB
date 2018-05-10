package org.msh.quantb.view.tableExt.columnSpan;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Collect spans and control them 
 *
 */
public class CMapImpl implements CMap {
	private Map<Point, Integer> columnSpans = new HashMap<Point, Integer>();
	private Map<Point, Integer> rowSpans = new HashMap<Point, Integer>();
	/**
	 * Add column span for current cell
	 * @param row 
	 * @param column
	 * @param numberOfCells number of cell to be merged (last cell will be exclusive)
	 */
	public void addColumnSpan(int row, int column, int numberOfCells){
		columnSpans.put(new Point(row, column), numberOfCells);
	}
	
	/**
	 * Add row span for current cell
	 * @param row
	 * @param column
	 * @param numberOfCells number of cell to be merged (last cell will be exclusive)
	 */
	public void addRowSpan(int row, int column, int numberOfCells){
		rowSpans.put(new Point(row, column), numberOfCells);
	}
	

	@Override
	public Point visibleCell(int row, int column) {
		Set<Point> columnKeys = columnSpans.keySet();
		Set<Point> rowKeys = rowSpans.keySet();
		int selectedRow = -1;
		int selectedColumn = -1;
		//Set<Point> 
		for (Point p: columnKeys){
			if (p.x==row && column>=p.y && (column<columnSpans.get(p)+p.y)){
				selectedColumn = p.y;
			}			
		}
		for (Point p: rowKeys){
			if (p.y==column && row>=p.x && (row<rowSpans.get(p)+p.x)){
				selectedRow = p.x;
			}
		}
		if (selectedColumn!=-1 || selectedRow!=-1){
			return new Point(selectedRow!=-1?selectedRow:row, selectedColumn!=-1?selectedColumn:column);
		}
		return new Point(row, column);
	}
	@Override
	public int columnSpan(int row, int column) {
		return columnSpans.containsKey(new Point(row, column))?columnSpans.get(new Point(row, column)):1;
	}
	@Override
	public int rowSpan(int row, int column) {
		return rowSpans.containsKey(new Point(row, column))?rowSpans.get(new Point(row, column)):1;
	}


}
