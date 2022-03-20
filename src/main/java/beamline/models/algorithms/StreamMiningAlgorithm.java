package beamline.models.algorithms;

import java.io.IOException;
import java.io.Serializable;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;

import beamline.events.BEvent;

/**
 * This abstract class defines the root of the mining algorithms hierarchy. It
 * is a {@link MapFunction} of elements with type {@link BEvent} that is capable
 * of producing responses of type {@link Response}.
 * 
 * @author Andrea Burattin
 */
public abstract class StreamMiningAlgorithm extends RichMapFunction<BEvent, Serializable> {

	private static final long serialVersionUID = 10170817098305999L;
	private transient ValueState<Long> processedEvents;
	private transient ValueState<Serializable> latestResponse;
	private transient HookEventProcessing onBeforeEvent = null;
	private transient HookEventProcessing onAfterEvent = null;
	
	@Override
	public void open(Configuration parameters) throws Exception {
		processedEvents = getRuntimeContext().getState(new ValueStateDescriptor<>("processede vents", Long.class));
		latestResponse = getRuntimeContext().getState(new ValueStateDescriptor<>("latest response", Serializable.class));
	}
	
	@Override
	public Serializable map(BEvent t) throws Exception {
		if (onBeforeEvent != null) {
			onBeforeEvent.trigger();
		}
		process(t);
		if (onAfterEvent != null) {
			onAfterEvent.trigger();
		}
		return getLatestResponse();
	}
	
	/**
	 * This abstract method is what each derive class is expected to implement.
	 * The argument of the method is the new observation and the returned value
	 * is the result of the mining.
	 * 
	 * @param event the new event being observed
	 * @return the result of the mining of the event
	 */
	public abstract Serializable ingest(BEvent event);
	
	/**
	 * Returns the total number of events processed so far
	 * 
	 * @return the total number of events processed so far
	 */
	public long getProcessedEvents() {
		try {
			if (processedEvents == null || processedEvents.value() == null) {
				return 0l;
			}
			return processedEvents.value().longValue();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Returns the latest result of the mining
	 * 
	 * @return the latest result of the mining
	 */
	public Serializable getLatestResponse() {
		try {
			return latestResponse.value();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
	
	/*
	 * The internal processor in charge of updating the internal status of the
	 * map.
	 */
	protected void process(BEvent event) {
		try {
			long value = 1;
			if (processedEvents.value() != null) {
				value = processedEvents.value() + 1;
			}
			processedEvents.update(value);
			latestResponse.update(ingest(event));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Setter of the latest response onto the status.
	 */
	protected void setLatestResponse(Serializable latestResponse) {
		try {
			this.latestResponse.update(latestResponse);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
