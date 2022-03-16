package beamline.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.junit.jupiter.api.Test;

import beamline.exceptions.EventException;
import beamline.filters.ExcludeActivitiesFilter;
import beamline.filters.ExcludeOnCaseAttributeEqualityFilter;
import beamline.filters.RetainActivitiesFilter;
import beamline.filters.RetainOnCaseAttributeEqualityFilter;
import beamline.utils.EventUtils;
import io.reactivex.rxjava3.core.Observable;

public class FiltersTest {

	@Test
	public void test_exclude_activities_on_name_filter() {
		List<String> results = new ArrayList<String>();
		generateObservableSameCaseId()
			.filter(new ExcludeActivitiesFilter("A"))
			.subscribe((t) -> results.add(EventUtils.getActivityName(t)));
		assertEquals(3, results.size());
		assertThat(results, hasItems("K","B","C"));
	}
	
	@Test
	public void test_retain_activities_on_name_filter() {
		List<String> results = new ArrayList<String>();
		generateObservableSameCaseId()
			.filter(new RetainActivitiesFilter("A","B"))
			.subscribe((t) -> results.add(EventUtils.getActivityName(t)));
		assertEquals(3, results.size());
		assertThat(results, hasItems("A","B","A"));
	}
	
	@Test
	public void test_retain_activities_on_case_attribute_filter_1() {
		List<String> results = new ArrayList<String>();
		generateObservableSameCaseId()
			.filter(new RetainOnCaseAttributeEqualityFilter<XAttributeLiteralImpl>(
					"a1",
					new XAttributeLiteralImpl("a1", "v1")))
			.subscribe((t) -> results.add(EventUtils.getActivityName(t)));
		assertEquals(1, results.size());
		assertThat(results, hasItems("A"));
	}
	
	@Test
	public void test_retain_activities_on_case_attribute_filter_2() {
		List<String> results = new ArrayList<String>();
		generateObservableSameCaseId()
			.filter(new RetainOnCaseAttributeEqualityFilter<XAttributeLiteralImpl>(
					"a1",
					new XAttributeLiteralImpl("a1", "v1"),
					new XAttributeLiteralImpl("a1", "v4")))
			.subscribe((t) -> results.add(EventUtils.getActivityName(t)));
		assertEquals(2, results.size());
		assertThat(results, hasItems("A","C"));
	}
	
	@Test
	public void test_exclude_activities_on_case_attribute_filter_1() {
		List<String> results = new ArrayList<String>();
		generateObservableSameCaseId()
			.filter(new ExcludeOnCaseAttributeEqualityFilter<XAttributeLiteralImpl>(
					"a1",
					new XAttributeLiteralImpl("a1", "v1")))
			.subscribe((t) -> results.add(EventUtils.getActivityName(t)));
		assertEquals(4, results.size());
		assertThat(results, hasItems("K","B","A","C"));
	}
	
	@Test
	public void test_exclude_activities_on_case_attribute_filter_2() {
		List<String> results = new ArrayList<String>();
		generateObservableSameCaseId()
			.filter(new ExcludeOnCaseAttributeEqualityFilter<XAttributeLiteralImpl>(
					"a1",
					new XAttributeLiteralImpl("a1", "v1"),
					new XAttributeLiteralImpl("a1", "v4")))
			.subscribe((t) -> results.add(EventUtils.getActivityName(t)));
		assertEquals(3, results.size());
		assertThat(results, hasItems("K","B","A"));
	}
	
	/*
	 * Generate a streams with these events:
	 * - K
	 * - A / trace attribute: (a1,v1)
	 * - B
	 * - A
	 * - C / trace attribute: (a1,v4)
	 */
	private Observable<XTrace> generateObservableSameCaseId() {
		XTrace[] events = null;
		try {
			events = new XTrace[] {
				EventUtils.create("K", "c"),
				EventUtils.create("A", "c"),
				EventUtils.create("B", "c"),
				EventUtils.create("A", "c"),
				EventUtils.create("C", "c")
			};
		} catch (EventException e) {
			e.printStackTrace();
		}
		events[1].getAttributes().put("a1", new XAttributeLiteralImpl("a1", "v1"));
		events[2].get(0).getAttributes().put("a2", new XAttributeLiteralImpl("a2", "v3"));
		events[3].get(0).getAttributes().put("a2", new XAttributeLiteralImpl("a2", "v2"));
		events[4].getAttributes().put("a1", new XAttributeLiteralImpl("a1", "v4"));
		return Observable.fromArray(events);
	}
}
