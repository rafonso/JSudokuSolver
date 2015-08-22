package jsudokusolver.core;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static jsudokusolver.core.CellFunctions.CELL_TO_VALUE;
import static jsudokusolver.core.CellFunctions.rangeStream;
import static jsudokusolver.core.CellStatus.EVALUATING;
import static jsudokusolver.core.CellStatus.FILLED;
import static jsudokusolver.core.CellStatus.GUESSING;
import static jsudokusolver.core.CellStatus.IDLE;
import static jsudokusolver.core.PuzzlePositions.COLUMN;
import static jsudokusolver.core.PuzzlePositions.ROW;
import static jsudokusolver.core.PuzzlePositions.SECTOR;
import static jsudokusolver.core.PuzzleStatus.INVALID;
import static jsudokusolver.core.PuzzleStatus.READY;
import static jsudokusolver.core.PuzzleStatus.RUNNING;
import static jsudokusolver.core.PuzzleStatus.SOLVED;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import jsudokusolver.core.SolverGuessEvent.SolverGuessEventType;

public class Solver {

	public static final String SOLVER_CYCLE = "Solver.cycle";

	private int cycle = 0;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private final List<SolverGuessListener> solverGuessListeners = new ArrayList<>();

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
			puzzle.setStatus(INVALID);
			throw new InvalidPuzzleException("Cell " + c + " without no remaing value");
		} else if (possibleValues.size() == 1) {
			c.setValue(possibleValues.get(0));
			c.setStatus(FILLED);
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
		}
		SolverGuess solverGuess = memento.peek();
		if (solverGuess == null) {
			puzzle.setStatus(INVALID);
			throw new InvalidPuzzleException("There is no solution!");
		}
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

		if (!possibleValues.isEmpty()) {
			final Function<? super Cell, ? extends int[]> cellToPosition = c -> new int[] { c.getRow(), c.getColumn() };
			List<int[]> emptyPositions = emptyCells.stream().map(cellToPosition).collect(toList());
			memento.addFirst(new SolverGuess(cellToPosition.apply(guessCell), possibleValues, emptyPositions));
		} else if (!memento.isEmpty()) {
			for (SolverGuess solverGuess = removeInvalidGuess(puzzle, memento, false); //
			solverGuess.isEmpty(); //
			solverGuess = removeInvalidGuess(puzzle, memento, true)) {
				;
			}
		} else {
			puzzle.setStatus(INVALID);
			throw new InvalidPuzzleException("There is no solution!");
		}

		fillGuess(memento.peek(), puzzle);
	}

	private List<Cell> solveCycle(Puzzle puzzle, final Deque<SolverGuess> guesses, List<Cell> emptyCells) {
		this.incrementCycle();
		emptyCells.forEach(c -> this.solveCell(c, puzzle, guesses.isEmpty()));

		List<Cell> nextEmptyCells = this.getEmptyCells(puzzle);
		if (nextEmptyCells.size() == emptyCells.size()) {
			this.prepareGuess(puzzle, nextEmptyCells, guesses);
			nextEmptyCells = this.getEmptyCells(puzzle);
		}
		return nextEmptyCells;
	}

	public void start(Puzzle puzzle) {
		assert puzzle.getStatus() == READY : "Puzzle is not ready to be solved. Current status: " + puzzle.getStatus();

		final Deque<SolverGuess> guesses = new LinkedList<>();
		List<Cell> emptyCells = this.getEmptyCells(puzzle);

		puzzle.setStatus(RUNNING);
		// Main loop
		while (!emptyCells.isEmpty()) {
			emptyCells = this.solveCycle(puzzle, guesses, emptyCells);
		}
		puzzle.setStatus(SOLVED);
	}

	public int getCycle() {
		return cycle;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	public void addSolverGuessListener(SolverGuessListener listener) {
		this.solverGuessListeners.add(listener);
	}

	public void removeSolverGuessListener(SolverGuessListener listener) {
		this.solverGuessListeners.remove(listener);
	}

}
