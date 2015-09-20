package jsudokusolver.javafx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JFXSudokuSolver extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent parent = FXMLLoader.load(getClass().getResource("JFXSudokuSolver.fxml"));
		Scene scene = new Scene(parent);
		primaryStage.setScene(scene);
		primaryStage.setTitle("JavaFX Sudoku Solver");

		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
