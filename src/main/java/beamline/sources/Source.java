package beamline.sources;

import beamline.exceptions.SourceException;
import io.reactivex.rxjava3.core.Observable;

/**
 * This interface is the base type that should be extended by all sources to be
 * used in the framework. When using a source implementing this type, the method
 * {@link #prepare()} should be called <strong>before</strong>
 * {@link #getObservable()}.
 * 
 * @author Andrea Burattin
 *
 * @param <T> the type of observable objects this interface will produce.
 */
public interface Source<T> {

	/**
	 * This method returns the observable created by the source. Before calling
	 * this method, it is important to prepare the source by calling the
	 * {@link #prepare()} method.
	 * 
	 * @return the {@link Observable}
	 */
	public Observable<T> getObservable();
	
	/**
	 * This method is supposed to be called before the {@link #getObservable()}
	 * one: it is in charge of preparing the source to be processed.
	 * 
	 * @throws SourceException while preparing the source, it is important to be
	 * aware that some sources may generate specific exceptions (e.g., file not
	 * found, network problems).
	 */
	public void prepare() throws SourceException;
}
