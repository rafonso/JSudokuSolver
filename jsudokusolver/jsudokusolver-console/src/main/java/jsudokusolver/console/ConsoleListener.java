package jsudokusolver.console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jsudokusolver.core.Cell;
import jsudokusolver.core.CellStatus;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;
import jsudokusolver.core.Solver;
import jsudokusolver.core.SolverGuessEvent;
import jsudokusolver.core.SolverGuessEvent.SolverGuessEventType;
import jsudokusolver.core.SolverGuessListener;

public class ConsoleListener implements PropertyChangeListener, SolverGuessListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Deque<Cell> guessingCells = new ArrayDeque<>();
	
	private PuzzleStatus puzzleStatus; 

	public ConsoleListener() {
	}

	private void cellValueChanged(Cell cell, Optional<Integer> from, Optional<Integer> to) {
		if (log.isTraceEnabled() && (puzzleStatus == PuzzleStatus.RUNNING)) {
			if (from.isPresent() && to.isPresent()) {
				log.trace("Cell({},{}): Changing from {} to {}", cell.getRow(), cell.getColumn(), from.get(), to.get());
			} else if (!from.isPresent() && to.isPresent()) {
				log.trace("Cell({},{}): Filling with {}", cell.getRow(), cell.getColumn(), to.get());
			} else if (from.isPresent() && !to.isPresent()) {
//				msg = "Removing " + from.get();
			} else {
				log.trace("Maintaining empty");
			}
		}
	}

	private void cellStatusChanged(Cell cell, CellStatus from, CellStatus to) {
		if (log.isTraceEnabled()) {
			log.trace("Cell({},{}): changing status from {} to {}", cell.getRow(), cell.getColumn(), from, to);
		}
	}

	private void puzzleStatusChanged(Puzzle source, PuzzleStatus from, PuzzleStatus to) {
		this.puzzleStatus = to;
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
//			this.cellStatusChanged((Cell) evt.getSource(), (CellStatus) evt.getOldValue(),
//					(CellStatus) evt.getNewValue());
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

	@Override
	public void guessAction(SolverGuessEvent event) {
		if (event.getType() == SolverGuessEventType.ADDITION) {
			this.guessingCells.addLast(event.getGuessCell());
		} else {
			this.guessingCells.removeLast();
		}
		log.debug("{} Guess Cell {}. Guesses: {}", event.getType(), event.getGuessCell(), this.guessingCells);
	}

}
