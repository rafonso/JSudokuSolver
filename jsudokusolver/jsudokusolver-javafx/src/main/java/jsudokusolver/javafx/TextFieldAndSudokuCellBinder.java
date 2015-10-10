package jsudokusolver.javafx;

import static jsudokusolver.core.PuzzleStatus.INVALID;
import static jsudokusolver.core.PuzzleStatus.WAITING;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import jsudokusolver.core.Cell;
import jsudokusolver.core.CellStatus;
import jsudokusolver.core.CorrectedCellListener;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;

class TextFieldAndSudokuCellBinder implements Consumer<Node> {

	private final Puzzle puzzle;

	private final ObjectProperty<PuzzleStatus> puzzleStatusProperty;

	private final PuzzleFormatParserEventHandler puzzleFormatParserHandler;
	private final CorrectedCellListener correctedCellListener;
	private final BooleanBinding puzzleStatusIsEditable;

	public TextFieldAndSudokuCellBinder(Puzzle puzzle, ObjectProperty<PuzzleStatus> puzzleStatusProperty) {
		this.puzzle = puzzle;
		this.puzzleStatusProperty = puzzleStatusProperty;

		this.puzzleFormatParserHandler = new PuzzleFormatParserEventHandler(this.puzzle);
		this.correctedCellListener = new CorrectedCellListener(this.puzzle);
		this.puzzleStatusIsEditable = this.puzzleStatusProperty.isEqualTo(WAITING)
				.or(this.puzzleStatusProperty.isEqualTo(INVALID));
	}

	@Deprecated
	private Random random = new Random();

	private <E extends Enum<E>> ChangeListener<E> changeCellStyle(List<String> styleClass) {
		return (ov, oldValue, newValue) -> {
			styleClass.remove(oldValue.toString());
			styleClass.add(newValue.toString());
		};
	}

	private void bindStyleClass(SudokuTextField textField, Cell cell, ObjectProperty<CellStatus> cellStatusProperty) {
		final ObservableList<String> styleClass = textField.getStyleClass();

		styleClass.addAll(cell.getStatus().toString(), this.puzzle.getStatus().toString());

		cellStatusProperty.addListener(this.changeCellStyle(styleClass));
		this.puzzleStatusProperty.addListener(this.changeCellStyle(styleClass));
	}

	private ChangeListener<? super CellStatus> requestFocusWhenError(SudokuTextField textField) {
		return (ov, oldValue, newValue) -> {
			CellStatus newStatus = newValue;
			if (newStatus == CellStatus.ERROR) {
				textField.requestFocus();
			}
		};
	}

	private ChangeListener<? super Optional<Integer>> cellStatusWhileWaiting(Cell cell) {
		return (ov, oldValue, newValue) -> {
			if (puzzle.getStatus() == WAITING || puzzle.getStatus() == INVALID) {
				cell.setStatus(newValue.isPresent() ? CellStatus.ORIGINAL : CellStatus.IDLE);
			}
			if (puzzle.getStatus() == INVALID) {
				puzzle.setStatus(WAITING);
			}
		};
	}

	/**
	 * Workaround to allow that changes direclty in the Cell value and status be
	 * detected by JavaFX Properties. See
	 * http://stackoverflow.com/q/32899031/1659543 for more details.
	 */
	private PropertyChangeListener bindCellEvents(ObjectProperty<Optional<Integer>> valueProperty,
			ObjectProperty<CellStatus> cellStatusProperty) {
		return evt -> {
			if (evt.getPropertyName().equals(Cell.CELL_VALUE)) {
				@SuppressWarnings("unchecked")
				final Optional<Integer> newValue = (Optional<Integer>) evt.getNewValue();
				valueProperty.set(newValue);
			} else if (evt.getPropertyName().equals(Cell.CELL_STATUS)) {
				final CellStatus newValue = (CellStatus) evt.getNewValue();
				cellStatusProperty.set(newValue);
			}
		};
	}

	@Override
	public void accept(Node node) {
		try {
			SudokuTextField textField = (SudokuTextField) node;
			textField.addEventFilter(KeyEvent.KEY_PRESSED, puzzleFormatParserHandler);
			Cell cell = this.puzzle.getCell(textField.getRow(), textField.getColumn());

			@SuppressWarnings("unchecked")
			ObjectProperty<Optional<Integer>> valueProperty = JavaBeanObjectPropertyBuilder.create().bean(cell)
					.name("value").build();
			final StringProperty textProperty = textField.textProperty();
			// Binding ...
			Bindings.bindBidirectional(textProperty, valueProperty, new CellValueStringConverter());
			valueProperty.addListener(cellStatusWhileWaiting(cell));

			@SuppressWarnings("unchecked")
			ObjectProperty<CellStatus> cellStatusProperty = JavaBeanObjectPropertyBuilder.create().bean(cell)
					.name("status").build();
			cellStatusProperty.addListener(this.requestFocusWhenError(textField));

			// TextField Style
			this.bindStyleClass(textField, cell, cellStatusProperty);

			cell.addPropertyChangeListener(this.bindCellEvents(valueProperty, cellStatusProperty));
			cell.addPropertyChangeListener(this.correctedCellListener);

			textField.editableProperty().bind(this.puzzleStatusIsEditable);
			textField.focusTraversableProperty().bind(this.puzzleStatusIsEditable);

			// TODO Remove when Solver is implemented.
			textField.addEventFilter(MouseEvent.MOUSE_CLICKED, me -> {
				if (me.getClickCount() == 2) {
					int x = random.nextInt(10);
					cell.setValue(x > 0 ? Optional.of(x) : Optional.empty());
				} else if (me.getClickCount() == 3) {
					System.out.println(textField.getId() + ": " + textField.getStyleClass());
				}
			});
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

}
