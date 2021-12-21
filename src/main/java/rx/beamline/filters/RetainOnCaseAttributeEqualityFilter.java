package rx.beamline.filters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XTrace;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Predicate;

public class RetainOnCaseAttributeEqualityFilter<T extends XAttribute> implements Predicate<XTrace> {

	private String attributeName;
	private Set<T> attributeValues;
	
	@SafeVarargs
	public RetainOnCaseAttributeEqualityFilter(String attributeName, T ...values) {
		this.attributeName = attributeName;
		this.attributeValues = new HashSet<T>(Arrays.asList(values));
	}
	
	public void addValue(T value) {
		this.attributeValues.add(value);
	}
	
	@Override
	public boolean test(@NonNull XTrace t) throws Throwable {
		return attributeValues.contains(t.getAttributes().get(attributeName));
	}

}
