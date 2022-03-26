package beamline.sources;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.ICSVParser;

import beamline.events.BEvent;
import beamline.exceptions.SourceException;

/**
 * This implementation of a {@link BeamlineAbstractSource} produces events
 * according to the events contained in a CSV file.
 * 
 * @author Andrea Burattin
 */
public class CSVLogSource extends BeamlineAbstractSource {

	private static final long serialVersionUID = 205574514393782145L;
	private CSVLogSource.ParserConfiguration parserConfiguration;
	private String filename;
	private int caseIdColumn;
	private int activityNameColumn;
	
	/**
	 * Constructs the source by providing a CSV parser.
	 * 
	 * @param filename the absolute path of the CSV file
	 * @param caseIdColumn the id of the column containing the case id (counting
	 * starts from 0)
	 * @param activityNameColumn the id of the column containing the activity
	 * name (counting starts from 0)
	 * @param parserConfiguration the parser configuration to be used for
	 * parsing the CSV file
	 */
	public CSVLogSource(String filename, int caseIdColumn, int activityNameColumn, CSVLogSource.ParserConfiguration parserConfiguration) {
		this.filename = filename;
		this.caseIdColumn = caseIdColumn;
		this.activityNameColumn = activityNameColumn;
		this.parserConfiguration = parserConfiguration;
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
		this(filename, caseIdColumn, activityNameColumn, new CSVLogSource.ParserConfiguration());
	}
	
	@Override
	public void run(SourceContext<BEvent> ctx) throws Exception {
		Reader reader = null;
		CSVReader csvReader = null;
		try {
			CSVParser parser = new CSVParserBuilder()
					.withSeparator(parserConfiguration.separator)
					.build();
			reader = Files.newBufferedReader(Paths.get(filename));
			csvReader = new CSVReaderBuilder(reader)
					.withCSVParser(parser)
					.build();
			
			String[] line;
			while ((line = csvReader.readNext()) != null && isRunning()) {
				List<Pair<String, String>> attributes = new LinkedList<>();
				for (int i = 0; i < line.length; i++) {
					attributes.add(Pair.of("attribute_" + i, line[i]));
				}
				synchronized (ctx.getCheckpointLock()) {
					ctx.collect(new BEvent(filename, line[caseIdColumn], line[activityNameColumn], null, attributes));
				}
			}
		} catch (IOException e) {
			throw new SourceException(e.getMessage());
		} finally {
			if (csvReader != null) {
				csvReader.close();
			}
		}
	}
	
	/**
	 * This class is used to configure the parser of the CSV
	 *
	 * @author Andrea Burattin
	 */
	public static class ParserConfiguration implements Serializable {
		
		private static final long serialVersionUID = 375203248074405954L;
		char separator = ICSVParser.DEFAULT_SEPARATOR;
		
		/**
		 * Configures the fields separator
		 *
		 * @param separator the separator for the lines' fields
		 * @return the parser configuration object
		 */
		public ParserConfiguration withSeparator(char separator) {
			this.separator = separator;
			return this;
		}
	}
}
