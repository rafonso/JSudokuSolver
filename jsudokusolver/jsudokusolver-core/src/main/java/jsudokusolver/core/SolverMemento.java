package jsudokusolver.core;

import java.util.List;

class SolverMemento {

	private final Cell cell;

	private final int pendentValue;

	private final List<Cell> pendents;

	SolverMemento(Cell cell, int pendentValue, List<Cell> pendents) {
		this.cell = cell;
		this.pendentValue = pendentValue;
		this.pendents = pendents;
	}

	public Cell getCell() {
		return cell;
	}

	public int getPendentValue() {
		return pendentValue;
	}

	public List<Cell> getPendents() {
		return pendents;
	}

}
