package edu.asu.nlu.knet.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import edu.asu.nlu.knet.query.RetrieveFromMaps;

/**
 * Servlet implementation class ParserServlet
 */
public class InputOutputServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RetrieveFromMaps retFrmMap;
	private Pattern pattern = Pattern.compile("(Event1)(=)(.*)([\\s]*,[\\s]*)(Rel)(=)(.*)([\\s]*,[\\s]*)(Event2)(=)(.*)([\\s]*,[\\s]*)(Slot1)(=)(.*)([\\s]*,[\\s]*)(Slot2)(=)(.*)");//Pattern.compile("([a-zA-Z0-9_\\s]+)(;)([a-zA-Z0-9_\\s]*)");

	private HashMap<String,String> exampleMap = new HashMap<String,String>();
	
	{
		exampleMap.put("", "");
		
	}
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InputOutputServlet() {
		super();
		retFrmMap = new RetrieveFromMaps();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String event1 = request.getParameter("event1").trim();
		String slot1 = request.getParameter("event1Slot").trim();
		String relation = request.getParameter("relation").trim();
		String event2 = request.getParameter("event2").trim();
		String slot2 = request.getParameter("event2Slot").trim();

		if(event1.equalsIgnoreCase("")){
			event1 = "*";
		}

		if(event2.equalsIgnoreCase("")){
			event2 = "*";
		}

		if(relation.equalsIgnoreCase("")){
			relation = "*";
		}

		if(slot1.equalsIgnoreCase("")){
			slot1 = "*";
		}

		if(slot2.equalsIgnoreCase("")){
			slot2 = "*";
		}

		String nlQuery = request.getParameter("nlQuery");

		if(nlQuery.equalsIgnoreCase("")){
			nlQuery = "Event1="+event1+","+"Rel="+relation+","+"Event2="+event2+","+"Slot1="+slot1+","+"Slot2="+slot2;
		}
		nlQuery = nlQuery.trim();

		PrintWriter out = response.getWriter();
		String result = null;

		Matcher matcher = pattern.matcher(nlQuery);
		if (matcher.find()) {
			event1 = matcher.group(3).trim();
			relation = matcher.group(7).trim();
			event2 = matcher.group(11).trim();
			slot1 = matcher.group(15).trim();
			slot2 = matcher.group(19).trim();
			if((!event1.equalsIgnoreCase("*")&&!relation.equalsIgnoreCase("*")&&!event2.equalsIgnoreCase("*")&&!slot1.equalsIgnoreCase("*")&&!slot2.equalsIgnoreCase("*"))
					||(event1.equalsIgnoreCase("*")&&!relation.equalsIgnoreCase("*")&&!event2.equalsIgnoreCase("*")&&!slot1.equalsIgnoreCase("*")&&!slot2.equalsIgnoreCase("*"))
					||(!event1.equalsIgnoreCase("*")&&!relation.equalsIgnoreCase("*")&&event2.equalsIgnoreCase("*")&&!slot1.equalsIgnoreCase("*")&&!slot2.equalsIgnoreCase("*"))
					||(!event1.equalsIgnoreCase("*")&&!relation.equalsIgnoreCase("*")&&!event2.equalsIgnoreCase("*")&&slot1.equalsIgnoreCase("*")&&!slot2.equalsIgnoreCase("*"))
					||(!event1.equalsIgnoreCase("*")&&!relation.equalsIgnoreCase("*")&&!event2.equalsIgnoreCase("*")&&!slot1.equalsIgnoreCase("*")&&slot2.equalsIgnoreCase("*")
							||(!event1.equalsIgnoreCase("*")&&relation.equalsIgnoreCase("*")&&!event2.equalsIgnoreCase("*")&&!slot1.equalsIgnoreCase("*")&&!slot2.equalsIgnoreCase("*")
									||(!event1.equalsIgnoreCase("*")&&relation.equalsIgnoreCase("*")&&!event2.equalsIgnoreCase("*")&&slot1.equalsIgnoreCase("*")&&slot2.equalsIgnoreCase("*"))))){
				try {
					result = retFrmMap.process(nlQuery);
				} catch (JSONException e) {
					result = "An Exception Occured in retrieval.";
				}
			}else{
				result = "Please enter a valid query!";
			}
		}else{
			result = "Please enter a valid query!";
		}
		out.append(result);
		//		out.append(ReadJSON.readJSON().toJSONString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
