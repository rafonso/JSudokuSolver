package jsudokusolver.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("CellTextFieldListener.propertyChange() " + evt.getPropertyName() + ": " + evt.getOldValue()
				+ " -> " + evt.getNewValue());
		switch (evt.getPropertyName()) {
		case Cell.CELL_STATUS:
			switch ((CellStatus) evt.getNewValue()) {
			case ORIGINAL:
				this.textField.setFont(SudokuTextField.FONT_ORIGINAL);
				break;
			case ERROR:
				this.textField.setBackground(SudokuTextField.COLOR_ERROR);
				break;
			default:
				this.textField.setFont(Utils.FONT_DEFAULT);
				break;
			}
			break;
		case Cell.CELL_VALUE:

			break;
		default:
			if (evt.getOldValue() != null) {
				throw new IllegalStateException("Unexpected Property Change: " + evt);
			}
		}

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (this.puzzleStatus == PuzzleStatus.WAITING) {
			try {
				final Document document = e.getDocument();
				final String text = document.getText(0, document.getLength());
				System.out.println("CellTextFieldListener.insertUpdate() " + text);
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
