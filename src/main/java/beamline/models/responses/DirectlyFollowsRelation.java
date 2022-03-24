package beamline.models.responses;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

import beamline.events.BEvent;
import beamline.models.algorithms.InfiniteSizeDirectlyFollowsMapper;

/**
 * This class represents a directly follows relation as produced by
 * {@link InfiniteSizeDirectlyFollowsMapper}.
 * 
 * @author Andrea Burattin
 */
public class DirectlyFollowsRelation extends Response {

	private static final long serialVersionUID = 1775695752885219490L;
	private Pair<BEvent, BEvent> pair;
	
	/**
	 * Constructor
	 * 
	 * @param caseId the case id
	 * @param first the first event
	 * @param second the second event
	 */
	public DirectlyFollowsRelation(BEvent from, BEvent to) {
		if (!from.getTraceName().equals(to.getTraceName())) {
			throw new IllegalArgumentException();
		}
		pair = Pair.of(from, to);
	}
	
	/**
	 * Returns the case id this directly follows relation belongs to
	 * 
	 * @return the case id
	 */
	public String getCaseId() {
		return pair.getLeft().getTraceName();
	}
	
	/**
	 * Returns the source event
	 * 
	 * @return the source event
	 */
	public BEvent getFrom() {
		return pair.getLeft();
	}

	/**
	 * Returns the target event
	 * 
	 * @return the target event
	 */
	public BEvent getTo() {
		return pair.getRight();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		DirectlyFollowsRelation other = (DirectlyFollowsRelation) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(getFrom(), other.getFrom())
				.append(getTo(), other.getTo())
				.isEquals();

	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(getFrom())
				.append(getTo())
				.toHashCode();
	}
}
