package discovery.beamline.view.graph;

import beamline.graphviz.DotEdge;
import beamline.graphviz.DotNode;

public class PMDotEdge extends DotEdge {

	public PMDotEdge(DotNode source, DotNode target, String edgeText, Double weight) {
		super(source, target);

		setOption("decorate", "false");
		setOption("fontsize", "8");
		setOption("arrowsize", "0.5");
		setOption("fontname", "Arial");
		setOption("tailclip", "false");

		if (edgeText != null) {
			setLabel(" " + edgeText);
		}

		if (weight != null) {
			setOption("color",
					ColorPalette.colorToString(ColorPalette.getValue(ColorPalette.Colors.DARK_GRAY, weight)));
			if ((source instanceof PMDotStartNode) || (target instanceof PMDotEndNode)) {
				setOption("penwidth", "" + (1 + (5 * weight)));
			} else {
				setOption("penwidth", "" + (1 + (8 * weight)));
			}
		} else {
			if ((source instanceof PMDotStartNode) || (target instanceof PMDotEndNode)) {
				setOption("penwidth", "2");
			} else {
				setOption("penwidth", "3");
			}
		}

		if (source instanceof PMDotStartNode) {
			setOption("style", "dashed");
			setOption("color", "#ACB89C");
		}

		if (target instanceof PMDotEndNode) {
			setOption("style", "dashed");
			setOption("color", "#C2B0AB");
		}
	}
}