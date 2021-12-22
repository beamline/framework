package beamline.view.graph;

import java.awt.Color;

// see https://content.linkedin.com/content/dam/brand/site/img/color/color-palette-order.png
public class ColorPalette {

	public enum Colors {
		BLUE  ("#CFEDFB", "#0B4971"),
		VIOLET("#F0E3EF", "#593482"),
		RED   ("#FFE2D2", "#98041B"),
		ORANGE("#FFEBB6", "#933304"),
		AQUA  ("#D2ECEB", "#0E5C68"),
		YELLOW("#FAF0B5", "#856A1D"),
		PINK  ("#FBE2ED", "#951343"),
		GREEN ("#E5EFC7", "#3F652D"),
		GRAY  ("#E0E2E4", "#3A3C3E"),
		DARK_GRAY("#86888A", "#252526");

		public Color min;
		public Color max;

		Colors(String min, String max) {
			this.min = Color.decode(min);
			this.max = Color.decode(max);
		}
	}

	public static Color getValue(Colors base, double value) {
		float rMin = base.min.getRed() / 255f;
		float gMin = base.min.getGreen() / 255f;
		float bMin = base.min.getBlue() / 255f;

		float rMax = base.max.getRed() / 255f;
		float gMax = base.max.getGreen() / 255f;
		float bMax = base.max.getBlue() / 255f;

		float rOwn = (float) (rMin + (rMax - rMin) * value);
		float gOwn = (float) (gMin + (gMax - gMin) * value);
		float bOwn = (float) (bMin + (bMax - bMin) * value);
		
		rOwn = (rOwn > 1f)? 1 : (rOwn < 0? 0 : rOwn);
		gOwn = (gOwn > 1f)? 1 : (gOwn < 0? 0 : gOwn);
		bOwn = (bOwn > 1f)? 1 : (bOwn < 0? 0 : bOwn);

		return new Color(rOwn, gOwn, bOwn);
	}

	public static Color getFontColor(Color background) {
		double a = 1
				- (0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue()) / 255;
		return a < 0.5 ? Color.BLACK : Color.WHITE;
	}

	public static String colorToString(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}
}