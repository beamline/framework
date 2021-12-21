package beamline.models.filters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import org.deckfour.xes.model.XTrace;

public class EventNameFilterKeepOnly extends XesFilter {

	private Set<String> activityNamesToKeep;
	
	public EventNameFilterKeepOnly(String ...activityNamesToKeep) {
		this.activityNamesToKeep = new HashSet<String>(Arrays.asList(activityNamesToKeep));
	}
	
	@Override
	public Iterator<XTrace> iterator() {
		return null;
	}

	@Override
	public Stream<XTrace> stream() {
		// TODO Auto-generated method stub
		return null;
	}

}
