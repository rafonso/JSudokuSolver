package jsudokusolver.core;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static jsudokusolver.core.Cell.CELL_TO_VALUE;
import static jsudokusolver.core.CellFunctions.rangeStream;
import static jsudokusolver.core.CellStatus.EVALUATING;
import static jsudokusolver.core.CellStatus.FILLED;
import static jsudokusolver.core.CellStatus.GUESSING;
import static jsudokusolver.core.CellStatus.IDLE;
import static jsudokusolver.core.PuzzlePositions.COLUMN;
import static jsudokusolver.core.PuzzlePositions.ROW;
import static jsudokusolver.core.PuzzlePositions.SECTOR;
import static jsudokusolver.core.PuzzleStatus.ERROR;
import static jsudokusolver.core.PuzzleStatus.READY;
import static jsudokusolver.core.PuzzleStatus.RUNNING;
import static jsudokusolver.core.PuzzleStatus.SOLVED;
import static jsudokusolver.core.PuzzleStatus.STOPPED;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import jsudokusolver.core.SolverGuessEvent.SolverGuessEventType;
import jsudokusolver.core.exception.CellWithNoRemaningValueExcetion;
import jsudokusolver.core.exception.NoSolutionPuzzleException;

/**
 * Solves a {@link Puzzle}
 */
public class Solver {

	/**
	 * Indicates the {@link #getCycle() Cycle} was changed.
	 */
	public static final String SOLVER_CYCLE = "Solver.cycle";

	private int cycle = 0;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private final List<SolverGuessListener> solverGuessListeners = new ArrayList<>();

	/**
	 * Flag to interrupt this Solver
	 */
	private boolean stopRequested = false;

	/**
	 * Throws a {@link NoSolutionPuzzleException}
	 * @param isInvalid
	 * @param puzzle
	 */
	private void verifyIfCanBeSolved(boolean isInvalid, Puzzle puzzle) {
		if (isInvalid) {
			puzzle.setStatus(ERROR);
			throw new NoSolutionPuzzleException();
		}
	}

	/**
	 * Incements the current Solver's Cycle. It fires a
	 * {@link PropertyChangeEvent} of type {@link #SOLVER_CYCLE}.
	 */
	private void incrementCycle() {
		this.cycle++;
		this.pcs.firePropertyChange(SOLVER_CYCLE, this.cycle - 1, this.cycle);
	}

	private Predicate<Integer> getCellValuePredicate(Puzzle puzzle, int pos, PuzzlePositions puzzlePositions) {
		return puzzle.getCellsStream() //
				.filter(puzzlePositions.getPositionPredicate(pos)) //
				.filter(Cell::hasValue) //
				.map(CELL_TO_VALUE) //
				.collect(toSet()) //
				::contains;
	}

	private List<Integer> getPossibleValues(Cell c, Puzzle puzzle) {
		return rangeStream() //
				.filter(this.getCellValuePredicate(puzzle, c.getRow(), ROW).negate())
				.filter(this.getCellValuePredicate(puzzle, c.getColumn(), COLUMN).negate())
				.filter(this.getCellValuePredicate(puzzle, c.getSector(), SECTOR).negate()) //
				.collect(toList());
	}

	private void solveCell(Cell c, Puzzle puzzle, boolean mementoIsEmpty) {
		c.setStatus(EVALUATING);

		List<Integer> possibleValues = this.getPossibleValues(c, puzzle);

		if (possibleValues.isEmpty() && mementoIsEmpty) {
			c.setStatus(CellStatus.ERROR);
			puzzle.setStatus(ERROR);
			throw new CellWithNoRemaningValueExcetion(c);
		}
		if (possibleValues.size() == 1) {
			c.setValueStatus(possibleValues.get(0), FILLED);
		} else {
			c.setStatus(IDLE);
		}
	}

	private void fireSolverGuessEvent(SolverGuessEventType type, Cell guessCell) {
		SolverGuessEvent event = new SolverGuessEvent(this, type, guessCell);
		solverGuessListeners.forEach(l -> l.guessAction(event));
	}

	private List<Cell> getEmptyCells(Puzzle puzzle) {
		return puzzle.getCellsStream().filter(c -> !c.hasValue()).collect(toList());
	}

	private SolverGuess removeInvalidGuess(Puzzle puzzle, Deque<SolverGuess> memento, boolean popMemento) {
		if (popMemento) {
			memento.pop();
			this.verifyIfCanBeSolved(memento.isEmpty(), puzzle);
		}

		SolverGuess solverGuess = memento.peek();
		solverGuess.discardCurrentGuessValue();
		final int[] guessPosition = solverGuess.getGuessPosition();
		this.fireSolverGuessEvent(SolverGuessEventType.REMOVAL, puzzle.getCell(guessPosition[0], guessPosition[1]));
		return solverGuess;
	}

