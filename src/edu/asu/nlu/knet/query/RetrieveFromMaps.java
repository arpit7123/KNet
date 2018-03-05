/**
 * 
 */
package edu.asu.nlu.knet.query;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.asu.nlu.knet.utilities.JAWSutility;
import edu.asu.nlu.knet.utilities.Tools;

/**
 * @author arpit
 *
 */
public class RetrieveFromMaps {
//	{Event1 = {Event2 = {Relation = SentId}}}
	private HashMap<String,HashMap<String,HashMap<String,String>>> relationsMap = null;
	
//	{SentId = {Event = Slot}}
	private HashMap<String,HashMap<String,String>> slotValueMap = null;
	
//	{SentId = sent}
	private HashMap<String,String> sentMap = null;
	
	private HashSet<String> event1List = null;
	private HashSet<String> relationsList = null;
	private HashSet<String> event2List = null;
	private HashSet<String> slot1List = null;
	private HashSet<String> slot2List = null;
	
	private JAWSutility jaws = null;
	
	@SuppressWarnings("unchecked")
	public RetrieveFromMaps(){
		String relationsMapFile = RetrieveFromMaps.class.getResource("relationsMap").toString();
		relationsMapFile = relationsMapFile.substring(relationsMapFile.indexOf(":")+1);
		String slotValueMapFile = RetrieveFromMaps.class.getResource("slotValueMap").toString();
		slotValueMapFile = slotValueMapFile.substring(slotValueMapFile.indexOf(":")+1);
		String sentMapFile = RetrieveFromMaps.class.getResource("sentMap").toString();
		sentMapFile = sentMapFile.substring(sentMapFile.indexOf(":")+1);
		
		this.relationsMap = (HashMap<String, HashMap<String, HashMap<String, String>>>) Tools.load(relationsMapFile);
		this.slotValueMap = (HashMap<String, HashMap<String, String>>) Tools.load(slotValueMapFile);
		this.sentMap = (HashMap<String, String>) Tools.load(sentMapFile);
		
		this.event1List = new HashSet<String>();
		this.relationsList = new HashSet<String>();
		this.event2List = new HashSet<String>();
		this.slot1List = new HashSet<String>();
		this.slot2List = new HashSet<String>();
		
		jaws = new JAWSutility();
	}
	
	public String process(String query) throws JSONException{
		ArrayList<String> sentList = getSentences(query);
		JSONArray jsonArray= new JSONArray();
		JSONObject jObjInfo = new JSONObject();
		jObjInfo.put("event1List",Joiner.on(",").join(this.event1List));
		jObjInfo.put("relationsList",Joiner.on(",").join(this.relationsList));
		jObjInfo.put("event2List",Joiner.on(",").join(this.event2List));
		jObjInfo.put("slot1List",Joiner.on(",").join(this.slot1List));
		jObjInfo.put("slot2List",Joiner.on(",").join(this.slot2List));
		jsonArray.put(jObjInfo);
		for(String sent : sentList){
			JSONObject jObj = new JSONObject();
			jObj.put("sent", sent);
			jObj.put("weight", 1);
			jsonArray.put(jObj);
		}
		return jsonArray.toString();
	}
	
