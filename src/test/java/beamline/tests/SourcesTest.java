package beamline.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.flink.runtime.client.JobExecutionException;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.experimental.CollectSink;
import org.apache.flink.util.Collector;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.jupiter.api.Test;

import com.opencsv.CSVParserBuilder;

import beamline.events.BEvent;
import beamline.exceptions.SourceException;
import beamline.sources.CSVLogSource;
import beamline.sources.MQTTXesSource;
import beamline.sources.XesLogSource;

public class SourcesTest {

	@Test
	public void test_csv_source_1() throws Exception {
		List<String> acts = new LinkedList<>();
		List<String> caseIds = new LinkedList<>();
		CSVLogSource source = new CSVLogSource("src/test/resources/sources/source.csv", 0, 1);

		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		DataStream<BEvent> stream = env.addSource(source);
		stream.executeAndCollect().forEachRemaining((BEvent e) -> {
			acts.add(e.getEventName());
			caseIds.add(e.getTraceName());
		});
		
		assertEquals(5, acts.size());
		assertEquals(5, caseIds.size());
		
		assertThat(acts, hasItems("a11","a21","a22","a12","a23"));
		assertThat(caseIds, hasItems("c1","c2","c2","c1","c2"));
	}
	
	@Test
	public void test_csv_source_2() throws Exception {
		List<String> acts = new LinkedList<>();
		List<String> caseIds = new LinkedList<>();
		CSVLogSource source = new CSVLogSource(
				"src/test/resources/sources/source_2.csv",
				0,
				1,
				new CSVLogSource.ParserConfiguration().withSeparator('|'));
		
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		DataStream<BEvent> stream = env.addSource(source);
		stream.executeAndCollect().forEachRemaining((BEvent e) -> {
			acts.add(e.getEventName());
			caseIds.add(e.getTraceName());
		});
		
		assertEquals(5, acts.size());
		assertEquals(5, caseIds.size());
		
		assertThat(acts, hasItems("a11","a21","a22","a12","a23"));
		assertThat(caseIds, hasItems("c1","c2","c2","c1","c2"));
	}
	
	@Test
	public void test_csv_source_3() {
		CSVLogSource source = new CSVLogSource("DOESNT_EXIST", 0, 1);
		assertThrowsExactly(JobExecutionException.class, () -> {
			StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
			env.addSource(source).map(e -> e).print();
			env.execute();
		});
	}
	
//	@Test
//	public void test_xes_source_1() {
//		XesLogSource s1 = new XesLogSource("src/test/resources/sources/empty.xes");
//		assertThrowsExactly(SourceException.class, () -> s1.prepare());
//		
//		XesLogSource s2 = new XesLogSource("src/test/resources/sources/empty_2.xes");
//		assertThrowsExactly(SourceException.class, () -> s2.prepare());
//
//		XesLogSource s3 = new XesLogSource("src/test/resources/sources/empty.csv");
//		assertThrowsExactly(SourceException.class, () -> s3.prepare());
//	}
//	
//	@Test
//	public void test_xes_source_2() {
//		List<String> acts = new LinkedList<>();
//		List<String> caseIds = new LinkedList<>();
//		XesLogSource s = new XesLogSource(Utils.generteXLog());
//		try {
//			s.prepare();
//		} catch (SourceException e) {
//			e.printStackTrace();
//		}
//		s.getObservable().subscribe((t) -> {
//			acts.add(EventUtils.getActivityName(t));
//			caseIds.add(EventUtils.getCaseId(t));
//		});
//		
//		assertEquals(9, acts.size());
//		assertEquals(9, caseIds.size());
//		
//		assertThat(acts, hasItems("K","C","A","I","B","O","A","A","C"));
//		assertThat(caseIds, hasItems("c1","c2","c1","c2","c1","c2","c1","c2","c1"));
//	}
//	
//	@Test
//	public void test_xes_source_3() {
//		List<String> acts = new LinkedList<>();
//		List<String> caseIds = new LinkedList<>();
//		XesLogSource s = new XesLogSource("src/test/resources/sources/source.xes.gz");
//		try {
//			s.prepare();
//		} catch (SourceException e) {
//			e.printStackTrace();
//		}
//		s.getObservable().subscribe((t) -> {
//			acts.add(EventUtils.getActivityName(t));
//			caseIds.add(EventUtils.getCaseId(t));
//		});
//		
//		assertEquals(5, acts.size());
//		assertEquals(5, caseIds.size());
//		
//		assertThat(acts, hasItems("a11","a21","a22","a12","a23"));
//		assertThat(caseIds, hasItems("c1","c2","c2","c1","c2"));
//	}
//
//	@Test
//	public void test_mqtt_1() {
//		try {
//			// create mqtt broker
//			BrokerService brokerService = createBroker();
//			brokerService.start();
//			brokerService.waitUntilStarted();
//			
//			List<String> acts = new LinkedList<>();
//			List<String> caseIds = new LinkedList<>();
//			
//			// create actual source
//			MQTTXesSource source = new MQTTXesSource("tcp://localhost:9999", "test", "name");
//			source.prepare();
//			source.getObservable().subscribe((t) -> {
//				acts.add(EventUtils.getActivityName(t));
//				caseIds.add(EventUtils.getCaseId(t));
//			});
//
//			MqttClient client = new MqttClient("tcp://localhost:9999", "clientid", new MemoryPersistence());
//			client.connect();
//			
//			publish(client, "c1", "a11");
//			publish(client, "c2", "a21");
//			publish(client, "c2", "a22");
//			publish(client, "c1", "a12");
//			publish(client, "c2", "a23");
//			
//			Thread.sleep(100);
//			
//			assertThat(acts, hasItems("a11","a21","a22","a12","a23"));
//			assertThat(caseIds, hasItems("c1","c2","c2","c1","c2"));
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void test_mqtt_2() {
//		MQTTXesSource source = new MQTTXesSource("tcp://localhost:1", "test", "name");
//		assertThrowsExactly(SourceException.class, () -> source.prepare());
//	}
//	
//	protected void publish(MqttClient client, String caseId, String activityName) throws MqttPersistenceException, MqttException {
//		client.publish("test/name/" + caseId + "/" + activityName, "{}".getBytes(StandardCharsets.UTF_8), 1, false);
//	}
//	
//	protected BrokerService createBroker() throws Exception {
//		BrokerService brokerService = new BrokerService();
//		brokerService.setDeleteAllMessagesOnStartup(true);
//		brokerService.setPersistent(false);
//		brokerService.setAdvisorySupport(false);
//		brokerService.setUseJmx(true);
//		brokerService.getManagementContext().setCreateConnector(false);
//		brokerService.setPopulateJMSXUserID(true);
//
//		TransportConnector connector = new TransportConnector();
//		connector.setUri(new URI("mqtt://localhost:9999"));
//		connector.setName("mqtt");
//		brokerService.addConnector(connector);
//
//		return brokerService;
//	}
}
