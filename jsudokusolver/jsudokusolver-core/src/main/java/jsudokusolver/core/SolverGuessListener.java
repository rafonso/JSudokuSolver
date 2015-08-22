package jsudokusolver.core;

import java.util.EventListener;

public interface SolverGuessListener extends EventListener {

	void guessAction(SolverGuessEvent event);

}
