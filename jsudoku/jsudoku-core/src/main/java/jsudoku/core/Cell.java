package jsudoku.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Optional;

public class Cell {

	public static final Optional<Integer> NO_VALUE = Optional.empty();

	public static final String CELL_VALUE = "Cell.Value";

	public static final String CELL_STATUS = "CEll.Status";

	private final int row;

	private final int column;

	private final int sector;

	private Optional<Integer> value;

	private CellStatus status;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

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

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public int getSector() {
		return sector;
	}

	public Optional<Integer> getValue() {
		return value;
	}

	public void setValue(Optional<Integer> value) {
		assert value != null;
		if (value.isPresent()) {
			CellFunctions.validateRange(value.get(), "Cell Value");
		}

		Optional<Integer> old = this.value;
		this.value = value;
		this.pcs.firePropertyChange(CELL_VALUE, old, value);
	}

	public void setValue(int value) {
		this.setValue(Optional.of(value));
	}

	public CellStatus getStatus() {
		return status;
	}

	public void setStatus(CellStatus status) {
		assert status != null;

		CellStatus old = this.status;
		this.status = status;
		this.pcs.firePropertyChange(CELL_STATUS, old, status);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + row;
		return result;
	}

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

	@Override
	public String toString() {
		return "[" + row + column + (value.isPresent() ? value.get() : 0) + status.getCode() + "]";
	}

}
