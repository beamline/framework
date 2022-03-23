package beamline.models.responses;

public class StringResponse extends Response {

	private static final long serialVersionUID = 7863665787098981704L;
	private String str;
	
	public StringResponse(String str) {
		set(str);
	}
	
	public String get() {
		return str;
	}
	
	public void set(String str) {
		this.str = str;
	}
	
	@Override
	public String toString() {
		return str;
	}
}
