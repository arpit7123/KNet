/**
 * 
 */
package edu.asu.nlu.knet.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import module.graph.helper.GraphPassingNode;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * @author Arpit Sharma
 *
 */
public class TextPreprocessor {
	private String conns = ",|then|because|so|in order to|after|but|that|when|to|even though|;|although";
	private ArrayList<String> connsList = null;
	private int sent_words_limit = 30;
	private Pattern parPat = Pattern.compile("(.*)(\\(.*\\))(.*)");
	private Pattern wordPat = Pattern.compile("(.*)(-)([0-9]{1,7})");
	private static TextPreprocessor preprocessor = null;

	static{
		preprocessor = new TextPreprocessor();
	}

	public static TextPreprocessor getInstance(){
		return preprocessor;
	}

	private TextPreprocessor(){
		connsList = populateConns();
	}

	public ArrayList<String> breakParagraph(String paragraph, int sent_words_limit){
		this.sent_words_limit = sent_words_limit;
		return breakParagraph(paragraph);
	}

	public ArrayList<String> breakParagraph(String paragraph){
		ArrayList<String> result = new ArrayList<String>();
		String[] words = paragraph.split(" ");
		int finIndx = words.length/sent_words_limit;
		if(words.length%sent_words_limit!=0){
			finIndx +=1; 
		}
		for(int i=0; i<finIndx; ++i){
			if(words.length-1 >= (i*sent_words_limit)+(sent_words_limit-1)){
				result.add(Joiner.on(" ").join(Arrays.copyOfRange(words, i*sent_words_limit, (i*sent_words_limit)+(sent_words_limit-1))));
			}else{
				result.add(Joiner.on(" ").join(Arrays.copyOfRange(words, i*sent_words_limit, words.length-1)));
			}
		}

		//		boolean wrongWords = false;
		//		for(String word : words){
		//			if(word.length() > words_chars_limit){
		//				wrongWords = true;
		//			}
		//		}
		return result;
	}

	public ArrayList<DiscourseInfo> divideUsingDiscConn(String sent){
		ArrayList<DiscourseInfo> discInfoList = new ArrayList<DiscourseInfo>();
		for(String conn : connsList){
			if(!conn.equals(",") && !conn.equals(";")){
				conn = " "+conn+" ";
			}
			String[] partsOfSent = sent.split(conn);
			if(partsOfSent.length > 1){				
				for(int i=0;i<partsOfSent.length-1;++i){
					DiscourseInfo discInfo = new DiscourseInfo();;
					discInfo.setConn(conn.trim());
					String tmpLeft = Joiner.on(conn).join(Arrays.copyOfRange(partsOfSent, 0, i+1));
					if(tmpLeft.trim().equals("")){
						continue;
					}
					discInfo.setLeftArg(tmpLeft);
					String tmpRight = Joiner.on(conn).join(Arrays.copyOfRange(partsOfSent, i+1, partsOfSent.length));
					if(tmpRight.trim().equals("")){
						continue;
					}
					discInfo.setRightArg(tmpRight);
					discInfoList.add(discInfo);
				}
				//				for(int i = 1; i<=partsOfSent.length-1;++i){
				//					discInfo.setLeftArg(Joiner.on(" ").join(Arrays.copyOfRange(partsOfSent, 0, i)));
				//					discInfo.setRightArg(Joiner.on(" ").join(Arrays.copyOfRange(partsOfSent, i+1, partsOfSent.length)));
				//					discInfoList.add(discInfo);
				//				}
			}
			//			Pattern regExPat = Pattern.compile("(\\b[I|i]f\\b)?(.*)("+ conn +")(.*)");
			//			Matcher m = regExPat.matcher(sent);
			//			if(m.matches()){
			//				discInfo = new DiscourseInfo();
			//				String part1 = m.group(2);
			//				if(m.group(1)!=null){
			//					part1 = "If "+part1;
			//					discInfo.setInitConn("if");
			//				}
			//				discInfo.setConn(m.group(3));
			//				discInfo.setLeftArg(part1);
			//				discInfo.setRightArg(m.group(4));
			//				discInfoList.add(discInfo);
			//			}
		}
		return discInfoList;
	}

