/**
 * 
 */
package edu.asu.nlu.knet.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import edu.asu.nlu.knet.utilities.JAWSutility;
import edu.asu.nlu.knet.utilities.Tools;
import edu.asu.nlu.wordnet.WordNetTools;

/**
 * @author Arpit Sharma
 * @date Jul 10, 2017
 *
 */
public class QueryTermsIndexer {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QueryTermsIndexer qti = new QueryTermsIndexer();
//		System.out.println(qti.toString());
		
//		String dir = "./commonsense_knowledge/all_raw_asp_k/";
//		Tools.saveObject("./indexing_related/kbDetails.ser", qti.extractKeywordsFromFiles(dir));
		
//		KBDetails kbd = (KBDetails) Tools.load("./indexing_related/kbDetails.ser");
//		Tools.saveObject("./indexing_related/indexingModelEvents.ser",qti.createIndexedModel(kbd.getEvents()));
//		Tools.saveObject("./indexing_related/indexingModelProperties.ser",qti.createIndexedModel(kbd.getProperties()));
		
		HashMap<String,TreeMap<Double,ArrayList<String>>> eventsModel = (HashMap<String, TreeMap<Double, ArrayList<String>>>) Tools.load("./indexing_related/indexingModelEvents.ser");
		HashMap<String,TreeMap<Double,ArrayList<String>>> propertiesModel = (HashMap<String, TreeMap<Double, ArrayList<String>>>) Tools.load("./indexing_related/indexingModelProperties.ser");

		System.exit(0);
	}
	
	public HashMap<String,TreeMap<Double,ArrayList<String>>> createIndexedModel(HashSet<String> terms){
		HashMap<String,TreeMap<Double,ArrayList<String>>> model = new HashMap<String, TreeMap<Double,ArrayList<String>>>();
		HashSet<String> doneTerms = new HashSet<String>();
		for(String term : terms){
//			System.out.println(term);
			if(doneTerms.contains(term)){
				continue;
			}
			if(!model.containsKey(term)){
				String similarTerm = null;
				double maxSim = -99;
				for(String key : model.keySet()){
					if(key.equals(term)){
						continue;
					}
					Double tmpSim = WordNetTools.getSimilarity(term, key); 
					if(tmpSim > 0.8 && tmpSim > maxSim){
						similarTerm = key;
						maxSim = tmpSim;
					}
				}
				
				if(similarTerm!=null){
					TreeMap<Double, ArrayList<String>> map = model.get(similarTerm);
					ArrayList<String> listOfEvents = null;
					if(map.containsKey(maxSim)){
						listOfEvents = map.get(maxSim);
					}else{
						listOfEvents = new ArrayList<String>();
					}
					listOfEvents.add(term);
					map.put(maxSim, listOfEvents);
					doneTerms.add(term);
					model.put(similarTerm, map);
				}else{
					TreeMap<Double, ArrayList<String>> map = new TreeMap<Double, ArrayList<String>>(Collections.reverseOrder());
					model.put(term, map);
				}
			}
		}
		return model;
	}
	
	public KBDetails extractKeywordsFromFiles(String dir){
		KBDetails kbd = null;
		HashSet<String> events = new HashSet<String>();
		HashSet<String> properties = new HashSet<String>();

		File[] files = new File(dir).listFiles(File::isFile);
		for(File textKnowledgeFile: files){
			System.out.println(textKnowledgeFile.getName());
			try(BufferedReader in = new BufferedReader(new FileReader(textKnowledgeFile))){
				while(in.readLine()!=null){
					String knowStr = in.readLine().trim();
					String[] knows = knowStr.split(" ");
					for(String kStr : knows){
						kStr = kStr.substring(kStr.indexOf("(")+1,kStr.length()-1);
						String[] knowArr = kStr.split(",");
						if(knowArr.length == 11){
							events.add(knowArr[2]);
							events.add(knowArr[8]);
						}else if(knowArr.length==5){
							events.add(knowArr[3]);
							String prop = knowArr[1];
							if(prop.matches("(.*)(_)([0-9]{1,7})")){
								properties.add(prop.substring(0, prop.lastIndexOf("_")));
							}else{
								properties.add(prop);
							}
						}else if(knowArr.length==4){
							events.add(knowArr[2]);
							String prop = knowArr[0];
							if(prop.matches("(.*)(_)([0-9]{1,7})")){
								properties.add(prop.substring(0, prop.lastIndexOf("_")));
							}else{
								properties.add(prop);
							}
						}else{
							System.err.println(knowStr);
						}
					}
					in.readLine();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		boolean nullFlag = true;
		if(events.size()!=0){
			if(nullFlag){
				kbd = new KBDetails();
				nullFlag = false;
			}
			kbd.setEvents(events);
		}
		
		if(properties.size()!=0){
			if(nullFlag){
				kbd = new KBDetails();
				nullFlag = false;
			}
			kbd.setProperties(properties);
		}
		return kbd;
	}
	
	

}
