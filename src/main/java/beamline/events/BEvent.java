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

import beamline.exceptions.EventException;

/**
 * This class describes an event for the Beamline framework. An event is the
 * minimal observable unit in the context of streaming process mining. Each
 * event is expected to belong to a process instance which, in turn, belongs to
 * a process. For this reason, when an event is created (for example using the
 * {@link #BEvent(String, String, String)} constructor).
 * 
 * <p>
 * Some level of compatibility with the OpenXES library is guaranteed by:
 * <ul>
 * 	<li>using the same attribute names for internally storing the name of the
 * attributes;</li>
 * 	<li>having the capability of setting attributes using the
 * 	{@link #setTraceAttribute(String, XAttribute)} and
 * 	{@link #setEventAttribute(String, XAttribute)} which take an
 * 	{@link XAttribute} as parameter.</li>
 * </ul>
 * 
 * @author Andrea Burattin
 */
public class BEvent implements Serializable, Comparable<BEvent> {

	private static final long serialVersionUID = -7300189277034528917L;
	
	private Map<String, Serializable> eventAttributes;
	private Map<String, Serializable> traceAttributes;
	private Map<String, Serializable> processAttributes;

	/**
	 * Creates a new {@link BEvent} referring to one event
	 *
	 * @param processName the name of the process
	 * @param activityName the name of the activity
	 * @param caseId the identifier of the process instance
	 * @param time the time when the event has happened
	 * @param eventAttributes a collection of string attributes for the event
	 * @throws EventException this exception is thrown is incomplete information
	 * is provided
	 */
	public BEvent(
			String processName,
			String caseId,
			String activityName,
			Date time,
			Collection<Pair<String, String>> eventAttributes) throws EventException {
		if (processName == null || activityName == null || caseId == null) {
			throw new EventException("Activity name or case id missing");
		}
		
		this.eventAttributes = new HashMap<>();
		this.traceAttributes = new HashMap<>();
		this.processAttributes = new HashMap<>();
		
		setProcessName(processName);
		setTraceName(caseId);
		setEventName(activityName);
		if (time == null) {
			setTimestamp(new Date());
		} else {
			setTimestamp(time);
		}
		
		if (eventAttributes != null) {
			for(Pair<String, String> a : eventAttributes) {
				setEventAttribute(a.getLeft(), a.getRight());
			}
		}
	}
	
	/**
	 * Creates a new {@link BEvent} referring to one event
	 * 
	 * @param processName the name of the process
	 * @param activityName the name of the activity
	 * @param caseId the identifier of the process instance
	 * @param time the time when the event has happened
	 * @throws EventException this exception is thrown is incomplete information
	 * is provided
	 */
	public BEvent(String processName, String caseId, String activityName, Date time) throws EventException {
		this(processName, caseId, activityName, time, null);
	}
	
	/**
	 * Creates a new {@link BEvent} referring to one event
	 * 
	 * @param processName the name of the process
	 * @param activityName the name of the activity
	 * @param caseId the identifier of the process instance
	 * @return the new event
	 * @throws EventException this exception is thrown is incomplete information
	 * is provided
	 */
	public BEvent(String processName, String caseId, String activityName) throws EventException {
		this(processName, caseId, activityName, null, null);
	}
	
	//
	// Specific methods
	//
	/**
	 * Sets the process name
	 * 
	 * @param name the process name
	 */
	public void setProcessName(String name) {
		setProcessAttribute(XConceptExtension.KEY_NAME, name);
	}
	
	/**
	 * Gets the process name
	 * 
	 * @return the process name
	 */
	public String getProcessName() {
		return (String) processAttributes.get(XConceptExtension.KEY_NAME);
	}
	
	/**
	 * Sets the trace name (a.k.a. case id)
	 * 
	 * @param name the trace name
	 */
	public void setTraceName(String name) {
		setTraceAttribute(XConceptExtension.KEY_NAME, name);
	}
	
	/**
	 * Gets the trace name (a.k.a. case id)
	 * 
	 * @return the trace name
	 */
	public String getTraceName() {
		return (String) traceAttributes.get(XConceptExtension.KEY_NAME);
	}
	
	/**
	 * Sets the event name (a.k.a. activity name)
	 * 
	 * @param name the event name
	 */
	public void setEventName(String name) {
		setEventAttribute(XConceptExtension.KEY_NAME, name);
	}
	
	/**
	 * Gets the event name (a.k.a. activity name)
	 * 
	 * @return the event name
	 */
	public String getEventName() {
		return (String) eventAttributes.get(XConceptExtension.KEY_NAME);
	}
	
	/**
	 * Sets the event time
	 * 
	 * @param timestamp the event time
	 */
	public void setTimestamp(Date timestamp) {
		setEventAttribute(XTimeExtension.KEY_TIMESTAMP, timestamp);
	}
	
	/**
	 * Gets the event time
	 * 
	 * @return the event time
	 */
	public Date getEventTime() {
		return (Date) eventAttributes.get(XTimeExtension.KEY_TIMESTAMP);
	}
	
	//
	// General methods
	//
	/**
	 * Gets all event level attributes
	 * 
	 * @return the attributes
	 */
	public Map<String, Serializable> getEventAttributes() {
		return eventAttributes;
	}
	
	/**
	 * Gets all trace level attributes
	 * 
	 * @return the attributes
	 */
	public Map<String, Serializable> getTraceAttributes() {
		return traceAttributes;
	}
	
	/**
	 * Gets all process level attributes
	 * 
	 * @return the attributes
	 */
	public Map<String, Serializable> getProcessAttributes() {
		return processAttributes;
	}
	
	/**
	 * Sets a event level attribute
	 * 
	 * @param name the attribute name
	 * @param value the attribute value
	 */
	public void setEventAttribute(String name, Serializable value) {
		eventAttributes.put(name, value);
	}
	
	/**
	 * Sets a event level attribute
	 * 
	 * @param name the attribute name
	 * @param value the attribute value
	 */
	public void setEventAttribute(String name, XAttribute value) {
		setAttributeFromXAttribute(eventAttributes, name, value);
	}
	
	/**
	 * Sets a trace level attribute
	 * 
	 * @param name the attribute name
	 * @param value the attribute value
	 */
	public void setTraceAttribute(String name, Serializable value) {
		traceAttributes.put(name, value);
	}
	
	/**
	 * Sets a trace level attribute
	 * 
	 * @param name the attribute name
	 * @param value the attribute value
	 */
	public void setTraceAttribute(String name, XAttribute value) {
		setAttributeFromXAttribute(traceAttributes, name, value);
	}
	
	/**
	 * Sets a process level attribute
	 * 
	 * @param name the attribute name
	 * @param value the attribute value
	 */
	public void setProcessAttribute(String name, Serializable value) {
		processAttributes.put(name, value);
	}
	
	/**
	 * Sets a process level attribute
	 * 
	 * @param name the attribute name
	 * @param value the attribute value
	 */
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
