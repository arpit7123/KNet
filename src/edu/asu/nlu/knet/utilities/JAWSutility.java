package edu.asu.nlu.knet.utilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.smu.tspell.wordnet.*;
import edu.smu.tspell.wordnet.impl.file.ReferenceSynset;

/**
 * Displays word forms and definitions for synsets containing the word form
 * specified on the command line. To use this application, specify the word
 * form that you wish to view synsets for, as in the following example which
 * displays all synsets containing the word form "airplane":
 * <br>
 * java TestJAWS airplane
 */
public class JAWSutility
{
	private String wordnet_dir = null;//"/host/stuff/CreateGraph/Files/WordNet-3.0/dict";
//	
	public JAWSutility(){
		String path = JAWSutility.class.getResource("JAWSutility.class").toString();
		path = path.substring(0, path.lastIndexOf("/")).split(":")[1];
		wordnet_dir = path + "/WordNet-3.0/dict";
	}
	
	public String getSynonymWordnet(String word,String use){
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();

		List<Synset> synset_list = null;
		if(use.equals("v"))
			synset_list=Arrays.asList(database.getSynsets(word, SynsetType.VERB));
		else if(use.equals("n"))
			synset_list=Arrays.asList(database.getSynsets(word, SynsetType.NOUN));
		else if(use.equals("a"))
			synset_list=Arrays.asList(database.getSynsets(word, SynsetType.ADJECTIVE));
		else if(use.equals("ad"))
			synset_list=Arrays.asList(database.getSynsets(word, SynsetType.ADVERB));

		for (Synset synset : synset_list) {
			//System.out.println("\nsynset type: " +SynsetType.ALL_TYPES[synset.getType().getCode()]);
			//System.out.println(" definition: " +synset.getDefinition());
			// word forms are synonyms:
			for (String wordForm : synset.getWordForms()) {
				if (!wordForm.equals(word) && wordForm!=null && wordForm!=""&&!wordForm.contains(" ")) {
					//System.out.println(" synonym: " +wordForm);
					return wordForm;
				}
			}
		}
		return null;
	}
	
	// returns the list of synonyms of this word
	public List<String> getAllSynonymsWordnet(String word,String use){
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		List<String> returnList = new ArrayList<String>();

		if(!word.contains("_")){
			List<Synset> synset_list = null;
			if(use.equals("v"))
				synset_list=Arrays.asList(database.getSynsets(word, SynsetType.VERB));
			else if(use.equals("n"))
				synset_list=Arrays.asList(database.getSynsets(word, SynsetType.NOUN));
			else if(use.equals("j"))
				synset_list=Arrays.asList(database.getSynsets(word, SynsetType.ADJECTIVE));

			if(synset_list!=null){
				for (Synset synset : synset_list) {
					//System.out.println("\nsynset type: " +SynsetType.ALL_TYPES[synset.getType().getCode()]);
					//System.out.println(" definition: " +synset.getDefinition());
					// word forms are synonyms:
					for (String wordForm : synset.getWordForms()) {
						if (!wordForm.equals(word) && wordForm!=null && wordForm!="" && !wordForm.contains(" ")) {
							//System.out.println(" synonym: " +wordForm);
							//return wordForm;
							returnList.add(wordForm);
						}
					}
				}
			}
		}
		return returnList;
	}

	// Returns the base form of the input word. We return the first base form from the output.
	public String getBaseForm(String word,String use){
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		/*Morphology id=Morphology.getInstance();
		String[] arr = id.getBaseFormCandidates("winner", SynsetType.ADJECTIVE);
		System.out.println(arr[0]);*/
		List<String> base_list = null;
		edu.smu.tspell.wordnet.SynsetType stype=null;
		if(use.equalsIgnoreCase("v"))
			stype=SynsetType.VERB;
		else if(use.equalsIgnoreCase("n"))
			stype=SynsetType.NOUN;
		else if(use.equalsIgnoreCase("p"))
			stype=SynsetType.NOUN;
		else if(use.equalsIgnoreCase("j"))
			stype=SynsetType.ADJECTIVE;
		else if(use.equalsIgnoreCase("r"))
			stype=SynsetType.ADVERB;
		else
			stype=SynsetType.NOUN;
		
		base_list=Arrays.asList(database.getBaseFormCandidates(word, stype));

		/*System.out.println("Base Form : ");
		for(String s:base_list)
			System.out.println(s);*/
		
		// check for conversions like asked--> aske
		if(base_list.size()>0){
			for(String str:base_list){
				if( Arrays.asList(database.getSynsets(str, stype)).size()>0 )
					return str;
			}
//			if(word.endsWith("ed") && base_list.get(0).endsWith("e")){
//				List<Synset> synset_list=Arrays.asList(database.getSynsets(base_list.get(0), stype));
//				if(synset_list.size()==0){
//					synset_list=Arrays.asList(database.getSynsets(base_list.get(0).substring(0, base_list.get(0).length()-2), stype));
//					if(synset_list.size()>0)
//						return base_list.get(0).substring(0, base_list.get(0).length()-2);
//				}
//			}
//			else{
				// check if the returned base form is a word in the wordnet..if not then don;t return this form
				List<Synset> synset_list=Arrays.asList(database.getSynsets(base_list.get(0), stype));
				if(synset_list.size()!=0){
					return base_list.get(0);
				}
//			}
		}

		return word;
	}

