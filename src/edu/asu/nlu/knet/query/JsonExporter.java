package edu.asu.nlu.knet.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonExporter {

	public static String exportToJSON(ArrayList<WeightedDBObject> results) throws JSONException {
		if(results==null){
			return null;
		}
		JSONArray jsonArray = new JSONArray();
		for(WeightedDBObject result:results) {
			if(result.getWeight() > Double.MIN_VALUE) {
				JSONObject obj = new JSONObject();
				
				obj.put("sent", result.getSentence());
				obj.put("weight", result.getWeight());
				obj.put("sentID", result.getSentenceId());
				jsonArray.put(obj);
			}
		}
		return jsonArray.toString();
	}
}
