package jsudokusolver.javafx;

import java.util.Optional;

import javafx.util.StringConverter;

public class CellValueStringConverter extends StringConverter<Optional<Integer>> {

	@Override
	public String toString(Optional<Integer> value) {
		return value.isPresent()? String.valueOf(value.get()): "";
	}

	@Override
	public Optional<Integer> fromString(String string) {
		if(string.matches("^[1-9]$")) {
			return Optional.of(Integer.valueOf(string));
		}
		if(string.isEmpty() || string.matches("^( |0)$")) {
			return Optional.empty();
		}

		throw new IllegalArgumentException("Illegal value for a Cell: " + string);
	}

}
