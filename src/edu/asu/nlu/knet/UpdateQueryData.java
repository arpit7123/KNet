package edu.asu.nlu.knet;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class UpdateQueryData {
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String sentId = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String paraId = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String docId = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String sentence = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ArrayList<String> queryList = null;

	public UpdateQueryData(String sentId, String paraId, String docId, String sentence, ArrayList<String> queryList){
		this.sentId = sentId;
		this.paraId = paraId;
		this.docId = docId;
		this.sentence = sentence;
		this.queryList = queryList;
	}
}
