package jsudokusolver.core;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * It contains a {@link Function} which returns a {@link Cell}'s
 * Row/Column/Sector inside a {@link Puzzle}.
 *
 */
public enum PuzzlePositions {

	/**
	 * Represents a Row's {@link Cell}.
	 * 
	 * @see Cell#getRow()
	 */
	ROW("Row", Cell::getRow), //
	/**
	 * Represents a Column's {@link Cell}.
	 * 
	 * @see Cell#getColumn()
	 */
	COLUMN("Column", Cell::getColumn), //
	/**
	 * Represents a Sector {@link Cell}.
	 * 
	 * @see Cell#getSector()
	 */
	SECTOR("Sector", Cell::getSector);

	private final String description;

	private final Function<Cell, Integer> positionFunction;

	private PuzzlePositions(String description, Function<Cell, Integer> positionFunction) {
		this.description = description;
		this.positionFunction = positionFunction;
	}

	/*
	 * Returns the Position's description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Return a {@link Predicate} which verifies if a {@link Cell} is in a
	 * determinated position (Row/Column/Sector) inside a {@link Puzzle}.
	 * 
	 * @param pos
	 *            Position
	 * @return a Predicate which verifies if a Cell is in a determinated
	 *         position inside a Puzzle.
	 */
	public Predicate<Cell> getPositionPredicate(int pos) {
		return (c -> positionFunction.apply(c) == pos);
	}

}
