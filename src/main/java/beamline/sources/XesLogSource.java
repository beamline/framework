package beamline.sources;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.in.XMxmlGZIPParser;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import beamline.events.BEvent;
import beamline.exceptions.EventException;
import beamline.exceptions.SourceException;

/**
 * This implementation of a {@link BeamlineAbstractSource} produces events according to
 * the events contained in an {@link XLog}. The events are first sorted
 * according to their timestamp and then sent.
 * 
 * @author Andrea Burattin
 */
public class XesLogSource extends BeamlineAbstractSource {

	private static final long serialVersionUID = 1095855454671335981L;

	private String fileName;
	private transient XLog log;
	private List<BEvent> events;
	
	/**
	 * Constructs a source from the path of a log
	 * 
	 * @param fileName the file containing the log to use. The file can be
	 * either a file parsed by {@link XesXmlGZIPParser} or {@link XesXmlParser}
	 * (i.e., extensions <code>.xes.gz</code> or <code>.xes</code>). If the file
	 * is none of these, then {@link #prepare()} will throw an exception.
	 */
	public XesLogSource(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Constructs a source from the given log
	 * 
	 * @param log the log to use as source
	 */
	public XesLogSource(XLog log) {
		this.log = log;
	}
	
	@Override
	public void run(SourceContext<BEvent> ctx) throws Exception {
		if (log == null) {
			parseLog(fileName);
		}
		if (events == null) {
			prepareStream();
		}
		Iterator<BEvent> i = events.iterator();
		while(i.hasNext() && isRunning()) {
			BEvent event = i.next();
			if (event.getEventTime() != null) {
				synchronized (ctx.getCheckpointLock()) {
					ctx.collectWithTimestamp(event, event.getEventTime().getTime());
				}
			} else {
				synchronized (ctx.getCheckpointLock()) {
					ctx.collect(i.next());
				}
			}
		}
	}
	
	private void parseLog(String fileName) throws SourceException {
		XParser[] parsers = new XParser[] {
				new XesXmlGZIPParser(),
				new XesXmlParser(),
				new XMxmlParser(),
				new XMxmlGZIPParser() };
		File file = new File(fileName);
		for (XParser p : parsers) {
			if (p.canParse(file)) {
				try {
					log = p.parse(file).get(0);
				} catch (Exception e) {
					throw new SourceException(e.getMessage());
				}
				return;
			}
		}
		throw new SourceException("XES file format not supported");
	}
	
	private void prepareStream() throws SourceException, EventException {
		if (log.isEmpty()) {
			throw new SourceException("The given log is empty");
		}
		
		// construct the process name
		String processName = XConceptExtension.instance().extractName(log);
		if (processName == null) {
			processName = "unnamed-xes-process";
			if (fileName != null) {
				processName = fileName;
			}
		}
		
		// populate all events
		events = new LinkedList<>();
		for (XTrace t : log) {
			for (XEvent e : t) {
				BEvent be = BEvent.create(
					processName,
					XConceptExtension.instance().extractName(e),
					XConceptExtension.instance().extractName(t),
					XTimeExtension.instance().extractTimestamp(e));
				
				// log attributes
				for (Map.Entry<String, XAttribute> v : log.getAttributes().entrySet()) {
					be.setLogAttribute(v.getKey(), v.getValue());
				}
				
				// trace attributes
				for (Map.Entry<String, XAttribute> v : t.getAttributes().entrySet()) {
					be.setTraceAttribute(v.getKey(), v.getValue());
				}
				
				// event attributes
				for (Map.Entry<String, XAttribute> v : e.getAttributes().entrySet()) {
					be.setEventAttribute(v.getKey(), v.getValue());
				}
				
				events.add(be);
			}
		}
		
		// sort events
		Collections.sort(events);
	}
}
