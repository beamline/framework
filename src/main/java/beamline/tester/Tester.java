package beamline.tester;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
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
import beamline.mappers.DirectlyFollowsRelation;
import beamline.mappers.InfiniteSizeDirectlyFollowsMapper;
import beamline.models.algorithms.HookEventProcessing;
import beamline.models.algorithms.StreamMiningAlgorithm;
import beamline.models.responses.GraphvizResponse;
import beamline.sources.MQTTXesSource;
import beamline.sources.XesLogSource;
import beamline.sources.XesSource;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import jdk.jshell.execution.StreamingExecutionControl;

public class Tester {

	public static void main(String[] args) throws Exception {
		System.out.println("start");
		
		XParser p = new XesXmlParser();
		XLog l = p.parse(new File("C:\\Users\\andbur\\Desktop\\input.xes")).get(0);
//		XesSource source = new XesLogSource(l);
		XesSource source = new MQTTXesSource("tcp://broker.hivemq.com:1883", "pmcep", "test");
		source.prepare();
		
//		DiscoveryMiner miner = new DiscoveryMiner();
//		miner.setMinDependency(0.3);
//		miner.setModelRefreshRate(1);
//		miner.setOnAfterEvent(new HookEventProcessing() {
//			@Override
//			public void trigger() {
//				if (miner.getProcessedEvents() % 100 == 0) {
//					try {
//						GraphvizResponse resp = miner.getLatestResponse();
//						resp.generateDot().exportToSvg(new File("C:\\Users\\andbur\\Desktop\\output-" + miner.getProcessedEvents() + ".svg"));
//					} catch (IOException e) { }
//				}
//			}
//		});
		
		Observable<XTrace> obs = source.getObservable();
		obs
//		.filter(new RetainActivitiesFilter("A", "B", "C", "dummy-retain"))
//		.filter(new ExcludeActivitiesFilter("A", "dummy-exclude"))
//		.map(new DirectSuccessionMapper())
//		.combine(new SlidingWindow(1000))
//		.map(new Miner(1, 0.5))
//		.subscribe(miner);
//		.mapOptional(new Function<XTrace, Optional<Pair<String, String>>>() {
//
//			@Override
//			public @NonNull Optional<Pair<String, String>> apply(@NonNull XTrace t) throws Throwable {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		});
//		.map(new Function<XTrace, DirectlyFollowRelation>() {
//			Map<String, String> map = new HashMap<String, String>();
//			
//			@Override
//			public @NonNull DirectlyFollowRelation apply(@NonNull XTrace t) throws Throwable {
//				String caseId = XConceptExtension.instance().extractName(t);
//				String act = XConceptExtension.instance().extractName(t.get(0));
//				DirectlyFollowRelation toRet = new DirectlyFollowRelation();
//				if (map.containsKey(caseId)) {
//					String prevAct = map.get(caseId);
//					toRet.first = prevAct;
//					toRet.second = act;
//					toRet.caseId = caseId;
//				}
//				map.put(caseId, act);
//				
//				return toRet;
//			}
//		})
//		.filter(new Predicate<DirectlyFollowRelation>() {
//			@Override
//			public boolean test(@NonNull DirectlyFollowRelation t) throws Throwable {
//				return t.first != null && t.second != null;
//			}
//		})
		.flatMap(new InfiniteSizeDirectlyFollowsMapper())
		.subscribe(new Consumer<DirectlyFollowsRelation>() {

			@Override
			public void accept(@NonNull DirectlyFollowsRelation t) throws Throwable {
				System.out.println(
						XConceptExtension.instance().extractName(t.getFirst()) +  " -> " +
						XConceptExtension.instance().extractName(t.getSecond()) + " for case " + t.getCaseId());
			}
		});
		
		
		
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
		
//		miner.getLatestResponse().generateDot().exportToSvg(new File("C:\\Users\\andbur\\Desktop\\output.svg"));

		System.out.println("done");
	}

}
