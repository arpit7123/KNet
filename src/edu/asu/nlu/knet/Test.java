package edu.asu.nlu.knet;
//package knowledgeNet;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import module.graph.SentenceToGraph;
//
//import com.google.common.base.Splitter;
//import com.google.common.collect.Lists;
//
//public class Test {
//	
//	public static void main(String[] args){
////		Test t = new Test();
////		String documentName = "./ASP/test.txt";
////		ArrayList<ParaSentNode> list = t.divideIntoParagraphs(documentName, 1);
////		for(ParaSentNode n : list){
////			System.out.println(n.getParaId());
////			System.out.println(n.getSentId());
////			System.out.println(n.getSentence());
////			System.out.println();
////		}
//		
//		
//		SentenceToGraph stg = new SentenceToGraph();
//		System.out.println("Hello");
//	}
//
//	public ArrayList<ParaSentNode> divideIntoParagraphs(String documentName, int sentId){
//		ArrayList<ParaSentNode> listOfParaNodes = Lists.newArrayList();
//		try(BufferedReader in = new BufferedReader(new FileReader(documentName))){
//			String line = null;
//			int paraId = 1;
//			StringBuilder  paragraph = new StringBuilder();
//			while((line=in.readLine())!=null){
//				if(line.trim().equalsIgnoreCase("")){
//					ArrayList<String> listOfSentences = divideIntoSentences(paragraph.toString());
//					for(String sentence : listOfSentences){
//						ParaSentNode psn = new ParaSentNode(paraId, sentId, sentence);
//						listOfParaNodes.add(psn);
//						sentId++;
//					}
//					paraId++;
//					paragraph.setLength(0);
//				}else{
//					paragraph.append(line);
//					paragraph.append(" ");
//				}
//			}
//		}catch(IOException e){
//			e.printStackTrace();
//		}
//		return listOfParaNodes;
//	}
//	
//	public ArrayList<String> divideIntoSentences(String paragraph){
//		ArrayList<String> listOfSentences = Lists.newArrayList();
//		ArrayList<String> tempList = Lists.newArrayList(Splitter.on(".").split(paragraph));
//		for(String s : tempList){
//			ArrayList<String> tempList1 = Lists.newArrayList(Splitter.on(";").split(s));
//			listOfSentences.addAll(tempList1);
//		}
//		return listOfSentences;
//	}
//}
