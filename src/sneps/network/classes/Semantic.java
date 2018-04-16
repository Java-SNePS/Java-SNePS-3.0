package sneps.network.classes;

import java.io.Serializable;
import java.util.LinkedList;

public class Semantic implements Serializable {
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

	public boolean isAsserted(Object contextByName) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
