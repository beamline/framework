package beamline.tester;

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

import beamline.filters.ExcludeActivitiesFilter;
import beamline.filters.RetainActivitiesFilter;
import beamline.miners.DiscoveryMiner;
import beamline.miners.ProcessMap;
import beamline.models.algorithms.HookEventProcessing;
import beamline.models.algorithms.StreamMiningAlgorithm;
import beamline.sources.XesLogSource;
import beamline.sources.XesSource;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import jdk.jshell.execution.StreamingExecutionControl;

public class Tester {

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

		System.out.println("done");
	}

}
