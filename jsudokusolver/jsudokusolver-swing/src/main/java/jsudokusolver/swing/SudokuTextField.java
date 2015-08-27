package jsudokusolver.swing;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

public class SudokuTextField extends JTextField implements FocusListener {

	private static final long serialVersionUID = 1970097593494061829L;

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
