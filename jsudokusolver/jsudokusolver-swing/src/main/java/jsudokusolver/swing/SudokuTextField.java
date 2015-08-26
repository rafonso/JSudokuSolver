package jsudokusolver.swing;

import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

public class SudokuTextField extends JTextField {

	private static final long serialVersionUID = 1970097593494061829L;

	public SudokuTextField(int row, int col) {
		super(1);
		super.setHorizontalAlignment(JTextField.CENTER);
		super.setName("Cell" + row + col);
		PlainDocument document = (PlainDocument)super.getDocument();
		document.setDocumentFilter(new SudokuDocFilter());

	}

}
