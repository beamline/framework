package beamline.sources;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import beamline.events.BEvent;

/**
 * This class represents the general source to generate {@link BEvent}s. The
 * goal of the class is to bind the type of the underlying
 * {@link RichSourceFunction} to {@link BEvent} as well as provide basic
 * management for running/not running.
 * 
 * <p>
 * Since the source is a "rich" one, it means that it is possible to access the
 * state from within all sources derived from this one.
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