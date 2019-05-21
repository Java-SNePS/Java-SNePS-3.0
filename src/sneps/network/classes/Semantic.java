package sneps.network.classes;

import java.io.Serializable;
import java.util.LinkedList;

import sneps.exceptions.SemanticNotFoundInNetworkException;

public class Semantic implements Serializable {
	protected String semanticType;
	private String superClass;
	public static Semantic act, proposition, infimum, individual;

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

	public boolean isAsserted(Object contextByName) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void createDefaultSemantics() {
		act = SemanticHierarchy.createSemanticType("Act");
		proposition = SemanticHierarchy.createSemanticType("Proposition");
		infimum = SemanticHierarchy.createSemanticType("Infimum");
		individual = SemanticHierarchy.createSemanticType("Individual");
	}

}
