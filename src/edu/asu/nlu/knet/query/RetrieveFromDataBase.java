package edu.asu.nlu.knet.query;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import edu.asu.nlu.knet.utilities.Tools;

/**
 * Given a @Query object, it can handle retrieval, ranking and sorting the 
 * resulting sentences from database.
 * @author somak
 *
 */
public class RetrieveFromDataBase {

	private static final String KNOWLEDGE_BASE_DB_NAME = "KnowledgeNetDB";
	private MongoClient mongoClient = null;
	private DB db = null;
	@SuppressWarnings("unused")
	private DBCollection documentTable = null;
	private DBCollection sentenceTable = null;
	private DBCollection slotValueTable = null;
	private DBCollection relationsTable = null;
	@SuppressWarnings("unused")
	private DBCollection attributeKnowledgeTable = null;

	public RetrieveFromDataBase() throws UnknownHostException{
		this.mongoClient = new MongoClient();
		this.db = mongoClient.getDB(KNOWLEDGE_BASE_DB_NAME);

		this.documentTable = this.db.getCollection("DocumentTable");
		this.sentenceTable = this.db.getCollection("SentenceTable");
		this.slotValueTable = this.db.getCollection("SlotValueTable");
		this.relationsTable = this.db.getCollection("RelationsTable");
		this.attributeKnowledgeTable = this.db.getCollection("AttributeKnowledgeTable");
	}

	/**
	 * Finds results from database, weights the results and sorts them
	 * @param query
	 */
	public ArrayList<WeightedDBObject> handleQuery(Query query) {

		ArrayList<String> sentenceIds = new ArrayList<String>();

		for(EventRelation eventRelation: query.getEventRelations()) {
			BasicDBObject query1 = null;
			Event event1, event2;
			String relation = null;
			if((event1 = eventRelation.getEvent1()) != null) {
				query1 = new BasicDBObject("EVENT_VALUE1", event1.getEvent());
			}
			if((event2 = eventRelation.getEvent2()) != null) {
				query1 = query1 == null ? new BasicDBObject("EVENT_VALUE2", event2.getEvent()) :
					query1.append("EVENT_VALUE2", event2.getEvent());
			}
			if((relation = eventRelation.getRelation()) != null) {
				query1 = query1 == null ? new BasicDBObject("REL_VALUE", relation) :
					query1.append("REL_VALUE", relation);
			}

			DBCursor cursor = this.relationsTable.find(query1);
			try {
				//				System.out.println("RelationsTable results:: ");
				//				System.out.println("Query:"+ query1);
				while(cursor.hasNext()) {
					DBObject dbObject =cursor.next() ;
					String sentenceID = dbObject.get("SENT_ID").toString();
					sentenceIds.add(sentenceID);
					//					System.out.println(dbObject);
				}
			} finally {
				cursor.close();
			}
		}

		for(SlotRelation slotRelation: query.getSlotRelations()) {
			BasicDBObject query1 = null;
			Event event;
			@SuppressWarnings("unused")
			String relation, slotValue = null;
			if((event = slotRelation.getEvent()) != null) {
				query1 = new BasicDBObject("VALUE", event.getEvent());
			}
			if((relation = slotRelation.getSlotType()) != null) {
				query1 = query1 == null ? new BasicDBObject("SLOT", relation) :
					query1.append("SLOT", relation);
			}
			/*if((slotValue = slotRelation.getSlotValue()) != null) {
				query1 = query1 == null ? new BasicDBObject("SLOT_VALUE", slotValue) :
					query1.append("SLOT_VALUE", slotValue);
			}*/
			DBCursor cursor = this.slotValueTable.find(query1);
			try {
				//				System.out.println("RelationsTable results:: ");
				//				System.out.println("Query:"+ query1);
				while(cursor.hasNext()) {
					DBObject dbObject =cursor.next() ;
					String sentenceID = dbObject.get("SENT_ID").toString();
					sentenceIds.add(sentenceID);
					//					System.out.println(dbObject);
				}
			} finally {
				cursor.close();
			}
		}

		Collections.sort(sentenceIds);
		ArrayList<WeightedDBObject> results = null;

		if(sentenceIds.size()>0){
			sentenceIds = getUniqueIDs(sentenceIds);
			results = new ArrayList<WeightedDBObject>();
			for(String sentenceId: sentenceIds) {
				ArrayList<DBObject> eventRelationResults = queryEventRelationsTable(sentenceId);
				ArrayList<DBObject> slotRelationResults = querySlotValueTable(sentenceId);
				DBObject sentenceInfo = querySentenceTable(sentenceId);
				String sentence = sentenceInfo.get("SENT").toString();
				results.add(new WeightedDBObject(eventRelationResults, slotRelationResults, 0.0,
						sentenceId, sentence));
			}

			sortAndRankSentences(results, query);
		}

		return results;
	}

