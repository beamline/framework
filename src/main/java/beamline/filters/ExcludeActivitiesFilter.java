package beamline.filters;

import org.deckfour.xes.extension.std.XConceptExtension;

/**
 * A specific instance of the {@link ExcludeOnEventAttributeEqualityFilter} that
 * considers the name of the activity as attribute to filter.
 * 
 * @author Andrea Burattin
 *
 */
public class ExcludeActivitiesFilter extends ExcludeOnEventAttributeEqualityFilter<String> {

	private static final long serialVersionUID = -5319332746992005641L;

	/**
	 * Constructors
	 * 
	 * @param activities the sequence of activity names to exclude
	 */
	public ExcludeActivitiesFilter(String ...activities) {
		super(XConceptExtension.KEY_NAME);
		
		for (String activity : activities) {
			addValue(activity);
		}
	}
}
