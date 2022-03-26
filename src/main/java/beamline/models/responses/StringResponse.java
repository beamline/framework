package beamline.models.responses;

/**
 * A simple {@link Response} containing just a string
 * 
 * @author Andrea Burattin
 */
public class StringResponse extends Response {

	private static final long serialVersionUID = 7863665787098981704L;
	private String str;
	
	/**
	 * Constructor
	 * 
	 * @param str the value of this response
	 */
	public StringResponse(String str) {
		set(str);
	}
	
	/**
	 * Gets the string of this response
	 * 
	 * @return the string of this response
	 */
	public String get() {
		return str;
	}
	
	/**
	 * Sets the string of this response
	 * 
	 * @param str the string of this response
	 */
	public void set(String str) {
		this.str = str;
	}
	
	@Override
	public String toString() {
		return str;
	}
}
