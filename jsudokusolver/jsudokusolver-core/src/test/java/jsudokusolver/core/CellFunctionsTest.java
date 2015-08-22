package jsudokusolver.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;

import jsudokusolver.core.Cell;
import jsudokusolver.core.CellFunctions;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzlePositions;

public class CellFunctionsTest {

	public void testPredicate(Function<Integer, Predicate<Cell>> predicateGenerator,
			Function<Cell, Integer> cellToPosition) {
		Puzzle p = new Puzzle();
		for (int i = 1; i <= 9; i++) {
			Set<Integer> positions = new HashSet<>();
			final int position = i;
			Predicate<Cell> predicate = predicateGenerator.apply(i);
			p.getCells().stream().filter(predicate).forEach(c -> {
				assertEquals(position, cellToPosition.apply(c).intValue());
				if (!positions.add(c.getRow())) {
					fail("Repeated Row: " + c.getRow());
				}
			});
		}
	}

	@Test
	public void cellIsNotFilled() {
		Cell c = new Cell(1, 1);
		assertFalse(c.hasValue());
	}

	@Test
	public void cellIsFilled() {
		Cell c = new Cell(1, 1);
		c.setValue(5);
		assertTrue(c.hasValue());
	}

	@Test(expected = NoSuchElementException.class)
	public void cellToValueInvalid() {
		Cell c = new Cell(1, 1);
		int value = CellFunctions.CELL_TO_VALUE.apply(c);
		fail(value + "");
	}

	@Test
	public void cellToValueValid() {
		Cell c = new Cell(1, 1);
		c.setValue(5);
		assertEquals(5, CellFunctions.CELL_TO_VALUE.apply(c).intValue());
	}

	@Test
	public void cellToValueOr0With0() {
		Cell c = new Cell(1, 1);
		assertEquals(0, CellFunctions.CELL_TO_VALUE_OR_0.apply(c).intValue());
	}

	@Test
	public void cellToValueOr0Valid() {
		Cell c = new Cell(1, 1);
		c.setValue(5);
		assertEquals(5, CellFunctions.CELL_TO_VALUE_OR_0.apply(c).intValue());
	}

	@Test
	public void testGetColumnPredicate() {
		Puzzle p = new Puzzle();
		for (int i = 1; i <= 9; i++) {
			Set<Integer> positions = new HashSet<>();
			final int position = i;			
			p.getCells().stream().filter(PuzzlePositions.COLUMN.getPositionPredicate(i)).forEach(c -> {
				assertEquals(position, c.getColumn());
				if (!positions.add(c.getRow())) {
					fail("Repeated Row: " + c.getRow());
				}
			});
		}
	}

	@Test
	public void testGetRowPredicate() {
		Puzzle p = new Puzzle();
		for (int i = 1; i <= 9; i++) {
			Set<Integer> positions = new HashSet<>();
			final int position = i;
			p.getCells().stream().filter(PuzzlePositions.ROW.getPositionPredicate(i)).forEach(c -> {
				assertEquals(position, c.getRow());
				if (!positions.add(c.getColumn())) {
					fail("Repeated Column: " + c.getColumn());
				}
			});
		}
	}

	@Test
	public void testGetSectorPredicate() {
		Puzzle p = new Puzzle();
		for (int i = 1; i <= 9; i++) {
//			Set<Integer> positions = new HashSet<>();
			final int position = i;
			p.getCells().stream().filter(PuzzlePositions.SECTOR.getPositionPredicate(i)).forEach(c -> {
				assertEquals(position, c.getSector());
//				if (!positions.add(c.getColumn())) {
//					fail("Repeated Column: " + c.getColumn());
//				}
			});
		}
	}

}