	private DBObject querySentenceTable(String sentenceId) {
		BasicDBObject query1 = new BasicDBObject("SENT_ID", sentenceId);
		DBCursor cursor = this.sentenceTable.find(query1);
		DBObject dbObject  = null;
		try {
			if(cursor.hasNext()) {
				dbObject =cursor.next() ;
				//				System.out.println(dbObject);
			}
		} finally {
			cursor.close();
		}
		return dbObject;
	}

	private void sortAndRankSentences(ArrayList<WeightedDBObject> results,
			Query query) {
		for(WeightedDBObject weightedDBObject: results) {
			double weight = computeSimilairty(weightedDBObject, query);
			weightedDBObject.setWeight(weight);
		}

		Collections.sort(results, new Comparator<WeightedDBObject>() {

			@Override
			public int compare(WeightedDBObject o1, WeightedDBObject o2) {
				double diff = o1.getWeight()- o2.getWeight();
				if(diff > 0)
					return -1;
				else if(diff < 0)
					return 1;
				return 0;
			}
		});
	}

	private ArrayList<String> getUniqueIDs(ArrayList<String> sentenceIds) {
		ArrayList<String> uniqueIDs = new ArrayList<String>();
		uniqueIDs.add(sentenceIds.get(0));
		String prev = sentenceIds.get(0);
		for(String id: sentenceIds) {
			if(!id.equals(prev)) {
				uniqueIDs.add(id);
				prev = id;
			}
		}
		return uniqueIDs;
	}

	private ArrayList<DBObject> queryEventRelationsTable(String sentenceId) {
		ArrayList<DBObject> dbObjectResults = new ArrayList<DBObject>();
		BasicDBObject query1 = new BasicDBObject("SENT_ID", sentenceId);
		DBCursor cursor = this.relationsTable.find(query1);
		try {
			while(cursor.hasNext()) {
				DBObject dbObject =cursor.next() ;
				dbObjectResults.add(dbObject);
				//				System.out.println(dbObject);
			}
		} finally {
			cursor.close();
		}
		return dbObjectResults;
	}

	private ArrayList<DBObject> querySlotValueTable(String sentenceId) {
		ArrayList<DBObject> dbObjectResults = new ArrayList<DBObject>();
		BasicDBObject query1 = new BasicDBObject("SENT_ID", sentenceId);
		DBCursor cursor = this.slotValueTable.find(query1);
		try {
			while(cursor.hasNext()) {
				DBObject dbObject =cursor.next() ;
				dbObjectResults.add(dbObject);
				//				System.out.println(dbObject);
			}
		} finally {
			cursor.close();
		}
		return dbObjectResults;
	}

	private double computeSimilairty(WeightedDBObject result, Query query) {
		double wt = 0;
		for(SlotRelation slotRelation: query.getSlotRelations()) {
			boolean slotMatches = doesSlotsMatch(slotRelation, slotRelation.getValueMatches(),
					result.getSlotRelationResults());
			if(!slotMatches)
				return Double.MIN_VALUE;
		}
		for(EventRelation eventRelation: query.getEventRelations()) {
			wt += eventRelation.returnSimilarity(result.getEventRelationResults());
		}

		for(SlotRelation slotRelation: query.getSlotRelations()) {
			wt += slotRelation.returnSimilarity(result.getSlotRelationResults());
		}
		return wt;
	}

	private boolean doesSlotsMatch(SlotRelation slotRelation,
			SlotRelation valueMatches, ArrayList<DBObject> slotRelationResults) {
		if(valueMatches  == null)
			return true;
		String valueForSlot1 = getValueOfSlot(slotRelationResults, slotRelation);
		String valueForSlot2 = getValueOfSlot(slotRelationResults, valueMatches);
		if(valueForSlot1 == null || valueForSlot2 == null ||
				!valueForSlot1.equals(valueForSlot2))
			return false;
		return true;
	}

