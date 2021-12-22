package beamline.sources;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

public class MQTTXesSource implements XesSource {

	private String processName;
	private String brokerHost;
	private String topicBase;
	
	public MQTTXesSource(String brokerHost, String topicBase, String processName) {
		this.brokerHost = brokerHost;
		this.topicBase = topicBase;
		this.processName = processName;
//		this.stream = Stream.generate(new Supplier<XTrace>() {
//			@Override
//			public XTrace get() {
//				try {
//					return queue.take();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//		});
	}
	
	@Override
	public Observable<XTrace> getObservable() {
		return Observable.create(new ObservableOnSubscribe<XTrace>() {

			@Override
			public void subscribe(@NonNull ObservableEmitter<@NonNull XTrace> emitter) throws Throwable {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	@Override
	public void prepare() throws Exception {
//		MqttConnectOptions options = new MqttConnectOptions();
//		options.setCleanSession(true);
//		options.setKeepAliveInterval(30);
//
//		IMqttClient myClient = new MqttClient(brokerHost, UUID.randomUUID().toString());
//		myClient.setCallback(new MqttCallback() {
//			
//			@Override
//			public void messageArrived(String topic, MqttMessage message) throws Exception {
//				int posLastSlash = topic.lastIndexOf("/");
//				String partBeforeActName = topic.substring(0, posLastSlash);
//				String activityName = topic.substring(posLastSlash + 1);
//				String caseId = partBeforeActName.substring(partBeforeActName.lastIndexOf("/") + 1);
//
//				XEvent event = xesFactory.createEvent();
//				XConceptExtension.instance().assignName(event, activityName);
//				XTimeExtension.instance().assignTimestamp(event, new Date());
//				XTrace eventWrapper = xesFactory.createTrace();
//				XConceptExtension.instance().assignName(eventWrapper, caseId);
//				eventWrapper.add(event);
//				
//				queue.add(eventWrapper);
//			}
//			
//			@Override
//			public void deliveryComplete(IMqttDeliveryToken token) { }
//			
//			@Override
//			public void connectionLost(Throwable cause) { }
//		});
//		myClient.connect(options);
//		myClient.subscribe(topicBase + "/" + processName + "/#");
	}

}
