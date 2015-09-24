package jsudokusolver.javafx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jsudokusolver.core.Cell;

public class JFXSudokuSolver extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent parent = FXMLLoader.load(getClass().getResource("JFXSudokuSolver.fxml"));
		Scene scene = new Scene(parent);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("JavaFX Sudoku Solver");

		GridPane pnlCells = (GridPane) scene.lookup("#pnlCells");
		System.out.println("JFXSudokuSolver.start()" + pnlCells);

		for (int i = 0; i < 81; i++) {
			int[] positions = Cell.valueToPositions(i);
			TextField txfCell = new SudokuTextField(positions[0], positions[1]);
			pnlCells.add(txfCell, positions[1] - 1, positions[0] - 1);
		}

		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
