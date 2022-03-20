package beamline.filters;

import org.deckfour.xes.extension.std.XConceptExtension;

/**
 * A specific instance of the {@link RetainOnEventAttributeEqualityFilter} that
 * considers the name of the activity as attribute to filter.
 * 
 * @author Andrea Burattin
 *
 */
public class RetainActivitiesFilter extends RetainOnEventAttributeEqualityFilter<String> {

	private static final long serialVersionUID = 102039300555271213L;

	/**
	 * Constructors
	 * 
	 * @param activities the sequence of activity names to retain
	 */
	public RetainActivitiesFilter(String ...activities) {
		super(XConceptExtension.KEY_NAME);
		
		for (String activity : activities) {
			addValue(activity);
		}
	}
}
