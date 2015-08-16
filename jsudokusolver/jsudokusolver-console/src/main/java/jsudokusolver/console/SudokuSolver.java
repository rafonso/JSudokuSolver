package jsudokusolver.console;

import jsudokusolver.core.Puzzle;
import jsudokusolver.core.Validator;

public class SudokuSolver {

	public SudokuSolver() {
		// TODO Auto-generated constructor stub
	}

	private static final Puzzle commandToPuzzle(String str) {
		if (!str.matches("^(\\d|.)+$")) {
			throw new IllegalArgumentException("Invalid Puzzle");
		}
		String digits = str.replaceAll("\\.", "");
		if (digits.length() != 81) {
			throw new IllegalArgumentException("it should have 81 digits!");
		}

		Puzzle p = new Puzzle();
		for (int pos = 0; pos < digits.toCharArray().length; pos++) {
			int digit = digits.toCharArray()[pos] - '0';
			if (digit > 0) {
				int row = (pos / 9) + 1;
				int col = (pos % 9) + 1;
				p.getCell(row, col).setValue(digit);
			}
		}

		return p;
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("No puzzle");
			return;
		}

		Puzzle puzzle = commandToPuzzle(args[0]);
		new Validator().validate(puzzle);

		System.out.println(puzzle);
	}

}
