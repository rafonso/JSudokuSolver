package jsudokusolver.swing;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import jsudokusolver.core.Puzzle;
import jsudokusolver.core.Puzzle.CellsFormatter;
import jsudokusolver.core.PuzzleStatus;

public class PuzzleFormatParserListener extends KeyAdapter implements ClipboardOwner {

	private static final String INPUT_MESSAGE = "Enter the puzzle. 1 to 9 for filled Cells. "
			+ "0 for empty Cells. Dots(.) are optionals";
	private static final String INPUT_TILE = "Input Puzzle";

	private final Puzzle puzzle;

	public PuzzleFormatParserListener(Puzzle puzzle) {
		this.puzzle = puzzle;
	}

	private void exportCells(Component component, String title, final CellsFormatter cellsFormatter) {
		final String cells = puzzle.formatCells(cellsFormatter);

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(cells), this);

		String dialogTitle = title + " Exported to the Clipboard.";
		JOptionPane.showMessageDialog(component, cells, dialogTitle, JOptionPane.INFORMATION_MESSAGE);
	}

	private void importCells(Component component) {
		boolean importingComplete = false;
		String cells = null;
		do {
			cells = ((String) JOptionPane.showInputDialog(component, INPUT_MESSAGE, INPUT_TILE,
					JOptionPane.PLAIN_MESSAGE, null, null, cells));
			if ((cells == null) || cells.trim().isEmpty()) {
				importingComplete = true;
			} else {
				try {
					this.puzzle.cleanCells();
					this.puzzle.parse(cells.trim());
					importingComplete = true;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(component, e.getMessage(), "Parser Error!",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} while (!importingComplete);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (!e.isControlDown()) {
			return;
		}

		if (e.getKeyCode() == KeyEvent.VK_E && e.isShiftDown()) {
			exportCells(e.getComponent(), "All Puzzle Cells.", CellsFormatter.ALL);
		} else if (e.getKeyCode() == KeyEvent.VK_E && !e.isShiftDown()) {
			exportCells(e.getComponent(), "The Original Puzzle Cells.", CellsFormatter.ORIGINALS);
		} else if (e.getKeyCode() == KeyEvent.VK_I && puzzle.getStatus() != PuzzleStatus.RUNNING) {
			this.importCells(e.getComponent());
		}
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub

	}

}