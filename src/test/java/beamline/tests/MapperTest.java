package beamline.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.Test;

import beamline.events.BEvent;
import beamline.exceptions.EventException;
import beamline.models.algorithms.InfiniteSizeDirectlyFollowsMapper;
import beamline.models.responses.DirectlyFollowsRelation;

public class MapperTest {

	@Test
	public void test_infinite_size_df() throws EventException, Exception {
		List<DirectlyFollowsRelation> results = new ArrayList<>();
		// <K,A,B,A,C>, <A,B,A>
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env
			.fromElements(
				new BEvent("p", "c", "K"),
				new BEvent("p", "c2", "A"),
				new BEvent("p", "c", "A"),
				new BEvent("p", "c", "B"),
				new BEvent("p", "c2", "B"),
				new BEvent("p", "c", "A"),
				new BEvent("p", "c2", "A"),
				new BEvent("p", "c", "C"))
			.keyBy(BEvent::getProcessName)
			.flatMap(new InfiniteSizeDirectlyFollowsMapper())
			.executeAndCollect().forEachRemaining((DirectlyFollowsRelation e) -> {
				results.add(e);
			});
		
		assertEquals(6, results.size());
		assertTrue(Utils.verifyDirectFollows(results.get(0), "K", "A", "c"));
		assertTrue(Utils.verifyDirectFollows(results.get(1), "A", "B", "c"));
		assertTrue(Utils.verifyDirectFollows(results.get(2), "A", "B", "c2"));
		assertTrue(Utils.verifyDirectFollows(results.get(3), "B", "A", "c"));
		assertTrue(Utils.verifyDirectFollows(results.get(4), "B", "A", "c2"));
		assertTrue(Utils.verifyDirectFollows(results.get(5), "A", "C", "c"));
	}
}
