/**
 * 
 */
package edu.asu.nlu.knet.knowledge;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author arpit
 *
 */
public class Knowledge {
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private boolean eventKnowledge = false;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private boolean attrKnowledge = false;
	
}
