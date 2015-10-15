/* Farest : Facts representation of sentences
 * ------------------------------------------
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
package dz.aak.faris.philosophical;

import java.util.HashSet;
import java.util.Set;

import dz.aak.faris.linguistic.Noun;

/**
 * Substance (οὐσία, ousia, essence or substance).
 * Substance is that which cannot be predicated of anything or be said to be in anything. 
 * Hence, this particular man or that particular tree are substances. Later in the text, 
 * Aristotle calls these particulars “primary substances”, to distinguish them from 
 * secondary substances, which are universals and can be predicated. Hence, Socrates is 
 * a primary substance, while man is a secondary substance. Man is predicated of Socrates, 
 * and therefore all that is predicated of man is predicated of Socrates.
 * 
 * @author kariminf
 *
 */
public class Substance {

	//a substance is a noun
	private Noun noun;
	
	
	//Here the substance is the subject (doer)
	private Set<Action> actions = new HashSet<Action>();
	
	//Here the substance is the object (receiver of the action)
	private Set<Action> affections = new HashSet<Action>();
	
	//Qualities
	private Set<Quality> qualities = new HashSet<Quality>();
	
	//Quanity (one)
	private Quantity quantity;
	
	
	public Substance(int nounSynSet) {
		noun = Noun.getNew(nounSynSet);
	}
	
	public void addAction(Action action){
		actions.add(action);
	}
	
	public void addAffection(Action action){
		affections.add(action);
	}
	
	public void addQuality(Quality quality){
		qualities.add(quality);
	}
	
	public void setQuantity(Quantity quantity){
		this.quantity = quantity;
	}

}