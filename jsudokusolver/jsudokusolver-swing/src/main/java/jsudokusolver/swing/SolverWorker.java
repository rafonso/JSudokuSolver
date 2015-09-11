package jsudokusolver.swing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import jsudokusolver.core.Cell;
import jsudokusolver.core.CellStatus;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;
import jsudokusolver.core.Solver;

class SolverWorker extends SwingWorker<Void, Void>implements ItemListener, PropertyChangeListener {

	private final Puzzle puzzle;

	private final JLabel lblCycles;

	private final JLabel lblTime;

	private final JComboBox<Integer> cmbStepTime;

	private Solver solver;

	private Integer stepTime;

	private long t0;

	public SolverWorker(Puzzle puzzle, JComboBox<Integer> cmbStepTime, JLabel lblCycles, JLabel lblTime) {
		this.puzzle = puzzle;
		this.cmbStepTime = cmbStepTime;
		this.stepTime = cmbStepTime.getItemAt(cmbStepTime.getSelectedIndex());
		this.lblCycles = lblCycles;
		this.lblTime = lblTime;
		this.solver = new Solver();

		this.addListeners();
	}

	private void addListeners() {
		super.addPropertyChangeListener(this);
		this.solver.addPropertyChangeListener(this);
		this.cmbStepTime.addItemListener(this);
		this.puzzle.addPropertyChangeListener(this);
		for (int row = 1; row <= 9; row++) {
			for (int col = 1; col <= 9; col++) {
				this.puzzle.getCell(row, col).addPropertyChangeListener(this);
			}
		}
	}

	private void removeListeners() {
		super.removePropertyChangeListener(this);
		this.solver.removePropertyChangeListener(this);
		this.cmbStepTime.removeItemListener(this);
		this.puzzle.removePropertyChangeListener(this);
		for (int row = 1; row <= 9; row++) {
			for (int col = 1; col <= 9; col++) {
				this.puzzle.getCell(row, col).removePropertyChangeListener(this);
			}
		}
	}

	private void showCycle(int cycle) {
		this.lblCycles.setText(String.valueOf(cycle));
	}

	private void showTime() {
		this.lblTime.setText(String.valueOf(System.currentTimeMillis() - t0));
	}

	private void stepTime(boolean pause) {
		if (pause && (stepTime > 0)) {
			try {
				int delta = stepTime * 1000 / 2;
				int millis = delta / 1000;
				int nanos = delta % 1000;
				Thread.sleep(millis, nanos);
			} catch (InterruptedException e) {
				// http://stackoverflow.com/a/9139139/1659543
				// restore interrupted status
				Thread.currentThread().interrupt(); 
			}
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		this.t0 = System.currentTimeMillis();
		this.showCycle(0);
		this.showTime();
		this.solver.start(puzzle);

		return null;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			this.stepTime = (Integer) e.getItem();
			System.out.println("SolverWorker.itemStateChanged(): " + this.stepTime);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case Solver.SOLVER_CYCLE:
			this.showCycle((int) evt.getNewValue());
			break;
		case Puzzle.PUZZLE_STATUS:
			if (evt.getNewValue() == PuzzleStatus.SOLVED) {
				this.removeListeners();
			} else if (evt.getNewValue() == PuzzleStatus.STOPPED) {
				this.solver.requestStop();
				this.removeListeners();
			}
			break;
		case Cell.CELL_STATUS:
			boolean pause = evt.getOldValue().equals(CellStatus.EVALUATING)
					|| evt.getNewValue().equals(CellStatus.EVALUATING);
			this.stepTime(pause);
			break;
		case Cell.CELL_VALUE:
			CellStatus cellStatus = ((Cell) evt.getSource()).getStatus();
			@SuppressWarnings("unchecked")
			Optional<Integer> cellValue = (Optional<Integer>) evt.getNewValue();
			this.stepTime(cellStatus.equals(CellStatus.GUESSING) && cellValue.isPresent());
			break;
		case "state": // SwingWorker.StateValue
			if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
				this.removeListeners();
			}
			break;
		default:
			System.out.println("SolverWorker.propertyChange(): " + evt);
		}
		this.showTime();
	}

}
