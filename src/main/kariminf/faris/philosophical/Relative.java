/* FARIS : Factual Arrangement and Representation of Ideas in Sentences
 * FAris : Farabi & Aristotle
 * Faris : A knight (in Arabic)
 * --------------------------------------------------------------------
 * Copyright (C) 2015 Abdelkrime Aries (kariminfo0@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package kariminf.faris.philosophical;

import kariminf.faris.linguistic.Adjective;
import kariminf.faris.process.Generator;

/**
 * Relative or relation (πρός τι, pros ti, toward something). 
 * This is the way one object may be related to another. Examples: double, half, 
 * large, master, knowledge.
 * 
 * @author Abdelkrime Aries (kariminfo0@gmail.com)
 *         <br>
 *         Copyright (c) 2015-2017 Abdelkrime Aries
 *         <br><br>
 *         Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <br><br>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         <br><br>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
public class Relative extends Being{
	
	
	//The relative is:
	//  Possession: "son of someone"
	//	Comparison: taller than
	//Issue #9
	public static enum RelativeType {
		OF, // other relation defined by the preposition
		MORE,
		LESS,
		MOST,
		LEAST,
		EQUAL
	}
	
	//The owner can be a substance: the man is taller than the boy
	private QuantSubstance owner;
	//The owner can be an action: Karim worked harder than his colleague.
	private Action actOwner;
	
	private RelativeType relationType;
	private QuantSubstance relative;
	
	//He works more than Me.
	private Adjective adjective;
		
	private Relative(RelativeType type, Adjective relation, QuantSubstance relative){
		this.relationType = type;
		this.adjective = relation;
		this.relative = relative;
	}
	
	/**
	 * Creates a Relative OF. For example: the mother of the child.
	 * @param owner the owner of the relation; in the example: the mother
	 * @param relative the relative; in the example: the child
	 * @return null if the owner or the relative are null; or a new relative
	 */
	public static Relative getNew(QuantSubstance owner, QuantSubstance relative){

		if (owner == null) return null;
		if (relative == null) return null;
		
		Relative result = new Relative(RelativeType.OF, null, relative);
		result.owner = owner;
		
		return result;
	}
	
	/**
	 * Comparison relative: He is taller than me
	 * @param type
	 * @param relation
	 * @return
	 */
	public static Relative getNew(RelativeType type, Adjective relation, 
			Action owner, QuantSubstance relative){
		
		//This type of relative is for comparison
		if (type == RelativeType.OF) return null;
		if (owner == null) return null;
		
		
		Relative result = new Relative(type, relation, relative);
		result.actOwner = owner;
		
		return result;
	}
	
	
	/**
	 * Comparison relative: He is taller than me
	 * @param type
	 * @param relation
	 * @return
	 */
	public static Relative getNew(RelativeType type, Adjective relation, 
			QuantSubstance owner, QuantSubstance relative){
		
		//This type of relative is for comparison
		if (type == RelativeType.OF) return null;
		if (owner == null) return null;
		if (relative == null) return null;
		//Here, we must have an adjective
		if (relation == null) return null;
		
		
		Relative result = new Relative(type, relation, relative);
		result.owner = owner;
		
		return result;
	}
	
	public Action getOwnerAction(){
		return actOwner;
	}
	
	public QuantSubstance getOwnerSubstance(){
		return owner;
	}
	
	public QuantSubstance getRelative(){
		return owner;
	}
	
	public RelativeType getRelativeType(){
		return relationType;
	}
	
	public Adjective getAdjective(){
		return adjective;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		String result = "R:";
		
		if (relationType == RelativeType.OF){
			result += "OF{" + owner + "}";
			return result;
		} 
		
		result += relationType + "{";
		result += (adjective == null)? "": adjective;
		result += "}." + relative;
		
		return result;
	}
	

	@Override
	public void generate(Generator gr) {
		// TODO Auto-generated method stub
		
	}
	
	

}
