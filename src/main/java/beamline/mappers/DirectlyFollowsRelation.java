package beamline.mappers;

import org.deckfour.xes.model.XEvent;

/**
 * This class represents a directly follows relation as produced by
 * {@link InfiniteSizeDirectlyFollowsMapper}.
 * 
 * @author Andrea Burattin
 */
public class DirectlyFollowsRelation {

	private String caseId;
	private XEvent first;
	private XEvent second;
	
	/**
	 * Constructor
	 * 
	 * @param caseId the case id
	 * @param first the first event
	 * @param second the second event
	 */
	public DirectlyFollowsRelation(String caseId, XEvent first, XEvent second) {
		this.caseId = caseId;
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Returns the case id this directly follows relation belongs to
	 * 
	 * @return the case id
	 */
	public String getCaseId() {
		return caseId;
	}

	/**
	 * Returns the first event
	 * 
	 * @return the first event
	 */
	public XEvent getFirst() {
		return first;
	}

	/**
	 * Returns the second event
	 * 
	 * @return the second event
	 */
	public XEvent getSecond() {
		return second;
	}
}
