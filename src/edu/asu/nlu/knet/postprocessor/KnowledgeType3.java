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
public class KnowledgeType3 extends KnowledgeType {
	
	/**
	 * X.trait=true/false may cause execution of EVENT [ARG*: X; ARG*: Y] 
	 */
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private boolean traitpolarity;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private String trait;

	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private String event;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private String eventLemma;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private String eventRel;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<Node> eventChildren;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<String> evntChildEdges;
	
	public String getReadableKnowledge(){
		String result = "";

		result += "X." + trait +"= "+traitpolarity;
		result+=" may cause execution of " + event +"["+eventRel + ": X";

		String treeStr = getTreeStr(eventChildren,evntChildEdges);
		if(!treeStr.equals("")){
			result+="; "+treeStr;
		}

		result += "]";

		return result;
	}

	public String getNeo4JCreateQuery(){
		String result = "";
		HashMap<String,Integer> nodeIndexMap = new HashMap<String,Integer>();
		nodeIndexMap.put(trait, 1);
		result += "CREATE (n" + nodeIndexMap.size() + ":Trait {name: '" + trait +"',polarity: '" + traitpolarity +"'}),";
		nodeIndexMap.put("X", nodeIndexMap.size()+1);
		result += "(n" + nodeIndexMap.get("X") + ":Entity {name: 'X'}),";
		result += "(n1)-[r00 {name: 'is_trait_of', id: '00'}]->(n"+ nodeIndexMap.get("X") +"),";
		
		nodeIndexMap.put(event, nodeIndexMap.size()+1);
		
		result += "(n" + nodeIndexMap.size() + ":Event {name: '" + event +"', lemma: '" + eventLemma +"'}),";
		result += "(n1)-[r01 {name: 'may_cause', id: '01']->(n"+nodeIndexMap.size()+"),";
		int event2NodeIndx = nodeIndexMap.size();
		String treeStr = getTreeStrForCreateQry(event2NodeIndx,eventChildren,evntChildEdges,nodeIndexMap,1);
		if(!treeStr.equals("")){
			result+= treeStr;
		}

		result += "(n"+event2NodeIndx+")-[r02 {name: '"+eventRel+"', id: '02'}]->(n"+ nodeIndexMap.get("X") +"),";
		if(result.substring(result.length()-1).equalsIgnoreCase(",")){
			result = result.substring(0, result.length()-1) + ";";
		}

		return result;
	}
}
