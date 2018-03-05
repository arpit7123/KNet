/**
 * 
 */
package edu.asu.nlu.knet.servlets;


import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

/**
 * @author arpit
 *
 */
public class ReadJSON {


	public static JSONArray readJSON() {
		JSONArray jsonArr = null;
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(
					"/media/arpit/Windows7_OS/Users/ARPIT/Documents/stuff/KnowledgeNet/testJason.txt"));

			jsonArr = (JSONArray) obj;

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jsonArr;
	}
	
	public static void main(String[] args){
		System.out.println(readJSON().toString());
		
//		String URL = "give next_event give;";
//		Pattern pattern = Pattern.compile("([a-zA-Z0-9_\\s]+)(;)([a-zA-Z0-9_\\s]*)");
//		Matcher matcher = pattern.matcher(URL);
//		if (matcher.find()) {
//		    System.out.println(matcher.group(0)); //prints /{item}/
//		} else {
//		    System.out.println("Match not found");
//		}
	}

}
