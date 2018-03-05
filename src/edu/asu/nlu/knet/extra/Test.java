package edu.asu.nlu.knet.extra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import edu.asu.nlu.knet.CreateDBase;
import edu.asu.nlu.knet.ParaSentNode;

public class Test {
	public static void main3(String[] args){
		Test t = new Test();
		Table<String,String,String> table = HashBasedTable.create();
		String directory = "/host/stuff/KnowledgeGraphWeb/src/module/graph/FrameFiles";
		ArrayList<String> listOfFiles = t.listFilesForFolder(new File(directory));
		int count=1;
		for(String s : listOfFiles){
			if(s.endsWith(".txt")){
				System.out.println(s);
				count++;
				try(BufferedReader in = new BufferedReader(new FileReader(directory + "/" + s))){
					String line = null;
					while((line=in.readLine())!=null){
						String[] values = line.split("\t");
						if(line.startsWith("Arg0")){
							table.put(s.split("\\.")[0], "agent", values[1]);
						}else if(line.startsWith("Arg1")){
							table.put(s.split("\\.")[0], "recipient", values[1]);
						}
					}
				}catch(IOException e){
					e.printStackTrace();
				}
				
				
			}
		}
		System.out.println(count);
		
		t.updateCacheFile("/host/stuff/KnowledgeGraphWeb/FrameFilesTable.ser",table);
		
	}
	
	public static void main(String[] args){
		Test t = new Test();
		Table<String,String,String> table = t.loadFrameFilesTable("/host/stuff/KnowledgeGraphWeb/FrameFilesTable.ser");
		String s = table.get("abandon", "agent");
		System.out.println(s);
	}
	
	@SuppressWarnings("unchecked")
	public Table<String,String,String> loadFrameFilesTable(String fileName){
		Table<String,String,String> table = null;
		try{
			FileInputStream fin = new FileInputStream(fileName);
			ObjectInputStream is = new ObjectInputStream(fin);   
			table = (Table<String,String,String>)is.readObject();
			is.close();
		}catch(Exception ex){
			System.out.println("Cache file not found.");
			return table;
		}
		return table;
	}
	
