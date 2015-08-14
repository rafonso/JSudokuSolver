package jsudoku.core;

public class RepeatedCellsException extends InvalidPuzzleException {

	private static final long serialVersionUID = -6239853384721694158L;

	private final PuzzlePositions puzzlePositions;

	private final int position;

	private final int repeatedValue;

	private final Cell cell1;

	private final Cell cell2;

	public RepeatedCellsException(PuzzlePositions puzzlePositions, int position, Cell cell1, Cell cell2) {
		super();
		this.puzzlePositions = puzzlePositions;
		this.position = position;
		this.repeatedValue = cell1.getValue().get();
		this.cell1 = cell1;
		this.cell2 = cell2;
	}

	public PuzzlePositions getPuzzlePositions() {
		return puzzlePositions;
	}

	public int getPosition() {
		return position;
	}

	public int getRepeatedValue() {
		return repeatedValue;
	}

	public Cell getCell1() {
		return cell1;
	}

	public Cell getCell2() {
		return cell2;
	}

	@Override
	public String getMessage() {
		return String.format("Repeated cell value (%d) at %s %d: %s and %s", repeatedValue,
				puzzlePositions.getDescription(), position, cell1, cell2);
	}
}
