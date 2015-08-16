package jsudokusolver.console;

import java.util.function.Function;
import java.util.stream.Collectors;

import jsudokusolver.core.Cell;
import jsudokusolver.core.CellFunctions;
import jsudokusolver.core.CellStatus;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzlePositions;
import jsudokusolver.core.Solver;
import jsudokusolver.core.Validator;

public class SudokuSolver {

	public SudokuSolver() {
		// TODO Auto-generated constructor stub
	}

	private static final Puzzle commandToPuzzle(String str, ConsoleListener listener) {
		if (!str.matches("^(\\d|.)+$")) {
			throw new IllegalArgumentException("Invalid Puzzle");
		}
		String digits = str.replaceAll("\\.", "");
		if (digits.length() != 81) {
			throw new IllegalArgumentException("it should have 81 digits!");
		}

		Puzzle p = new Puzzle();
		p.addPropertyChangeListener(listener);
		for (int pos = 0; pos < digits.toCharArray().length; pos++) {
			final Cell cell = p.getCell((pos / 9) + 1, (pos % 9) + 1);
			cell.addPropertyChangeListener(listener);

			int digit = digits.toCharArray()[pos] - '0';
			if (digit > 0) {
				cell.setValue(digit);
				cell.setStatus(CellStatus.FILLED);
			}
		}

		return p;
	}

	private static String formatPuzzle(Puzzle p) {
		final Function<? super Integer, ? extends String> formatRow = row -> {
			Integer[] values = p.getCellsStream().filter(PuzzlePositions.ROW.getPositionPredicate(row))
					.map(CellFunctions.CELL_TO_VALUE_OR_0).collect(Collectors.toList()).toArray(new Integer[9]);
			return String.format("\u2502%d%d%d\u2502%d%d%d\u2502%d%d%d\u2502%n", values[0], values[1], values[2], values[3], values[4],
					values[5], values[6], values[7], values[8]);
		};
		String[] formatedRows = CellFunctions.rangeStream().map(formatRow).collect(Collectors.toList())
				.toArray(new String[9]);

		return "\u250C\u2500\u2500\u2500\u252C\u2500\u2500\u2500\u252C\u2500\u2500\u2500\u2510\n" //
				+ formatedRows[0] //
				+ formatedRows[1] //
				+ formatedRows[2] //
				+ "\u251C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u2524\n" //
				+ formatedRows[3] //
				+ formatedRows[4] //
				+ formatedRows[5] //
				+ "\u251C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u253C\u2500\u2500\u2500\u2524\n" //
				+ formatedRows[6] //
				+ formatedRows[7] //
				+ formatedRows[8] //
				+ "\u2514\u2500\u2500\u2500\u2534\u2500\u2500\u2500\u2534\u2500\u2500\u2500\u2518\n";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("No puzzle");
			return;
		}

		ConsoleListener listener = new ConsoleListener();
		Puzzle puzzle = commandToPuzzle(args[0], listener);
		new Validator().validate(puzzle);

		System.out.println(formatPuzzle(puzzle));

		final Solver solver = new Solver();
		solver.addPropertyChangeListener(listener);
		solver.start(puzzle);

		System.out.println(formatPuzzle(puzzle));
	}

}
