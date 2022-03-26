package beamline.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;
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
import org.deckfour.xes.out.XesXmlGZIPSerializer;

import beamline.events.BEvent;
import beamline.exceptions.EventException;
import beamline.exceptions.SourceException;

/**
 * This implementation of a {@link BeamlineAbstractSource} produces events
 * according to the events contained in an {@link XLog}. The events are first
 * sorted according to their timestamp and then sent.
 * 
 * @author Andrea Burattin
 */
public class XesLogSource extends BeamlineAbstractSource {

	private static final long serialVersionUID = 1095855454671335981L;

	private String fileName;
	private List<BEvent> events;
	
	/**
	 * Constructs a source from the path of a log
	 * 
	 * @param fileName the file containing the log to use. The file can be
	 * either a file parsed by {@link XesXmlGZIPParser} or {@link XesXmlParser}
	 * (i.e., extensions <code>.xes.gz</code> or <code>.xes</code>) or any other
	 * parser currently supported by the OpenXES library.
	 */
	public XesLogSource(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Constructs a source from the given log
	 * 
	 * @param log the log to use as source
	 * @throws IOException an exception that might occur when a temporary file
	 * is created
	 */
	public XesLogSource(XLog log) throws IOException {
		Path tmpFile = getTempFile();
		new XesXmlGZIPSerializer().serialize(log, new FileOutputStream(tmpFile.toFile()));
		this.fileName = tmpFile.toFile().getAbsolutePath();
	}
	
	@Override
	public void run(SourceContext<BEvent> ctx) throws Exception {
		if (events == null) {
			prepareStream(parseLog(fileName));
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
	
	private XLog parseLog(String fileName) throws SourceException {
		XParser[] parsers = new XParser[] {
				new XesXmlGZIPParser(),
				new XesXmlParser(),
				new XMxmlParser(),
				new XMxmlGZIPParser() };
		File file = new File(fileName);
		for (XParser p : parsers) {
			if (p.canParse(file)) {
				try {
					return p.parse(file).get(0);
				} catch (Exception e) {
					throw new SourceException(e.getMessage());
				}
			}
		}
		throw new SourceException("XES file format not supported");
	}
	
	private void prepareStream(XLog log) throws SourceException, EventException {
		if (log.isEmpty()) {
			throw new SourceException("The given log is empty");
		}
		
		// construct the process name
		String processName = XConceptExtension.instance().extractName(log);
		if (processName == null) {
			processName = "unnamed-xes-process";
		}
		
		// populate all events
		events = new LinkedList<>();
		for (XTrace t : log) {
			for (XEvent e : t) {
				BEvent be = BEvent.create(
					processName,
					XConceptExtension.instance().extractName(t),
					XConceptExtension.instance().extractName(e),
					XTimeExtension.instance().extractTimestamp(e));
				
				// log attributes
				for (Map.Entry<String, XAttribute> v : log.getAttributes().entrySet()) {
					be.setProcessAttribute(v.getKey(), v.getValue());
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
	
	private Path getTempFile() throws IOException {
		Path p = null;
		if (SystemUtils.IS_OS_UNIX) {
			FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
			p = Files.createTempFile("log", ".xes.gz", attr);
		} else {
			File f = Files.createTempFile("log", ".xes.gz").toFile();
			boolean configured =
					f.setReadable(true, true) &&
					f.setWritable(true, true) &&
					f.setExecutable(true, true);
			if (!configured) {
				// potential issue with unable to configure all flags
			}
			p = f.toPath();
		}
		return p;
	}
}
