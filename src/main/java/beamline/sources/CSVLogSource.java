package beamline.sources;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.deckfour.xes.model.XTrace;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import beamline.utils.EventUtils;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

/**
 * This implementation of a {@link XesSource} produces events according to the
 * events contained in a CSV file. This source produces a cold observable.
 * 
 * @author Andrea Burattin
 */
public class CSVLogSource implements XesSource {

	private CSVReader csvReader;
	private String filename;
	private int caseIdColumn;
	private int activityNameColumn;
	private CSVParser parser;
	
	/**
	 * Constructs the source by providing a CSV parser.
	 * 
	 * <p>
	 * A parser can be produced, for example with the following code:
	 * <pre>
	 * CSVParser parser = new CSVParserBuilder()
	 *     .withSeparator(',')
	 *     .withIgnoreQuotations(true)
	 *     .build();
	 * </pre>
	 * 
	 * @param filename the absolute path of the CSV file
	 * @param caseIdColumn the id of the column containing the case id (counting
	 * starts from 0)
	 * @param activityNameColumn the id of the column containing the activity
	 * name (counting starts from 0)
	 * @param parser the parser to be used for parsing the CSV file
	 */
	public CSVLogSource(String filename, int caseIdColumn, int activityNameColumn, CSVParser parser) {
		this.filename = filename;
		this.caseIdColumn = caseIdColumn;
		this.activityNameColumn = activityNameColumn;
		this.parser = parser;
	}
	
	/**
	 * Constructs the source
	 * 
	 * @param filename the absolute path of the CSV file
	 * @param caseIdColumn the id of the column containing the case id (counting
	 * starts from 0)
	 * @param activityNameColumn the id of the column containing the activity
	 * name (counting starts from 0)
	 */
	public CSVLogSource(String filename, int caseIdColumn, int activityNameColumn) {
		this(filename, caseIdColumn, activityNameColumn, null);
	}

	@Override
	public Observable<XTrace> getObservable() {
		return Observable.create(new ObservableOnSubscribe<XTrace>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<@NonNull XTrace> emitter) throws Throwable {
				String[] line;
				while ((line = csvReader.readNext()) != null) {
					List<Pair<String, String>> attributes = new LinkedList<Pair<String, String>>();
					for (int i = 0; i < line.length; i++) {
						attributes.add(Pair.of("attribute_" + i, line[i]));
					}
					emitter.onNext(EventUtils.create(line[activityNameColumn], line[caseIdColumn], null, attributes));
				}
				emitter.onComplete();
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
