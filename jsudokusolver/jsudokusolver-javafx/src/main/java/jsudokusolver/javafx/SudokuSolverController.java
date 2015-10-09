package jsudokusolver.javafx;

import static jsudokusolver.core.PuzzleStatus.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import jsudokusolver.core.Cell;
import jsudokusolver.core.CellStatus;
import jsudokusolver.core.CorrectedCellListener;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;
import jsudokusolver.core.Solver;
import jsudokusolver.core.Validator;
import jsudokusolver.core.exception.EmptyPuzzleException;
import jsudokusolver.core.exception.RepeatedCellsException;

public class SudokuSolverController implements Initializable {

	@FXML
	private GridPane pnlCells;

	@FXML
	private Button btnRun;

	@FXML
	private Button btnClean;

	@FXML
	private Button btnStop;

	@FXML
	private Button btnReset;

	@FXML
	private StackPane pnlStopReset;

	@FXML
	private ComboBox<Integer> cmbStepTime;

	@FXML
	private Label lblTimeMs;

	@FXML
	private Label lblCycles;

	private final Puzzle puzzle;

	private final ObjectProperty<PuzzleStatus> puzzleStatusProperty;

	private BooleanProperty stopVisible = new SimpleBooleanProperty(true);

	private IntegerProperty stepTime = new SimpleIntegerProperty();

	private boolean disableBtns = true;

