package jsudokusolver.core;

import static jsudokusolver.core.CellFunctions.rangeStream;

import jsudokusolver.core.exception.EmptyPuzzleException;
import jsudokusolver.core.exception.SudokuException;
import jsudokusolver.core.exception.RepeatedCellsException;

/**
 * Validates a {@link Puzzle}. 4 types of validation are done:
 * <ol>
 * <li>If all {@link Cell}s are empty. If yes, it is thrown a
 * {@link EmptyPuzzleException}.</li>
 * <li>If for each {@link PuzzlePositions#ROW Row} there is some repeated value.
 * If yes, it is thrown a {@link RepeatedCellsException}.</li>
 * <li>If for each {@link PuzzlePositions#COLUMN Column} there is some repeated
 * value. If yes, it is thrown a {@link RepeatedCellsException}.</li>
 * <li>If for each {@link PuzzlePositions#SECTOR Sector} there is some repeated
 * value. If yes, it is thrown a {@link RepeatedCellsException}.</li>
 * </ol>
 * 
 * In all cases, The {@link Puzzle}'s {@link Puzzle#getStatus() Status} is
 * changed to {@link PuzzleStatus#INVALID}.
 *
 */
public class Validator {

	private void validateFilling(Puzzle p) throws EmptyPuzzleException {
		if (p.getCellsStream().noneMatch(Cell::hasValue)) {
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
					cellsByValue[value].setStatus(CellStatus.ERROR);
					c.setStatus(CellStatus.ERROR);

					throw new RepeatedCellsException(puzzlePositions, pos, cellsByValue[value], c);
				}
				cellsByValue[value] = c;
			});
		});

	}

	/**
	 * Validates a {@link Puzzle}.
	 * 
	 * @param p
	 *            Puzzle to be validated.
	 * @throws SudokuException
	 *             If it was found some error.
	 */
	public void validate(Puzzle p) throws SudokuException {
		p.setStatus(PuzzleStatus.VALIDATING);
		this.validateFilling(p);
		this.validateRepetition(p, PuzzlePositions.ROW);
		this.validateRepetition(p, PuzzlePositions.COLUMN);
		this.validateRepetition(p, PuzzlePositions.SECTOR);
		p.setStatus(PuzzleStatus.READY);
	}

}
