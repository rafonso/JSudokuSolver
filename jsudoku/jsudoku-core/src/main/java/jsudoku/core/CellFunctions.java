package jsudoku.core;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class CellFunctions {

	static void validateRange(Integer i, String description) {
		assert(i > 0) && (i < 10) : "Invalid " + description + ": " + i;
	}

	public static final Predicate<Cell> IS_FILLED = (c -> c.getValue().isPresent());

	public static final ToIntFunction<Cell> CELL_TO_VALUE = (c -> c.getValue().get());

	public static final ToIntFunction<Cell> CELL_TO_VALUE_OR_0 = (c -> c.getValue().orElse(0));

	public static Predicate<Cell> getColumnPredicate(int col) {
		return (c -> c.getColumn() == col);
	}

	public static Predicate<Cell> getRowPredicate(int row) {
		return (c -> c.getRow() == row);
	}

	public static Predicate<Cell> getSectorPredicate(int sector) {
		return (c -> c.getSector() == sector);
	}

}
