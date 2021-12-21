package rx.beamline.sources;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

public class XesLogSource implements XesSource {

private static XFactory xesFactory = new XFactoryNaiveImpl();
	
	private String fileName;
	private XLog log;
	private List<XTrace> events;
	
	public XesLogSource(String fileName) {
		this.fileName = fileName;
	}
	
	public XesLogSource(XLog log) {
		this.log = log;
	}
	
	public Observable<XTrace> getObservable() {
		return Observable.create(new ObservableOnSubscribe<XTrace>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<@NonNull XTrace> emitter) throws Throwable {
				for (XTrace wrapper : events) {
					emitter.onNext(wrapper);
				}
			}
		});
	}
	
	public void prepare() throws Exception {
		if (log == null) {
			parseLog(fileName);
		}
		prepareStream();
	}
	
	private void parseLog(String fileName) throws Exception {
		XParser[] parsers = new XParser[] { new XesXmlGZIPParser(), new XesXmlParser() };
		File file = new File(fileName);
		for (XParser p : parsers) {
			if (p.canParse(file)) {
				log = p.parse(file).get(0);
				return;
			}
		}
		throw new Exception("XES file format not supported");
	}
	
	private void prepareStream() {
		if (log == null) {
			return;
		}
		// populate all events
		events = new LinkedList<XTrace>();
		for (XTrace t : log) {
			for (XEvent e : t) {
				// create the wrapping trace
				XTrace eventWrapper = xesFactory.createTrace();
				XAttributeMap am = t.getAttributes();
				for (String key : am.keySet()) {
					eventWrapper.getAttributes().put(key, am.get(key));
				}
				// create the actual event
				XEvent newEvent = xesFactory.createEvent();
				XAttributeMap amEvent = e.getAttributes();
				for (String key : amEvent.keySet()) {
					newEvent.getAttributes().put(key, amEvent.get(key));
				}
				eventWrapper.add(newEvent);
				events.add(eventWrapper);
			}
		}
		
		// sort events
		Collections.sort(events, new Comparator<XTrace>() {
			public int compare(XTrace o1, XTrace o2) {
				XEvent e1 = o1.get(0);
				XEvent e2 = o2.get(0);
				Date d1 = XTimeExtension.instance().extractTimestamp(e1);
				Date d2 = XTimeExtension.instance().extractTimestamp(e2);
				if (d1 == null || d2 == null) {
					return 0;
				}
				return d1.compareTo(d2);
			}
		});
	}
}