	private void fillGuess(SolverGuess currentGuess, Puzzle puzzle) {
		currentGuess.getEmptyPositions()
				.forEach(position -> puzzle.getCell(position[0], position[1]).setValueStatus(null, IDLE));
		int[] guessPosition = currentGuess.getGuessPosition();
		final Cell guessCell = puzzle.getCell(guessPosition[0], guessPosition[1]);
		guessCell.setValueStatus(currentGuess.getCurrentGuessValue(), GUESSING);
		this.fireSolverGuessEvent(SolverGuessEventType.ADDITION, guessCell);
	}

	private void prepareGuess(Puzzle puzzle, List<Cell> emptyCells, Deque<SolverGuess> memento) {
		final BinaryOperator<Cell> accumulator = (c1,
				c2) -> (getPossibleValues(c1, puzzle).size() <= getPossibleValues(c2, puzzle).size()) ? c1 : c2;
		Cell guessCell = emptyCells.subList(1, emptyCells.size()).stream().reduce(emptyCells.get(0), accumulator);
		List<Integer> possibleValues = this.getPossibleValues(guessCell, puzzle);

		this.verifyIfCanBeSolved(possibleValues.isEmpty() && memento.isEmpty(), puzzle);
		if (!possibleValues.isEmpty()) {
			final Function<? super Cell, ? extends int[]> cellToPosition = c -> new int[] { c.getRow(), c.getColumn() };
			List<int[]> emptyPositions = emptyCells.stream().map(cellToPosition).collect(toList());
			memento.addFirst(new SolverGuess(cellToPosition.apply(guessCell), possibleValues, emptyPositions));
		} else if (!memento.isEmpty()) {
			SolverGuess solverGuess = this.removeInvalidGuess(puzzle, memento, false);
			while (solverGuess.isEmpty()) {
				solverGuess = this.removeInvalidGuess(puzzle, memento, true);
			}
		}

		fillGuess(memento.peek(), puzzle);
	}

	private List<Cell> solveCycle(Puzzle puzzle, final Deque<SolverGuess> guesses, List<Cell> emptyCells) {
		this.incrementCycle();
		for (Cell c : emptyCells) {
			this.solveCell(c, puzzle, guesses.isEmpty());
			if (stopRequested) {
				break;
			}
		}

		if (stopRequested) {
			return Collections.emptyList();
		}

		List<Cell> nextEmptyCells = this.getEmptyCells(puzzle);
		if (nextEmptyCells.size() == emptyCells.size()) {
			this.prepareGuess(puzzle, nextEmptyCells, guesses);
			nextEmptyCells = this.getEmptyCells(puzzle);
		}
		return nextEmptyCells;
	}

	/**
	 * Starts a Puzzle solving.
	 * 
	 * @param puzzle
	 *            Puzzle to be solved.
	 */
	public void start(Puzzle puzzle) {
		assert puzzle.getStatus() == READY : "Puzzle is not ready to be solved. Current status: " + puzzle.getStatus();

		final Deque<SolverGuess> guesses = new LinkedList<>();
		List<Cell> emptyCells = this.getEmptyCells(puzzle);

		puzzle.setStatus(RUNNING);
		// Main loop
		while (!stopRequested && !emptyCells.isEmpty()) {
			emptyCells = this.solveCycle(puzzle, guesses, emptyCells);
		}
		puzzle.setStatus(this.stopRequested ? STOPPED : SOLVED);
	}

	/**
	 * @return The current Solver's cycle.
	 */
	public int getCycle() {
		return cycle;
	}

	/**
	 * Adds a apropriate {@link PropertyChangeListener Listener} to this Solver.
	 * 
	 * @param listener
	 *            Listener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a {@link PropertyChangeListener Listener} to this Solver.
	 * 
	 * @param listener
	 *            Listener to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Adds a apropriate {@link SolverGuessListener} to this Solver.
	 * 
	 * @param listener
	 *            SolverGuessListener to be added.
	 */
	public void addSolverGuessListener(SolverGuessListener listener) {
		this.solverGuessListeners.add(listener);
	}

	/**
	 * Removes a {@link SolverGuessListener} to this Solver.
	 * 
	 * @param listener
	 *            Listener to be removed.
	 */
	public void removeSolverGuessListener(SolverGuessListener listener) {
		this.solverGuessListeners.remove(listener);
	}

	/**
	 * Request the interruption of this solver. The Puzzle will have its status
	 * changed to {@link PuzzleStatus#STOPPED}.
	 */
	public void requestStop() {
		this.stopRequested = true;
	}

}
