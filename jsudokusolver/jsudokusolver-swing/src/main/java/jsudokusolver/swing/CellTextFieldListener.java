package jsudokusolver.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import jsudokusolver.core.Cell;
import jsudokusolver.core.CellStatus;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;

/**
 * Listener which links a {@link Cell} and a {@link SudokuTextField} handling
 * the changes in one to the other.
 */
class CellTextFieldListener implements PropertyChangeListener, DocumentListener {

	private final Cell cell;

	private final SudokuTextField textField;

	private PuzzleStatus puzzleStatus;

	private final Set<PuzzleStatus> editableStatus = EnumSet.of(PuzzleStatus.WAITING, PuzzleStatus.INVALID);
	
	public CellTextFieldListener(Puzzle puzzle, Cell cell, SudokuTextField textField) {
		if ((cell.getRow() != textField.getRow()) || (cell.getColumn() != textField.getCol())) {
			throw new IllegalArgumentException(
					"Cell " + cell.toString() + " does not match with Text Field " + textField.getName());
		}

		this.cell = cell;
		this.textField = textField;
		this.puzzleStatus = puzzle.getStatus();

		this.cell.addPropertyChangeListener(this);
		this.textField.getDocument().addDocumentListener(this);
		puzzle.addPropertyChangeListener(this);
		this.textField.addPropertyChangeListener(this);
	}

	private void cellStatusChanged(final CellStatus newCellStatus) {
		switch (newCellStatus) {
		case ORIGINAL:
			this.textField.setFont(SudokuTextField.FONT_ORIGINAL);
			if (!this.textField.getBackground().equals(SudokuTextField.COLOR_DEFAULT)) {
				this.textField.setBackground(SudokuTextField.COLOR_DEFAULT);
			}
			break;
		case ERROR:
			this.textField.setBackground(SudokuTextField.COLOR_ERROR);
			this.textField.requestFocusInWindow();
			break;
		case EVALUATING:
			this.textField.setBackground(SudokuTextField.COLOR_EVALUATING);
			break;
		case IDLE:
			this.textField.setBackground(SudokuTextField.COLOR_DEFAULT);
			break;
		case GUESSING:
			this.textField.setFont(Utils.FONT_DEFAULT);
			this.textField.setBackground(SudokuTextField.COLOR_GUESSING);
			break;
		default:
			this.textField.setFont(Utils.FONT_DEFAULT);
			break;
		}
	}

	private void cellValueChanged(Optional<?> value) {
		String str = value.isPresent() ? value.get().toString() : "";
		// Verification seen in http://stackoverflow.com/a/11743648/1659543 to
		// avoid "java.lang.IllegalStateException: Attempt to mutate in
		// notification"
		if (!str.equals(this.textField.getText())) {
			this.textField.setText(str);
		}
	}

	private void puzzleSatusChanged(PuzzleStatus puzzleStatus) {
		final boolean isEditable = this.editableStatus.contains(puzzleStatus);
		this.textField.setEditable(isEditable);
		this.textField.setDocumentFilter(isEditable ? SudokuDocFilter.INSTANCE : null);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case Cell.CELL_STATUS:
			this.cellStatusChanged((CellStatus) evt.getNewValue());
			break;
		case Cell.CELL_VALUE:
			this.cellValueChanged((Optional<?>) evt.getNewValue());
			break;
		case Puzzle.PUZZLE_STATUS:
			this.puzzleSatusChanged((PuzzleStatus) evt.getNewValue());
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (this.puzzleStatus == PuzzleStatus.WAITING) {
			try {
				final Document document = e.getDocument();
				final String text = document.getText(0, document.getLength());
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