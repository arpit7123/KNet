/**
 * 
 */
package edu.asu.nlu.knet.extra;

/**
 * @author arpit
 *
 */

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ReadPropbankVerbs {

	public static void main(String[] args){
		ReadPropbankVerbs rpv = new ReadPropbankVerbs();
		
		String verbsListFile = "propbankVerbsList.txt";
		String connFile = "conjunctions.txt";
		//		String url = "http://verbs.colorado.edu/propbank/framesets-english/";
		//		
		//		HashSet<String> verbs = getFromGoogleJsoup(url);
		//		
		//		try(BufferedWriter bw = new BufferedWriter(new FileWriter(verbsListFile))){
		//			for(String s : verbs){
		//				System.out.println(s);
		//				bw.append(s);
		//				bw.newLine();
		//			}
		//		}catch(IOException e){
		//			e.printStackTrace();
		//		}

		HashSet<String> conns = new HashSet<String>();
		HashSet<String> verbsList = new HashSet<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(connFile));
				BufferedReader verbs = new BufferedReader(new FileReader(verbsListFile))){
			String line = null;
			while((line=br.readLine())!=null){
				conns.add(line.split("\t")[0].trim());
			}
			
			String line1 = null;
			while((line1=verbs.readLine())!=null){
				verbsList.add(line1);
			}
			
			br.close();
			verbs.close();
		}catch(IOException e){
			e.printStackTrace();
		}

		rpv.createQueries(verbsList, conns, "queriesFile");

	}
	
	
	public void createQueries(HashSet<String> verbsList,HashSet<String> connectives, String outQueriesFile){
		try(BufferedWriter out = new BufferedWriter(new FileWriter(outQueriesFile))){
			HashSet<String> duplicateVerbs = new HashSet<String>(verbsList);
			for(String v1 : verbsList){
				for(String v2 : duplicateVerbs){
					for(String conn : connectives){
						out.append(".*"+v1+".*"+conn+".*"+v2+".*");
						out.newLine();
					}
				}
			}
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	

	public static HashSet<String> getFromGoogleJsoup(String url){
		HashSet<String> result = new HashSet<String>();
		try{
			Pattern p = Pattern.compile("(.*)(-)(v)(\\.html)");
			Document doc = Jsoup.connect(url).get();
			Elements elements = doc.select("a");
			for(Element e : elements){
				String attribute = e.attr("href");
				Matcher m = p.matcher(attribute);
				if(m.matches()){
					String vrb = m.group(1);
					if(!vrb.equalsIgnoreCase("fedex") 
							&& !vrb.equalsIgnoreCase("lol") 
							&& !vrb.equalsIgnoreCase("od")
							&& !vrb.equalsIgnoreCase("rofl")
							&& !vrb.equalsIgnoreCase("ups")){
						result.add(m.group(1));	
					}						
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unused")
	public int getNumberOfLines(String fileName){
		int result = 0;
		try(BufferedReader bw = new BufferedReader(new FileReader(fileName))){
			String line = null;
			while((line=bw.readLine())!=null){
				result++;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}
	
}
