/**
 * 
 */
package edu.asu.nlu.knet.postprocessor;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Arpit Sharma
 * @date Jun 23, 2017
 *
 */
public class KnowledgeNode {
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<KnowledgeType1> typ1KnowInsts = null;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<KnowledgeType2> typ2KnowInsts = null;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<KnowledgeType3> typ3KnowInsts = null;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<KnowledgeType4> typ4KnowInsts = null;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<KnowledgeType5> typ5KnowInsts = null;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<KnowledgeType6> typ6KnowInsts = null;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<KnowledgeType7> typ7KnowInsts = null;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private ArrayList<KnowledgeType8> typ8KnowInsts = null;

	@Override
	public String toString(){
		String result = "";
		if(typ1KnowInsts!=null){
			for(KnowledgeType1 ins : typ1KnowInsts){
				result += ins.toString() + "\n";
			}
			result += "*************************\n";
		}
		
		if(typ2KnowInsts!=null){
			for(KnowledgeType2 ins : typ2KnowInsts){
				result += ins.toString() + "\n";
			}
			result += "*************************\n";
		}
		
		if(typ3KnowInsts!=null){
			for(KnowledgeType3 ins : typ3KnowInsts){
				result += ins.toString() + "\n";
			}
			result += "*************************\n";
		}
		
		if(typ4KnowInsts!=null){
			for(KnowledgeType4 ins : typ4KnowInsts){
				result += ins.toString() + "\n";
			}
			result += "*************************\n";
		}
		
		if(typ5KnowInsts!=null){
			for(KnowledgeType5 ins : typ5KnowInsts){
				result += ins.toString() + "\n";
			}
			result += "*************************\n";
		}
		
		if(typ6KnowInsts!=null){
			for(KnowledgeType6 ins : typ6KnowInsts){
				result += ins.toString() + "\n";
			}
			result += "*************************\n";
		}
		
		if(typ7KnowInsts!=null){
			for(KnowledgeType7 ins : typ7KnowInsts){
				result += ins.toString() + "\n";
			}
			result += "*************************\n";
		}
		
		if(typ8KnowInsts!=null){
			for(KnowledgeType8 ins : typ8KnowInsts){
				result += ins.toString() + "\n";
			}
			result += "*************************\n";
		}
		
//		if(result.length()>0){
//			result = result.substring(0,result.length()-1);
//		}
		return result;
	}


}
