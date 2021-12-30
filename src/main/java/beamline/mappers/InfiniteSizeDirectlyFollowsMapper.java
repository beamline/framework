package beamline.mappers;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

public class InfiniteSizeDirectlyFollowsMapper implements Function<XTrace, ObservableSource<DirectlyFollowsRelation>> {

	private Map<String, XEvent> map = new HashMap<String, XEvent>();
	
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
