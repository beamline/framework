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
	 * @param from the first event
	 * @param to the second event
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
				.append(getFrom().getEventName(), other.getFrom().getEventName())
				.append(getTo().getEventName(), other.getTo().getEventName())
				.isEquals();

	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(getFrom().getEventName())
				.append(getTo().getEventName())
				.toHashCode();
	}
}
