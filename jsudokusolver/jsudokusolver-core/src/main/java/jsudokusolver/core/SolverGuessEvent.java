package jsudokusolver.core;

import java.util.EventObject;

public class SolverGuessEvent extends EventObject {

	private static final long serialVersionUID = -7794076992199163136L;

	public enum SolverGuessEventType {
		ADDITION, REMOVAL
	}

	private final SolverGuessEventType type;

	private final Cell guessCell;

	public SolverGuessEvent(Object source, SolverGuessEventType type, Cell guessCell) {
		super(source);
		this.type = type;
		this.guessCell = guessCell;
	}

	public SolverGuessEventType getType() {
		return type;
	}

	public Cell getGuessCell() {
		return guessCell;
	}

}
