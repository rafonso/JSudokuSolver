package jsudokusolver.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

import javax.swing.JOptionPane;

import jsudokusolver.core.Cell;
import jsudokusolver.core.CellStatus;
import jsudokusolver.core.InvalidPuzzleException;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.PuzzleStatus;
import jsudokusolver.core.RepeatedCellsException;
import jsudokusolver.core.Validator;
import jsudokusolver.swing.PanelControls.ButtonToShow;

public class PuzzlePanelControlsListener implements PropertyChangeListener, ActionListener, ItemListener {

	private final Puzzle puzzle;

	private final PanelControls panelControls;

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

		this.panelControls.addMouseListener(new MouseAdapter() {
			// Just to simulate cell filling while running
			Random random = new Random();

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					boolean fill = false;
					while (!fill) {
						Cell c = PuzzlePanelControlsListener.this.puzzle.getCell(random.nextInt(9) + 1,
								random.nextInt(9) + 1);
						if (!c.hasValue()) {
							c.setValueStatus(random.nextInt(9) + 1, CellStatus.FILLED);
							fill = true;
						}
					}
				}
			}
		});
	}

	private void startSolver() {
		try {
			new Validator().validate(puzzle);
			puzzle.setStatus(PuzzleStatus.RUNNING);
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

	@Override
	public void actionPerformed(ActionEvent e) {
//		final JComponent jComponent = (JComponent) e.getSource();
//		System.out.println("PuzzlePanelControlsListener.actionPerformed(): " + jComponent.getName());
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
		if (!evt.getPropertyName().equals(Puzzle.PUZZLE_STATUS)) {
			return;
		}

		PuzzleStatus newStatus = (PuzzleStatus) evt.getNewValue();
		switch (newStatus) {
		case INVALID:
		case READY:
		case VALIDATING:
		case WAITING:
			this.panelControls.getBtnRun().setEnabled(true);
			this.panelControls.getBtnClean().setEnabled(true);
			this.panelControls.getBtnStop().setEnabled(false);
			this.panelControls.getBtnReset().setEnabled(false);
			this.panelControls.showButton(ButtonToShow.STOP);
			break;
		case RUNNING:
			this.panelControls.getBtnRun().setEnabled(false);
			this.panelControls.getBtnClean().setEnabled(false);
			this.panelControls.getBtnStop().setEnabled(true);
			this.panelControls.getBtnReset().setEnabled(false);
			this.panelControls.showButton(ButtonToShow.STOP);
			break;
		case SOLVED:
			this.panelControls.getBtnRun().setEnabled(false);
			this.panelControls.getBtnClean().setEnabled(true);
			this.panelControls.getBtnStop().setEnabled(false);
			this.panelControls.getBtnReset().setEnabled(true);
			this.panelControls.showButton(ButtonToShow.RESET);
			break;
		case STOPPED:
			this.panelControls.getBtnRun().setEnabled(true);
			this.panelControls.getBtnClean().setEnabled(true);
			this.panelControls.getBtnStop().setEnabled(false);
			this.panelControls.getBtnReset().setEnabled(true);
			this.panelControls.showButton(ButtonToShow.RESET);
			break;
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			System.out.println("PuzzlePanelControlsListener.itemStateChanged(): " + e.getItem());
		}
	}

}