	private String getValueOfSlot(ArrayList<DBObject> slotRelationResults,
			SlotRelation slotRelation) {
		String slotType = slotRelation.getSlotType();
		Event event = slotRelation.getEvent();
		for(DBObject dbObject:slotRelationResults) {
			String type = dbObject.get("SLOT").toString();
			String value = dbObject.get("SLOT_VALUE").toString();
			String eventName = dbObject.get("VALUE").toString();

			if(slotType.equals(type) && event.getEvent().equals(eventName))
				return value;
		}
		return null;
	}

	public ArrayList<String> getSentences(String query){
		QueryParser qp = new QueryParser();


		Query q = qp.parseQuery(query);

		ArrayList<EventRelation> ers = q.getEventRelations();
		ArrayList<SlotRelation> srs = q.getSlotRelations();

		EventRelation er = ers.get(0);
		String event1 = er.getEvent1().getEvent();
		String relation = er.getRelation();
		String event2 = er.getEvent2().getEvent();

		SlotRelation sr1 = srs.get(0);
		SlotRelation sr2 = srs.get(1);

		String slot1 = sr1.getSlotValue();
		String slot2 = sr2.getSlotValue();

		BasicDBObject relTableQuery = null;
		if(!event1.equals("*") 
				&& !relation.equals("*")
				&& !event2.equals("*")){
			relTableQuery = new BasicDBObject("EVENT_VALUE1", event1)
			.append("EVENT_VALUE2", event2)
			.append("REL_VALUE", relation);
		}else if(event1.equals("*") 
				&& !relation.equals("*")
				&& !event2.equals("*")){
			relTableQuery = new BasicDBObject("EVENT_VALUE2", event1)
			.append("REL_VALUE", relation);
		}else if(!event1.equals("*") 
				&& !relation.equals("*")
				&& event2.equals("*")){
			relTableQuery = new BasicDBObject("EVENT_VALUE1", event1)
			.append("REL_VALUE", relation);
		}else if(!event1.equals("*")  
				&& relation.equals("*")
				&& !event2.equals("*")){
			relTableQuery = new BasicDBObject("EVENT_VALUE1", event1)
			.append("EVENT_VALUE2", event2);
		}

		ArrayList<String> sentences = Lists.newArrayList();
		DBCursor cursor = this.relationsTable.find(relTableQuery);

		try {
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();

				BasicDBObject slotValTableQuery1 = new BasicDBObject("SENT_ID", obj.get("SENT_ID"))
				.append("BASE_VALUE", event1);
				if(!slot1.equalsIgnoreCase("*")){
					slotValTableQuery1.append("SLOT", slot1);	
				}

				BasicDBObject slotValTableQuery2 = new BasicDBObject("SENT_ID", obj.get("SENT_ID"))
				.append("BASE_VALUE", event2);
				if(!slot2.equalsIgnoreCase("*")){
					slotValTableQuery2.append("SLOT", slot2);	
				}

				if(this.slotValueTable.find(slotValTableQuery1).size()!=0
						&& this.slotValueTable.find(slotValTableQuery2).size()!=0){
					BasicDBObject sentenceTableQuery = new BasicDBObject("SENT_ID", obj.get("SENT_ID"));
					DBCursor cur = this.sentenceTable.find(sentenceTableQuery);
					try{
						while(cur.hasNext()){
							sentences.add(cur.next().get("SENT").toString());
						}
					}finally{
						cur.close();
					}

				}			
			}
		} finally {
			cursor.close();
		}

