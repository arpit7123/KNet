package edu.asu.nlu.knet;

import lombok.AccessLevel;
import lombok.Getter;

import com.mongodb.BasicDBObject;

public class DataBaseNode {
	@Getter (AccessLevel.PUBLIC) private BasicDBObject dbObj= null;
	@Getter (AccessLevel.PUBLIC) private int index;
	
	public DataBaseNode(BasicDBObject obj, int i){
		this.dbObj = obj;
		this.index = i;
	}
}
