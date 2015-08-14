package jsudoku.core;

import static jsudoku.core.CellFunctions.IS_FILLED;
import static jsudoku.core.CellFunctions.rangeStream;

public class Validator {

	private void validateFilling(Puzzle p) throws EmptyPuzzleException {
		if (p.getCells().stream().noneMatch(IS_FILLED)) {
			throw new EmptyPuzzleException();
		}
	}

	private void validateRepetition(Puzzle p, PuzzlePositions puzzlePositions) throws RepeatedCellsException {
		rangeStream().forEach(pos -> {
			Cell[] cellsByValue = new Cell[10];
			p.getCellsStream().filter(puzzlePositions.getPositionPredicate(pos).and(IS_FILLED)).forEach(c -> {
				final Integer value = c.getValue().get();
				if (cellsByValue[value] != null) {
					throw new RepeatedCellsException(puzzlePositions, pos, cellsByValue[value], c);
				}
				cellsByValue[value] = c;
			});
		});

	}

	public void validate(Puzzle p) throws InvalidPuzzleException {
		this.validateFilling(p);
		this.validateRepetition(p, PuzzlePositions.ROW);
		this.validateRepetition(p, PuzzlePositions.COLUMN);
		this.validateRepetition(p, PuzzlePositions.SECTOR);
	}

}
