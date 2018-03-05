package edu.asu.nlu.knet.query;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QueryParser {

//	Event1=E1, REL=R, Event2=E2, Slot1=S1, Slot2=S2
	private Pattern queryPattern = Pattern.compile("(.*)(=)(.*)(,)(.*)(=)(.*)(,)(.*)(=)(.*)(,)(.*)(=)(.*)(,)(.*)(=)(.*)");
	
	public Query parseQuery(String query){
		ArrayList<EventRelation> eventRelations = new ArrayList<EventRelation>();
		ArrayList<SlotRelation> slotRelations = new ArrayList<SlotRelation>();
		
		String event1 = null;
		String relation = null;
		String event2 = null;
		String slot1 = null;
		String slot2 = null;
		Matcher m = this.queryPattern.matcher(query);
		if(m.matches()){
			EventRelation er = new EventRelation();
			
			event1 = m.group(3).trim();
			relation = m.group(7).trim();
			event2 = m.group(11).trim();
			slot1 = m.group(15).trim();
			slot2 = m.group(19).trim();
			er.setEvent1(new Event(event1));
			er.setEvent2(new Event(event2));
			er.setRelation(relation);
			eventRelations.add(er);
			
			SlotRelation sr1 = new SlotRelation();
			sr1.setEvent(new Event(event1));
			sr1.setSlotValue(slot1);
			
			SlotRelation sr2 = new SlotRelation();
			sr2.setEvent(new Event(event2));
			sr2.setSlotValue(slot2);
			
			slotRelations.add(sr1);
			slotRelations.add(sr2);
		}
		return new Query(eventRelations, slotRelations);
	}

	public static void main(String[] args){
		QueryParser qp = new QueryParser();		
//		Event1=E1, REL=R, Event2=E2, Slot1=S1, Slot2=S2
		String query = "Event1=E1, REL=R, Event2=E2, Slot1=S1, Slot2=S2";
		Query parsedQuery = qp.parseQuery(query);
		System.out.println(parsedQuery.toString());
	}
}







