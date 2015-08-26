package jsudokusolver.swing;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PanelPuzzle extends JPanel implements KeyListener {

	private static final long serialVersionUID = -6150165597619678264L;

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

	@Override
	public void keyPressed(KeyEvent e) {
		SudokuTextField textField = (SudokuTextField) e.getSource();
		System.out.println(textField.getName() + " - Pressed : " + e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		SudokuTextField textField = (SudokuTextField) e.getSource();
		System.out.println(textField.getName() + " - Released: " + e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		SudokuTextField textField = (SudokuTextField) e.getSource();
		System.out.println(textField.getName() + " - Typed   : " + e);
	}

}
