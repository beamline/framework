package beamline.models.responses;

import beamline.graphviz.Dot;

public interface GraphvizResponse extends Response {

	public abstract Dot generateDot();
}
