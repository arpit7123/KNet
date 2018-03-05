package edu.asu.nlu.knet.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClingoWrapper {
	enum Enum_OS {
		LINUX,
		WINDOWS,
		UNKNOWN;
	}

	//    static String ClingoWrapper_clingoLinuxCmd = Config.loadProperty("ClingoWrapper.clingoLinuxCmd", "asp/Linux/clingo | asp/Linux/mkatoms -n");
	//    static String ClingoWrapper_clingoWindowsCmd = Config.loadProperty("ClingoWrapper.clingoWindowsCmd", "asp\\Windows\\clingo.exe | asp\\Windows\\mkatoms.exe -n");

	private String ClingoWrapper_clingoLinuxCmd = "";
	private String clingoDir = "/home/arpit/workspace/KNet/ASP/clingo ";
	private Enum_OS currentOS = getOS();
	private static ClingoWrapper clingoWrapper = null;
	
	static {
		clingoWrapper = new ClingoWrapper();
	}
	
	public static ClingoWrapper getInstance(){
		return clingoWrapper;
	}
	
	private ClingoWrapper(){}
	
	public Enum_OS getOS() {
		String OS = System.getProperty("os.name").toLowerCase(); 
		Enum_OS currentOS;
		if (OS.contains("linux")) { 
			currentOS = Enum_OS.LINUX;
		} else if (OS.contains("windows")) { 
			currentOS = Enum_OS.WINDOWS;
		} else {
			currentOS = Enum_OS.UNKNOWN;
			System.err.println("Unknown Operating System"); 
		}
		return currentOS;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public String[] getExecCommand() throws IOException {
		switch (currentOS) {
		case LINUX:
			String[] cmd = {
					"/bin/sh",
					"-c",
					ClingoWrapper_clingoLinuxCmd
			};
			return cmd;

		default:

			String[] cmd2 = {
					"cmd",
					"/c",
					//                      ClingoWrapper_clingoWindowsCmd
			};

			return cmd2;
		}
	}

	public ArrayList<String> callASPusingFiles(String sentFile, String bkFile, String rulesFile){
		ClingoWrapper_clingoLinuxCmd = clingoDir + " " + sentFile + " " + bkFile + " " + rulesFile + " 0";
		ArrayList<String> result = null;
		try {
			switch (currentOS) {
			case LINUX:
				String[] cmd = {
						"/bin/sh",
						"-c",
						ClingoWrapper_clingoLinuxCmd
				};
				Process process;
				process = Runtime.getRuntime().exec(cmd);

				BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = null;
				result = new ArrayList<String>();
				while((line=stdInput.readLine())!=null){
					Pattern p = Pattern.compile(".*Answer.*");
					Matcher m = p.matcher(line);
					if(m.find()){
						line=stdInput.readLine();
						if(!line.trim().equalsIgnoreCase("")){
							result.add(line);
						}
						//						while(!(line=stdInput.readLine()).equalsIgnoreCase("SATISFIABLE") &&
						//								!line.startsWith("Answer")){
						//							if(!line.trim().equalsIgnoreCase("")){
						//								result.add(line);
						//							}
						//						}
					}
				}
				break;
			default:
				@SuppressWarnings("unused")
				String[] cmd2 = {
						"cmd",
						"/c",
						//                      ClingoWrapper_clingoWindowsCmd
				};
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}


	public ArrayList<String> callASPusingFilesList(ArrayList<String> listOfFiles) throws InterruptedException{// sentFile, String bkFile, String rulesFile){
		String files = "";
		for(String name : listOfFiles){
			files = files + " " + name;
		}
		ClingoWrapper_clingoLinuxCmd = clingoDir + " " +files.trim();
		ArrayList<String> result = null;
		try {
			switch (currentOS) {
			case LINUX:
				String[] cmd = {
						"/bin/sh",
						"-c",
						ClingoWrapper_clingoLinuxCmd
				};
				Process process;
				process = Runtime.getRuntime().exec(cmd);
				process.waitFor();
				//Thread.sleep(3000);
				
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = null;
				while((line=stdInput.readLine())!=null){
					Pattern p = Pattern.compile(".*Answer.*");
					Matcher m = p.matcher(line);
					if(m.find()){
						if(result==null){
							result=new ArrayList<String>();
						}
						line=stdInput.readLine();
						if(!line.trim().equalsIgnoreCase("")){
							result.add(line);
						}
					}
				}
				break;
			default:
				@SuppressWarnings("unused")
				String[] cmd2 = {
						"cmd",
						"/c",
						//                      ClingoWrapper_clingoWindowsCmd
				};
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static void main(String[] args) throws IOException{
		ClingoWrapper cw = new ClingoWrapper();
		String rulesFile = "./ASP/rulesFile.txt";
		String dataFile = "./ASP/tempFile1.txt";

		ArrayList<String> list = new ArrayList<String>();
		list.add(rulesFile);
		list.add(dataFile);
		ArrayList<String> res = null;
		try {
			res = cw.callASPusingFilesList(list);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(res!=null){
			System.out.println("Knowledge found:");
			for(String s : res){
				System.out.println(s);
			}
		}
		System.out.println("Finished !!!");		

		System.exit(0);

	}


}