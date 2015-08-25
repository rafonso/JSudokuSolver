package jsudokusolver.swing;

import java.awt.GridLayout;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PanelPuzzle extends JPanel {

	private static final long serialVersionUID = -6150165597619678264L;

	/**
	 * Create the panel.
	 */
	public PanelPuzzle() {
		initialize();
	}

	private JTextField createCell(int row, int col) {
		JFormattedTextField txtCell = new JFormattedTextField();
		txtCell.setColumns(1);
		txtCell.setName("cell" + row + col);
		txtCell.setHorizontalAlignment(JTextField.CENTER);
//		txtCell.set

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

}
