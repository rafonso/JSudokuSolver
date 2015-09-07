package jsudokusolver.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;

import jsudokusolver.core.InvalidPuzzleException;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;
import jsudokusolver.core.RepeatedCellsException;
import jsudokusolver.core.Solver;
import jsudokusolver.core.Validator;
import jsudokusolver.swing.PanelControls.ButtonToShow;

public class PuzzlePanelControlsListener implements PropertyChangeListener, ActionListener, ItemListener {

	private final Puzzle puzzle;

	private final PanelControls panelControls;

	private long t0;

	PuzzlePanelControlsListener(Puzzle puzzle, PanelControls panelControls) {
		super();
		this.puzzle = puzzle;
		this.panelControls = panelControls;

		this.puzzle.addPropertyChangeListener(this);
		this.panelControls.getBtnClean().addActionListener(this);
		this.panelControls.getBtnReset().addActionListener(this);
		this.panelControls.getBtnRun().addActionListener(this);
		this.panelControls.getBtnStop().addActionListener(this);
		this.panelControls.getCmbStepTime().addItemListener(this);
	}

	private void startSolver() {
		try {
			new Validator().validate(puzzle);
			Solver solver = new Solver();
			solver.addPropertyChangeListener(this);
			this.t0 = System.currentTimeMillis();
			solver.start(puzzle);
		} catch (RepeatedCellsException e) {
			String msg = String.format("Repeated value %d in %s %d, cells [%d,%d] and [%d,%d]", e.getRepeatedValue(),
					e.getPuzzlePositions().getDescription(), e.getPosition(), e.getCell1().getRow(),
					e.getCell1().getColumn(), e.getCell2().getRow(), e.getCell2().getColumn());
			JOptionPane.showMessageDialog(this.panelControls, msg, "Puzzle Error!", JOptionPane.ERROR_MESSAGE);
		} catch (InvalidPuzzleException e) {
			JOptionPane.showMessageDialog(this.panelControls, e.getMessage(), "Puzzle Error!",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void enableControls(boolean run, boolean clean, boolean stop, boolean reset, ButtonToShow buttonToShow) {
		this.panelControls.getBtnRun().setEnabled(run);
		this.panelControls.getBtnClean().setEnabled(clean);
		this.panelControls.getBtnStop().setEnabled(stop);
		this.panelControls.getBtnReset().setEnabled(reset);
		this.panelControls.showButton(buttonToShow);
	}

	private void puzzleStatusChanged(PuzzleStatus newStatus) {
		switch (newStatus) {
		case INVALID:
		case READY:
		case VALIDATING:
		case WAITING:
			this.enableControls(true, true, false, false, ButtonToShow.STOP);
			this.panelControls.showState(false);
			break;
		case RUNNING:
			this.enableControls(false, false, true, false, ButtonToShow.STOP);
			this.panelControls.showState(true);
			this.panelControls.getLblCycles().setText("0");
			break;
		case SOLVED:
			this.enableControls(false, true, false, true, ButtonToShow.RESET);
			break;
		case STOPPED:
			this.enableControls(true, true, false, true, ButtonToShow.RESET);
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.panelControls.getBtnClean()) {
			this.puzzle.cleanCells();
		} else if (e.getSource() == this.panelControls.getBtnReset()) {
			this.puzzle.reset();
		} else if (e.getSource() == this.panelControls.getBtnRun()) {
			this.startSolver();
		} else if (e.getSource() == this.panelControls.getBtnStop()) {
			this.puzzle.setStatus(PuzzleStatus.STOPPED);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Puzzle.PUZZLE_STATUS)) {
			puzzleStatusChanged((PuzzleStatus) evt.getNewValue());
		} else if (evt.getPropertyName().equals(Solver.SOLVER_CYCLE)) {
			this.panelControls.getLblCycles().setText(evt.getNewValue().toString());
		}
		this.panelControls.getLblTime().setText(String.valueOf(System.currentTimeMillis() - t0));
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			System.out.println("PuzzlePanelControlsListener.itemStateChanged(): " + e.getItem());
		}
	}

}
