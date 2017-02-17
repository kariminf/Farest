/* FARIS : Factual Arrangement and Representation of Ideas in Sentences
 * FAris : Farabi & Aristotle
 * Faris : A knight (in Arabic)
 * --------------------------------------------------------------------
 * Copyright (C) 2017 Abdelkrime Aries (kariminfo0@gmail.com)
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

package kariminf.faris.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import kariminf.faris.knowledge.Mind.MentalState;
import kariminf.faris.linguistic.*;
import kariminf.faris.philosophical.*;

/**
 * A generator which generates a text or any thing from a Faris representation (model)
 * @author Abdelkrime Aries
 *
 */
public abstract class Generator<T> {
	
	public static final String ACTION = "a";
	public static final String ROLE = "r";

	private HashMap<Action, Integer> actionIDs = new HashMap<Action, Integer>();
	private int actionsNbr = 0;
	
	private HashMap<Substance, Integer> substanceIDs = new HashMap<>();
	private HashMap<QuantSubstance, Integer> qsubstanceIDs = new HashMap<>();
	
	private int substancesNbr = 0;

	/**
	 * 
	 * @param action
	 */
	public void processAction(Action action){

		//We don't add an action, already there
		if (actionIDs.containsKey(action)) return;

		actionIDs.put(action, actionsNbr);
		String actID = ACTION + actionsNbr;
		actionsNbr++;

		beginActionHandler(actID, action.getVerb(), action.getAdverbs());

		beginAgentsHandler();
		processDisjunctions(action.getAgents());
		endAgentsHandler();

		beginThemesHandler();
		processDisjunctions(action.getThemes());
		endThemesHandler();

		endActionHandler(actID);


	}

	private void processDisjunctions(ArrayList<ArrayList<QuantSubstance>> disj){
		//Disjunctions 
		for(ArrayList<QuantSubstance> conj: disj){
			beginDisjunctionHandler();
			for (QuantSubstance substance: conj){
				substance.generate(this);
			}

			endDisjunctionHandler();
		}
	}
	
	private void addSubstance(String id, Substance sub, Quantity q){
		
		beginSubstanceHandler(id, sub.getNoun());
		
		if (q != null) q.generate(this);
		for (Quality ql : sub.getQualities()) ql.generate(this);
		endSubstanceHandler();
	}
	
	public void processQuality(Quality ql){
		addQualityHandler(ql.getAdjective(), ql.getAdverbs());
	}
	
	public void processQuantity(Quantity q){
		Noun unit = (q.getUnit() == null)? null: q.getUnit().getNoun();
		addQuantityHandler(q.getNumber(), unit);
	}

	public void processSubstance(QuantSubstance qsub){
		String id = ROLE + substancesNbr;
		if (qsubstanceIDs.containsKey(qsub)){
			substanceFoundHandler(id);
			return;
		}
		substancesNbr++;
		qsubstanceIDs.put(qsub, substancesNbr);
		addSubstance(id, qsub.getSubstance(), qsub.getQuantity());
	}

	public void processSubstance(Substance sub){
		String id = ROLE + substancesNbr;
		if (substanceIDs.containsKey(sub)){
			substanceFoundHandler(id);
			return;
		}
		substancesNbr++;
		substanceIDs.put(sub, substancesNbr);
		addSubstance(id, sub, null);
	}
	
	public void processMind(QuantSubstance s){
		//TODO When we already processed a substance, and found it was referenced again
	}
	
	public void processMentalState(MentalState ms){
		//A mental state of a mind
	}
	
	
	//Abstract methods
	//=====================
	
	/**
	 * When an action is found, this method will be called
	 * @param id the ID of the action
	 * @param verb the verb describing the action
	 * @param adverbs the adverbs modifying the verb
	 */
	protected abstract void beginActionHandler(String id, Verb verb, Set<Adverb> adverbs);

	/**
	 * This is called when the action ends (all its components have been processed
	 * @param id The ID of the action
	 */
	protected abstract void endActionHandler(String id);

	/**
	 * This is called to mark the start of the current action's agents enumeration
	 */
	protected abstract void beginAgentsHandler();

	/**
	 * This is called to mark the end of current action's agents enumeration
	 */
	protected abstract void endAgentsHandler();

	/**
	 * This is called to mark the start of current action's themes enumeration
	 */
	protected abstract void beginThemesHandler();

	/**
	 * This is called to mark the end of current action's themes enumeration
	 */
	protected abstract void endThemesHandler();

	/**
	 * This is called whenever there is an enumeration; each time it is called 
	 * it marks a disjunction "OR". The components called after are conjunctions "AND"
	 */
	protected abstract void beginDisjunctionHandler();

	/**
	 * This is called when the disjunction of elements is over, and to start a new disjunction
	 * if there is any
	 */
	protected abstract void endDisjunctionHandler();

	//If the substance has a noun with synset 0, so it is the pronoun it
	//for example it is believed
	/**
	 * This is called when a substance is found; A substance and a quantified substance
	 * identical to it are considered as two distinct substances
	 * @param id the ID of the substance
	 * @param noun the noun, which can of type ProperNoun as well
	 */
	protected abstract void beginSubstanceHandler(String id, Noun noun);
	
	/**
	 * This is called when a substance is found, but it was already processed earlier
	 * @param id the ID of the substance
	 */
	protected abstract void substanceFoundHandler(String id);
	
	/**
	 * This is called when an action is found, but it was already processed earlier
	 * @param id
	 */
	protected abstract void actionFoundHandler(String id);
	
	/**
	 * This marks the end of a substance processing
	 */
	protected abstract void endSubstanceHandler();

	/**
	 * This is called when we want to add a quantity to the current substance
	 * @param nbr The quantity
	 * @param unit the unit of the quantity
	 */
	protected abstract void addQuantityHandler(double nbr, Noun unit);
	
	/**
	 * This is called when we want to add a quality to the current substance
	 * @param adjective the adjective that describes this quality
	 * @param adverbs the adverbs modifying this adjective
	 */
	protected abstract void addQualityHandler(Adjective adjective, ArrayList<Adverb> adverbs);
	
	/**
	 * This is called when an Idea has been found
	 */
	protected abstract void beginIdeaHandler();
	
	/**
	 * This is called to mark the end of an idea
	 */
	protected abstract void endIdeaHandler();
	
	/**
	 * This is called to generate a representation of a given type 
	 * @return
	 */
	public abstract T generate();
}
