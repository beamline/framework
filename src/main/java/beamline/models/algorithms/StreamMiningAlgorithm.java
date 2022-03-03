package beamline.models.algorithms;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * This abstract class defines the root of the mining algorithms hierarchy. It
 * is a {@link Consumer} of elements with type <code>T</code> that is capable of
 * producing responses of a certain type <code>K</code>.
 * 
 * @author Andrea Burattin
 *
 * @param <T> the type of the consumed events
 * @param <K> the type of the responses produced by the mining algorithm
 */
public abstract class StreamMiningAlgorithm<T, K> implements Consumer<T> {

	private int processedEvents = 0;
	private K latestResponse;
	private HookEventProcessing onBeforeEvent = null;
	private HookEventProcessing onAfterEvent = null;
	
	/**
	 * This abstract method is what each derive class is expected to implement.
	 * The argument of the method is the new observation and the returned value
	 * is the result of the mining.
	 * 
	 * @param event the new event being observed
	 * @return the result of the mining of the event
	 */
	public abstract K ingest(T event);
	
	/**
	 * Returns the total number of events processed so far
	 * 
	 * @return the total number of events processed so far
	 */
	public int getProcessedEvents() {
		return processedEvents;
	}
	
	/**
	 * Returns the latest result of the mining
	 * 
	 * @return the latest result of the mining
	 */
	public K getLatestResponse() {
		return latestResponse;
	}
	
	/**
	 * This method can be used to set a hook to a callback function to be
	 * executed before an event is processed
	 * 
	 * @param onBeforeEvent the callback function
	 */
	public void setOnBeforeEvent(HookEventProcessing onBeforeEvent) {
		this.onBeforeEvent = onBeforeEvent;
	}
	
	/**
	 * This method can be used to set a hook to a callback function to be
	 * executed after an event is processed
	 * 
	 * @param onAfterEvent the callback function
	 */
	public void setOnAfterEvent(HookEventProcessing onAfterEvent) {
		this.onAfterEvent = onAfterEvent;
	}
	
	protected void process(T event) {
		this.processedEvents++;
		latestResponse = ingest(event);
	}
	
	protected K setLatestResponse(K latestResponse) {
		this.latestResponse = latestResponse;
		return latestResponse;
	}
	
	@Override
	public void accept(@NonNull T t) throws Throwable {
		if (onBeforeEvent != null) {
			onBeforeEvent.trigger();
		}
		process(t);
		if (onAfterEvent != null) {
			onAfterEvent.trigger();
		}
	}
}
