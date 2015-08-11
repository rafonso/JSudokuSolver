package jsudoku.core;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class CellFunctions {

	public static final Predicate<Cell> IS_FILLED = (c -> c.getValue().isPresent());

	public static final ToIntFunction<Cell> CELL_TO_VALUE = (c -> c.getValue().get());

	public static final ToIntFunction<Cell> CELL_TO_VALUE_OR_0 = (c -> c.getValue().orElse(0));

	public static Predicate<Cell> getColumnPredicate(int x) {
		return (c -> c.getColumn() == x);
	}

	public static Predicate<Cell> getRowPredicate(int x) {
		return (c -> c.getRow() == x);
	}

	public static Predicate<Cell> getSectorPredicate(int x) {
		return (c -> c.getSector() == x);
	}

}