	@SuppressWarnings("unchecked")
	public SudokuSolverController() {
		try {
			this.puzzle = new Puzzle();
			this.puzzleStatusProperty = JavaBeanObjectPropertyBuilder.create().bean(this.puzzle).name("status").build();
			// See Observation in bindTextFieldAndSudokuCell().
			this.puzzle.addPropertyChangeListener(evt -> {
				if (evt.getPropertyName().equals(Puzzle.PUZZLE_STATUS)) {
					final PuzzleStatus newValue = (PuzzleStatus) evt.getNewValue();
					puzzleStatusProperty.set(newValue);
				}
			});

		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

	}

	private Random random = new Random();

	private <E extends Enum<E>> ChangeListener<E> changeCellStyle(SudokuTextField textField) {
		return (ov, oldValue, newValue) -> {
			final List<String> styleClass = textField.getStyleClass();
			styleClass.remove(oldValue.toString());
			styleClass.add(newValue.toString());
		};
	}

	private void bindTextFieldAndSudokuCell(Node node, PuzzleFormatParserEventHandler puzzleFormatParserEventHandler,
			CorrectedCellListener correctedCellListener, BooleanBinding puzzleStatusIsEditable) {
		try {
			SudokuTextField textField = (SudokuTextField) node;
			textField.addEventFilter(KeyEvent.KEY_PRESSED, puzzleFormatParserEventHandler);
			Cell cell = this.puzzle.getCell(textField.getRow(), textField.getColumn());

			@SuppressWarnings("unchecked")
			ObjectProperty<Optional<Integer>> valueProperty = JavaBeanObjectPropertyBuilder.create().bean(cell)
					.name("value").build();
			final StringProperty textProperty = textField.textProperty();
			// Binding ...
			Bindings.bindBidirectional(textProperty, valueProperty, new CellValueStringConverter());
			valueProperty.addListener((ov, oldValue, newValue) -> {
				if (puzzle.getStatus() == PuzzleStatus.WAITING || puzzle.getStatus() == PuzzleStatus.INVALID) {
					cell.setStatus(newValue.isPresent() ? CellStatus.ORIGINAL : CellStatus.IDLE);
				}
				if (puzzle.getStatus() == PuzzleStatus.INVALID) {
					puzzle.setStatus(PuzzleStatus.WAITING);
				}
			});

			@SuppressWarnings("unchecked")
			ObjectProperty<CellStatus> cellStatusProperty = JavaBeanObjectPropertyBuilder.create().bean(cell)
					.name("status").build();
			cellStatusProperty.addListener(this.changeCellStyle(textField));
			cellStatusProperty.addListener((ov, oldValue, newValue) -> {
				CellStatus newStatus = newValue;
				if (newStatus == CellStatus.ERROR) {
					textField.requestFocus();
				}
			});
			this.puzzleStatusProperty.addListener(this.changeCellStyle(textField));

			textField.getStyleClass().add(cell.getStatus().toString());
			textField.getStyleClass().add(this.puzzle.getStatus().toString());

			/*
			 * Workaround to allow that changes direclty in the Cell value be
			 * detected by JavaFX Properties. See
			 * http://stackoverflow.com/q/32899031/1659543 for more details.
			 */
			cell.addPropertyChangeListener(evt -> {
				if (evt.getPropertyName().equals(Cell.CELL_VALUE)) {
					@SuppressWarnings("unchecked")
					final Optional<Integer> newValue = (Optional<Integer>) evt.getNewValue();
					valueProperty.set(newValue);
				} else if (evt.getPropertyName().equals(Cell.CELL_STATUS)) {
					final CellStatus newValue = (CellStatus) evt.getNewValue();
					cellStatusProperty.set(newValue);
				}
			});
			cell.addPropertyChangeListener(correctedCellListener);

			textField.editableProperty().bind(puzzleStatusIsEditable);
			textField.focusTraversableProperty().bind(puzzleStatusIsEditable);

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

	@FXML
	public void runPressed() {
		System.out.println("SudokuSolverController.runPressed()");
		try {
			new Validator().validate(this.puzzle);

			this.puzzle.setStatus(PuzzleStatus.RUNNING);
		} catch (RepeatedCellsException e) {
			String msg = String.format("Repeated value %d in %s %d, cells [%d,%d] and [%d,%d]", //
					e.getRepeatedValue(), e.getPuzzlePositions().getDescription(), e.getPosition(), //
					e.getCell1().getRow(), e.getCell1().getColumn(), //
					e.getCell2().getRow(), e.getCell2().getColumn());

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Puzzle Error!");
			alert.setHeaderText("Repeated values");
			alert.setContentText(msg);
			alert.showAndWait();
		} catch (EmptyPuzzleException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Puzzle Error!");
			alert.setHeaderText("Empty Puzzle.");
			alert.setContentText("Empty Puzzle.");
			alert.showAndWait();

			this.pnlCells.getChildrenUnmodifiable().get(0).requestFocus();
		}
	}

	@FXML
	public void cleanPressed() {
		System.out.println("SudokuSolverController.cleanPressed()");

		this.puzzle.cleanCells();
	}

	@FXML
	public void stopPressed() {
		System.out.println("SudokuSolverController.stopPressed()");
		this.puzzle.setStatus(STOPPED);
	}

	@FXML
	public void resetPressed() {
		System.out.println("SudokuSolverController.resetPressed()");
		this.puzzle.reset();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.stepTime.bind(this.cmbStepTime.getSelectionModel().selectedItemProperty());
		this.stepTime.addListener((ov, oldValue, newValue) -> System.out.println("stepTime = " + newValue));

		this.btnStop.visibleProperty().bind(this.stopVisible);
		this.btnReset.visibleProperty().bind(this.stopVisible.not());

		// this.pnlCells.getScene().getst

	}

	@FXML
	public void keyTyped(KeyEvent event) {
		System.out.println("SudokuSolverController.keyTyped(): " + event);
	}

	void init() {
		final PuzzleFormatParserEventHandler puzzleFormatParserHandler = new PuzzleFormatParserEventHandler(
				this.puzzle);
		final CorrectedCellListener correctedCellListener = new CorrectedCellListener(this.puzzle);
		final BooleanBinding puzzleStatusIsEditable = this.puzzleStatusProperty.isEqualTo(PuzzleStatus.WAITING)
				.or(this.puzzleStatusProperty.isEqualTo(PuzzleStatus.INVALID));
		this.pnlCells.getChildrenUnmodifiable().forEach(node -> this.bindTextFieldAndSudokuCell(node,
				puzzleFormatParserHandler, correctedCellListener, puzzleStatusIsEditable));

		this.btnRun.disableProperty().bind( //
				this.puzzleStatusProperty.isNotEqualTo(WAITING) //
						.and(this.puzzleStatusProperty.isNotEqualTo(VALIDATING))
						.and(this.puzzleStatusProperty.isNotEqualTo(READY))
						.and(this.puzzleStatusProperty.isNotEqualTo(INVALID)));
		this.btnClean.disableProperty().bind(this.puzzleStatusProperty.isEqualTo(RUNNING));
		this.btnStop.disableProperty().bind(this.puzzleStatusProperty.isNotEqualTo(RUNNING));
		this.btnReset.disableProperty().bind( //
				this.puzzleStatusProperty.isEqualTo(SOLVED) //
						.or(this.puzzleStatusProperty.isEqualTo(STOPPED)).not());

		BooleanBinding btnResetVisible = this.puzzleStatusProperty.isEqualTo(SOLVED)
				.or(this.puzzleStatusProperty.isEqualTo(STOPPED)).or(this.puzzleStatusProperty.isEqualTo(ERROR));
		this.btnReset.visibleProperty().bind(btnResetVisible);
		this.btnStop.visibleProperty().bind(this.btnReset.visibleProperty().not());

	}

}
