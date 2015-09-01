package jsudokusolver.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a Sudoku Puzzle Cell. It contains a {@link #getRow() Row}, a
 * {@link #getColumn() Column}, a {@link #getValue() Value} and a
 * {@link #getStatus() Status}. When the Value and/or Status are changed, it
 * fires a {@link PropertyChangeEvent} which can be listened by the appropriate
 * {@link PropertyChangeListener Listeners}.
 */
public class Cell implements Cloneable {

	private static Function<Cell, Optional<Integer>> CELL_TO_OPT_VALUE = Cell::getValue;

	/**
	 * Given a {@link Cell}, returns its Integer {@link Cell#getValue() Value}.
	 * It can throws a {@link NoSuchElementException}, if the Cell is empty.
	 */
	static final Function<Cell, Integer> CELL_TO_VALUE = CELL_TO_OPT_VALUE.andThen(Optional::get);

	/**
	 * Given a {@link Cell}, returns its Integer {@link Cell#getValue() Value}
	 * or 0 if empty.
	 */
	static final Function<Cell, Integer> CELL_TO_VALUE_OR_0 = CELL_TO_OPT_VALUE.andThen(o -> o.orElse(0));

	/**
	 * Indicate a no-value for a Cell.
	 * 
	 * @see Optional#empty()
	 */
	public static final Optional<Integer> NO_VALUE = Optional.empty();

	/**
	 * Indicates that a Cell {@link #getValue() Value} was changed.
	 */
	public static final String CELL_VALUE = "Cell.Value";

	/**
	 * Indicates that a Cell {@link #getStatus() Status} was changed.
	 */
	public static final String CELL_STATUS = "Cell.Status";

	private final int row;

	private final int column;

	private final int sector;

	private Optional<Integer> value;

	private CellStatus status;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Constructor. The {@link #getSector() Sector} is calculated from Row and
	 * Column. The initial value will be a {@link #NO_VALUE}. The inital
	 * {@link #getStatus() Status} will be {@link CellStatus#IDLE}.
	 * 
	 * @param row
	 *            The Cell Row.
	 * @param column
	 *            The Cell Column.
	 */
	public Cell(int row, int column) {
		CellFunctions.validateRange(row, "Row");
		CellFunctions.validateRange(column, "Column");

		this.row = row;
		this.column = column;
		this.sector = ((row > 6) ? 6 : ((row > 3) ? 3 : 0)) //
				+ ((column > 6) ? 3 : ((column > 3) ? 2 : 1));
		this.value = NO_VALUE;
		this.status = CellStatus.IDLE;
	}

	/**
	 * @return Cell's Row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @return Cell's Column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * 
	 * @return The Cell's sector, calculed from the row and column.
	 */
	public int getSector() {
		return sector;
	}

	/**
	 * @return The Cell value. When empty, it returns a {@link #NO_VALUE}.
	 */
	public Optional<Integer> getValue() {
		return value;
	}

	/**
	 * Change this Cell's Value. It fires a {@link PropertyChangeEvent} of type
	 * {@link #CELL_VALUE}.
	 * 
	 * @param value
	 *            the new Cell value.
	 */
	public void setValue(Optional<Integer> value) {
		assert value != null;
		if (value.isPresent()) {
			CellFunctions.validateRange(value.get(), "Cell Value");
		}

		Optional<Integer> old = this.value;
		this.value = value;
		this.pcs.firePropertyChange(CELL_VALUE, old, value);
	}

	/**
	 * Sets the new Cell value.
	 * 
	 * @param value
	 *            The new Cell value. It can be a <code>null</code>.
	 */
	public void setValue(int value) {
		this.setValue(Optional.of(value));
	}

	/**
	 * @return The current Cell's Status.
	 */
	public CellStatus getStatus() {
		return status;
	}

	/**
	 * Change this Cell's Status. It fires a {@link PropertyChangeEvent} of type
	 * {@link #CELL_STATUS}.
	 * 
	 * @param status
	 *            The new Cell's Status
	 */
	public void setStatus(CellStatus status) {
		assert status != null;

		CellStatus old = this.status;
		this.status = status;
		this.pcs.firePropertyChange(CELL_STATUS, old, status);
	}

	/**
	 * Change both Value and Status of this Cell.
	 * 
	 * @param value
	 *            The new Cell value. It can be a <code>null</code>.
	 * @param status
	 *            The new Cell's Status
	 */
	public void setValueStatus(Integer value, CellStatus status) {
		this.setValue(Optional.ofNullable(value));
		this.setStatus(status);
	}

	/**
	 * 
	 * @return <code>true</code> if this cell contains a valid value (from 1 to
	 *         9), <code>false</code> otherwise.
	 */
	public boolean hasValue() {
		return this.value.isPresent();
	}

	/**
	 * Adds a apropriate {@link PropertyChangeListener Listener} to this Cell.
	 * 
	 * @param listener
	 *            Listener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a {@link PropertyChangeListener Listener} to this Cell.
	 * 
	 * @param listener
	 *            Listener to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Return this Cell's hash code, based on this {@link #getRow() Row} and
	 * {@link #getColumn() Column}.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + row;
		return result;
	}

	/**
	 * Compares this Cell with another based on this {@link #getRow() Row} and
	 * {@link #getColumn() Column}.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (row != other.row)
			return false;
		return true;
	}

	/**
	 * Returns a String representation of this Cell. This String will be in the
	 * form <code>[RCVS]</code>, where :
	 * <ul>
	 * <li>R represents the {@link #getRow() Row}</li>
	 * <li>C represents the {@link #getColumn() Column}</li>
	 * <li>V represents the {@link #getValue() Value}; If empty it will be 0.
	 * </li>
	 * <li>S represents the {@link #getStatus() Status}'s first letter</li>
	 * </ul>
	 */
	@Override
	public String toString() {
		return "[" + row + column + (value.isPresent() ? value.get() : 0) + status.getCode() + "]";
	}

	/**
	 * Creates a new Cell, with the same Row and Column than this one.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Cell(row, column);
	}

	/**
	 * Given a number from 0 to 80, it returns the equivalent {@link #getRow()
	 * Row} and {@link #getColumn() Column} in Puzzle.
	 * 
	 * @param value
	 *            The number
	 * @return The equivalent Row and Column in Puzzle.
	 */
	public static int[] valueToPositions(int value) {
		assert(value >= 0) && (value < 81);

		return new int[] { (value / 9) + 1, (value % 9) + 1 };
	}

}
