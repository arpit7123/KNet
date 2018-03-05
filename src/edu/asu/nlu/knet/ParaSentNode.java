package edu.asu.nlu.knet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ParaSentNode {
	
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private int paraId = -9999;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private int sentId = -9999;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String sentence = null;

	public ParaSentNode(int paraId, int sentId, String sentence) {
		this.paraId = paraId;
		this.sentId = sentId;
		this.sentence = sentence;
	}
}
