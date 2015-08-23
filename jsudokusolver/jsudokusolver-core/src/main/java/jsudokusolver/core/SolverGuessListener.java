package jsudokusolver.core;

import java.util.EventListener;

/**
 * Listens a {@link SolverGuessEvent}
 *
 */
public interface SolverGuessListener extends EventListener {

	/**
	 * A {@link SolverGuessEvent} happened.
	 * 
	 * @param event
	 *            the {@link SolverGuessEvent} happened.
	 */
	void guessAction(SolverGuessEvent event);

}
