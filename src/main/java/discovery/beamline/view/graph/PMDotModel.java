package discovery.beamline.view.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import org.apache.commons.lang3.tuple.Pair;

import beamline.graphviz.Dot;
import beamline.graphviz.DotNode;
import discovery.beamline.miners.basic.ProcessMap;


/**
 *
 * @author Andrea Burattin
 */
public class PMDotModel extends Dot {

	private ProcessMap model;
	private ColorPalette.Colors activityColor;

	public PMDotModel(ProcessMap model, ColorPalette.Colors activityColor) {
		this.model = model;
		this.activityColor = activityColor;

		realize();
	}

	private void realize() {
//		setOption("rankdir", "LR");
		setOption("ranksep", ".1");
		setOption("fontsize", "9");
		setOption("remincross", "true");
		setOption("margin", "0.0,0.0");
		setOption("outputorder", "edgesfirst");

		Map<String, DotNode> activityToNode = new HashMap<String, DotNode>();
		Map<String, String> nodeToActivity = new HashMap<String, String>();

		Set<DotNode> startNodes = new HashSet<DotNode>();
		Set<DotNode> endNodes = new HashSet<DotNode>();

		// add all activities
		for(String activity : model.getActivities()) {
			DotNode node = addNodeIfNeeded(activity, activityToNode, nodeToActivity);
			if (node instanceof PMDotNode) {
				((PMDotNode) node).setColorWeight(model.getActivityValue(activity), activityColor);
			}
			if (model.isStartActivity(activity)) {
				startNodes.add(node);
			}
			if (model.isEndActivity(activity)) {
				endNodes.add(node);
			}
		}

		// add all relations
		for (Pair<String, String> relation : model.getRelations()) {
			String sourceActivity = relation.getLeft();
			String targetActivity = relation.getRight();

			// adding source nodes
			DotNode sourceNode = addNodeIfNeeded(sourceActivity, activityToNode, nodeToActivity);
			// adding target nodes
			DotNode targetNode = addNodeIfNeeded(targetActivity, activityToNode, nodeToActivity);

			// adding relations
			addRelation(sourceNode, targetNode, model.getRelationValue(relation));
		}

		// add relations from start and end
		if (startNodes.size() > 0) {
			PMDotStartNode start = new PMDotStartNode();
			addNode(start);
			for (DotNode n : startNodes) {
				addRelation(start, n, null);
			}
		}
		if (endNodes.size() > 0) {
			PMDotEndNode end = new PMDotEndNode();
			addNode(end);
			for (DotNode n : endNodes) {
				addRelation(n, end, null);
			}
		}
	}

	private void addRelation(DotNode sourceNode, DotNode targetNode, Double value) {
		addEdge(new PMDotEdge(sourceNode, targetNode, (value == null? null : String.format("%.2g%n", value)), value));
	}

	private DotNode addNodeIfNeeded(String activity, Map<String, DotNode> activityToNode, Map<String, String> nodeToActivity) {
		DotNode existingNode = activityToNode.get(activity);
		if (existingNode == null) {
//			if (model.isStartActivity(activity)) {
//				PMCEPDotStartNode startNode = new PMCEPDotStartNode();
//				addNode(startNode);
//				activityToNode.put(activity, startNode);
//				nodeToActivity.put(startNode.getId(), activity);
//				return startNode;
//			} else if (model.isEndActivity(activity)) {
//				PMCEPDotEndNode endNode = new PMCEPDotEndNode();
//				addNode(endNode);
//				activityToNode.put(activity, endNode);
//				nodeToActivity.put(endNode.getId(), activity);
//				return endNode;
//			} else {
				PMDotNode newNode = new PMDotNode(activity.toString());
				newNode.setColorWeight(model.getActivityValue(activity), activityColor);
				newNode.setSecondLine(String.format("%.2g%n", model.getActivityValue(activity)));
				addNode(newNode);
				activityToNode.put(activity, newNode);
				nodeToActivity.put(newNode.getId(), activity);
				return newNode;
//			}
		} else {
			return existingNode;
		}
	}
}