package sneps.setClasses;

import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TreeMap;

import sneps.network.PropositionNode;

public class PropositionSet implements Iterable<PropositionNode> {
	private	TreeMap<Integer, PropositionNode> nodes;

	public PropositionSet() {
		nodes = new TreeMap<Integer, PropositionNode>();
		
	}
	public void insert(PropositionNode propNode) {
		nodes.put(propNode.getId(), propNode);
	}
	public void remove(PropositionNode propNode) {
		nodes.remove(propNode.getId());

	}
	public boolean contains(PropositionNode propNode){
		return nodes.containsKey(propNode.getId());
	}
	public boolean isEmpty(){
		return nodes.isEmpty();
	}
	public void clearSet() {
		nodes.clear();
	}
	@Override
	public Iterator<PropositionNode> iterator() {
		return nodes.values().iterator();
	}
	
}