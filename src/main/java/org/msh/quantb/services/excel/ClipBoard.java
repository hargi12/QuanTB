package org.msh.quantb.services.excel;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JTable;

/**
 * Receive data from the Excel, use the System Clipboard
 * @author alexey
 *
 */
public class ClipBoard {
	/**
	 * simple imitate ctrl-c
	 * @param table 
	 * @param offset columns index offset if this table will became fixed column
	 */
	public static void copy(JTable table, int offset){
		int cols[] = table.getSelectedColumns();
		int rows[] = table.getSelectedRows();
		String res = "";
		for(int i=0;i<rows.length;i++){
			for(int j=0;j<cols.length;j++){
				if(cols[j]>=0){
					res=res+table.getModel().getValueAt(rows[i], cols[j]+offset).toString() +"\t";
				}
			}
			if(res.length()>0){
				res=res.substring(0,res.length()-1);
				res=res+"\n";
			}
		}
		if(res.length()>0){
			res=res.substring(0,res.length()-1);
			Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
			Clipboard systemClipboard = defaultToolkit.getSystemClipboard();
			systemClipboard.setContents(new StringSelection(res), null);
		}
	}

	/**
	 * Get quantities from the excel file use the System clipboard
	 * @return Two dimension Integer array with row and columns contains quantities or null if something wrong on the clipboard
	 */
	public static Integer[][] getQuantities(){
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		if (toolkit != null){
			Clipboard board = Toolkit.getDefaultToolkit().getSystemClipboard();
			if (board != null){
				Transferable contents = board.getContents(board);
				boolean hasTransferableText =
						(contents != null) &&
						contents.isDataFlavorSupported(DataFlavor.stringFlavor)
						;
				if (hasTransferableText) {
					try {
						String result = (String)contents.getTransferData(DataFlavor.stringFlavor);
						String[] rows = result.split("\\n");
						String[][] data = new String[rows.length][];
						int i = 0;
						for(String value : rows){
							data[i] = value.split("\\t");
							i++;
						}
						return convertToIntegers(data);
					} catch (UnsupportedFlavorException e) {
						return null;
					} catch (IOException e) {
						return null;
					}
				}

			}
			return null;
		}
		return null;
	}
	/**
	 * Convert string data to the Integer data
	 * @param data
	 * @return two dimension Integer array or null if it is impossible (something wrong in the data parameter)
	 */
	private static Integer[][] convertToIntegers(String[][] data) {
		Integer[][] result = new Integer[data.length][];
		if(data.length>0){
			for(int i=0;i<data.length;i++){
				result[i] = new Integer[data[i].length];
				for(int j=0; j<data[i].length;j++){
					try {
						result[i][j] = new Integer(data[i][j]);
					} catch (NumberFormatException e) {
						return null;
					}
				}
			}
		}
		if (checkResult(result)){
			return result;
		}else{
			return null;
		}
	}
	/**
	 * Result must have equals number of columns for each row
	 * @param result result to checked
	 * @return
	 */
	private static boolean checkResult(Integer[][] result) {
		if (result == null){
			return false;
		}
		try {
			int length = result[0].length;
			boolean flag = true;
			for(int i=0 ; i<result.length;i++){
				if(result[i].length != length){
					flag = false;
					break;
				};
			}
			return flag;
		} catch (Exception e) {
			return false;
		}
	}
}
