package jsudokusolver.swing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

		super.addPropertyChangeListener(this);
		this.solver.addPropertyChangeListener(this);
		cmbStepTime.addItemListener(this);
		this.puzzle.addPropertyChangeListener(this);
		for (int row = 1; row <= 9; row++) {
			for (int col = 1; col <= 9; col++) {
				this.puzzle.getCell(row, col).addPropertyChangeListener(this);
			}
		}
	}

	private void showCycle(int cycle) {
		this.lblCycles.setText(String.valueOf(cycle));
	}

	private void showTime() {
		this.lblTime.setText(String.valueOf(System.currentTimeMillis() - t0));
	}

	private void stepTime(CellStatus previousStatus, CellStatus newStatus) {
		boolean pause = (previousStatus == CellStatus.EVALUATING) || (newStatus == CellStatus.EVALUATING);
		if (pause && (stepTime > 0)) {
			try {
				if (stepTime == 1) {
					Thread.sleep(0, 500);
				} else if (stepTime == 5) {
					Thread.sleep(2, 500);
				} else {
					Thread.sleep(this.stepTime / 2);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void removeListeners() {
		super.removePropertyChangeListener(this);
		this.solver.removePropertyChangeListener(this);
		cmbStepTime.removeItemListener(this);
		this.puzzle.removePropertyChangeListener(this);
		for (int row = 1; row <= 9; row++) {
			for (int col = 1; col <= 9; col++) {
				this.puzzle.getCell(row, col).removePropertyChangeListener(this);
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
			if(evt.getNewValue() == PuzzleStatus.SOLVED) {
				this.removeListeners();
			}
			break;
		case Cell.CELL_STATUS:
			this.stepTime((CellStatus) evt.getOldValue(), (CellStatus) evt.getNewValue());
			break;
		case Cell.CELL_VALUE:
			// Nothing?
			break;
		case "state": // SwingWorker.StateValue
			if(evt.getNewValue() == SwingWorker.StateValue.DONE) {
				this.removeListeners();
			}
			break;
		default:
			System.out.println("SolverWorker.propertyChange(): " + evt);
		}
		this.showTime();
	}

}
