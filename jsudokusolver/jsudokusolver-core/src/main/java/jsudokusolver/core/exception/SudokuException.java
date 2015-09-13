package jsudokusolver.core.exception;

import jsudokusolver.core.Puzzle;

/**
 * Indicates some kind of error while solving or validating a {@link Puzzle}
 */
public class SudokuException extends RuntimeException {

	private static final long serialVersionUID = -7474006112414026951L;

	public SudokuException() {
		super();
	}

	public SudokuException(String msg) {
		super(msg);
	}

}
