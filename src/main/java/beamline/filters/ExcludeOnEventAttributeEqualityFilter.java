package beamline.filters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XTrace;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Predicate;

/**
 * This filter excludes events based on the equality of a certain event
 * attribute to a given set of values. Values are considered in disjunction
 * (i.e., it is enough that the attribute is equal to one of the values to
 * discard the event).
 * 
 * @author Andrea Burattin
 *
 * @param <T> the type of the attribute
 */
public class ExcludeOnEventAttributeEqualityFilter<T extends XAttribute> implements Predicate<XTrace> {

	private String attributeName;
	private Set<T> attributeValues;
	
	/**
	 * Constructor
	 * 
	 * @param attributeName the name of the event attribute
	 * @param values the sequence of values to consider
	 */
	@SafeVarargs
	public ExcludeOnEventAttributeEqualityFilter(String attributeName, T ...values) {
		this.attributeName = attributeName;
		this.attributeValues = new HashSet<T>(Arrays.asList(values));
	}
	
	/**
	 * Adds the value to the list of values to be considered for removal
	 * 
	 * @param value value
	 */
	public void addValue(T value) {
		this.attributeValues.add(value);
	}
	
	@Override
	public boolean test(@NonNull XTrace t) throws Throwable {
		return !attributeValues.contains(t.get(0).getAttributes().get(attributeName));
	}

}
