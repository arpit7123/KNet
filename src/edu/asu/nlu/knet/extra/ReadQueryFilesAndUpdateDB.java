/**
 * 
 */
package edu.asu.nlu.knet.extra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;

import edu.asu.nlu.knet.utilities.Tools;

/**
 * @author arpit
 *
 */
public class ReadQueryFilesAndUpdateDB {
	
	private HashSet<String> set = null;
//	private CreateDBase cdb = null;
	
//	objective
//	previous_event
//	next_event
//	caused_by
//	causes
//	private HashSet<String> uniqueRelations = new HashSet<String>();
	
	public ReadQueryFilesAndUpdateDB(){// throws UnknownHostException{
//		this.cdb = new CreateDBase("KnowledgeNetDB");
	}
	
	public int updateUsingDir(String dir){
		ArrayList<String> fileNames = Tools.listFilesWithAbsPath(dir);
		int count = 0;
		for(String fileName : fileNames){
			count = updateUsingFile(fileName,count);
		}
		return count;
	}
	
	@SuppressWarnings("unused")
	public int updateUsingFile(String fileName, int count){
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))){//;
//				BufferedWriter bw = new BufferedWriter(new FileWriter("evaluationKnowledgeNL"))){
			
			String line = null;
			while((line=br.readLine())!=null){
				String q1 = line;
				String q2 = br.readLine();
				String q3 = br.readLine();
				String q4 = br.readLine();
				String q5 = br.readLine();
				String q6 = br.readLine();
				String[] tmp = q1.trim().split(":::");
				
				String docId = tmp[0];
				String paraId = tmp[1];
				String sentId = "";
				String sent = "";
				
				String relation = "";
				
				String polarity1 = "";
				String verb1 = "";
				String verb1Base = "";
				String slot1 = "";
				String arg1 = "";
				
				String polarity2 = "";
				String verb2 = "";
				String verb2Base = "";
				String slot2 = "";
				String arg2 = "";
				
				String eventPol = "";
				String eventVerb = "";
				String eventVerbBase = "";
				String slot = "";
				String attrPol = "";
				String attrValue = "";
				String attrBase = "";
				
				if(!q2.equalsIgnoreCase("null")){
					tmp = q2.split(":::");
					sentId = tmp[0];
					relation = tmp[2];
//					uniqueRelations.add(relation);
					verb1Base = tmp[3];
					verb2Base = tmp[4];
					
					tmp = q3.split(":::");
					sent = tmp[1];
					
//					System.out.println(fileName);
//					System.out.println(q4);
					tmp = q4.split(":::");
					polarity1 = tmp[2];
					verb1 = tmp[3];
					slot1 = tmp[5];
					arg1 = tmp[6];
					
					tmp = q5.split(":::");
					polarity2 = tmp[2];
					verb2 = tmp[3];
					slot2 = tmp[5];
					arg2 = tmp[6];
					
//					if(polarity1.equalsIgnoreCase("NEGETIVE")||polarity1.equalsIgnoreCase("NEGATIVE")){
//						System.out.println(count);
//						System.out.println(polarity1 + " : " +verb1 + " : " + polarity2 + " : " + verb2);
//					}
//					
//					this.cdb.updateDocumentTable(docId, paraId, sentId);
//					this.cdb.updateRelationsTable(sentId,"X",relation,verb1Base,verb2Base);
//					this.cdb.updateSentenceTable(sentId,sent);
//					this.cdb.updateSlotValueable(sentId,"X",polarity1,verb1,verb1Base,slot1,arg1);
//					this.cdb.updateSlotValueable(sentId,"X",polarity2,verb2,verb2Base,slot2,arg2);
				
				}else{
					tmp = q3.split(":::");
					sentId = tmp[0];
					sent = tmp[1];
					
					tmp = q4.split(":::");
					eventPol = tmp[2];
					eventVerb = tmp[3];
					eventVerbBase = tmp[4];
					slot = tmp[5];
					
					tmp = q5.split(":::");
					attrPol = tmp[4];
					attrValue = tmp[5];
					attrBase = tmp[6];
					
//					this.cdb.updateDocumentTable(docId, paraId, sentId);
//					this.cdb.updateSentenceTable(sentId,sent);
//					this.cdb.updateSlotValueable(sentId,"X",eventPol,eventVerb,eventVerbBase,slot,"X");
//					this.cdb.updateAttrKnowTable(sentId, "X", eventVerb, eventVerbBase, attrPol, attrValue, attrBase);
				
				}
				
//				objective
//				previous_event
//				next_event
//				caused_by
//				causes
				
				if(polarity1.equalsIgnoreCase("NEGETIVE")||
						polarity2.equalsIgnoreCase("NEGETIVE")||
						polarity1.equalsIgnoreCase("NEGATIVE")||
						polarity2.equalsIgnoreCase("NEGATIVE")){
				
				if(relation.equalsIgnoreCase("objective")){
//					String sasa = docId+"\t"+paraId+"\t"+sentId+"\t"+relation+"\t"+polarity1+"\t"
//							+verb1+"\t"+verb1Base+"\t"+slot1+"\t"+arg1+"\t"+polarity2+"\t"
//							+verb2+"\t"+verb2Base+"\t"+slot2+"\t"+arg2+"\t"+eventPol+"\t"
//							+eventVerb+"\t"+eventVerbBase+"\t"+slot+"\t"+attrPol+"\t"+attrValue+"\t"
//							+attrBase;
					if(slot1.equalsIgnoreCase("agent") && slot2.equalsIgnoreCase("recipient")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) so that possibly someone/something(W) "+verb2+" someone/something(X)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("agent") && slot2.equalsIgnoreCase("agent")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) so that possibly someone/something(X) "+verb2+" someone/something(W)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("recipient") && slot2.equalsIgnoreCase("recipient")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) so that possibly someone/something(W) "+verb2+" someone/something(Y)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("recipient") && slot2.equalsIgnoreCase("agent")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) so that possibly someone/something(Y) "+verb2+" someone/something(W)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}
