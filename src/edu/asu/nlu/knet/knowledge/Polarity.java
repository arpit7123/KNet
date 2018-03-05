/**Polarity.java
 * 2:12:08 PM @author Arindam
 */
package edu.asu.nlu.knet.knowledge;

/**
 * @author Arpit
 *
 */
public enum Polarity {
	POSITIVE,
	NEGETIVE;
	
	public static Polarity getEnum(String s){
		if(s!=null){
			if(s.toLowerCase().startsWith("n"))
				return Polarity.NEGETIVE;
			else
				return Polarity.POSITIVE;
		}
		
		return null;
	}
}
