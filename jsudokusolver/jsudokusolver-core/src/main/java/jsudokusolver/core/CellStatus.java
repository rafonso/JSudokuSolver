package jsudokusolver.core;

/**
 * Indicates the {@link Cell}'s Status.
 */
public enum CellStatus {

	/**
	 * The Cell is empty and wating to be filled.
	 */
	IDLE('I'), //
	/**
	 * The Cell was filled with a value <b>before</b> the {@link Solver} is
	 * running.
	 */
	ORIGINAL('O'), //
	/**
	 * The Cell was filled with a value <b>while</b> the {@link Solver} is
	 * running.
	 */
	FILLED('F'), //
	/**
	 * The Cell is empty and is being evaluated by the {@link Solver}.
	 */
	EVALUATING('E'), //
	/**
	 * The Cell was filled with a guess value by the {@link Solver}.
	 */
	GUESSING('G'), //
	/**
	 * The Cell presents some type of irregularity.
	 */
	ERROR('X');

	private final char code;

	private CellStatus(char code) {
		this.code = code;
	}

	public char getCode() {
		return code;
	}

}
