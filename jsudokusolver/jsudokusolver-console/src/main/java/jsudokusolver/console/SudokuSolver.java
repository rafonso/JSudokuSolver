package jsudokusolver.console;

import java.time.Instant;
import java.util.Scanner;

import jsudokusolver.core.Cell;
import jsudokusolver.core.CellStatus;
import jsudokusolver.core.Puzzle;
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
				cell.setStatus(CellStatus.ORIGINAL);
			}
		}

		return p;
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

		if (args.length > 1 && args[1].equalsIgnoreCase("pause")) {
			@SuppressWarnings("resource")
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
		}

		System.out.println("Solving Puzzle: " + puzzle.formatCells());

		final Solver solver = new Solver();
		solver.addPropertyChangeListener(listener);
		solver.addSolverGuessListener(listener);

		Instant t0 = Instant.now();
		solver.start(puzzle);
		Instant t1 = Instant.now();

		// System.out.println(formatPuzzle(puzzle));
		System.out.println("Puzzle Solved: " + puzzle.formatCells());
		System.out.printf("Time: %d ms", (t1.toEpochMilli() - t0.toEpochMilli()));
	}

}