//					String sasa = "If "+verb1+" has objective "+verb2+" then " + slot1 + " of " +verb1 + " is same as " +slot2 + " of " +verb2;
//					System.out.println(sasa);
//					bw.append(sasa);
//					bw.newLine();
				}else if(relation.equalsIgnoreCase("previous_event")){
					if(slot1.equalsIgnoreCase("agent") && slot2.equalsIgnoreCase("recipient")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) and before possibly someone/something(W) "+verb2+" someone/something(X)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("agent") && slot2.equalsIgnoreCase("agent")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) and before possibly someone/something(X) "+verb2+" someone/something(W)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("recipient") && slot2.equalsIgnoreCase("recipient")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) and before possibly someone/something(W) "+verb2+" someone/something(Y)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("recipient") && slot2.equalsIgnoreCase("agent")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) and before possibly someone/something(Y) "+verb2+" someone/something(W)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}
//					String sasa = "If "+verb1+" happened after "+verb2+" then " + slot1 + " of " +verb1 + " is same as " +slot2 + " of " +verb2;
//					System.out.println(sasa);
//					bw.append(sasa);
//					bw.newLine();
				}else if(relation.equalsIgnoreCase("next_event")){
					if(slot1.equalsIgnoreCase("agent") && slot2.equalsIgnoreCase("recipient")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) and possibly someone/something(W) "+verb2+" someone/something(X)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("agent") && slot2.equalsIgnoreCase("agent")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) and possibly someone/something(X) "+verb2+" someone/something(W)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("recipient") && slot2.equalsIgnoreCase("recipient")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) and possibly someone/something(W) "+verb2+" someone/something(Y)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("recipient") && slot2.equalsIgnoreCase("agent")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) and possibly someone/something(Y) "+verb2+" someone/something(W)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}
//					String sasa = "If "+verb1+" is followed by "+verb2+" then " + slot1 + " of " +verb1 + " is same as " +slot2 + " of " +verb2;
//					System.out.println(sasa);
//					bw.append(sasa);
//					bw.newLine();
				}else if(relation.equalsIgnoreCase("caused_by")){
					if(slot1.equalsIgnoreCase("agent") && slot2.equalsIgnoreCase("recipient")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) possibly because someone/something(W) "+verb2+" someone/something(X)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("agent") && slot2.equalsIgnoreCase("agent")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) possibly because someone/something(X) "+verb2+" someone/something(W)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("recipient") && slot2.equalsIgnoreCase("recipient")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) possibly because someone/something(W) "+verb2+" someone/something(Y)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("recipient") && slot2.equalsIgnoreCase("agent")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) possibly because someone/something(Y) "+verb2+" someone/something(W)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}
