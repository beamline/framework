package discovery.beamline.miners.basic;

import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XTrace;

import beamline.models.algorithms.StreamMiningAlgorithm;
import beamline.models.responses.Response;

public class DiscoveryMiner extends StreamMiningAlgorithm<XTrace, ProcessMap> {

	private Map<String, String> latestActivityInCase = new HashMap<String, String>();
	private Map<Pair<String, String>, Double> relations = new HashMap<Pair<String, String>, Double>();
	private Map<String, Double> activities = new HashMap<String, Double>();
	private Double maxActivityFreq = Double.MIN_VALUE;
	private Double maxRelationsFreq = Double.MIN_VALUE;
	private double minDependency = 1d;
	private int modelRefreshRate = 0;
	
	public void setMinDependency(double minDependency) {
		this.minDependency = minDependency;
	}

	public void setModelRefreshRate(int modelRefreshRate) {
		this.modelRefreshRate = modelRefreshRate;
	}

	public ProcessMap mine(double threshold) {
		ProcessMap process = new ProcessMap();
		for (String activity : activities.keySet()) {
			process.addActivity(activity, activities.get(activity) / maxActivityFreq);
		}
		for (Pair<String, String> relation : relations.keySet()) {
			double dependency = relations.get(relation) / maxRelationsFreq;
			if (dependency >= threshold) {
				process.addRelation(relation.getLeft(), relation.getRight(), dependency);
			}
		}
		Set<String> toRemove = new HashSet<String>();
		Set<String> selfLoopsToRemove = new HashSet<String>();
		for (String activity : activities.keySet()) {
			if (process.isStartActivity(activity) && process.isEndActivity(activity)) {
				toRemove.add(activity);
			}
			if (process.isIsolatedNode(activity)) {
				selfLoopsToRemove.add(activity);
			}
		}
		for (String activity : toRemove) {
			process.removeActivity(activity);
		}

		return process;
	}

	@Override
	public ProcessMap ingest(XTrace event) {
		String caseID = XConceptExtension.instance().extractName(event);
		String activityName = XConceptExtension.instance().extractName(event.get(0));
		
		Double activityFreq = 1d;
		if (activities.containsKey(activityName)) {
			activityFreq += activities.get(activityName);
			maxActivityFreq = Math.max(maxActivityFreq, activityFreq);
		}
		activities.put(activityName, activityFreq);

		if (latestActivityInCase.containsKey(caseID)) {
			Pair<String, String> relation = new ImmutablePair<String, String>(latestActivityInCase.get(caseID), activityName);
			Double relationFreq = 1d;
			if (relations.containsKey(relation)) {
				relationFreq += relations.get(relation);
				maxRelationsFreq = Math.max(maxRelationsFreq, relationFreq);
			}
			relations.put(relation, relationFreq);
		}
		latestActivityInCase.put(caseID, activityName);

		if (getProcessedEvents() % modelRefreshRate == 0) {
			setLatestResponse(mine(minDependency));
		}
		
		return getLatestResponse();
	}

	@Override
	public Stream<ProcessMap> stream() {
		// TODO Auto-generated method stub
		return null;
	}
}