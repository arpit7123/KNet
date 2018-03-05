package edu.asu.nlu.knet.extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostprocessKnowledge {

	public static Pattern kn1Pat = Pattern.compile("answerEventsType1\\((.*)\\)");
	public static Pattern kn2Pat = Pattern.compile("answerType2\\((.*)\\)");
	public static Pattern kn3Pat = Pattern.compile("answerType3\\((.*)\\)");
	public static Pattern kn4Pat = Pattern.compile("answerType4\\((.*)\\)");
	public static Pattern kn5Pat = Pattern.compile("answerEventsType5\\((.*)\\)");

	public static void main(String[] args) {
		PostprocessKnowledge pk = new PostprocessKnowledge();
		//		String file = "knowledge.txt";
		//		try(BufferedReader in = new BufferedReader(new FileReader(file))){
		//			String line = null;
		//			while((line=in.readLine())!=null){
		//				String sent = line;
		//				sent = sent.replaceAll("\\*", "");
		//				String knowledge = in.readLine();
		//				String[] knowIns = knowledge.split(" ");
		//				for(String kn : knowIns){
		//					System.out.println(sent);
		//					System.out.println(pk.getKnowledgeInReadableFormat(kn));
		//				}
		//			}
		//		}catch(IOException e){
		//			e.printStackTrace();
		//		}

//		pk.postProcessKnowledge("knowledge_new.txt","readableKnowledge.tsv");
		
		String inputDir = "./knowledge/";
		String outDir = "./readableKnowledge/";
		File[] listOfFiles = (new File(inputDir)).listFiles(); 
		for(File f : listOfFiles){
			System.out.println(f.getName());
			try{
				pk.postProcessKnowledge(f.getAbsolutePath(), outDir+f.getName());
			}catch(Exception e){
				System.err.println("Error in post processing knowledge !!!");
			}
		}
		System.exit(0);

	}

	public void postProcessKnowledge(String inFile, String outFile){
		try(BufferedReader in = new BufferedReader(new FileReader(inFile));
				BufferedWriter out = new BufferedWriter(new FileWriter(outFile))){
			String line = null;
			while((line=in.readLine())!=null){
				System.out.println(line);
				String sent = line;
				String[] knows = in.readLine().split(" ");
				for(String k : knows){
					out.append(sent+"\t"+getKnowledgeInReadableFormat(k));
					out.newLine();
				}
				in.readLine();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private String getKnowledgeInReadableFormat(String kn){
		String readableKnowledge = "";
		if(kn.startsWith("answerEventsType1")){
			Matcher m = kn1Pat.matcher(kn);
			if(m.matches()){
				String argss = m.group(1);	
				String[] argsArr = argss.split(",");
				String vrb1Pol = argsArr[0];
				String vrb1 = argsArr[1];
				String vrb1Base = argsArr[2];
				String vrb1Rel = argsArr[3];
				String vrb1Arg = argsArr[4];
				String conn = argsArr[5];
				String vrb2Pol = argsArr[6];
				String vrb2 = argsArr[7];
				String vrb2Base = argsArr[8];
				String vrb2Rel = argsArr[9];
				String vrb2Arg = argsArr[10];
				if(vrb1Pol.equals("positive")){
					if(vrb2Pol.equals("positive")){
						if(conn.equalsIgnoreCase("because") || conn.equalsIgnoreCase("after") || conn.equalsIgnoreCase("to")){
							readableKnowledge = "execution of "+vrb2Base+" ["+vrb2Rel+": "+vrb2Arg+"] may cause execution of "+vrb1Base+" ["+vrb1Rel+": "+vrb1Arg+"]";
						}else{
							readableKnowledge = "execution of "+vrb1Base+" ["+vrb1Rel+": "+vrb1Arg+"] may cause execution of "+vrb2Base+" ["+vrb2Rel+": "+vrb2Arg+"]";
						}
					}else{
						if(conn.equalsIgnoreCase("because") || conn.equalsIgnoreCase("after") || conn.equalsIgnoreCase("to")){
							readableKnowledge = "execution of "+vrb2Pol+" "+vrb2Base+" ["+vrb2Rel+": "+vrb2Arg+"] may cause execution of "+vrb1Base+" ["+vrb1Rel+": "+vrb1Arg+"]";
						}else{
							readableKnowledge = "execution of "+vrb1Base+" ["+vrb1Rel+": "+vrb1Arg+"] may prevent execution of "+vrb2Base+" ["+vrb2Rel+": "+vrb2Arg+"]";
						}
					}
				}else{
					if(vrb2Pol.equals("positive")){
						if(conn.equalsIgnoreCase("because") || conn.equalsIgnoreCase("after") || conn.equalsIgnoreCase("to")){
							readableKnowledge = "execution of "+vrb2Base+" ["+vrb2Rel+": "+vrb2Arg+"] may prevent execution of "+vrb1Base+" ["+vrb1Rel+": "+vrb1Arg+"]";
						}else{
							readableKnowledge = "execution of "+vrb1Pol+" "+vrb1Base+" ["+vrb1Rel+": "+vrb1Arg+"] may cause execution of "+vrb2Base+" ["+vrb2Rel+": "+vrb2Arg+"]";
						}
					}else{
						if(conn.equalsIgnoreCase("because") || conn.equalsIgnoreCase("after") || conn.equalsIgnoreCase("to")){
							readableKnowledge = "execution of "+vrb2Pol+" "+vrb2Base+" ["+vrb2Rel+": "+vrb2Arg+"] may prevent execution of "+vrb1Base+" ["+vrb1Rel+": "+vrb1Arg+"]";
						}else{
							readableKnowledge = "execution of "+vrb1Pol+" "+vrb1Base+" ["+vrb1Rel+": "+vrb1Arg+"] may prevent execution of "+vrb2Base+" ["+vrb2Rel+": "+vrb2Arg+"]";
						}
					}
				}				
			}
		}else if(kn.startsWith("answerType2")){
			Matcher m = kn2Pat.matcher(kn);
			if(m.matches()){
				String argss = m.group(1);
				String[] argsArr = argss.split(",");
				String propPol = argsArr[0];
				String prop = argsArr[1];
				String vrb = argsArr[2];
				String vrbBase = argsArr[3];
				String vrbRel = argsArr[4];
				readableKnowledge = "X."+prop+" = "+propPol+" may prevent execution of "+vrbBase+" ["+vrbRel+": X]";
			}
		}else if(kn.startsWith("answerType3")){
			Matcher m = kn3Pat.matcher(kn);
			if(m.matches()){
				String argss = m.group(1);
				String[] argsArr = argss.split(",");
				String propPol = argsArr[0];
				String prop = argsArr[1];
				String vrb = argsArr[2];
				String vrbBase = argsArr[3];
				String vrbRel = argsArr[4];
				readableKnowledge = "X."+prop+" = "+propPol+" may cause execution of "+vrbBase+" ["+vrbRel+": X]";
			}
		}else if(kn.startsWith("answerType4")){
			Matcher m = kn4Pat.matcher(kn);
			if(m.matches()){
				String argss = m.group(1);
				String[] argsArr = argss.split(",");
				String propPol = argsArr[0];
				String prop = argsArr[1];
				String vrb = argsArr[2];
				String vrbBase = argsArr[3];
				String vrbRel = argsArr[4];
				readableKnowledge = "execution of "+vrbBase+" ["+vrbRel+": X]"+" may cause "+"X."+prop+" = "+propPol;
			}
		}else if(kn.startsWith("answerEventsType5")){
			Matcher m = kn5Pat.matcher(kn);
			if(m.matches()){
				String argss = m.group(1);	
				String[] argsArr = argss.split(",");
				readableKnowledge = "execution of "+argsArr[2]+" ["+argsArr[3]+": X] may prevent execution of "+argsArr[7]+" ["+argsArr[8]+": X]";
			}
		}

		return readableKnowledge;
	}

}
