package jsudokusolver.core;

import java.util.function.Function;
import java.util.function.Predicate;

public enum PuzzlePositions {

	ROW("Row", Cell::getRow), //
	COLUMN("Column", Cell::getColumn), //
	SECTOR("Sector", Cell::getSector);

	private final String description;

	private final Function<Cell, Integer> positionFunction;

	private PuzzlePositions(String description, Function<Cell, Integer> positionFunction) {
		this.description = description;
		this.positionFunction = positionFunction;
	}

	public String getDescription() {
		return description;
	}

	public Predicate<Cell> getPositionPredicate(int pos) {
		return (c -> positionFunction.apply(c) == pos);
	}

}
