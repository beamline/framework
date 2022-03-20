package beamline.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.junit.jupiter.api.Test;

import beamline.filters.ExcludeActivitiesFilter;
import beamline.filters.ExcludeOnCaseAttributeEqualityFilter;
import beamline.filters.RetainActivitiesFilter;
import beamline.filters.RetainOnCaseAttributeEqualityFilter;
import beamline.utils.EventUtils;

public class FiltersTest {

	@Test
	public void test_exclude_activities_on_name_filter() {
		
		FilterFunction f = new ExcludeActivitiesFilter("A");
		
		List<String> out = new ArrayList<>();
		ListCollector<String> listCollector = new ListCollector<>(out);
		
		f.fil
		
		
//		List<String> results = new ArrayList<String>();
//		Utils.generateObservableSameCaseId()
//			.filter(new ExcludeActivitiesFilter("A"))
//			.subscribe((t) -> results.add(EventUtils.getActivityName(t)));
//		assertEquals(3, results.size());
//		assertThat(results, hasItems("K","B","C"));
	}
	
	@Test
	public void test_retain_activities_on_name_filter() {
		List<String> results = new ArrayList<String>();
		Utils.generateObservableSameCaseId()
			.filter(new RetainActivitiesFilter("A","B"))
			.subscribe((t) -> results.add(EventUtils.getActivityName(t)));
		assertEquals(3, results.size());
		assertThat(results, hasItems("A","B","A"));
	}
	
	@Test
	public void test_retain_activities_on_case_attribute_filter_1() {
		List<String> results = new ArrayList<String>();
		Utils.generateObservableSameCaseId()
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
		Utils.generateObservableSameCaseId()
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
		Utils.generateObservableSameCaseId()
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
		Utils.generateObservableSameCaseId()
			.filter(new ExcludeOnCaseAttributeEqualityFilter<XAttributeLiteralImpl>(
					"a1",
					new XAttributeLiteralImpl("a1", "v1"),
					new XAttributeLiteralImpl("a1", "v4")))
			.subscribe((t) -> results.add(EventUtils.getActivityName(t)));
		assertEquals(3, results.size());
		assertThat(results, hasItems("K","B","A"));
	}
}
