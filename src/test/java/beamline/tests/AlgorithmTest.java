package beamline.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.operators.StreamFlatMap;
import org.apache.flink.streaming.runtime.tasks.OneInputStreamTask;
import org.apache.flink.streaming.runtime.tasks.OneInputStreamTaskTestHarness;
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.util.Collector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import beamline.events.BEvent;
import beamline.models.algorithms.StreamMiningAlgorithm;
import beamline.models.responses.Response;
import beamline.models.responses.StringResponse;
import beamline.sources.CSVLogSource;

public class AlgorithmTest {

//	private OneInputStreamOperatorTestHarness<String, Long> testHarness;
//	private StreamMiningAlgorithm statefulFlatMapFunction;

//	@BeforeEach
//	public void setupTestHarness() throws Exception {
//
//		// instantiate user-defined function
//		statefulFlatMapFunction = new StatefulFlatMapFunction();
//
//		// wrap user defined function into a the corresponding operator
//		testHarness = new OneInputStreamOperatorTestHarness<>(new StreamFlatMap<>(statefulFlatMapFunction));
//
//		// optionally configured the execution environment
//		testHarness.getExecutionConfig().setAutoWatermarkInterval(50);
//
//		// open the test harness (will also call open() on RichFunctions)
//		testHarness.open();
//	}

	@Test
	public void test_result() throws Exception {
//		List<String> acts = new LinkedList<>();
//		List<String> caseIds = new LinkedList<>();
//		CSVLogSource source = new CSVLogSource("src/test/resources/sources/source.csv", 0, 1);
//
//		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//		DataStream<BEvent> stream = env.addSource(source);
//		stream.executeAndCollect().forEachRemaining((BEvent e) -> {
//			acts.add(e.getEventName());
//			caseIds.add(e.getTraceName());
//		});
//		
//		assertEquals(5, acts.size());
//		assertEquals(5, caseIds.size());
//		
//		assertThat(acts, hasItems("a11","a21","a22","a12","a23"));
//		assertThat(caseIds, hasItems("c1","c2","c2","c1","c2"));
		
		StreamMiningAlgorithm<StringResponse> m = new StreamMiningAlgorithm<StringResponse>() {
			@Override
			public StringResponse ingest(BEvent event) throws Exception {
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
		
//		StreamMiningAlgorithm m = new StreamMiningAlgorithm() {
//			private static final long serialVersionUID = 3268754545347297698L;
//			
//			@Override
//			public Serializable ingest(BEvent event) {
//				int product = 1;
//				if (getLatestResponse() != null) {
//					product = (int) getLatestResponse();
//				}
//				product *= Integer.parseInt(event.getEventName());
//				setLatestResponse(-product);
//				return product;
//			}
//		};
		
//		private OneInputStreamOperatorTestHarness<BEvent, Serializable> testHarness = new OneInputStreamOperatorTestHarness<BEvent, Serializable>(m);
//		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//		env.fromElements(
//			BEvent.create("p", "3", "c1"),
//			BEvent.create("p", "7", "c1"),
//			BEvent.create("p", "11", "c1"),
//			BEvent.create("p", "13", "c1")).keyBy(BEvent::getProcessName).map(m).print();
//		env.execute();
		
//		Collector<BEvent> stream = mock
		
//		System.out.println(m.getProcessedEvents());
		
//		assertEquals(4l, m.getProcessedEvents());
//		assertEquals(3003, m.getLatestResponse());
	}

//	@Test
//	public void test_hooks() throws Exception {
//		StreamMiningAlgorithm<Integer, Integer> m = new StreamMiningAlgorithm<Integer, Integer>() {
//			public Integer product = 1;
//			
//			@Override
//			public Integer ingest(Integer event) {
//				product *= event;
//				setLatestResponse(-product);
//				return product;
//			}
//		};
//		
//		List<Long> resultsBefore = new ArrayList<>();
//		m.setOnBeforeEvent(() -> {
//			resultsBefore.add(m.getProcessedEvents());
//		});
//		
//		List<Long> resultsAfter = new ArrayList<>();
//		m.setOnAfterEvent(() -> {
//			resultsAfter.add(m.getProcessedEvents());
//		});
//		
//		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//		env.fromElements(3, 7, 11, 13).map(m);
//		env.execute();
//		
//		assertThat(resultsBefore, hasItems(0l,1l,2l,3l));
//		assertThat(resultsAfter, hasItems(1l,2l,3l,4l));
//	}
}
