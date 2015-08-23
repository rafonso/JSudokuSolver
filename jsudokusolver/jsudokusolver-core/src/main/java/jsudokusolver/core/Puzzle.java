package jsudokusolver.core;

import static jsudokusolver.core.CellFunctions.CELL_TO_VALUE_OR_0;
import static jsudokusolver.core.CellFunctions.rangeStream;
import static jsudokusolver.core.CellFunctions.validateRange;
import static jsudokusolver.core.PuzzlePositions.ROW;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a Sudoku Puzzle, with its respectives {@link Cell}s.
 */
public class Puzzle {

	/**
	 * Indicates that a Puzzle Status was changed.
	 */
	public static final String PUZZLE_STATUS = "Puzzle.Status";

	private final Function<? super Integer, ? extends String> rowToString = row -> this.getCellsStream()
			.filter(ROW.getPositionPredicate(row)) //
			.map(CELL_TO_VALUE_OR_0) //
			.map(String::valueOf) //
			.collect(Collectors.joining());

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private final List<Cell> cells;

	private PuzzleStatus status;

	/**
	 * Constructor. It creates the Puzzle's Cell and alse sets the initial
	 * Status as {@link PuzzleStatus#WAITING}.
	 */
	public Puzzle() {
		final Function<Integer, Stream<Cell>> rowToCells = r -> rangeStream().map(c -> new Cell(r, c));
		final Stream<Cell> cellsStream = rangeStream().flatMap(rowToCells);

		this.cells = Collections.unmodifiableList(cellsStream.collect(Collectors.toList()));
		this.status = PuzzleStatus.WAITING;
	}

	/**
	 * Returns a {@link Cell} located in a Row and Column.
	 * 
	 * @param row
	 *            Requested Row
	 * @param col
	 *            Requested Column
	 * @return Cell in the requested Row and Column,
	 */
	public Cell getCell(int row, int col) {
		validateRange(row, "Row");
		validateRange(col, "Column");

		return cells.get((9 * (row - 1)) + (col - 1));
	}

	/**
	 * REturns the current Puzzle Status.
	 * 
	 * @return the current Puzzle Status.
	 */
	public PuzzleStatus getStatus() {
		return status;
	}

	/**
	 * Sets a new Puzzle Status. It fires a {@link PropertyChangeEvent} of type
	 * {@link #PUZZLE_STATUS}.
	 * 
	 * @param status
	 *            the new Puzzle Status.
	 */
	public void setStatus(PuzzleStatus status) {
		assert status != null : "Puzzle Status Null";

		PuzzleStatus old = this.status;
		this.status = status;
		this.pcs.firePropertyChange(PUZZLE_STATUS, old, status);
	}

	/**
	 * @return This Puzzle Cells.
	 */
	public List<Cell> getCells() {
		return cells;
	}

	/**
	 * @return This Puzzle Cells as a {@link Stream}
	 */
	public Stream<Cell> getCellsStream() {
		return this.getCells().stream();
	}

	/**
	 * Adds a apropriate {@link PropertyChangeListener Listener} to this Puzzle.
	 * 
	 * @param listener
	 *            Listener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a {@link PropertyChangeListener Listener} to this Puzzle.
	 * 
	 * @param listener
	 *            Listener to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Returns a String with the Cell's values. This String will be in the
	 * format
	 * <code>NNNNNNNNN.NNNNNNNNN.NNNNNNNNN.NNNNNNNNN.NNNNNNNNN.NNNNNNNNN.NNNNNNNNN.NNNNNNNNN.NNNNNNNNN</code>
	 * , where N is a value from 1 to 9 or 0 if the Cell is empty.
	 * 
	 * @return String with the Cell's values.
	 */
	public String formatCells() {
		return rangeStream().map(rowToString).collect(Collectors.joining("."));
	}

	/**
	 * Returns a String representation of this puzzle.
	 */
	@Override
	public String toString() {
		return "[" + this.formatCells() + ", " + this.status + "]";
	}

}
