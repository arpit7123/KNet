/**
 * 
 */
package edu.asu.nlu.knet.database;

import java.io.Serializable;
import java.util.HashSet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Arpit Sharma
 * @date Jul 10, 2017
 *
 */
public class KBDetails implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private HashSet<String> events = null;
	@Getter (AccessLevel.PUBLIC) @Setter (AccessLevel.PUBLIC) private HashSet<String> properties = null;
	
}
