/**
 * 
 */
package edu.asu.nlu.knet.postprocessor;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import module.graph.helper.Node;

/**
 * @author Arpit Sharma
 * @date Jun 23, 2017
 *
 */
public class KnowledgeType5 extends KnowledgeType{
	
	/**
	 * execution of EVENT1 [ARG*: X; ARG*: Y] may prevent execution of EVENT2 [ARG*: X; ARG*: Z]
	 */
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private boolean event1polarity;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private String event1;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private String event1Lemma;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private String event1Rel;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<Node> event1Children;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<String> evnt1ChildEdges;

	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private boolean event2polarity;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private String event2;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private String event2Lemma;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private String event2Rel;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<Node> event2Children;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<String> evnt2ChildEdges;
	
	public String getReadableKnowledge(){
		String result = "";

		result += "execution of " + event1 +"["+event1Rel + ": X";
		String treeStr = getTreeStr(event1Children,evnt1ChildEdges);
		if(!treeStr.equals("")){
			result+="; "+treeStr;
		}
		result+="] may prevent execution of " + event2 +"["+event2Rel + ": X";

		treeStr = getTreeStr(event2Children,evnt2ChildEdges);
		if(!treeStr.equals("")){
			result+="; "+treeStr;
		}

		result += "]";

		return result;
	}

	public String getNeo4JCreateQuery(){
		String result = "";
		HashMap<String,Integer> eventIndexMap = new HashMap<String,Integer>();
		eventIndexMap.put(event1, 1);
		result += "CREATE (n" + eventIndexMap.size() + ":Event {name: '" + event1 +"', lemma: '" + event1Lemma +"'}),";
		String treeStr = getTreeStrForCreateQry(1,event1Children,evnt1ChildEdges,eventIndexMap,1);
		if(!treeStr.equals("")){
			result+= treeStr;
		}		
		eventIndexMap.put("X", eventIndexMap.size()+1);
		result += "(n" + eventIndexMap.get("X") + ":Entity {name: 'X'}),";
		result += "(n1)-[r00:"+event1Rel+" {name: '"+event1Rel+"', id: '00'}]->(n"+ eventIndexMap.get("X") +"),";
		
		int event2Index = eventIndexMap.size();
		if(eventIndexMap.containsKey(event2)){
			event2Index = eventIndexMap.get(event2);
		}else{
			event2Index = eventIndexMap.size()+1;
			result += "(n"+ event2Index + ":Event {name: '" + event2 +"', lemma: '" + event2Lemma +"'}),";
			eventIndexMap.put(event2,event2Index);
		}
		result += "(n1)-[r01:may_prevent {name: 'may_prevent', id: '01'}]->(n"+event2Index+"),";
		treeStr = getTreeStrForCreateQry(event2Index,event2Children,evnt2ChildEdges,eventIndexMap,2);
		if(!treeStr.equals("")){
			result+= treeStr;
		}

		result += "(n"+event2Index+")-[r02:"+event2Rel+" {name: '"+event2Rel+"', id: '02'}]->(n"+ eventIndexMap.get("X") +"),";
		if(result.substring(result.length()-1).equalsIgnoreCase(",")){
			result = result.substring(0, result.length()-1) + ";";
		}

		return result;
	}
	
}
