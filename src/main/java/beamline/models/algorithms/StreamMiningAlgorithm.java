package beamline.models.algorithms;

import beamline.models.responses.Response;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Consumer;

public abstract class StreamMiningAlgorithm<T, K extends Response> implements Consumer<T> {

	private int processedEvents = 0;
	private K latestResponse;
	private HookEventProcessing onBeforeEvent = null;
	private HookEventProcessing onAfterEvent = null;
	
	public abstract K ingest(T event);
	
	public void process(T event) {
		this.processedEvents++;
		latestResponse = ingest(event);
	}
	
	public int getProcessedEvents() {
		return processedEvents;
	}
	
	public K getLatestResponse() {
		return latestResponse;
	}
	
	public void setOnBeforeEvent(HookEventProcessing onBeforeEvent) {
		this.onBeforeEvent = onBeforeEvent;
	}
	
	public void setOnAfterEvent(HookEventProcessing onAfterEvent) {
		this.onAfterEvent = onAfterEvent;
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
