/**
 * 
 */
package edu.asu.nlu.knet.database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import module.graph.helper.Node;

import org.neo4j.driver.v1.*;
/**
 * @author Arpit Sharma
 * @date Jun 30, 2017
 *
 */
public class DbConnectionResource {
	//	public static File DB_PATH = new File("/home/arpit/workspace/KnowledgeNet/neo4j_db/test-db");
	private Session session = null;
	private Driver driver = null;
	private static DbConnectionResource dbCon = null;

	static {
		dbCon = new DbConnectionResource();
	}

	public static DbConnectionResource getInstance(){
		return dbCon;
	}

	private DbConnectionResource(){
		driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "bhardwaj" ) );
		session = driver.session();
	}

	public static void main(String[] args){
		DbConnectionResource dbcr = new DbConnectionResource();

		String query = "MATCH path = (jon:Person {firstname:'Jon', lastname:'Smith'})-[:PARENT_OF*]-(:Person) WITH collect(path) as paths CALL apoc.convert.toTree(paths) yield value RETURN value;";
		query = "MATCH path = (root1)-[rels1*]->(child1), (root2)-[rels2*]->(child2), "
				+ "(root1)-[r:may_cause]->(root2) WHERE root1.lemma = 'adopt' "
				+ "AND NONE (r1 in rels1 WHERE r1.name IN [\"may_cause\"]) "
				+ "AND root2.lemma = 'study' "
				+ "AND NONE (r2 in rels2 WHERE r2.name IN [\"may_cause\"]) "
				+ "WITH collect(path) as paths "
				+ "CALL apoc.convert.toTree(paths) yield value "
				+ "RETURN value;";
		//		session.run( "CREATE (a:Person {name: 'Arthur', title: 'King'})");
		//		StatementResult result = session.run( "MATCH (a:Person) WHERE a.name = 'Arthur' " +
		//                "RETURN a.name AS name, a.title AS title");
		//		StatementResult result = session.run( "MATCH (x)-[:may_cause]->(y) RETURN x,y");
		//		StatementResult result = session.run("MATCH (root)-[rels*]->(child) WHERE root.name = 'perform_1' AND NONE (r in rels WHERE r.name IN [\"may_cause\"]) RETURN *;");
		//		StatementResult result = session.run("MATCH (root1)-[rels1*]->(child1), (root2)-[rels2*]->(child2), (root1)-[r:may_cause]->(root2) WHERE root1.lemma = 'adopt' AND NONE (r1 in rels1 WHERE r1.name IN [\"may_cause\"]) AND root2.lemma = 'study' AND NONE (r2 in rels2 WHERE r2.name IN [\"may_cause\"])  RETURN *;");

		query = dbcr.generateReadQuery("adopt","study");
		ArrayList<HashSet<String>> res = dbcr.executeQuery(query);
		for(HashSet<String> rdfs : res){
			System.out.println("*****************************************");
			for(String rdf : rdfs){
				System.out.println(rdf);
			}
			System.out.println("*****************************************");
		}

		dbcr.disconnect();
	}

	public ArrayList<HashSet<String>> executeQuery(String query){
		ArrayList<HashSet<String>> result = new ArrayList<HashSet<String>>();
		StatementResult dbResult = session.run(query);
		while ( dbResult.hasNext() )
		{
			Record record = dbResult.next();
			Value jsonStr = record.get("value");
			Map<String, Object> map = jsonStr.asMap();
			Set<?> entrySet = map.entrySet();
			Iterator<?> iter = entrySet.iterator();
			Node root = new Node();
			root.setValue(map.get("name").toString());
			if(map.containsKey("lemma")){
				root.setLemma(map.get("lemma").toString());
			}
			while(iter.hasNext()){
				Map.Entry e = (Map.Entry) iter.next();
				String key = (String) e.getKey();
				if(e.getValue() instanceof List){
					updateGraph(root,key,e.getValue());
				}
			}
			HashSet<String> rdfs = root.toRDFString();
			result.add(rdfs);
		}

		return result;
	}

	private void updateGraph(Node n,String edge, Object entry){
		List<?> entryList = (List<?>)entry;
		ArrayList<String> edgeList = n.getEdgeList();
		for(int i=0; i<entryList.size();++i){
			edgeList.add(edge);
			Map<?, ?> map = (Map<?, ?>)entryList.get(i);
			Set<?> entrySet = map.entrySet();
			Iterator<?> iter = entrySet.iterator();
			Node child = null;
			while(iter.hasNext()){
				Map.Entry e1 = (Map.Entry) iter.next();
				String edgeName = (String) e1.getKey();
				if(e1.getValue() instanceof String){
					if(edgeName.equals("name")){
						if(child==null){
							child = new Node();
						}
						child.setValue(e1.getValue().toString());
					}else if(edgeName.equals("lemma")){
						if(child==null){
							child = new Node();
						}
						child.setLemma(e1.getValue().toString());
					}
				}else if(e1.getValue() instanceof List){
					if(child==null){
						child = new Node();
					}
					updateGraph(child,edgeName,e1.getValue());
				}
			}
			if(child!=null){
				ArrayList<Node> childList = n.getChildren();
				childList.add(child);
				n.setChildren(childList);
			}
		}
		n.setEdgeList(edgeList);
	}
	
	private String generateReadQuery(String precedent, String subsequent){
		String query = "MATCH path = (root1)-[rels1*]->(child1), (root2)-[rels2*]->(child2), "
				+ "(root1)-[r:may_cause]->(root2) WHERE root1.lemma = '"+precedent+"' "
				+ "AND NONE (r1 in rels1 WHERE r1.name IN [\"may_cause\"]) "
				+ "AND root2.lemma = '"+subsequent+"' "
				+ "AND NONE (r2 in rels2 WHERE r2.name IN [\"may_cause\"]) "
				+ "WITH collect(path) as paths "
				+ "CALL apoc.convert.toTree(paths) yield value "
				+ "RETURN value;";
		return query;
	}

	public String disconnect(){
		try{
			session.close();
			driver.close();
			return "Done";
		}catch(Exception e){
			return "Error in closing driver and session";
		}
	}
}
