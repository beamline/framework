package beamline.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.Test;

import beamline.events.BEvent;
import beamline.models.algorithms.StreamMiningAlgorithm;
import beamline.models.responses.StringResponse;

public class AlgorithmTest {

	@Test
	public void test_result_1() throws Exception {
		StreamMiningAlgorithm<StringResponse> m = new StreamMiningAlgorithm<StringResponse>() {
			private static final long serialVersionUID = -8445717838576941924L;

			@Override
			public StringResponse ingest(BEvent event) {
				return new StringResponse(event.getProcessName() + event.getEventName() + event.getTraceName());
			}
		};
		
		List<String> events = new LinkedList<>();
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env
			.fromElements(
				BEvent.create("p", "3", "c1"),
				BEvent.create("p", "7", "c1"),
				BEvent.create("p", "11", "c1"),
				BEvent.create("p", "13", "c1"))
			.keyBy(BEvent::getProcessName)
			.flatMap(m)
			.executeAndCollect().forEachRemaining((StringResponse e) -> {
				events.add(e.get());
			});
		
		assertEquals(4, events.size());
		assertThat(events, hasItems("p3c1","p7c1","p11c1","p13c1"));
	}
	
	@Test
	public void test_result_2() throws Exception {
		StreamMiningAlgorithm<StringResponse> m = new StreamMiningAlgorithm<StringResponse>() {
			private static final long serialVersionUID = -8445717838576941924L;

			@Override
			public StringResponse ingest(BEvent event) {
				return new StringResponse(event.getProcessName() + event.getEventName() + event.getTraceName());
			}
		};
		
		assertEquals(-1, m.getProcessedEvents());
	}
}
