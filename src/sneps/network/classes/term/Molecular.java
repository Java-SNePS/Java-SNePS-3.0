package sneps.network.classes.term;

import java.io.Serializable;
import java.util.Enumeration;

import sneps.network.Node;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.cables.UpCable;
import sneps.network.classes.Relation;
import sneps.network.classes.setClasses.NodeSet;

public class Molecular extends Term implements Serializable{
	protected DownCableSet downCableSet;

	/**
	 * The constructor of this class.
	 * 
	 * @param identifier
	 * 			the name of the node that will be created.
	 * @param downCableSet
	 * 			the down cable set of the node  that will be
	 * 			created.
	 */
	public Molecular(String identifier, DownCableSet downCableSet){
		super(identifier);
		this.downCableSet = downCableSet;
	}

	public DownCableSet getDownCableSet() {
		return this.downCableSet;
	}
	
	/**
	 * This method overrides the default toString method inherited from the Term class.
	 * 
	 * @return a string representing the name of the current node + the to string of the 
	 * 	down cable set.
	 */
	@Override
	public String toString(){
		return this.getIdentifier()+":("+this.getDownCableSet().toString()+")";
	}
	
	/**
	 * A method that adds node object, that have the current molecular
	 * 	object as its syntactic object, to the suitable up cable of all 
	 * 	the nodes dominated by the current node.
	 * 
	 * @param node
	 * 			the node object having the current molecular object
	 * 			as its syntactic object.
	 */
	public void updateUpCables(Node node) {
		DownCableSet dCableSet = this.getDownCableSet();
		Enumeration<DownCable> dCables = dCableSet.getDownCables().elements();
		while(dCables.hasMoreElements()){
			DownCable dCable = dCables.nextElement();
			Relation r = dCable.getRelation();
			NodeSet ns = dCable.getNodeSet();
			for (int j = 0; j < ns.size(); j++){
				Node n = ns.getNode(j);
				if (!n.getUpCableSet().contains(r))
					n.getUpCableSet().addUpCable(new UpCable(r));
				n.getUpCableSet().getUpCable(r.getName()).addNode(node);
			}
		}
	}
	
}
