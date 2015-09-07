package jsudokusolver.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import jsudokusolver.core.Cell;
import jsudokusolver.core.CellStatus;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;

class CellTextFieldListener implements PropertyChangeListener, DocumentListener {

	private final Cell cell;

	private final SudokuTextField textField;

	private PuzzleStatus puzzleStatus;

	public CellTextFieldListener(Puzzle puzzle, Cell cell, SudokuTextField textField) {
		if ((cell.getRow() != textField.getRow()) || (cell.getColumn() != textField.getCol())) {
			throw new IllegalArgumentException(
					"Cell " + cell.toString() + " does not match with Text Field " + textField.getName());
		}

		this.cell = cell;
		this.textField = textField;
		this.puzzleStatus = puzzle.getStatus();

		this.cell.addPropertyChangeListener(this);
		// this.textField.addPropertyChangeListener(this);
		this.textField.getDocument().addDocumentListener(this);
		puzzle.addPropertyChangeListener(this);
	}

	private void changeTextField(PropertyChangeEvent evt) {
		@SuppressWarnings("unchecked")
		Optional<Integer> value = (Optional<Integer>) evt.getNewValue();
		String str = value.isPresent() ? value.get().toString() : "";
		// Verification seen in http://stackoverflow.com/a/11743648/1659543 to
		// avoid "java.lang.IllegalStateException: Attempt to mutate in
		// notification"
		if (!str.equals(this.textField.getText())) {
			this.textField.setText(str);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case Cell.CELL_STATUS:
//		System.out.println(
//				evt.getSource() + " " + evt.getPropertyName() + ": " + evt.getOldValue() + " -> " + evt.getNewValue());
			switch ((CellStatus) evt.getNewValue()) {
			case ORIGINAL:
				this.textField.setFont(SudokuTextField.FONT_ORIGINAL);
				if (!this.textField.getBackground().equals(SudokuTextField.COLOR_DEFAULT)) {
					System.out.println(evt.getSource()+ ": isBackgroundSet? " + this.textField.isBackgroundSet());
					this.textField.setBackground(SudokuTextField.COLOR_DEFAULT);
					
				}
				break;
			case ERROR:
				this.textField.setBackground(SudokuTextField.COLOR_ERROR);
				this.textField.requestFocusInWindow();
				break;
			case IDLE:
				if (evt.getOldValue().equals(CellStatus.ERROR)) {
					this.textField.setBackground(SudokuTextField.COLOR_DEFAULT);
				}
				break;
			default:
				this.textField.setFont(Utils.FONT_DEFAULT);
				break;
			}
			break;
		case Cell.CELL_VALUE:
			this.changeTextField(evt);
			break;
		case Puzzle.PUZZLE_STATUS:
			PuzzleStatus puzzleStatus = (PuzzleStatus) evt.getNewValue();
			switch (puzzleStatus) {
			case RUNNING:
			case SOLVED:
			case STOPPED:
			case VALIDATING:
				this.textField.setEditable(false);
				break;
			default:
				this.textField.setEditable(true);
				break;
			}
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (this.puzzleStatus == PuzzleStatus.WAITING) {
			try {
				final Document document = e.getDocument();
				final String text = document.getText(0, document.getLength());
				// System.out.println("CellTextFieldListener.insertUpdate() " +
				// text);
				this.cell.setValueStatus(Integer.valueOf(text), CellStatus.ORIGINAL);
			} catch (BadLocationException e1) {
				throw new RuntimeException("Problem in " + this.textField.getName() + ": " + e1.getMessage(), e1);
			}
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		this.cell.setValueStatus(null, CellStatus.IDLE);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		System.out.println("CellTextFieldListener.changedUpdate() + " + e);
	}

}
