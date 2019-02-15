package sneps.exceptions;

import sneps.network.Node;

public class EquivalentNodeException extends Exception {

	private Node equivalentNode;

	public EquivalentNodeException(String message, Node equivalentNode) {
		super(message);
		this.equivalentNode = equivalentNode;
	}

	public Node getEquivalentNode() {
		return equivalentNode;
	}

}
