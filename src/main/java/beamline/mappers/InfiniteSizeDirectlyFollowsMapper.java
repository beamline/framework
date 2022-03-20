package beamline.mappers;
/*
import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

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
 *
public class InfiniteSizeDirectlyFollowsMapper implements Function<XTrace, ObservableSource<DirectlyFollowsRelation>> {

	private Map<String, XEvent> map = new HashMap<>();
	
	@Override
	public @NonNull ObservableSource<DirectlyFollowsRelation> apply(@NonNull XTrace t) throws Throwable {
		String caseId = XConceptExtension.instance().extractName(t);
		DirectlyFollowsRelation toRet = null;
		
		if (map.containsKey(caseId)) {
			toRet = new DirectlyFollowsRelation(caseId, map.get(caseId), t.get(0));
		}
		
		map.put(caseId, t.get(0));
		
		if (toRet == null) {
			return Observable.empty();
		} else {
			return Observable.just(toRet);
		}
	}

}
*/
class InfiniteSizeDirectlyFollowsMapper{}