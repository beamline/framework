package beamline.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import beamline.mappers.DirectlyFollowsRelation;
import beamline.mappers.InfiniteSizeDirectlyFollowsMapper;

public class MapperTest {

	@Test
	public void test_infinite_size_df() {
		List<DirectlyFollowsRelation> results = new ArrayList<>();
		// <K,A,B,A,C>
		Utils.generateObservableSameCaseId()
			.flatMap(new InfiniteSizeDirectlyFollowsMapper())
			.subscribe((df) -> results.add(df));
		
		assertEquals(4, results.size());
		assertTrue(Utils.verifyDirectFollows(results.get(0), "K", "A", "c"));
		assertTrue(Utils.verifyDirectFollows(results.get(1), "A", "B", "c"));
		assertTrue(Utils.verifyDirectFollows(results.get(2), "B", "A", "c"));
		assertTrue(Utils.verifyDirectFollows(results.get(3), "A", "C", "c"));
	}
}
