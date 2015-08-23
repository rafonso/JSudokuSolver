package jsudokusolver.core;

import java.util.stream.Stream;

/**
 * Utility Functions.
 */
class CellFunctions {

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
	 * Returns a {@link Stream} contaning the numbers from 1 to 9.
	 * 
	 * @return {@link Stream} contaning the numbers from 1 to 9.
	 */
	static Stream<Integer> rangeStream() {
		return Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

}
