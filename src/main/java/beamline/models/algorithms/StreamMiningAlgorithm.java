package beamline.models.algorithms;

import java.io.IOException;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import beamline.events.BEvent;
import beamline.models.responses.Response;

/**
 * This abstract class defines the root of the mining algorithms hierarchy. It
 * is a {@link RichFlatMapFunction} of elements with type {@link BEvent} that is
 * capable of producing responses of type {@link Response}.
 * 
 * <p>
 * Since this map is actually "rich" this means that classes that extends this
 * one can have access to the state of the operator and use it in a distributed
 * fashion. Additionally, being this map a "flat" it might return 0 or 1 results
 * for each event being consumed.
 * 
 * @author Andrea Burattin
 */
public abstract class StreamMiningAlgorithm<T extends Response> extends RichFlatMapFunction<BEvent, T> {

	private static final long serialVersionUID = 10170817098305999L;
	private transient ValueState<Long> processedEvents;
	
	@Override
	public void open(Configuration parameters) throws Exception {
		processedEvents = getRuntimeContext().getState(new ValueStateDescriptor<>("processed-events", Long.class));
	}
	
	@Override
	public void flatMap(BEvent event, Collector<T> out) throws Exception {
		T latestResponse = process(event);
		if (latestResponse != null) {
			out.collect(latestResponse);
		}
	}
	
	/**
	 * This abstract method is what each derive class is expected to implement.
	 * The argument of the method is the new observation and the returned value
	 * is the result of the mining.
	 * 
	 * <p>
	 * If this method returns value <code>null</code>, then the value is not
	 * moved forward into the pipeline.
	 * 
	 * @param event the new event being observed
	 * @return the result of the mining of the event, or <code>null</code> if
	 * nothing should go through the rest of the pipeline
	 */
	public abstract T ingest(BEvent event);
	
	/**
	 * Returns the total number of events processed so far
	 * 
	 * @return the total number of events processed so far
	 */
	public long getProcessedEvents() {
		try {
			if (processedEvents == null || processedEvents.value() == null) {
				return -1;
			}
			return processedEvents.value().longValue();
		} catch (IOException e) {
			// this exception would mean that there are serialization issues
		}
		return -1;
	}
	
	
	/*
	 * The internal processor in charge of updating the internal status of the
	 * map.
	 */
	protected T process(BEvent event) {
		try {
			long value = 1;
			if (processedEvents != null && processedEvents.value() != null) {
				value = processedEvents.value() + 1;
			}
			processedEvents.update(value);
		} catch (IOException e) {
			// this exception would mean that there are serialization issues
		}
		T tmp = ingest(event);
		if (tmp != null) {
			tmp.setProcessedEvents(getProcessedEvents());
		}
		return tmp;
	}
}
