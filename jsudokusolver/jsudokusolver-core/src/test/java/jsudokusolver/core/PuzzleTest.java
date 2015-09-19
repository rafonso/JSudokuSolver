package jsudokusolver.core;

import static jsudokusolver.core.CellFunctions.rangeStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeEvent;
import java.util.function.Predicate;

import org.junit.Test;

import jsudokusolver.core.Puzzle.CellsFormatter;
import jsudokusolver.core.exception.ParserException;

public class PuzzleTest {

	private static final String STOPPED_PUZZLE_STR = "000090310.004000705.020008946.000002400.300800007.007500000.453900060.708000100.091030000";

	private Puzzle getStoppedPuzzle() {
		Puzzle p = new Puzzle();

		p.parse(STOPPED_PUZZLE_STR);

		p.getCell(1, 1).setValueStatus(1, CellStatus.FILLED);
		p.getCell(2, 2).setValueStatus(2, CellStatus.FILLED);
		p.getCell(3, 3).setValueStatus(3, CellStatus.FILLED);
		p.getCell(4, 4).setValueStatus(4, CellStatus.FILLED);
		p.getCell(5, 5).setValueStatus(5, CellStatus.FILLED);
		p.getCell(6, 6).setValueStatus(6, CellStatus.FILLED);
		p.getCell(7, 7).setValueStatus(7, CellStatus.FILLED);
		p.getCell(8, 8).setValueStatus(8, CellStatus.FILLED);
		p.getCell(9, 9).setValueStatus(9, CellStatus.FILLED);

		p.setStatus(PuzzleStatus.STOPPED);

		return p;
	}

	@Test
	public void puzzleCreation() {
		Puzzle p = new Puzzle();

		assertSame(PuzzleStatus.WAITING, p.getStatus());
		assertEquals("000000000.000000000.000000000.000000000.000000000.000000000.000000000.000000000.000000000",
				p.formatCells(CellsFormatter.ALL));
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

	@Test(expected = ParserException.class)
	public void parseInvalidString() {
		Puzzle p = new Puzzle();

		p.parse("ASDFGHJKL");
	}

	@Test(expected = ParserException.class)
	public void parseInvalidLenght() {
		Puzzle p = new Puzzle();

		p.parse("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
	}

	@Test
	public void parseValid() {
		Puzzle p = new Puzzle();

		p.parse("123456789012345678901234567890123456789012345678901234567890123456789012345678901");

		assertEquals("123456789.012345678.901234567.890123456.789012345.678901234.567890123.456789012.345678901",
				p.formatCells(CellsFormatter.ALL));
	}

	@Test
	public void testformatCells() {
		Puzzle p = this.getStoppedPuzzle();

		assertEquals("100090310.024000705.023008946.000402400.300850007.007506000.453900760.708000180.091030009",
				p.formatCells(CellsFormatter.ALL));
		assertEquals(STOPPED_PUZZLE_STR, p.formatCells(CellsFormatter.ORIGINALS));
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
		p.setStatus(PuzzleStatus.SOLVED);

		p.cleanCells();

		for (int i = 0; i < 81; i++) {
			int[] pos = Cell.valueToPositions(i);
			Cell c = p.getCell(pos[0], pos[1]);
			assertFalse("Cell " + c + " should be empty", c.getValue().isPresent());
			assertSame("Cell " + c + " should be in Status IDLE but was " + c.getStatus(), CellStatus.IDLE,
					c.getStatus());
		}
		assertEquals(PuzzleStatus.WAITING, p.getStatus());
	}

	@Test(expected = AssertionError.class)
	public void resetAtRunningStatus() {
		Puzzle p = new Puzzle();
		p.setStatus(PuzzleStatus.RUNNING);

		p.reset();
	}

	@Test
	public void resetAtValidStatus() {
		Puzzle p = getStoppedPuzzle();

		p.reset();

		p.getCellsStream().filter(c -> (c.getRow() == c.getColumn())).forEach(c -> {
			assertFalse(c.toString(), c.hasValue());
			assertEquals(c.toString(), CellStatus.IDLE, c.getStatus());
		});
		p.getCellsStream().filter(Cell::hasValue).forEach(c -> {
			assertEquals(c.toString(), CellStatus.ORIGINAL, c.getStatus());
		});
		p.getCellsStream().filter(((Predicate<Cell>) Cell::hasValue).negate()).forEach(c -> {
			assertEquals(c.toString(), CellStatus.IDLE, c.getStatus());
		});
		assertEquals(PuzzleStatus.WAITING, p.getStatus());
	}

}
