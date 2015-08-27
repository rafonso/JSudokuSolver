package jsudokusolver.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * 
 * Code originally based on http://stackoverflow.com/a/6172281/1659543
 *
 */
class SudokuDocFilter extends DocumentFilter {

	private StringBuilder getOldText(final FilterBypass fb) throws BadLocationException {
		Document doc = fb.getDocument();
		String oldText = doc.getText(0, doc.getLength());
		return new StringBuilder(oldText);
	}

	private boolean verifyText(String text, boolean verifyDigit) {
		if (text.isEmpty()) {
			return true;
		}
		if (text.length() > 1) {
			return false;
		}
		if (verifyDigit) {
			char ch = text.charAt(0);
			int i = ch - '0';
			return i >= 1 && i <= 9;
		}

		return true;
	}

	@Override
	public void insertString(final FilterBypass fb, int offset, String string, AttributeSet attr)
			throws BadLocationException {
		StringBuilder sb = getOldText(fb);
		sb.insert(offset, string);
		System.out.println("SudokuDocFilter.insertString() : " + sb);
		if (verifyText(sb.toString(), true)) {
			super.insertString(fb, offset, string, attr);
		}
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
		StringBuilder sb = getOldText(fb);
		sb.replace(offset, offset + length, text);
		if (verifyText(sb.toString(), true)) {
			System.out.println("SudokuDocFilter.replace() : " + sb);
			super.replace(fb, offset, length, text, attrs);
		}
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		StringBuilder sb = getOldText(fb);
		sb.replace(offset, offset + length, "");
		System.out.println("SudokuDocFilter.remove() : " + sb);
		if (verifyText(sb.toString(), false)) {
			super.remove(fb, offset, length);
		}
	}
}