package jsudoku.core;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;

import org.junit.Test;

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
			System.out.println(evt.getSource());
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
		for (int r = 1; r <= 9; r++) {
			for (int c = 1; c <= 9; c++) {
				Cell cell = p.getCell(r, c);
				assertEquals(r, cell.getRow());
				assertEquals(c, cell.getColumn());
			}
		}
	}

}
