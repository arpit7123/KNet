/**
 * 
 */
package edu.asu.nlu.knet.postprocessor;

import java.util.ArrayList;
import java.util.HashMap;

import module.graph.helper.Node;

/**
 * @author Arpit Sharma
 * @date Jun 30, 2017
 *
 */
public abstract class KnowledgeType {
	
	public abstract String getNeo4JCreateQuery();

	protected String getTreeStrForCreateQry(int parentNodeIndx, ArrayList<Node> eventChildren, ArrayList<String> evntChildEdges,HashMap<String,Integer> eventIndexMap, int level){
		String result = "";
		int indx = 0;
		if(eventChildren!=null){
//			int parentNodeIndx = eventIndexMap.size();
			for(Node eventChild : eventChildren){
				String tmpNode = "";
				String eventChildValue = eventChild.getValue();
				if(eventIndexMap.containsKey(eventChild.getValue())){
					tmpNode = "n"+eventIndexMap.get(eventChildValue);
				}else{
					tmpNode = "n"+(eventIndexMap.size()+1);
					eventIndexMap.put(eventChildValue, eventIndexMap.size()+1);
					result+= "("+tmpNode+":";
					if(eventChild.isAnEvent()){
						result += "Event ";
					}else if(eventChild.isAnEntity()){
						result += "Entity ";
					}else{
						result += "Other ";
					}
					if(eventChild.isAnEvent()){
						result += "{name: '"+eventChildValue+"', lemma: '"+eventChild.getLemma()+"'}),";
					}else{
						result += "{name: '"+eventChildValue+"'}),";
					}
				}
				String lvl = (level+"");
				result += "(n"+parentNodeIndx+")-[r"+lvl+indx+":"+evntChildEdges.get(indx)+" {name: '"+evntChildEdges.get(indx)+"', id: '" +lvl+indx+ "'}]->("+tmpNode+"),";
				
				String newLevel = (level+"")+indx;
				String childString = getTreeStrForCreateQry(eventIndexMap.get(eventChildValue),eventChild.getChildren(), eventChild.getEdgeList(),eventIndexMap,Integer.parseInt(newLevel));
				if(!childString.equals("")){
					result += childString;
				}
				indx++;
			}
		}
		return result;
	}
	
	
	protected String getTreeStr(ArrayList<Node> eventChildren, ArrayList<String> evntChildEdges){
		String result = "";
		int indx = 0;
		if(eventChildren!=null){
			for(Node eventChild : eventChildren){
				result += evntChildEdges.get(indx) +": "+eventChild.getValue()  ;
				String childString = getTreeStr(eventChild.getChildren(), eventChild.getEdgeList());
				if(!childString.equals("")){
					result += "[" + childString + "]";
				}
				if(indx<eventChildren.size()-1){
					result+= ";";
				}
				indx++;
			}
		}
		return result;
	}
}
