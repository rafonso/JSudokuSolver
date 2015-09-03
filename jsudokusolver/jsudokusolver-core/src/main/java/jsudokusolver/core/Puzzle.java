package jsudokusolver.core;

import static jsudokusolver.core.Cell.CELL_TO_VALUE_OR_0;
import static jsudokusolver.core.CellFunctions.rangeStream;
import static jsudokusolver.core.CellFunctions.validateRange;
import static jsudokusolver.core.PuzzlePositions.ROW;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
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

	private void clean(Predicate<Cell> predicate, boolean validStatus) {
		assert validStatus : "Invalid Puzzle Status: " + this.status;

		this.getCellsStream() //
				.filter(predicate) //
				.forEach(c -> c.setValueStatus(null, CellStatus.IDLE));
		this.setStatus(PuzzleStatus.WAITING);
	}

	/**
	 * @return This Puzzle Cells as a {@link Stream}
	 */
	Stream<Cell> getCellsStream() {
		return this.cells.stream();
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
	 * Clean all {@link Cell}s, setting these values and status to
	 * {@link Optional#empty() empty} and {@link CellStatus#IDLE}, and sets
	 * Puzzle status to {@link PuzzleStatus#WAITING}.
	 */
	public void cleanCells() {
		this.clean(c -> true, //
				(this.status == PuzzleStatus.INVALID) || (this.status == PuzzleStatus.SOLVED)
						|| (this.status == PuzzleStatus.WAITING));
	}

	/**
	 * Clean all {@link Cell}s that are not {@link CellStatus#ORIGINAL}, setting
	 * these values and status to {@link Optional#empty() empty} and
	 * {@link CellStatus#IDLE}, and sets Puzzle status to
	 * {@link PuzzleStatus#WAITING}. This way, it will be possible to re-run the
	 * solver with the same puzzle.
	 */
	public void reset() {
		this.clean(c -> (c.getStatus() != CellStatus.ORIGINAL), //
				this.status != PuzzleStatus.RUNNING);
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

	public void parse(String str) {
		assert(this.status == PuzzleStatus.WAITING);
		if (!str.matches("^(\\d|.)+$")) {
			throw new IllegalArgumentException("Invalid Puzzle: " + str);
		}
		char[] digits = str.replaceAll("\\.", "").toCharArray();
		if (digits.length != 81) {
			throw new IllegalArgumentException("Puzzle should have 81 digits! " + str);
		}

		for (int pos = 0; pos < digits.length; pos++) {
			int digit = digits[pos] - '0';
			if (digit > 0) {
				int[] position = Cell.valueToPositions(pos);
				this.getCell(position[0], position[1]).setValueStatus(digit, CellStatus.ORIGINAL);
			}
		}

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
	 * Returs a String representation of this Puzzle as a square of numbers.
	 * 
	 * @return
	 */
	public String formatPuzzle() {
		final Function<? super Integer, ? extends String> formatRow = row -> {
			Integer[] values = this.getCellsStream().filter(PuzzlePositions.ROW.getPositionPredicate(row))
					.map(CELL_TO_VALUE_OR_0).collect(Collectors.toList()).toArray(new Integer[9]);
			return String.format("\u2502%d%d%d\u2502%d%d%d\u2502%d%d%d\u2502%n", values[0], values[1], values[2],
					values[3], values[4], values[5], values[6], values[7], values[8]);
		};
		String[] formatedRows = CellFunctions.rangeStream().map(formatRow).collect(Collectors.toList())
				.toArray(new String[9]);

		return "\u250C\u2500\u2500\u2500\u252C\u2500\u2500\u2500\u252C\u2500\u2500\u2500\u2510\n" //
				+ formatedRows[0] //
				+ formatedRows[1] //
				+ formatedRows[2] //
				+ "\u251C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u2524\n" //
				+ formatedRows[3] //
				+ formatedRows[4] //
				+ formatedRows[5] //
				+ "\u251C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u2524\n" //
				+ formatedRows[6] //
				+ formatedRows[7] //
				+ formatedRows[8] //
				+ "\u2514\u2500\u2500\u2500\u2534\u2500\u2500\u2500\u2534\u2500\u2500\u2500\u2518\n";
	}

	/**
	 * Returns a String representation of this puzzle.
	 */
	@Override
	public String toString() {
		return "[" + this.formatCells() + ", " + this.status + "]";
	}

}
