/**
 * 
 */
package edu.asu.nlu.knet.extra;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.asu.nlu.knet.knowledge.AttributeKnowledge;
import edu.asu.nlu.knet.knowledge.EventsKnowledge;
import edu.asu.nlu.knet.knowledge.Knowledge;
import edu.asu.nlu.knet.knowledge.Polarity;

public class AnsSetToKnowledge {
	
	/**
	 * answerEvents(positive,loved_2,recipient,caused_by,positive,loved_6,agent)
	 */
	private final Pattern EventsKnowledgePattern= Pattern.compile("(answerEvents\\()(.*)(,)(.*)(,)(.*)(,)"
			+ "(.*)(,)(.*)(,)(.*)(,)(.*)(,)(.*)(,)(.*)(,)(.*)(,)(.*)(\\))");
	
	/**
	 * answerProperty(positive,loves_2,love,recipient,trait,positive,cute_7,cute) 
	 */
	private final Pattern AttrKnowledgePattern= Pattern.compile("(answerProperty\\()(.*)(,)(.*)(,)(.*)(,)"
			+ "(.*)(,)(.*)(,)(.*)(,)(.*)(\\))");
	
	private static AnsSetToKnowledge as2KInstance = null;
	
	static{
		as2KInstance = new AnsSetToKnowledge();
	}
	
	private AnsSetToKnowledge(){	
	}
	
	public static AnsSetToKnowledge getInstance(){
		return as2KInstance;
	}
	
	public Knowledge convert(String ans){
		Knowledge k = null;
		Matcher m = EventsKnowledgePattern.matcher(ans);
		if(m.matches()){
			String pol1 = m.group(2);
			String verb1 = m.group(4);
			String verb1Base = m.group(6);
			String slot1 = m.group(8);
			String arg1 = m.group(10);
			String rel = m.group(12);
			String pol2 = m.group(14);
			String verb2 = m.group(16);
			String verb2Base = m.group(18);
			String slot2 = m.group(20);
			String arg2 = m.group(22);
			
			Polarity pola1 = Polarity.getEnum(pol1);
			Polarity pola2 = Polarity.getEnum(pol2);
			
			k = new EventsKnowledge(pola1,pola2,verb1,verb2,verb1Base,verb2Base,rel,slot1,slot2,arg1,arg2);
			k.setEventKnowledge(true);
		}else {
//			answerProperty(positive,loves_2,love,recipient,positive,cute_7,cute) 
			m = AttrKnowledgePattern.matcher(ans);
			if(m.matches()){
				String ePol = m.group(2);
				String event = m.group(4);
				String eventBase = m.group(6);
				String slot = m.group(8);
				String aPol = m.group(10);
				String attr = m.group(12);
				String attrBase = m.group(14);
				
				Polarity eventPol = Polarity.getEnum(ePol);
				Polarity attrPol = Polarity.getEnum(aPol);
				
				k = new AttributeKnowledge(eventPol, event, eventBase, slot, attrPol, attr, attrBase);
				k.setAttrKnowledge(true);
			}
		}
		return k;
	}
	
}
