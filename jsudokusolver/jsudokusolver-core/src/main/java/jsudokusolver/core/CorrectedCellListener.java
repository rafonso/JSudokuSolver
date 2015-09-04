package jsudokusolver.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Predicate;

/**
 * It listens when a Cell with {@link CellStatus#ERROR} {@link Cell#getStatus()
 * Status} is cleaned (Status changed to {@link CellStatus#IDLE}) and then
 * reaches by the other ERROR-ed Cell to change its Status to
 * {@link CellStatus#ORIGINAL}.
 */
public class CorrectedCellListener implements PropertyChangeListener {

	private final Puzzle puzzle;

	private final Predicate<Cell> cellIsError = (c -> c.getStatus().equals(CellStatus.ERROR));

	public CorrectedCellListener(Puzzle puzzle) {
		this.puzzle = puzzle;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		final boolean cellStatusChanged = evt.getPropertyName().equals(Cell.CELL_STATUS);
		final boolean fromError = evt.getOldValue().equals(CellStatus.ERROR);
		final boolean toIdle = evt.getNewValue().equals(CellStatus.IDLE);

		if (cellStatusChanged && fromError && toIdle) {
			this.puzzle.getCellsStream() //
					.filter(cellIsError.and(c -> c != evt.getSource()).and(Cell::hasValue)) //
					.findFirst() //
					.ifPresent(c -> c.setStatus(CellStatus.ORIGINAL));
		}

	}

}
