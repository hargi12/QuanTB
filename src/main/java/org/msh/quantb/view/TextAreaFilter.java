package org.msh.quantb.view;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class TextAreaFilter extends DocumentFilter{
	/* the number of characters including spaces */
	private int maxLen = 36;

	public TextAreaFilter (int max) { 
		this.maxLen = max;
	} 

	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String str, AttributeSet attr) throws BadLocationException {
		if ((fb.getDocument().getLength() + str.length()) <= this.maxLen)
			super.replace(fb, offset, length, str, attr);
		else{
			String substr = "";
			if(fb.getDocument().getLength() <= maxLen){
				int len = maxLen - fb.getDocument().getLength();
				if(len > 0){
					substr = str.substring(0, len);
				}else{
					substr = str;
				}
			}
			super.replace(fb, offset, length, substr, attr);
		}
	}
}
