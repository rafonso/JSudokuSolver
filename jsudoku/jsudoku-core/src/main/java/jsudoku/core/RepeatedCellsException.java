package jsudoku.core;

public class RepeatedCellsException extends InvalidPuzzleException {

	private static final long serialVersionUID = -6239853384721694158L;

	private final String type;

	private final int position;

	private final Cell cell1;

	private final Cell cell2;

	public RepeatedCellsException(String type, int position, Cell cell1, Cell cell2) {
		super();
		this.type = type;
		this.position = position;
		this.cell1 = cell1;
		this.cell2 = cell2;
	}

	public String getType() {
		return type;
	}

	public int getPosition() {
		return position;
	}

	public Cell getCell1() {
		return cell1;
	}

	public Cell getCell2() {
		return cell2;
	}

	@Override
	public String getMessage() {
		return String.format("Repeated cells at %s %d: %s and %s", type, position, cell1, cell2);
	}
}
