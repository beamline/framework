package beamline.miners;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import beamline.graphviz.Dot;
import beamline.models.responses.GraphvizResponse;
import beamline.view.graph.PMDotModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProcessMap implements GraphvizResponse {

	private Map<String, Double> activities;
	private Map<Pair<String, String>, Double> relations;

	@Override
	public Dot generateDot() {
		return new PMDotModel(this, beamline.view.graph.ColorPalette.Colors.BLUE);
	}

	public ProcessMap() {
		this.activities = new HashMap<String, Double>();
		this.relations = new HashMap<Pair<String, String>, Double>();
	}

	public void addActivity(String activityName, Double value) {
		this.activities.put(activityName, value);
	}

	public void removeActivity(String activityName) {
		this.activities.remove(activityName);
	}

	public void addRelation(String activitySource, String activityTarget, Double value) {
		relations.put(new ImmutablePair<String, String>(activitySource, activityTarget), value);
	}

	public void removeRelation(String activitySource, String activityTarget) {
		relations.remove(new ImmutablePair<String, String>(activitySource, activityTarget));
	}

	public Set<String> getActivities() {
		return activities.keySet();
	}

	public Set<Pair<String, String>> getRelations() {
		return relations.keySet();
	}

	public Double getActivityValue(String activity) {
		return this.activities.get(activity);
	}

	public Double getRelationValue(Pair<String, String> relation) {
		return this.relations.get(relation);
	}

	public Set<String> getIncomingActivities(String candidate) {
		Set<String> result = new HashSet<String>();
		for (Pair<String, String> relation : getRelations()) {
			if (relation.getRight().equals(candidate)) {
				result.add(relation.getLeft());
			}
		}
		return result;
	}

	public Set<String> getOutgoingActivities(String candidate) {
		Set<String> result = new HashSet<String>();
		for (Pair<String, String> relation : getRelations()) {
			if (relation.getLeft().equals(candidate)) {
				result.add(relation.getRight());
			}
		}
		return result;
	}

	public boolean isStartActivity(String candidate) {
		return getIncomingActivities(candidate).size() == 0;
	}

	public boolean isEndActivity(String candidate) {
		return getOutgoingActivities(candidate).size() == 0;
	}

	public boolean isIsolatedNode(String candidate) {
		return getOutgoingActivities(candidate).equals(getIncomingActivities(candidate));
	}
}