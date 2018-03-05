/**
 * 
 */
package edu.asu.nlu.knet.extractor;
/**
 * This class implements an algorithm to create a semantic graph of the given English sentence.
 * The graph is returned in RDF style has triplets, i.e., <i>has(X,R,Y)</i>, where X is the start node,
 * Y is the end node and R is the semantic relation between them.
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import module.SentenceToGraphInterface;
import module.graph.dependencymapping.MappingHandlerOutput;
import module.graph.helper.ClassesResource;
import module.graph.helper.GraphPassingNode;
import module.graph.helper.NEObject;
import module.graph.questions.PreprocessQuestions;
import module.graph.questions.ProcessedQuestion;
import module.graph.questions.QuestionType;
import module.graph.resources.CorefResolution;
import module.graph.resources.EventRelationsExtractor;
import module.graph.resources.InputDependencies;
import module.graph.resources.NamedEntityTagger;
import module.graph.resources.PrepositionSenseResolver;
import module.graph.resources.Preprocessor;
import module.graph.pdtbp.resources.PDTBResource;
import module.graph.resources.ResourceHandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.stanford.nlp.trees.Tree;

public class LocalKparser implements SentenceToGraphInterface{
	private static final int words_chars_limit = 40;
	
	public ResourceHandler resourceHandler;

	//	private String questionSym = "q_";

	private Pattern p = Pattern.compile("([^,]*)(,)([^,]*)(,)(.*)");

	private HashMap<String,String> posMap = new HashMap<String,String>();
	private HashMap<String,String> eventOrderMap = new HashMap<String,String>();
	private HashMap<String,HashMap<String,String>> mapOfDependencies = new HashMap<String,HashMap<String,String>>();
	private HashMap<String,ArrayList<String>> mapOfDeps = new HashMap<String,ArrayList<String>>();
	private ArrayList<String> finalList = new ArrayList<String>();
	private Map<String,Boolean> protoTypicalWords = new HashMap<String,Boolean>();
	private HashMap<String,ArrayList<String>> wordSenseMap = new HashMap<String,ArrayList<String>>();
	private ClassesResource conceptualClassResource = null;

	private HashMap<String, String> stringToNamedEntityMap = new HashMap<String, String>(4);
	private HashMap<String, String> pronominalCoreferenceMap = new HashMap<String, String>(4);

	//Map of Multi-Word Expressions
	private HashMap<String, String> mweMap = new HashMap<String, String>(4);

	private EventRelationsExtractor evRelExt = null;
	private PDTBResource pdtbInstance = null;
	private PreprocessQuestions preprocessQues = null;
	private Preprocessor preprocessorInstance = null;

	private boolean backGroundFlag;
	private boolean wordSenseFlag;
	private boolean useCoreferenceResolution;

	/**
	 * This is the constructor for this class. It is used to initialize the instances
	 * of:
	 * 1. module.graph.resources.ResourceHandler class, which initialized various other 
	 * resources such as syntactic parser and weka classifier for Preposition Sense Disambiguation.
	 * 
	 * 2. module.graph.questions.PreprocessQuestions class, which initializes resources needed for
	 * parsing different types of questions.
	 * 
	 * 4. module.graph.resources.PDTBResource class, which initializes resources for
	 * Penn Discourse Tree Bank style discourse parsing. 
	 * 
	 * 3. module.graph.resources.EventRelationsExtractor class, which implements the algorithm to 
	 * find relations between event nodes in output graph. This is used if discourse parsing does not
	 * find any event-event relations.
	 */
	public LocalKparser(){
		resourceHandler = ResourceHandler.getInstance();
		preprocessorInstance = Preprocessor.getInstance();
		preprocessQues = PreprocessQuestions.getInstance();
		pdtbInstance = PDTBResource.getInstance();
		evRelExt = EventRelationsExtractor.getInstance();
	}

	/**
	 * This is the main class used to test the basic functionality of this class.
	 * @param args empty array of strings
	 */
	public static void main(String[] args){
		long startTime = System.currentTimeMillis();
		LocalKparser stg = new LocalKparser();
		ArrayList<String> slist = new ArrayList<String>();
		slist.add("Belize_Benin_Bermuda_Bhutan_Bolivia_Bosnia-Herzegovina_Botswana_Brazil_Brunei_Darussalam_Bulgaria_Burkina_Faso_Burundi_Cambodia_Cameroon_Canada Cape_Verde_Cayman_Islands_Central_African_Republic_Chad_Chile_China_Colombia_Comoros");
		int count = 1;
		for(String sent : slist){
			if(count==1){
				startTime = System.currentTimeMillis();
			}
			GraphPassingNode gpn2 = stg.extractGraph(sent,false,true,false);
			ArrayList<String> list = gpn2.getAspGraph();
			for(String s : list){
				System.out.println(s);
			}
			System.out.println("*********************************");
			count++;
		}

		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime));
		System.exit(0);
	}

	/**
	 * This method is used to extract the graph of an input sentence.
	 * @param sentence it is the input sentence.
	 * @param bgFlag it is the boolean variable used specify if you would like to add 100 to the index of each word in the input sentence.
	 * @param wsFlag it is the boolean variable used specify if you would like to perform word sense disambiguation.
	 * @return GraphPassingNode it is an instance of module.graph.helper.GraphPassingNode
	 */
	public GraphPassingNode extractGraph(String sentence, boolean bgFlag, boolean wsFlag){
		return this.extractGraph(sentence, bgFlag, wsFlag, false);
	}

	/**
	 * This method is used to extract the graph of an input sentence.
	 * @param sentence it is the input sentence.
	 * @param bgFlag it is the boolean variable used specify if you would like to add 100 to the index of each word in the input sentence.
	 * @param wsFlag it is the boolean variable used specify if you would like to perform word sense disambiguation on the words in the input sentence.
	 * @param useCoreference it is the boolean variable used specify if you would like to perform co-reference resolution on the entities in the input sentence.
	 * @return GraphPassingNode it is an instance of module.graph.helper.GraphPassingNode
	 */
	public GraphPassingNode extractGraph(String sentence, boolean bgFlag, boolean wsFlag, boolean useCoreference){
		return extractGraph(sentence,bgFlag,wsFlag,useCoreference,0);
	}

	/**
	 * This method is used to extract the graph of an input sentence.
	 * @param sentence it is the input sentence.
	 * @param bgFlag it is the boolean variable used specify if you would like to add 100 to the index of each word in the input sentence.
	 * @param wsFlag it is the boolean variable used specify if you would like to perform word sense disambiguation on the words in the input sentence.
	 * @param useCoreference it is the boolean variable used specify if you would like to perform co-reference resolution on the entities in the input sentence.
	 * @param index it is the number that will be added to the index of each word in the input sentence in case the background flag (bgflag) is true.
	 * @return GraphPassingNode it is an instance of module.graph.helper.GraphPassingNode
	 */
	public GraphPassingNode extractGraph(String sentence, boolean bgFlag, boolean wsFlag, boolean useCoreference, int index){
		wordSenseFlag = wsFlag;
		backGroundFlag = bgFlag;
		useCoreferenceResolution = useCoreference;

		finalList.clear();
		wordSenseMap.clear();
		pronominalCoreferenceMap.clear();

		if(sentence.equalsIgnoreCase("")){
			return null;
		}

		mweMap.clear();
		sentence = tagNamedEntities(sentence);
		System.out.println("NE Tagged Sentence:" + sentence);

		sentence = preprocessorInstance.preprocessText(sentence,resourceHandler,mweMap,stringToNamedEntityMap);
		System.out.println("Preprocessed Sentence:" + sentence);
		
		boolean wrongWords = false;
		String[] words = sentence.split(" ");
		for(String word : words){
			if(word.length() > words_chars_limit){
				wrongWords = true;
			}
		}
		if(wrongWords){
			return new GraphPassingNode(finalList, posMap, sentence, wordSenseMap);
		}
		
		if((sentence.substring(sentence.length()-1).equalsIgnoreCase("?"))){
			extractFromQuestion(sentence, bgFlag, wsFlag, useCoreference,index);
		}else{
			extractFromSentence(sentence, bgFlag, wsFlag, useCoreference,index);
			HashSet<String> markedSet = new HashSet<String>();
			ArrayList<String> list = Lists.newArrayList();
			for(String s : finalList){
				Matcher m = p.matcher(s);
				if(m.find()){
					String arg1 = m.group(1);
					String rel = m.group(3);
					String arg2 = m.group(5);

					if(!arg2.startsWith("the-") && !arg2.startsWith("The-")
							&& !arg2.startsWith("an-") && !arg2.startsWith("An-")
							&& !arg2.startsWith("a-") && !arg2.startsWith("A-")
							&& !markedSet.contains(s)){
						if(!rel.equalsIgnoreCase("instance_of")
								&&!rel.equalsIgnoreCase("is_subclass_of")){
							if(mweMap.containsKey(arg1)){ 
								arg1 = mweMap.get(arg1)+"-"+arg1.substring(arg1.indexOf("-")+1);
							}
							if(mweMap.containsKey(arg2)){ 
								arg2 = mweMap.get(arg2)+"-"+arg2.substring(arg2.indexOf("-")+1);
							}
						}else{
							
						}
						list.add("has("+arg1+","+rel+","+arg2+").");//.replaceAll("-", "_")+").");//System.out.println("has("+s+").");
					}else{
						markedSet.add(arg2+",instance_of,"+arg2.substring(0, arg2.indexOf("-")));
					}
				}
			}
			finalList.clear();
			finalList.addAll(list);
		}

		if(finalList.size()==0){
			extractEntities();
		}

		GraphPassingNode result = new GraphPassingNode(finalList, posMap, sentence, wordSenseMap);
		result.setConClassRes(this.conceptualClassResource);

		return result;
	}
	
	public String preprocessText(String inputText){
		inputText = tagNamedEntities(inputText);
		System.out.println("NE Tagged Sentence:" + inputText);

		inputText = preprocessorInstance.preprocessText(inputText,resourceHandler,mweMap,stringToNamedEntityMap);
		System.out.println("Preprocessed Sentence:" + inputText);
		return inputText;
	}
	
	/**
	 * This method is used to extract the graph of an input sentence.
	 * @param sentence it is the input sentence.
	 * @param bgFlag it is the boolean variable used specify if you would like to add 100 to the index of each word in the input sentence.
	 * @param wsFlag it is the boolean variable used specify if you would like to perform word sense disambiguation on the words in the input sentence.
	 * @param useCoreference it is the boolean variable used specify if you would like to perform co-reference resolution on the entities in the input sentence.
	 * @param index it is the number that will be added to the index of each word in the input sentence in case the background flag (bgflag) is true.
	 * @return GraphPassingNode it is an instance of module.graph.helper.GraphPassingNode
	 */
	public GraphPassingNode extractGraphWithoutPrep(String sentence, boolean bgFlag, boolean wsFlag, boolean useCoreference, int index){
		wordSenseFlag = wsFlag;
		backGroundFlag = bgFlag;
		useCoreferenceResolution = useCoreference;

		finalList.clear();
		wordSenseMap.clear();
		pronominalCoreferenceMap.clear();

		if(sentence.equalsIgnoreCase("")){
			return null;
		}

		mweMap.clear();
		
		boolean wrongWords = false;
		String[] words = sentence.split(" ");
		for(String word : words){
			if(word.length() > words_chars_limit){
				wrongWords = true;
			}
		}
		if(wrongWords){
			return new GraphPassingNode(finalList, posMap, sentence, wordSenseMap);
		}
		
		if((sentence.substring(sentence.length()-1).equalsIgnoreCase("?"))){
			extractFromQuestion(sentence, bgFlag, wsFlag, useCoreference,index);
		}else{
			extractFromSentence(sentence, bgFlag, wsFlag, useCoreference,index);
			HashSet<String> markedSet = new HashSet<String>();
			ArrayList<String> list = Lists.newArrayList();
			for(String s : finalList){
				Matcher m = p.matcher(s);
				if(m.find()){
					String arg1 = m.group(1);
					String rel = m.group(3);
					String arg2 = m.group(5);

					if(!arg2.startsWith("the-") && !arg2.startsWith("The-")
							&& !arg2.startsWith("an-") && !arg2.startsWith("An-")
							&& !arg2.startsWith("a-") && !arg2.startsWith("A-")
							&& !markedSet.contains(s)){
						if(!rel.equalsIgnoreCase("instance_of")
								&&!rel.equalsIgnoreCase("is_subclass_of")){
							if(mweMap.containsKey(arg1)){ 
								arg1 = mweMap.get(arg1)+"-"+arg1.substring(arg1.indexOf("-")+1);
							}
							if(mweMap.containsKey(arg2)){ 
								arg2 = mweMap.get(arg2)+"-"+arg2.substring(arg2.indexOf("-")+1);
							}
						}else{
							
						}
						list.add("has("+arg1+","+rel+","+arg2+").");//.replaceAll("-", "_")+").");//System.out.println("has("+s+").");
					}else{
						markedSet.add(arg2+",instance_of,"+arg2.substring(0, arg2.indexOf("-")));
					}
				}
			}
			finalList.clear();
			finalList.addAll(list);
		}

		if(finalList.size()==0){
			extractEntities();
		}

		GraphPassingNode result = new GraphPassingNode(finalList, posMap, sentence, wordSenseMap);
		result.setConClassRes(this.conceptualClassResource);

		return result;
	}

	/**
	 * This method is used to extract the graph of an input question after initial processing in other methods.
	 * The final graph in RDF style <i>has(X,R,Y)</i> triplets is saved in global array list. 
	 * @param sentence it is the input sentence.
	 * @param bgFlag it is the boolean variable used specify if you would like to add 100 to the index of each word in the input sentence.
	 * @param wsFlag it is the boolean variable used specify if you would like to perform word sense disambiguation on the words in the input sentence.
	 * @param useCoreference it is the boolean variable used specify if you would like to perform co-reference resolution on the entities in the input sentence.
	 */
	private void extractFromQuestion(String question, boolean bgFlag, boolean wsFlag, boolean useCoreference, int index){
		Tree t = (Tree) resourceHandler.getSyntacticTree(question);

		ProcessedQuestion proQues = preprocessQues.preprocessQuestion(question, t);
		String sent = proQues.getProcessedText();
		extractFromSentence(sent, bgFlag, wsFlag, useCoreference, index);
		if(proQues.getType().equals(QuestionType.YES_NO)){
			ArrayList<String> newList = Lists.newArrayList();
			for(String s : finalList){
				Matcher m = p.matcher(s);
				if(m.find()){
					String arg1 = m.group(1);
					//					String rel = m.group(3);
					String arg2 = m.group(5);
					if(!arg2.startsWith("the-") && !arg2.startsWith("The-")
							&& !arg2.startsWith("an-") && !arg2.startsWith("An-")
							&& !arg2.startsWith("a-") && !arg2.startsWith("A-")){
						newList.add("has("+s+").");
						if(arg1.equalsIgnoreCase(proQues.getNextWord() + "-1")){
							if(posMap.containsKey(arg2)){
								if(posMap.get(arg2).startsWith("V")){
									newList.add("has("+arg2+",question,true-false).");
									posMap.put("true-false", "NN");
								}
							}
						}else if(arg2.equalsIgnoreCase(proQues.getNextWord() + "-" + String.valueOf(proQues.getNextWordPosition()))){
							if(posMap.containsKey(arg2)){
								if(posMap.get(arg1).startsWith("V")){
									newList.add("has("+arg1+",question,true-false).");
									posMap.put("true-false", "NN");
								}
							}
						}
					}
				}
			}
			finalList.clear();
			finalList.addAll(newList);
		}else if(proQues.getType().equals(QuestionType.WH)){
			ArrayList<String> newList = Lists.newArrayList();
			for(String s : finalList){
				Matcher m = p.matcher(s);
				if(m.find()){
					String arg1 = m.group(1);
					String rel = m.group(3);
					String arg2 = m.group(5);
					if(!arg2.startsWith("the-") && !arg2.startsWith("The-")
							&& !arg2.startsWith("an-") && !arg2.startsWith("An-")
							&& !arg2.startsWith("a-") && !arg2.startsWith("A-")){
						if(!s.startsWith(proQues.getPlaceholder()+"-1,instance_of") 
								&& !s.startsWith(proQues.getPlaceholder()+",is_subclass_of,")
								&& !s.startsWith(proQues.getPlaceholder()+"-1,semantic_role")){
							if(arg1.equalsIgnoreCase(proQues.getPlaceholder()+"-1")){
								newList.add("has(?-1,"+rel+","+arg2+").");
								newList.add("has(?-1,instance_of,?).");
								newList.add("has(?,is_subclass_of,"+proQues.getWordSense()+").");
								posMap.put("?-1", "NN");
								posMap.put("?", "NN");
							}else if(arg2.equalsIgnoreCase(proQues.getPlaceholder()+"-1")){
								newList.add("has("+arg1+","+rel+",?-1).");
								newList.add("has(?-1,instance_of,?).");
								newList.add("has(?,is_subclass_of,"+proQues.getWordSense()+").");
								posMap.put("?-1", "NN");
								posMap.put("?", "NN");
							}else{
								newList.add("has("+s+").");
							}
						}else if(s.startsWith(proQues.getPlaceholder()+"-1,semantic_role")){
							newList.add("has(?-1,"+rel+","+arg2+").");
						}
					}
				}
			}
			finalList.clear();
			finalList.addAll(newList);
		}else if(proQues.getType().equals(QuestionType.TAG)){
			String firstEvent = eventOrderMap.get("e1");
			ArrayList<String> newList = Lists.newArrayList();
			for(String s : finalList){
				Matcher m = p.matcher(s);
				if(m.find()){
					String arg1 = m.group(1);
					String rel = m.group(3);
					String arg2 = m.group(5);
					if(!arg2.startsWith("the-") && !arg2.startsWith("The-")
							&& !arg2.startsWith("an-") && !arg2.startsWith("An-")
							&& !arg2.startsWith("a-") && !arg2.startsWith("A-")){
						newList.add("has("+s+").");
						if(!rel.equalsIgnoreCase("instance_of") && !rel.equalsIgnoreCase("is_subclass_of")){
							if(arg1.equalsIgnoreCase(firstEvent)){
								newList.add("has("+arg1+",question,true-false).");
								posMap.put("true-false", "NN");
							}else if(arg2.equalsIgnoreCase(firstEvent)){
								newList.add("has("+arg2+",question,true-false).");
								posMap.put("true-false", "NN");
							}
						}
					}
				}
			}
			finalList.clear();
			finalList.addAll(newList);
		}
	}

	/**
	 * This method is used to extract the graph of an input sentence after initial processing in other methods.
	 * The final graph in RDF style <i>has(X,R,Y)</i> triplets is saved in global array list. 
	 * @param sentence it is the input sentence.
	 * @param bgFlag it is the boolean variable used specify if you would like to add 100 to the index of each word in the input sentence.
	 * @param wsFlag it is the boolean variable used specify if you would like to perform word sense disambiguation on the words in the input sentence.
	 * @param useCoreference it is the boolean variable used specify if you would like to perform co-reference resolution on the entities in the input sentence.
	 */
	private void extractFromSentence(String inputText, boolean bgFlag, boolean wsFlag, boolean useCoreference,int index){	
		///Plugging in the NamedEntityTagger
		//		String inputText = tagNamedEntities(sentence);

		InputDependencies inDep = resourceHandler.getSyntacticDeps(inputText, this.backGroundFlag, index);
		if(inDep==null){
			return;
		}
		mapOfDependencies = inDep.getMapOfDependencies();
		mapOfDeps = inDep.getMapOfDeps();
		posMap = inDep.getPosMap();
		eventOrderMap = inDep.getEventOrderMap();

		if(this.wordSenseFlag){
			if(inputText.charAt(inputText.length()-1)=='.'){
				wordSenseMap = resourceHandler.getWordSenses(inputText.substring(0,inputText.length()-1),index);
			}else{
				wordSenseMap = resourceHandler.getWordSenses(inputText,index);
			}
		}else{
			wordSenseMap = new HashMap<String,ArrayList<String>>();
		}

		protoTypicalWords = extractPrototypicalWords();


		ClassesResource cr = extractClasses();
		HashMap<String,String> mapOfSuperClasses = cr.getSuperclassesMap();
		//		mapOfSuperClasses.put("Wednesday", "date");
		//		mapOfSuperClasses.put("6am", "time");
		//		posMap.put("Wednesday", "NN");
		//		posMap.put("6am", "NN");
		inDep.setMapOfSuperClasses(mapOfSuperClasses);

		this.conceptualClassResource = cr;

		/**		Code to use the Preposition Sense Disambiguation module to find correct
		 * 		semantic relations due to prepositions in the input text */
		//				HashSet<String> flaggedDeps = mappingOutput.getFlaggedDeps();
		//		HashSet<String> flaggedDeps = 
		classifyPrepositions(mapOfSuperClasses);
		//		inDep.setFlaggedDeps(flaggedDeps);
		//				divide(inputText);
		//////////////////////////////////////////////////////////////////////////////////

		MappingHandlerOutput mappingOutput = resourceHandler.getSemanticMapping(inDep);

		ArrayList<String> tempList =Lists.newArrayList(mappingOutput.getMappings()); 
		finalList.addAll(tempList);
		posMap.putAll(mappingOutput.getPosMap());

		for(String s : cr.getClassesMap().keySet()){
			int endIndex = s.length()-1;
			if(s.contains("-")){
				endIndex = s.indexOf("-");
			}
			//			if(!resourceHandler.isAnArticle(s.substring(0, endIndex))){
			try{
				if(!s.substring(0, endIndex).equalsIgnoreCase("a")
						&& !s.substring(0, endIndex).equalsIgnoreCase("an")
						&& !s.substring(0, endIndex).equalsIgnoreCase("the")){
					if(mapOfSuperClasses.containsKey(s)){
						if(protoTypicalWords.containsKey(s)){
							if(protoTypicalWords.get(s)){
								finalList.add(s+",prototype_of,"+cr.getClassesMap().get(s));
							}else{
								finalList.add(s+",instance_of,"+cr.getClassesMap().get(s));
							}
						}else{
							finalList.add(s+",instance_of,"+cr.getClassesMap().get(s));
						}
						if(mapOfSuperClasses.get(s)!=null){
							finalList.add(cr.getClassesMap().get(s)+",is_subclass_of,"+mapOfSuperClasses.get(s));
						}
					}
				}
			}catch(Exception e){
				System.err.println("Error Occurred !!!");
			}
		}

		if(this.useCoreferenceResolution){
			pronominalCoreferenceMap.clear();
			CorefResolution corefInstance = CorefResolution.getInstance();
			pronominalCoreferenceMap = corefInstance.extractCoreferenceChain(inputText);
		}


		/**
		 * Code to call Penn Discourse Treebank style parser to find discourse connectives
		 * and map them to appropriate semantic relations
		 */
//		************************************************************
//		String depTree = "";
//		for(String s : mapOfDependencies.keySet()){
//			HashMap<String,String> temp = mapOfDependencies.get(s);
//			for(String s1 : temp.keySet()){
//				depTree += s + "(" + s1 +"," + temp.get(s1) + ")\n";
//			}
//		}
//
//		//		depTree = depTree.substring(0,depTree.length()-1);
//		depTree = "";
//
//		Tree t = inDep.getPennTree();
//
//		String pennTree = t.firstChild().toString();
//		pennTree = pennTree.substring(1,pennTree.length()-1);
//		pennTree = t.toString();
//
//		pdtbInstance.setResHandler(resourceHandler);
//		pdtbInstance.setPosMap(posMap);
//		ArrayList<String> eventRelations = new ArrayList<String>();
//		try{
//			eventRelations = pdtbInstance.getEventRelations(inputText,pennTree,depTree,index);
//		}catch(Exception e){
//			System.err.println("Error in discourse parsing");
//		}
//		finalList.addAll(eventRelations);
//		************************************************************
//
//		if(eventRelations.size()==0){
//			evRelExt.setResHandler(resourceHandler);
//			finalList.addAll(evRelExt.divide(inputText, inDep));
//		}
//		************************************************************

		extractSemanticRoles();
	}

	/**
	 * This private method is used to tag the named entities in the input sentence/question.
	 * @param sentence it is the input sentence/question.
	 * @return java.lang.String it is the modified sentence in which the named entities with multiple words
	 * are combined into one word by using underscore (_). Also, a global map of named entities is updated.
	 */
	private String tagNamedEntities(String sentence) {
		stringToNamedEntityMap.clear();
		NEObject namedEntityTagger = NamedEntityTagger.tagNamedEntities(sentence);

		// sentence will now have New York City as New_York_City
		sentence = namedEntityTagger.getModifiedText();
		// map will contain (New_York_City, LOCATION) as its class
		stringToNamedEntityMap = namedEntityTagger.getNamedEntityMap();

		return sentence;
	}

	/**
	 * This private method is used to find the semantic relations among words in the input sentence caused
	 * because of prepositions in the sentence. A classification based approach is used for this. 
	 * A separate trained classifier model is used for each preposition.
	 * @param mapOfSuperclasses it is a map of words in the input sentence and their conceptual classes.
	 * @return This method updates the global list of <i>has(X,R,Y)</i> triplets.
	 */
	private HashSet<String> classifyPrepositions(HashMap<String,String> mapOfSuperclasses){
		HashSet<String> flaggedDeps = Sets.newHashSet();
		Instances classifierInstStr =  PrepositionSenseResolver.getArffStructure();

		//		System.out.println(classifierInstStr.toString());
		for(String dep : mapOfDeps.keySet()){
			//			String dependency = null;
			//			if(dep.startsWith("prep")){
			//				dependency = dep;
			//			}else if(dep.equalsIgnoreCase("agent")){
			//				dependency = "prep_by";
			//			}
			if(dep.startsWith("prep_")){
				String classes = resourceHandler.getPrepClasses(dep);
				if(classes!=null){
					FastVector attributes = new FastVector();
					if(classes.contains(",")){
						String[] temp = classes.split(",");
						for(String cls : temp){
							attributes.addElement(cls);
						}
					}else{
						attributes.addElement(classes);
					}

					// Create nominal attribute "position" 
					Attribute classAttr = new Attribute("class", attributes);

					classifierInstStr.insertAttributeAt(classAttr, 8);

					Classifier classifier = resourceHandler.getWekaClassifier(dep);
					if(classifier!=null){
						//				Classifier classifier = resourceHandler.getWekaClassifier(dependency);
//						HashMap<String,String> tempMap = mapOfDependencies.get(dep);
						ArrayList<String> tempList = mapOfDeps.get(dep);
						for(String args : tempList){
//						for(String word1 : tempMap.keySet()){
//							String word2 = tempMap.get(word1);
							String[] words = args.split("#");
							if(words.length != 2){
								continue;
							}
							String word1 = words[0];
							String word2 = words[1];
							Instances unlabeled = new Instances(classifierInstStr);
							double[] vals;
							// 3. fill with data
							// first instance
							vals = new double[unlabeled.numAttributes()];
							// - nominal
							vals[0] = unlabeled.attribute(0).indexOfValue(getGeneralPOS(posMap.get(word1)));
							// - nominal
							vals[1] = unlabeled.attribute(1).indexOfValue(getSpecificPOS(posMap.get(word1)));
							// - nominal
							vals[2] = unlabeled.attribute(2).indexOfValue(getGeneralPOS(posMap.get(word2)));
							// - nominal
							vals[3] = unlabeled.attribute(3).indexOfValue(getSpecificPOS(posMap.get(word2)));
							// - nominal
							String superClass1 = mapOfSuperclasses.get(word1);
							if(superClass1==null){
								vals[4] = unlabeled.attribute(4).indexOfValue("all");
							}else{
								vals[4] = unlabeled.attribute(4).indexOfValue(superClass1);
							}
							// - nominal
							String superClass2 = mapOfSuperclasses.get(word2);
							if(superClass2==null){
								vals[5] = unlabeled.attribute(5).indexOfValue("all");
							}else{
								vals[5] = unlabeled.attribute(5).indexOfValue(superClass2);
							}

							unlabeled.add(new Instance(1.0, vals));

							unlabeled.instance(0).setValue(6, resourceHandler.getFeature7Value("*"+word1.substring(0,word1.lastIndexOf("-"))+"*"+word2.substring(0,word2.lastIndexOf("-"))+"*"));
							unlabeled.instance(0).setValue(7, resourceHandler.getFeature8Value(mapOfSuperclasses.get(word2)+":"+dep.substring(dep.indexOf("_")+1)));

							// set class attribute
							int classIndex = unlabeled.numAttributes()-1;
							unlabeled.setClassIndex(classIndex);
							try{
								double clsLabel = classifier.classifyInstance(unlabeled.instance(0));
								String label = unlabeled.attribute(classIndex).value((int)clsLabel);
								finalList.add(word1 +","+ label +","+ word2);
								flaggedDeps.add(dep+";"+word1+";"+word2);
							}catch(Exception e){
								System.err.println("Error in classifying the preposition sense.");
							}
						}
					}
					classifierInstStr.deleteAttributeAt(8);
				}
			}
		}
		return flaggedDeps;
	}

	/**
	 * This private method is used to get the general POS tags from specific ones.
	 * @param pos it is a specific POS tag. For example, <i>VBD</i>.
	 * @return java.lang.String it is the general POS tag. For example, <i>verb</i>
	 */
	private String getGeneralPOS(String pos){
		if(pos==null){
			return "useless";
		}
		
		if(pos.startsWith("V")){
			return "verb";
		}else if(pos.startsWith("N")){
			return "noun";
		}else if(pos.startsWith("R")){
			return "adverb";
		}else if(pos.startsWith("J")){
			return "adjective";
		}else{
			return "useless";
		}
	}
	
	private String getSpecificPOS(String pos){
		if(pos==null){
			return "useless";
		}
		return pos;
	}

	/**
	 * This is a private method to mark the words in the sentence which are prototypes of their classes.
	 * @return HashMap<String,Boolean>
	 */
	private HashMap<String,Boolean> extractPrototypicalWords(){
		HashMap<String,Boolean> result = Maps.newHashMap();
		if(mapOfDependencies.containsKey("det")){
			HashMap<String,String> tempMap = mapOfDependencies.get("det");
			for(String key : tempMap.keySet()){
				String quant = tempMap.get(key).substring(0, tempMap.get(key).lastIndexOf('-')).trim();
				if("all".equalsIgnoreCase(quant)||
						"every".equalsIgnoreCase(quant)||
						"any".equalsIgnoreCase(quant)||
						"each".equalsIgnoreCase(quant)){
					result.put(key, true);
				}
			}
		}
		if(mapOfDependencies.containsKey("predet")){
			HashMap<String,String> tempMap = mapOfDependencies.get("predet");
			for(String key : tempMap.keySet()){
				String quant = tempMap.get(key).substring(0, tempMap.get(key).lastIndexOf('-')).trim();
				if("all".equalsIgnoreCase(quant)||
						"every".equalsIgnoreCase(quant)||
						"any".equalsIgnoreCase(quant)||
						"each".equalsIgnoreCase(quant)){
					result.put(key, true);
				}
			}
		}
		return result;
	}

	/**
	 * This method extracts the semantic roles of entities if there are any.
	 */
	private void extractSemanticRoles(){
		ArrayList<String> srlList = Lists.newArrayList();

		for(String str : finalList){
			Matcher m = p.matcher(str);
			if(m.find()){
				String tempVerb = m.group(1);
				if(tempVerb.contains("-")){
					String tmp1 = tempVerb.substring(0, tempVerb.lastIndexOf("-"));
					String tmp2 = tempVerb.substring(tempVerb.lastIndexOf("-")+1);
					if(pronominalCoreferenceMap.containsKey(tmp1+"_"+tmp2)){
						srlList.add(tempVerb+",has_coreferent,"+pronominalCoreferenceMap.get(tmp1+"_"+tmp2));
					}
					if(!m.group(3).equalsIgnoreCase("instance_of")){
						tempVerb = tempVerb.substring(0,tempVerb.lastIndexOf("-"));
						String role = null;
						if((role=resourceHandler.getSemanticRoleLabel(resourceHandler.getBaseFormOfWord(tempVerb, "v"), m.group(3)))!=null){
							srlList.add(m.group(5)+",semantic_role,"+":"+role);
						}
					}
				}
			}
		}
		finalList.addAll(srlList);
	}

	/**
	 * This method extracts entities from the input sentence in case 
	 * semantic graph is empty at the end. 
	 */
	private void extractEntities(){
		ArrayList<String> tempList = Lists.newArrayList();
		HashMap<String,String> tempPosMap = new HashMap<String, String>(posMap);
		for(String s : tempPosMap.keySet()){
			if(posMap.get(s).startsWith("N")||
					posMap.get(s).startsWith("P")||
					posMap.get(s).startsWith("CD")||
					posMap.get(s).startsWith("JJ")){
				String baseWord = resourceHandler.getBaseFormOfWord(s.split("-")[0],posMap.get(s.replaceAll("_", "-")).substring(0, 1));
				tempList.add("has("+s+",instance_of,"+baseWord+").");
				posMap.put(baseWord, posMap.get(s));
				if(stringToNamedEntityMap.containsKey(s.split("-")[0])){
					tempList.add("has("+baseWord+",is_subclass_of,"+stringToNamedEntityMap.get(s.split("-")[0]).toLowerCase()+").");
				}else{
					String supClass = getSuperclass(s, baseWord);
					if(supClass!=null){
						tempList.add("has("+baseWord+",is_subclass_of,"+supClass+").");
						posMap.put(supClass, posMap.get(s));
					}
				}
			}
		}

		finalList.addAll(tempList);
	}

	/**
	 * This method is used to extract conceptual classes of words in the input sentence.
	 * It used lemmatization, saved word senses from WordNet, and manually curated adjective classes.
	 * @return model.graph.helper.ClassesResource it is used to store maps of conceptual classes.
	 */
	private ClassesResource extractClasses(){
		HashMap<String,String> mapOfClasses = Maps.newHashMap();
		HashMap<String,String> mapOfSuperClasses = Maps.newHashMap();
		for(String dep : mapOfDeps.keySet()){
//			HashMap<String,String> depTuple = mapOfDependencies.get(dep);
			ArrayList<String> depList = mapOfDeps.get(dep);
			for(String tmp : depList){
//			for(String word1 : depTuple.keySet()){
//				String word2 = depTuple.get(word1);
				String[] words = tmp.split("#");
				if(words.length!=2){
					continue;
				}
				String word1 = words[0];
				String word2 = words[1];
				if(mweMap.containsKey(word1)){
					String oldWord1 = word1;
					word1 = mweMap.get(word1) + oldWord1.substring(oldWord1.lastIndexOf("-"));
					posMap.put(word1, posMap.get(oldWord1));
				}
				
				if(mweMap.containsKey(word2)){
					String oldWord2 = word2;
					word2 = mweMap.get(word2) + oldWord2.substring(oldWord2.lastIndexOf("-"));
					posMap.put(word2, posMap.get(oldWord2));
				}
				
				String word1POS = posMap.get(word1);
				if(word1POS!=null){
					if(!word1POS.startsWith("CC")
							&& !word1POS.startsWith(",")
							&& !word1POS.startsWith(".")
							&& !word1POS.startsWith("IN")
							&& !word1POS.startsWith("TO")){

						/*
						 * Word1 Class extraction
						 */
						String baseWord = resourceHandler.getBaseFormOfWord(word1.substring(0, word1.indexOf("-")),word1POS.substring(0, 1));
						mapOfClasses.put(word1, baseWord);
						posMap.put(baseWord, word1POS);

						/*
						 * Word1 Superclass extraction
						 */
						String superclass = getSuperclass(word1,baseWord);

						if(word1POS.equalsIgnoreCase("JJ")){
							String sup = resourceHandler.getAdjectiveClass(baseWord);
							if(sup!=null){
								superclass = sup;
							}else{
								sup = resourceHandler.getAdjectiveClass(word1.substring(0, word1.lastIndexOf("-")));
								if(sup!=null){
									superclass = sup;
								}
							}
						}else if(word1POS.equalsIgnoreCase("JJS")){
							superclass = "superlative";
						}else if(word1POS.equalsIgnoreCase("JJR")){
							superclass = "comparative";
						}

						mapOfClasses.put(baseWord,superclass);
						mapOfSuperClasses.put(word1, superclass);
					}
				}

				/*
				 * Word2 Class extraction
				 */
				String word2POS = posMap.get(word2);
				if(word2POS!=null){
					if(!word2POS.startsWith("CC")
							&& !word2POS.startsWith(",")
							&& !word2POS.startsWith(".")
							&& !word2POS.startsWith("IN")
							&& !word2POS.startsWith("TO")){
						String baseWord = resourceHandler.getBaseFormOfWord(word2.substring(0, word2.indexOf("-")),word2POS.substring(0, 1));
						mapOfClasses.put(word2, baseWord);
						posMap.put(baseWord, word2POS);

						/*
						 * Word2 Superclass extraction
						 */
						String superclass = getSuperclass(word2,baseWord);

						if(word2POS.equalsIgnoreCase("JJ")){
							String sup = resourceHandler.getAdjectiveClass(baseWord);
							if(sup!=null){
								superclass = sup;
							}else{
								sup = resourceHandler.getAdjectiveClass(word2);
								if(sup!=null){
									superclass = sup;
								}
							}
						}else if(word2POS.equalsIgnoreCase("JJS")){
							superclass = "superlative";
						}else if(word2POS.equalsIgnoreCase("JJR")){
							superclass = "comparative";
						}

						mapOfClasses.put(baseWord,superclass);
						mapOfSuperClasses.put(word2, superclass);
					}
				}
			}
		}

		ClassesResource classResource = new ClassesResource(mapOfClasses,mapOfSuperClasses);
		return classResource;

	}

	/**
	 * This method is used to extract super-classes of a word in the input sentence given the word and its base form.
	 * @param word it is the word in the input sentence. For example, <i>understood</i>.
	 * @param baseWord it is the base form of a word in the input sentence. For example, <i>understand</i>.
	 * @return superclass it is the conceptual superclass of the input word. For example, <i>cognition</i>.
	 */
	private String getSuperclass(String word, String baseWord){
		String superclass = null;
		String lexicalFileName =  wordSenseMap.get(word.toLowerCase().replaceAll("-", "_")) == null ? null : wordSenseMap.get(word.toLowerCase().replaceAll("-", "_")).get(1);
		String  entityType= stringToNamedEntityMap.get(baseWord);
		if(entityType != null) {
			entityType = entityType.toLowerCase();
			superclass = entityType.replace(NamedEntityTagger.NAMED_MULTI_WORD_SEPARATOR," ");
			posMap.put(entityType.toLowerCase(), posMap.get(baseWord));
		}else if(lexicalFileName != null){
			if(lexicalFileName.contains(".")){
				lexicalFileName = lexicalFileName.substring(lexicalFileName.indexOf(".")+1,lexicalFileName.length());
			}
			if(resourceHandler.isAPronounRole(baseWord.toLowerCase())){
				String parent = resourceHandler.getPronounRole(baseWord.toLowerCase());
				superclass = parent;
				posMap.put(parent, posMap.get(baseWord.replaceAll("_", "-")));
			}else{
				String parent = lexicalFileName;
				superclass = parent;
				posMap.put(parent, posMap.get(baseWord.replaceAll("_", "-")));
			}
		}else{
			if(resourceHandler.isAPronounRole(baseWord.toLowerCase())){
				String parent = resourceHandler.getPronounRole(baseWord.toLowerCase());
				superclass = parent;
				posMap.put(parent, posMap.get(baseWord.replaceAll("_", "-")));
			}else{
			}
		}
		return superclass;
	}

}
