package sneps.network.classes.setClasses;

import sneps.network.PropositionNode;

public class PropositionSet {
	private int[] nodes;
	private String hash;

	public PropositionSet() {
		nodes = new int[0];
	}

	public PropositionSet(PropositionSet propSet, PropositionNode propNode) {
		int[] old = propSet.getNodes();
		nodes = new int[old.length + 1];
		hash = "";
		int j = 0;
		for (int i = 0; i < nodes.length; i++) {
			if (propNode.getId() < old[i]) {
				nodes[j] = propNode.getId();
				hash += propNode.getId() + ", ";
				j++;
				i--;
			} else {
				nodes[j] = old[i];
				hash += old[i] + ", ";
				j++;
			}
		}
		nodes[old.length + 1] = propNode.getId();
		hash += propNode.getId();
	}

	public PropositionSet(PropositionSet propSet) {
		int[] old = propSet.getNodes();
		nodes = new int[old.length];
		nodes = propSet.getNodes();
		hash = propSet.getHash();
	}

	public PropositionSet add(PropositionNode propNode) {
		PropositionSet temp = new PropositionSet(this, propNode);
		return temp;
	}

	public void remove(PropositionNode propNode) {
		int[] current = this.getNodes();
		int[] newSet = new int[current.length - 1];
		int j = 0;
		boolean found = false;
		for (int i = 0; i < current.length; i++) {
			if (propNode.getId() < current[i] && !found)
				return;// Have to throw NotFound Exception or even return false
			if (!propNode.equals(current[i])) {
				newSet[j] = current[i];
				j++;
			} else {
				found = true;
			}
		}
		this.setNodes(newSet);
	}

	public boolean equals(PropositionNode propNode) {
		int[] current = this.getNodes();
		for (int i = 0; i < current.length; i++) {
			if (propNode.getId() < current[i])
				return false;
			if (propNode.equals(current[i]))
				return true;
		}
		return false;
	}

	public boolean isSubSet(PropositionSet propSet) {
		int[] large = propSet.getNodes();
		int[] small = this.getNodes();
		if (large.length < small.length)
			return false;
		int j = 0;
		for (int i = 0; i < large.length; i++) {
			if (j == small.length)
				break;
			if (large[i] == small[j]) {
				j++;
			} else {
				if (j != 0 && j < small.length)
					return false;
			}
		}
		if (j == small.length)
			return true;
		return false;
	}

	public boolean isEmpty() {
		return nodes.length == 0;
	}

	public void clearSet() {
		nodes = new int[0];
	}

	public int[] getNodes() {
		return nodes;
	}

	private void setNodes(int[] newNodes) {
		nodes = newNodes;
	}

	public String getHash() {
		return hash;
	}

}