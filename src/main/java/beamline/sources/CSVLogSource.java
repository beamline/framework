package beamline.sources;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import beamline.events.BEvent;
import beamline.exceptions.SourceException;

/**
 * This implementation of a {@link BeamlineAbstractSource} produces events according to
 * the events contained in a CSV file.
 * 
 * @author Andrea Burattin
 */
public class CSVLogSource extends BeamlineAbstractSource {

	private static final long serialVersionUID = 205574514393782145L;
	private transient CSVParser parser;
	private String filename;
	private int caseIdColumn;
	private int activityNameColumn;
	
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
	public void run(SourceContext<BEvent> ctx) throws Exception {
		Reader reader = null;
		CSVReader csvReader = null;
		try {
			reader = Files.newBufferedReader(Paths.get(filename));
			if (parser == null) {
				csvReader = new CSVReader(reader);
			} else  {
				csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();
			}
			
			String[] line;
			while ((line = csvReader.readNext()) != null && isRunning()) {
				List<Pair<String, String>> attributes = new LinkedList<>();
				for (int i = 0; i < line.length; i++) {
					attributes.add(Pair.of("attribute_" + i, line[i]));
				}
				ctx.collect(BEvent.create(filename, line[activityNameColumn], line[caseIdColumn], null, attributes));
			}
		} catch (IOException e) {
			throw new SourceException(e.getMessage());
		} finally {
			if (csvReader != null) {
				csvReader.close();
			}
		}
	}
}
