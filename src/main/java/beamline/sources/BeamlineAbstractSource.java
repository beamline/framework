package beamline.sources;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import beamline.events.BEvent;

/**
 * This interface is supposed just to bind the type of {@link SourceFunction} to
 * {@link BEvent}.
 * 
 * @author Andrea Burattin
 */
public abstract class BeamlineAbstractSource extends RichSourceFunction<BEvent> {

	private static final long serialVersionUID = 1072198158533070679L;
	private boolean running = true;
	
	/**
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public void cancel() {
		running = false;
	}
}