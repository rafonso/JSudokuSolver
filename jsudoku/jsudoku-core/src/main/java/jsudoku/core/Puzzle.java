package jsudoku.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Puzzle {

	public static final String PUZZLE_STATUS = "Puzzle.Status";

	private static void validateRange(Integer i, String description) {
		assert(i > 0) && (i < 10) : "Invalid " + description + ": " + i;
	}

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private final List<Cell> cells;

	private PuzzleStatus status;

	public Puzzle() {
		List<Cell> cells = new ArrayList<>(81);
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				cells.add(new Cell(r + 1, c + 1));
			}
		}
		this.cells = Collections.unmodifiableList(cells);

		this.status = PuzzleStatus.WAITING;
	}

	public Cell getCell(int row, int col) {
		validateRange(row, "Row");
		validateRange(col, "Column");

		return cells.get((9 * (row - 1)) + (col - 1));
	}

	public PuzzleStatus getStatus() {
		return status;
	}

	public void setStatus(PuzzleStatus status) {
		assert status != null : "Puzzle Status Null";

		PuzzleStatus old = this.status;
		this.status = status;
		this.pcs.firePropertyChange(PUZZLE_STATUS, old, status);
	}

	public List<Cell> getCells() {
		return cells;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

}
