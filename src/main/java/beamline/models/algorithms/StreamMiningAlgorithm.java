package beamline.models.algorithms;

import java.util.Iterator;

import beamline.models.responses.Response;
import beamline.models.streams.ObservableStream;

public abstract class StreamMiningAlgorithm<T, K extends Response> implements ObservableStream<K> {

	private int processedEvents = 0;
	private K latestResponse;
	
	protected abstract K ingest(T event);
	
	public K process(T event) {
		this.processedEvents++;
		latestResponse = ingest(event);
		return latestResponse;
	}
	
	public int getProcessedEvents() {
		return processedEvents;
	}
	
	public K getLatestResponse() {
		return latestResponse;
	}
	
	protected K setLatestResponse(K latestResponse) {
		this.latestResponse = latestResponse;
		return latestResponse;
	}
	
	@Override
	public void prepare() throws Exception { }
	
	@Override
	public Iterator<K> iterator() {
		return null;
	}
}
