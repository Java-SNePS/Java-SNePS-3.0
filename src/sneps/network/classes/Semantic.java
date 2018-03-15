package sneps.network.classes;

import java.util.LinkedList;

public class Semantic {
	protected String semanticType; 

	public Semantic() {	}
	
	public Semantic(String sem){
		semanticType = sem;
	}

	public LinkedList<Object[]> getSuperClassesNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSemanticType() {
		return semanticType;
	}

	
	
}
