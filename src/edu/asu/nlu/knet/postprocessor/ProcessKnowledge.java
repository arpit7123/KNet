package edu.asu.nlu.knet.postprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import module.graph.MakeGraph;
import module.graph.helper.Node;
import module.graph.helper.NodePassedToViewer;

/**
 * @author Arpit Sharma
 *
 */
public class ProcessKnowledge {

	private MakeGraph mk = null;
	private HashMap<String,String> mapOfConns = null;

	public ProcessKnowledge(){
		mk = new MakeGraph();

		String connsFilePath = "/home/arpit/workspace/KNet/conns_polarity.txt";
		mapOfConns = populateMapOfConns(connsFilePath);
	}

	public static void main(String[] args) throws Exception{
		String outputNeo4jFile = "/home/arpit/workspace/KNet/commonsense_knowledge/Processed_Final/test";//know_may2317_neo4j_create_queries";
		String outputStringFile = "/home/arpit/workspace/KNet/commonsense_knowledge/Processed_Final/test_out";//know_may2317_readable_knowledge";
		ProcessKnowledge pk = new ProcessKnowledge();
		String inputDir = "/home/arpit/workspace/KNet/commonsense_knowledge/knowledge_may23/";
		//				"/home/arpit/workspace/KnowledgeNet/commonsense_knowledge/knowledge_because2.0/";
		//		dir = "/home/arpit/workspace/KnowledgeNet/commonsense_knowledge/type_specific_knowledge/";
		inputDir = "/home/arpit/workspace/KNet/commonsense_knowledge/test/";

		pk.readAspKnWriteNeo4jPlusString(inputDir, outputNeo4jFile, outputStringFile);
		//		String sent = "I often feel as if I only get half the story sometimes so I typically do a massive amount of reporting that is most often much more than I will ever need";
		//		sent = "he could not vote because he had been declared mentally incapacitated";
		//		String aspKnowledgeStr = "answerEventsType1(positive,get_8,get,agent,i_6,so,positive,do_16,do,agent,i_14) answerEventsType1(positive,feel_3,feel,agent,i_1,so,positive,do_16,do,agent,i_14)";
		//		aspKnowledgeStr = "answerEventsType1(negative,vote_4,vote,agent,he_1,because,positive,declared_9,declare,recipient,he_6)";
		//		KnowledgeNode kn = pk.getKnowledgeFromSent(sent, aspKnowledgeStr);
		//		for(String qry : pk.getAllKnowledge(kn)){
		//			System.out.println(qry);
		//		}
	}

