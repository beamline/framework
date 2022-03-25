package beamline.models.algorithms;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.model.XTrace;

import beamline.events.BEvent;
import beamline.models.responses.DirectlyFollowsRelation;

/**
 * This mapper transforms a stream of {@link XTrace}s into a stream of
 * {@link DirectlyFollowsRelation}s. It transforms each pair of consequent
 * events appearing in the same case as a directly follows operator (generating
 * an object with type {@link DirectlyFollowsRelation}).
 * 
 * <p>
 * This mapper is called infinite because it's memory footprint will grow as the
 * number of case ids grows as well.
 * 
 * @author Andrea Burattin
 */
public class InfiniteSizeDirectlyFollowsMapper extends StreamMiningAlgorithm<DirectlyFollowsRelation> {

	private static final long serialVersionUID = 9114527510820073110L;
	private Map<String, BEvent> map = new HashMap<>();

	@Override
	public DirectlyFollowsRelation ingest(BEvent event) {
		String caseId = event.getTraceName();
		DirectlyFollowsRelation toRet = null;
		
		if (map.containsKey(caseId)) {
			toRet = new DirectlyFollowsRelation(map.get(caseId), event);
		}
		
		map.put(caseId, event);
		
		return toRet;
	}
}
