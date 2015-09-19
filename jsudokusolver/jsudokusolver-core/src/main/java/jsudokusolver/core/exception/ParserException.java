package jsudokusolver.core.exception;

public class ParserException extends SudokuException {

	private static final long serialVersionUID = -8405412023792868277L;

	private final String irregularPuzzle;

	public ParserException(String irregularPuzzle) {
		this.irregularPuzzle = irregularPuzzle;
	}

	public String getIrregularPuzzle() {
		return irregularPuzzle;
	}

}
