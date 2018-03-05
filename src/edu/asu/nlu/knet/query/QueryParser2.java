package edu.asu.nlu.knet.query;
//package knowledgeNet.query;
//
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.json.JSONException;
//
//
//public class QueryParser2 {
//
//	private Pattern queryPattern = Pattern.compile("(.+?)(;)");
//	private Pattern slotQueryPattern = Pattern.compile("(.+?)(,|$)");
//	private RetrieveFromDataBase retrieveFromDataBase;
//	
//	
//	public Query parseStringQuery(String query){
//		Query result = null;
//		ArrayList<String> eventRelations = new ArrayList<String>();
//		ArrayList<String> slotRelations = new ArrayList<String>();
//		
//		
//		
//		
//		return result;
//	}
//	
//	
//	public Query parseQuery(String query) {
//		
//		ArrayList<String> eventRelations = new ArrayList<String>();
//		ArrayList<String> slotRelations = new ArrayList<String>();
//		
//		Matcher m = this.queryPattern.matcher(query);
//		if(m.find()){
//			String eventRelation = m.group(1);
//			eventRelations.add(eventRelation);
//			System.out.println(eventRelation);	
//		}
//		Matcher m1 = this.slotQueryPattern.matcher(query.substring(m.end(2)));
//		while(m1.find()) {
//			String slotRelation = m1.group(1);
//			slotRelations.add(slotRelation);
//			System.out.println(slotRelation);
//		}
//		
//		return null;//new Query(eventRelations,slotRelations);
//	}
//	
//	public ArrayList<WeightedDBObject> retrieveFromDatabase(Query query) throws UnknownHostException {
//		retrieveFromDataBase = new RetrieveFromDataBase();
//		return retrieveFromDataBase.handleQuery(query);
//	}
//	
//	public String retrieveFromDatabaseAndReturnJson(Query query) throws UnknownHostException, JSONException{
//		ArrayList<WeightedDBObject> results = retrieveFromDatabase(query);
//		return JsonExporter.exportToJSON(results);
//	}
//	
//	public static void main(String[] args){
//		/* An example from Database (RelationsTable):
//		 * 
//		 * { "_id" : { "$oid" : "55241e17ace0eb0a1b9f4b51"} ,
//		 *  "EVENT_VALUE1" : "giving_3" ,
//		 *  "EVENT_VALUE2" : "giving_19" , 
//		 *  "REL_TYPE" : "X" , 
//		 *  "REL_VALUE" : 
//		 *  "next_event" , 
//		 *  "SENT_ID" : "667"
//		 *  }*/
//		QueryParser2 qp = new QueryParser2();
//		String query = "give next_event give;";
////		query = "explain next_event convince;";
////				+ "rescued age X, rescued rec Z";
//		Query parsedQuery = qp.parseQuery(query);
//		ArrayList<WeightedDBObject> results;
//		try {
//			results = qp.retrieveFromDatabase(parsedQuery);
////			for(WeightedDBObject result:results) {
////				if(result.getWeight() > Double.MIN_VALUE)
////					System.out.println(result.toString());
////			}
//			System.out.println(JsonExporter.exportToJSON(results));
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}		
//	}
//}
