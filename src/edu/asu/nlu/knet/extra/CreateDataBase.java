package edu.asu.nlu.knet.extra;

/**
 * Author: Arpit Sharma
 * Date: July 24 2014
 */
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.bson.types.ObjectId;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import edu.asu.nlu.knet.DataBaseNode;
import edu.asu.nlu.knet.RelationsNode;


@SuppressWarnings("unchecked")
public class CreateDataBase {

	private MongoClient mongoClient = null;
	private DB db = null;
	private DBCollection dbc = null;

	/**
	 * 
	 * @throws UnknownHostException
	 */
	public CreateDataBase() throws UnknownHostException{
		this.mongoClient = new MongoClient();
		this.db = mongoClient.getDB("mydb");
		this.dbc = this.db.getCollection("KnowledgeNet");
		this.dbc.drop();
	}
	
	/**
	 * 
	 * @param values
	 * @throws UnknownHostException
	 */
	public CreateDataBase(RelationsNode values) throws UnknownHostException{
		this();
		update(values);
	}

	/**
	 * 
	 * @return
	 */
	public DBCollection getDBCollection(){
		return this.dbc;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		CreateDataBase t = new CreateDataBase();

		String neg1 = "not";
		String verb1 = "refused";
		String rel = "caused_by";
		String neg2 = "not";
		String verb2 = "feared";
		String result = "agentEQagent";

		//		System.out.println(t.db.getName());

		//KnowledgeNet
		t.dbc.drop();
		BasicDBObject new_entry = new BasicDBObject("verb1",neg1+"_"+verb1)
		.append("rels",Lists.newArrayList(new BasicDBObject("rel",rel)
		.append("verb2s", Lists.newArrayList(new BasicDBObject("verb2",neg2+"_"+verb2)
		.append("results", Lists.newArrayList(new BasicDBObject("result",result)))))));


		t.dbc.insert(new_entry);

		RelationsNode rn = new RelationsNode();
		rn.setVerb1Negative("not");
		rn.setVerb1("refused");
		rn.setVerb1Relation("agent");
		rn.setRelation("caused_by");
		rn.setVerb2Negative("");
		rn.setVerb2("feared");
		rn.setVerb2Relation("recipient");
		
		t.update(rn);

		DBCursor dbcur = t.dbc.find();
		while(dbcur.hasNext()){
			System.out.println(dbcur.next());
		}
		
		System.exit(0);
	}

