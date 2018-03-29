package sneps.network.classes;

import java.util.Collections;
import java.util.LinkedList;

public class CaseFrame { 
	
	
	private Semantic semanticClass; 
	
	private LinkedList<Relation> relations; 
	
	private String id;
	
	
	public CaseFrame(String semanticClass, LinkedList<Relation> relations){
		
		   this.semanticClass = new Semantic(semanticClass);
		   this.relations = relations;
		   this.id = createId(relations);
	} 
	
	public CaseFrame(Semantic semanticClass, LinkedList<Relation> relations){
			
		   this.semanticClass = new Semantic(semanticClass);
		   this.relations = relations;
		   this.id = createId(relations);
	}
	
	public CaseFrame(Semantic semanticClass, LinkedList<Relation> relations){
		
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

	public Semantic getSemanticClass() {
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
	
	

}
