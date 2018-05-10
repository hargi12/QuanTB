package org.msh.quantb.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.msh.quantb.view.tableExt.columnSpan.CMap;
import org.msh.quantb.view.tableExt.columnSpan.CMapImpl;
import org.msh.quantb.view.tableExt.columnSpan.CTable;

/**
 * Same as Fixed Column table, but for CTable (tables with columns and rows span
 * ability) Prevent the specified number of columns from scrolling horizontally
 * in the scroll pane. The table must already exist in the scroll pane.
 * 
 * The functionality is accomplished by creating a second JTable (fixed) that
 * will share the TableModel and SelectionModel of the main table. This table
 * will be used as the row header of the scroll pane.
 * 
 * The fixed table created can be accessed by using the getFixedTable() method.
 * will be returned from this method. It will allow you to:
 * 
 * You can change the model of the main table and the change will be reflected
 * in the fixed model. However, you cannot change the structure of the model.
 * 
 * @author user
 */
public class FixedColumnCTable implements ChangeListener, PropertyChangeListener {

	private CTable main;
	private CTable fixed;
	private JScrollPane scrollPane;

	/*
	 * Specify the number of columns to be fixed and the scroll pane containing
	 * the table.
	 */
	public FixedColumnCTable(int fixedColumns, JScrollPane scrollPane) {
		this.scrollPane = scrollPane;

		main = ((CTable) scrollPane.getViewport().getView());
		main.setAutoCreateColumnsFromModel(false);
		main.addPropertyChangeListener(this);

		//  Use the existing table to create a new table sharing
		//  the DataModel and ListSelectionModel

		CMap cMapMain = rebuildMapMain(fixedColumns, main.getRowCount(), main.getColumnCount(), main.getMap());
		CMap cMapFixed = rebuildMapFixed(fixedColumns, main.getRowCount(), main.getMap());
		fixed = new CTable(cMapFixed, null);

		fixed.setAutoCreateColumnsFromModel(false);
		fixed.setModel(main.getModel());
		fixed.setSelectionModel(main.getSelectionModel());
		fixed.setFocusable(false);
		fixed.setEnabled(false);
		fixed.getTableHeader().setReorderingAllowed(false);
		fixed.getTableHeader().setResizingAllowed(false);
		//  Remove the fixed columns from the main table
		//  and add them to the fixed table
		for (int i = 0; i < fixedColumns; i++) {
			TableColumnModel columnModel = main.getColumnModel();
			TableColumn column = columnModel.getColumn(0);
			columnModel.removeColumn(column);
			fixed.getColumnModel().addColumn(column);
		}

		//adjust rows height in fixed table
		for (int i = 0; i < main.getRowCount(); i++) {
			fixed.setRowHeight(i, main.getRowHeight(i));
		}
		//add column offset to the main - fixedcolumns lost in main table
		main.setColumnOffset(fixedColumns);
		main.setMap(cMapMain);
		/*
		 * scrollPane.getViewport().remove(main);
		 * scrollPane.setViewportView(fixed);
		 */

		//  Add the fixed table to the scroll pane

		fixed.setPreferredScrollableViewportSize(fixed.getPreferredSize());
		scrollPane.setRowHeaderView(fixed);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixed.getTableHeader());

		// Synchronize scrolling of the row header with the main table

		scrollPane.getRowHeader().addChangeListener(this);
	}

	private CMap rebuildMapFixed(int fixedColumns, int rowCount, CMap map) {
		CMapImpl cMap = new CMapImpl();
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < fixedColumns; j++) {
				int rowSpan = map.rowSpan(i, j);
				int columnSpan = map.columnSpan(i, j);
				cMap.addRowSpan(i, j, rowSpan);
				cMap.addColumnSpan(i, j, columnSpan);
				i+= rowSpan-1;
				j+= columnSpan-1;
			}
		}
		return cMap;
	}

	private CMapImpl rebuildMapMain(int fixedColumns, int rowCount, int columnCount, CMap map) {
		CMapImpl cMap = new CMapImpl();
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				if (j <= fixedColumns) {
					cMap.addRowSpan(i, j, 1);//map.rowSpan(i, j));
					cMap.addColumnSpan(i, j, 1);//map.columnSpan(i, j));
				}
			}
		}
		return cMap;
	}

	/*
	 * Return the table being used in the row header
	 */
	public JTable getFixedTable() {
		return fixed;
	}

	//
	//  Implement the ChangeListener
	//
	public void stateChanged(ChangeEvent e) {
		//  Sync the scroll pane scrollbar with the row header

		JViewport viewport = (JViewport) e.getSource();
		scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
	}

	//
	//  Implement the PropertyChangeListener
	//
	public void propertyChange(PropertyChangeEvent e) {
		//  Keep the fixed table in sync with the main table

		if ("selectionModel".equals(e.getPropertyName())) {
			fixed.setSelectionModel(main.getSelectionModel());
		}

		if ("model".equals(e.getPropertyName())) {
			fixed.setModel(main.getModel());
		}
	}
}
