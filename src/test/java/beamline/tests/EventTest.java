package beamline.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.junit.jupiter.api.Test;

import beamline.events.BEvent;
import beamline.exceptions.EventException;

public class EventTest {

	String processName = UUID.randomUUID().toString();
	String traceName = UUID.randomUUID().toString();
	String eventName = UUID.randomUUID().toString();
	Date eventDate = new Date();
	
	@Test
	public void event_creation() throws EventException {
		assertThrows(EventException.class, () -> BEvent.create("", "", null));
		assertThrows(EventException.class, () -> BEvent.create("", null, ""));
		assertThrows(EventException.class, () -> BEvent.create(null, "", ""));
		
		BEvent e = BEvent.create(processName, traceName, eventName, eventDate, Set.of(Pair.of("a1", "v1")));
		assertEquals("v1", e.getEventAttributes().get("a1"));
	}
	
	@Test
	public void event_names() throws EventException {
		BEvent e = BEvent.create(processName, traceName, eventName);
		assertEquals(processName, e.getProcessName());
		assertEquals(traceName, e.getTraceName());
		assertEquals(eventName, e.getEventName());
		
		Date eventDate = new Date();
		BEvent e2 = BEvent.create(processName, traceName, eventName, eventDate);
		assertEquals(eventDate, e2.getEventTime());
	}
	
	@Test
	public void test_equals() throws EventException {
		BEvent e = BEvent.create(processName, traceName, eventName);
		e.setTimestamp(null);
		BEvent e2 = BEvent.create(processName, traceName, eventName, eventDate);
		BEvent e3 = BEvent.create(processName, traceName, eventName, eventDate);

		assertEquals(e2, e3);
		assertThat(e2).hasSameHashCodeAs(e3);
		
		assertEquals(0, e2.compareTo(e3));
		assertEquals(0, e.compareTo(e3));
		
		assertThat(e).isEqualTo(e).isNotEqualTo(null).isNotEqualTo(eventDate);
	}
	
	@Test
	public void event_attributes() throws EventException {
		BEvent e = BEvent.create(processName, traceName, eventName);
		e.setProcessAttribute("pa", "v1");
		e.setTraceAttribute("ta", "v2");
		e.setEventAttribute("ea", "v3");
		
		assertEquals("v1", e.getProcessAttributes().get("pa"));
		assertEquals("v2", e.getTraceAttributes().get("ta"));
		assertEquals("v3", e.getEventAttributes().get("ea"));
	}
	
	@Test
	public void event_attributes_xattributable() throws EventException {
		Date date = new Date();
		BEvent e = BEvent.create(processName, traceName, eventName);
		e.setProcessAttribute("pa", new XAttributeLiteralImpl("pa", "v1"));
		e.setTraceAttribute("ta", new XAttributeBooleanImpl("ta", false));
		e.setEventAttribute("ea", new XAttributeDiscreteImpl("ea", 42));
		e.setEventAttribute("ea2", new XAttributeContinuousImpl("ea2", 3.14));
		e.setEventAttribute("ea3", new XAttributeTimestampImpl("ea3", date));
		
		assertEquals("v1", e.getProcessAttributes().get("pa"));
		assertEquals(false, e.getTraceAttributes().get("ta"));
		assertEquals(42l, e.getEventAttributes().get("ea"));
		assertEquals(3.14, e.getEventAttributes().get("ea2"));
		assertEquals(date, e.getEventAttributes().get("ea3"));
	}
	
	@Test
	public void event_to_string() throws EventException {
		BEvent e = BEvent.create(processName, traceName, eventName, eventDate);
		assertEquals(
			"{concept:name=" + processName +"} - " +
			"{concept:name=" + traceName + "} - " +
			"{concept:name=" + eventName + ", time:timestamp=" + eventDate.toString() + "}", e.toString());
	}
}
