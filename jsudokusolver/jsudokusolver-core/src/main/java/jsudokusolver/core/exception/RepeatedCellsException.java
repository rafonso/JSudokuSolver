package jsudokusolver.core.exception;

import jsudokusolver.core.Cell;
import jsudokusolver.core.PuzzlePositions;

/**
 * It indicates that it was found a repeated value in a position
 * (Row/Column/Sector) during the Validation.
 */
public class RepeatedCellsException extends SudokuException {

	private static final long serialVersionUID = -6239853384721694158L;

	private final PuzzlePositions puzzlePositions;

	private final int position;

	private final int repeatedValue;

	private final Cell cell1;

	private final Cell cell2;

	/**
	 * Constructor.
	 * 
	 * @param puzzlePositions
	 *            If it was a Row or Column or Sector
	 * @param position
	 *            the Position where the repetition happened.
	 * @param cell1
	 *            First cell with the repeated value.
	 * @param cell2
	 *            Second cell with the repeated value.
	 */
	public RepeatedCellsException(PuzzlePositions puzzlePositions, int position, Cell cell1, Cell cell2) {
		super();
		this.puzzlePositions = puzzlePositions;
		this.position = position;
		this.repeatedValue = cell1.getValue().get();
		this.cell1 = cell1;
		this.cell2 = cell2;
	}

	/**
	 * @return If it was a Row or Column or Sector
	 */
	public PuzzlePositions getPuzzlePositions() {
		return puzzlePositions;
	}

	/**
	 * @return the Position where the repetition happened.
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @return the repeated value.
	 */
	public int getRepeatedValue() {
		return repeatedValue;
	}

	/**
	 * @return First cell with the repeated value.
	 */
	public Cell getCell1() {
		return cell1;
	}

	/**
	 * @return Second cell with the repeated value.
	 */
	public Cell getCell2() {
		return cell2;
	}

	@Override
	public String getMessage() {
		return String.format("Repeated cell value (%d) at %s %d: %s and %s", repeatedValue,
				puzzlePositions.getDescription(), position, cell1, cell2);
	}
}
