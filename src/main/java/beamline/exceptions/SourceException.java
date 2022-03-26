package beamline.exceptions;

/**
 * Exception thrown during source processing
 * 
 * @author Andrea Burattin
 */
public class SourceException extends Exception {

	private static final long serialVersionUID = -3387719059778550013L;

	/**
	 * Constructs a new exception
	 * 
	 * @param message the message
	 */
	public SourceException(String message) {
		super(message);
	}
}
