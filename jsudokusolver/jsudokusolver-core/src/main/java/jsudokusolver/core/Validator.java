package jsudokusolver.core;

import static jsudokusolver.core.CellFunctions.rangeStream;

public class Validator {

	private void validateFilling(Puzzle p) throws EmptyPuzzleException {
		if (p.getCells().stream().noneMatch(Cell::hasValue)) {
			p.setStatus(PuzzleStatus.INVALID);
			throw new EmptyPuzzleException();
		}
	}

	private void validateRepetition(Puzzle p, PuzzlePositions puzzlePositions) throws RepeatedCellsException {
		rangeStream().forEach(pos -> {
			Cell[] cellsByValue = new Cell[10];
			p.getCellsStream().filter(puzzlePositions.getPositionPredicate(pos).and(Cell::hasValue)).forEach(c -> {
				final Integer value = c.getValue().get();
				if (cellsByValue[value] != null) {
					p.setStatus(PuzzleStatus.INVALID);
					throw new RepeatedCellsException(puzzlePositions, pos, cellsByValue[value], c);
				}
				cellsByValue[value] = c;
			});
		});

	}

	public void validate(Puzzle p) throws InvalidPuzzleException {
		p.setStatus(PuzzleStatus.VALIDATING);;
		this.validateFilling(p);
		this.validateRepetition(p, PuzzlePositions.ROW);
		this.validateRepetition(p, PuzzlePositions.COLUMN);
		this.validateRepetition(p, PuzzlePositions.SECTOR);
		p.setStatus(PuzzleStatus.READY);
	}

}