	public void updateCacheFile(String fileName, Object o){
		try{
			FileOutputStream fout = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(o);
			oos.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public ArrayList<String> listFilesForFolder(File folder) {
		ArrayList<String> listOfFiles = Lists.newArrayList();
	    for (File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	listOfFiles.addAll(listFilesForFolder(fileEntry));
	        } else {
	        	String name = fileEntry.getName();
//	        	if(name.endsWith(".txt")){
	        		listOfFiles.add(name);
//	        	}
	        }
	    }
	    return listOfFiles;
	}
	public static void main___(String[] args){
		String fileName = "./Queries/queries";
		int counter=1;
		try(BufferedReader in = new BufferedReader(new FileReader(fileName))){
			String line = null;
			while((line=in.readLine())!=null){
				in.readLine();
				in.readLine();
				String sent = in.readLine();//line;///
//				System.out.println(counter);
				System.out.println("BACKGROUND SENTENCE: "+sent);
				System.out.println("EXTRACTED KNOWLEDGE:-");
				while(!(line=in.readLine()).trim().equalsIgnoreCase("")){//line.startsWith("answerEvents")){
					line = line.replaceAll("answerEvents\\(", "");//counter++;
					line = line.replaceAll("\\)", "");
					String[] temp = line.split(",");
					System.out.println("("+temp[1].split("_")[0]+","+temp[0]+") "+temp[3]+" ("+temp[5].split("_")[0]+","+temp[4]+")");
					System.out.println("THEN "+temp[2] +" of "+temp[1].split("_")[0]+" = "+temp[6] + " of "+temp[5].split("_")[0]);
					System.out.println();
				}
				System.out.println();
				counter++;
			}
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		System.out.println(counter);
	}
	
	public static void main_PutInDb(String[] args) throws UnknownHostException{
		CreateDBase cdb = new CreateDBase();
		try(BufferedReader in = new BufferedReader(new FileReader("./Queries/queries"))){
			String line = null;
			while((line=in.readLine())!=null){
				System.out.println("NEW VALUES");
				String docId = line;
				String paraId = in.readLine();
				String sentId = in.readLine();
				String sentence = in.readLine();
				System.out.println(docId);
				System.out.println(paraId);
				System.out.println(sentId);
				System.out.println(sentence);
				cdb.updateDocumentTable(docId, paraId, sentId);
				cdb.updateSentenceTable(sentId, sentence);
				while(!(line=in.readLine()).trim().equalsIgnoreCase("")){
					String temp = line.replace("answerEvents(", "");
					temp = temp.replace(")", "");
					ArrayList<String> tempList = Lists.newArrayList(Splitter.on(",").split(temp));
					String type1 = "";
					String polarity1 = tempList.get(0);
					String value1 = tempList.get(1).split("_")[0];
					String slot1 = tempList.get(2);
					String slotValue1 = "";
					cdb.updateSlotValueable(sentId, type1, polarity1,"X", value1, slot1, slotValue1);
					
					String relType = "";
					String relValue = tempList.get(3);
					
					String type2 = "";
					String polarity2 = tempList.get(4);
					String value2 = tempList.get(5).split("_")[0];
					String slot2 = tempList.get(6);
					String slotValue2 = "";
					cdb.updateSlotValueable(sentId, type2, polarity2, "X", value2, slot2, slotValue2);
					cdb.updateRelationsTable(sentId, relType, relValue, value1, value2);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
//		String documentName = "./ASP/test.txt";
//		ArrayList<ParaSentNode> list = t.divideIntoParagraphs(documentName, 1);
//		for(ParaSentNode n : list){
//			System.out.println(n.getParaId());
//			System.out.println(n.getSentId());
//			System.out.println(n.getSentence());
//			System.out.println();
//		}
//		String s = "answerEvents(positive,fell_10,agent,next_event,positive,went_22,agent) answerEvents(positive,going_33,agent,objective,positive,happen_35,agent)";
//		Pattern p = Pattern.compile("(answerEvents\\()(.*)(,)(.*)(,)(.*)(,)(.*)(,)(.*)(,)(.*)(,)(.*)(\\))");
//		ArrayList<String> sList = Lists.newArrayList(Splitter.on(" ").split(s)); 
//		for(String s1 : sList){
//			System.out.println(s1);
//		}
//		Matcher m = p.matcher(s);
//		int i = 1;
//		while(m.find()){
//			System.out.println(i + m.group(0));
//			i++;
//		}
	}

	public ArrayList<ParaSentNode> divideIntoParagraphs(String documentName, int sentId){
		ArrayList<ParaSentNode> listOfParaNodes = Lists.newArrayList();
		try(BufferedReader in = new BufferedReader(new FileReader(documentName))){
			String line = null;
			int paraId = 1;
			StringBuilder  paragraph = new StringBuilder();
			while((line=in.readLine())!=null){
				if(line.trim().equalsIgnoreCase("")){
					ArrayList<String> listOfSentences = divideIntoSentences(paragraph.toString());
					for(String sentence : listOfSentences){
						ParaSentNode psn = new ParaSentNode(paraId, sentId, sentence);
						listOfParaNodes.add(psn);
						sentId++;
					}
					paraId++;
					paragraph.setLength(0);
				}else{
					paragraph.append(line);
					paragraph.append(" ");
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return listOfParaNodes;
	}
	
	public ArrayList<String> divideIntoSentences(String paragraph){
		ArrayList<String> listOfSentences = Lists.newArrayList();
		ArrayList<String> tempList = Lists.newArrayList(Splitter.on(".").split(paragraph));
		for(String s : tempList){
			ArrayList<String> tempList1 = Lists.newArrayList(Splitter.on(";").split(s));
			listOfSentences.addAll(tempList1);
		}
		return listOfSentences;
	}
}
