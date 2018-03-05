package edu.asu.nlu.knet.query;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.mongodb.DBObject;

@AllArgsConstructor
public class SlotRelation {
//	@Getter (AccessLevel.PUBLIC) private String polarity = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private Event event;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String slotType;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String slotValue;
	// slotValue in itself does not mean anything. However, if it matches other slotValues, 
	// then it becomes a constraint
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC)private SlotRelation valueMatches = null;
		
	
	public SlotRelation(){		
	}
	
	public double returnSimilarity(ArrayList<DBObject> slotRelationResults) {
		for(DBObject dbObject:slotRelationResults) {
			double wt = 0;
			String type = dbObject.get("SLOT").toString();
			String value = dbObject.get("SLOT_VALUE").toString();
			String eventName = dbObject.get("VALUE").toString();
			
			if(!ignoreField(slotType) && !slotType.equals(type))
				continue;
			// fuzzy matching the slot value
			if(!ignoreField(slotValue) && !slotValue.equals(value))
				wt += -0.5;
			if(event != null  && event.getEvent().equals(eventName))
				return wt+1;
		}
		return -1;
	}
	
	private boolean ignoreField(String field) {
		return field == null || field.equals("*");
	}

	public SlotRelation(Event event, String slotType, String slotValue) {
		super();
		this.event = event;
		this.slotType = slotType;
		this.slotValue = slotValue;
	}
}
