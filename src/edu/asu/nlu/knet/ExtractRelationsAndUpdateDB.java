package edu.asu.nlu.knet;
/**
 * Author: Arpit Sharma
 * Author: Arindam Mitra
 * Date: July 24 2014
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import edu.asu.nlu.knet.extra.AnsSetToKnowledge;
import edu.asu.nlu.knet.knowledge.AttributeKnowledge;
import edu.asu.nlu.knet.knowledge.EventsKnowledge;
import edu.asu.nlu.knet.knowledge.Knowledge;
import edu.asu.nlu.knet.utilities.ClingoWrapper;
import edu.asu.nlu.knet.utilities.CommonFuntions;
import edu.asu.nlu.knet.utilities.Tools;
import module.graph.SentenceToGraph;
import module.graph.helper.GraphPassingNode;

public class ExtractRelationsAndUpdateDB {
	private String tempFileName = "./ASP/tempFile.txt";
	private String aspRulesFile = "./ASP/ASPRules.txt";
	private SentenceToGraph stg = null;
	private ClingoWrapper cw = null;
	private CreateDBase cdb = null;
	private AnsSetToKnowledge as2K  = null;
	private Pattern p = Pattern.compile("(.*)(_)([0-9]{1,5})");

	/**
	 * 
	 * @throws UnknownHostException
	 */
	public ExtractRelationsAndUpdateDB(String dbName) throws UnknownHostException{
//		this.stg = new SentenceToGraph();
//		this.cw = new ClingoWrapper();
//		this.cdb = new CreateDBase(dbName);
//		this.as2K = AnsSetToKnowledge.getInstance();
	}

	public static void main__MAIN(String args[]) throws Exception{
		ExtractRelationsAndUpdateDB er = new ExtractRelationsAndUpdateDB("dummy");
		String directory = "./corpus";
		er.extractUpdateQueries(directory);
	}

	public static void main_file(String args[]) throws Exception{
		ExtractRelationsAndUpdateDB er = new ExtractRelationsAndUpdateDB("UnitTest");
		String file = "./corpus/TEST.txt";

		int sentId=1;
		try(BufferedReader in = new BufferedReader(new FileReader(file))){
			String line = null;
			while((line=in.readLine())!=null){
				er.process("TEST", 1, sentId, line);
				in.readLine();
				sentId++;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception{
		//		String sent = "John loved Mia because Mia loved him.";
		ExtractRelationsAndUpdateDB er = new ExtractRelationsAndUpdateDB("KnowledgeNetDB");
		String directory = "/media/arpit/Windows7_OS/Users/ARPIT/Desktop/corpus/OANC-GrAF/data/written_1";
		directory = "/home/arpit/workspace/KnowledgeNet/test_corpus1/";
		directory = "/media/arpit/Windows7_OS/Users/ARPIT/Desktop/corpus/OANC-GrAF/data/written_1/fiction/";
		er.updateDataBase(directory);
		
	}

	/**
	 * 
	 * @param args
	 * @throws UnknownHostException
	 */
	public void updateDataBase(String directory) throws Exception{
		//		directory = "/host/stuff/KnowledgeNet/corpus";
		ArrayList<String> listOfFiles = Tools.listFilesForFolder(directory);
		int sentNumber = 1;

		for(String s : listOfFiles){
			String docId = s+"_3153";
			ArrayList<ParaSentNode> list = divideIntoParagraphs(directory + "/"+s, sentNumber);
			for(ParaSentNode n : list){
				process(docId, n.getParaId(), n.getSentId(), n.getSentence());
			}
			if(list.size()>0){
				sentNumber = list.get(list.size()-1).getSentId();
			}
		}		
	}


	public void process(String docId, int paraId, int senId, String sen) throws Exception{
		String newSentId = docId+"_"+senId;
		ArrayList<String> extractedRelation = null;

		if((extractedRelation = this.isUseful(sen))!=null){
			ArrayList<String> queries = this.constructQueries(extractedRelation);
			System.out.println("SENTENCE: "+sen);
			for(String ans: queries){
				if(!ans.trim().equalsIgnoreCase("")){
					Knowledge k = this.as2K.convert(ans);
					System.out.println("Events' knowledge extracted!!!");//:"+ k.toString());

					if(k.isEventKnowledge()){
						EventsKnowledge ek = (EventsKnowledge) k;
						//update db
						String polarity1 = ek.getPolarity1().toString();
						String verb1 = "X";
						Matcher m = p.matcher(ek.getVerb1());
						if(m.matches()){
							verb1 = ek.getVerb1().substring(0,ek.getVerb1().lastIndexOf("_"));
						}
						String verb1Base = ek.getVerb1Base();
						String slot1 = ek.getSlot1();
						String arg1 = ek.getArg1();

						String relation = ek.getRelation();

						String polarity2 = ek.getPolarity2().toString();
						String verb2 = "X";
						m = p.matcher(ek.getVerb2());
						if(m.matches()){
							verb2 = ek.getVerb2().substring(0,ek.getVerb2().lastIndexOf("_"));
						}
						String verb2Base = ek.getVerb2Base();
						String slot2 = ek.getSlot2();
						String arg2 = ek.getArg2();


						this.cdb.updateDocumentTable(docId, paraId+"", senId+"");
						this.cdb.updateRelationsTable(newSentId,"X",relation,verb1Base,verb2Base);
						this.cdb.updateSentenceTable(newSentId,sen);
						this.cdb.updateSlotValueable(newSentId,"X",polarity1,verb1,verb1Base,slot1,arg1);
						this.cdb.updateSlotValueable(newSentId,"X",polarity2,verb2,verb2Base,slot2,arg2);
					}
					
					if(k.isAttrKnowledge()){
						AttributeKnowledge ak = (AttributeKnowledge) k;
						//update db
						String eventPol = ak.getEventPolarity().toString();
						
						String eventVerb = "X";
						Matcher m = p.matcher(ak.getEventVerb());
						if(m.matches()){
							eventVerb = ak.getEventVerb().substring(0,ak.getEventVerb().lastIndexOf("_"));;
						}
						
						String eventVerbBase = ak.getEventVerbBase();
						
						String slot = ak.getSlot();
						
						String attrPol = ak.getAttrPolarity().toString();
						
						String attrValue = "X";
						m = p.matcher(ak.getAttrValue());
						if(m.matches()){
							attrValue = ak.getAttrValue().substring(0,ak.getAttrValue().lastIndexOf("_"));;
						}
						
						String attrBase = ak.getAttrBase();

						this.cdb.updateDocumentTable(docId, paraId+"", senId+"");
						this.cdb.updateSentenceTable(newSentId,sen);
						this.cdb.updateSlotValueable(newSentId,"X",eventPol,eventVerb,eventVerbBase,slot,"X");
						this.cdb.updateAttrKnowTable(newSentId, "X", eventVerb, eventVerbBase, attrPol, attrValue, attrBase);
					}
				}

			}

		}
	}

	/**
	 * 
	 * @param args
	 * @throws UnknownHostException
	 */
	public void extractUpdateQueries(String directory) throws Exception{
		try(BufferedWriter out = new BufferedWriter(new FileWriter("./Queries/queries"))){
			ArrayList<UpdateQueryData> updateQueryDateList = Lists.newArrayList();
			//		directory = "/host/stuff/KnowledgeNet/corpus";
			ArrayList<String> listOfFiles = Tools.listFilesForFolder(directory);
			int sentNumber = 1;
			for(String s : listOfFiles){
				ArrayList<ParaSentNode> list = divideIntoParagraphs(directory + "/"+s, sentNumber);
				for(ParaSentNode n : list){
					ArrayList<String> extractedRelation = null;
					//				System.out.println("SENTENCE is: "+ n.getSentence());

					if((extractedRelation = isUseful(n.getSentence()))!=null){
						//					for(String str : parsedGraph){
						//						System.out.println(str);
						//					}

						ArrayList<String> queries = constructQueries(extractedRelation);
						String docId = s.substring(0, s.lastIndexOf('.'));
						String paraId = ""+n.getParaId();
						String sentId = ""+n.getSentId();
						String sentence = n.getSentence();
						out.append(docId);
						out.newLine();
						out.append(paraId);
						out.newLine();
						out.append(sentId);
						out.newLine();
						out.append(sentence);
						out.newLine();
						//					System.out.println("DOC ID: "+ docId);
						//					System.out.println("PARA ID: "+ paraId);
						System.out.println("SENT ID: "+ sentId);
						System.out.println("SENTENCE: "+ sentence);
						for(String ss : queries){
							//						System.out.println(ss);
							out.append(ss);
							out.newLine();
						}
						UpdateQueryData uqd = new UpdateQueryData(docId,paraId,sentId,sentence,queries);
						updateQueryDateList.add(uqd);
					}
				}
				if(list.size()>0){
					sentNumber = list.get(list.size()-1).getSentId();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}

	}

	private ArrayList<String> constructQueries(ArrayList<String> extractedRelation){
		ArrayList<String> result = Lists.newArrayList();
		for(String s : extractedRelation){
			result.addAll(Lists.newArrayList(Splitter.on(" ").split(s)));
		}
		return result;
	}

	public ArrayList<String> isUseful(String sentence) throws Exception{
		GraphPassingNode gpn = this.stg.extractGraph(sentence, false, false);
		ArrayList<String> graph = gpn.getAspGraph();
		HashMap<String,String> posMap = gpn.getposMap();
		for(String s : posMap.keySet()){
			String temp = s.replaceAll("[^a-zA-Z-0-9]", "");
			String pos = posMap.get(s);
			if(pos!=null && temp!=null && !temp.startsWith("-")){						
				graph.add("has("+temp.replaceAll("-", "_")+",pos,"+pos.toLowerCase().replaceAll("\\$", "")+").");
			}
		}

		for(int i=0;i< graph.size();i++){
			graph.set(i, graph.get(i).toLowerCase().replaceAll(" ", "_").replaceAll(":", "").replaceAll("-", "_"));
		}
		ArrayList<String> listOfFiles = Lists.newArrayList();
		listOfFiles.add(this.tempFileName);
		listOfFiles.add(this.aspRulesFile);

		CommonFuntions.createTempFile(graph,this.tempFileName);
		ArrayList<String> result = this.cw.callASPusingFilesList(listOfFiles);
		if(result!=null && result.size()>0){
			return result;
		}
		return null;
	}

	/**
	 * 
	 * @param sentence
	 * @return
	 * @throws Exception 
	 */
	public ArrayList<RelationsNode> extractRelations(String sentence) throws Exception{
		ArrayList<RelationsNode> rnList = new ArrayList<RelationsNode>();
		GraphPassingNode gpn = this.stg.extractGraph(sentence, false, false);
		ArrayList<String> graph = gpn.getAspGraph();
		HashMap<String,String> posMap = gpn.getposMap();
		for(String s : posMap.keySet()){
			String temp = s.replaceAll("[^a-zA-Z-0-9]", "");
			if(!temp.startsWith("-")){
				graph.add("has("+temp.replaceAll("-", "_")+",pos,"+posMap.get(s).toLowerCase().replaceAll("\\$", "")+").");
			}
		}

		ArrayList<String> listOfFiles = Lists.newArrayList();
		listOfFiles.add(this.tempFileName);
		listOfFiles.add(this.aspRulesFile);

		CommonFuntions.createTempFile(graph,this.tempFileName);
		ArrayList<String> result = this.cw.callASPusingFilesList(listOfFiles);
		for(String s : result){

			String[] temp = s.split(" ");
			for(String s1 : temp){
				RelationsNode rn = new RelationsNode();
				Pattern p = Pattern.compile("(answerEvents\\()(.*)(,)(.*)(,)(.*)(,)(.*)(,)(.*)(,)(.*)(,)(.*)(\\))");
				Matcher m = p.matcher(s1);
				if(m.find()){
					String verb1Negative = "";
					if(m.group(2).equalsIgnoreCase("negative")){
						verb1Negative = "not";
					}
					rn.setVerb1Negative(verb1Negative);

					String verb1 = m.group(4).split("_")[0];
					rn.setVerb1(verb1);

					rn.setVerb1Relation(m.group(6));

					String relation = m.group(8);
					rn.setRelation(relation);

					String verb2Negative = "";
					if(m.group(10).equalsIgnoreCase("negative")){
						verb2Negative = "not";
					}
					rn.setVerb2Negative(verb2Negative);

					String verb2 = m.group(12).split("_")[0];
					rn.setVerb2(verb2);

					rn.setVerb1Relation(m.group(14));
				}
				rnList.add(rn);
			}
		}
		return rnList;
	}


	public ArrayList<ParaSentNode> divideIntoParagraphs(String documentName, int sentId){
		ArrayList<ParaSentNode> listOfParaNodes = Lists.newArrayList();
		try(BufferedReader in = new BufferedReader(new FileReader(documentName))){
			String line = null;
			int paraId = 1;
			boolean onlyOneSentFlag = true;
			StringBuilder  paragraph = new StringBuilder();
			while((line=in.readLine())!=null){
				if(line.trim().equalsIgnoreCase("")){
					ArrayList<String> listOfSentences = Tools.divideIntoSentences(paragraph.toString());
					for(String sentence : listOfSentences){
						ParaSentNode psn = new ParaSentNode(paraId, sentId, sentence);
						listOfParaNodes.add(psn);
						onlyOneSentFlag=false;
						sentId++;
					}
					paraId++;
					paragraph.setLength(0);
				}else{
					paragraph.append(line);
					paragraph.append(" ");
				}
			}
			if(onlyOneSentFlag){
				ArrayList<String> listOfSentences = Tools.divideIntoSentences(paragraph.toString());
				for(String sentence : listOfSentences){
					ParaSentNode psn = new ParaSentNode(paraId, sentId, sentence);
					listOfParaNodes.add(psn);
					onlyOneSentFlag=false;
					sentId++;
				}
				paraId++;
				paragraph.setLength(0);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return listOfParaNodes;
	}


}