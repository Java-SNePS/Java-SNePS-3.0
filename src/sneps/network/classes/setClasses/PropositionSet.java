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
}