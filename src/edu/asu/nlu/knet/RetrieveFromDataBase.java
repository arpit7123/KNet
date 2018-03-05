package edu.asu.nlu.knet;

import java.net.UnknownHostException;
import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;

import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class RetrieveFromDataBase {
	
	private MongoClient mongoClient = null;
	private DB db = null;
	private DBCollection documentTable = null;
	private DBCollection sentenceTable = null;
	private DBCollection slotValueTable = null;
	private DBCollection relationsTable = null;
	
	private QueryParser queryParser = null;
	
	private DBObject verb1Object = null;
	@Getter (AccessLevel.PUBLIC) private JSONObject verb1Json = null;
	private ArrayList<DBObject> relationsList = null;
	private ArrayList<DBObject> verb2sList = null;
	private ArrayList<DBObject> resultList = null;
	
	public RetrieveFromDataBase() throws UnknownHostException{
		this.mongoClient = new MongoClient();
		this.db = mongoClient.getDB("mydb");
		this.documentTable = this.db.getCollection("DocumentTable");
		this.sentenceTable = this.db.getCollection("SentenceTable");
		this.slotValueTable = this.db.getCollection("SlotValueTable");
		this.relationsTable = this.db.getCollection("RelationsTable");
		
		this.queryParser = new QueryParser();
	}

	public static void main(String[] args) throws UnknownHostException{
		RetrieveFromDataBase rfdb = new RetrieveFromDataBase();
//		rfdb.documentTable.drop();
//		rfdb.sentenceTable.drop();
//		rfdb.slotValueTable.drop();
//		rfdb.relationsTable.drop();
		DBCursor dbcur = rfdb.relationsTable.find();
		while(dbcur.hasNext()){
			System.out.println(dbcur.next());
		}
		try{
//			for(String s : rfdb.db.getCollectionNames()){
//				System.out.println(s);
//			}
//			String verb1 = "going";
//			String rel = "objective";
//			String verb2 = "happen";
//			RetrieveFromDataBase rfdb = new RetrieveFromDataBase();
////			System.out.println("result is :\n"+rfdb.getFromMongoDB("tried"));
//			rfdb.getFromMongoDB(verb1);
//			rfdb.getRelations();
//			rfdb.getVerb2s(rel);
//			rfdb.getFinalRelation(verb2);
//			for(String s : rfdb.getFinalResult()){
//				System.out.println(s);
//			}
		}catch(Exception e){
			System.out.println("Exception occured");
		}
	}
	
//	public void getFromMongoDB(String verb1){
//		DBObject result = this.dbc.findOne(new BasicDBObject("verb1",verb1));
//		this.verb1Object = result;
//		String json = JSON.serialize(this.verb1Object);
//		this.verb1Json = (JSONObject) JSONObject.stringToValue(json);
//	}
	
	@SuppressWarnings("unchecked")
	public void getRelations(){
		ArrayList<DBObject> second = (ArrayList<DBObject>) this.verb1Object.get("rels");
		this.relationsList = second;
	}
	
	@SuppressWarnings("unchecked")
	public void getVerb2s(String relation){
		for(DBObject o : this.relationsList){
			if(o.get("rel").toString().equalsIgnoreCase(relation)){
				this.verb2sList = (ArrayList<DBObject>) o.get("verb2s");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getFinalRelation(String verb2){
		for(DBObject o : this.verb2sList){
			if(o.get("verb2").toString().equalsIgnoreCase(verb2)){
				this.resultList = (ArrayList<DBObject>) o.get("results");
			}
		}
	}
	
	public ArrayList<String> getFinalResult(){
		ArrayList<String> finalResult = Lists.newArrayList();
		for(DBObject o : this.resultList){
			finalResult.add(o.get("result").toString());
		}
		return finalResult;
	}
	
}