	/**
	 * 
	 * @param relations
	 */
	public void update(RelationsNode relations){
		String verb1 = relations.getVerb1();
		if(!relations.getVerb1Negative().equalsIgnoreCase("")){
			verb1 = relations.getVerb1Negative()+"_"+verb1;
		}
		String rel = relations.getRelation();
		String verb2 = relations.getVerb2();
		if(!relations.getVerb2Negative().equalsIgnoreCase("")){
			verb2 = relations.getVerb2Negative()+"_"+verb2;
		}
		String result = relations.getVerb1Relation()+"EQ"+relations.getVerb1Relation();

//		System.out.println(verb1);
//		System.out.println(rel);
//		System.out.println(verb2);
//		System.out.println(result);
		
		BasicDBObject entry = new BasicDBObject("verb1",verb1)
		.append("rels",Lists.newArrayList(new BasicDBObject("rel",rel)
		.append("verb2s", Lists.newArrayList(new BasicDBObject("verb2",verb2)
		.append("results", Lists.newArrayList(new BasicDBObject("result",result)))))));

		DBObject obj = this.dbc.findOne(new BasicDBObject("verb1",verb1));
		if(obj==null){
			this.dbc.insert(entry);
//			System.out.println("in part 0");
		}else{
			DataBaseNode temp1 = listContainsFieldValue(obj.get("rels"),"rel",rel);
//			System.out.println("TEMP1 "+temp1.getDbObj());
			if(temp1==null){
				DBObject relObj = new BasicDBObject("rels", new BasicDBObject("rel",rel)
				.append("verb2s", Lists.newArrayList(new BasicDBObject("verb2",verb2)
				.append("results", Lists.newArrayList(new BasicDBObject("result",result))))));
				DBObject updateQuery = new BasicDBObject("$push", relObj);
				this.dbc.update(new BasicDBObject("verb1",verb1), updateQuery);
//				System.out.println("in part 1");
			}else{
				DataBaseNode temp2 = listContainsFieldValue((temp1.getDbObj().get("verb2s")),"verb2",verb2);
//				System.out.println("TEMP2 "+temp2);
				if(temp1!=null && temp2==null){
//					System.out.println("TEMP1 "+temp1.getDbObj());
//					System.out.println("TEMP2 "+temp2);

					ArrayList<BasicDBObject> tmpList = (ArrayList<BasicDBObject>) temp1.getDbObj().get("verb2s");
					BasicDBObject listItem = new BasicDBObject("verb2",verb2)
					.append("results", Lists.newArrayList(new BasicDBObject("result",result)));
					tmpList.add(listItem);

					BasicDBObject relObj = new BasicDBObject("rel",rel)
					.append("verb2s", tmpList);

					DataBaseNode dbn = listContainsFieldValue(obj.get("rels"),"rel",rel);
					ArrayList<BasicDBObject> relList = (ArrayList<BasicDBObject>)obj.get("rels");
					relList.set(dbn.getIndex(), relObj);

					BasicDBObject verb1Obj = new BasicDBObject("verb1",verb1).append("rels",relList);
					verb1Obj.append("_id", new ObjectId(obj.get("_id").toString()));

					this.dbc.update(
							new BasicDBObject("_id",obj.get("_id")),
							new BasicDBObject("$set",verb1Obj),true,false);

//					System.out.println("in part 2");
				}else{
					DataBaseNode temp3 = listContainsFieldValue((temp2.getDbObj().get("results")),"result",result);
//					System.out.println("TEMP3 "+temp3);
					if(temp1!=null && temp2!=null && temp3==null){
//						System.out.println("TEMP1 in 3 "+temp1.getDbObj() + " index " + temp1.getIndex());
//						System.out.println("TEMP2 in 3 "+temp2.getDbObj() + " index " + temp2.getIndex());
						//			System.out.println("TEMP3 in 3 "+temp1.getDbObj() + " index " + temp3.getIndex());

						ArrayList<BasicDBObject> tmpList = (ArrayList<BasicDBObject>)temp2.getDbObj().get("results");
						BasicDBObject listItem = new BasicDBObject("result",result);
						tmpList.add(listItem);

						BasicDBObject verb2Obj = new BasicDBObject("verb2",verb2)
						.append("results", tmpList);

						ArrayList<BasicDBObject> verb2List = (ArrayList<BasicDBObject>)temp1.getDbObj().get("verb2s");
						verb2List.set(temp1.getIndex(), verb2Obj);

						BasicDBObject relObj = new BasicDBObject("rel",rel)
						.append("verb2s", verb2List);

						DataBaseNode dbn = listContainsFieldValue(obj.get("rels"),"rel",rel);
						ArrayList<BasicDBObject> relList = ((ArrayList<BasicDBObject>)obj.get("rels"));
						relList.set(dbn.getIndex(), relObj);

						BasicDBObject verb1Obj = new BasicDBObject("verb1",verb1).append("rels",relList);
						verb1Obj.append("_id", new ObjectId(obj.get("_id").toString()));

						this.dbc.update(
								new BasicDBObject("_id",obj.get("_id")),
								new BasicDBObject("$set",verb1Obj),true,false);

//						System.out.println("in part 3");
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param list
	 * @param field
	 * @param relation
	 * @return DataBaseNode
	 */
	private DataBaseNode listContainsFieldValue(Object list,String field,String relation){
		DataBaseNode result = null;
		ArrayList<BasicDBObject> l = (ArrayList<BasicDBObject>)list;
		for(int i = 0; i<l.size();++i){
			if(l.get(i).get(field).toString().equalsIgnoreCase(relation)){
				result = new DataBaseNode(l.get(i),i);
			}
		}
		return result;
	}

}
