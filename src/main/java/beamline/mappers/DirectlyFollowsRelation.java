package beamline.mappers;

import org.deckfour.xes.model.XEvent;

public class DirectlyFollowsRelation {

	private String caseId;
	public XEvent first;
	public XEvent second;
	
	public DirectlyFollowsRelation(String caseId, XEvent first, XEvent second) {
		this.caseId = caseId;
		this.first = first;
		this.second = second;
	}
	
	public String getCaseId() {
		return caseId;
	}

	public XEvent getFirst() {
		return first;
	}

	public XEvent getSecond() {
		return second;
	}
}
