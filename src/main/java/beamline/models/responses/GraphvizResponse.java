package beamline.models.responses;

import beamline.graphviz.Dot;

/**
 * A refined {@link Response} that can be rendered in Graphviz using the Dot
 * language.
 * 
 * @author Andrea Burattin
 */
public abstract class GraphvizResponse extends Response {

	private static final long serialVersionUID = 7232727657074096321L;

	/**
	 * Returns the Dot representation of the response
	 * 
	 * @return the Dot representation of the response
	 */
	public abstract Dot generateDot();
}
