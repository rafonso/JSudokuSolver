package jsudokusolver.core;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Utility Functions.
 */
public class CellFunctions {

	/**
	 * Evaluate if a integer value is betwwen 1 and 9. This method is just
	 * effective if the <code>-ea</code> (enable asserts) JVM argument is used.
	 * 
	 * @param i
	 *            Number to be validated.
	 * @param description
	 */
	static void validateRange(Integer i, String description) {
		assert(i > 0) && (i < 10) : "Invalid " + description + ": " + i;
	}

	/**
	 * Given a {@link Cell}, returns its Integer {@link Cell#getValue() Value}.
	 * It can throws a {@link NoSuchElementException}, if the Cell is empty.
	 */
	public static final Function<Cell, Integer> CELL_TO_VALUE = (c -> c.getValue().get());

	/**
	 * Given a {@link Cell}, returns its Integer {@link Cell#getValue() Value}
	 * or 0 if empty.
	 */
	public static final Function<Cell, Integer> CELL_TO_VALUE_OR_0 = (c -> c.getValue().orElse(0));

	/**
	 * Returns a {@link Stream} contaning the numbers from 1 to 9.
	 * @return {@link Stream} contaning the numbers from 1 to 9.
	 */
	public static Stream<Integer> rangeStream() {
		return Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

}
