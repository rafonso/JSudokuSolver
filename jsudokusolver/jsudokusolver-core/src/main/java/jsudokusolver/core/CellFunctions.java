package jsudokusolver.core;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CellFunctions {

	static void validateRange(Integer i, String description) {
		assert(i > 0) && (i < 10) : "Invalid " + description + ": " + i;
	}

	public static final Predicate<Cell> IS_FILLED = (c -> c.getValue().isPresent());

	public static final Function<Cell, Integer> CELL_TO_VALUE = (c -> c.getValue().get());

	public static final Function<Cell, Integer> CELL_TO_VALUE_OR_0 = (c -> c.getValue().orElse(0));

	public static Stream<Integer> rangeStream() {
		return Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

}