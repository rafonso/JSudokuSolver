package jsudokusolver.core;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class SolverGuess {

	private static final Function<int[], String> POS_TO_STRING = (arr -> "[" + arr[0] + arr[1] + "]");

	private final int[] guessPosition;

	private final Deque<Integer> pendentValues = new LinkedList<>();

	private final List<int[]> emptyPositions;

	SolverGuess(int[] guessPosition, List<Integer> pendentValues, List<int[]> emptyPositions) {
		this.guessPosition = guessPosition;
		this.pendentValues.addAll(pendentValues);
		this.emptyPositions = emptyPositions;
	}

	public int[] getGuessPosition() {
		return guessPosition;
	}

	public List<int[]> getEmptyPositions() {
		return emptyPositions;
	}

	public Deque<Integer> getPendentValues() {
		return pendentValues;
	}

	public Integer discardCurrentGuessValue() {
		return this.pendentValues.pop();
	}

	public Integer getCurrentGuessValue() {
		return this.pendentValues.peek();
	}

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