	public HashMap<String,ArrayList<String>> prepareKnowledgeSent(GraphPassingNode gpn){
		HashMap<String,ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		//		inputText = removeParentheses(inputText);
		HashMap<String,String> posMap = gpn.getposMap();
		HashMap<String,HashSet<String>> parentMap = new HashMap<String, HashSet<String>>();
		HashMap<String,HashSet<String>> childMap = new HashMap<String, HashSet<String>>();
		ArrayList<String> rdfGraph = gpn.getAspGraph();
		for(String triplet : rdfGraph){
			triplet = triplet.substring(4,triplet.length()-2);
			String[] tmpArr = triplet.split(",");
			if(!tmpArr[1].equalsIgnoreCase("instance_of") 
					&& !tmpArr[1].equalsIgnoreCase("is_subclass_of")
					&& !tmpArr[1].equalsIgnoreCase("semantic_role")){
				if(parentMap.containsKey(tmpArr[2])){
					HashSet<String> parentSet = parentMap.get(tmpArr[2]);
					parentSet.add(tmpArr[0]);
					parentMap.put(tmpArr[2], parentSet);
				}else{
					HashSet<String> parentSet = new HashSet<String>();
					parentSet.add(tmpArr[0]);
					parentMap.put(tmpArr[2], parentSet);
				}

				if(childMap.containsKey(tmpArr[0])){
					HashSet<String> childSet = childMap.get(tmpArr[0]);
					childSet.add(tmpArr[2]);
					childMap.put(tmpArr[0], childSet);
				}else{
					HashSet<String> childSet = new HashSet<String>();
					childSet.add(tmpArr[2]);
					childMap.put(tmpArr[0], childSet);
				}
			}
		}
		
		ArrayList<String> maleNouns = new ArrayList<String>();
		ArrayList<String> neutralNouns = new ArrayList<String>();
		ArrayList<String> femaleNouns = new ArrayList<String>();
		ArrayList<String> malePronouns = new ArrayList<String>();
		ArrayList<String> neutralPronouns = new ArrayList<String>();
		ArrayList<String> femalePronouns = new ArrayList<String>();
		
		for(String word : posMap.keySet()){
			Matcher wordMat = wordPat.matcher(word);
			if(wordMat.matches()){
				String jstWord = wordMat.group(1);
				
				String gender = getGender(jstWord);
				if(gender.equals("male")){
					if(posMap.get(word).startsWith("NNP")){
						maleNouns.add(word);
					}else if(posMap.get(word).equals("PRP")){
						malePronouns.add(word);
					}
				}else if(gender.equals("neutral")){
					if(posMap.get(word).startsWith("NN")){
						neutralNouns.add(word);
					}else if(posMap.get(word).equals("PRP")){
						neutralPronouns.add(word);
					}
				}else if(gender.equals("female")){
					if(posMap.get(word).startsWith("NNP")){
						femaleNouns.add(word);
					}else if(posMap.get(word).equals("PRP")){
						femalePronouns.add(word);
					}
				}
			}
		}
		
		
		if(allAreOne(maleNouns)){
			ArrayList<String> males = new ArrayList<String>();
			males.addAll(maleNouns);
			males.addAll(malePronouns);

			int size = males.size();
			if(size > 0){
				boolean ambiguityFlag = false;
				for(int i=0;i<size;++i){
					for(int j=i+1;j<size;++j){
						if(commonParents(parentMap.get(males.get(i)),parentMap.get(males.get(j)))){
							ambiguityFlag = true;
							break;
						}
					}
				}
				if(!ambiguityFlag){
					result.put("males", males);
				}
			}
		}
		
		if(allAreOne(neutralNouns)){
			ArrayList<String> neutrals = new ArrayList<String>();
			neutrals.addAll(neutralNouns);
			neutrals.addAll(neutralPronouns);

			int size = neutrals.size();
			if(size > 0){
				boolean ambiguityFlag = false;
				for(int i=0;i<size;++i){
					for(int j=i+1;j<size;++j){
						if(commonParents(parentMap.get(neutrals.get(i)),parentMap.get(neutrals.get(j)))){
							ambiguityFlag = true;
							break;
						}
					}
				}
				if(!ambiguityFlag){
					result.put("neutrals", neutrals);
				}
			}
		}
		
		
		if(allAreOne(femaleNouns)){
			ArrayList<String> females = new ArrayList<String>();
			females.addAll(femaleNouns);
			females.addAll(femalePronouns);

			int size = females.size();
			if(size > 0){
				boolean ambiguityFlag = false;
				for(int i=0;i<size;++i){
					for(int j=i+1;j<size;++j){
						if(commonParents(parentMap.get(females.get(i)),parentMap.get(females.get(j)))){
							ambiguityFlag = true;
							break;
						}
					}
				}
				if(!ambiguityFlag){
					result.put("females", females);
				}
			}
		}
		
		return result;
	}
	
