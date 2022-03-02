package beamline.sources;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

public class CSVLogSource implements XesSource {

	private static XFactory xesFactory = new XFactoryNaiveImpl();
	private CSVReader csvReader;
	private String filename;
	private int caseIdColumn;
	private int activityNameColumn;
	private CSVParser parser;
	
	public CSVLogSource(String filename, int caseIdColumn, int activityNameColumn, CSVParser parser) throws IOException {
		this.filename = filename;
		this.caseIdColumn = caseIdColumn;
		this.activityNameColumn = activityNameColumn;
		this.parser = parser;
	}
	
	public CSVLogSource(String filename, int caseIdColumn, int activityNameColumn) throws IOException {
		this(filename, caseIdColumn, activityNameColumn, null);
	}

	@Override
	public Observable<XTrace> getObservable() {
		return Observable.create(new ObservableOnSubscribe<XTrace>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<@NonNull XTrace> emitter) throws Throwable {
				String[] line;
				while ((line = csvReader.readNext()) != null) {
					XTrace eventWrapper = xesFactory.createTrace();
					XEvent newEvent = xesFactory.createEvent();
					XConceptExtension.instance().assignName(eventWrapper, line[caseIdColumn]);
					XConceptExtension.instance().assignName(newEvent, line[activityNameColumn]);
					for (int i = 0; i < line.length; i++) {
						String attributeName = "attribute_" + i;
						newEvent.getAttributes().put(attributeName, xesFactory.createAttributeLiteral(attributeName, line[i], null));
					}
					eventWrapper.add(newEvent);
					emitter.onNext(eventWrapper);
				}
			}
		});
	}

	@Override
	public void prepare() throws Exception {
		Reader reader = Files.newBufferedReader(Paths.get(filename));
		if (parser == null) {
			csvReader = new CSVReader(reader);
		} else {
			csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();
		}
	}

}
