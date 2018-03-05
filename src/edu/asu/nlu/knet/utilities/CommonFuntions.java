package edu.asu.nlu.knet.utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CommonFuntions {
	
	
	public static void createTempFile(ArrayList<String> graphText, String fileName){
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){
			for(String s : graphText){
				bw.append(s.replaceAll("\\(not,", "\\(nt,").replaceAll(",not\\)", ",nt\\)"));
				bw.newLine();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
