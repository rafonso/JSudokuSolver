package jsudokusolver.javafx;

import java.net.URL;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import jsudokusolver.core.Cell;
import jsudokusolver.core.CellStatus;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.Puzzle.CellsFormatter;
import jsudokusolver.core.PuzzleStatus;

public class SudokuSolverController implements Initializable {

	private final Set<PuzzleStatus> editableStatus = EnumSet.of(PuzzleStatus.WAITING, PuzzleStatus.INVALID);

	private final PuzzleFormatParserEventHandler puzzleFormatParserHandler;

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

	private BooleanProperty stopVisible = new SimpleBooleanProperty(true);

	private IntegerProperty stepTime = new SimpleIntegerProperty();

	private boolean disableBtns = true;

	public SudokuSolverController() {
		this.puzzle = new Puzzle();
		puzzleFormatParserHandler = new PuzzleFormatParserEventHandler(this.puzzle);
	}

	private Random random = new Random();

	private void bindTextFieldAndSudokuCell(Node node) {
		try {
			SudokuTextField textField = (SudokuTextField) node;
			Cell cell = this.puzzle.getCell(textField.getRow(), textField.getColumn());
			textField.addEventFilter(KeyEvent.KEY_PRESSED, this.puzzleFormatParserHandler);

			@SuppressWarnings("unchecked")
			ObjectProperty<Optional<Integer>> valueProperty = JavaBeanObjectPropertyBuilder.create().bean(cell)
					.name("value").build();
			final StringProperty textProperty = textField.textProperty();

			// Binding ...
			Bindings.bindBidirectional(textProperty, valueProperty, new CellValueStringConverter());

			textField.addEventFilter(MouseEvent.MOUSE_CLICKED, me -> {
				if (me.getClickCount() == 2) {
					int x = random.nextInt(10);
					cell.setValue(x > 0 ? Optional.of(x) : Optional.empty());
				}
			});

			// // textProperty.addListener(
			// // ov -> System.out.printf("textProperty : invalidation.
			// // valueProperty: %n", valueProperty.get()));
			// textProperty.addListener(
			// (ov, oldValue, newValue) -> System.out.printf("textProperty : %s
			// -> %s%n", oldValue, newValue));
			// // valueProperty.addListener(
			// // ov -> System.out.printf("valueProperty: invalidation.
			// // textProperty: %n", textProperty.get()));
			// valueProperty.addListener(
			// (ov, oldValue, newValue) -> System.out.printf("valueProperty: %s
			// -> %s%n", oldValue, newValue));

			@SuppressWarnings("unchecked")
			ObjectProperty<CellStatus> cellStatusProperty = JavaBeanObjectPropertyBuilder.create().bean(cell)
					.name("status").build();

//			cellStatusProperty.addListener((ov, oldValue, newValue) -> System.out
//					.printf("cellStatusProperty : %s -> %s%n", oldValue, newValue));

			valueProperty.addListener((ov, oldValue, newValue) -> {
				if (puzzle.getStatus() == PuzzleStatus.WAITING) {
					cell.setStatus(newValue.isPresent() ? CellStatus.ORIGINAL : CellStatus.IDLE);
				}
			});
			cellStatusProperty.addListener((ov, oldValue, newValue) -> {
				textField.getStyleClass().remove(oldValue.toString());
				textField.getStyleClass().add(newValue.toString());
			});

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
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	public void runPressed() {
		System.out.println("SudokuSolverController.runPressed()");

		this.stopVisible.set(!this.stopVisible.get());
	}

	@FXML
	public void cleanPressed() {
		System.out.println("SudokuSolverController.cleanPressed()");

		disableBtns = !disableBtns;
		this.btnStop.setDisable(disableBtns);
		this.btnReset.setDisable(disableBtns);

		this.puzzle.cleanCells();
	}

	@FXML
	public void stopPressed() {
		System.out.println("SudokuSolverController.stopPressed()");
	}

	@FXML
	public void resetPressed() {
		System.out.println("SudokuSolverController.resetPressed()");
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
		this.pnlCells.getChildrenUnmodifiable().forEach(this::bindTextFieldAndSudokuCell);
	}

}
