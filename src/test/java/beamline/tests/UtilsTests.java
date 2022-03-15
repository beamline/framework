package beamline.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XTrace;
import org.junit.jupiter.api.Test;

import beamline.exceptions.EventException;
import beamline.utils.EventUtils;

public class UtilsTests {

	@Test
	public void test_create_event() {
		String activityName = UUID.randomUUID().toString();
		String caseId = UUID.randomUUID().toString();
		XTrace t = null;
		try {
			t = EventUtils.create(activityName, caseId);
		} catch (EventException e) { }
		
		assertNotNull(t);
		assertEquals(XConceptExtension.instance().extractName(t), caseId);
		assertEquals(XConceptExtension.instance().extractName(t.get(0)), activityName);
	}
	
	@Test
	public void test_create_event_time() {
		String activityName = UUID.randomUUID().toString();
		String caseId = UUID.randomUUID().toString();
		Date date = Date.valueOf("1996-01-23");
		XTrace t = null;
		try {
			t = EventUtils.create(activityName, caseId, date);
		} catch (EventException e) { }
		
		assertNotNull(t);
		assertEquals(XConceptExtension.instance().extractName(t), caseId);
		assertEquals(XConceptExtension.instance().extractName(t.get(0)), activityName);
		assertEquals(XTimeExtension.instance().extractTimestamp(t.get(0)), date);
	}
	
	@Test
	public void test_create_event_attributes() {
		String activityName = UUID.randomUUID().toString();
		String caseId = UUID.randomUUID().toString();
		Date date = Date.valueOf("1996-01-23");
		List<Pair<String, String>> attributes = new LinkedList<Pair<String, String>>();
		Map<String, String> values = new HashMap<String, String>();
		for (int i = 0; i < 10; i++) {
			String attributeName = "attr-" + i;
			String attributeValue = UUID.randomUUID().toString();
			values.put(attributeName, attributeValue);
			attributes.add(Pair.of(attributeName, attributeValue));
		}
		XTrace t = null;
		try {
			t = EventUtils.create(activityName, caseId, date, attributes);
		} catch (EventException e) { }
		
		assertNotNull(t);
		assertEquals(XConceptExtension.instance().extractName(t), caseId);
		assertEquals(XConceptExtension.instance().extractName(t.get(0)), activityName);
		assertEquals(XTimeExtension.instance().extractTimestamp(t.get(0)), date);
		for(String name : t.get(0).getAttributes().keySet()) {
			if (name.startsWith("attr-")) {
				assertEquals(t.get(0).getAttributes().get(name).toString(), values.get(name));
			}
		}
	}
	
	@Test
	public void test_no_activityname() {
		assertThrows(EventException.class, () -> {
			EventUtils.create(null, "");
		});
		assertThrows(EventException.class, () -> {
			EventUtils.create("", null);
		});
	}
	
	@Test
	public void test_extract_name_case() throws EventException {
		String activityName = UUID.randomUUID().toString();
		String caseId = UUID.randomUUID().toString();
		XTrace t = EventUtils.create(activityName, caseId);
		assertEquals(activityName, EventUtils.getActivityName(t));
		assertEquals(caseId, EventUtils.getCaseId(t));
	}
}
