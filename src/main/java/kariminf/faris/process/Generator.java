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

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kariminf.faris.knowledge.Mind.MentalState;
import kariminf.faris.linguistic.*;
import kariminf.faris.philosophical.*;
import kariminf.faris.philosophical.Action.ActionWrapper;
import kariminf.faris.philosophical.Place.PlaceWrapper;
import kariminf.faris.philosophical.Quality.QualityWrapper;
import kariminf.faris.philosophical.QuantSubstance.QSubstanceWrapper;
import kariminf.faris.philosophical.Quantity.QuantityWrapper;
import kariminf.faris.philosophical.Relative.RelativeType;
import kariminf.faris.philosophical.Relative.RelativeWrapper;
import kariminf.faris.philosophical.State.StateWrapper;
import kariminf.faris.philosophical.Substance.SubstanceWrapper;
import kariminf.faris.philosophical.Time.TimeWrapper;
import kariminf.faris.tools.ConjunctedSubstances;
import kariminf.sentrep.types.Comparison;
import kariminf.sentrep.types.Relation.Adpositional;

/**
 * A generator which generates a text or any thing from a Faris representation (model)
 * @author Abdelkrime Aries
 *
 */
public abstract class Generator<T> {
	
	public static final String ACTION = "a";
	public static final String ROLE = "r";
	
	private ArrayDeque<QuantSubstance> currentMinds = new ArrayDeque<>();

	private HashMap<Action, Integer> actionIDs = new HashMap<Action, Integer>();
	private int actionsNbr = 0;
	
	private HashMap<Substance, Integer> substanceIDs = new HashMap<>();
	private HashMap<QuantSubstance, Integer> qsubstanceIDs = new HashMap<>();
	
	//A substance can have many IDs according to the states 
	//private HashMap<QuantSubstance, List<HashMap<Integer, List<State>>>> qsubstanceIDs = new HashMap<>();
	
	private int substancesNbr = 0;
	
	private MentalState mentalState;
	
	private boolean isMainIdea = false;
	
	private Action currentAction;
	
	private QuantSubstance currentSubstance;
	
	
	public void processRelative(RelativeWrapper wrapper){
		Comparison cmp = RelativeType.toComparison(wrapper.relationType);
		
		//System.out.println("Generator.processRelative: " + wrapper.relSubstance);
		
		//If the relative substance is not defined already, no need to process
		if (! qsubstanceIDs.containsKey(wrapper.relSubstance)){
			wrapper.relSubstance.generate(this);
			//System.out.println("created substance" + qsubstanceIDs.get(wrapper.relSubstance));
		}
		
		String relID = ROLE + qsubstanceIDs.get(wrapper.relSubstance);
		
		//It is an OF relation, between current substance and another
		/*if (cmp == null){
			//if (wrapper.owner == null || wrapper.owner != currentSubstance) return;
			addRelative(null, null, relID);
			//System.out.println("relative OF called");
		}*/
		
		//if (wrapper.actOwner == null && wrapper.actOwner != currentAction) return;
		
		addRelative(cmp, wrapper.adjective, relID);
		
	}
	
	public void processPlace(PlaceWrapper wrapper){
		beginPlaceHandler(wrapper.relation, wrapper.adv);
		//System.out.println("Generator: Place=" + relation);
		if (wrapper.places != null && !wrapper.places.isEmpty()){
			Set<ConjunctedSubstances> disj = new HashSet<>();
			ConjunctedSubstances conj = new ConjunctedSubstances();
			conj.addAll(wrapper.places);
			disj.add(conj);
			processDisjunctions(disj);
		}
		
		endPlaceHandler(wrapper.relation, wrapper.adv);
	}
	
	public void processTime(TimeWrapper wrapper){
		beginTimeHandler(wrapper.relation, wrapper.adv, wrapper.datetime);
		
		if (wrapper.times != null && !wrapper.times.isEmpty()){
			Set<ConjunctedSubstances> disj = new HashSet<>();
			ConjunctedSubstances conj = new ConjunctedSubstances();
			conj.addAll(wrapper.times);
			disj.add(conj);
			processDisjunctions(disj);
		}
		endTimeHandler(wrapper.relation, wrapper.adv, wrapper.datetime);
	}

