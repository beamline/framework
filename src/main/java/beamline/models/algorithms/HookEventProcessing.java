package beamline.models.algorithms;

/**
 * This interface defines the structure of the callback function that a
 * {@link StreamMiningAlgorithm} can execute (cf.,
 * {@link StreamMiningAlgorithm#setOnBeforeEvent(HookEventProcessing)} and
 * {@link StreamMiningAlgorithm#setOnAfterEvent(HookEventProcessing)}).
 * 
 * @author Andrea Burattin
 */
public interface HookEventProcessing {

	/**
	 * The actual function to trigger
	 */
	public void trigger();
}
