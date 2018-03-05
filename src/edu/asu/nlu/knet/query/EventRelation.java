package edu.asu.nlu.knet.query;

import java.util.ArrayList;

import com.mongodb.DBObject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class EventRelation {
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private boolean isPositive1;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private Event event1;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String relation;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private boolean isPositive2;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private Event event2;
	
	public EventRelation(){
		
	}
	
	public double returnSimilarity(ArrayList<DBObject> eventRelationResults) {
		for(DBObject dbObject:eventRelationResults) {
			String relation = dbObject.get("REL_VALUE").toString();
			String eventName1 = dbObject.get("EVENT_VALUE1").toString();
			String eventName2 = dbObject.get("EVENT_VALUE2").toString();
			
			if(!ignoreField(relation) && !relation.equals(this.relation))
				continue;
			if(!ignoreField(eventName1) && !eventName1.equals(this.event1.getEvent()))
				continue;
			if(ignoreField(eventName2) || eventName2.equals(this.event2.getEvent()))
				return 1;
		}
		return -1;
	}
	
	private boolean ignoreField(String field) {
		return field == null || field.equals("*");
	}
}
