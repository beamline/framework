package beamline.sources;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import beamline.events.BEvent;
import beamline.exceptions.SourceException;

/**
 * This implementation of a {@link BeamlineAbstractSource} produces events as
 * they are observed in an MQTT-XES broker.
 * 
 * <p>
 * Example of usage:
 * <pre>
 * XesSource source = new MQTTXesSource("tcp://broker.hivemq.com:1883", "topicBase", "processName");
 * source.prepare();
 * </pre>
 * 
 * <p>
 * See also the documentation of MQTT-XES at http://www.beamline.cloud/mqtt-xes/
 * 
 * @author Andrea Burattin
 */
public class MQTTXesSource extends BeamlineAbstractSource {

	private static final long serialVersionUID = 7849358403852399322L;
	private String processName;
	private String brokerHost;
	private String topicBase;
	
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
	}
	
	@Override
	public void run(SourceContext<BEvent> ctx) throws Exception {
		Queue<BEvent> buffer = new LinkedList<>();
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(true);
		options.setKeepAliveInterval(30);
		
		IMqttClient myClient = null;
		try {
			myClient = new MqttClient(brokerHost, UUID.randomUUID().toString());
			myClient.setCallback(new MqttCallback() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					int posLastSlash = topic.lastIndexOf("/");
					String partBeforeActName = topic.substring(0, posLastSlash);
					String activityName = topic.substring(posLastSlash + 1);
					String caseId = partBeforeActName.substring(partBeforeActName.lastIndexOf("/") + 1);
					BEvent b = new BEvent(processName, caseId, activityName);
					buffer.add(b);
				}
				
				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
					// no need to have anything here
				}
				
				@Override
				public void connectionLost(Throwable cause) {
					// no need to have anything here
				}
			});
			myClient.connect(options);
			myClient.subscribe(topicBase + "/" + processName + "/#");
		} catch (MqttException e) {
			throw new SourceException(e.getMessage());
		}
		
		while(isRunning()) {
			while (isRunning() && buffer.isEmpty()) {
				Thread.sleep(100l);
			}
			if (isRunning()) {
				synchronized (ctx.getCheckpointLock()) {
					BEvent e = buffer.poll();
					ctx.collect(e);
				}
			}
		}
		
		if (!isRunning() && myClient.isConnected()) {
			try {
				myClient.disconnect();
			} catch (MqttException e) {
				// nothing to do here
			}
		}
	}
}
