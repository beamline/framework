package beamline.sources;

import beamline.events.BEvent;

/**
 * Source useful for designing new algorithm. It allows to specify sequences of
 * events directly in the constructor.
 * 
 * <p>
 * Example of usage:
 * <pre>
 * StringTestSource s = new StringTestSource("ABC", "ADCE");
 * </pre>
 * This is going to emit 7 events as part of 2 traces. Each trace is a string
 * provided in the constructor and each event is one character of the string.
 * 
 * @author Andrea Burattin
 */
public class StringTestSource extends BeamlineAbstractSource {

	private static final long serialVersionUID = 7657971352128040279L;
	private String[] traces;
	
	/**
	 * Constructs the source by providing the strings representing the events to
	 * emit
	 * 
	 * @param traces one string for each trace, where each character is an event
	 */
	public StringTestSource(String...traces) {
		this.traces = traces;
	}

	@Override
	public void run(SourceContext<BEvent> ctx) throws Exception {
		for (int j = 0; j < traces.length; j++) {
			for (int i = 0; i < traces[j].length(); i++) {
				ctx.collect(new BEvent("test-process", "case-"+j, traces[j].substring(i, i+1)));
			}
		}
	}
}
