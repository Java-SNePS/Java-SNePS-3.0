package sneps.network;

import sneps.network.classes.Term;

public class Node {
	
	private Term term;
	private String semanticType; 
	private static int count=0;
	private int id;
	
	public Node() {	}
	
	public Node(Term trm){
		term = trm;
		id = count++;		
	}

	
	
	
	
	
	
	public Term getTerm() {
		return term;
	}
	public void setTerm(Term term) {
		this.term = term;
	}
	public String getSemanticType() {
		return semanticType;
	}
	public void setSemanticType(String semanticType) {
		this.semanticType = semanticType;
	}
	public static int getCount() {
		return count;
	}
	public static void setCount(int count) {
		Node.count = count;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