	public String returnVerbForm(String word,String use){
		System.setProperty("wordnet.database.dir",this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		//String word="winner";
		edu.smu.tspell.wordnet.SynsetType stype=null;
		if(use.equals("v"))
			stype=SynsetType.VERB;
		else if(use.equals("n"))
			stype=SynsetType.NOUN;
		else if(use.equals("j"))
			stype=SynsetType.ADJECTIVE;
		else if(use.equals("ad"))
			stype=SynsetType.ADVERB;
		List<Synset> synset_list = Arrays.asList(database.getSynsets(word, stype));
		for (int i=0;i<synset_list.size();i++) {
			Synset synset=synset_list.get(i);
			WordSense[] wordsense = synset.getDerivationallyRelatedForms(word);
			//System.out.println(wordsense[0].getWordForm());
			//System.out.println(wordsense[0].getSynset().getType());
			if(wordsense.length>0 && wordsense[0].getSynset().getType()==SynsetType.VERB)
				return wordsense[0].getWordForm();
		}

		return word;
	}
	
	public int getAncester(String word, String use){
		int lexicalCode = -1;
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		edu.smu.tspell.wordnet.SynsetType stype=null;
		try{
		if(use.equalsIgnoreCase("v"))
			stype=SynsetType.VERB;
		else if(use.equalsIgnoreCase("n"))
			stype=SynsetType.NOUN;
		else if(use.equalsIgnoreCase("j"))
			stype=SynsetType.ADJECTIVE;
		else if(use.equalsIgnoreCase("ad"))
			stype=SynsetType.ADVERB;
		
		ReferenceSynset referenceSynset = (ReferenceSynset) database.getSynsets(word, stype)[0];
		lexicalCode =referenceSynset.getLexicalFileNumber();
		}catch(ArrayIndexOutOfBoundsException e){
//			System.out.println("Out of bounds");
		}
		return lexicalCode;
	}
	
	public String getWordFromOffset(String word){
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] result = database.getSynsets(word);
		
		
		return result.toString();
	}

	
	public boolean isAnAdjective(String word){
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		if((database.getSynsets(word, SynsetType.ADJECTIVE)).length > 0){
			return true;
		}
		
		return false;
	}
	

	public static void main(String[] args){
		JAWSutility jaws = new JAWSutility();
		/*System.setProperty("wordnet.database.dir", "/media/F/WordNet-3.0/dict");
		NounSynset nounSynset; 
		NounSynset[] hyponyms; 
		VerbSynset vs;

		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		Synset[] synsets = database.getSynsets("vindicate", SynsetType.VERB); 
		String word="witness";
		edu.smu.tspell.wordnet.SynsetType stype=SynsetType.NOUN;
		List<Synset> synset_list = Arrays.asList(database.getSynsets(word, stype));

		System.out.println("\n\n**Process word: " + word);
		for (Synset synset : synset_list) {
			//System.out.println("\nsynset type: " +SynsetType.ALL_TYPES[synset.getType().getCode()]);
			System.out.println(" definition: " +synset.getDefinition());
			//System.out.println("Examples : ");
			// word forms are synonyms:
			/*for (String wordForm : synset.getWordForms()) {
				if (!wordForm.equals(word)) {
					System.out.println(" synonym: " +wordForm);
				}
			}
			for (String example : synset.getUsageExamples()) {
				System.out.println(" use : " +example);
			}
		}*/

		String word=//"upset";
			//"successful";
			//"had";
			//"envies";
			//"happy";
			//"forgotten";
			//"councilmen";
			//"Bus";
			//"small";
			//"zoom";
			//"fit";
			//"ask";
			//"slice";
			//"bunk_bed";
			//"after";
			//"balance";
			//"bottom";
			//"repaired";
			"another";
//		System.out.println(jaws.getAncester(word,"JJ".substring(0,1)));
		//String syn=getSynonymWordnet(word, "v");
		//System.out.println(syn);
	System.out.println(jaws.getBaseForm(word,"d"));
//	jaws.getWordFromOffset("Bad");
//	System.out.println(jaws.isAnAdjective(word));

	
		//System.out.println(returnVerbForm(word,"v"));
		//System.out.println(getSynonymWordnet(word, "v"));
		
//		for(String str:getAllSynonymsWordnet(word, "n")){
//			System.out.println(str);
//		}
	}
}