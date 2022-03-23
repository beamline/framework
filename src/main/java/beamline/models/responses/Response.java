package beamline.models.responses;

import java.io.Serializable;

/**
 * Marker interface used to define the type of the responses
 * 
 * @author Andrea Burattin
 */
public class Response implements Serializable {

	private static final long serialVersionUID = 3104314256198741100L;
	private Long processedEvents;

	public Long getProcessedEvents() {
		return processedEvents;
	}

	public void setProcessedEvents(Long processedEvents) {
		this.processedEvents = processedEvents;
	}
	
	
}