	/**
	 * 
	 * @param action
	 */
	public void processAction(ActionWrapper wrapper){
		currentAction = wrapper.action;
		//We don't add an action, already there
		if (actionIDs.containsKey(wrapper.action)){
			/*if (isMainIdea && currentMinds.peek().getSubstance().getNounSynSet() == 0){
				System.out.println("main sentence1");
				String actID = ACTION + actionIDs.get(action);
				addIdeaHandler(actID);
				isMainIdea = false;
			}*/
			String actID = ACTION + actionIDs.get(wrapper.action);
			actionFoundHandler(actID);
			return;
		}
		
		Action tmpLastAction = currentAction;
		QuantSubstance tmpSubstance = currentSubstance;

		actionIDs.put(wrapper.action, actionsNbr);
		String actID = ACTION + actionsNbr;
		actionsNbr++;

		beginActionHandler(actID, wrapper.verb, wrapper.adverbs);
		currentAction = tmpLastAction;
		currentSubstance = tmpSubstance;

		beginAgentsHandler(actID);
		processDisjunctions(wrapper.doers);
		endAgentsHandler(actID);
		
		currentAction = tmpLastAction;
		currentSubstance = tmpSubstance;

		beginThemesHandler(actID);
		processDisjunctions(wrapper.receivers);
		endThemesHandler(actID);
		
		currentAction = tmpLastAction;
		currentSubstance = tmpSubstance;
		
		for(Place place: wrapper.locations) place.generate(this);
		
		currentAction = tmpLastAction;
		currentSubstance = tmpSubstance;
		
		for(Time time: wrapper.times) time.generate(this);
		
		currentAction = tmpLastAction;
		currentSubstance = tmpSubstance;
		
		beginActionRelativeHandler(actID);
		for (Relative relative: wrapper.relatives){
			relative.generate(this);
		}
		endActionRelativeHandler(actID);
			
		endActionHandler(actID, wrapper.verb, wrapper.adverbs);
		
		if (isMainIdea && currentMinds.peek().getSubstance().getNounSynSet() == 0){
			//System.out.println("main sentence");
			addIdeaHandler(actID);
			isMainIdea = false;
		}
		
		currentAction = tmpLastAction;
		currentSubstance = tmpSubstance;

	}
	

	public void processState(StateWrapper wrapper){
		
		if(!wrapper.mainActions.contains(currentAction)) return;
		
		boolean isAgent = wrapper.stateAction.hasAgent(currentSubstance);
		boolean isTheme = wrapper.stateAction.hasTheme(currentSubstance);
		if(!( isAgent || isTheme )) return;
		
		Action tmpLastAction = currentAction;
		QuantSubstance tmpSubstance = currentSubstance;
		
		Action stateAction = wrapper.stateAction;
		if (isAgent)  stateAction= stateAction.copyAgentTheme(false, true);
		if (isTheme)  stateAction= stateAction.copyAgentTheme(true, false);
		
		stateAction.generate(this);
		
		String actID = ACTION + actionIDs.get(stateAction);
		
		
		addStateHandler(isAgent, actID);
		
		currentAction = tmpLastAction;
		currentSubstance = tmpSubstance;
	}

	private void processDisjunctions(Set<ConjunctedSubstances> disjSub){
		//Disjunctions 
		for(ConjunctedSubstances conj: disjSub){
			beginDisjunctionHandler();
			for (QuantSubstance substance: conj){
				substance.generate(this);
			}

			endDisjunctionHandler();
		}
	}
	
	/*private void addSubstance(String id, Substance sub, Quantity pl, Quantity nbr){
		
		beginSubstanceHandler(id, sub.getNoun());
		
		if(pl != null) pl.generate(this);
		
		if(nbr != null) nbr.generate(this);
		
		for (Quality ql : sub.getQualities()) ql.generate(this);
		endSubstanceHandler();
	}*/
	
	public void processQuality(QualityWrapper wrapper){
		addQualityHandler(wrapper.adjective, wrapper.adverbs);
	}
	
	public void processQuantity(QuantityWrapper wrapper){
		Noun unit = (wrapper.unit == null)? null: wrapper.unit.getNoun();
		if(wrapper.plural) addQuantityHandler(unit);
		else addQuantityHandler(wrapper.nbr, unit, wrapper.cardinal);
	}

	
	public void processSubstance(QSubstanceWrapper wrapper){
		currentSubstance = wrapper.qsubstance;
		
		Action tmpLastAction = currentAction;
		//QuantSubstance tmpSubstance = currentSubstance;
		if (qsubstanceIDs.containsKey(wrapper.qsubstance)){
			String subID = ROLE + qsubstanceIDs.get(wrapper.qsubstance);
			substanceFoundHandler(subID);
			return;
		}
		
		String subID = ROLE + substancesNbr;
		
		
		List<HashMap<Integer, List<State>>> idStates = new ArrayList<>();
		
		qsubstanceIDs.put(wrapper.qsubstance, substancesNbr);
		
		substancesNbr++;
		
		beginSubstanceHandler(subID, wrapper.noun);
		
		if(wrapper.plQuantity != null) wrapper.plQuantity.generate(this);
		
		if(wrapper.nbrQuantity != null) wrapper.nbrQuantity.generate(this);
		
		for (Quality ql : wrapper.qualities) ql.generate(this);
		
		String actID = ACTION + actionIDs.get(tmpLastAction);
		
		beginStateHandler(subID, actID);
		for (State state: wrapper.states){
			
			state.generate(this);
		}
		endStateHandler(subID, actID);
		
		beginSubstanceRelativeHandler(subID);
		for (Relative relative: wrapper.relatives){
			relative.generate(this);
		}
		endSubstanceRelativeHandler(subID);
		
		
		endSubstanceHandler(subID, wrapper.noun);
		
		//currentAction = tmpLastAction;
		//currentSubstance = tmpSubstance;
	}

