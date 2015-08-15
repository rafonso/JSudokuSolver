package jsudokusolver.core;

public enum CellStatus {

	IDLE('I'), //
	ORIGINAL('O'), //
	FILLED('F'), //
	EVALUATING('E'), //
	GUESSING('G'), //
	ERROR('X');

	private final char code;

	private CellStatus(char code) {
		this.code = code;
	}

	public char getCode() {
		return code;
	}

}
