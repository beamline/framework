package beamline.sources;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import beamline.events.BEvent;

/**
 * This interface is supposed to bind the type of {@link RichSourceFunction} to
 * {@link BEvent} and to provide minimal infrastructure to check if the sourece
 * is currently running.
 * 
 * @author Andrea Burattin
 */
public abstract class BeamlineAbstractSource extends RichSourceFunction<BEvent> {

	private static final long serialVersionUID = 1072198158533070679L;
	private boolean running = true;
	
	/**
	 * Returns if the source is still generting events
	 *
	 * @return whether the current source is still running or not
	 */
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public void cancel() {
		this.running = false;
	}
}