	public ArrayList<String> getSentences(String query){
		this.event1List.clear();
		this.relationsList.clear();
		this.event2List.clear();
		this.slot1List.clear();
		this.slot2List.clear();
		
		HashSet<String> sentSet = Sets.newHashSet();
		
		QueryParser qp = new QueryParser();

		Query q = qp.parseQuery(query);

		ArrayList<EventRelation> ers = q.getEventRelations();
		ArrayList<SlotRelation> srs = q.getSlotRelations();

		EventRelation er = ers.get(0);
		String event1 = er.getEvent1().getEvent();
		event1 = jaws.getBaseForm(event1, "v");
		String relation = er.getRelation();
		String event2 = er.getEvent2().getEvent();
		event2 = jaws.getBaseForm(event2, "v");

		SlotRelation sr1 = srs.get(0);
		SlotRelation sr2 = srs.get(1);

		String slot1 = sr1.getSlotValue();
		String slot2 = sr2.getSlotValue();
		
		HashMap<String,String> sentIdEvent1Map = Maps.newHashMap();
		HashMap<String,String> sentIdEvent2Map = Maps.newHashMap();

		if(!event1.equals("*") 
				&& !relation.equals("*")
				&& !event2.equals("*")){
			this.event1List.add(event1);
			this.relationsList.add(relation);
			this.event2List.add(event2);
//			relTableQuery = new BasicDBObject("EVENT_VALUE1", event1)
//			.append("EVENT_VALUE2", event2)
//			.append("REL_VALUE", relation);
			if(this.relationsMap.containsKey(event1)){
				HashMap<String,HashMap<String,String>> innerMap1 = this.relationsMap.get(event1);
				if(innerMap1.containsKey(event2)){
					HashMap<String,String> innerMap2 = innerMap1.get(event2);
					if(innerMap2.containsKey(relation)){
						String sentId = innerMap2.get(relation);
						sentIdEvent1Map.put(sentId, event1);
						sentIdEvent2Map.put(sentId, event2);
					}
				}
			}
		}else if(event1.equals("*") 
				&& !relation.equals("*")
				&& !event2.equals("*")){
			this.relationsList.add(relation);
			this.event2List.add(event2);
//			relTableQuery = new BasicDBObject("EVENT_VALUE2", event1)
//			.append("REL_VALUE", relation);
			
			for(String ev1 : this.relationsMap.keySet()){
				HashMap<String,HashMap<String,String>> innerMap1 = this.relationsMap.get(ev1);
				if(innerMap1.containsKey(event2)){
					HashMap<String,String> innerMap2 = innerMap1.get(event2);
					if(innerMap2.containsKey(relation)){
						this.event1List.add(ev1);
						String sentId = innerMap2.get(relation);
						sentIdEvent1Map.put(sentId, ev1);
						sentIdEvent2Map.put(sentId, event2);
					}
				}				
			}
		}else if(!event1.equals("*") 
				&& !relation.equals("*")
				&& event2.equals("*")){
			this.event1List.add(event1);
			this.relationsList.add(relation);
//			relTableQuery = new BasicDBObject("EVENT_VALUE1", event1)
//			.append("REL_VALUE", relation);
			if(this.relationsMap.containsKey(event1)){
				HashMap<String,HashMap<String,String>> innerMap1 = this.relationsMap.get(event1);
				for(String ev2 : innerMap1.keySet()){
					HashMap<String,String> innerMap2 = innerMap1.get(ev2);
					if(innerMap2.containsKey(relation)){
						this.event2List.add(ev2);
						String sentId = innerMap2.get(relation);
						sentIdEvent1Map.put(sentId, event1);
						sentIdEvent2Map.put(sentId, ev2);
					}
				}				
			}
		}else if(!event1.equals("*")  
				&& relation.equals("*")
				&& !event2.equals("*")){
			this.event1List.add(event1);
			this.event2List.add(event2);
//			relTableQuery = new BasicDBObject("EVENT_VALUE1", event1)
//			.append("EVENT_VALUE2", event2);
			if(this.relationsMap.containsKey(event1)){
				HashMap<String,HashMap<String,String>> innerMap1 = this.relationsMap.get(event1);
				if(innerMap1.containsKey(event2)){
					HashMap<String,String> innerMap2 = innerMap1.get(event2);
					for(String rel : innerMap2.keySet()){
						this.relationsList.add(rel);
						String sentId = innerMap2.get(rel);
						sentIdEvent1Map.put(sentId, event1);
						sentIdEvent2Map.put(sentId, event2);
					}
				}
			}
		}
		
		for(String sID : sentIdEvent1Map.keySet()){
			if(this.slotValueMap.containsKey(sID)){
				HashMap<String,String> inMap = this.slotValueMap.get(sID);
				String ev1 = sentIdEvent1Map.get(sID);
				if(sentIdEvent2Map.containsKey(sID)){
					String ev2 = sentIdEvent2Map.get(sID);
					String tempSlot1 = "";
					String tempSlot2 = "";
					if(inMap.containsKey(ev1)){
						tempSlot1 = inMap.get(ev1);
					}
					if(inMap.containsKey(ev2)){
						tempSlot2 = inMap.get(ev2);
					}
					
					if(!event1.equalsIgnoreCase("*") && !event2.equalsIgnoreCase("*")
							&& !slot1.equalsIgnoreCase("*") && !slot2.equalsIgnoreCase("*")){
						this.slot1List.add(slot1);
						this.slot2List.add(slot2);
						if(ev1.equalsIgnoreCase(event1) && tempSlot1.equalsIgnoreCase(slot1)
								&& ev2.equalsIgnoreCase(event2) && tempSlot2.equalsIgnoreCase(slot2)){
							if(this.sentMap.containsKey(sID)){
								sentSet.add(this.sentMap.get(sID));
							}
						}
					}else if(event1.equalsIgnoreCase("*") && !event2.equalsIgnoreCase("*")
							&& !slot1.equalsIgnoreCase("*") && !slot2.equalsIgnoreCase("*")){
						this.slot1List.add(slot1);
						this.slot2List.add(slot2);
						if(tempSlot1.equalsIgnoreCase(slot1)
								&& ev2.equalsIgnoreCase(event2) && tempSlot2.equalsIgnoreCase(slot2)){
							if(this.sentMap.containsKey(sID)){
								sentSet.add(this.sentMap.get(sID));
							}
						}
					}else if(!event1.equalsIgnoreCase("*") && event2.equalsIgnoreCase("*")
							&& !slot1.equalsIgnoreCase("*") && !slot2.equalsIgnoreCase("*")){
						this.slot1List.add(slot1);
						this.slot2List.add(slot2);
						if(ev1.equalsIgnoreCase(event1) && tempSlot1.equalsIgnoreCase(slot1)
								&& tempSlot2.equalsIgnoreCase(slot2)){
							if(this.sentMap.containsKey(sID)){
								sentSet.add(this.sentMap.get(sID));
							}
						}
					}else if(!event1.equalsIgnoreCase("*") && !event2.equalsIgnoreCase("*")
							&& slot1.equalsIgnoreCase("*") && !slot2.equalsIgnoreCase("*")){
						this.slot2List.add(slot2);
						if(ev1.equalsIgnoreCase(event1) && ev2.equalsIgnoreCase(event2)
								&& tempSlot2.equalsIgnoreCase(slot2)){
							this.slot1List.add(tempSlot1);
							if(this.sentMap.containsKey(sID)){
								sentSet.add(this.sentMap.get(sID));
							}
						}
					}else if(!event1.equalsIgnoreCase("*") && !event2.equalsIgnoreCase("*")
							&& !slot1.equalsIgnoreCase("*") && slot2.equalsIgnoreCase("*")){
						this.slot1List.add(slot1);
						if(ev1.equalsIgnoreCase(event1) && ev2.equalsIgnoreCase(event2)
								&& tempSlot1.equalsIgnoreCase(slot1)){
							this.slot2List.add(tempSlot2);
							if(this.sentMap.containsKey(sID)){
								sentSet.add(this.sentMap.get(sID));
							}
						}
					}else if(!event1.equalsIgnoreCase("*") && !event2.equalsIgnoreCase("*")
							&& slot1.equalsIgnoreCase("*") && slot2.equalsIgnoreCase("*")){
						if(ev1.equalsIgnoreCase(event1) && ev2.equalsIgnoreCase(event2)){
							this.slot1List.add(tempSlot1);
							this.slot2List.add(tempSlot2);
							if(this.sentMap.containsKey(sID)){
								sentSet.add(this.sentMap.get(sID));
							}
						}
					}
				}
			}
		}

		return new ArrayList<String>(sentSet);
	}
	
	
	/**
	 * 
	 * @param args
	 * @throws UnknownHostException

	Event1=E1, REL=R, Event2=E2, Slot1=S1, Slot2=S2
	query = "Event1=bully, REL=objective, Event2=cajole, Slot1=agent, Slot2=agent";
	Event1=*, REL=R, Event2=E2, Slot1=S1, Slot2=S2
	query = "Event1=*, REL=R, Event2=E2, Slot1=S1, Slot2=S2";
	Event1=E1, REL=R, Event2=*, Slot1=S1, Slot2=S2
	query = "Event1=E1, REL=R, Event2=*, Slot1=S1, Slot2=S2";
	Event1=E1, REL=R, Event2=E2, Slot1=*, Slot2=S2
	query = "Event1=E1, REL=R, Event2=E2, Slot1=*, Slot2=S2";
	Event1=E1, REL=R, Event2=E2, Slot1=S1, Slot2=*
	query = "Event1=E1, REL=R, Event2=E2, Slot1=S1, Slot2=*";
	Event1=E1, REL=∗, Event2=E2, Slot1=S1, Slot2=S2
	query = "Event1=E1, REL=∗, Event2=E2, Slot1=S1, Slot2=S2";
	Event1=E1, REL=∗, Event2=E2, Slot1=∗, Slot2=∗
	query = "Event1=E1, REL=∗, Event2=E2, Slot1=∗, Slot2=∗";
	 * @throws JSONException 
	 * */
	public static void main(String[] args) throws UnknownHostException, JSONException{
		RetrieveFromMaps rfdb = new RetrieveFromMaps();
		String query = "Event1=bully, REL=*,Event2=rescue,Slot1=*,Slot2=*";

		System.out.println(rfdb.process(query));
	}
	
	
	public static void main_(String[] args) throws UnknownHostException, JSONException{
		RetrieveFromMaps rfdb = new RetrieveFromMaps();
//		String query = "Event1=bully, REL=next_event, Event2=cajole, Slot1=agent, Slot2=agent";
//
//		System.out.println(rfdb.process(query));
		rfdb.sentMap.put("1", "children bullying a tortoise on a shore, so he rescued the tortoise");
		
		if(rfdb.relationsMap.containsKey("bully")){
			HashMap<String,HashMap<String,String>> inMap = rfdb.relationsMap.get("bully");
			if(inMap.containsKey("rescue")){
				HashMap<String,String> inMap2 = inMap.get("rescue");
				if(inMap2.containsKey("causes")){
					@SuppressWarnings("unused")
					String sentId = inMap2.get("causes");
				}else{
					inMap2.put("causes", "1");
					inMap.put("rescue", inMap2);
					rfdb.relationsMap.put("bully", inMap);
				}
			}else{
				HashMap<String,String> inMap2 = new HashMap<String,String>();
				inMap2.put("causes", "1");
				inMap.put("rescue", inMap2);
				rfdb.relationsMap.put("bully", inMap);
			}
		}else{
			HashMap<String,HashMap<String,String>> inMap = new HashMap<String, HashMap<String,String>>();
			HashMap<String,String> inMap2 = new HashMap<String,String>();
			inMap2.put("causes", "1");
			inMap.put("bully", inMap2);
			rfdb.relationsMap.put("bully", inMap);
		}
		
		if(rfdb.slotValueMap.containsKey("1")){
			
		}else{
			HashMap<String,String> inMap = new HashMap<String,String>();
			inMap.put("bully", "recipient");
			inMap.put("rescue", "recipient");
			rfdb.slotValueMap.put("1", inMap);
		}
		
		Tools.saveObject("/home/arpit/workspace/KnowledgeNet/sentMap", rfdb.sentMap);
		Tools.saveObject("/home/arpit/workspace/KnowledgeNet/relationsMap", rfdb.relationsMap);
		Tools.saveObject("/home/arpit/workspace/KnowledgeNet/slotValueMap", rfdb.slotValueMap);
	}

}
