package beamline.tester;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XTrace;

import com.google.common.collect.Iterators;

import beamline.models.responses.GraphvizResponse;
import beamline.models.streams.XesMqttSource;
import beamline.models.streams.XesSource;
import beamline.models.streams.ObservableStream;
import beamline.models.streams.XesStreamSource;
import discovery.beamline.miners.basic.DiscoveryMiner;

public class Tester {

	public static void main(String[] args) throws Exception {

		System.out.println("Started");
		
		XesSource source;
		source = new XesStreamSource("C:\\Users\\andbur\\Desktop\\input-small.xes");
//		source = new XesMqttSource("tcp://broker.hivemq.com:1883", "pmcep", "test");
		
		source.prepare();
		
		int refreshRate = 1;
		
		DiscoveryMiner discoveryAlgorithm = new DiscoveryMiner();
		discoveryAlgorithm.setMinDependency(0.3);
		discoveryAlgorithm.setModelRefreshRate(refreshRate);

		Stream<String> s = Arrays.asList("1234", "12", "123").stream();
		
		s
		.map(new Function<String, Integer>() {
			@Override
			public Integer apply(String t) {
				return t.length();
			}
		})
		.forEach(System.out::println);
		
		
//		Stream<XTrace> s = source.stream();
//		
//		s
//		.filter(new Predicate<XTrace>() {
//			@Override
//			public boolean test(XTrace t) {
//				return "A".equals(XConceptExtension.instance().extractName(t.get(0))) || 
//						"B".equals(XConceptExtension.instance().extractName(t.get(0)));
//			}
//		})
//		.filter(new Predicate<XTrace>() {
//			@Override
//			public boolean test(XTrace t) {
//				return "c1".equals(XConceptExtension.instance().extractName(t));
//			}
//		})
//		.limit(2)
//		.map(new Function<XTrace, String>() {
//			@Override
//			public String apply(XTrace t) {
//				return XConceptExtension.instance().extractName(t.get(0));
//			}
//		})
//		.forEach(System.out::println);
//		.
//		.forEach(new Consumer<XTrace>() {
//			@Override
//			public void accept(XTrace t) {
//				System.out.println(
//					XConceptExtension.instance().extractName(t) + " - " +
//					XConceptExtension.instance().extractName(t.get(0)) + " - " +
//					XTimeExtension.instance().extractTimestamp(t.get(0)));
////				discoveryAlgorithm.process(t);
//			}
//		});
		
//		for(XTrace t : (Iterable<XTrace>) s::iterator) {
//			System.out.println(XConceptExtension.instance().extractName(t) + " - " +
//					XConceptExtension.instance().extractName(t.get(0)) + " - " +
//					XTimeExtension.instance().extractTimestamp(t.get(0)));
//			discoveryAlgorithm.process(eventWrapper);
//			GraphvizResponse response = (GraphvizResponse) discoveryAlgorithm.getLatestResponse();
//			if (discoveryAlgorithm.getProcessedEvents() % refreshRate == 0) {
////				response.generateDot().exportToSvg(new File("C:\\Users\\andbur\\Desktop\\output-" + discoveryAlgorithm.getProcessedEvents() + ".svg"));
//				System.out.println("Processed " + discoveryAlgorithm.getProcessedEvents() + " events, the map has " + response.generateDot().getEdges().size() + " edges");
//			}
//		}
//		System.out.println("Processed " + discoveryAlgorithm.getProcessedEvents() + " events, the map has " + discoveryAlgorithm.getLatestResponse().generateDot().getEdges().size() + " edges");
		
		System.out.println("Done");
	}
}
