package org.msh.quantb.view.tableExt.columnSpan;

import java.awt.Point;

/**
 * Describe spanning data for cells
 *
 */
public interface CMap {
	/**
	 * @param row
	 *            logical cell row
	 * @param column
	 *            logical cell column
	 * @return number of columns spanned a cell
	 */
	int columnSpan(int row, int column);
	/**
	 * @param row logical cell row 
	 * @param column logical cell column
	 * @return number of rows spanned a cell
	 */
	int rowSpan(int row, int column);
	/**
	 * @param row
	 *            logical cell row
	 * @param column
	 *            logical cell column
	 * @return the index of a visible cell covering a logical cell
	 */
	Point visibleCell(int row, int column);
	
}
