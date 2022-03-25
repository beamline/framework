package beamline.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
		BEvent e1 = BEvent.create("p", "a", "c1");
		BEvent e2 = BEvent.create("p", "a", "c2");
		
		assertThrows(IllegalArgumentException.class, () -> new DirectlyFollowsRelation(e1, e2));
		
		BEvent e21 = BEvent.create("p", "a", "c1");
		BEvent e22 = BEvent.create("p", "b", "c1");
		BEvent e23 = BEvent.create("p", "a", "c2");
		BEvent e24 = BEvent.create("p", "b", "c2");
		BEvent e25 = BEvent.create("p", "d", "c1");

		DirectlyFollowsRelation df = new DirectlyFollowsRelation(e21, e22);
		DirectlyFollowsRelation df2 = new DirectlyFollowsRelation(e23, e24);
		DirectlyFollowsRelation df3 = new DirectlyFollowsRelation(e21, e25);
		
		assertEquals(df, df2);
		assertNotEquals(df, df3);
		assertNotEquals(null, df);
		
		assertEquals("c1", df.getCaseId());
	}
	
	@Test
	public void test_equals() throws EventException {
		BEvent e21 = BEvent.create("p", "a", "c1");
		BEvent e22 = BEvent.create("p", "b", "c1");
		BEvent e23 = BEvent.create("p", "a", "c2");
		BEvent e24 = BEvent.create("p", "b", "c2");
		
		DirectlyFollowsRelation df = new DirectlyFollowsRelation(e21, e22);
		DirectlyFollowsRelation df2 = new DirectlyFollowsRelation(e23, e24);
		
		assertThat(df).isEqualTo(df).hasSameHashCodeAs(df2);
	}
	
	@Test
	public void string_tests() {
		String unique = UUID.randomUUID().toString();
		StringResponse sr = new StringResponse("");
		
		sr.set(unique);
		assertEquals(sr.get(), unique);
		assertEquals(sr.toString(), unique);
		
	}
}
