/**
 * 
 */
package edu.asu.nlu.knet.query;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.mongodb.DBObject;

/**
 * @author arpit
 *
 */
@AllArgsConstructor
class WeightedDBObject {
	@Getter (AccessLevel.PUBLIC) private ArrayList<DBObject> eventRelationResults;
	@Getter (AccessLevel.PUBLIC) private ArrayList<DBObject> slotRelationResults;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private double weight;
	@Getter (AccessLevel.PUBLIC) private String sentenceId;
	@Getter (AccessLevel.PUBLIC) private String sentence;
	
	@Override
	public String toString() {
		return "WeightedDBObject [eventRelationResults=" + eventRelationResults
				+ ", slotRelationResults=" + slotRelationResults + ", weight="
				+ weight + ", sentenceId=" + sentenceId + ", sentence="
				+ sentence + "]";
	}
	
}
