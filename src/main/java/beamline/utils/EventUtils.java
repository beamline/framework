package beamline.utils;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.tuple.Pair;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

import beamline.exceptions.EventException;

/**
 * This class contains some utility methods useful to handle and process events.
 * 
 * @author Andrea Burattin
 */
public class EventUtils {

	private static final XFactory xesFactory = new XFactoryNaiveImpl();
	
	private EventUtils() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Creates a new {@link XTrace} referring to one event
	 * 
	 * @param activityName the name of the activity
	 * @param caseId the identifier of the process instance
	 * @param time the time when the event has happened
	 * @param eventAttributes a collection of string attributes for the event
	 * @return the new event
	 * @throws EventException this exception is thrown is incomplete information
	 * is provided
	 */
	public static XTrace create(String activityName, String caseId, Date time, Collection<Pair<String, String>> eventAttributes) throws EventException {
		if (activityName == null || caseId == null) {
			throw new EventException("Activity name or case id missing");
		}
		
		XEvent event = xesFactory.createEvent();
		XConceptExtension.instance().assignName(event, activityName);
		if (time == null) {
			XTimeExtension.instance().assignTimestamp(event, new Date());
		} else {
			XTimeExtension.instance().assignTimestamp(event, time);
		}
		if (eventAttributes != null) {
			for(Pair<String, String> a : eventAttributes) {
				event.getAttributes().put(a.getLeft(), new XAttributeLiteralImpl(a.getLeft(), a.getRight()));
			}
		}
		XTrace eventWrapper = xesFactory.createTrace();
		XConceptExtension.instance().assignName(eventWrapper, caseId);
		eventWrapper.add(event);
		return eventWrapper;
	}
	
	/**
	 * Creates a new {@link XTrace} referring to one event
	 * 
	 * @param activityName the name of the activity
	 * @param caseId the identifier of the process instance
	 * @param time the time when the event has happened
	 * @return the new event
	 * @throws EventException this exception is thrown is incomplete information
	 * is provided
	 */
	public static XTrace create(String activityName, String caseId, Date time) throws EventException {
		return create(activityName, caseId, time, null);
	}
	
	/**
	 * Creates a new {@link XTrace} referring to one event. The time of the
	 * event is set to the current time
	 * 
	 * @param activityName the name of the activity
	 * @param caseId the identifier of the process instance
	 * @return the new event
	 * @throws EventException this exception is thrown is incomplete information
	 * is provided
	 */
	public static XTrace create(String activityName, String caseId) throws EventException {
		return create(activityName, caseId, null, null);
	}
	
	/**
	 * Extracts the activity name
	 * 
	 * @param event the event
	 * @return the activity name
	 */
	public static String getActivityName(XTrace event) {
		return XConceptExtension.instance().extractName(event.get(0));
	}
	
	/**
	 * Extracts the case id
	 * 
	 * @param event the event
	 * @return the case id
	 */
	public static String getCaseId(XTrace event) {
		return XConceptExtension.instance().extractName(event);
	}
}
