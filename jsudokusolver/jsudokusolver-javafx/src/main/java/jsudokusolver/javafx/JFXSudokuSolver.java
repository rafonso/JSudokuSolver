package jsudokusolver.javafx;

import java.io.IOException;

import javafx.application.Application;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jsudokusolver.core.Cell;

public class JFXSudokuSolver extends Application {

	private void addAcceleratorToButton(Scene scene, String btnName, KeyCode keyCode) {
		final ObservableMap<KeyCombination, Runnable> accelerators = scene.getAccelerators();
		accelerators.put(new KeyCodeCombination(keyCode, KeyCombination.ALT_DOWN),
				() -> ((Button)scene.lookup("#" + btnName)).fire());
	}

	
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("JFXSudokuSolver.fxml"));
		Parent parent = loader.load();
		Scene scene = new Scene(parent);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image("/icons/jfxsudokusolver.png"));
		primaryStage.setTitle("JavaFX Sudoku Solver");

		GridPane pnlCells = (GridPane) scene.lookup("#pnlCells");
		for (int i = 0; i < 81; i++) {
			int[] positions = Cell.valueToPositions(i);
			TextField txfCell = new SudokuTextField(positions[0], positions[1]);
			pnlCells.add(txfCell, positions[1] - 1, positions[0] - 1);
		}

		this.addAcceleratorToButton(scene, "btnClean", KeyCode.C);
		this.addAcceleratorToButton(scene, "btnReset", KeyCode.T);
		this.addAcceleratorToButton(scene, "btnRun", KeyCode.R);
		this.addAcceleratorToButton(scene, "btnStop", KeyCode.S);

		loader.<SudokuSolverController>getController().init();
		
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
