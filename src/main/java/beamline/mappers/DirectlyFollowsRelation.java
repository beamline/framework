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
	public XEvent first;
	public XEvent second;
	
	/**
	 * Constructor
	 * 
	 * @param caseId
	 * @param first
	 * @param second
	 */
	public DirectlyFollowsRelation(String caseId, XEvent first, XEvent second) {
		this.caseId = caseId;
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Returns the case id this directly follows relation belongs to
	 * 
	 * @return
	 */
	public String getCaseId() {
		return caseId;
	}

	/**
	 * Returns the first event
	 * 
	 * @return
	 */
	public XEvent getFirst() {
		return first;
	}

	/**
	 * Returns the second event
	 * 
	 * @return
	 */
	public XEvent getSecond() {
		return second;
	}
}
