package jsudokusolver.core;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static jsudokusolver.core.CellFunctions.CELL_TO_VALUE;
import static jsudokusolver.core.CellStatus.EVALUATING;
import static jsudokusolver.core.CellStatus.FILLED;
import static jsudokusolver.core.CellStatus.IDLE;
import static jsudokusolver.core.PuzzlePositions.COLUMN;
import static jsudokusolver.core.PuzzlePositions.ROW;
import static jsudokusolver.core.PuzzlePositions.SECTOR;
import static jsudokusolver.core.PuzzleStatus.READY;
import static jsudokusolver.core.PuzzleStatus.RUNNING;
import static jsudokusolver.core.PuzzleStatus.SOLVED;
import static jsudokusolver.core.PuzzleStatus.INVALID;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class Solver {

	public static final String SOLVER_CYCLE = "Solver.cycle";

	private int cycle = 0;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private final Deque<SolverMemento> memento = new LinkedList<>();

	public Solver() {
		// TODO Auto-generated constructor stub
	}

	private void incrementCycle() {
		this.cycle++;
		this.pcs.firePropertyChange(SOLVER_CYCLE, this.cycle - 1, this.cycle);
	}

	public void solve(Puzzle puzzle) {
		List<Cell> emptyCells = getEmptyCells(puzzle);
		int quantOfEmptyCells = emptyCells.size();
		while (quantOfEmptyCells > 0) {
			this.incrementCycle();
			emptyCells.forEach(c -> this.solveCell(c, puzzle));
			emptyCells = getEmptyCells(puzzle);
			if (emptyCells.size() < quantOfEmptyCells) {
				quantOfEmptyCells = emptyCells.size();
			} else {
				puzzle.setStatus(INVALID);
				throw new InvalidPuzzleException("Guesses not implemented yet!");
			}
		}
	}

	private Predicate<Integer> getCellValuePredicate(Puzzle puzzle, int pos, PuzzlePositions puzzlePositions) {
		return puzzle.getCellsStream() //
				.filter(puzzlePositions.getPositionPredicate(pos)) //
				.filter(Cell::hasValue) //
				.map(CELL_TO_VALUE) //
				.collect(toSet()) //
				::contains;
	}

	private void solveCell(Cell c, Puzzle puzzle) {
		c.setStatus(EVALUATING);

		List<Integer> possibleValues = CellFunctions.rangeStream() //
				.filter(this.getCellValuePredicate(puzzle, c.getRow(), ROW).negate())
				.filter(this.getCellValuePredicate(puzzle, c.getColumn(), COLUMN).negate())
				.filter(this.getCellValuePredicate(puzzle, c.getSector(), SECTOR).negate()) //
				.collect(toList());

		if (possibleValues.isEmpty()) {
			puzzle.setStatus(INVALID);
			throw new InvalidPuzzleException("Cell " + c + " without no remaing value");
		}
		if (possibleValues.size() == 1) {
			c.setValue(possibleValues.get(0));
			c.setStatus(FILLED);
		} else {
			c.setStatus(IDLE);
		}
	}

	private List<Cell> getEmptyCells(Puzzle puzzle) {
		return puzzle.getCellsStream().filter(c -> !c.getValue().isPresent()).collect(toList());
	}

	public void start(Puzzle puzzle) {
		assert puzzle.getStatus() == READY : "Puzzle is not ready to be solved";

		puzzle.setStatus(RUNNING);
		this.solve(puzzle);
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

}