	private void readAspKnWriteNeo4jPlusString(String inputDir, String outputNeo4jFile, String outputStringFile) throws Exception{
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outputNeo4jFile));
				BufferedWriter sbw = new BufferedWriter(new FileWriter(outputStringFile))){

			File[] files = new File(inputDir).listFiles(File::isFile);

			for(File file : files){
				if(file.length()>0){
					try(BufferedReader br = new BufferedReader(new FileReader(file))){
						String line = null;
						while((line=br.readLine())!=null){
							KnowledgeNode kn = getKnowledgeFromSent(line,br.readLine());
							ArrayList<String> ks = getAllKnowledge(kn);
							if(ks.size()>0){
								bw.append(line);
								bw.newLine();
								sbw.append(line);
								sbw.newLine();
								sbw.append(kn.toString());
								for(String qry : ks){
									bw.append(qry);
									bw.newLine();
								}
								bw.newLine();
								sbw.newLine();
							}
							br.readLine();
						}
						br.close();
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}
			bw.close();
			sbw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void readAspKnowAndWriteNeo4jQry(String inputDir, String outputFile) throws Exception{
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))){

			File[] files = new File(inputDir).listFiles(File::isFile);

			for(File file : files){
				if(file.length()>0){
					try(BufferedReader br = new BufferedReader(new FileReader(file))){
						String line = null;
						while((line=br.readLine())!=null){
							KnowledgeNode kn = getKnowledgeFromSent(line,br.readLine());
							ArrayList<String> ks = getAllKnowledge(kn);
							if(ks.size()>0){
								bw.append(line);
								bw.newLine();
								for(String qry : ks){
									bw.append(qry);
									bw.newLine();
								}
								bw.newLine();
							}
							br.readLine();
						}
						br.close();
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private ArrayList<String> getAllKnowledge(KnowledgeNode kn){
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<KnowledgeType1> ins1 = kn.getTyp1KnowInsts();
		if(ins1!=null){
			for(KnowledgeType1 i : ins1){
				result.add(i.getNeo4JCreateQuery());
			}
		}

		ArrayList<KnowledgeType2> ins2 = kn.getTyp2KnowInsts();
		if(ins2!=null){
			for(KnowledgeType2 i : ins2){
				result.add(i.getNeo4JCreateQuery());
			}
		}

		ArrayList<KnowledgeType3> ins3 = kn.getTyp3KnowInsts();
		if(ins3!=null){
			for(KnowledgeType3 i : ins3){
				result.add(i.getNeo4JCreateQuery());
			}
		}

		ArrayList<KnowledgeType4> ins4 = kn.getTyp4KnowInsts();
		if(ins4!=null){
			for(KnowledgeType4 i : ins4){
				result.add(i.getNeo4JCreateQuery());
			}
		}

		ArrayList<KnowledgeType5> ins5 = kn.getTyp5KnowInsts();
		if(ins5!=null){
			for(KnowledgeType5 i : ins5){
				result.add(i.getNeo4JCreateQuery());
			}
		}

		return result;
	}

	private KnowledgeNode getKnowledgeFromSent(String sent, String aspKnowledgeStr) throws Exception{
		ArrayList<NodePassedToViewer> listOfGraphs = mk.createGraphUsingSentence(sent, false, true, false);
		HashMap<String,Node> mapOfPrunedRoots = new HashMap<String,Node>();
		for(NodePassedToViewer npv : listOfGraphs){
			updatePrunedActionsMap(npv.getGraphNode(),mapOfPrunedRoots);
		}
		String[] knowledges = aspKnowledgeStr.split(" ");
		KnowledgeNode kNode = new KnowledgeNode();
		for(String k : knowledges){
			getProcessedKnowledge(k,kNode,mapOfPrunedRoots);
		}
		return kNode;
	}

	private void updatePrunedActionsMap(Node n, HashMap<String,Node> mapOfPrunedRoots){
		if(n.getPOS()!=null){
			if(n.getPOS().startsWith("V")){
				ArrayList<Node> children = n.getChildren();
				ArrayList<String> edges = n.getEdgeList();

				ArrayList<Node> newChildren = new  ArrayList<Node>();
				ArrayList<String> newEdges = new  ArrayList<String>();

				int i=0;
				for(Node child : children){
					Node nn = getNewChild(child);
					if(nn!=null){
						newChildren.add(nn);
						newEdges.add(edges.get(i));
					}
					i++;
				}
				n.setChildren(newChildren);
				n.setEdgeList(newEdges);

				mapOfPrunedRoots.put(n.getValue(), n);
			}

			ArrayList<Node> children = n.getChildren();
			for(Node child : children){
				updatePrunedActionsMap(child, mapOfPrunedRoots);
			}
		}
	}

	private Node getNewChild(Node n){
		ArrayList<Node> children = n.getChildren();
		ArrayList<String> edges = n.getEdgeList();

		ArrayList<Node> newChildren = new ArrayList<Node>();
		ArrayList<String> newEdges = new ArrayList<String>();

		int i = 0;
		for(Node child : children){
			Node nn = getNewChild(child);
			if(nn!=null){
				newChildren.add(nn);
				newEdges.add(edges.get(i));
			}
			i++;
		}
		if(n.getPOS()!=null){
			if(!n.getPOS().equals("NNP") 
					&& !n.getPOS().equals("NNPS")
					&& !n.getPOS().equals("PRP")
					&& !n.getPOS().equals("PRP$")
					&& !n.isAClass()
					&& !n.isASemanticRole()){
				n.setChildren(newChildren);
				n.setEdgeList(newEdges);
				return n;
			}
		}
		return null;
	}

	public void getProcessedKnowledge(String k, KnowledgeNode kNode, HashMap<String,Node> mapOfPrunedRoots){
		String startOfString = k.substring(0, k.indexOf("("));
		String[] tmp = null;
		
		switch (startOfString){
		case "answerEventsType1":
			k = k.substring(18);
			tmp = k.split(",");
			String evnt1 = null;
			String evnt1Lemma = null;
			String evnt1Rel = null;
			boolean evnt1Polarity = false;
			ArrayList<Node> evnt1Children = null;
			ArrayList<String> evnt1ChilEdgs = null;

			String evnt2 = null;
			String evnt2Lemma = null;
			String evnt2Rel = null;
			boolean evnt2Polarity = false;
			ArrayList<Node> evnt2Children = null;
			ArrayList<String> evnt2ChilEdgs = null;

			String type = "one";

			if(tmp[0].equals("positive") && tmp[6].equals("positive")){
				String conn_polarity = getConnPolarity(tmp[5]);
				if(conn_polarity != null){
					if(conn_polarity.equals("reverse")){
						evnt1 = tmp[7].substring(0, tmp[7].lastIndexOf("_")) + "-" + tmp[7].substring(tmp[7].lastIndexOf("_")+1,tmp[7].length());
						evnt1Polarity = true;
						evnt1Rel = tmp[9];
						evnt1Lemma = tmp[8];

						evnt2 = tmp[1].substring(0, tmp[1].lastIndexOf("_")) + "-" + tmp[1].substring(tmp[1].lastIndexOf("_")+1,tmp[1].length());
						evnt2Polarity = true;
						evnt2Rel = tmp[3];
						evnt2Lemma = tmp[2];
					}else{
						evnt1 = tmp[1].substring(0, tmp[1].lastIndexOf("_")) + "-" + tmp[1].substring(tmp[1].lastIndexOf("_")+1,tmp[1].length());
						evnt1Polarity = true;
						evnt1Rel = tmp[3];
						evnt1Lemma = tmp[2];

						evnt2 = tmp[7].substring(0, tmp[7].lastIndexOf("_")) + "-" + tmp[7].substring(tmp[7].lastIndexOf("_")+1,tmp[7].length());
						evnt2Polarity = true;
						evnt2Rel = tmp[9];
						evnt2Lemma = tmp[8];
					}
				}
			}else if(tmp[0].equals("positive") && !tmp[6].equals("positive")){
				if(getConnPolarity(tmp[5]).equals("reverse")){
					evnt1 = tmp[7].substring(0, tmp[7].lastIndexOf("_")) + "-" + tmp[7].substring(tmp[7].lastIndexOf("_")+1,tmp[7].length());
					evnt1Lemma = tmp[8];
					evnt1Rel = tmp[9];

					evnt2Polarity = true;
					evnt2 = tmp[1].substring(0, tmp[1].lastIndexOf("_")) + "-" + tmp[1].substring(tmp[1].lastIndexOf("_")+1,tmp[1].length());
					evnt2Lemma = tmp[2];
					evnt2Rel = tmp[3];

				}else{
					type = "five";
					evnt1Polarity = true;
					evnt1 = tmp[1].substring(0, tmp[1].lastIndexOf("_")) + "-" + tmp[1].substring(tmp[1].lastIndexOf("_")+1,tmp[1].length());
					evnt1Lemma = tmp[2];
					evnt1Rel = tmp[3];

					evnt2Polarity = true;
					evnt2 = tmp[7].substring(0, tmp[7].lastIndexOf("_")) + "-" + tmp[7].substring(tmp[7].lastIndexOf("_")+1,tmp[7].length());
					evnt2Lemma = tmp[8];
					evnt2Rel = tmp[9];
				}
			}else if(!tmp[0].equals("positive") && tmp[6].equals("positive")){
				if(getConnPolarity(tmp[5]).equals("reverse")){
					type = "five";
					evnt1Polarity = true;
					evnt1 = tmp[7].substring(0, tmp[7].lastIndexOf("_")) + "-" + tmp[7].substring(tmp[7].lastIndexOf("_")+1,tmp[7].length());
					evnt1Lemma = tmp[8];
					evnt1Rel = tmp[9];

					evnt2Polarity = true;
					evnt2 = tmp[1].substring(0, tmp[1].lastIndexOf("_")) + "-" + tmp[1].substring(tmp[1].lastIndexOf("_")+1,tmp[1].length());
					evnt2Lemma = tmp[2];
					evnt2Rel = tmp[3];
				}else{
					evnt1 = tmp[1].substring(0, tmp[1].lastIndexOf("_")) + "-" + tmp[1].substring(tmp[1].lastIndexOf("_")+1,tmp[1].length());
					evnt1Lemma = tmp[2];
					evnt1Rel = tmp[3];

					evnt2Polarity = true;
					evnt2 = tmp[7].substring(0, tmp[7].lastIndexOf("_")) + "-" + tmp[7].substring(tmp[7].lastIndexOf("_")+1,tmp[7].length());
					evnt2Lemma = tmp[8];
					evnt2Rel = tmp[9];
				}		
			}else {
				type = "five";
				if(getConnPolarity(tmp[5]).equals("reverse")){
					evnt1 = tmp[7].substring(0, tmp[7].lastIndexOf("_")) + "-" + tmp[7].substring(tmp[7].lastIndexOf("_")+1,tmp[7].length());
					evnt1Lemma = tmp[8];
					evnt1Rel = tmp[9];

					evnt2Polarity = true;
					evnt2 = tmp[1].substring(0, tmp[1].lastIndexOf("_")) + "-" + tmp[1].substring(tmp[1].lastIndexOf("_")+1,tmp[1].length());
					evnt2Lemma = tmp[2];
					evnt2Rel = tmp[3];
				}else{
					evnt1 = tmp[1].substring(0, tmp[1].lastIndexOf("_")) + "-" + tmp[1].substring(tmp[1].lastIndexOf("_")+1,tmp[1].length());
					evnt1Lemma = tmp[2];
					evnt1Rel = tmp[3];

					evnt2Polarity = true;
					evnt2 = tmp[7].substring(0, tmp[7].lastIndexOf("_")) + "-" + tmp[7].substring(tmp[7].lastIndexOf("_")+1,tmp[7].length());
					evnt2Lemma = tmp[8];
					evnt2Rel = tmp[9];
				}					
			}

			if(mapOfPrunedRoots.containsKey(evnt1)){
				evnt1Children = mapOfPrunedRoots.get(evnt1).getChildren();
				evnt1ChilEdgs = mapOfPrunedRoots.get(evnt1).getEdgeList();
			}

			if(mapOfPrunedRoots.containsKey(evnt2)){
				evnt2Children = mapOfPrunedRoots.get(evnt2).getChildren();
				evnt2ChilEdgs = mapOfPrunedRoots.get(evnt2).getEdgeList();
			}

			if(type.equals("one")){
				KnowledgeType1 type1Instance = new KnowledgeType1();
				type1Instance.setEvent1(evnt1);
				type1Instance.setEvent1polarity(evnt1Polarity);
				type1Instance.setEvent1Lemma(evnt1Lemma);
				type1Instance.setEvent1Rel(evnt1Rel);
				type1Instance.setEvent1Children(evnt1Children);
				type1Instance.setEvnt1ChildEdges(evnt1ChilEdgs);
				type1Instance.setEvent2(evnt2);
				type1Instance.setEvent2polarity(evnt2Polarity);
				type1Instance.setEvent2Lemma(evnt2Lemma);
				type1Instance.setEvent2Rel(evnt2Rel);
				type1Instance.setEvent2Children(evnt2Children);
				type1Instance.setEvnt2ChildEdges(evnt2ChilEdgs);
				ArrayList<KnowledgeType1> type1Instances = kNode.getTyp1KnowInsts();
				if(type1Instances==null){
					type1Instances = new ArrayList<KnowledgeType1>();
					kNode.setTyp1KnowInsts(type1Instances);
				}
				type1Instances.add(type1Instance);
			}else if (type.equals("five")){
				KnowledgeType5 type5Instance = new KnowledgeType5();
				type5Instance.setEvent1(evnt1);
				type5Instance.setEvent1polarity(evnt1Polarity);
				type5Instance.setEvent1Lemma(evnt1Lemma);
				type5Instance.setEvent1Rel(evnt1Rel);
				type5Instance.setEvent1Children(evnt1Children);
				type5Instance.setEvnt1ChildEdges(evnt1ChilEdgs);
				type5Instance.setEvent2(evnt2);
				type5Instance.setEvent2polarity(evnt2Polarity);
				type5Instance.setEvent2Lemma(evnt2Lemma);
				type5Instance.setEvent2Rel(evnt2Rel);
				type5Instance.setEvent2Children(evnt2Children);
				type5Instance.setEvnt2ChildEdges(evnt2ChilEdgs);
				ArrayList<KnowledgeType5> type5Instances = kNode.getTyp5KnowInsts();
				if(type5Instances==null){
					type5Instances = new ArrayList<KnowledgeType5>();
					kNode.setTyp5KnowInsts(type5Instances);
				}
				type5Instances.add(type5Instance);
			}
			break;
			
		case "answerType2":
			k = k.substring(12);
			k = k.substring(0, k.length()-1);
			tmp = k.split(",");
			
			ArrayList<KnowledgeType2> type2Instances = kNode.getTyp2KnowInsts();
			KnowledgeType2 type2Instance = new KnowledgeType2();
			
			if(tmp.length==5){
				if(tmp[0].equals("positive")){
					type2Instance.setTraitpolarity(true);
				}else{
					type2Instance.setTraitpolarity(false);
				}
				String event = tmp[2].substring(0, tmp[2].lastIndexOf("_")) + "-" + tmp[2].substring(tmp[2].lastIndexOf("_")+1,tmp[2].length());
				type2Instance.setEvent(event);
				type2Instance.setEventLemma(tmp[3]);
				type2Instance.setEventRel(tmp[4]);
				type2Instance.setTrait(tmp[1]);
				if(mapOfPrunedRoots.containsKey(event)){
					type2Instance.setEventChildren(mapOfPrunedRoots.get(event).getChildren());
					type2Instance.setEvntChildEdges(mapOfPrunedRoots.get(event).getEdgeList());
				}
			}else{
				System.err.println("Knowledge Type 2: " + k);
			}

			if(type2Instances==null){
				type2Instances = new ArrayList<KnowledgeType2>();
				kNode.setTyp2KnowInsts(type2Instances);
			}
			type2Instances.add(type2Instance);
			break;

		case "answerType3":
			k = k.substring(12);
			k = k.substring(0, k.length()-1);
			tmp = k.split(",");
			
			ArrayList<KnowledgeType3> type3Instances = kNode.getTyp3KnowInsts();
			KnowledgeType3 type3Instance = new KnowledgeType3();
			
			if(tmp.length==5){
				if(tmp[0].equals("positive")){
					type3Instance.setTraitpolarity(true);
				}else{
					type3Instance.setTraitpolarity(false);
				}
				String event = tmp[2].substring(0, tmp[2].lastIndexOf("_")) + "-" + tmp[2].substring(tmp[2].lastIndexOf("_")+1,tmp[2].length());
				type3Instance.setEvent(event);
				type3Instance.setEventLemma(tmp[3]);
				type3Instance.setEventRel(tmp[4]);
				type3Instance.setTrait(tmp[1]);
				if(mapOfPrunedRoots.containsKey(event)){
					type3Instance.setEventChildren(mapOfPrunedRoots.get(event).getChildren());
					type3Instance.setEvntChildEdges(mapOfPrunedRoots.get(event).getEdgeList());
				}
			}else {
				System.err.println("Knowledge Type 3: " + k);
			}
			
			if(type3Instances==null){
				type3Instances = new ArrayList<KnowledgeType3>();
				kNode.setTyp3KnowInsts(type3Instances);
			}
			type3Instances.add(type3Instance);
			break;
			
		case "answerType4":
			k = k.substring(12);
			k = k.substring(0, k.length()-1);
			tmp = k.split(",");
			
			ArrayList<KnowledgeType4> type4Instances = kNode.getTyp4KnowInsts();
			KnowledgeType4 type4Instance = new KnowledgeType4();
			
			if(tmp.length==5){
				if(tmp[0].equals("positive")){
					type4Instance.setTraitpolarity(true);
				}else{
					type4Instance.setTraitpolarity(false);
				}
				String event = tmp[2].substring(0, tmp[2].lastIndexOf("_")) + "-" + tmp[2].substring(tmp[2].lastIndexOf("_")+1,tmp[2].length());
				type4Instance.setEvent(event);
				type4Instance.setEventLemma(tmp[3]);
				type4Instance.setEventRel(tmp[4]);
				type4Instance.setTrait(tmp[1]);
				if(mapOfPrunedRoots.containsKey(event)){
					type4Instance.setEventChildren(mapOfPrunedRoots.get(event).getChildren());
					type4Instance.setEvntChildEdges(mapOfPrunedRoots.get(event).getEdgeList());
				}
			}else{
				System.err.println("Knowledge Type 4: " + k);
			}
			
			if(type4Instances==null){
				type4Instances = new ArrayList<KnowledgeType4>();
				kNode.setTyp4KnowInsts(type4Instances);
			}
			type4Instances.add(type4Instance);
			break;
			
		case "answerEventsType5":
			//			k = k.substring(18);
			//			k = k.substring(0, k.length()-1);
			break;
		}
	}

	public String getConnPolarity(String conn){
		conn = conn.toLowerCase();
		if(mapOfConns.containsKey(conn)){
			return mapOfConns.get(conn);
		}
		return "forward";
	}

	private HashMap<String,String> populateMapOfConns(String connsFileName){
		HashMap<String,String> result = new HashMap<String, String>();
		try(BufferedReader br = new BufferedReader(new FileReader(connsFileName))){
			String line = null;
			while((line=br.readLine())!=null){
				String[] tmp = line.trim().split("\t");
				if(tmp.length==2){
					result.put(tmp[0], tmp[1]);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}

}
