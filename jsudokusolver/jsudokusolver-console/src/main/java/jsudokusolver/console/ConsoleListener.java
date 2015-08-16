package jsudokusolver.console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jsudokusolver.core.Cell;
import jsudokusolver.core.CellStatus;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;
import jsudokusolver.core.Solver;

public class ConsoleListener implements PropertyChangeListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public ConsoleListener() {
	}

	private void cellValueChanged(Cell cell, Optional<Integer> from, Optional<Integer> to) {
		if (log.isTraceEnabled()) {
			String msg;
			if (from.isPresent() && to.isPresent()) {
				msg = "Changing from " + from.get() + " to " + to.get();
			} else if (!from.isPresent() && to.isPresent()) {
				msg = "Filling with " + to.get();
			} else if (from.isPresent() && !to.isPresent()) {
				msg = "Removing " + from.get();
			} else if (from.isPresent() && !to.isPresent()) {
				msg = "Changing from " + from.get() + " to " + to.get();
			} else {
				msg = "Maintaining empty";
			}

			log.trace("Cell({},{}): {}", cell.getRow(), cell.getColumn(), msg);

		}
	}

	private void cellStatusChanged(Cell cell, CellStatus from, CellStatus to) {
		if (log.isTraceEnabled()) {
			log.trace("Cell({},{}): changing status from {} to {}", cell.getRow(), cell.getColumn(), from, to);
		}
	}

	private void puzzleStatusChanged(Puzzle source, PuzzleStatus from, PuzzleStatus to) {
		if (log.isTraceEnabled()) {
			log.trace("Puzzle   : changing status from {} to {}", from, to);
		}
	}

	private void solverCycleChanged(Integer newValue) {
		log.debug("Cycle {}", newValue);
	}

	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case Cell.CELL_VALUE:
			this.cellValueChanged((Cell) evt.getSource(), (Optional<Integer>) evt.getOldValue(),
					(Optional<Integer>) evt.getNewValue());
			break;
		case Cell.CELL_STATUS:
			this.cellStatusChanged((Cell) evt.getSource(), (CellStatus) evt.getOldValue(),
					(CellStatus) evt.getNewValue());
			break;
		case Puzzle.PUZZLE_STATUS:
			this.puzzleStatusChanged((Puzzle) evt.getSource(), (PuzzleStatus) evt.getOldValue(),
					(PuzzleStatus) evt.getNewValue());
			break;
		case Solver.SOLVER_CYCLE:
			this.solverCycleChanged((Integer) evt.getNewValue());
			break;
		default:
			throw new IllegalStateException("Irrregular change type: " + evt.getPropertyName() + ". Event: " + evt);
		}

	}

}
