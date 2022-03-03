package beamline.filters;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

/**
 * A specific instance of the {@link ExcludeOnEventAttributeEqualityFilter} that
 * considers the name of the activity as attribute to filter.
 * 
 * @author Andrea Burattin
 *
 */
public class ExcludeActivitiesFilter extends ExcludeOnEventAttributeEqualityFilter<XAttributeLiteral> {

	/**
	 * Constructors
	 * 
	 * @param activities the sequence of activity names to exclude
	 */
	public ExcludeActivitiesFilter(String ...activities) {
		super(XConceptExtension.KEY_NAME);
		
		for (String activity : activities) {
			addValue(new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, activity));
		}
	}
}
