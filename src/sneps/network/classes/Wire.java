package sneps.network.classes;

import java.io.Serializable;

import sneps.exceptions.CustomException;
import sneps.exceptions.DuplicateNodeException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.Node;

public class Wire implements Serializable{

	private Relation wireRelation;
	private Node wireNode;

	/**
	 * first constructor that assigns the wire node and builds a node instance
	 * according to the passed parameters
	 * 
	 * @param wireRelation
	 * @param nodeId
	 * @param syntacticType
	 * @param semanticType
	 * 
	 * @throws CustomException
	 * @throws NotAPropositionNodeException 
	 * @throws NodeNotFoundInNetworkException 
	 * @throws DuplicateNodeException 
	 */

	public Wire(Relation wireRelation, String nodeId, String syntacticType, String semanticType)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicateNodeException {

		this.wireRelation = wireRelation;

		if (Network.getNodes().containsKey(nodeId))
			throw new DuplicateNodeException("This node's identifier already exists");

		else {

			if (syntacticType == "Base") {
				this.wireNode = Network.buildBaseNode(nodeId, new Semantic(semanticType));
			}

		}
	}

	/**
	 * a second constructor used to build a wire with a relation pointing to
	 * variable node
	 * 
	 * @param wireRelation
	 * 
	 * @throws CustomException
	 */

	public Wire(Relation wireRelation) throws CustomException {

		this.wireRelation = wireRelation;
		this.wireNode = Network.buildVariableNode();

	}

	/**
	 * a second constructor used to build a wire with a relation pointing to
	 * variable node with a given semantic type
	 * 
	 * @param wireRelation
	 * 
	 * @throws CustomException
	 */

	public Wire(Relation wireRelation, Semantic semantic) throws CustomException {

		this.wireRelation = wireRelation;
		this.wireNode = Network.buildVariableNode(semantic);

	}

	/**
	 * a third constructor that accepts a node object as a parameter in case of
	 * creating two wires with different relations and one common node
	 * 
	 * @param wireRelation
	 * 
	 * @param node
	 */

	public Wire(Relation wireRelation, Node node) {

		this.wireRelation = wireRelation;
		this.wireNode = node;
	}

	public Node getWireNode() {
		return wireNode;
	}

	public Relation getWireRelation() {
		return wireRelation;
	}

}