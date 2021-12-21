package rx.beamline;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import discovery.beamline.miners.basic.ProcessMap;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import jdk.jshell.execution.StreamingExecutionControl;
import rx.beamline.filters.ExcludeActivitiesFilter;
import rx.beamline.filters.RetainActivitiesFilter;
import rx.beamline.miners.DiscoveryMiner;
import rx.beamline.miners.HookAroundEventProcessing;
import rx.beamline.miners.StreamMiningAlgorithm;
import rx.beamline.sources.XesLogSource;
import rx.beamline.sources.XesSource;

public class Tester2 {

	public static void main(String[] args) throws Exception {
		System.out.println("start");
		
		XParser p = new XesXmlParser();
		XLog l = p.parse(new File("C:\\Users\\andbur\\Desktop\\input.xes")).get(0);
		
		XesSource source = new XesLogSource(l);
//		XesSource source = new MQTTXESSource(broker, topic, process);
		source.prepare();
		
		DiscoveryMiner miner = new DiscoveryMiner();
		miner.setMinDependency(0.3);
		miner.setModelRefreshRate(1);
		miner.setOnAfterEvent(new HookAroundEventProcessing() {
			@Override
			public void run() {
				System.out.println(miner.getProcessedEvents());
			}
		});
		
		Observable<XTrace> obs = source.getObservable();
		obs
//		.filter(new RetainActivitiesFilter("A", "B", "C", "dummy-retain"))
//		.filter(new ExcludeActivitiesFilter("A", "dummy-exclude"))
//		.map(new DirectSuccessionMapper())
//		.combine(new SlidingWindow(1000))
//		.map(new Miner(1, 0.5))
		.subscribe(miner);
//		.subscribe(new Consumer<XTrace>() {
//			@Override
//			public void accept(@NonNull XTrace t) throws Throwable {
//				System.out.println(
//					XConceptExtension.instance().extractName(t) + " - " +
//					XConceptExtension.instance().extractName(t.get(0)) +  " - " +
//					XTimeExtension.instance().extractTimestamp(t.get(0))
//				);
//			}
//		});
		
		miner.getLatestResponse().generateDot().exportToSvg(new File("C:\\Users\\andbur\\Desktop\\output.svg"));
		
		
//		Observable<String> stream = Observable.just("4444", "22", "333", "55555", "aaa", "bbb");
//		
//		stream
//		.compose(new ObservableTransformer<String, Integer>() {
//			@Override
//			public @NonNull ObservableSource<@NonNull Integer> apply(@NonNull Observable<@NonNull String> upstream) {
//				return upstream
//						.filter(new Predicate<String>() {
//							@Override
//							public boolean test(@NonNull String t) throws Throwable {
//								return t.length() > 0;
//							}
//						})
//						.map(new Function<String, Integer>() {
//							@Override
//							public @NonNull Integer apply(@NonNull String t) throws Throwable {
//								return t.length();
//							}
//						})
//						;
//			}
//		})
////		.filter(new Predicate<String>() {
////			@Override
////			public boolean test(@NonNull String t) throws Throwable {
////				return t.length() > 0;
////			}
////		})
////		.map(new Function<String, Integer>() {
////			@Override
////			public @NonNull Integer apply(@NonNull String t) throws Throwable {
////				return t.length();
////			}
////		})
////		.buffer(4)
//		.subscribe(new Consumer<Integer>() {
//			@Override
//			public void accept(@NonNull Integer t) throws Throwable {
//				System.out.println(t);
//			}
//		})
//		;
		System.out.println("done");
	}

}
