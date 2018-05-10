package org.msh.quantb.view;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * Document filter which will accept only positive (>=0) numbers.
 * @author User
 * 
 */
public class NumericFilter extends DocumentFilter {
	
	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.insert(offset, string);

		if (isValid(sb.toString())) {
			super.insertString(fb, offset, string, attr);
		}
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		boolean replaceZero = false;
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));		
		sb.replace(offset, offset + length, text);
		if (sb.length() > 1 && sb.charAt(0) == '0') {
			replaceZero = true;
		}
		if (isValid(sb.toString())) {
			super.replace(fb, replaceZero?0:offset, replaceZero?1:length, text, attrs);
		}
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.delete(offset, offset + length);
		if (sb.toString().isEmpty()) {
			replace(fb, offset, length, "0", doc.getDefaultRootElement().getAttributes());
		} else if (isValid(sb.toString())) {
			super.remove(fb, offset, length);
		}
	}

	/**
	 * Check for, source text may be positive number?
	 * 
	 * @param text
	 *            source
	 * @return true - positive number, false - another
	 */
	private boolean isValid(String text) {
		try {
			int tmp = Integer.parseInt(text);
			return tmp >= 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