	public void processSubstance(SubstanceWrapper wrapper){
		
		if (substanceIDs.containsKey(wrapper.substance)){
			String subID = ROLE + substanceIDs.get(wrapper.substance);
			substanceFoundHandler(subID);
			return;
		}
		
		String subID = ROLE + substancesNbr;
		substanceIDs.put(wrapper.substance, substancesNbr);
		substancesNbr++;
		
		beginSubstanceHandler(subID, wrapper.noun);
		for (Quality ql : wrapper.qualities) ql.generate(this);
		endSubstanceHandler(subID, wrapper.noun);
	}
	
	/**
	 * This is called by {@link kariminf.faris.knowledge.Mind} to process the caller's mind
	 * @param s the Mind of the caller as a substance
	 */
	public void processMind(QuantSubstance s){
		currentMinds.push(s);
		
		//These cases when a substance or its noun are null may never happen
		//but as a security measure, I added the two checks
		Substance sub = s.getSubstance();
		if (sub == null) return;
		Noun n = sub.getNoun();
		if (n == null) return;
		
		//If the synset is null (0), it is the main mind
		if (n.getSynSet() == 0) return; 
		
		//Else, it is a new mind, so we have to process the substance
		
		s.generate(this);
	}
	
	public void endMindProcessing(QuantSubstance s){
		if (s == currentMinds.peek()) currentMinds.pop();
	}
	
	public void processMentalState(MentalState ms){
		//A mental state of a mind
		mentalState = ms;
	}
	
	/**
	 * Called by different Ideas to mark the first action as the main action
	 */
	public void mainIdea(){
		isMainIdea = true;
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
	protected abstract void endActionHandler(String id, Verb verb, Set<Adverb> adverbs);

	/**
	 * This is called to mark the start of the current action's agents enumeration
	 */
	protected abstract void beginAgentsHandler(String actID);

	/**
	 * This is called to mark the end of current action's agents enumeration
	 */
	protected abstract void endAgentsHandler(String actID);

	/**
	 * This is called to mark the start of current action's themes enumeration
	 */
	protected abstract void beginThemesHandler(String actID);

	/**
	 * This is called to mark the end of current action's themes enumeration
	 */
	protected abstract void endThemesHandler(String actID);

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
	protected abstract void endSubstanceHandler(String id, Noun noun);

	/**
	 * This is called when we want to add a quantity to the current substance
	 * @param nbr The quantity
	 * @param unit the unit of the quantity
	 */
	protected abstract void addQuantityHandler(double nbr, Noun unit, boolean cardinal);
	
	/**
	 * This is called when we want to add a quantity to the current substance. 
	 * It is used to say the substance is plural
	 * @param unit the unit of the quantity
	 */
	protected abstract void addQuantityHandler(Noun unit);
	
	/**
	 * This is called when we want to add a quality to the current substance
	 * @param adjective the adjective that describes this quality
	 * @param adverbs the adverbs modifying this adjective
	 */
	protected abstract void addQualityHandler(Adjective adjective, Set<Adverb> adverbs);
	
	/**
	 * This is called when an Idea has been found
	 */
	protected abstract void addIdeaHandler(String actionID);
	
	/**
	 * This is called when the current substance has a probable action state in one of 
	 * the relative actions in a substance
	 * @param isAgent if true, then the current substance is an agent, 
	 * otherwise it is a theme
	 * @param relIDs a list of probable relative actions
	 */
	protected abstract void addStateHandler(boolean isAgent, String stateID);
	
	protected abstract void beginStateHandler(String subID, String actID);
	
	protected abstract void endStateHandler(String subID, String actID);
	
	protected abstract void beginPlaceHandler(Adpositional relation, Adverb adv);
	
	protected abstract void endPlaceHandler(Adpositional relation, Adverb adv);
	
	protected abstract void beginTimeHandler(Adpositional relation, Adverb adv, LocalDateTime datetime);
	
	protected abstract void endTimeHandler(Adpositional relation, Adverb adv, LocalDateTime datetime);
	
	protected abstract void beginActionRelativeHandler(String actID);
	
	protected abstract void endActionRelativeHandler(String actID);
	
	protected abstract void beginSubstanceRelativeHandler(String subID);
	
	protected abstract void endSubstanceRelativeHandler(String subID);
	
	/**
	 * 
	 * @param cmp if it is null, then it is a relative OF between a substance and another
	 * @param adjective
	 * @param relID
	 */
	protected abstract void addRelative (Comparison cmp, Adjective adjective, String relID);
	
	/**
	 * This is called to generate a representation of a given type 
	 * @return
	 */
	public abstract T generate();
}