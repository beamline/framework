package beamline.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.flink.runtime.client.JobExecutionException;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.jupiter.api.Test;

import beamline.events.BEvent;
import beamline.sources.CSVLogSource;
import beamline.sources.MQTTXesSource;
import beamline.sources.StringTestSource;
import beamline.sources.XesLogSource;

public class SourcesTest {

	@Test
	public void test_string_test_source() throws Exception {
		List<String> acts = new LinkedList<>();
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		DataStream<BEvent> stream = env.addSource(new StringTestSource("ABCDA"));
		stream.executeAndCollect().forEachRemaining((BEvent e) -> {
			acts.add(e.getEventName());
		});
		
		assertEquals(5, acts.size());
		
		assertThat(acts, hasItems("A","B","C","D","A"));
	}
	
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
	
	@Test
	public void test_xes_source_1() {
		XesLogSource s1 = new XesLogSource("src/test/resources/sources/empty.xes");
		assertThrowsExactly(JobExecutionException.class, () -> {
			StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
			env.addSource(s1).map(e -> e).print();
			env.execute();
		});
		
		XesLogSource s2 = new XesLogSource("src/test/resources/sources/empty_2.xes");
		assertThrowsExactly(JobExecutionException.class, () -> {
			StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
			env.addSource(s2).map(e -> e).print();
			env.execute();
		});

		XesLogSource s3 = new XesLogSource("src/test/resources/sources/empty.csv");
		assertThrowsExactly(JobExecutionException.class, () -> {
			StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
			env.addSource(s3).map(e -> e).print();
			env.execute();
		});
	}
	
	@Test
	public void test_xes_source_2() throws Exception {
		List<String> acts = new LinkedList<>();
		List<String> caseIds = new LinkedList<>();
		XesLogSource source = new XesLogSource(Utils.generteXLog());
		
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		DataStream<BEvent> stream = env.addSource(source);
		stream.executeAndCollect().forEachRemaining((BEvent e) -> {
			acts.add(e.getEventName());
			caseIds.add(e.getTraceName());
		});
		
		assertEquals(9, acts.size());
		assertEquals(9, caseIds.size());
		
		assertThat(acts, hasItems("K","C","A","I","B","O","A","A","C"));
		assertThat(caseIds, hasItems("c1","c2","c1","c2","c1","c2","c1","c2","c1"));
	}
	
	@Test
	public void test_xes_source_3() throws Exception {
		List<String> acts = new LinkedList<>();
		List<String> caseIds = new LinkedList<>();
		XesLogSource s = new XesLogSource("src/test/resources/sources/source.xes.gz");
		
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		DataStream<BEvent> stream = env.addSource(s);
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
	public void test_xes_source_4() throws Exception {
		List<String> acts = new LinkedList<>();
		List<String> caseIds = new LinkedList<>();
		XesLogSource s = new XesLogSource("src/test/resources/sources/source_2.xes");
		
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		DataStream<BEvent> stream = env.addSource(s);
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
	public void test_mqtt_1() {
		try {
			// create mqtt broker
			BrokerService brokerService = createBroker();
			brokerService.start();
			brokerService.waitUntilStarted();
			
			MQTTXesSource s = new MQTTXesSource("tcp://localhost:9999", "test", "name");
			
			// create the sink file
			final File tmpFile = File.createTempFile("mqtt", "log");
			
			// create actual source
			StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
			env
				.addSource(s)
				.keyBy(BEvent::getProcessName)
				.addSink(new RichSinkFunction<BEvent>() {
					private static final long serialVersionUID = -8658786866403985570L;

					@Override
					public void invoke(BEvent value, Context context) throws Exception {
						String toWrite = value.getProcessName() + "-" + value.getTraceName() + "-" + value.getEventName() + "/";
						java.nio.file.Files.write(tmpFile.toPath(), toWrite.getBytes(), StandardOpenOption.APPEND);
					}
				});
			env.executeAsync();
			
			Thread.sleep(2000);
			
			MqttClient client = new MqttClient("tcp://localhost:9999", "clientid", new MemoryPersistence());
			client.connect();
			publish(client, "c1", "a11");
			publish(client, "c2", "a21");
			publish(client, "c2", "a22");
			publish(client, "c1", "a12");
			publish(client, "c2", "a23");
			
			Thread.sleep(2000);
			assertEquals(
					"name-c1-a11/name-c2-a21/name-c2-a22/name-c1-a12/name-c2-a23/",
					org.apache.commons.io.FileUtils.readFileToString(tmpFile, "utf-8"));

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false); // error
		}
	}
	
	protected void publish(MqttClient client, String caseId, String activityName) throws MqttPersistenceException, MqttException {
		client.publish("test/name/" + caseId + "/" + activityName, "{}".getBytes(StandardCharsets.UTF_8), 1, false);
	}
	
	protected BrokerService createBroker() throws Exception {
		BrokerService brokerService = new BrokerService();
		brokerService.setDeleteAllMessagesOnStartup(true);
		brokerService.setPersistent(false);
		brokerService.setAdvisorySupport(false);
		brokerService.setUseJmx(true);
		brokerService.getManagementContext().setCreateConnector(false);
		brokerService.setPopulateJMSXUserID(true);

		TransportConnector connector = new TransportConnector();
		connector.setUri(new URI("mqtt://localhost:9999"));
		connector.setName("mqtt");
		brokerService.addConnector(connector);

		return brokerService;
	}
}
