package beamline.sources;

import org.deckfour.xes.model.XTrace;

/**
 * This interface is supposed just to bind the type of {@link Source} to
 * {@link XTrace}.
 * 
 * @author Andrea Burattin
 */
public interface XesSource extends Source<XTrace> {

}