package beamline.filters;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.flink.api.common.functions.FilterFunction;

import beamline.events.BEvent;

/**
 * This filter retains events based on the equality of a certain event
 * attribute to a given set of values. Values are considered in disjunction
 * (i.e., it is enough that the attribute is equal to one of the values to
 * retain the event).
 * 
 * @author Andrea Burattin
 *
 * @param <T> the type of the attribute
 */
public class RetainOnEventAttributeEqualityFilter<T extends Serializable> implements FilterFunction<BEvent> {

	private static final long serialVersionUID = -720485056040728235L;
	private String attributeName;
	private Set<T> attributeValues;
	
	/**
	 * Constructor
	 * 
	 * @param attributeName the name of the event attribute
	 * @param values the sequence of values to consider
	 */
	@SafeVarargs
	public RetainOnEventAttributeEqualityFilter(String attributeName, T ...values) {
		this.attributeName = attributeName;
		this.attributeValues = new HashSet<>(Arrays.asList(values));
	}
	
	/**
	 * Adds the value to the list of values to be considered for retention
	 * 
	 * @param value value
	 */
	public void addValue(T value) {
		this.attributeValues.add(value);
	}
	
	@Override
	public boolean filter(BEvent event) {
		return attributeValues.contains(event.getEventAttributes().get(attributeName));
	}
}
