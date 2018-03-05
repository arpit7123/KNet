package edu.asu.nlu.knet.extra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestJsoup {

	/**
	 * @param args
	 * @throws Exception 
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		TestJsoup tj = new TestJsoup();
		String url = "http://minkara.carview.co.jp/userid/266023/friend/";
		HashSet<String> ids = tj.getFromGoogleJsoup(url);
		for(String s : ids){
			System.out.println(s);
		}

		HashMap<String,String> h = new HashMap<String,String>();
		h.put("A", "tom");
		HashMap<String,String> h1= new HashMap<String,String>();
		h1.put("A", "john");
		h.putAll(h1);
		for(String s : h.keySet()){
			System.out.println(s+"  : "+h.get(s));
		}
	}


	public static HashSet<String> getFromGoogleJsoup(String url) throws Exception{
		HashSet<String> result = new HashSet<String>();
		Pattern p = Pattern.compile("(/userid/)(.*)(/profile/)");
		Document doc = Jsoup.connect(url).get();
		Elements elements = doc.select("a");
		for(Element e : elements){
			String attribute = e.attr("href");
			Matcher m = p.matcher(attribute);
			if(m.find()){
				result.add(m.group(2));
			}
		}
		return result;

	}


}














