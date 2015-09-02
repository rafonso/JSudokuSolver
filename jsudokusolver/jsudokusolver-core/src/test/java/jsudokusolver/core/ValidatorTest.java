package jsudokusolver.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeListener;

import org.junit.Test;

public class ValidatorTest {

	private Validator validator = new Validator();

	private void fillCell(Puzzle p, int row, int col, int value) {
		p.getCell(row, col).setValue(value);
	}

	private PropertyChangeListener addValidationListening(Puzzle p, PuzzleStatus endStatus) {
		return (evt -> {
			assertSame(p, evt.getSource());
			assertSame("Should be changing Puzzle Status", Puzzle.PUZZLE_STATUS, evt.getPropertyName());
			assertSame(evt.getNewValue(), p.getStatus());
			if (evt.getNewValue().equals(PuzzleStatus.VALIDATING)) {
				assertSame("Old Puzzle Status Should be " + PuzzleStatus.WAITING, PuzzleStatus.WAITING,
						evt.getOldValue());
			} else if (evt.getNewValue().equals(endStatus)) {
				assertSame("Old Puzzle Status Should be " + PuzzleStatus.VALIDATING, PuzzleStatus.VALIDATING,
						evt.getOldValue());
			} else {
				fail("Invalid New Status: " + evt.getNewValue());
			}
		});
	}

	private Puzzle getSamplePuzzle() {
		Puzzle p = new Puzzle();

		this.fillCell(p, 1, 1, 1);
		this.fillCell(p, 1, 2, 2);
		this.fillCell(p, 1, 6, 8);
		this.fillCell(p, 1, 7, 7);
		this.fillCell(p, 2, 1, 4);
		this.fillCell(p, 2, 4, 7);
		this.fillCell(p, 2, 5, 1);
		this.fillCell(p, 2, 6, 9);
		this.fillCell(p, 2, 9, 6);
		this.fillCell(p, 3, 2, 6);
		this.fillCell(p, 3, 3, 7);
		this.fillCell(p, 3, 4, 2);
		this.fillCell(p, 3, 7, 8);
		this.fillCell(p, 3, 8, 1);
		this.fillCell(p, 4, 1, 2);
		this.fillCell(p, 4, 3, 6);
		this.fillCell(p, 4, 8, 4);
		this.fillCell(p, 5, 5, 2);
		this.fillCell(p, 5, 6, 6);
		this.fillCell(p, 6, 1, 7);
		this.fillCell(p, 6, 3, 1);
		this.fillCell(p, 6, 8, 3);
		this.fillCell(p, 7, 2, 7);
		this.fillCell(p, 7, 3, 9);
		this.fillCell(p, 7, 4, 1);
		this.fillCell(p, 7, 7, 4);
		this.fillCell(p, 7, 8, 6);
		this.fillCell(p, 8, 1, 6);
		this.fillCell(p, 8, 4, 4);
		this.fillCell(p, 8, 5, 9);
		this.fillCell(p, 8, 6, 7);
		this.fillCell(p, 8, 9, 1);
		this.fillCell(p, 9, 1, 8);
		this.fillCell(p, 9, 2, 1);
		this.fillCell(p, 9, 6, 5);
		this.fillCell(p, 9, 7, 2);

		return p;
	}

	@Test(expected = EmptyPuzzleException.class)
	public void emptyPuzzle() throws InvalidPuzzleException {
		final Puzzle p = new Puzzle();
		p.addPropertyChangeListener(this.addValidationListening(p, PuzzleStatus.INVALID));
		this.validator.validate(p);
	}

	@Test
	public void validPuzzle() {
		Puzzle p = this.getSamplePuzzle();
		p.addPropertyChangeListener(this.addValidationListening(p, PuzzleStatus.READY));
		this.validator.validate(p);
		p.getCellsStream().forEach(
				c -> assertNotEquals("Status of cell " + c + " must not be ERROR", CellStatus.ERROR, c.getStatus()));
	}

	@Test
	public void repetitionInRow() {
		Puzzle p = this.getSamplePuzzle();
		p.addPropertyChangeListener(this.addValidationListening(p, PuzzleStatus.INVALID));
		this.fillCell(p, 6, 6, 1);

		try {
			this.validator.validate(p);

			fail("Should have repetition in row 6");
		} catch (RepeatedCellsException e) {
			assertSame(PuzzlePositions.ROW, e.getPuzzlePositions());
			assertEquals(1, e.getRepeatedValue());
			assertEquals(6, e.getPosition());

			assertEquals(CellStatus.ERROR, e.getCell1().getStatus());
			assertEquals(6, e.getCell1().getRow());
			assertEquals(3, e.getCell1().getColumn());
			assertEquals(1, e.getCell1().getValue().get().intValue());

			assertEquals(CellStatus.ERROR, e.getCell2().getStatus());
			assertEquals(6, e.getCell2().getRow());
			assertEquals(6, e.getCell2().getColumn());
			assertEquals(1, e.getCell2().getValue().get().intValue());
		}
	}

	@Test
	public void repetitionInColumn() {
		Puzzle p = this.getSamplePuzzle();
		p.addPropertyChangeListener(this.addValidationListening(p, PuzzleStatus.INVALID));
		this.fillCell(p, 8, 8, 3);

		try {
			this.validator.validate(p);

			fail("Should have repetition in column 8");
		} catch (RepeatedCellsException e) {
			assertSame(PuzzlePositions.COLUMN, e.getPuzzlePositions());
			assertEquals(3, e.getRepeatedValue());
			assertEquals(8, e.getPosition());

			assertEquals(CellStatus.ERROR, e.getCell1().getStatus());
			assertEquals(6, e.getCell1().getRow());
			assertEquals(8, e.getCell1().getColumn());
			assertEquals(3, e.getCell1().getValue().get().intValue());

			assertEquals(CellStatus.ERROR, e.getCell2().getStatus());
			assertEquals(8, e.getCell2().getRow());
			assertEquals(8, e.getCell2().getColumn());
			assertEquals(3, e.getCell2().getValue().get().intValue());
		}
	}

	@Test
	public void repetitionInSector() {
		Puzzle p = this.getSamplePuzzle();
		p.addPropertyChangeListener(this.addValidationListening(p, PuzzleStatus.INVALID));
		this.fillCell(p, 6, 9, 4);

		try {
			this.validator.validate(p);

			fail("Should have repetition in sector 6");
		} catch (RepeatedCellsException e) {
			assertSame(PuzzlePositions.SECTOR, e.getPuzzlePositions());
			assertEquals(4, e.getRepeatedValue());
			assertEquals(6, e.getPosition());

			assertEquals(CellStatus.ERROR, e.getCell1().getStatus());
			assertEquals(4, e.getCell1().getRow());
			assertEquals(8, e.getCell1().getColumn());
			assertEquals(4, e.getCell1().getValue().get().intValue());

			assertEquals(CellStatus.ERROR, e.getCell2().getStatus());
			assertEquals(6, e.getCell2().getRow());
			assertEquals(9, e.getCell2().getColumn());
			assertEquals(4, e.getCell2().getValue().get().intValue());
		}
	}

}
