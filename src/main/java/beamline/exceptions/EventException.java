package beamline.exceptions;

/**
 * Exception thrown during events creation
 * 
 * @author Andrea Burattin
 */
public class EventException extends Exception {

	private static final long serialVersionUID = 5835305478001040595L;
	
	/**
	 * Constructs a new exception
	 * 
	 * @param message the message
	 */
	public EventException(String message) {
		super(message);
	}
}
