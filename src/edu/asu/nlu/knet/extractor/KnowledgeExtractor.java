package edu.asu.nlu.knet.extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sun.webkit.Utilities;

import edu.asu.nlu.knet.utilities.ClingoWrapper;
//import module.graph.SentenceToGraph;
import module.graph.helper.GraphPassingNode;
import module.graph.helper.JAWSutility;
import module.graph.helper.Node;
import module.graph.resources.PrepositionSenseResolver;

/**
 * @author Arpit Sharma
 *
 */
public class KnowledgeExtractor {

	private static String tempFileName = "/home/arpit/workspace/KNet/ASP/tempFile.txt";
	private static String aspRulesFileName = "/home/arpit/workspace/KNet/ASP/rulesFile.txt";

//	private static final int words_chars_limit = 40;

	//	private SentenceToGraph stg = null;
	private LocalKparser lk = null;
	private EventSubgraphsExtractor eventExtractor = null;
	private ASPDataPreparer aspPreparer = null;
	private ClingoWrapper clingoWrapper = null;
	private TextPreprocessor preprocessor = null;


	public KnowledgeExtractor(){
		lk = new LocalKparser();
		eventExtractor = EventSubgraphsExtractor.getInstance();
		aspPreparer = ASPDataPreparer.getInstance();
		clingoWrapper = ClingoWrapper.getInstance();
		preprocessor = TextPreprocessor.getInstance();
		//		stg = new SentenceToGraph();
	}

	public static void main_(String[] args){
		KnowledgeExtractor mc = new KnowledgeExtractor();
		String word = "by";
		JAWSutility j = new JAWSutility();
		System.out.println(j.getBaseForm(word, "v"));
		//		String inputText = "John was bullying Tom so Tim rescued Tom.";
		//		String inputText = "John could not lift Tim although Tim was very heavy.";
//		String dir = "/home/ASUAD/asharm73/processed_sents/gpu_machine2";
//		File[] directories = new File(dir).listFiles(File::isDirectory);
//		try(BufferedWriter bw = new BufferedWriter(new FileWriter("/home/ASUAD/asharm73/knowledge.txt"))){
//			for(File f : directories){
//				File[] files = f.listFiles(File::isFile);
//				for(File file : files){
//					try(BufferedReader br = new BufferedReader(new FileReader(file))){
//						String line = null;
//						while((line=br.readLine())!=null){
//							String[] words = line.split(" ");
//							boolean wrongWords = false;
//							for(String word : words){
//								if(word.length() > words_chars_limit){
//									wrongWords = true;
//								}
//							}
//							ArrayList<String> lines = new ArrayList<String>();
//							int finIndx = words.length/sent_words_limit;
//							if(words.length%sent_words_limit!=0){
//								finIndx +=1; 
//							}
//							for(int i=0; i<finIndx; ++i){
//								if(words.length-1 >= (i*sent_words_limit)+(sent_words_limit-1)){
//									lines.add(Joiner.on(" ").join(Arrays.copyOfRange(words, i*sent_words_limit, (i*sent_words_limit)+(sent_words_limit-1))));
//								}else{
//									lines.add(Joiner.on(" ").join(Arrays.copyOfRange(words, i*sent_words_limit, words.length-1)));
//								}
//							}
//							//							System.out.println(f.toString()+" = "+file.toString());
//							if(!file.toString().equalsIgnoreCase("/home/ASUAD/asharm73/processed_sents/gpu_machine2/0013wb-38.warc.gz/2913")
//									&& !wrongWords){
//								for(String sent : lines){
//									ArrayList<String> knowledge = mc.getKnowledge(sent.trim());
//									if(knowledge.size()>0){
//										for(String know : knowledge){
//											bw.append(line);
//											bw.newLine();
//											bw.append(know);
//											bw.newLine();
//											bw.append("*******************************************");
//											bw.newLine();
//										}
//									}
//								}
//							}
//						}
//						br.close();
//					}catch(Exception e){
//						System.err.println(e.toString());
//						//											e.printStackTrace();
//					}
//
//				}
//			}
//			bw.close();
//		}catch(Exception e){
//			//			e.printStackTrace();
//			System.err.println("ERROR occured !!!");
//		}

	}

