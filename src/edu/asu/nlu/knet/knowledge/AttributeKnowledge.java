/**
 * 
 */
package edu.asu.nlu.knet.knowledge;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author arpit
 *
 */
public class AttributeKnowledge extends Knowledge {
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private Polarity eventPolarity = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String eventVerb = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String eventVerbBase = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String slot = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private Polarity attrPolarity = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String attrValue = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String attrBase = null;

	
	public AttributeKnowledge(Polarity eventPolarity, String eventVerb,
			String eventVerbBase, String slot, Polarity attrPolarity, String attrValue,
			String attrBase) {
		this.eventPolarity = eventPolarity;
		this.eventVerb = eventVerb;
		this.eventVerbBase = eventVerbBase;
		this.slot = slot;
		this.attrPolarity = attrPolarity;
		this.attrValue = attrValue;
		this.attrBase = attrBase;
	}
	
	@Override
	public String toString() {
		return "AttributeKnowledge:- [\neventPolarity=" + this.eventPolarity + ", eventVerb="
				+ this.eventVerb + ", eventVerbBase=" + this.eventVerbBase + ", slot=" + this.slot
				+ ", attrPolarity=" + this.attrPolarity + ", attrValue=" + this.attrValue
				+ ", attrBase=" + this.attrBase + "]";
	}
}
