package beamline.models.filters;

import beamline.models.streams.XesSource;

public abstract class XesFilter implements XesSource {

	@Override
	public void prepare() throws Exception { }
}
