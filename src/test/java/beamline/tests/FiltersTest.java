package beamline.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import beamline.events.BEvent;
import beamline.exceptions.EventException;
import beamline.filters.ExcludeActivitiesFilter;
import beamline.filters.ExcludeOnCaseAttributeEqualityFilter;
import beamline.filters.RetainActivitiesFilter;
import beamline.filters.RetainOnCaseAttributeEqualityFilter;

public class FiltersTest {

	@Test
	public void test_exclude_activities_on_name_filter() throws EventException, Exception {
		ExcludeActivitiesFilter f = new ExcludeActivitiesFilter("A");
		
		assertTrue(f.filter(new BEvent("", "", "B")));
		assertFalse(f.filter(new BEvent("", "", "A")));
	}
	
	@Test
	public void test_retain_activities_on_name_filter() throws EventException {
		RetainActivitiesFilter f = new RetainActivitiesFilter("A");

		assertTrue(f.filter(new BEvent("", "", "A")));
		assertFalse(f.filter(new BEvent("", "", "B")));
	}
	
	@Test
	public void test_retain_activities_on_case_attribute_filter() throws EventException {
		BEvent e1 = new BEvent("", "", "");
		BEvent e2 = new BEvent("", "", "");
		
		e1.getTraceAttributes().put("a", "v1");
		e2.getTraceAttributes().put("a", "v2");
		
		RetainOnCaseAttributeEqualityFilter<String> f = new RetainOnCaseAttributeEqualityFilter<String>("a", "v1");
		
		assertTrue(f.filter(e1));
		assertFalse(f.filter(e2));
	}
	
	@Test
	public void test_exclude_activities_on_case_attribute_filter() throws EventException {
		BEvent e1 = new BEvent("", "", "");
		BEvent e2 = new BEvent("", "", "");
		
		e1.getTraceAttributes().put("a", "v1");
		e2.getTraceAttributes().put("a", "v2");
		
		ExcludeOnCaseAttributeEqualityFilter<String> f = new ExcludeOnCaseAttributeEqualityFilter<String>("a", "v1");
		
		assertFalse(f.filter(e1));
		assertTrue(f.filter(e2));
	}
	
}
