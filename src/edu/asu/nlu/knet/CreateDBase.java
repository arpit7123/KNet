package edu.asu.nlu.knet;

/**
 * Author: Arpit Sharma
 * Author: Arindam Mitra
 * Date: July 24 2014
 */
import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;


public class CreateDBase {

	private MongoClient mongoClient = null;
	private DB db = null;
	private DBCollection documentTable = null;
	private DBCollection sentenceTable = null;
	private DBCollection slotValueTable = null;
	private DBCollection relationsTable = null;
	private DBCollection attributeKnowledgeTable = null;

	/**
	 * 
	 * @throws UnknownHostException
	 */
	public CreateDBase(String dbName ) throws UnknownHostException{
		this.mongoClient = new MongoClient();
		this.db = mongoClient.getDB(dbName);
		this.documentTable = this.db.getCollection("DocumentTable");
		this.sentenceTable = this.db.getCollection("SentenceTable");
		this.slotValueTable = this.db.getCollection("SlotValueTable");
		this.relationsTable = this.db.getCollection("RelationsTable");
		this.attributeKnowledgeTable = this.db.getCollection("AttributeKnowledgeTable");
	}
	
	public CreateDBase() throws UnknownHostException{
		this("knowledgeNetDB");
	}
	
//	/**
//	 * 
//	 * @param values
//	 * @throws UnknownHostException
//	 */
//	public CreateDBase(RelationsNode values) throws UnknownHostException{
//		this();
////		update(values);
//	}

	/**
	 * 
	 * @return
	 */
	public DBCollection getDocumentTable(){
		return this.documentTable;
	}
	
	/**
	 * 
	 * @return
	 */
	public DBCollection getSentenceTable(){
		return this.sentenceTable;
	}
	
	/**
	 * 
	 * @return
	 */
	public DBCollection getSlotValueTable(){
		return this.slotValueTable;
	}
	
	/**
	 * 
	 * @return
	 */
	public DBCollection getRelationsTable(){
		return this.relationsTable;
	}
	
	/**
	 * 
	 * @param documentId
	 * @param paraId
	 * @param sentenceId
	 */
	public void updateDocumentTable(String documentId, String paraId, String sentenceId){
		this.documentTable.save(new BasicDBObject("DOC_ID",documentId)
		.append("PARA_ID", paraId)
		.append("SENT_ID", sentenceId));		
	}

	/**
	 * 
	 * @param sentenceId
	 * @param sentence
	 */
	public void updateSentenceTable(String sentenceId, String sentence){
		this.sentenceTable.save(new BasicDBObject("SENT_ID", sentenceId)
		.append("SENT", sentence));
	}

	/**
	 * 
	 * @param sentenceId
	 * @param type
	 * @param polarity
	 * @param value
	 * @param slot
	 * @param slotValue
	 */
	public void updateSlotValueable(String sentenceId, String type, String polarity, String value, String baseValue, String slot, String slotValue){
		this.slotValueTable.save(new BasicDBObject("SENT_ID", sentenceId)
		.append("TYPE", type)
		.append("POLARITY", polarity)
		.append("VALUE", value)
		.append("BASE_VALUE", baseValue)
		.append("SLOT", slot)
		.append("SLOT_VALUE", slotValue));
	}
	
	/**
	 * 
	 * @param sentenceId
	 * @param relationType
	 * @param relationValue
	 */
	public void updateRelationsTable(String sentenceId, String relationType, String relationValue, String eventValue1, String eventValue2){
		this.relationsTable.save(new BasicDBObject("SENT_ID", sentenceId)
		.append("REL_TYPE", relationType)
		.append("REL_VALUE", relationValue)
		.append("EVENT_VALUE1", eventValue1)
		.append("EVENT_VALUE2", eventValue2));
	}
	
	/**
	 * 
	 * @param sentenceId
	 * @param eventType
	 * @param eventValue
	 * @param traitPolarity
	 * @param trait
	 */
	public void updateAttrKnowTable(String sentenceId, String eventType, String eventValue, String eventBase, String traitPolarity, String trait, String traitBase){
		this.attributeKnowledgeTable.save(new BasicDBObject("SENT_ID", sentenceId)
		.append("EVENT_TYPE", eventType)
		.append("EVENT_VALUE", eventValue)
		.append("EVENT_BASE", eventBase)
		.append("TRAIT_POL", traitPolarity)
		.append("TRAIT_VALUE", trait)
		.append("TRAIT_BASE", traitBase));
	}
	
	public static void main(String[] args){
		try {
			CreateDBase cdb = new CreateDBase("newDatabase");
			cdb.updateRelationsTable("1", "X", "XX", "asasa", "asqewe");
			System.out.println("hello");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}
	
}