package jsudokusolver.core.exception;

public class NoSolutionPuzzleException extends SudokuException {

	private static final long serialVersionUID = 5664205858430383466L;

	public NoSolutionPuzzleException() {
		super("There is no solution!");
	}

}
