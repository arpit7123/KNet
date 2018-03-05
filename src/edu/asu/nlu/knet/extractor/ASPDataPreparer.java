/**
 * 
 */
package edu.asu.nlu.knet.extractor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import module.graph.helper.Node;

/**
 * @author Arpit Sharma
 *
 */
public class ASPDataPreparer {
	
//	private Pattern wordPat = Pattern.compile("(.*)(-)([0-9]{1,7})");
	private String tempFileName = "./ASP/tempFile.txt";
	private static ASPDataPreparer dataPreparer = null;
	
	static {
		dataPreparer = new ASPDataPreparer();
	}
	
	public static ASPDataPreparer getInstance(){
		return dataPreparer;
	}
	private ASPDataPreparer(){}
	
	
	public void prepareASPFile(DiscourseInfo discInfo, ArrayList<Node> subgraphs, String fileName, HashMap<String,ArrayList<String>> mapOfLists){
		if(!fileName.equalsIgnoreCase("")){
			this.tempFileName = fileName;
		}
	
		ArrayList<String> aspCodeLines = new ArrayList<String>();
		String leftArg = discInfo.getLeftArg();
		String conn = discInfo.getConn();
		String initConn = discInfo.getInitConn();
		if(initConn==null){
			initConn = "null";
		}
		int threshIndx = leftArg.split(" ").length + conn.split(" ").length;
		for(Node root : subgraphs){
			String rootValue = root.getValue();
			if(rootValue.matches("(.*)(-)([0-9]{1,7})")){
				if(Integer.parseInt(rootValue.substring(rootValue.lastIndexOf("-")+1))>=threshIndx){
					aspCodeLines.addAll(traverseSubevent("has_part2",root,mapOfLists));
				}else{
					aspCodeLines.addAll(traverseSubevent("has_part1",root,mapOfLists));
				}
			}
		}
		
		aspCodeLines.add("initConn("+initConn+").");
		aspCodeLines.add("conn("+getConnString(conn)+").");
		
		createTempFile(aspCodeLines);
	}
	
	private String getConnString(String conn){
		if(conn.equalsIgnoreCase(";")){
			return "semi_colon";
		}else if(conn.equalsIgnoreCase(".")){
			return "stop";
		}else if(conn.equalsIgnoreCase(",")){
			return "comma";
		}		
		return conn;
	}
	
	private ArrayList<String> traverseSubevent(String predicate, Node node, HashMap<String,ArrayList<String>> mapOfLists){
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<Node> children = node.getChildren();
		ArrayList<String> edges = node.getEdgeList();
		int indx = 0;
		for(Node child : children){
			result.add(predicate + "(" + getNodeValue(node.getValue(),mapOfLists)+","+edges.get(indx)+","+getNodeValue(child.getValue(),mapOfLists)+").");
			result.add(predicate + "(" + getNodeValue(node.getValue(),mapOfLists)+",pos,"+node.getPOS().substring(0,1).toLowerCase()+").");
			result.addAll(traverseSubevent(predicate, child, mapOfLists));
			indx++;
		}		
		return result;
	}	
	
	private String getNodeValue(String value, HashMap<String,ArrayList<String>> mapOfLists){
		for(String key : mapOfLists.keySet()){
			ArrayList<String> list = mapOfLists.get(key);
			for(String val : list){
				if(val.equals(value)){
//					System.out.println(val+" ::: "+value +" ::: "+list.get(0));
					return list.get(0);
				}
			}
		}
		return value;
	}
	
	private void createTempFile(ArrayList<String> graphText){
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(this.tempFileName))){
			for(String s : graphText){
				s = s.replaceAll(" +", " ");
				s = s.replaceAll(" ", "_");
				s = s.replaceAll("-", "_");
				s = s.replaceAll("\\$", "");
				s = s.replaceAll("\\(not,", "\\(nt,");
				s = s.replaceAll(",not\\)", ",nt\\)");
				bw.append(s.toLowerCase());
				bw.newLine();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
