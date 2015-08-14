package jsudoku.core;

public class EmptyPuzzleException extends InvalidPuzzleException {

	private static final long serialVersionUID = -200681751472833054L;

	public EmptyPuzzleException() {
		super("Empty Puzzle");
	}

}
