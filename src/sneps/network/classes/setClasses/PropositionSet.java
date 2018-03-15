package sneps.network.classes.setClasses;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import sneps.network.PropositionNode;

public class PropositionSet implements Iterable<PropositionNode> {
	private Set<PropositionNode> nodes;

	public PropositionSet() {
		nodes = new HashSet<PropositionNode>();
	}

	public PropositionSet(Set<PropositionNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public Iterator<PropositionNode> iterator() {
		return nodes.iterator();
	}

	public Set<PropositionNode> getNodes() {
		return nodes;
	}

	public boolean add (PropositionNode p) {
		return nodes.add(p);
	}

	public boolean addAll(Set<PropositionNode> props) {
		return nodes.addAll(props);
	}

	public boolean remove(PropositionNode prop) {
		return nodes.remove(prop);
	}

	@Override
	public boolean equals(Object obj) {
		PropositionSet props = (PropositionSet)obj;
		for (PropositionNode node: props.nodes) {
			if(!this.nodes.contains(node))
				return false;
		}
		return true;
	}

	public boolean isSubSet(PropositionSet hyps) {
		for (PropositionNode node: this.nodes) {
			if (!hyps.nodes.contains(node))
				return false;
		}
		return true;
	}
}