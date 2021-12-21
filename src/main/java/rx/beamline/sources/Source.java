package rx.beamline.sources;

import io.reactivex.rxjava3.core.Observable;

public interface Source<T> {

	public Observable<T> getObservable();
	
	public void prepare() throws Exception;
}