	private boolean allAreOne(ArrayList<String> words){
		for(int i=0;i<words.size();++i){
			for(int j=i+1;j<words.size();++j){
				String word1 = words.get(i);
				word1 = word1.substring(0, word1.lastIndexOf("-"));
				
				String word2 = words.get(j);
				word2 = word2.substring(0, word2.lastIndexOf("-"));
				
				char[] word1Chars = word1.toCharArray();
				char[] word2Chars = word2.toCharArray();
				int numOfSameChars = 0;
				for(int k=0;k<word1Chars.length;++k){
					if(k < word2Chars.length){
						if(word1Chars[k]==word2Chars[k]){
							numOfSameChars+=1;
						}
					}
				}
				
				int totalChars = word1Chars.length;
				if(totalChars<word2Chars.length){
					totalChars = word2Chars.length;
				}
				
				if((double)(numOfSameChars/totalChars)<0.9){
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean commonParents(HashSet<String> anchorPars, HashSet<String> currentPars){
		for(String anchorPar : anchorPars){
			for(String currentPar : currentPars){
				if(anchorPar.equals(currentPar)){
					return true;
				}
			}
		}
		return false;
	}

	private String getGender(String word){
		Object gender = null;
		String apiUrl = "https://api.genderize.io/?name="+word;
		try{
			URL url = new URL(apiUrl);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			while((line=br.readLine())!=null){
				org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
				org.json.simple.JSONObject obj = (org.json.simple.JSONObject)parser.parse(line);
				gender = obj.get("gender");
			}
			
		}catch(IOException | org.json.simple.parser.ParseException e){
			gender = "neutral";
			System.err.println("Error Occurred in gender api");
		}
		if(gender!=null){
			return (String)gender;
		}
		return "neutral";
	}
	
	public String removeParentheses(String inputText){
		Matcher m = parPat.matcher(inputText);
		if(m.matches()){
			inputText = m.group(1).trim() + " " + m.group(3).trim();
			inputText = removeParentheses(inputText);
		}
		return inputText;
	}

	public static void main(String[] args){
		TextPreprocessor prep = new TextPreprocessor();
		//		String s = "If something happened then we will take it.";
		//		s = ", came over and gave him a hug, and I knew that he wanted that crown more than anything,” he told WFAA";
		//		System.out.println(s);
		//		ArrayList<DiscourseInfo> info = prep.divideUsingDiscConn(s);
		//		for(DiscourseInfo d : info){
		//			System.out.println(d.toString());
		//		}

//		String testStr = "Arpit (sharma) is trying his hardest ().";
//		testStr = prep.removeParentheses(testStr);
//		System.out.println(testStr);

		String testWord = "because";
		String out = prep.getGender(testWord);
		System.out.println(out);
		
		//		Pattern regExPat = Pattern.compile("(\\b[I|i]f\\b)?(.*)("+ "because" +")(.*)");
		//		Matcher m = regExPat.matcher(s);
		//		if(m.matches()){
		//			if(m.group(1)==null){
		//				System.out.println("group1 is null");
		//			}else{
		//				System.out.println("group1: "+ m.group(1));
		//			}
		//			System.out.println("group2: "+m.group(2));
		//			System.out.println("group3: "+m.group(3));
		//			System.out.println("group4: "+m.group(4));
		//		}
		System.exit(0);
	}

	private ArrayList<String> populateConns(){
		ArrayList<String> result = Lists.newArrayList(conns.split("\\|"));
		return result;
	}

}
