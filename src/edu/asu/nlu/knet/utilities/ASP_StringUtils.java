package edu.asu.nlu.knet.utilities;
//package knowledgeNet.utilities;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//
//import org.apache.log4j.Logger;
//
//public class ASP_StringUtils {
//    /**
//     * Logger for this class
//     */
//    private static final Logger logger = Logger.getLogger(ASP_StringUtils.class);
//
//    /**
//     * @param input
//     * @return valid string for ASP
//     */
//    public static String getValidStr(String input) {
//        // convert to lower case
//        input = input.toLowerCase();
//
//        // process punctuation
//        //--> ",.:;'\"<>[]{}-_=+`|/?~!@#$%^&*()"
//        if (input.equals(".")) {
//            input = "_dot_";
//        } else if (input.equals(",")) {
//            input = "_comma_";
//        } else if (input.equals(":")) {
//            input = "_colon_";
//        } else if (input.equals(";")) {
//            input = "_semicol_";
//        } else if (input.equals("'")) {
//            input = "_appos_";
//        } else if (input.equals("`")) {
//            input = "_prime_";
//        } else if (input.equals("\"")) {
//            input = "_dprime_";
//        } else if (input.equals("/")) {
//            input = "_slash_";
//        } else if (input.equals("not")) {
//            input = "_not_";
//        } else if (input.equals("?")) {
//            input = "_ques_";
//        } else if (input.equals("??")) {
//            input = "_ques_";
//        } else if (input.equals("_")) {
//            input = "_undsc_";
//        } else if (input.equals("-")) {
//            input = "_hyphen_";
//        } else if (input.equals("--")) {
//            input = "_hyphen_";
//        } else if (input.equals("!")) {
//            input = "_exclaim_";
//        } else if (input.equals("#")) {
//            input = "_sharp_";
//        } else if (input.equals("$")) {
//            input = "_dollar_";
//        } else if (input.equals("&")) {
//            input = "_and_";
//        } else if (input.equals("@")) {
//            input = "_at_";
//        } else if (input.equals("*")) {
//            input = "_star_";
//        } else if (input.equals("^")) {
//            input = "_up_";
//        } else if (input.equals("~")) {
//            input = "_or_";
//        } else if (input.equals("+")) {
//            input = "_plus_";
//        } else if (input.equals("%")) {
//            input = "_per_";
//        } else if (input.equals(")")) {
//            input = "_per_";
//        } else if (input.equals("\\*")) {
//            input = "_star_";
//        } else if (input.equals("\\*\\*")) {
//            input = "_star_";
//        } else if (input.startsWith("'")) {
//            input = input.replaceAll("'", "_prime_");
//        } else if (input.startsWith("-")) {
//            input = input.replaceAll("-([a-z]*)-", "_$1_");
//        } else if (input.matches("[0-9]+.*")) {
//            input = "zzz" + input;
//        } else if (input.contains("$")) {
//            input = input.replaceAll("\\$", "_S_");
//        } else if (input.contains("`")) {
//            input = input.replaceAll("`", "_prime_");
//        } else if (input.contains(":")) {
//            input = input.replaceAll(":", "_colon_");
//        } else if (input.contains("+")) {
//            input = input.replaceAll("\\+", "_plus_");
//        } else if (input.contains("=")) {
//            input = input.replaceAll("=", "_equal_");
//        } else if (input.contains("\\+")) {
//            input = input.replaceAll("\\\\+", "_plus_");
//        } else if (input.contains(".")) {
//            input = input.replaceAll("\\.", "_");
//        }
//        input = input.replaceAll("[\\.\\-\\\\/,]+", "_");
//        if (input.matches("_([0-9]*)")) {
//            input = input.replaceAll("_([0-9]*)", "zzz$1");
//        }
//
//        return input;
//    }
//
//    public static String getValidQuote(String input) {
//        if (input.contains("\\")) {
//            input = input.replaceAll("\\\\", "\\\\\\\\");
//        }
//        return input;
//    }
//
//    /**
//     * @param inputStream TODO
//     * @param errorStream TODO
//     * @param output
//     * @return
//     * @throws Exception
//     */
//    public static ArrayList<String> parseASPoutput(InputStream inputStream, InputStream errorStream)
//            throws Exception {
//        ArrayList<String> output = new ArrayList<String>();
//
//        BufferedReader stdInput = new BufferedReader(new InputStreamReader(inputStream));
//        String ss;
//        while ((ss = stdInput.readLine()) != null) {
//            logger.trace(ss);
//            // Filter the answer
//            if (!ss.trim().isEmpty()) {
//                output.add(ss);
//            }
//        }
//
//        BufferedReader stdError = new BufferedReader(new InputStreamReader(errorStream));
//        StringBuffer stdE = new StringBuffer();
//        boolean hasError = false;
//        while ((ss = stdError.readLine()) != null) {
//            ss = ss.trim();
//            stdE.append(ss);
//            stdE.append("\n");
//            if ((!ss.isEmpty()) && (!ss.startsWith("%"))) {
//                hasError = true;
//            }
//        }
//        if (hasError) {
//            throw new Exception("Error in ASP: " + stdE.toString());
//        }
//
//        if (output.isEmpty()) {
//            throw new Exception("No output from ASP");
//        }
//
//        return output;
//    }
//    
//    public static String unquote(String string) {
//        return string.replaceAll("(^\\\")|(\\\"$)", "");
//    }
//    
//    /**
//     * Parse a predicate like evidence("210.56.81.153","demonstrates",positive,"closeness","towards","Oceanus","(Confidence:",low,")")
//     * and return all the String array of value ["210.56.81.153","demonstrates",positive,"closeness","towards","Oceanus","(Confidence:",low,")"])
//     * @param predicateName. i.e. "evidence"
//     * @param text. i.e. evidence("210.56.81.153","demonstrates",positive,"closeness","towards","Oceanus","(Confidence:",low,")")
//     * @return ["210.56.81.153","demonstrates",positive,"closeness","towards","Oceanus","(Confidence:",low,")"])
//     */
//    /**
//     * @param predicateName
//     * @param text
//     * @return Array of atoms if predicate name is matched. Empty array otherwise.
//     */
//    public static String[] parsePredicate(String predicateName, String text) {
//    	String matching_regex = predicateName+"\\((.*)\\)";
//    	if (!text.matches(matching_regex)) {
//			return new String[0]; 
//		}
//    	
//        String contentStr = text.replaceAll(matching_regex, "$1");
//        String otherThanQuote = " [^\"] ";
//        String quotedString = String.format(" \" %s* \" ", otherThanQuote);
//        String regex = String.format("(?x) " + // enable comments, ignore white spaces
//                ",                         " + // match a comma
//                "(?=                       " + // start positive look ahead
//                "  (                       " + //   start group 1
//                "    %s*                   " + //     match 'otherThanQuote' zero or more times
//                "    %s                    " + //     match 'quotedString'
//                "  )*                      " + //   end group 1 and repeat it zero or more times
//                "  %s*                     " + //   match 'otherThanQuote'
//                "  $                       " + // match the end of the string
//                ")                         ", // stop positive look ahead
//                otherThanQuote, quotedString, otherThanQuote);
//
//        String[] contents = contentStr.split(regex);
//        return contents;
//    }
//
//	public static void main(String[] args) {
//		String[] parsePredicate = parsePredicate("evidence", "evidence(\"210.56.81.153\",\"demonstrates\",positive,\"closeness\",\"towards\",\"Oceanus, Comma\")");
//		for (String string : parsePredicate) {
//			System.out.println(string);
//		}
//		
//	    System.out.println(getValidStr("'s"));
//	    System.out.println(getValidStr("co-addmit"));
//	    System.out.println(getValidStr("6-co-addmit"));
//	    System.out.println(getValidStr("node(29,prp$)."));
//	    System.out.println(getValidStr("node(29,and\\/or)."));
//	    System.out.println(getValidStr("node(29,=)."));
//	    System.out.println(getValidStr("-"));
//	    System.out.println(getValidStr("1,000"));
//	    System.out.println(getValidQuote(" \\n "));
//	}
//	
//    
//    
//
//}
