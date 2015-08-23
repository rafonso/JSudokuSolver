package jsudokusolver.core;

/**
 * Indicates a {@link Puzzle}'s Status
 */
public enum PuzzleStatus {
	/**
	 * The Puzzle is waiting to be solved.
	 */
	WAITING, //
	/**
	 * The Puzzle is being validated.
	 */
	VALIDATING, //
	/**
	 * The Puzzle was validated and it is ready to be solved.
	 */
	READY, //
	/**
	 * The Puzzle was validated and it was found some error.
	 */
	INVALID, //
	/**
	 * The Puzzle is being solved.
	 */
	RUNNING, //
	/**
	 * The {@link Solver} was paused.
	 */
	STOPPED, //
	/**
	 * The Puzzle was completly solved.
	 */
	SOLVED //
}
