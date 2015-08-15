package jsudokusolver.core;

public class InvalidPuzzleException extends RuntimeException {

	private static final long serialVersionUID = -7474006112414026951L;

	public InvalidPuzzleException() {
		super();
	}

	public InvalidPuzzleException(String msg) {
		super(msg);
	}

}
