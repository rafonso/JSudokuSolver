package jsudokusolver.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;

import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;
import jsudokusolver.core.Validator;
import jsudokusolver.core.exception.EmptyPuzzleException;
import jsudokusolver.core.exception.RepeatedCellsException;

public class PuzzlePanelControlsListener implements PropertyChangeListener, ActionListener {

	private final Puzzle puzzle;

	private final PanelControls panelControls;

	private SolverWorker solverWorker;

	PuzzlePanelControlsListener(Puzzle puzzle, PanelControls panelControls) {
		super();
		this.puzzle = puzzle;
		this.panelControls = panelControls;

		this.puzzle.addPropertyChangeListener(this);
		this.panelControls.getBtnClean().addActionListener(this);
		this.panelControls.getBtnReset().addActionListener(this);
		this.panelControls.getBtnRun().addActionListener(this);
		this.panelControls.getBtnStop().addActionListener(this);
	}

	private void startSolver() {
		try {
			new Validator().validate(puzzle);

			this.solverWorker = new SolverWorker(puzzle, this.panelControls.getCmbStepTime(),
					this.panelControls.getLblCycles(), this.panelControls.getLblTime());
			this.solverWorker.execute();
		} catch (RepeatedCellsException e) {
			String msg = Messages.getString("puzzle.error.repeated_cells", e.getRepeatedValue(),
					Messages.getString("PuzzlePositions." + e.getPuzzlePositions()), e.getPosition(), //
					e.getCell1().getRow(), e.getCell1().getColumn(), //
					e.getCell2().getRow(), e.getCell2().getColumn());
			JOptionPane.showMessageDialog(this.panelControls, msg, Messages.getString("puzzle.error.title"),
					JOptionPane.ERROR_MESSAGE);
		} catch (EmptyPuzzleException e) {
			JOptionPane.showMessageDialog(this.panelControls, Messages.getString("puzzle.error.empty_puzzle"),
					Messages.getString("puzzle.error.title"), JOptionPane.ERROR_MESSAGE);
		}
	}

	private void puzzleStatusChanged(PuzzleStatus newStatus) {
		switch (newStatus) {
		case RUNNING:
			this.panelControls.enableControls(false, false, true, false, true, true);
			break;
		case SOLVED:
			this.panelControls.enableControls(false, true, false, true, true, false);
			break;
		case STOPPED:
			this.solverWorker.cancel(true);
			this.panelControls.enableControls(false, true, false, true, true, false);
			break;
		case ERROR:
			this.panelControls.enableControls(false, true, false, false, true, false);
			break;
		default:
			this.panelControls.enableControls(true, true, false, false, false, true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(Messages.getString("btnClean.title"))) {
			this.puzzle.cleanCells();
		} else if (e.getActionCommand().equals(Messages.getString("btnReset.title"))) {
			this.puzzle.reset();
		} else if (e.getActionCommand().equals(Messages.getString("btnRun.title"))) {
			this.startSolver();
		} else if (e.getActionCommand().equals(Messages.getString("btnStop.title"))) {
			this.puzzle.setStatus(PuzzleStatus.STOPPED);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Puzzle.PUZZLE_STATUS)) {
			puzzleStatusChanged((PuzzleStatus) evt.getNewValue());
		}
	}

}
