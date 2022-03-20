package beamline.filters;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.flink.api.common.functions.FilterFunction;

import beamline.events.BEvent;

/**
 * This filter excludes events based on the equality of a certain trace
 * attribute to a given set of values. Values are considered in disjunction
 * (i.e., it is enough that the attribute is equal to one of the values to
 * discard the event).
 * 
 * @author Andrea Burattin
 *
 * @param <T> the type of the attribute
 */
public class ExcludeOnCaseAttributeEqualityFilter<T extends Serializable> implements FilterFunction<BEvent> {

	private static final long serialVersionUID = 371257881178171433L;
	private String attributeName;
	private Set<T> attributeValues;
	
	/**
	 * Constructor
	 * 
	 * @param attributeName the name of the trace attribute
	 * @param values the sequence of values to consider
	 */
	@SafeVarargs
	public ExcludeOnCaseAttributeEqualityFilter(String attributeName, T ...values) {
		this.attributeName = attributeName;
		this.attributeValues = new HashSet<>(Arrays.asList(values));
	}
	
	@Override
	public boolean filter(BEvent t) {
		return !attributeValues.contains(t.getTraceAttributes().get(attributeName));
	}

}
