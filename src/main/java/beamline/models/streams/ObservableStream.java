package beamline.models.streams;

import java.util.Iterator;
import java.util.stream.Stream;

public interface ObservableStream<T> extends Iterable<T> {

	public abstract void prepare() throws Exception;
	
	public abstract Stream<T> stream();
}
