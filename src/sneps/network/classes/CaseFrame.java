package sneps.network.classes;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;

import sneps.exceptions.CustomException;
import sneps.network.Network;

public class CaseFrame implements Serializable { 
	
	
	public static CaseFrame act,planGoal,preconditionAct,actEffect,planAct,doAllAct,SNSequenceAct,
	guardAct,SNIF,propertyObject,withSome;

	//public static CaseFrame planGoal;

	private String semanticClass; 
	
	private LinkedList<Relation> relations; 
	
	private String id;
	
	
	
	public CaseFrame(String semanticClass, LinkedList<Relation> relations){
			
		   this.semanticClass = semanticClass;
		   this.relations = relations;
		   this.id = createId(relations);
	} 
	
	/**
	 * A method that is invoked by the constructor to create the ID of the newly
	 * created case frame.
	 * 
	 * @param r
	 *            a linked list of RCFP (Relation case frame properties) for all
	 *            the relations included in the current case frame.
	 * 
	 * @return the ID of the newly created case frame.
	 */
	private String createId(LinkedList<Relation> r) {
		String id = "";
		LinkedList<String> relationNames = new LinkedList<String>();
		for (int i = 0; i < r.size(); i++) {
			relationNames.add(r.get(i).getName());
		}
		Collections.sort(relationNames);
		for (int i = 0; i < relationNames.size(); i++) {
			if (i == 0) {
				id = id.concat(relationNames.get(i));
			} else {
				id = id.concat(",").concat(relationNames.get(i));
			}
		}
		return id;
	}

	public String getSemanticClass() {
		return semanticClass;
	}

	

	public LinkedList<Relation> getRelations() {
		return relations;
	}

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	} 
	
	public static void createDefaultCaseFrames() throws CustomException {
	LinkedList<Relation> actCF = new LinkedList<Relation>();
	actCF.add(Relation.action);
	actCF.add(Relation.obj);
	act = Network.defineCaseFrame("Act", actCF);
	
	LinkedList<Relation> doAllactCF = new LinkedList<Relation>();
	doAllactCF.add(Relation.action);
	doAllactCF.add(Relation.doo);
	doAllAct= Network.defineCaseFrame("Act", doAllactCF);
	
	LinkedList<Relation> SNSequenceActactCF = new LinkedList<Relation>();
	SNSequenceActactCF.add(Relation.action);
	SNSequenceActactCF.add(Relation.doo);
	SNSequenceAct= Network.defineCaseFrame("Act", SNSequenceActactCF);
	
	
	LinkedList<Relation> plangoal = new LinkedList<Relation>();
	plangoal.add(Relation.plan);
	plangoal.add(Relation.goal);
	planGoal = Network.defineCaseFrame("Proposition", plangoal);
	
	LinkedList<Relation> preAct = new LinkedList<Relation>();
	preAct.add(Relation.precondition);
	preAct.add(Relation.act);
	preconditionAct=Network.defineCaseFrame("Proposition",preAct);
	
	LinkedList<Relation> acteffect = new LinkedList<Relation>();
	acteffect.add(Relation.act);
	acteffect.add(Relation.effect);
	actEffect=Network.defineCaseFrame("Proposition", acteffect);

	LinkedList<Relation> planact = new LinkedList<Relation>();
	planact.add(Relation.plan);
	planact.add(Relation.act);
	planAct = Network.defineCaseFrame("Proposition", planact);
	
	LinkedList<Relation> guardact = new LinkedList<Relation>();
	guardact.add(Relation.guard);
	guardact.add(Relation.act);
	guardAct=  Network.defineCaseFrame("Proposition", guardact);
	
	LinkedList<Relation> SNIFArr = new LinkedList<Relation>();
	SNIFArr.add(Relation.action);
	SNIFArr.add(Relation.obj);
	SNIF= Network.defineCaseFrame("Act", SNIFArr);
	
	LinkedList<Relation> propertyObjectArr = new LinkedList<Relation>();
	propertyObjectArr.add(Relation.property);
	propertyObjectArr.add(Relation.obj);
	propertyObject= Network.defineCaseFrame("Proposition", propertyObjectArr);
	
	LinkedList<Relation> withSomeArr = new LinkedList<Relation>();
	withSomeArr.add(Relation.suchthat);
	withSomeArr.add(Relation.vars);
	withSomeArr.add(Relation.doo);
	withSomeArr.add(Relation.action);
	withSome= Network.defineCaseFrame("Act", withSomeArr);
}
}