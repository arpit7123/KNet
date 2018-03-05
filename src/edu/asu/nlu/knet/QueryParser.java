package edu.asu.nlu.knet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParser {
	
	
	private Pattern queryPattern = Pattern.compile("(<)(.*)(>)(<)(.*)(>)(<)(.*)(>)");
	
	public enum parts{
		TYPE1,
		POLARITY1,
		VALUE1,
		SLOT1,
		SLOT_VALUE1,
		REL_TYPE,
		REL_VALUE,
		TYPE2,
		POLARITY2,
		VALUE2,
		SLOT2,
		SLOT_VALUE2;
		
	}
	
	
	
	//	<TYPE=T1,VALUE=X,SLOT=S1,SLOT_VALUE=A> 
	//						<REL_TYPE=RT,REL_VALUE=R> 
	//								<TYPE=T2,VALUE=Y,SLOT=S2,SLOT_VALUE=A>
	/**
	 * 
	 * @param query
	 */
	public void parseQuery(String query){
		Matcher m = this.queryPattern.matcher(query);
		if(m.find()){
			String verb1Part = m.group(2);
			String relPart = m.group(5);
			String verb2Part = m.group(8);

			System.out.println(verb1Part);
			System.out.println(relPart);
			System.out.println(verb2Part);
		}else{
			System.out.println("No Pattern matched the given query");
		}
	}
	
	public static void main(String[] args){
		QueryParser qp = new QueryParser();
		String query = "<TYPE=T1,VALUE=X,SLOT=S1,SLOT_VALUE=A><REL_TYPE=RT,REL_VALUE=R><TYPE=T2,VALUE=Y,SLOT=S2,SLOT_VALUE=A>";
		qp.parseQuery(query);
	}
}
