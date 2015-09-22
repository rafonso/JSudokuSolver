package jsudokusolver.javafx;

import java.util.regex.Pattern;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class SudokuTextField extends TextField {

	private static final Pattern PATTERN = Pattern.compile("^[0-9 ]$");

	private final int row;

	private final int column;

	public SudokuTextField(int row, int column) {
		super();
		this.row = row;
		this.column = column;

		super.setId("cell" + row + column);
		super.focusedProperty()
				.addListener((ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean isReceivingFocus) -> {
					if (isReceivingFocus) {
						SudokuTextField.this.selectAll();
					}
				});
	}

	private String getFinalText(String text) {
		char ch = text.charAt(0);
		return ch > '0' ? String.valueOf(ch) : " ";
	}

	@Override
	public void replaceText(int start, int end, String insertedText) {
		System.out.println(super.getId() +  ".replaceText(" + start + ", " + end + ", '" + insertedText + "')");
		if ((start == 0) && PATTERN.matcher(insertedText).matches()) {
			super.replaceText(start, end, this.getFinalText(insertedText));
		}
	}

	@Override
	public void replaceSelection(String replacement) {
		System.out.println(super.getId() +  ".replaceSelection('" + replacement +"')");
		if (PATTERN.matcher(replacement).matches()) {
			super.replaceSelection(this.getFinalText(replacement));
		}
	}

}
