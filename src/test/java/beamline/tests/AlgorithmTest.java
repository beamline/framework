package beamline.tests;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import beamline.models.algorithms.StreamMiningAlgorithm;
import io.reactivex.rxjava3.core.Observable;

public class AlgorithmTest {

	Observable<Integer> o = Observable.just(3, 7, 11, 13);
	
	@Test
	public void test_result() {
		StreamMiningAlgorithm<Integer, Integer> m = new StreamMiningAlgorithm<Integer, Integer>() {
			public Integer product = 1;
			
			@Override
			public Integer ingest(Integer event) {
				product *= event;
				setLatestResponse(-product);
				return product;
			}
		};
		
		o.subscribe(m);
		assertEquals(4, m.getProcessedEvents());
		assertEquals(3003, m.getLatestResponse());
	}
	
	@Test
	public void test_hooks() {
		StreamMiningAlgorithm<Integer, Integer> m = new StreamMiningAlgorithm<Integer, Integer>() {
			public Integer product = 1;
			
			@Override
			public Integer ingest(Integer event) {
				product *= event;
				setLatestResponse(-product);
				return product;
			}
		};
		
		List<Integer> resultsBefore = new ArrayList<Integer>();
		m.setOnBeforeEvent(() -> {
			resultsBefore.add(m.getProcessedEvents());
		});
		
		List<Integer> resultsAfter = new ArrayList<Integer>();
		m.setOnAfterEvent(() -> {
			resultsAfter.add(m.getProcessedEvents());
		});
		
		o.subscribe(m);
		
		assertThat(resultsBefore, hasItems(0,1,2,3));
		assertThat(resultsAfter, hasItems(1,2,3,4));
	}
}