	public static void main(String[] args)throws Exception{
		KnowledgeExtractor main = new KnowledgeExtractor();
	//		main.getKnowFromSent("Fish ate the worm because worm was tasty.");
	//		String inputDir = "/home/ASUAD/asharm73/ReadingWarcFiles/manual_webpages/knowledgeSents/";
	//		String outDir = "./knowledge/";
		
		
		
//		String inputDir = "/home/ASUAD/asharm73/knowledgeExtWorkspace/Test/prashant/june6/sents/";
//		inputDir = "/home/ASUAD/asharm73/home/ASUAD/asharm73/KnowledgeExtraction/wiki_qry_sents/";
//		String outDir = "/home/ASUAD/asharm73/knowledgeExtWorkspace/Test/prashant/june6/knowledge/";
//		outDir = "/home/ASUAD/asharm73/knowledgeExtWorkspace/Test/wiki/";
//		File[] listOfFiles = (new File(inputDir)).listFiles(); 
//		for(File f : listOfFiles){
//			try{
//				main.getKnowFromFile(f.getAbsolutePath(), outDir+f.getName());
//			}catch(Exception e){
//				System.err.println("Error in finding knowledge !!!");
//			}
//		}
		
		
		
		String sentence = "Usually when I eat it is because I am hungry.";
//		sentence = "A food you eat because you are hungry ";
//		sentence = "John defeated Tom because Tom did not start well.";
//		sentence = "We are impressed to see juggler of Las Vegas";
		
		//***************** TYPE 2 ***************************
		sentence = "installed CPU and fan would not fit into the box because the fan was too large and";
		sentence = "She could not lift it off the floor because she is a weak girl.";
		sentence = "The driver then may have decided not to lift it because it was too heavy.";
//		sentence = "hi everyone, I am a student and my organisation blocked the torrents so we can not use torrents";
		
		ArrayList<String> know = main.getKnowledge(sentence);
		for(String s : know){
			System.out.println(s);
		}
		System.exit(0);
	}

