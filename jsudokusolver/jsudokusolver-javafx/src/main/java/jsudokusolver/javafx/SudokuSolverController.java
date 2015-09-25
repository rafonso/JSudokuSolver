package jsudokusolver.javafx;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class SudokuSolverController implements Initializable {

	@FXML
	private Button btnRun;

	@FXML
	private Button btnClean;

	@FXML
	private Button btnStop;

	@FXML
	private Button btnReset;

	@FXML
	private StackPane pnlStopReset;

	@FXML
	private ChoiceBox<Integer> chbStepTime;

	@FXML
	private Label lblTimeMs;

	@FXML
	private Label lblCycles;

	private BooleanProperty stopVisible = new SimpleBooleanProperty(true);

	private boolean disableBtns = true;

	public SudokuSolverController() {
		System.out.println("SudokuSolverController.SudokuSolverController() Iniciando o controle");
	}

	@FXML
	public void runPressed() {
		System.out.println("SudokuSolverController.runPressed()");

		this.stopVisible.set(!this.stopVisible.get());
	}

	@FXML
	public void cleanPressed() {
		System.out.println("SudokuSolverController.cleanPressed()");

		disableBtns = !disableBtns;
		this.btnStop.setDisable(disableBtns);
		this.btnReset.setDisable(disableBtns);
	}

	@FXML
	public void stopPressed() {
		System.out.println("SudokuSolverController.stopPressed()");
	}

	@FXML
	public void resetPressed() {
		System.out.println("SudokuSolverController.resetPressed()");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.chbStepTime.setValue(0);
		this.chbStepTime.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("SudokuSolverController.initialize(): " + oldValue + " -> " + newValue);
		});

		this.stopVisible.addListener((observable, oldValue, showStop) -> {
			this.btnStop.setVisible(showStop);
			this.btnReset.setVisible(!showStop);
		});

	}

}
