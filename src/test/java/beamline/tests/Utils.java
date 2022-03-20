package beamline.tests;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

import beamline.events.BEvent;
import beamline.exceptions.EventException;
import beamline.mappers.DirectlyFollowsRelation;

public class Utils {

	public static XFactory factory = new XFactoryNaiveImpl();
	
	/*
	 * Generate a streams with these events:
	 * - K
	 * - A / trace attribute: (a1,v1)
	 * - B
	 * - A
	 * - C / trace attribute: (a1,v4)
	 */
//	public static Observable<XTrace> generateObservableSameCaseId() {
//		XTrace[] events = null;
//		try {
//			events = new XTrace[] {
//				EventUtils.create("K", "c"),
//				EventUtils.create("A", "c"),
//				EventUtils.create("B", "c"),
//				EventUtils.create("A", "c"),
//				EventUtils.create("C", "c")
//			};
//		} catch (EventException e) {
//			e.printStackTrace();
//		}
//		events[1].getAttributes().put("a1", new XAttributeLiteralImpl("a1", "v1"));
//		events[2].get(0).getAttributes().put("a2", new XAttributeLiteralImpl("a2", "v3"));
//		events[3].get(0).getAttributes().put("a2", new XAttributeLiteralImpl("a2", "v2"));
//		events[4].getAttributes().put("a1", new XAttributeLiteralImpl("a1", "v4"));
//		return Observable.fromArray(events);
//	}
	
	public static DataStream<BEvent> generateObservableSameCaseId(ExecutionEnvironment env) {
		DataSet<BEvent> ds = CollectionDataSets.getCustomTypeDataSet(env);
	}
	
	/*
	 * c1: <K,A,B,A,C>
	 * c2: <O,A,I,C>
	 * 
	 * time order: (K,c1),(C,c2),(A,c1),(I,c2)(B,c1),(A,c2)(A,c1),(O,c2),(C,c1)
	 */
	public static XLog generteXLog() {
		XLog l = factory.createLog();
		XTrace c1 = factory.createTrace();
		XConceptExtension.instance().assignName(c1, "c1");
		c1.add(factory.createEvent()); XConceptExtension.instance().assignName(c1.get(0), "K");
		c1.add(factory.createEvent()); XConceptExtension.instance().assignName(c1.get(1), "A"); XTimeExtension.instance().assignTimestamp(c1.get(1), 2);
		c1.add(factory.createEvent()); XConceptExtension.instance().assignName(c1.get(2), "B"); XTimeExtension.instance().assignTimestamp(c1.get(2), 4);
		c1.add(factory.createEvent()); XConceptExtension.instance().assignName(c1.get(3), "A"); XTimeExtension.instance().assignTimestamp(c1.get(3), 6);
		c1.add(factory.createEvent()); XConceptExtension.instance().assignName(c1.get(4), "C"); XTimeExtension.instance().assignTimestamp(c1.get(4), 8);
		
		XTrace c2 = factory.createTrace();
		XConceptExtension.instance().assignName(c2, "c2");
		c2.add(factory.createEvent());
		c2.add(factory.createEvent());
		c2.add(factory.createEvent());
		c2.add(factory.createEvent());
		XConceptExtension.instance().assignName(c2.get(3), "O"); XTimeExtension.instance().assignTimestamp(c2.get(3), 5);
		XConceptExtension.instance().assignName(c2.get(2), "A"); XTimeExtension.instance().assignTimestamp(c2.get(2), 7);
		XConceptExtension.instance().assignName(c2.get(1), "I"); XTimeExtension.instance().assignTimestamp(c2.get(1), 3);
		XConceptExtension.instance().assignName(c2.get(0), "C"); XTimeExtension.instance().assignTimestamp(c2.get(0), 1);

		l.add(c1);
		l.add(c2);
		return l;
	}

	public static boolean verifyDirectFollows(DirectlyFollowsRelation df, String a1, String a2, String caseId) {
		String df_a1 = XConceptExtension.instance().extractName(df.getFirst());
		String df_a2 = XConceptExtension.instance().extractName(df.getSecond());
		return df_a1.equals(a1) && df_a2.equals(a2) && df.getCaseId().equals(caseId);
	}
}
