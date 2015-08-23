package jsudokusolver.core;

import java.util.EventObject;

/**
 * Indicates that a Guess was created or reverted.
 */
public class SolverGuessEvent extends EventObject {

	private static final long serialVersionUID = -7794076992199163136L;

	/**
	 * the type of {@link SolverGuessEvent}
	 */
	public enum SolverGuessEventType {
		/**
		 * A Guess was added.
		 */
		ADDITION, //
		/**
		 * A Guess was reverted.
		 */
		REMOVAL
	}

	private final SolverGuessEventType type;

	private final Cell guessCell;

	/**
	 * Constructor.
	 * 
	 * @param source
	 *            The {@link Solver} where the guess was done.
	 * @param type
	 *            Type of Guess
	 * @param guessCell
	 *            The Guessing {@link Cell}.
	 */
	public SolverGuessEvent(Object source, SolverGuessEventType type, Cell guessCell) {
		super(source);
		this.type = type;
		this.guessCell = guessCell;
	}

	/**
	 * @return Type of Guess
	 */
	public SolverGuessEventType getType() {
		return type;
	}

	/**
	 * @return The Guessing {@link Cell}.
	 */
	public Cell getGuessCell() {
		return guessCell;
	}

}
