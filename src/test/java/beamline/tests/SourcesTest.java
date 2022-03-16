package beamline.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.opencsv.CSVParserBuilder;

import beamline.exceptions.SourceException;
import beamline.sources.CSVLogSource;
import beamline.sources.XesLogSource;
import beamline.utils.EventUtils;

public class SourcesTest {

	@Test
	public void test_csv_source_1() {
		List<String> acts = new LinkedList<>();
		List<String> caseIds = new LinkedList<>();
		CSVLogSource s = new CSVLogSource("src/test/resources/sources/source.csv", 0, 1);
		try {
			s.prepare();
		} catch (SourceException e) {
			e.printStackTrace();
		}
		s.getObservable().subscribe((t) -> {
			acts.add(EventUtils.getActivityName(t));
			caseIds.add(EventUtils.getCaseId(t));
		});
		
		assertEquals(5, acts.size());
		assertEquals(5, caseIds.size());
		
		assertThat(acts, hasItems("a11","a21","a22","a12","a23"));
		assertThat(caseIds, hasItems("c1","c2","c2","c1","c2"));
	}
	
	@Test
	public void test_csv_source_2() {
		List<String> acts = new LinkedList<>();
		List<String> caseIds = new LinkedList<>();
		CSVLogSource s = new CSVLogSource(
				"src/test/resources/sources/source_2.csv",
				0,
				1,
				new CSVParserBuilder().withSeparator('|').build());
		try {
			s.prepare();
		} catch (SourceException e) {
			e.printStackTrace();
		}
		s.getObservable().subscribe((t) -> {
			acts.add(EventUtils.getActivityName(t));
			caseIds.add(EventUtils.getCaseId(t));
		});
		
		assertEquals(5, acts.size());
		assertEquals(5, caseIds.size());
		
		assertThat(acts, hasItems("a11","a21","a22","a12","a23"));
		assertThat(caseIds, hasItems("c1","c2","c2","c1","c2"));
	}
	
	@Test
	public void test_csv_source_3() {
		CSVLogSource s = new CSVLogSource("DOESNT_EXIST", 0, 1);
		assertThrowsExactly(SourceException.class, () -> s.prepare());
	}
	
	@Test
	public void test_xes_source_1() {
		XesLogSource s1 = new XesLogSource("src/test/resources/sources/empty.xes");
		assertThrowsExactly(SourceException.class, () -> s1.prepare());
		
		XesLogSource s2 = new XesLogSource("src/test/resources/sources/empty_2.xes");
		assertThrowsExactly(SourceException.class, () -> s2.prepare());

		XesLogSource s3 = new XesLogSource("src/test/resources/sources/empty.csv");
		assertThrowsExactly(SourceException.class, () -> s3.prepare());
	}
	
	@Test
	public void test_xes_source_2() {
		List<String> acts = new LinkedList<>();
		List<String> caseIds = new LinkedList<>();
		XesLogSource s = new XesLogSource(Utils.generteXLog());
		try {
			s.prepare();
		} catch (SourceException e) {
			e.printStackTrace();
		}
		s.getObservable().subscribe((t) -> {
			acts.add(EventUtils.getActivityName(t));
			caseIds.add(EventUtils.getCaseId(t));
		});
		
		assertEquals(9, acts.size());
		assertEquals(9, caseIds.size());
		
		assertThat(acts, hasItems("K","C","A","I","B","O","A","A","C"));
		assertThat(caseIds, hasItems("c1","c2","c1","c2","c1","c2","c1","c2","c1"));
	}
	
	@Test
	public void test_xes_source_3() {
		List<String> acts = new LinkedList<>();
		List<String> caseIds = new LinkedList<>();
		XesLogSource s = new XesLogSource("src/test/resources/sources/source.xes.gz");
		try {
			s.prepare();
		} catch (SourceException e) {
			e.printStackTrace();
		}
		s.getObservable().subscribe((t) -> {
			acts.add(EventUtils.getActivityName(t));
			caseIds.add(EventUtils.getCaseId(t));
		});
		
		assertEquals(5, acts.size());
		assertEquals(5, caseIds.size());
		
		assertThat(acts, hasItems("a11","a21","a22","a12","a23"));
		assertThat(caseIds, hasItems("c1","c2","c2","c1","c2"));
	}
}
