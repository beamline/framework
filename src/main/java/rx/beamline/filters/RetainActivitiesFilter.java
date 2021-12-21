package rx.beamline.filters;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

public class RetainActivitiesFilter extends RetainOnEventAttributeEqualityFilter<XAttributeLiteral> {

	public RetainActivitiesFilter(String ...activities) {
		super(XConceptExtension.KEY_NAME);
		
		for (String activity : activities) {
			addValue(new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, activity));
		}
	}
}
