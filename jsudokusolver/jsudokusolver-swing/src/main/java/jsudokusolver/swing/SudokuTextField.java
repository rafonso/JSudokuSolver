package jsudokusolver.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

public class SudokuTextField extends JTextField implements FocusListener {

	private static final long serialVersionUID = 1970097593494061829L;
	
	private static final Font FONT_DEFAULT = new Font("Tahoma", Font.PLAIN, 16);
	@SuppressWarnings("unused")
	private static final Font FONT_ORIGINAL = FONT_DEFAULT.deriveFont(Font.BOLD);
	@SuppressWarnings("unused")
	private static final Font FONT_GUESS = FONT_DEFAULT.deriveFont(Font.ITALIC);

	private final int row;
	private final int col;

	public SudokuTextField(int row, int col) {
		super(1);
		super.setHorizontalAlignment(JTextField.CENTER);
		super.setName("Cell" + row + col);

		this.row = row;
		this.col = col;

		PlainDocument document = (PlainDocument) super.getDocument();
		document.setDocumentFilter(new SudokuDocFilter());
		this.addFocusListener(this);

		// Border
		int top = (row == 1) ? 3 : 1; 
		int left = (col == 1) ? 3 : 1; 
		int bottom = (row == 9) ? 3 : ((row == 3) || (row == 6)) ? 2 : 1;
		int right = (col == 9) ? 3 : ((col == 3) || (col == 6)) ? 2 : 1;
		super.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
		
		super.setFont(FONT_DEFAULT);
	}

	int getPosition() {
		return (row - 1) * 9 + col - 1;
	}

	int getRow() {
		return row;
	}

	int getCol() {
		return col;
	}

	void clean() {
		this.setText("");
	}

	@Override
	public void focusGained(FocusEvent e) {
		this.selectAll();
	}

	@Override
	public void focusLost(FocusEvent e) {
		// Do nothing
	}

}
