package sneps.network.classes;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;

import sneps.exceptions.CustomException;
import sneps.network.Network;

public class CaseFrame implements Serializable { 
	
	
	public static CaseFrame act;

	public static CaseFrame planGoal;

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
	
	LinkedList<Relation> plangoal = new LinkedList<Relation>();
	plangoal.add(Relation.plan);
	plangoal.add(Relation.goal);
	planGoal = Network.defineCaseFrame("Proposition", plangoal);
	}
	

}