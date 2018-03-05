package edu.asu.nlu.knet.extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainClass{
	
	public static void main(String[] args){
		String knowledgeFile = "";
		String readableKnowFile = "";
		try(BufferedReader in = new BufferedReader(new FileReader(knowledgeFile));
				BufferedWriter out = new BufferedWriter(new FileWriter(readableKnowFile))){
			String line = null;
			while((line=in.readLine())!=null){
				String sent = line;
				String[] knows = in.readLine().split(" ");
				for(String k : knows){
					
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}