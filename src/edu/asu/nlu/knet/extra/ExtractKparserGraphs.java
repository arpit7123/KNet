package edu.asu.nlu.knet.extra;
///**
// * 
// */
//package knowledgeNet.extra;
//
//import java.net.UnknownHostException;
//
//import module.graph.ParserHelper;
//
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.DBCursor;
//import com.mongodb.DBObject;
//import com.mongodb.MongoClient;
//
///**
// * @author arpit
// *
// */
//public class ExtractKparserGraphs {
//	
//	private static final String KNOWLEDGE_BASE_DB_NAME = "KnowledgeBase";
//	private MongoClient mongoClient = null;
//	private DB db = null;
//	private DBCollection sentenceTable = null;
//	private ParserHelper phelper = null;
//	
//	public ExtractKparserGraphs() throws UnknownHostException{
//		
//		
//		this.mongoClient = new MongoClient();
//		this.db = mongoClient.getDB(KNOWLEDGE_BASE_DB_NAME);
//		this.sentenceTable = this.db.getCollection("SentenceTable");
//	}
//
//	public static void main(String[] args) throws UnknownHostException{
//		ExtractKparserGraphs rfdb = new ExtractKparserGraphs();
//		DBCursor dbcur = rfdb.sentenceTable.find();
//		while(dbcur.hasNext()){
//			DBObject s = dbcur.next();
//			System.out.println(s.get("SENT_ID"));
//			System.out.println(s.get("SENT"));
//			
////			System.out.println(dbcur.next());
//		}
//	}
//}