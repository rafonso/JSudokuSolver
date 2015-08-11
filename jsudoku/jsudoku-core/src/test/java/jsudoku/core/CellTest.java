package jsudoku.core;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.util.Optional;

import org.junit.Test;

public class CellTest {

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
			System.out.println(evt.getSource());
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
			System.out.println(evt.getSource());
			assertSame(c, evt.getSource());
			assertEquals(Cell.CELL_STATUS, evt.getPropertyName());
			assertEquals(CellStatus.IDLE, evt.getOldValue());
			assertEquals(evt.getNewValue(), newStatus);
		});

		c.setStatus(newStatus);
	}

}