		return sentences;
	}
	
	
	public String process(String query) throws JSONException{
		ArrayList<String> sentList = getSentences(query);
		JSONArray jsonArray= new JSONArray();
		for(String sent : sentList){
			JSONObject jObj = new JSONObject();
			jObj.put("sent", sent);
			jObj.put("weight", 1);
			jsonArray.put(jObj);
		}
		return jsonArray.toString();
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
	public static void main__(String[] args) throws UnknownHostException, JSONException{
		RetrieveFromDataBase rfdb = new RetrieveFromDataBase();
		String query = "Event1=bully, REL=next_event, Event2=cajole, Slot1=agent, Slot2=agent";

		System.out.println(rfdb.process(query));
	}
	
	
	public static void main___(String[] args) throws UnknownHostException{
		RetrieveFromDataBase rfdb = new RetrieveFromDataBase();
		int counter = 1;
		HashMap<String,HashMap<String,HashMap<String,String>>> relationsMap	= Maps.newHashMap();
//		DBCursor cursor = rfdb.relationsTable.find();
		DBCursor cursor = rfdb.slotValueTable.find();

		try {
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				
				System.out.println(counter);
				String sentId = obj.get("SENT_ID").toString();
				String event1 = obj.get("EVENT_VALUE1").toString();
				String relation = obj.get("REL_VALUE").toString();
				String event2 = obj.get("EVENT_VALUE2").toString();
				
//				if(relationsMap!=null){
					if(relationsMap.containsKey(event1)){
						HashMap<String,HashMap<String,String>> innerMap1 = relationsMap.get(event1);
						if(innerMap1.containsKey(event2)){
							HashMap<String,String> innerMap2 = innerMap1.get(event2);
							if(innerMap2.containsKey(relation)){
								@SuppressWarnings("unused")
								String sId = innerMap2.get(relation);
							}else{
								innerMap2.put(relation, sentId);
								innerMap1.put(event2, innerMap2);
								relationsMap.put(event1, innerMap1);
							}
						}else{
							HashMap<String,String> innerMap2 = Maps.newHashMap();
							innerMap2.put(relation, sentId);
							innerMap1.put(event2, innerMap2);
							relationsMap.put(event1, innerMap1);
						}
					}else{
						HashMap<String,HashMap<String,String>> innerMap1 = new HashMap<String,HashMap<String, String>>();
						HashMap<String,String> innerMap2 = new HashMap<String, String>();
						innerMap2.put(relation, sentId);
						innerMap1.put(event2, innerMap2);
						relationsMap.put(event1, innerMap1);
					}
//				}
					counter++;				
			}
		}finally{
			cursor.close();
		}
		
		Tools.saveObject("/home/arpit/workspace/KnowledgeNet/relationsMap", relationsMap);
	}
	
	
	public static void main_(String[] args) throws UnknownHostException{
		RetrieveFromDataBase rfdb = new RetrieveFromDataBase();
		int counter = 1;
		HashMap<String,HashMap<String,String>> slotValueMap	= Maps.newHashMap();
		DBCursor cursor = rfdb.slotValueTable.find();

		try {
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				
//				System.out.println(counter);
				String sentId = obj.get("SENT_ID").toString();
				String event = obj.get("BASE_VALUE").toString();
				String slot = obj.get("SLOT").toString();
				
				if(slotValueMap.containsKey(sentId)){
					HashMap<String,String> innerMap1 = slotValueMap.get(sentId);
					if(innerMap1.containsKey(event)){
						
					}else{
						innerMap1.put(event,slot);
						slotValueMap.put(sentId, innerMap1);
					}
				}else{
					HashMap<String,String> innerMap1 = Maps.newHashMap();
					innerMap1.put(event,slot);
					slotValueMap.put(sentId, innerMap1);
				}
				
				
				

					counter++;				
			}
		}finally{
			cursor.close();
		}
		
		System.out.println(counter);
		
		Tools.saveObject("/home/arpit/workspace/KnowledgeNet/slotValueMap", slotValueMap);
	}
	
	
	public static void main____(String[] args) throws UnknownHostException{
		RetrieveFromDataBase rfdb = new RetrieveFromDataBase();
		int counter = 1;
		HashMap<String,String> sentMap	= Maps.newHashMap();
		DBCursor cursor = rfdb.sentenceTable.find();

		try {
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				
//				System.out.println(counter);
				String sentId = obj.get("SENT_ID").toString();
				String sentence = obj.get("SENT").toString();		
				sentMap.put(sentId, sentence);
				counter++;				
			}
		}finally{
			cursor.close();
		}
		
		System.out.println(counter);
		
		Tools.saveObject("/home/arpit/workspace/KnowledgeNet/sentMap", sentMap);
	}
	
	public static void main(String[] args) throws UnknownHostException{
		RetrieveFromDataBase rfdb = new RetrieveFromDataBase();
//		int counter = 1;
		@SuppressWarnings("unused")
		HashMap<String,String> sentMap	= Maps.newHashMap();
		DBCursor cursor = rfdb.relationsTable.find();

		HashSet<String> setOfRels = new HashSet<String>();
		try {
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				String relation = obj.get("REL_VALUE").toString();
				setOfRels.add(relation);
//				System.out.println(counter);
//				String sentId = obj.get("SENT_ID").toString();
//				String sentence = obj.get("SENT").toString();		
//				sentMap.put(sentId, sentence);
//				counter++;				
			}
		}finally{
			cursor.close();
		}
		
		for(String s : setOfRels){
			System.out.println(s);
		}
		
//		System.out.println(counter);
//		
//		Tools.saveObject("/home/arpit/workspace/KnowledgeNet/sentMap", sentMap);
	}
}