	public void getKnowFromFile (String inputFile, String outputFile) throws Exception{
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
				BufferedReader br = new BufferedReader(new FileReader(inputFile))){
			String line = null;
			while((line=br.readLine())!=null){
				if(line.trim().equalsIgnoreCase("")){
					continue;
				}
				ArrayList<String> lines = preprocessor.breakParagraph(line);
				for(String sent : lines){
					ArrayList<String> knowledge = getKnowledge(sent.trim());
					if(knowledge.size()>0){
						System.out.println(knowledge.size());
						for(String know : knowledge){
							bw.append(line);
							bw.newLine();
							bw.append(know);
							bw.newLine();
							bw.append("*******************************************\n");
						}
					}
				}
			}
			br.close();
			bw.close();
		}catch(IOException e){
			e.printStackTrace();			
		}
	}

	public ArrayList<String> getKnowledge(String inputText) throws Exception{
		ArrayList<String> result = null;
		inputText = lk.preprocessText(inputText);
		
		inputText = preprocessor.removeParentheses(inputText);
		
		GraphPassingNode gpn = lk.extractGraphWithoutPrep(inputText, false, true, false, 0);
		
		HashMap<String,ArrayList<String>> mapOfLists = preprocessor.prepareKnowledgeSent(gpn);
		
		HashMap<String,String> posMap = gpn.getposMap();
		HashMap<Integer,String> wordsMap = new HashMap<Integer, String>();
		for(String s : posMap.keySet()){
			Pattern wordPat = Pattern.compile("(.*)(-)([0-9]{1,7})");
			Matcher m = wordPat.matcher(s);
			if(m.matches()){
				wordsMap.put(Integer.parseInt(m.group(3).trim()),m.group(1).trim());
			}
		}

		StringBuffer sb = new StringBuffer();
		for(int i=1;i<=wordsMap.size();++i){
			sb.append(wordsMap.get(i)+" ");
		}

		ArrayList<Node> subgraphs = eventExtractor.extractEventGraphs(gpn);
		ArrayList<DiscourseInfo> discInfoList = preprocessor.divideUsingDiscConn(sb.toString().trim());
		for(DiscourseInfo discInfo : discInfoList){
			if((discInfo)!=null){
				System.out.println(discInfo.getLeftArg());
				System.out.println(discInfo.getRightArg());

				aspPreparer.prepareASPFile(discInfo, subgraphs, tempFileName, mapOfLists);

				ArrayList<String> listOfFiles = Lists.newArrayList();
				listOfFiles.add(tempFileName);
				listOfFiles.add(aspRulesFileName);
				ArrayList<String> ansFromASP = clingoWrapper.callASPusingFilesList(listOfFiles);
				if(ansFromASP!=null){
					if(result==null){
						result = new ArrayList<String>();
					}
					result.addAll(ansFromASP);
				}else{
					System.err.println("Error in ASP module!!!");
				}
			}
		}
		return result;
	}
	
	
	public ArrayList<String> getKnowledge(String inputText, ArrayList<DiscourseInfo> discInfoList) throws Exception{
		ArrayList<String> result = new ArrayList<String>();
		inputText = preprocessor.removeParentheses(inputText);
		
		GraphPassingNode gpn = lk.extractGraphWithoutPrep(inputText, false, true, false, 0);
		
		HashMap<String,ArrayList<String>> mapOfLists = preprocessor.prepareKnowledgeSent(gpn);
		
		HashMap<String,String> posMap = gpn.getposMap();
		HashMap<Integer,String> wordsMap = new HashMap<Integer, String>();
		for(String s : posMap.keySet()){
			Pattern wordPat = Pattern.compile("(.*)(-)([0-9]{1,7})");
			Matcher m = wordPat.matcher(s);
			if(m.matches()){
				wordsMap.put(Integer.parseInt(m.group(3).trim()),m.group(1).trim());
			}
		}

		StringBuffer sb = new StringBuffer();
		for(int i=1;i<=wordsMap.size();++i){
			sb.append(wordsMap.get(i)+" ");
		}

		ArrayList<Node> subgraphs = eventExtractor.extractEventGraphs(gpn);
		for(DiscourseInfo discInfo : discInfoList){
			if((discInfo)!=null){
				System.out.println(discInfo.getLeftArg());
				System.out.println(discInfo.getRightArg());

				aspPreparer.prepareASPFile(discInfo, subgraphs, tempFileName, mapOfLists);

				ArrayList<String> listOfFiles = Lists.newArrayList();
				listOfFiles.add(tempFileName);
				listOfFiles.add(aspRulesFileName);
				result.addAll(clingoWrapper.callASPusingFilesList(listOfFiles));
			}
		}
		return result;
	}

	//	public static void main__(String[] args){
	//		PDTBResource pdtbRes = PDTBResource.getInstance();
	//		String inputText = "I spread the cloth on the table in order to protect it.";
	//		String pennTree = "(ROOT (S (NP (PRP I)) (VP (VBD spread) (NP (DT the) (NN cloth)) (PP (IN on) (NP (DT the) (NN table))) (SBAR (IN in) (NN order) (S (VP (TO to) (VP (VB protect) (NP (PRP it))))))) (. .)))";
	//		try{
	//			ArrayList<ConnectivesClass> listOfConns = pdtbRes.getConnAndArgs(inputText, pennTree);
	//			for(ConnectivesClass conn : listOfConns){
	//				System.out.println(conn.getConn());
	//			}
	//		}catch(Exception e){
	//			e.printStackTrace();
	//		}
	//	}

}

