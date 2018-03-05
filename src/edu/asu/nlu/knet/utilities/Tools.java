/**
 * 
 */
package edu.asu.nlu.knet.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * @author arpit
 *
 */
public class Tools {


	public static ArrayList<String> divideIntoSentences(String paragraph){
		ArrayList<String> listOfSentences = Lists.newArrayList();
		ArrayList<String> tempList = Lists.newArrayList(Splitter.on(".").split(paragraph));
		for(String s : tempList){
			s = Tools.preprocessText(s);
			if(!s.trim().equalsIgnoreCase("")){
				ArrayList<String> tempList1 = Lists.newArrayList(Splitter.on(";").split(s));
				for(String s1 : tempList1){
					if(!s1.trim().equalsIgnoreCase("")){
						listOfSentences.add(s1);
					}
				}				
			}

		}
		return listOfSentences;
	}

	public static String preprocessText(String sent){
		String result = null;
		result = sent.replaceAll("\t", " ");
		result = result.replaceAll("\n", " ");
		result = result.replaceAll(" +", " ");
		return result;
	}

	public static ArrayList<String> listFilesForFolder(String dir) {
		File folder = new File(dir);
		ArrayList<String> listOfFiles = Lists.newArrayList();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listOfFiles.addAll(listFilesForFolder(fileEntry.toString()));
			} else {
				String fileName = fileEntry.getName();
				if(fileName.endsWith(".txt")){
					listOfFiles.add(fileEntry.getName());
				}
			}
		}
		return listOfFiles;
	}
	
	public static ArrayList<String> listFilesWithAbsPath(String dir) {
		File folder = new File(dir);
		ArrayList<String> listOfFiles = Lists.newArrayList();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listOfFiles.addAll(listFilesWithAbsPath(fileEntry.toString()));
			} else {
				String fileName = fileEntry.getAbsolutePath();//fileEntry.getName();
				if(fileName.endsWith(".txt")){
					if(dir.endsWith("/")){
						listOfFiles.add(dir + fileEntry.getName());
					}else{
						listOfFiles.add(dir+"/" + fileEntry.getName());
					}
				}
			}
		}
		return listOfFiles;
	}
	
	public static void copyFileToDir(String sourceDir, String destDir) throws IOException{
		 Path FROM = Paths.get(sourceDir);
		    Path TO = Paths.get(destDir);
		    //overwrite existing file, if exists
		    CopyOption[] options = new CopyOption[]{
		      StandardCopyOption.REPLACE_EXISTING,
		      StandardCopyOption.COPY_ATTRIBUTES
		    }; 
		    Files.copy(FROM, TO, options);
	}

	
	public static void main(String[] args){
		String fromPath = "/media/arpit/Windows7_OS/Users/ARPIT/Desktop/corpus/OANC-GrAF/data/written_2";
		ArrayList<String> list = listFilesWithAbsPath(fromPath);
		String toPath = "/home/arpit/workspace/KnowledgeNet/corpus/finalCorpus2/";
		for(String s : list){
			String name = s.substring(s.lastIndexOf("/")+1);
			try {
				copyFileToDir(s, toPath+name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static Object load(String name){
		Object obj = null;
		try{
			FileInputStream fis = new FileInputStream(name);
			ObjectInputStream ois = new ObjectInputStream(fis);
			obj = ois.readObject();
			ois.close();
		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
		return obj;
	}
	
	public static void saveObject(String fileName, Object object){
		try{
			File fileOne=new File(fileName);
			FileOutputStream fos=new FileOutputStream(fileOne);
			ObjectOutputStream oos=new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			fos.close();
		}catch(Exception e){}
	}
	
}
