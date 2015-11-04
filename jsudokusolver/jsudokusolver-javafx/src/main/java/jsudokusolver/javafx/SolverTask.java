package jsudokusolver.javafx;

import static javafx.application.Platform.runLater;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanIntegerProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanIntegerPropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;
import jsudokusolver.core.Solver;
import jsudokusolver.core.exception.CellWithNoRemaningValueExcetion;
import jsudokusolver.core.exception.NoSolutionPuzzleException;

public class SolverTask extends Task<Void> {

	private final Puzzle puzzle;

	private final ObjectProperty<PuzzleStatus> puzzleStatusProperty;

	private final Label lblCycles;

	private final Label lblTime;

	private final ComboBox<Integer> cmbStepTime;

	private Solver solver;

	private final IntegerProperty stepTime = new SimpleIntegerProperty();

	private long t0;

	private final ReadOnlyJavaBeanIntegerProperty solverCyclesProperty;

	private final ChangeListener<? super Number> lblCycleListener;

	private final ChangeListener<? super PuzzleStatus> puzzleStatusListener;

	SolverTask(Puzzle puzzle, ObjectProperty<PuzzleStatus> puzzleStatusProperty, Label lblCycles, Label lblTime,
			ComboBox<Integer> cmbStepTime) {
		super();
		this.puzzle = puzzle;
		this.puzzleStatusProperty = puzzleStatusProperty;
		this.lblCycles = lblCycles;
		this.lblTime = lblTime;
		this.cmbStepTime = cmbStepTime;

		try {
			this.solver = new Solver();
			this.solverCyclesProperty = ReadOnlyJavaBeanIntegerPropertyBuilder.create().bean(this.solver).name("cycle")
					.build();
			/*
			 * Workaround to allow that changes direclty in the Puzzle Status be
			 * detected by JavaFX Properties. See
			 * http://stackoverflow.com/q/32899031/1659543 for more details.
			 */
			this.solver.addPropertyChangeListener(evt -> runLater(() -> {
				if (evt.getPropertyName().equals(Solver.SOLVER_CYCLE)) {
					solverCyclesProperty.fireValueChangedEvent();
				}
			}));

			// bindings
			this.solverCyclesProperty.addListener(
					(ov, oldValue, newValue) -> runLater(() -> this.lblCycles.setText(newValue.toString())));

		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		lblCycleListener = (ov, oldVal, newVal) -> runLater(() -> this.lblCycles.setText(String.valueOf(newVal)));
		puzzleStatusListener = (ov, oldVal, newVal) -> runLater(() -> {
			switch (newVal) {
			case STOPPED:
				this.solver.requestStop();
			case SOLVED:
			case INVALID:
			case ERROR:
				this.removeBindsAndListeners();
				break;
			default:
				break;
			}
		});
	}

	private void addBindsAndListeners() {
		// TODO Change to binding?
		this.solverCyclesProperty.addListener(lblCycleListener);
		this.puzzleStatusProperty.addListener(puzzleStatusListener);
		this.stepTime.bind(this.cmbStepTime.getSelectionModel().selectedItemProperty());
	}

	private void removeBindsAndListeners() {
		this.solverCyclesProperty.removeListener(lblCycleListener);
		this.puzzleStatusProperty.removeListener(puzzleStatusListener);
		this.stepTime.unbind();
	}

	private void showCycle(int cycle) {
		runLater(() -> this.lblCycles.setText(String.valueOf(cycle)));
	}

	private void showTime() {
		runLater(() -> this.lblTime.setText(String.valueOf(System.currentTimeMillis() - t0)));
	}

	@Override
	protected void succeeded() {
		super.succeeded();
		this.removeBindsAndListeners();
	}

	private void stepTime(boolean pause) {
		if (pause && (stepTime.get() > 0)) {
			try {
				int delta = stepTime.get() * 1000 / 2;
				int millis = delta / 1000;
				int nanos = delta % 1000;
				Thread.sleep(millis, nanos);
			} catch (InterruptedException e) {
				// http://stackoverflow.com/a/9139139/1659543
				// restore interrupted status
				Thread.currentThread().interrupt();
			}
		}
	}

	private Exception showErrorMessageAndThrow(final String msg, Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Puzzle Error!");
		// alert.setHeaderText(headerText);
		alert.setContentText(msg);
		alert.showAndWait();

		return e;
	}

	@Override
	protected Void call() throws Exception {
		try {
			this.t0 = System.currentTimeMillis();
			this.showCycle(0);
			this.showTime();
			this.solver.start(puzzle);

			return null;
		} catch (CellWithNoRemaningValueExcetion e) {
			String msg = String.format("Cell [{%d,%d] with no remaing value", e.getCell().getRow(),
					e.getCell().getColumn());
			throw this.showErrorMessageAndThrow(msg, e);
		} catch (NoSolutionPuzzleException e) {
			throw this.showErrorMessageAndThrow("There is no solution for this puzzle.", e);
		}
	}

}
