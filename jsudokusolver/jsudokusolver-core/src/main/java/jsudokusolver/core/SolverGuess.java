package jsudokusolver.core;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a Guess for a {@link Cell} and the other empty Cells when this
 * guess was done.
 */
class SolverGuess {

	private static final Function<int[], String> POS_TO_STRING = (arr -> "[" + arr[0] + arr[1] + "]");

	private final int[] guessPosition;

	private final Deque<Integer> pendentValues = new LinkedList<>();

	private final List<int[]> emptyPositions;

	/**
	 * Constructor.
	 * 
	 * @param guessPosition
	 *            The Row/Column of the Guessing Cell.
	 * @param pendentValues
	 *            The possible values for the Guessing position
	 * @param emptyPositions
	 *            The positions of the empty cells when the Guess was done,
	 *            including the guessPosition.
	 */
	SolverGuess(int[] guessPosition, List<Integer> pendentValues, List<int[]> emptyPositions) {
		this.guessPosition = guessPosition;
		this.pendentValues.addAll(pendentValues);
		this.emptyPositions = emptyPositions;
	}

	/**
	 * @return The Row/Column of the Guessing Cell.
	 */
	public int[] getGuessPosition() {
		return guessPosition;
	}

	/**
	 * @return The positions of the empty cells when the Guess was done,
	 *         including the {@link #getGuessPosition()}.
	 */
	public List<int[]> getEmptyPositions() {
		return emptyPositions;
	}

	/**
	 * @return The possible values for the Guessing position
	 */
	public Deque<Integer> getPendentValues() {
		return pendentValues;
	}

	/**
	 * Discards the current possible value.
	 * 
	 * @return the current possible value just discarded.
	 */
	public Integer discardCurrentGuessValue() {
		return this.pendentValues.pop();
	}

	/**
	 * @return the current possible value.
	 */
	public Integer getCurrentGuessValue() {
		return this.pendentValues.peek();
	}

	/**
	 * @return <code>true</code> if there is no more possible values for
	 *         {@link #getGuessPosition() Guess Position}.
	 */
	public boolean isEmpty() {
		return this.pendentValues.isEmpty();
	}

	@Override
	public String toString() {
		return "SolverGuess [" //
				+ "guessPosition=" + POS_TO_STRING.apply(guessPosition) //
				+ ", pendentValues=" + pendentValues //
				+ ", emptyPositions=" + emptyPositions.stream().map(POS_TO_STRING).collect(Collectors.toList()) //
				+ "]";
	}

}
