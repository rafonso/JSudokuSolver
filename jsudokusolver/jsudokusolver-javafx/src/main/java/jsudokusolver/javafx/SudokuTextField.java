package jsudokusolver.javafx;

import java.util.function.Function;
import java.util.regex.Pattern;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class SudokuTextField extends TextField {

	private static final Pattern PATTERN = Pattern.compile("^[0-9 ]$");

	private static final Function<SudokuTextField, Integer> gotoNextPosition = (txf -> (txf.getPosition() + 1) % 81);
	private static final Function<SudokuTextField, Integer> gotoPrevPosition = (txf -> (txf.getPosition() > 0)
			? (txf.getPosition() - 1) : 80);
	private static final Function<SudokuTextField, Integer> gotoDownPosition = (txf -> (txf.row < 9)
			? (txf.getPosition() + 9) : (txf.column) % 9);
	private static final Function<SudokuTextField, Integer> gotoUperPosition = (txf -> (txf.row > 1)
			? (txf.getPosition() - 9) : ((txf.getPosition() == 0) ? 80 : 71 + (txf.column - 1)));

	private final int row;

	private final int column;

	private final int position;

	public SudokuTextField(int row, int col) {
		super();
		this.row = row;
		this.column = col;
		this.position = (row - 1) * 9 + col - 1;
		
		// Borders
		int top = (row == 1) ? 3 : 1;
		int left = (col == 1) ? 3: 1;
		int bottom = (row == 9) ? 3 : ((row == 3) || (row == 6)) ? 3 : 1;
		int right = (col == 9) ? 3 : ((col == 3) || (col == 6)) ? 3 : 1;
		super.setStyle(super.getStyle() + String.format("%n\t-fx-border-width: %d %d %d %d;", top, right, bottom, left));

		super.setId("cell" + row + col);
		super.focusedProperty().addListener(this::handleFocus);
		super.addEventFilter(KeyEvent.KEY_RELEASED, this::keyReleased);
		super.addEventFilter(KeyEvent.KEY_PRESSED, this::keyPressed);
		super.addEventFilter(MouseEvent.MOUSE_CLICKED, me -> {
			if(me.getClickCount() == 2) {
				System.out.println(
				getScene().getWidth() + " - " + getScene().getHeight());
			}
		});
	}

	private void handleFocus(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean isReceivingFocus) {
		if (isReceivingFocus) {
			SudokuTextField.this.selectAll();
		}
	}

	private void gotoCell(Function<SudokuTextField, Integer> action) {
		this.getParent().getChildrenUnmodifiable().get(action.apply(this)).requestFocus();
	}

	private void keyPressed(final KeyEvent keyEvent) {
		switch (keyEvent.getCode()) {
		case DIGIT0:
		case NUMPAD0:
		case SPACE:
			this.clear();
			this.gotoCell(gotoNextPosition);
			break;
		case BACK_SPACE:
			this.clear();
			this.gotoCell(gotoPrevPosition);
			break;
		case RIGHT:
		case KP_RIGHT:
			this.gotoCell(gotoNextPosition);
			break;
		case LEFT:
		case KP_LEFT:
			this.gotoCell(gotoPrevPosition);
			break;
		case UP:
		case KP_UP:
			this.gotoCell(gotoUperPosition);
			break;
		case DOWN:
		case KP_DOWN:
			this.gotoCell(gotoDownPosition);
			break;
		case TAB:
			break;
		default:
			System.out.println(this.getId() + ".keyPressed(): " + keyEvent.getCode() + " " + keyEvent.isConsumed());
			keyEvent.consume();
		}
	}

	private void keyReleased(final KeyEvent keyEvent) {
		switch (keyEvent.getCode()) {
		case DIGIT1:
		case DIGIT2:
		case DIGIT3:
		case DIGIT4:
		case DIGIT5:
		case DIGIT6:
		case DIGIT7:
		case DIGIT8:
		case DIGIT9:
		case NUMPAD1:
		case NUMPAD2:
		case NUMPAD3:
		case NUMPAD4:
		case NUMPAD5:
		case NUMPAD6:
		case NUMPAD7:
		case NUMPAD8:
		case NUMPAD9:
			this.gotoCell(gotoNextPosition);
			break;
		case TAB:
			break;
		default:
			System.out.println(this.getId() + ".keyReleased(): " + keyEvent.getCode() + " " + keyEvent.isConsumed());
			keyEvent.consume();
		}
	}

	private String getFinalText(String text) {
		char ch = text.charAt(0);
		return ch > '0' ? String.valueOf(ch) : "";
	}

	@Override
	public void replaceText(int start, int end, String insertedText) {
		System.out.println(super.getId() + ".replaceText(" + start + ", " + end + ", '" + insertedText + "')");
		if ((start == 0) && PATTERN.matcher(insertedText).matches()) {
			super.replaceText(start, end, this.getFinalText(insertedText));
		}
	}

	@Override
	public void replaceSelection(String replacement) {
		System.out.println(super.getId() + ".replaceSelection('" + replacement + "')");
		if (PATTERN.matcher(replacement).matches()) {
			super.replaceSelection(this.getFinalText(replacement));
		}
	}

	int getPosition() {
		return position;
	}

}
