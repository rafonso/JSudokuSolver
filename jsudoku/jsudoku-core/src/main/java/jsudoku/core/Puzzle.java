package jsudoku.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Puzzle {

	public static final String PUZZLE_STATUS = "Puzzle.Status";

	private final Function<? super Integer, ? extends String> rowToString = row -> this.getCellsStream()
			.filter(CellFunctions.getRowPredicate(row)) //
			.map(CellFunctions.CELL_TO_VALUE_OR_0) //
			.map(String::valueOf) //
			.collect(Collectors.joining());

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private final List<Cell> cells;

	private PuzzleStatus status;

	public Puzzle() {
		final Function<? super Integer, ? extends Stream<? extends Cell>> rowToCells = r -> CellFunctions.rangeStream()
				.map(c -> new Cell(r, c));
		final Stream<Cell> cellsStream = CellFunctions.rangeStream().flatMap(rowToCells);

		this.cells = Collections.unmodifiableList(cellsStream.collect(Collectors.toList()));
		this.status = PuzzleStatus.WAITING;
	}

	public Cell getCell(int row, int col) {
		CellFunctions.validateRange(row, "Row");
		CellFunctions.validateRange(col, "Column");

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

	public Stream<Cell> getCellsStream() {
		return this.getCells().stream();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	public String formatCells() {
		return CellFunctions.rangeStream().map(rowToString).collect(Collectors.joining("."));
	}

}
