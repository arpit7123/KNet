/**
 * 
 */
package edu.asu.nlu.knet;

import module.graph.SentenceToGraph;
import module.graph.helper.GraphPassingNode;

/**
 * @author Arpit Sharma
 *
 */
public class MainClass {

	private LocalKparser lk = null;
	private SentenceToGraph stg = null;
	
	public MainClass(){
//		lk = new LocalKparser();
		stg = new SentenceToGraph();
	}
	
	public static void main(String[] args) throws Exception{
		MainClass mc = new MainClass();
		String inputText = "John gave #20 to Tom.";
		GraphPassingNode gpn = mc.stg.extractGraph(inputText, false, true);
		for(String s : gpn.getAspGraph()){
			System.out.println(s);
		}
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
