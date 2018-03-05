package edu.asu.nlu.knet.query;

import java.util.ArrayList;
//import java.util.HashSet;


import lombok.AccessLevel;
import lombok.Getter;

/**
 * Query structure:
 * <polarity+event1> <rel> <polarity+event2>; <event>  <rel> <entity>,....
 * Comma+semicolon separated query. Before semicolon are event-event relations.
 * After semicolon event-entity structures.
 * @author somak
 *
 */
public class Query {

	@Getter (AccessLevel.PUBLIC) private ArrayList<EventRelation> eventRelations ;
	@Getter (AccessLevel.PUBLIC) private ArrayList<SlotRelation> slotRelations;
//	private HashSet<Event> events = new HashSet<Event>();
	
	
	public Query(ArrayList<EventRelation> eventRelations, ArrayList<SlotRelation> slotRelations){
		this.eventRelations = eventRelations;
		this.slotRelations = slotRelations;
	}
	
	
	@Override
	public String toString(){
		String result = "";
		for(EventRelation er : eventRelations){
			result+="Event1 = " + er.getEvent1().getEvent()+"\n";
			result+="Event2 = " + er.getEvent2().getEvent()+"\n";
			result+="Relation = " + er.getRelation()+"\n";
		}
		
		int slotNumber = 1;
		for(SlotRelation sr : slotRelations){
			result+="Slot"+slotNumber+" = " + sr.getSlotValue()+"\n";
			slotNumber++;
		}
		
		return result;
	}
	
//	public Query(ArrayList<String> eventRelations2,
//			ArrayList<String> slotRelations2) {
//		eventRelations = new ArrayList<EventRelation>();
//		slotRelations = new ArrayList<SlotRelation>();
//		
//		for(String eventRelationString: eventRelations2){
//			eventRelationString = eventRelationString.trim();
//			String[] tokens =  eventRelationString.split(" ");
//			boolean isPositive1 = true,isPositive2 = true;
//			String relation;
//			Event event1,event2;
//			int index  = 0;
//			if(tokens[0].equals("not") || tokens[0].equals("nt")) {
//				isPositive1 = false;
//				index ++;			
//			}
//			
//			event1 = new Event(tokens[index++]);
//			if(event1.getEvent().equals("*"))
//				event1 = null;
//			else if(!events.contains(event1)) 
//				events.add(event1);
//			relation = tokens[index++];
//			relation = relation.equals("*") ? null: relation;
//			
//			if(tokens[0].equals("not") || tokens[0].equals("nt")) {
//				isPositive2 = false;
//				index++;
//			}
//			event2 = new Event(tokens[index]);
//			if(event2.getEvent().equals("*"))
//				event2 = null;
//			else if(!events.contains(event2)) 
//				events.add(event2);
//
//			eventRelations.add(new EventRelation(isPositive1, event1, relation, isPositive2, event2));
//		}
//		
//		for(String slotRelationStr: slotRelations2) {
//			slotRelationStr = slotRelationStr.trim();
//			String[] tokens =  slotRelationStr.split(" ");
//			Event event = new Event(tokens[0]);
//			
//			slotRelations.add(new SlotRelation(event, tokens[1], tokens[2]));
//		}
//		for(int i=0; i < slotRelations.size(); i++) {
//			SlotRelation slotRelation_i = slotRelations.get(i);
//			for(int j=i+1; j < slotRelations.size(); j++) {
//				SlotRelation slotRelation_j = slotRelations.get(j);
//				if(safeEquals(slotRelation_i.getSlotValue(), slotRelation_j.getSlotValue())) {
//					slotRelation_i.setValueMatches(slotRelation_j);
//					break;
//				}
//			}
//		}
//	}

//	private static boolean safeEquals(String slotValue, String slotValue2) {
//		if(slotValue == null || slotValue2 == null)
//			return false;
//		if(slotValue.equals(slotValue2))
//			return true;
//		return false;
//	}
	
}
