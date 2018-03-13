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

	@Override
	public Iterator<PropositionNode> iterator() {
		return nodes.iterator();
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
}