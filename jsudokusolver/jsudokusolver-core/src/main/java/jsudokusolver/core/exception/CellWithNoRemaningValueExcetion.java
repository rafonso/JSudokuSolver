package jsudokusolver.core.exception;

import jsudokusolver.core.Cell;

public class CellWithNoRemaningValueExcetion extends SudokuException {

	private static final long serialVersionUID = -4492998873540162585L;

	private final Cell cell;

	public CellWithNoRemaningValueExcetion(Cell cell) {
		super(String.format("Cell at row %d, column %d with no remaing value", cell.getRow(), cell.getColumn()));
		this.cell = cell;
	}

	public Cell getCell() {
		return cell;
	}

}
