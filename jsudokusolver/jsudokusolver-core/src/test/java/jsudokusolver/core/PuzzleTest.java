package jsudokusolver.core;

import static jsudokusolver.core.CellFunctions.rangeStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeEvent;

import org.junit.Test;

import jsudokusolver.core.Cell;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;

public class PuzzleTest {

	@Test
	public void puzzleCreation() {
		Puzzle p = new Puzzle();
		assertSame(PuzzleStatus.WAITING, p.getStatus());
		assertEquals("000000000.000000000.000000000.000000000.000000000.000000000.000000000.000000000.000000000",
				p.formatCells());
	}

	@Test(expected = AssertionError.class)
	public void setNullStatus() {
		Puzzle p = new Puzzle();
		p.setStatus(null);

		fail(p.toString());
	}

	@Test
	public void settingValidStatus() {
		Puzzle p = new Puzzle();
		for (PuzzleStatus status : PuzzleStatus.values()) {
			p.setStatus(status);
			assertSame(status, p.getStatus());
		}
	}

	@Test
	public void observingStatusChange() {
		Puzzle p = new Puzzle();
		PuzzleStatus newStatus = PuzzleStatus.READY;
		p.addPropertyChangeListener((PropertyChangeEvent evt) -> {
			assertSame(p, evt.getSource());
			assertEquals(Puzzle.PUZZLE_STATUS, evt.getPropertyName());
			assertEquals(PuzzleStatus.WAITING, evt.getOldValue());
			assertEquals(newStatus, evt.getNewValue());
		});

		p.setStatus(newStatus);
	}

	@Test(expected = AssertionError.class)
	public void getCellInvalidRow() {
		Puzzle p = new Puzzle();
		p.getCell(0, 1);
	}

	@Test(expected = AssertionError.class)
	public void getCellInvalidCol() {
		Puzzle p = new Puzzle();
		p.getCell(1, 10);
	}

	@Test
	public void getValidCells() {
		Puzzle p = new Puzzle();
		rangeStream().forEach(r -> rangeStream().forEach(c -> {
			Cell cell = p.getCell(r, c);
			assertEquals(r.intValue(), cell.getRow());
			assertEquals(c.intValue(), cell.getColumn());
		}));
	}

	@Test(expected = AssertionError.class)
	public void parseInvalidStatus() {
		Puzzle p = new Puzzle();
		p.setStatus(PuzzleStatus.READY);
		p.parse("1234");
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseInvalidString() {
		Puzzle p = new Puzzle();
		p.parse("ASDFGHJKL");
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseInvalidLenght() {
		Puzzle p = new Puzzle();
		p.parse("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
	}

	@Test
	public void parseValid() {
		Puzzle p = new Puzzle();
		p.parse("123456789012345678901234567890123456789012345678901234567890123456789012345678901");
		assertEquals("123456789.012345678.901234567.890123456.789012345.678901234.567890123.456789012.345678901",
				p.formatCells());
	}

	@Test(expected = AssertionError.class)
	public void cleanCellsInvalidStates() {
		Puzzle p = new Puzzle();
		p.setStatus(PuzzleStatus.RUNNING);
		p.cleanCells();
	}

	@Test
	public void cleanCellsValidStates() {
		Puzzle p = new Puzzle();
		p.parse("123456789012345678901234567890123456789012345678901234567890123456789012345678901");

		p.cleanCells();

		for (int i = 0; i < 81; i++) {
			int[] pos = Cell.valueToPositions(i);
			Cell c = p.getCell(pos[0], pos[1]);
			assertFalse("Cell " + c + " should be empty", c.getValue().isPresent());
			assertSame("Cell " + c + " should be in Status IDLE but was " + c.getStatus(), CellStatus.IDLE,
					c.getStatus());
		}
	}

}
