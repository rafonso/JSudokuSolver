package jsudokusolver.core;

/**
 * Exception that indicates a trying to solve a empty {@link Puzzle}
 */
public class EmptyPuzzleException extends InvalidPuzzleException {

	private static final long serialVersionUID = -200681751472833054L;

	public EmptyPuzzleException() {
		super("Empty Puzzle");
	}

}
