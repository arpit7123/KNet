package edu.asu.nlu.knet;

/**
 * Author: Arpit Sharma
 * Date: July 24 2014
 */
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class RelationsNode {
	
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String verb1Negative = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String verb1 = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String verb1Relation = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String relation = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String verb2Negative = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String verb2 = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String verb2Relation = null;
	
}
