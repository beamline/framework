package beamline.events;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XTrace;

import beamline.exceptions.EventException;

/**
 * 
 * @author Andrea Burattin
 */
public class BEvent implements Serializable, Comparable<BEvent> {

	private static final long serialVersionUID = -7300189277034528917L;
	
	private Map<String, Serializable> eventAttributes;
	private Map<String, Serializable> traceAttributes;
	private Map<String, Serializable> processAttributes;

	/**
	 * Constructor of a new event
	 */
	public BEvent() {
		this.eventAttributes = new HashMap<>();
		this.traceAttributes = new HashMap<>();
		this.processAttributes = new HashMap<>();
	}
	
	//
	// Factories
	//
	/**
	 * Creates a new {@link BEvent} referring to one event
	 *
	 * @param processName the name of the process
	 * @param activityName the name of the activity
	 * @param caseId the identifier of the process instance
	 * @param time the time when the event has happened
	 * @param eventAttributes a collection of string attributes for the event
	 * @return the new event
	 * @throws EventException this exception is thrown is incomplete information
	 * is provided
	 */
	public static BEvent create(
			String processName,
			String caseId,
			String activityName,
			Date time,
			Collection<Pair<String, String>> eventAttributes) throws EventException {
		if (processName == null || activityName == null || caseId == null) {
			throw new EventException("Activity name or case id missing");
		}
		
		BEvent event = new BEvent();
		event.setProcessName(processName);
		event.setTraceName(caseId);
		event.setEventName(activityName);
		if (time == null) {
			event.setTimestamp(new Date());
		} else {
			event.setTimestamp(time);
		}
		
		if (eventAttributes != null) {
			for(Pair<String, String> a : eventAttributes) {
				event.setEventAttribute(a.getLeft(), a.getRight());
			}
		}
		return event;
	}
	
	/**
	 * Creates a new {@link BEvent} referring to one event
	 * 
	 * @param processName the name of the process
	 * @param activityName the name of the activity
	 * @param caseId the identifier of the process instance
	 * @param time the time when the event has happened
	 * @return the new event
	 * @throws EventException this exception is thrown is incomplete information
	 * is provided
	 */
	public static BEvent create(String processName, String caseId, String activityName, Date time) throws EventException {
		return create(processName, caseId, activityName, time, null);
	}
	
	/**
	 * Creates a new {@link XTrace} referring to one event. The time of the
	 * event is set to the current time
	 * 
	 * @param processName the name of the process
	 * @param activityName the name of the activity
	 * @param caseId the identifier of the process instance
	 * @return the new event
	 * @throws EventException this exception is thrown is incomplete information
	 * is provided
	 */
	public static BEvent create(String processName, String caseId, String activityName) throws EventException {
		return create(processName, caseId, activityName, null, null);
	}
	
	//
	// Specific methods
	//
	public void setProcessName(String name) {
		setProcessAttribute(XConceptExtension.KEY_NAME, name);
	}
	
	public String getProcessName() {
		return (String) processAttributes.get(XConceptExtension.KEY_NAME);
	}
	
	public void setTraceName(String name) {
		setTraceAttribute(XConceptExtension.KEY_NAME, name);
	}
	
	public String getTraceName() {
		return (String) traceAttributes.get(XConceptExtension.KEY_NAME);
	}
	
	public void setEventName(String name) {
		setEventAttribute(XConceptExtension.KEY_NAME, name);
	}
	
	public String getEventName() {
		return (String) eventAttributes.get(XConceptExtension.KEY_NAME);
	}
	
	public void setTimestamp(Date timestamp) {
		setEventAttribute(XTimeExtension.KEY_TIMESTAMP, timestamp);
	}
	
	public Date getEventTime() {
		return (Date) eventAttributes.get(XTimeExtension.KEY_TIMESTAMP);
	}
	
	//
	// General methods
	//
	
	public Map<String, Serializable> getEventAttributes() {
		return eventAttributes;
	}
	
	public Map<String, Serializable> getTraceAttributes() {
		return traceAttributes;
	}
	
	public Map<String, Serializable> getProcessAttributes() {
		return processAttributes;
	}
	
	public void setEventAttribute(String name, Serializable value) {
		eventAttributes.put(name, value);
	}
	
	public void setEventAttribute(String name, XAttribute value) {
		setAttributeFromXAttribute(eventAttributes, name, value);
	}
	
	public void setTraceAttribute(String name, Serializable value) {
		traceAttributes.put(name, value);
	}
	
	public void setTraceAttribute(String name, XAttribute value) {
		setAttributeFromXAttribute(traceAttributes, name, value);
	}
	
	public void setProcessAttribute(String name, Serializable value) {
		processAttributes.put(name, value);
	}
	
	public void setProcessAttribute(String name, XAttribute value) {
		setAttributeFromXAttribute(processAttributes, name, value);
	}
	
	//
	// Overrides
	//
	
	@Override
	public String toString() {
		return processAttributes.toString() + " - " + traceAttributes.toString() + " - " + eventAttributes.toString();
	}

	@Override
	public int compareTo(BEvent o) {
		if (getEventTime() == null || o.getEventTime() == null) {
			return 0;
		}
		return getEventTime().compareTo(o.getEventTime());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		BEvent other = (BEvent) obj;
		return new EqualsBuilder()
				.append(processAttributes, other.processAttributes)
				.append(traceAttributes, other.traceAttributes)
				.append(eventAttributes, other.eventAttributes)
				.isEquals();

	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(processAttributes)
				.append(traceAttributes)
				.append(eventAttributes)
				.toHashCode();
	}
	
	//
	// Private methods
	//
	
	private void setAttributeFromXAttribute(Map<String, Serializable> map, String name, XAttribute value) {
		if (value instanceof XAttributeBoolean) {
			map.put(name, ((XAttributeBoolean) value).getValue());
		} else if (value instanceof XAttributeTimestamp) {
			map.put(name, ((XAttributeTimestamp) value).getValue());
		} else if (value instanceof XAttributeContinuous) {
			map.put(name, ((XAttributeContinuous) value).getValue());
		} else if (value instanceof XAttributeDiscrete) {
			map.put(name, ((XAttributeDiscrete) value).getValue());
		} else if (value instanceof XAttributeLiteral) {
			map.put(name, ((XAttributeLiteral) value).getValue());
		}
	}
}
