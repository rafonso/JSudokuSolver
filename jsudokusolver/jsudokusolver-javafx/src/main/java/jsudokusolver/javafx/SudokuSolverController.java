package jsudokusolver.javafx;

import static jsudokusolver.core.PuzzleStatus.ERROR;
import static jsudokusolver.core.PuzzleStatus.INVALID;
import static jsudokusolver.core.PuzzleStatus.READY;
import static jsudokusolver.core.PuzzleStatus.RUNNING;
import static jsudokusolver.core.PuzzleStatus.SOLVED;
import static jsudokusolver.core.PuzzleStatus.STOPPED;
import static jsudokusolver.core.PuzzleStatus.VALIDATING;
import static jsudokusolver.core.PuzzleStatus.WAITING;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;
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
	private Label lblTime;

	@FXML
	private Label lblCycles;

	@FXML
	private GridPane pnlControls;

	private final Puzzle puzzle;

	private final ObjectProperty<PuzzleStatus> puzzleStatusProperty;

	private IntegerProperty stepTime = new SimpleIntegerProperty();

	@SuppressWarnings("unchecked")
	public SudokuSolverController() {
		try {
			this.puzzle = new Puzzle();
			this.puzzleStatusProperty = JavaBeanObjectPropertyBuilder.create().bean(this.puzzle).name("status").build();
			/*
			 * Workaround to allow that changes direclty in the Puzzle Status be
			 * detected by JavaFX Properties. See
			 * http://stackoverflow.com/q/32899031/1659543 for more details.
			 */
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

	private void showInvalidMessage(final String headerText, String msg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Puzzle Error!");
		alert.setHeaderText(headerText);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	@FXML
	public void runPressed() {
		try {
			new Validator().validate(this.puzzle);

			this.puzzle.setStatus(RUNNING);
		} catch (RepeatedCellsException e) {
			final String headerText = "Repeated values";
			String msg = String.format("Repeated value %d in %s %d, cells [%d,%d] and [%d,%d]", //
					e.getRepeatedValue(), e.getPuzzlePositions().getDescription(), e.getPosition(), //
					e.getCell1().getRow(), e.getCell1().getColumn(), //
					e.getCell2().getRow(), e.getCell2().getColumn());
			showInvalidMessage(headerText, msg);
		} catch (EmptyPuzzleException e) {
			final String msg = "Empty Puzzle.";
			showInvalidMessage(msg, msg);
			this.pnlCells.getChildrenUnmodifiable().get(0).requestFocus();
		}
	}

	@FXML
	public void cleanPressed() {
		this.puzzle.cleanCells();
	}

	@FXML
	public void stopPressed() {
		this.puzzle.setStatus(STOPPED);
	}

	@FXML
	public void resetPressed() {
		this.puzzle.reset();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.stepTime.bind(this.cmbStepTime.getSelectionModel().selectedItemProperty());
		this.stepTime.addListener((ov, oldValue, newValue) -> System.out.println("stepTime = " + newValue));
	}

	@FXML
	public void keyTyped(KeyEvent event) {
		// System.out.println("SudokuSolverController.keyTyped(): " + event);
	}

	void init() {
		this.pnlCells.getChildrenUnmodifiable().forEach(new TextFieldAndSudokuCellBinder(puzzle, puzzleStatusProperty));

		final BooleanBinding btnRunDisabled = this.puzzleStatusProperty.isNotEqualTo(WAITING) //
				.and(this.puzzleStatusProperty.isNotEqualTo(VALIDATING))
				.and(this.puzzleStatusProperty.isNotEqualTo(READY))
				.and(this.puzzleStatusProperty.isNotEqualTo(INVALID));
		this.btnRun.disableProperty().bind(btnRunDisabled);
		this.btnClean.disableProperty().bind(this.puzzleStatusProperty.isEqualTo(RUNNING));
		this.btnStop.disableProperty().bind(this.btnClean.disableProperty().not());
		this.btnReset.disableProperty().bind(this.puzzleStatusProperty.isNotEqualTo(SOLVED) //
				.and(this.puzzleStatusProperty.isNotEqualTo(STOPPED)));

		BooleanBinding btnResetVisible = this.puzzleStatusProperty.isEqualTo(SOLVED)
				.or(this.puzzleStatusProperty.isEqualTo(STOPPED)).or(this.puzzleStatusProperty.isEqualTo(ERROR));
		this.btnReset.visibleProperty().bind(btnResetVisible);
		this.btnStop.visibleProperty().bind(this.btnReset.visibleProperty().not());

		BooleanBinding labelsVisible = btnResetVisible.or(this.puzzleStatusProperty.isEqualTo(RUNNING));
		this.pnlControls.getChildrenUnmodifiable().filtered(n -> n.getStyleClass().contains("counter"))
				.forEach(n -> n.visibleProperty().bind(labelsVisible));
	}

}
