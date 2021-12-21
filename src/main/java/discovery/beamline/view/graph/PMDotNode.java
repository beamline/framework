package discovery.beamline.view.graph;

import java.awt.Color;

import beamline.graphviz.DotNode;

public class PMDotNode extends DotNode {

	private String label;

	public PMDotNode(String label) {
		this(label, null, null, null);
	}

	public PMDotNode(String label, String secondLine, Double weight, ColorPalette.Colors activityColor) {
		super(label, null);

		this.label = label;

		setOption("shape", "box");
		setOption("fixedsize", "true");
		setOption("height", "0.23");
		setOption("width", "1.2");
		setOption("style", "rounded,filled");
		setOption("fontname", "Arial");

		setSecondLine(secondLine);
		setColorWeight(weight, activityColor);
	}

	public void setSecondLine(String secondLine) {
		if (secondLine != null) {
			setLabel("<<font point-size='22'>" + label + "</font> <br/><font point-size='16'>" + secondLine
					+ "</font>>");
		}
	}

	public void setColorWeight(Double weight, ColorPalette.Colors activityColor) {
		if (weight == null) {
			setOption("fillcolor", "#FDEFD8"); // #FDEFD8:#E1D3BC
		} else {
			Color backgroundColor = ColorPalette.getValue(activityColor, weight);
			Color fontColor = ColorPalette.getFontColor(backgroundColor);
			setOption("fillcolor", ColorPalette
					.colorToString(backgroundColor)/* + ":" + ColorPalette.colorToString(backgroundColor.darker()) */);
			setOption("fontcolor", ColorPalette.colorToString(fontColor));
			setOption("fixedsize", "false");
		}
	}

	public void setMovedIn() {
		setOption("fillcolor", "white");
	}

	public void setMovedOut() {
		setOption("fillcolor", "black");
	}

	@Override
	public int hashCode() {
		return getLabel().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return getLabel().equals(object);
	}
}