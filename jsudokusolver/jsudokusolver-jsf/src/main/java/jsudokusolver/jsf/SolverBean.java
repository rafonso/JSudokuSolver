package jsudokusolver.jsf;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class SolverBean implements Serializable {

	private static Logger LOGGER = Logger.getLogger("InfoLogging");

	private static final long serialVersionUID = 3591119137178469859L;

	private static final int[] stepTimes = new int[] { 0, 1, 5, 10, 50, 100, 500, 1000 };

	@PostConstruct
	public void init() {
		LOGGER.info("Inicializando Bean");
	}

	public Date getAgora() {
		return new Date();
	}
	
	public int[] getSteptimes() {
		return stepTimes;
	}

}