//					String sasa = "If "+verb1+" is caused by "+verb2+" then " + slot1 + " of " +verb1 + " is same as " +slot2 + " of " +verb2;
//					System.out.println(sasa);
//					bw.append(sasa);
//					bw.newLine();
				}else if(relation.equalsIgnoreCase("causes")){
					if(slot1.equalsIgnoreCase("agent") && slot2.equalsIgnoreCase("recipient")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) so, possibly someone/something(W) "+verb2+" someone/something(X)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("agent") && slot2.equalsIgnoreCase("agent")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) so, possibly someone/something(X) "+verb2+" someone/something(W)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("recipient") && slot2.equalsIgnoreCase("recipient")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) so, possibly someone/something(W) "+verb2+" someone/something(Y)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}else if(slot1.equalsIgnoreCase("recipient") && slot2.equalsIgnoreCase("agent")){
						String sasa = "someone/something(X) "+verb1+" someone/something(Y) so, possibly someone/something(Y) "+verb2+" someone/something(W)";
						if(this.set.contains(sasa)){
							System.out.println("polarity1: "+polarity1+" ::: polarity2: "+polarity2);
							System.out.println(sasa);
						}
//						bw.append(sasa);
//						bw.newLine();
					}
//					String sasa = "If "+verb1+" causes "+verb2+" then " + slot1 + " of " +verb1 + " is same as " +slot2 + " of " +verb2;
//					System.out.println(sasa);
//					bw.append(sasa);
//					bw.newLine();
				}else{
//					String sasa = "NO KNOWN KNOWLEDGE";
//					System.out.println(sasa);
//					bw.append(sasa);
//					bw.newLine();
				}
				}
				
