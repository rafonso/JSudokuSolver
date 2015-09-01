package jsudokusolver.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

import jsudokusolver.core.Cell;
import jsudokusolver.core.CellStatus;

public class CellTest {

	private void testGetPositionForValidValue(int pos, int expectedRow, int expectedCol) {
		int[] positions = Cell.valueToPositions(pos);
		assertTrue(
				"Position " + pos + " should to have generated positions [" + expectedRow + ", " + expectedCol
						+ "]. But it was gotten " + Arrays.toString(positions),
				(expectedRow == positions[0]) && (expectedCol == positions[1]));
	}

	private void testGetPositionForInvalidValue(int i) {
		try {
			int[] positions = Cell.valueToPositions(i);

			fail(i + " should be Invalid, but It was gotten " + Arrays.toString(positions));
		} catch (AssertionError e) {
			// OK
		}
	}

	@Test
	public void createNewCell() {
		Cell c = new Cell(1, 1);

		assertEquals(1, c.getRow());
		assertEquals(1, c.getColumn());
		assertEquals(Cell.NO_VALUE, c.getValue());
		assertEquals(CellStatus.IDLE, c.getStatus());
	}

	@Test(expected = AssertionError.class)
	public void createCellInvalidRow0() {
		Cell c = new Cell(0, 2);
		fail(c.toString());
	}

	@Test(expected = AssertionError.class)
	public void createCellInvalidRow10() {
		Cell c = new Cell(10, 2);
		fail(c.toString());
	}

	@Test(expected = AssertionError.class)
	public void createCellInvalidCol0() {
		Cell c = new Cell(1, 0);
		fail(c.toString());
	}

	@Test(expected = AssertionError.class)
	public void createCellInvalidCol10() {
		Cell c = new Cell(1, 10);
		fail(c.toString());
	}

	@Test(expected = AssertionError.class)
	public void settingNullValue() {
		Cell c = new Cell(1, 1);
		c.setValue(null);
	}

	@Test(expected = AssertionError.class)
	public void settingValue0() {
		Cell c = new Cell(1, 1);
		c.setValue(0);
	}

	@Test(expected = AssertionError.class)
	public void settingValue10() {
		Cell c = new Cell(1, 1);
		c.setValue(10);
	}

	@Test
	public void settingValidValues() {
		Cell c = new Cell(1, 1);
		for (int i = 1; i < 10; i++) {
			c.setValue(i);

			assertTrue(c.getValue().isPresent());
			assertEquals(i, c.getValue().get().intValue());
		}

		c.setValue(Cell.NO_VALUE);
		assertFalse(c.getValue().isPresent());
	}

	@Test
	public void observingValueChange() {
		Cell c = new Cell(1, 1);
		int newValue = 5;
		c.addPropertyChangeListener((PropertyChangeEvent evt) -> {
			assertSame(c, evt.getSource());
			assertEquals(Cell.CELL_VALUE, evt.getPropertyName());
			assertEquals(evt.getOldValue(), Cell.NO_VALUE);
			assertEquals(Optional.of(newValue), evt.getNewValue());
		});

		c.setValue(newValue);
	}

	@Test(expected = AssertionError.class)
	public void settingNullStatus() {
		Cell c = new Cell(1, 1);
		c.setStatus(null);
	}

	@Test
	public void settingValidStatus() {
		Cell c = new Cell(1, 1);
		for (CellStatus status : CellStatus.values()) {
			c.setStatus(status);
			assertSame(status, c.getStatus());
		}
	}

	@Test
	public void observingStatusChange() {
		Cell c = new Cell(1, 1);
		CellStatus newStatus = CellStatus.FILLED;
		c.addPropertyChangeListener((PropertyChangeEvent evt) -> {
			assertSame(c, evt.getSource());
			assertEquals(Cell.CELL_STATUS, evt.getPropertyName());
			assertEquals(CellStatus.IDLE, evt.getOldValue());
			assertEquals(evt.getNewValue(), newStatus);
		});

		c.setStatus(newStatus);
	}

	@Test
	public void getPositionForInvalidValues() {
		int[] invalidValues = { -2, -1, 0, 81, 100 };

		for (int i : invalidValues) {
			testGetPositionForInvalidValue(i);
		}
	}

	@Test
	public void getPositionForValidValues() {
		int[][] validValues = { { 0, 1, 1 }, { 1, 1, 2 }, { 2, 1, 3 }, { 3, 1, 4 }, { 4, 1, 5 }, { 5, 1, 6 },
				{ 6, 1, 7 }, { 7, 1, 8 }, { 8, 1, 9 },

				{ 9, 2, 1 }, { 10, 2, 2 }, { 11, 2, 3 }, { 12, 2, 4 }, { 13, 2, 5 }, { 14, 2, 6 }, { 15, 2, 7 },
				{ 16, 2, 8 }, { 17, 2, 9 },

				{ 18, 3, 1 }, { 19, 3, 2 }, { 20, 3, 3 }, { 21, 3, 4 }, { 22, 3, 5 }, { 23, 3, 6 }, { 24, 3, 7 },
				{ 25, 3, 8 }, { 26, 3, 9 },

				{ 27, 4, 1 }, { 28, 4, 2 }, { 29, 4, 3 }, { 30, 4, 4 }, { 31, 4, 5 }, { 32, 4, 6 }, { 33, 4, 7 },
				{ 34, 4, 8 }, { 35, 4, 9 },

				{ 36, 5, 1 }, { 37, 5, 2 }, { 38, 5, 3 }, { 39, 5, 4 }, { 40, 5, 5 }, { 41, 5, 6 }, { 42, 5, 7 },
				{ 43, 5, 8 }, { 44, 5, 9 },

				{ 45, 6, 1 }, { 46, 6, 2 }, { 47, 6, 3 }, { 48, 6, 4 }, { 49, 6, 5 }, { 50, 6, 6 }, { 51, 6, 7 },
				{ 52, 6, 8 }, { 53, 6, 9 },

				{ 54, 7, 1 }, { 55, 7, 2 }, { 56, 7, 3 }, { 57, 7, 4 }, { 58, 7, 5 }, { 59, 7, 6 }, { 60, 7, 7 },
				{ 61, 7, 8 }, { 62, 7, 9 },

				{ 63, 8, 1 }, { 64, 8, 2 }, { 65, 8, 3 }, { 66, 8, 4 }, { 67, 8, 5 }, { 68, 8, 6 }, { 69, 8, 7 },
				{ 70, 8, 8 }, { 71, 8, 9 },

				{ 72, 9, 1 }, { 73, 9, 2 }, { 74, 9, 3 }, { 75, 9, 4 }, { 76, 9, 5 }, { 77, 9, 6 }, { 78, 9, 7 },
				{ 79, 9, 8 }, { 80, 9, 9 }, };

		for (int[] validVal : validValues) {
			this.testGetPositionForValidValue(validVal[0], validVal[1], validVal[2]);
		}
	}
}
