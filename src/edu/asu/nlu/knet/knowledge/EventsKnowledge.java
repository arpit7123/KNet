package edu.asu.nlu.knet.knowledge;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class EventsKnowledge extends Knowledge{
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private Polarity polarity1 = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private Polarity polarity2 = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String verb1 = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String verb2 = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String verb1Base = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String verb2Base = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String relation = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String slot1 = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String slot2 = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String arg1 = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String arg2 = null;
	
	public EventsKnowledge(Polarity polarity1, Polarity polarity2,
			String verb1, String verb2, String verb1Base, String verb2Base,
			String relation, String slot1,String slot2, String arg1, String arg2) {
		this.polarity1 = polarity1;
		this.polarity2 = polarity2;
		this.verb1 = verb1;
		this.verb2 = verb2;
		this.verb1Base = verb1Base;
		this.verb2Base = verb2Base;
		this.relation = relation;
		this.slot1 = slot1;
		this.slot2 = slot2;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
	@Override
	public String toString() {
		return "EventsKnowledge \npolarity1=" + polarity1 + ", \npolarity2="
				+ polarity2 + ", \nverb1=" + verb1 + ", \nverb2=" + verb2
				+ ", \nverb1 base=" + verb1Base + ", \nverb2 base=" + verb2Base
				+ ", \nrelation=" + relation + ", \nslot1=" + slot1 + ", \narg1=" + arg1
				+ ", \nslot2=" + slot2 + ", \narg2=" + arg2 + "]";
	}
	
	
}
