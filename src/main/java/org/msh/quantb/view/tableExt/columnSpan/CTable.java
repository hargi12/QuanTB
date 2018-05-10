package org.msh.quantb.view.tableExt.columnSpan;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * Perform simple JTable with column span ability
 * 
 */
public class CTable extends JTable {
	private static final long serialVersionUID = -3684840444763150276L;
	public CMap map;
	int colOffset = 0;

	/**
	 * Create table with spans
	 * 
	 * @param cmp
	 *            spans map
	 * @param tbl
	 *            data model
	 */
	public CTable(CMap cmp, TableModel tbl) {
		super(tbl);
		map = cmp;
		setUI(new CTUI());
	}

	public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
		// required because getCellRect is used in JTable constructor
		if (map == null)
			return super.getCellRect(row, column, includeSpacing);
		// add widths of all spanned logical cells
		Point sk = map.visibleCell(row, column);
		//Rectangle r1 = super.getCellRect(row, sk.y, includeSpacing); 27.03.13
		Rectangle r1 = super.getCellRect(sk.x, sk.y, includeSpacing);
		//if (map.columnSpan(row, sk.y) != 1)27.03.13
		if (map.columnSpan(row, sk.y) != 1)
			for (int i = 1; i < map.columnSpan(row, sk.y); i++) {
				r1.width += getColumnModel().getColumn(sk.y + i).getWidth();
			}
		if (map.rowSpan(sk.x, column)!=1){
			for (int i = 1; i < map.rowSpan(sk.x, column); i++) {
				r1.height += getRowHeight(sk.x + i);
			}
		}
		return r1;
	}

	public int columnAtPoint(Point p) {
		int x = super.columnAtPoint(p);
		// -1 is returned by columnAtPoint if the point is not in the table
		if (x < 0)
			return x;
		int y = super.rowAtPoint(p);
		return map!=null?map.visibleCell(y, x).y:1;
	}
	
	@Override
	public int rowAtPoint(Point p) {
		int y = super.rowAtPoint(p);
		// -1 is returned by columnAtPoint if the point is not in the table
		if (y < 0)
			return y;
		int x = super.columnAtPoint(p);
		return map!=null?map.visibleCell(y, x).x:1;
	}

	/**
	 * @return the map
	 */
	public CMap getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(CMap map) {
		this.map = map;
	}

	public void setColumnOffset(int fixedColumns) {
		colOffset = fixedColumns;
		
	}
	
	
}
