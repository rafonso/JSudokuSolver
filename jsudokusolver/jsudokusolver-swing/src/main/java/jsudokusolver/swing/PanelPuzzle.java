package jsudokusolver.swing;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Function;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class PanelPuzzle extends JPanel implements KeyListener {

	private static final long serialVersionUID = -6150165597619678264L;

	private Function<SudokuTextField, Integer> gotoNextPosition = (txf -> (txf.getPosition() + 1) % 81);
	private Function<SudokuTextField, Integer> gotoPrevPosition = (txf -> (txf.getPosition() > 0)
			? (txf.getPosition() - 1) : 80);
	private Function<SudokuTextField, Integer> gotoDownPosition = (txf -> (txf.getRow() < 9) ? (txf.getPosition() + 9)
			: (txf.getCol()) % 9);
	private Function<SudokuTextField, Integer> gotoUperPosition = (txf -> (txf.getRow() > 1) ? (txf.getPosition() - 9)
			: ((txf.getPosition() == 0) ? 80 : 71 + (txf.getCol() - 1)));

	/**
	 * Create the panel.
	 */
	public PanelPuzzle() {
		initialize();
	}

	private JTextField createCell(int row, int col) {
		SudokuTextField txtCell = new SudokuTextField(row, col);
		txtCell.addKeyListener(this);

		return txtCell;
	}

	private void initialize() {
		setLayout(new GridLayout(9, 9, 0, 0));

		for (int row = 1; row < 10; row++) {
			for (int col = 1; col < 10; col++) {
				this.add(this.createCell(row, col));
			}
		}
	}


	private void gotoCell(SudokuTextField textField, Function<SudokuTextField, Integer> action) {
		this.getComponent(action.apply(textField)).requestFocusInWindow();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		SudokuTextField textField = (SudokuTextField) e.getSource();
		switch (e.getKeyCode()) {
		case KeyEvent.VK_0:
		case KeyEvent.VK_NUMPAD0:
		case KeyEvent.VK_SPACE:
			textField.clean();
			break;
		case KeyEvent.VK_BACK_SPACE:
			textField.clean();
			this.gotoCell(textField, this.gotoPrevPosition);
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_KP_RIGHT:
			this.gotoCell(textField, this.gotoNextPosition);
			break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_KP_LEFT:
			this.gotoCell(textField, this.gotoPrevPosition);
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_KP_UP:
			this.gotoCell(textField, this.gotoUperPosition);
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_KP_DOWN:
			this.gotoCell(textField, this.gotoDownPosition);
			break;

		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		SudokuTextField textField = (SudokuTextField) e.getSource();
		switch (e.getKeyCode()) {
		case KeyEvent.VK_0:
		case KeyEvent.VK_1:
		case KeyEvent.VK_2:
		case KeyEvent.VK_3:
		case KeyEvent.VK_4:
		case KeyEvent.VK_5:
		case KeyEvent.VK_6:
		case KeyEvent.VK_7:
		case KeyEvent.VK_8:
		case KeyEvent.VK_9:
		case KeyEvent.VK_NUMPAD0:
		case KeyEvent.VK_NUMPAD1:
		case KeyEvent.VK_NUMPAD2:
		case KeyEvent.VK_NUMPAD3:
		case KeyEvent.VK_NUMPAD4:
		case KeyEvent.VK_NUMPAD5:
		case KeyEvent.VK_NUMPAD6:
		case KeyEvent.VK_NUMPAD7:
		case KeyEvent.VK_NUMPAD8:
		case KeyEvent.VK_NUMPAD9:
		case KeyEvent.VK_SPACE:
			this.gotoCell(textField, this.gotoNextPosition);
			break;
		default:
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Do nothing ...
	}

	public void cleanAll() {
		for (int i = 0; i < 81; i++) {
			((SudokuTextField) super.getComponent(i)).clean();
		}
	}

}
