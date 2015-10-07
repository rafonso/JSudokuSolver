package jsudokusolver.javafx;

import java.util.Objects;
import java.util.Optional;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import jsudokusolver.core.Puzzle;
import jsudokusolver.core.Puzzle.CellsFormatter;
import jsudokusolver.core.PuzzleStatus;
import jsudokusolver.core.exception.ParserException;

public class PuzzleFormatParserEventHandler implements EventHandler<KeyEvent> {

	private final Puzzle puzzle;

	public PuzzleFormatParserEventHandler(Puzzle puzzle) {
		this.puzzle = puzzle;
	}

	private void exportCells(String title, final CellsFormatter cellsFormatter) {
		final String cells = this.puzzle.formatCells(cellsFormatter);

		ClipboardContent content = new ClipboardContent();
		content.putString(cells);
		Clipboard.getSystemClipboard().setContent(content);

		String dialogTitle = title + ' ' + "Exported to the Clipboard.";
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Sudoku Solver");
		alert.setHeaderText(dialogTitle);
		alert.setContentText(cells);
		alert.show();
	}

	private void importCells() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Input Puzzle");
		dialog.setHeaderText("Enter the puzzle. 1 to 9 for filled Cells. 0 for empty Cells. Dots(.) are optionals");
		dialog.getEditor().addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
			if (!keyEvent.getCharacter().matches("[0-9.]")) {
				keyEvent.consume();
			}
		});

		boolean importingComplete = false;
		Optional<String> cells = Optional.empty();
		do {
			dialog.setResult(cells.orElse(""));
			cells = dialog.showAndWait();
			if (cells.isPresent()) {
				try {
					this.puzzle.cleanCells();
					this.puzzle.parse(cells.get().trim());
					importingComplete = true;
				} catch (ParserException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Input Puzzle");
					alert.setHeaderText("Parser Error!");
					alert.setContentText(
							"Invalid Puzzle. It must have 81 Digits. " + Objects.toString(e.getMessage(), ""));
					alert.showAndWait();
				}
			} else {
				importingComplete = true;
			}
		} while (!importingComplete);
	}

	@Override
	public void handle(KeyEvent e) {
		if (!e.isControlDown()) {
			return;
		}

		if ((e.getCode() == KeyCode.E) && e.isShiftDown()) {
			exportCells("All Puzzle Cells.", CellsFormatter.ALL);
		} else if ((e.getCode() == KeyCode.E) && !e.isShiftDown()) {
			exportCells("The Original Puzzle Cells.", CellsFormatter.ORIGINALS);
		} else if ((e.getCode() == KeyCode.I) && (this.puzzle.getStatus() != PuzzleStatus.RUNNING)) {
			this.importCells();
		}
	}

}
