package jsudokusolver.javafx;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
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
	private ComboBox<Integer> cmbStepTime;

	@FXML
	private Label lblTimeMs;

	@FXML
	private Label lblCycles;

	private BooleanProperty stopVisible = new SimpleBooleanProperty(true);

	private IntegerProperty stepTime = new SimpleIntegerProperty();

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
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("teste");
		alert.setHeaderText("Information Alert");
		String s ="123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789.123456789";
		alert.setContentText(s);
		alert.show();
	}

	@FXML
	public void resetPressed() {
		System.out.println("SudokuSolverController.resetPressed()");
		
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Test");
		dialog.setHeaderText("Enter some text, or use default value.");

		Optional<String> result = dialog.showAndWait();
		System.out.println("Text entered: " + result.get());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// this.cmbStepTime.setValue(0);
		this.cmbStepTime.setItems(FXCollections.observableArrayList(0, 1, 5, 10, 50, 100, 500, 1000));
		this.cmbStepTime.setValue(0);
		this.stepTime.bind(this.cmbStepTime.getSelectionModel().selectedItemProperty());
		this.stepTime.addListener((ov, oldValue, newValue) -> System.out.println("stepTime = " + newValue));

		this.btnStop.visibleProperty().bind(this.stopVisible);
		this.btnReset.visibleProperty().bind(this.stopVisible.not());
	}

	@FXML
	public void keyTyped(KeyEvent event) {
		System.out.println("SudokuSolverController.keyTyped(): " + event);
	}

}
