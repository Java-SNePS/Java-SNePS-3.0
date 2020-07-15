package sneps.network.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import sneps.exceptions.SemanticNotFoundInNetworkException;

public class Semantic implements Serializable {
	protected String semanticType;
	public String superClass;
	public ArrayList<String> superClassSet;
	public static Semantic act, proposition, entity, individual;
	public static Semantic infimum;
	

	public Semantic() {
	}

	public Semantic(String sem) {
		semanticType = sem;
		superClass = null;
	}
	
	public Semantic(String sem, String superClass) {
		semanticType = sem;
		this.superClass = superClass;
	}
	
	public Semantic(String sem, ArrayList<String> Semantics) {
		semanticType = sem;
		this.superClassSet = Semantics;
	}

	public LinkedList<Object> getSuperClassesNames() {
		// this list will get populated with all the names of all the parent classes to this class
		LinkedList<Object> superClassesNames = new LinkedList<Object>();
		
		// get all the super classes for this class recursively
		getSuperClassesNamesRecursively(superClassesNames, this);
		
		return superClassesNames;
		
	}
	
	private void getSuperClassesNamesRecursively(LinkedList<Object> superClassesNames, Semantic currentSemantic) {
		if(currentSemantic.superClass == null)
			return;
		
		superClassesNames.add(currentSemantic.superClass);
		
		try {
			getSuperClassesNamesRecursively(superClassesNames, SemanticHierarchy.getSemantic(currentSemantic.superClass));
		}
		catch(Exception e) {
			return;
		}
	}

	public String getSemanticType() {
		return semanticType;
	}
	
	public ArrayList<String> getSuperClassSet() {
		return superClassSet;
	}

	public boolean isAsserted(Object contextByName) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void createDefaultSemantics() throws SemanticNotFoundInNetworkException {
		
		entity = SemanticHierarchy.createSemanticType("Entity");
		infimum = SemanticHierarchy.createSemanticType("Infimum"); // FOR TESTING ONLY
		act = SemanticHierarchy.createSemanticType("Act","Entity");
		proposition = SemanticHierarchy.createSemanticType("Proposition","Entity");
		individual = SemanticHierarchy.createSemanticType("Individual","Entity");
		
	}


}
