package beamline.sources;

import beamline.events.BEvent;

public class StringTestSource extends BeamlineAbstractSource {

	private static final long serialVersionUID = 7657971352128040279L;
	private String[] traces;
	
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
