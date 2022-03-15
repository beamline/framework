package beamline.tester;

import java.io.IOException;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XTrace;

import beamline.sources.CSVLogSource;
import beamline.sources.XesSource;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;

public class Tester2 {

	public static void main(String[] args) throws Exception {
		String f = "C:\\Users\\andbur\\OneDrive - Danmarks Tekniske Universitet\\uni\\publishing\\papers\\conferences\\2022-caise\\material\\data\\synthetic\\event streams\\sudden_time_noise0_500_pm_simple.csv";
		XesSource source = new CSVLogSource(f, 1, 2);
		source.prepare();
		
		Observable<XTrace> obs = source.getObservable();
		obs.subscribe(new Consumer<XTrace>() {
			@Override
			public void accept(@NonNull XTrace t) throws Throwable {
				System.out.println(
					XConceptExtension.instance().extractName(t) + " - " +
					XConceptExtension.instance().extractName(t.get(0)) +  " - " +
							t.get(0).getAttributes()
				);
			}
		});
		
		System.out.println("done");
	}
}