//				bw.newLine();
//				bw.append("The statement above is a reasonably clear, entirely plausible, generic claim and seems neither too specific nor too general or vague to be useful: ");
//				bw.newLine();
//				bw.append("1. I agree.");
//				bw.newLine();
//				bw.append("2. I lean towards agreement.");
//				bw.newLine();
//				bw.append("3. I'm not sure.");
//				bw.newLine();
//				bw.append("4. I lean towards disagreement.");
//				bw.newLine();
//				bw.append("5. I disagree.");
//				bw.newLine();
//				bw.append("****************************************************************");
//				bw.newLine();
//				bw.append("Answer: ");
//				bw.newLine();
//				bw.append("****************************************************************");
//				bw.newLine();
//				bw.newLine();
				
				count++;
				br.readLine();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return count;
	}

	public static void main___(String[] args) throws UnknownHostException{
		ReadQueryFilesAndUpdateDB rqfudb = new ReadQueryFilesAndUpdateDB();
		System.out.println("docID\tparaId\tsentId\trelation\tpolarity1\t"
				+ "verb1\tverb1Base\tslot1\targ1\tpolarity2\t"
				+ "verb2\tverb2Base\tslot2\targ2\teventPol\t"
				+ "eventVerb\teventVerbBase\tslot\tattrPol\tattrValue\tattrBase");
//		System.out.println("*********************************************************************************************");
//		String fileName = "/home/arpit/workspace/KnowledgeNet/test.txt";
//		rqfudb.updateUsingFile(fileName);
		String dirName = "/home/arpit/workspace/KnowledgeNet/UpdateQueries";
		dirName = "/home/arpit/workspace/finalKnowledge";
		int n = rqfudb.updateUsingDir(dirName);
//		19336
		System.out.println(n);
		
//		for(String s : rqfudb.uniqueRelations){
//			System.out.println(s);
//		}	
	}
	
	public static void main__(String[] args){
		
		try(BufferedReader br = new BufferedReader(new FileReader("evaluationKnowledgeNL"));
				BufferedWriter bw1 = new BufferedWriter(new FileWriter("evaluationKnowledgeNL1"));
				BufferedWriter bw2 = new BufferedWriter(new FileWriter("evaluationKnowledgeNL2")))	{
			String line = null;
			HashSet<String> set = new HashSet<String>();
			while((line=br.readLine())!=null){
				set.add(line.trim());
			}
			br.close();
			
			int count = 1;
			int size = set.size();
			for(String s : set){
				if(count < size/2){
					bw1.append(count + ": " + s);
					bw1.newLine();
//					bw1.newLine();
//					bw1.append("The statement above is a reasonably clear, entirely plausible, generic claim and seems neither too specific nor too general or vague to be useful: ");
//					bw1.newLine();
//					bw1.append("1. I agree.");
//					bw1.newLine();
//					bw1.append("2. I lean towards agreement.");
//					bw1.newLine();
//					bw1.append("3. I'm not sure.");
//					bw1.newLine();
//					bw1.append("4. I lean towards disagreement.");
//					bw1.newLine();
//					bw1.append("5. I disagree.");
//					bw1.newLine();
					bw1.append("****************************************************************");
					bw1.newLine();
					bw1.append("Answer: ");
					bw1.newLine();
					bw1.append("****************************************************************");
					bw1.newLine();
					bw1.newLine();
				}else{
					bw2.append(count + ": " + s);
					bw2.newLine();
//					bw2.newLine();
//					bw2.append("The statement above is a reasonably clear, entirely plausible, generic claim and seems neither too specific nor too general or vague to be useful: ");
//					bw2.newLine();
//					bw2.append("1. I agree.");
//					bw2.newLine();
//					bw2.append("2. I lean towards agreement.");
//					bw2.newLine();
//					bw2.append("3. I'm not sure.");
//					bw2.newLine();
//					bw2.append("4. I lean towards disagreement.");
//					bw2.newLine();
//					bw2.append("5. I disagree.");
//					bw2.newLine();
					bw2.append("****************************************************************");
					bw2.newLine();
					bw2.append("Answer: ");
					bw2.newLine();
					bw2.append("****************************************************************");
					bw2.newLine();
					bw2.newLine();
				}
				
				count++;
			}
			bw1.close();
			bw2.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
public static void main_(String[] args){
		String fileName = "/home/arpit/workspace/KnowledgeNet/evaluationKnowledgeNL2";
		int countOf1 = 0;
		int countOf2 = 0;
		int countOf3 = 0;
		int countOf4 = 0;
		int countOf5 = 0;
		int countOf6 = 0;
//		int count =341;
		try(BufferedReader br = new BufferedReader(new FileReader(fileName)))	{
			String line = null;
			while((line=br.readLine())!=null){
				if(line.startsWith("Answer:")){
//					System.out.println(count);
					String[] tmp = line.split(":");
					int num = Integer.parseInt(tmp[1].trim());
					if(num==1){
						countOf1++;
					}else if(num==2){
						countOf2++;
					}else if(num==3){
						countOf3++;
					}else if(num==4){
						countOf4++;
					}else if(num==5){
						countOf5++;
					}else if(num==6){
						countOf6++;
					}else{
						countOf6++;
					}
//					count++;
				}
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		System.out.println("Count of rank 1: "+countOf1);
		System.out.println("Count of rank 2: "+countOf2);
		System.out.println("Count of rank 3: "+countOf3);
		System.out.println("Count of rank 4: "+countOf4);
		System.out.println("Count of rank 5: "+countOf5);
		System.out.println("Count of rank 6: "+countOf6);
	}
	


	public static void main____(String[] args){
		
		ReadQueryFilesAndUpdateDB r = new ReadQueryFilesAndUpdateDB();
		HashSet<String> s = new HashSet<String>();
		try(BufferedReader br = new BufferedReader(new FileReader("/home/arpit/workspace/KnowledgeNet/evaluationKnowledgeNL"))){
			String line = null;
			while((line=br.readLine())!=null){
				s.add(line);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		r.set = s;
		
//		System.out.println(s.size());
		
		String dirName = "/home/arpit/workspace/KnowledgeNet/UpdateQueries";
		dirName = "/home/arpit/workspace/finalKnowledge";
		int n = r.updateUsingDir(dirName);
		System.out.println(n);
		
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args){
//		ReadQueryFilesAndUpdateDB r = new ReadQueryFilesAndUpdateDB();
		int countOf1 = 0;
		int countOf2 = 0;
		int countOf3 = 0;
		int countOf4 = 0;
		int countOf5 = 0;
		int countOf6 = 0;
		int countOfMisMatch = 0;
		int countOfMisMatch1 = 0;
		int countOfMisMatch2 = 0;
		int countOfMisMatch3 = 0;
		int countOfMisMatch4 = 0;
		int countOfMisMatch5 = 0;
		int countOfMisMatch6 = 0;
		
		int countOfSamMisMatch = 0;
		int countOfSamMisMatch1 = 0;
		int countOfSamMisMatch2 = 0;
		int countOfSamMisMatch3 = 0;
		int countOfSamMisMatch4 = 0;
		int countOfSamMisMatch5 = 0;
		int countOfSamMisMatch6 = 0;
		
		int count = 1;
		try(BufferedReader br1 = new BufferedReader(new FileReader("/home/arpit/workspace/KnowledgeNet/arpit_eval"));
				BufferedReader br2 = new BufferedReader(new FileReader("/home/arpit/workspace/KnowledgeNet/sam_eval"))){
			String line1 = null;
			String line2 = null;
			while((line1=br1.readLine())!=null){
				line2 = br2.readLine();
				
				String arpitLine = line1;
				String samLine = line2;
				
//				***** line
				line1=br1.readLine();
				line2=br2.readLine();

//				***** answer line
				line1=br1.readLine();
				line2=br2.readLine();
				
				if(line1.startsWith("Answer:")&&
						line2.startsWith("Answer:")){
					String[] tmp1 = line1.split(":");
					int num1 = Integer.parseInt(tmp1[1].trim());
					String[] tmp2 = line2.split(":");
					int num2 = Integer.parseInt(tmp2[1].trim());
					
					if(num1==1 && num2==1){
						countOf1++;
					}else if(num1==2 && num2==2){
						countOf2++;
					}else if(num1==3 && num2==3){
						countOf3++;
					}else if(num1==4 && num2==4){
						countOf4++;
					}else if(num1==5 && num2==5){
						countOf5++;
					}else if(num1==6 && num2==6){
						countOf6++;
					}else{
						countOfMisMatch++;
						
						if(Integer.parseInt(line1.split(":")[1].trim())==1){
							countOfMisMatch1++;
						}else if(Integer.parseInt(line1.split(":")[1].trim())==2){
							countOfMisMatch2++;
						}else if(Integer.parseInt(line1.split(":")[1].trim())==3){
							countOfMisMatch3++;
						}else if(Integer.parseInt(line1.split(":")[1].trim())==4){
							countOfMisMatch4++;
						}else if(Integer.parseInt(line1.split(":")[1].trim())==5){
							countOfMisMatch5++;
						}else if(Integer.parseInt(line1.split(":")[1].trim())==6){
							countOfMisMatch6++;
						}else{
							System.err.println("ERR");
						}
						
						
						if(Integer.parseInt(line2.split(":")[1].trim())==1){
							countOfSamMisMatch1++;
						}else if(Integer.parseInt(line2.split(":")[1].trim())==2){
							countOfSamMisMatch2++;
						}else if(Integer.parseInt(line2.split(":")[1].trim())==3){
							countOfSamMisMatch3++;
						}else if(Integer.parseInt(line2.split(":")[1].trim())==4){
							countOfSamMisMatch4++;
						}else if(Integer.parseInt(line2.split(":")[1].trim())==5){
							countOfSamMisMatch5++;
						}else if(Integer.parseInt(line2.split(":")[1].trim())==6){
							countOfSamMisMatch6++;
						}else{
							System.err.println("ERR");
						}
						
						
						System.err.println(Integer.parseInt(line1.split(":")[1].trim())-Integer.parseInt(line2.split(":")[1].trim()));
						
						
						
						
						
//						System.out.println("Arpit:- "+arpitLine);
//						System.out.println("Arpit:- "+line1);
//						System.out.println("Sam:- "+samLine);
//						System.out.println("Sam:- "+line2);
					}
					count++;
				}
				

//				***** line
				line1=br1.readLine();
				line2=br2.readLine();
				

//				empty
				line1=br1.readLine();
				line2=br2.readLine();
				
			}
			
			br1.close();
			br2.close();
			
			System.out.println("Count of rank 1: "+countOf1);
			System.out.println("Count of rank 2: "+countOf2);
			System.out.println("Count of rank 3: "+countOf3);
			System.out.println("Count of rank 4: "+countOf4);
			System.out.println("Count of rank 5: "+countOf5);
			System.out.println("Count of rank 6: "+countOf6);
			System.out.println("Count of miss matches: "+countOfMisMatch);
			System.out.println("Count: "+count);
			
			
			System.out.println("Count of MisMatch 1: "+countOfMisMatch1);
			System.out.println("Count of MisMatch 2: "+countOfMisMatch2);
			System.out.println("Count of MisMatch 3: "+countOfMisMatch3);
			System.out.println("Count of MisMatch 4: "+countOfMisMatch4);
			System.out.println("Count of MisMatch 5: "+countOfMisMatch5);
			System.out.println("Count of MisMatch 6: "+countOfMisMatch6);
			
			System.out.println("Count of SAM MisMatch 1: ");
			System.out.println("Count of MisMatch 1: "+countOfSamMisMatch1);
			System.out.println("Count of MisMatch 2: "+countOfSamMisMatch2);
			System.out.println("Count of MisMatch 3: "+countOfSamMisMatch3);
			System.out.println("Count of MisMatch 4: "+countOfSamMisMatch4);
			System.out.println("Count of MisMatch 5: "+countOfSamMisMatch5);
			System.out.println("Count of MisMatch 6: "+countOfSamMisMatch6);
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}


}
