package beamline.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import beamline.events.BEvent;
import beamline.exceptions.EventException;
import beamline.models.responses.DirectlyFollowsRelation;
import beamline.models.responses.Response;
import beamline.models.responses.StringResponse;

public class ResponsesTest {

	@Test
	public void response_tests() {
		Response r = new Response();
		
		long rand = new Random().nextLong();
		r.setProcessedEvents(rand);
		assertEquals(rand, r.getProcessedEvents());
	}
	
	@Test
	public void directly_follow_tests() throws EventException {
		assertThrows(IllegalArgumentException.class, () ->
				new DirectlyFollowsRelation(
						BEvent.create("p", "a", "c1"),
						BEvent.create("p", "a", "c2")));
		
		DirectlyFollowsRelation df = new DirectlyFollowsRelation(BEvent.create("p", "a", "c1"), BEvent.create("p", "b", "c1"));
		DirectlyFollowsRelation df2 = new DirectlyFollowsRelation(BEvent.create("p", "a", "c2"), BEvent.create("p", "b", "c2"));
		DirectlyFollowsRelation df3 = new DirectlyFollowsRelation(BEvent.create("p", "a", "c1"), BEvent.create("p", "d", "c1"));
		
		assertTrue(df.equals(df2));
		assertFalse(df.equals(df3));
		assertFalse(df.equals(null));
		assertFalse(df.equals(""));
		assertTrue(df.equals(df));
		
		assertTrue(df.hashCode() == df2.hashCode());
		assertFalse(df.hashCode() == df3.hashCode());
		
		assertEquals("c1", df.getCaseId());
	}
	
	@Test
	public void string_tests() {
		String unique = UUID.randomUUID().toString();
		StringResponse sr = new StringResponse("");
		
		sr.set(unique);
		assertTrue(sr.get().equals(unique));
		assertTrue(sr.toString().equals(unique));
		
	}
}
