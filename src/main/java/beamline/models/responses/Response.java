package beamline.models.responses;

import java.io.Serializable;

import beamline.models.algorithms.StreamMiningAlgorithm;

/**
 * General class describing what a {@link StreamMiningAlgorithm} is supposed to
 * return. This class can be extended with additional information. The class by
 * itself carries only information regarding the number of events processed.
 * 
 * @author Andrea Burattin
 */
public class Response implements Serializable {

	private static final long serialVersionUID = 3104314256198741100L;
	private Long processedEvents;

	/**
	 * Gets the number of events processed when the response was produced
	 * 
	 * @return number of events processed when the response was produced
	 */
	public Long getProcessedEvents() {
		return processedEvents;
	}

	/**
	 * Sets the number of events processed when the response was produced
	 * 
	 * @param processedEvents the number of events processed when the response
	 * was produced
	 */
	public void setProcessedEvents(Long processedEvents) {
		this.processedEvents = processedEvents;
	}
	
	
}
