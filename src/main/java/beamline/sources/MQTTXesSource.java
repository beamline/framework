package beamline.sources;

import java.util.Date;
import java.util.UUID;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * This implementation of a {@link XesSource} produces events as they are
 * observed in an MQTT-XES broker. This source produces a hot observable.
 * 
 * <p>
 * Example of usage:
 * <pre>
 * XesSource source = new MQTTXesSource("tcp://broker.hivemq.com:1883", "topicBase", "processName");
 * source.prepare();
 * </pre>
 * 
 * @see {@link http://www.beamline.cloud/mqtt-xes/}
 * @author Andrea Burattin
 */
public class MQTTXesSource implements XesSource {

	private static XFactory xesFactory = new XFactoryNaiveImpl();
	private String processName;
	private String brokerHost;
	private String topicBase;
	private PublishSubject<XTrace> ps;
	
	/**
	 * Constructs the source
	 * 
	 * @param brokerHost the URL of the broker host
	 * @param topicBase the base of the topic for the
	 * @param processName the name of the process
	 */
	public MQTTXesSource(String brokerHost, String topicBase, String processName) {
		this.brokerHost = brokerHost;
		this.topicBase = topicBase;
		this.processName = processName;
		this.ps = PublishSubject.create();
	}
	
	@Override
	public Observable<XTrace> getObservable() {
		return ps;
	}

	@Override
	public void prepare() throws Exception {
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(true);
		options.setKeepAliveInterval(30);

		IMqttClient myClient = new MqttClient(brokerHost, UUID.randomUUID().toString());
		myClient.setCallback(new MqttCallback() {
			
			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				int posLastSlash = topic.lastIndexOf("/");
				String partBeforeActName = topic.substring(0, posLastSlash);
				String activityName = topic.substring(posLastSlash + 1);
				String caseId = partBeforeActName.substring(partBeforeActName.lastIndexOf("/") + 1);

				XEvent event = xesFactory.createEvent();
				XConceptExtension.instance().assignName(event, activityName);
				XTimeExtension.instance().assignTimestamp(event, new Date());
				XTrace eventWrapper = xesFactory.createTrace();
				XConceptExtension.instance().assignName(eventWrapper, caseId);
				eventWrapper.add(event);
				ps.onNext(eventWrapper);
			}
			
			@Override
			public void deliveryComplete(IMqttDeliveryToken token) { }
			
			@Override
			public void connectionLost(Throwable cause) { }
		});
		myClient.connect(options);
		myClient.subscribe(topicBase + "/" + processName + "/#");
	}

}
