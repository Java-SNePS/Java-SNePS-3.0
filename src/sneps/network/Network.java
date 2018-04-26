package sneps.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import sneps.network.cables.Cable;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.cables.UpCable;
import sneps.network.classes.CFSignature;
import sneps.network.classes.CableTypeConstraint;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.PathTrace;
import sneps.network.classes.RCFP;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.Semantic;
import sneps.network.classes.SubDomainConstraint;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.VariableSet;
import sneps.network.classes.term.Base;
import sneps.network.classes.term.Closed;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Variable;
import sneps.exceptions.CannotBuildNodeException;
import sneps.exceptions.CaseFrameAlreadyExistException;
import sneps.exceptions.CaseFrameCannotBeRemovedException;
import sneps.exceptions.CaseFrameMissMatchException;
import sneps.exceptions.CaseFrameWithSetOfRelationsNotFoundException;
import sneps.exceptions.CustomException;
import sneps.exceptions.DuplicateNodeException;
import sneps.exceptions.NodeCannotBeRemovedException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.exceptions.RelationDoesntExistException;
import sneps.network.paths.FUnitPath;
import sneps.network.paths.Path;
import sneps.snebr.Context;
import sneps.snebr.Controller;

public class Network implements Serializable {
	
	 /* A hash table that stores all the nodes defined(available) in the network.
	 * Each entry is a 2-tuple having the name of the node as the key and the
	 * corresponding node object as the value.
	 */
	private static Hashtable<String, Node> nodes = new Hashtable<String, Node>();

	/* A hash table that stores all the proposition nodes defined(available) in the network.
	 * Each entry is a 2-tuple having the name of the node as the key and the
	 * corresponding proposition node object as the value.
	 */
	private static Hashtable<String, PropositionNode> propositionNodes = new Hashtable<String, PropositionNode>();
	
	/**
	 * an array list that stores all the nodes defined in the network. Each node
	 * is stored in the array list at the position corresponding to its ID.
	 */
	private static ArrayList<Node> nodesIndex = new ArrayList<Node>();

	/**
	 * A has hash table that contains all the molecular nodes defined in the
	 * network along with their case frames. Each entry is a 2-tuple having the
	 * string id of the case frame as the key and a node set containing the
	 * nodes that implement this case frame as the value.
	 */
	private static Hashtable<String, NodeSet> molecularNodes = new Hashtable<String, NodeSet>();

	/**
	 * A hash table that stores all the case frames defined in the network. Each
	 * entry is a 2-tuple having the string id of the case frame as the key and
	 * the corresponding case frame object as the value.
	 */
	private static Hashtable<String, CaseFrame> caseFrames = new Hashtable<String, CaseFrame>();

	/**
	 * A hash table that stores all the relations defined in the network. Each
	 * entry is a 2-tuple having the name of the relation as the key and the
	 * corresponding relation object as the value.
	 */
	private static Hashtable<String, Relation> relations = new Hashtable<String, Relation>();

	/**
	 * A counter used for generating the integer suffix that should be appended
	 * to the identifier of the next closed node that will be defined in the
	 * network.
	 */
	private static int molCounter = 0;

	/**
	 * A counter used for generating the integer suffix that should be appended
	 * to the identifier of the next pattern node that will be defined in the
	 * network.
	 */
	private static int patternCounter = 0;

	/**
	 * A counter used for generating the integer suffix that should be appended
	 * to the identifier of the next variable node that will be defined in the
	 * network.
	 */
	private static int varCounter = 0;

	/**
	 * a linked list of integers that contains the integer suffix of the
	 * user-defined base node identifiers that have the same form as the closed
	 * nodes' identifiers.
	 */
	private static LinkedList<Integer> userDefinedMolSuffix = new LinkedList<Integer>();

	/**
	 * a linked list of integers that contains the integer suffix of the
	 * user-defined base node identifiers that have the same form as the pattern
	 * nodes' identifiers.
	 */
	private static LinkedList<Integer> userDefinedPatSuffix = new LinkedList<Integer>();

	/**
	 * a linked list of integers that contains the integer suffix of the
	 * user-defined base node identifiers that have the same form as the
	 * variable nodes' identifiers.
	 */
	private static LinkedList<Integer> userDefinedVarSuffix = new LinkedList<Integer>();

	/**
	 *
	 * @return the hash table that stores the nodes defined in the network.
	 */
	public static Hashtable<String, Node> getNodes() {
		return nodes;
	}

	/**
	 * This is created to reduce the search space when searching for only proposition nodes
	 * @return the hash table that stores the proposition nodes defined in the network.
	 */
	public static Hashtable<String, PropositionNode> getPropositionNodes() {return propositionNodes;}


	/**
	 *
	 * @return the array list that stores the nodes defined in the network.
	 */
	public static ArrayList<Node> getNodesWithIDs() {
		return nodesIndex;
	}

	/**
	 *
	 * @return the hash table that stores the molecular nodes along with their
	 *         case frames.
	 */
	public static Hashtable<String, NodeSet> getMolecularNodes() {
		return molecularNodes;
	}

	/**
	 *
	 * @return the hash table that stores the case frames defined in the
	 *         network.
	 */
	public static Hashtable<String, CaseFrame> getCaseFrames() {
		return caseFrames;
	}

	/**
	 *
	 * @return the hash table that stores the relations defined in the network.
	 */
	public static Hashtable<String, Relation> getRelations() {
		return relations;
	}

	/**
	 *
	 * @return the linked list of the suffix of the user-defined closed nodes'
	 *         identifiers.
	 */
	public static LinkedList<Integer> getUserDefinedMolSuffix() {
		return userDefinedMolSuffix;
	}

	/**
	 *
	 * @return the linked list of the suffix of the user-defined pattern nodes'
	 *         identifiers.
	 */
	public static LinkedList<Integer> getUserDefinedPatSuffix() {
		return userDefinedPatSuffix;
	}

	/**
	 *
	 * @return the linked list of the suffix of the user-defined variable nodes'
	 *         identifiers.
	 */
	public static LinkedList<Integer> getUserDefinedVarSuffix() {
		return userDefinedVarSuffix;
	}

	/**
	 *
	 * @param name
	 *            the name of the relation that will be retrieved.
	 *
	 * @return the relation with the specified name if it exists.
	 * @throws RelationDoesntExistException 
	 *
	 * @throws CustomException
	 *             if the requested relation does not exist.
	 */
	public static Relation getRelation(String name) throws RelationDoesntExistException  {
		if (relations.containsKey(name)) {
			return relations.get(name);
		} else {
			throw new RelationDoesntExistException(
					"There is no relation with the following name: " + name);
		}
	}

	/**
	 *
	 * @param id
	 *            the string id of the case frame that will be retrieved.
	 *
	 * @return the case frame with the specified id if it exists.
	 *
	 * @throws CustomException
	 *             if the requested frame does not exist.
	 * @throws CaseFrameWithSetOfRelationsNotFoundException 
	 */
	public static CaseFrame getCaseFrame(String id) throws CaseFrameWithSetOfRelationsNotFoundException {
		if (caseFrames.containsKey(id)) {
			return caseFrames.get(id);
		} else {
			throw new CaseFrameWithSetOfRelationsNotFoundException(
					"There is no case frame defined with such set of relations");
		}
	}

	/**
	 *
	 * @param identifier
	 *            the name of the node that will be retrieved.
	 *
	 * @return the node with the specified name if it exists.
	 *
	 * @throws CustomException
	 *             if the requested node does not exist.
	 * @throws NodeNotFoundInNetworkException 
	 */
	public static Node getNode(String identifier) throws NodeNotFoundInNetworkException {
		if (nodes.containsKey(identifier)) {
			return nodes.get(identifier);
		} else {
			throw new NodeNotFoundInNetworkException("There is no node named '" + identifier
					+ "' in the network");
		}
	}

	public static Node getNodeById(int id) throws NodeNotFoundInNetworkException {
		if (nodesIndex.get(id) != null) {
			return nodesIndex.get(id);
		} else {
			throw new NodeNotFoundInNetworkException("There is no node named '" + id
					+ "' in the network");
		}
	}

	/**
	 * This method is used to define a new relation in the network.
	 *
	 * @param name
	 *            the name of the new relation.
	 * @param type
	 *            the name of the semantic class that specify the semantic of
	 *            the nodes that this new relation can point to.
	 * @param adjust
	 *            the adjustability of the new relation.
	 * @param limit
	 *            the minimum number of nodes that this new relation can point
	 *            to within a down-cable.
	 *
	 * @return the newly created relation.
	 *
	 * @throws CustomException
	 *             if another relation with the same given name is already
	 *             defined in the network.
	 */
	public static Relation defineRelation(String name, String type,
			String adjust, int limit) {
		if (relations.containsKey(name)) {
			return relations.get(name);
			// throw new CustomException("The relation named " + name +
			// " is already defined in the network");
		} else {
			relations.put(name, new Relation(name, type, adjust, limit));
		}
		return relations.get(name);
	}

	public static Relation defineRelation(String name, String type) {
		if (relations.containsKey(name)) {
			return relations.get(name);
			// throw new CustomException("The relation named " + name +
			// " is already defined in the network");
		} else {
			relations.put(name, new Relation(name, type));
		}
		return relations.get(name);
	}
	
	/**
	 * This method is used to delete a relation from the network.
	 *
	 * @param name
	 *            the name of the relation that will be deleted.
	 * @throws CaseFrameCannotBeRemovedException 
	 *
	 * @throws CustomException
	 *             if the relation cannot be removed because one of the case
	 *             frames that contains it cannot be removed.
	 */
	public static void undefineRelation(String name) throws CaseFrameCannotBeRemovedException {
		Relation r = relations.get(name);

		// removing the case frames that have this relation before removing the
		// relation.
		for (Enumeration<CaseFrame> e = caseFrames.elements(); e
				.hasMoreElements();) {
			CaseFrame cf = e.nextElement();
			for (int i = 0; i < cf.getRelations().size(); i++) {
				if (cf.getRelations().get(i).equals(r)) {
					undefineCaseFrame(cf.getId());
				}
			}
		}

		// removing the relation
		relations.remove(name);
	}

	// Assume the LinkedList<RCFP> is formulated in UI
		/**
		 * This method is used to define a new case frame.
		 *
		 * @param semanticType
		 *            the default semantic type specified by the new case frame.
		 * @param relationSet
		 *            the list that contains the RCFP's of the relations included in
		 *            the new case frame.
		 *
		 * @return the newly created case frame.
		 *
		 * @throws CustomException
		 *             if another case frame with the same given relations (same id)
		 *             is already defined in the network.
		 * @throws CaseFrameAlreadyExistException 
		 */
		public static RelationsRestrictedCaseFrame defineCaseFrameWithConstraints(String semanticType,
				LinkedList<RCFP> relationSet) throws CaseFrameAlreadyExistException {
			RelationsRestrictedCaseFrame caseFrame = new RelationsRestrictedCaseFrame(semanticType, relationSet);
			if (caseFrames.containsKey(caseFrame.getId())) {
				throw new CaseFrameAlreadyExistException(
						"This case frame already exists in the network");
			} else {
				caseFrames.put(caseFrame.getId(), caseFrame);
				// this to avoid non perfect hashing
				if (!molecularNodes.containsKey(caseFrame.getId()))
					molecularNodes.put(caseFrame.getId(), new NodeSet());
			}
			return (RelationsRestrictedCaseFrame)caseFrames.get(caseFrame.getId());
		} 
		
		public static CaseFrame defineCaseFrame(String semanticType,
				LinkedList<Relation> relationSet) throws CaseFrameAlreadyExistException {
			CaseFrame caseFrame = new CaseFrame(semanticType, relationSet);
			if (caseFrames.containsKey(caseFrame.getId())) {
				throw new CaseFrameAlreadyExistException(
						"This case frame already exists in the network");

			} else {
				caseFrames.put(caseFrame.getId(), caseFrame);
				// this to avoid non perfect hashing
				if (!molecularNodes.containsKey(caseFrame.getId()))
					molecularNodes.put(caseFrame.getId(), new NodeSet());
			}
			return caseFrames.get(caseFrame.getId());
		}

	/**
	 * This method is used to remove a case frame from the network.
	 *
	 * @param id
	 *            the ID of the case frame that will be removed.
	 *
	 * @throws CustomException
	 *             if the specified case frame cannot be removed because there
	 *             are nodes implementing this case frame and they need to be
	 *             removed first.
	 * @throws CaseFrameCannotBeRemovedException 
	 */
	public static void undefineCaseFrame(String id) throws CaseFrameCannotBeRemovedException {
		// first check if there are nodes implementing this case frame .. they
		// must be removed first
		if (molecularNodes.get(id).isEmpty()) {
			caseFrames.remove(id);
			molecularNodes.remove(id);
		} else {
			throw new CaseFrameCannotBeRemovedException("Case frame can not be removed .. "
					+ "remove the nodes implementing this case frame first");
		}
	}

	/**
	 * This method is used to define a certain path for a specific relation.
	 *
	 * @param relation
	 *            the relation that its path will be defined.
	 * @param path
	 *            the path that will be defined for the given relation.
	 */
	public static void definePath(Relation relation, Path path) {
		relation.setPath(path);
	}

	/**
	 * This method is used to undefine or remove the path of a a certain
	 * relation
	 *
	 * @param relation
	 *            the relation that its path will be removed.
	 */
	public static void undefinePath(Relation relation) {
		relation.setPath(null);
	}

	/**
	 * This method is used to remove a node from the network and also removes
	 * all the nodes that are only dominated by it.
	 *
	 * @param node
	 *            the node that will be removed.
	 *
	 * @throws CustomException
	 *             if the node cannot be removed because it is not isolated.
	 * @throws NodeCannotBeRemovedException 
	 */
	public static void removeNode(Node node) throws NodeCannotBeRemovedException {
		// check if the node is not isolated
		if (!node.getUpCableSet().isEmpty()) {
			throw new NodeCannotBeRemovedException("Cannot remove the node named '"
					+ node.getIdentifier() + "' because it is not isolated");
		}

		// if the node is isolated:

		// removing the node from the hash table
		nodes.remove(node.getIdentifier());
		// nullify entry of the removed node in the array list
		nodesIndex.set(node.getId(), null);
		// removing child nodes that are dominated by the removed node and has
		// no other parents
		if (node.getTerm().getClass().getSuperclass().getSimpleName()
				.equals("Molecular")) {
			Molecular m = (Molecular) node.getTerm();
			molecularNodes.get(m.getDownCableSet().getCaseFrame().getId())
					.removeNode(node);
			DownCableSet dCableSet = m.getDownCableSet();
			// loop for down cables
			Enumeration<DownCable> dCables = dCableSet.getDownCables()
					.elements();
			while (dCables.hasMoreElements()) {
				DownCable dCable = dCables.nextElement();
				NodeSet ns = dCable.getNodeSet();
				// loop for the nodes in the node set
				for (int j = 0; j < ns.size(); j++) {
					Node n = ns.getNode(j);
					// loop for the upCables of the current node
					Enumeration<UpCable> upCables = n.getUpCableSet()
							.getUpCables().elements();
					while (upCables.hasMoreElements()) {
						UpCable upCable = upCables.nextElement();
						upCable.removeNode(node);
						if (upCable.getNodeSet().isEmpty())
							n.getUpCableSet().removeUpCable(upCable);
					}
					// removing child nodes
					if (n.getUpCableSet().isEmpty())
						removeNode(n);
				}
			}
		}
	}
	
	/**
	 * This method builds a variable node with the default semantic type for
	 * variable nodes which is 'infimum'.
	 *
	 * @return the newly created variable node.
	 */
	public static VariableNode buildVariableNode() {
		Variable v = new Variable(getNextVarName());
		VariableNode node = new VariableNode(v);
		nodes.put(node.getIdentifier(), node);
		nodesIndex.add(node.getId(), node);
		return node;
	}

	/* check when this method should be used in the network??
	and how the variable node that have a semantic type should be handled and
	treated in the network?
	In the current version, all variable nodes are assumed to have only the
	default semantic type 'infimum'.
	 *
	
	/**
	 * This method builds a variable node with the given semantic type.
	 *
	 * @param semantic
	 *            the specified semantic type that will override the default
	 *            semantic type for the variable node that will be created.
	 *
	 * @return the newly created variable node.
	 */
	public static VariableNode buildVariableNode(Semantic semantic) {
		Variable v = new Variable(getNextVarName());
		VariableNode node = new VariableNode(semantic, v);
		nodes.put(node.getIdentifier(), node);
		nodesIndex.add(node.getId(), node);
		return node;
	}

	/**
	 * This method builds a new base node with the given name and semantic type.
	 *
	 * @param identifier
	 *            the name of the new base node.
	 * @param semantic
	 *            the semantic class that represents the semantic type of the
	 *            new base node.
	 *
	 * @return the newly created base node.
	 * @throws NotAPropositionNodeException 
	 * @throws NodeNotFoundInNetworkException 
	 *
	 * @throws CustomException
	 *             if another node with the same given name already exists in
	 *             the network.
	 */
	public static Node buildBaseNode(String identifier, Semantic semantic) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		if (semantic.getSemanticType().equals("Act")) {
			System.out.print("ERROR: Acts cannot be base nodes!!!");
			return null;
		}
		if (nodes.containsKey(identifier)) {
			//Already Present
			return nodes.get(identifier);
			//throw new CustomException("There is already another node with the same name existing in the network");
			
		} else {
			Base b = new Base(identifier);
			if(semantic.getSemanticType().equals("PropositionNode")){
				PropositionNode propNode =  new PropositionNode(b);
				nodes.put(identifier, propNode);
				try {
					nodesIndex.add(propNode.getId(), propNode);
					propNode.setBasicSupport();
				} catch (IndexOutOfBoundsException e) {
					System.out.println("wohoo");
				}
			}else{
			Node node;
      /*if (semantic.getSemanticType().equals("Action")) {
				if (semantic.getSemanticType().equals("ControlAction")) {
					node = new ControlActionNode(semantic, b);
				} else {
					node = new ActionNode(semantic, b);
				}
			} else {				
				node = new Node(semantic, b);
			}*/
			node = new Node(semantic, b);
			nodes.put(identifier, node);
			nodesIndex.add(node.getId(), node);
			}
			if (isMolName(identifier) > -1)
				userDefinedMolSuffix.add(new Integer(isMolName(identifier)));
			if (isPatName(identifier) > -1)
				userDefinedPatSuffix.add(new Integer(isPatName(identifier)));
			if (isVarName(identifier) > -1)
				userDefinedVarSuffix.add(new Integer(isVarName(identifier)));
			return nodes.get(identifier);
		}
	}
	
	/**
	 * This method builds a new molecular node with the given down cable set
	 * specifications and case frame.
	 *
	 * @param array
	 *            a 2D array of Relation-Node pairs that represents the
	 *            specifications of the down cable set of the new molecular
	 *            node.
	 * @param caseFrame
	 *            the case frame that will be implemented by the new molecular
	 *            node.
	 *
	 * @return the newly created molecular node.
	 *
	 * @throws Exception
	 *             if the invoked methods to create a pattern node or closed
	 *             node throw an Exception.
	 */
	public static Node buildMolecularNode(ArrayList<Wire> wires,
			CaseFrame caseFrame) throws Exception {
		Object[][] array = turnWiresIntoArray(wires);  
	    Object[] result = downCableSetExists(array);
		//System.out.println("Downcable set exists > "+ downCableSetExists(array)); 
		
		if (((Boolean)result[0]==true)&&(result[1]==null))
			throw new CannotBuildNodeException(
					"Cannot build the node .. down cable set already exists");  

		if(((Boolean)result[0]==true)&&(result[1]!=null)){
			throw new DuplicateNodeException(
					"This node has an equivalent node in the Network : " + result[1]);
		}
				
		// check the validity of the relation-node pairs
		// System.out.println("done 1st");
		if (!validRelNodePairs(array))
			throw new CannotBuildNodeException(
					"Cannot build the node .. the relation node pairs are not valid");
		// System.out.println("done 2nd");
		Object[][] relNodeSet = turnIntoRelNodeSet(array); 
		// check that the down cable set is following the case frame
		// System.out.println("done 3rd");
		// create the Molecular Node
		if(caseFrame.getSemanticClass().equals("PropositionNode")){
			PropositionNode propNode;
			if (isToBePattern(array)) {
				//System.out.println("building patt");
				propNode = (PropositionNode) createPatNode(relNodeSet, caseFrame);
			}else {
				//System.out.println("building closed");
				propNode = (PropositionNode) createClosedNode(relNodeSet, caseFrame);
			}
			nodes.put(propNode.getIdentifier(), propNode);
			nodesIndex.add(propNode.getId(), propNode);
			Molecular molecular = (Molecular)propNode.getTerm();
			molecularNodes.get(molecular.getDownCableSet().getCaseFrame().getId()).addNode(propNode);
			return propNode;
		}else{
		Node mNode;
		if (isToBePattern(array)) {
			//System.out.println("building patt");
			mNode = createPatNode(relNodeSet, caseFrame);
		}else {
			//System.out.println("building closed");
			mNode = createClosedNode(relNodeSet, caseFrame);
		}
		nodes.put(mNode.getIdentifier(), mNode);
		nodesIndex.add(mNode.getId(), mNode);
		Molecular molecular = (Molecular)mNode.getTerm();
		molecularNodes.get(molecular.getDownCableSet().getCaseFrame().getId()).addNode(mNode);
		return mNode;
		}
	} 
	
	public static Node buildMolecularNode(ArrayList<Wire> wires,
			RelationsRestrictedCaseFrame caseFrame) throws Exception, CustomException {
		Object[][] array = turnWiresIntoArray(wires);  
		Object[] result = downCableSetExists(array);
		//System.out.println("Downcable set exists > "+ downCableSetExists(array)); 
		
		if (((Boolean)result[0]==true)&&(result[1]==null))
			throw new CannotBuildNodeException(
					"Cannot build the node .. down cable set already exists"); 
		if(((Boolean)result[0]==true)&&(result[1]!=null)){
			throw new DuplicateNodeException(
					"This node has an equivalent node in the Network : " + result[1]);
		}
		// check the validity of the relation-node pairs
		// System.out.println("done 1st");
		if (!validRelNodePairs(array))
			throw new CannotBuildNodeException(
					"Cannot build the node .. the relation node pairs are not valid");
		// System.out.println("done 2nd");
		Object[][] relNodeSet = turnIntoRelNodeSet(array); 
		// check that the down cable set is following the case frame
		if (!followingCaseFrame(relNodeSet, caseFrame))
			throw new CaseFrameMissMatchException(
					"Not following the case frame .. wrong node set size or wrong set of relations");
		// System.out.println("done 3rd");
		// create the Molecular Node
		if(caseFrame.getSemanticClass().equals("PropositionNode")){
			PropositionNode propNode;
			if (isToBePattern(array)) {
				System.out.println("building patt");
				propNode = (PropositionNode) createPatNode(relNodeSet, caseFrame);
			}else {
				System.out.println("building closed");
				propNode = (PropositionNode) createClosedNode(relNodeSet, caseFrame);
			}
			nodes.put(propNode.getIdentifier(), propNode);
			nodesIndex.add(propNode.getId(), propNode);
			Molecular molecular = (Molecular)propNode.getTerm();
			molecularNodes.get(molecular.getDownCableSet().getCaseFrame().getId()).addNode(propNode);
			return propNode;
		}else{
		Node mNode;
		if (isToBePattern(array)) {
			System.out.println("building patt");
			mNode = createPatNode(relNodeSet, caseFrame);
		}else {
			System.out.println("building closed");
			mNode = createClosedNode(relNodeSet, caseFrame);
		}
		nodes.put(mNode.getIdentifier(), mNode);
		nodesIndex.add(mNode.getId(), mNode);
		Molecular molecular = (Molecular)mNode.getTerm();
		molecularNodes.get(molecular.getDownCableSet().getCaseFrame().getId()).addNode(mNode);
		return mNode;
		}
	}


	/**
	 * checks whether the given down cable set already exists in the network or
	 * not.
	 *
	 * @param array
	 *            a 2D array of Relation-Node pairs representing a down cable
	 *            set specifications.
	 *
	 * @return true if the down cable set exists, and false otherwise
	 */
	private static Object[] downCableSetExists(Object[][] array) {
		int size = 0;  
		
		Object [] result = new Object[2]; 
		boolean newBoundVarsExist = false; 
		boolean networkBoundVarsExist = false; 
		boolean[] newFlags = new boolean[array.length];
	
		for (int i = 0; i < array.length; i++) {
			if (!array[i][1].getClass().getSimpleName().equals("NodeSet")){
				size++; 
			}
			Relation r = (Relation) array[i][0];
			
 			if (r.isQuantifier()){ 
				newBoundVarsExist = true;
				size--; 
				
			}
			if ((newBoundVarsExist)&&(!r.isQuantifier())){ 
				newFlags[i]=true;
			}
		}	
	  
		 for(int k = 0; k < newFlags.length; k++){
			if(newFlags[k]==true)
				size--;
		}
	
		
		Object[][] temp = new Object[size][2];
		int counter = 0; 
		
		for (int i = 0; i < array.length; i++) {
			if (array[i][1].getClass().getSimpleName().equals("NodeSet"))
				continue;  
			Relation r = (Relation) array[i][0];
			NodeSet ns1 = new NodeSet(); 
			if((r.isQuantifier())){ 
				continue;
			
			}  
		     
			
			temp[counter][0] = new FUnitPath((Relation) array[i][0]);
			ns1.addNode((Node) array[i][1]); 
			temp[counter][1] = ns1;
			counter++; 
			
			
		}

		LinkedList<Object[]> ns = find(temp, Controller.createContext());
	
		
		for (int j = 0; j < ns.size(); j++) {
			Object[] x = ns.get(j);
			Node n = (Node) x[0];
			Molecular molecular = (Molecular) n.getTerm();
			for (int i = 0; i < array.length; i++) {
				if (array[i][1].getClass().getSimpleName().equals("NodeSet")) {
					if (molecular.getDownCableSet().contains(
							((Relation) array[i][0]).getName())
							&& molecular.getDownCableSet()
									.getDownCable(
											((Relation) array[i][0]).getName())
									.getNodeSet().isEmpty())
						continue; 
					else {
						ns.remove(j);
						j--;
						break;
					}
				}
			}
		}

		
		for (int i = 0; i < ns.size(); i++) {
			Object[] x = ns.get(i);
			Node n = (Node) x[0];
			Molecular molecular = (Molecular) n.getTerm();
			int c = 0;
			Enumeration<DownCable> dCables = molecular.getDownCableSet()
					.getDownCables().elements();
			while (dCables.hasMoreElements()) {
				Cable cb = dCables.nextElement();
				if (cb.getNodeSet().isEmpty())
					c++;
				else
					c += cb.getNodeSet().size();
				
				if(cb.getRelation().isQuantifier()){  
					networkBoundVarsExist=true;   
				}
				if((!cb.getRelation().isQuantifier())&&(networkBoundVarsExist)){  
					 break;
				}

			}
			if (c != array.length){
				ns.remove(i);
				i--;
			} 
			
		} 
           
		if(ns.size() == 1){ 
			result[0]= (Boolean) true; 
			result[1]= null; 
			
		  if((newBoundVarsExist)&&(networkBoundVarsExist)){ 
			Object[] s = ns.get(0);  
			result[1]=(Node) s[0];
		  }
		 
		  
			if((newBoundVarsExist)&&(!networkBoundVarsExist)){  
		       result[0]= (Boolean) false;   
		       ns.remove();
		  } 
			
		  
		}
		else{
			result[0]= (Boolean) false;
		    result[1]= null;
		} 
		
		if((ns.size()==1)&&((Boolean)result[0]==false))
			System.out.println("Downcable set already exist"); 
		if(!(ns.size()==1))
			System.out.println("Molecular Node built successfully");
		
		return result; 
	
	} 
	/**
	 * This method checks that each pair in a 2D array of relation-node pairs is
	 * valid. The pair is valid if the relation can point to the node paired
	 * with it according to the semantic type specified in the relation. In the
	 * current implementation any relation can point to the variable node
	 * because all nodes have infimum as their semantic type.
	 *
	 * @param array
	 *            a 2D array of Relation-Node pairs that represents the
	 *            specifications of the down cable set of a new molecular node.
	 *
	 * @return true if each pair in the 2D array is valid, and false otherwise.
	 */
	public static boolean validRelNodePairs(Object[][] array) {
		for (int i = 0; i < array.length; i++) {
			if (!array[i][1].getClass().getSimpleName().equals("NodeSet")) {
				if (array[i][1].getClass().getSimpleName()
						.equals("VariableNode")) {
					continue;
				} else {
					if (!(((Relation) array[i][0]).getType().equals(
							((Node) array[i][1]).getSemantic().getSemanticType()) || ((Node) array[i][1])
							.getSemantic().getSemanticType()
							.contains(((Relation) array[i][0]).getType()))) {
						return false;

					}
				}
			}
		}
		return true;
	}

	private static Object[][] turnWiresIntoArray(ArrayList<Wire> wires)
	{
		Object[][] result = new Object[wires.size()][2]; 
	  
		for(int i = 0; i < result.length; i++){ 
			
			result[i][0] = wires.get(i).getWireRelation();
			result[i][1] = wires.get(i).getWireNode();  

	    }
		return result;
	}
	
	private static Object[][] turnIntoRelNodeSet(Object[][] array) {
		Object[][] temp = new Object[array.length][];
		for (int i = 0; i < array.length; i++) {
			temp[i] = Arrays.copyOf(array[i], array[i].length);
		}
		Object[][] temp2 = new Object[array.length][];
		for (int i = 0; i < array.length; i++) {
			temp2[i] = Arrays.copyOf(array[i], array[i].length);
		}
		int relcounter = 0;
		for (int i = 0; i < temp.length; i++) {
			if (temp[i][0] != null) {
				Relation r = (Relation) temp[i][0];
				relcounter++;
				if (i + 1 < temp.length) {
					for (int j = i + 1; j < temp.length; j++) {
						if (temp[j][0] != null) {
							if (((Relation) temp[j][0]).equals(r)) {
								temp[j][0] = null;
							}
						}
					}
				}
			}
		}
		int addcounter = 0;
		Object[][] result = new Object[relcounter][2];
		for (int i = 0; i < temp2.length; i++) {
			if (temp2[i][0] != null) {
				Relation r = (Relation) temp2[i][0];
				NodeSet ns = new NodeSet();
				if (!temp2[i][1].getClass().getSimpleName().equals("NodeSet")) {
					ns.addNode((Node) temp2[i][1]);
				}
				if (i + 1 < temp2.length) {
					for (int j = i + 1; j < temp2.length; j++) {
						if (temp2[j][0] != null) {
							if (((Relation) temp2[j][0]).equals(r)) {
								if (!temp2[j][1].getClass().getSimpleName()
										.equals("NodeSet")) {
									ns.addNode((Node) temp2[j][1]);
								}
								temp2[j][0] = null;
							}
						}
					}
				}
				result[addcounter][0] = r;
				result[addcounter][1] = ns;
				addcounter++;
			}
		}
		return result;
	}


	private static boolean followingCaseFrame(Object[][] array,
			RelationsRestrictedCaseFrame caseFrame) {
		Hashtable<String, RCFP> list = new Hashtable<String, RCFP>(
				caseFrame.getrelationsWithConstraints());
		for (int i = 0; i < array.length; i++) {
			Relation r = (Relation) array[i][0];
			if (list.containsKey(r.getName())) {
				if (((NodeSet) array[i][1]).size() >= caseFrame.getRelationWithConstraints(r)
						.getLimit()) {
					list.remove(r.getName());
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		if (!list.isEmpty())
			return false;
		return true;
	}

	/**
	 * This method examines the down cable set of a certain molecular node to
	 * check whether it dominate free variables or not. Pattern nodes dominate
	 * free variables while closed nodes do not dominate free variables.
	 *
	 * @param array
	 *            a 2D array of Relation-Node pairs that represents the
	 *            specifications of the down cable set of the new molecular
	 *            node.
	 *
	 * @return true if the node dominates free variable and thus should be
	 *         pattern node, and false otherwise.
	 */
	private static boolean isToBePattern(Object[][] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i][1].getClass().getSimpleName().equals("NodeSet"))
				continue;
			Relation r = (Relation) array[i][0];
			Node node = (Node) array[i][1];
			if (node.getTerm().getClass().getSimpleName().equals("Variable")
					&& !r.isQuantifier())
				return true;
			if (node.getTerm().getClass().getSimpleName()
					.equals("Open")) {
				Open open = (Open) node.getTerm();
				VariableSet varNodes = open.getFreeVariables();
				for (int j = 0; j < varNodes.size(); j++) {
					Variable v = varNodes.getVariable(j);
					boolean flag = false;
					for (int k = 0; k < array.length; k++) {
						if (array[k][1].getClass().getSimpleName()
								.equals("NodeSet"))
							continue;
						Node n = (Node) array[k][1];
						if (n.getTerm().equals(v))
							flag = true;
					}
					if (!flag)
						return true;
				}

			}
		}
		return false;
	}


	/**
	 * This method builds a new pattern node or proposition node with the given
	 * down cable set specifications and case frame.
	 *
	 * @param relNodeSet
	 *            a 2D array of relation-nodeSet pairs that represents the down
	 *            cable set of the new pattern or proposition node.
	 * @param caseFrame
	 *            the case frame implemented by the new pattern or proposition
	 *            node.
	 *
	 * @return the newly created pattern node or proposition node.
	 *
	 * @throws Exception
	 *             if the semantic class specified by the case frame was not
	 *             successfully created and thus the node was not built.
	 */
	@SuppressWarnings("rawtypes")
	private static Node createPatNode(Object[][] relNodeSet,
			CaseFrame caseFrame) throws Exception {
		LinkedList<DownCable> dCables = new LinkedList<DownCable>();
		for (int i = 0; i < relNodeSet.length; i++) {
			dCables.add(new DownCable((Relation) relNodeSet[i][0],
					(NodeSet) relNodeSet[i][1]));
		}
		DownCableSet dCableSet = new DownCableSet(dCables, caseFrame);
		String patName = getNextPatName();
		Open open = new Open(patName, dCableSet);
		String temp = caseFrame.getSemanticClass();
		Semantic semantic = new Semantic(temp);
		// builds a proposition node if the semantic class is proposition, and
		// pattern node otherwise
		if (semantic.getSemanticType().equals("PropositionNode")) {
			PropositionNode propNode;
			if (caseFrame == RelationsRestrictedCaseFrame.andRule)
				propNode = null;
			//TODO
//				propNode = new AndNode(open);
			else if (caseFrame == RelationsRestrictedCaseFrame.orRule)
				propNode = null;
			//TODO
//				propNode = new OrNode(open);
			else if (caseFrame == RelationsRestrictedCaseFrame.andOrRule)
				propNode = null;
			//TODO
//				propNode = new AndOrNode(open);
			else if (caseFrame == RelationsRestrictedCaseFrame.threshRule)
				propNode = null;
			//TODO
//				propNode = new ThreshNode(open);
			else if (caseFrame == RelationsRestrictedCaseFrame.numericalRule)
				propNode = null;
			//TODO
//				propNode = new NumericalNode(open);	
			else if (caseFrame == RelationsRestrictedCaseFrame.doIf)
				propNode = null;
			//TODO
//				propNode = new DoIfNode(open);
			else if (caseFrame == RelationsRestrictedCaseFrame.whenDo)
				propNode = null;
			//TODO
//				propNode = new WhenDoNode(open);
			else
				propNode = new PropositionNode(open);
			return propNode;
		} else if (semantic.getSemanticType().equals("Act")) {
			return new ActNode();
		} else {
			Node pNode = new Node(semantic, open);
			return pNode;
		}

	}
	
	private static Node createPatNode(Object[][] relNodeSet,
			RelationsRestrictedCaseFrame caseFrame) throws Exception {
		LinkedList<DownCable> dCables = new LinkedList<DownCable>();
		for (int i = 0; i < relNodeSet.length; i++) {
			dCables.add(new DownCable((Relation) relNodeSet[i][0],
					(NodeSet) relNodeSet[i][1]));
		}
		DownCableSet dCableSet = new DownCableSet(dCables, caseFrame);
		String patName = getNextPatName();
		Open open = new Open(patName, dCableSet);
		String temp = getCFSignature(turnIntoHashtable(relNodeSet),
				caseFrame);
		Semantic semantic = new Semantic(temp);
		// builds a proposition node if the semantic class is proposition, and
		// pattern node otherwise
		if (semantic.getSemanticType().equals("Proposition")) {
			PropositionNode propNode;
			if (caseFrame == RelationsRestrictedCaseFrame.andRule)
				propNode = null;
			//TODO
//				propNode = new AndNode(open);
			else if (caseFrame == RelationsRestrictedCaseFrame.orRule)
				propNode = null;
			//TODO
//				propNode = new OrNode(open);
			else if (caseFrame == RelationsRestrictedCaseFrame.andOrRule)
				propNode = null;
			//TODO
//				propNode = new AndOrNode(open);
			else if (caseFrame == RelationsRestrictedCaseFrame.threshRule)
				propNode = null;
			//TODO
//				propNode = new ThreshNode(open);
			else if (caseFrame == RelationsRestrictedCaseFrame.numericalRule)
				propNode = null;
			//TODO
//				propNode = new NumericalNode(open);	
			else if (caseFrame == RelationsRestrictedCaseFrame.doIf)
				propNode = null;
			//TODO
//				propNode = new DoIfNode(open);
			else if (caseFrame == RelationsRestrictedCaseFrame.whenDo)
				propNode = null;
			//TODO
//				propNode = new WhenDoNode(open);
			else
				propNode = new PropositionNode(open);
			return propNode;
		} else if (semantic.getSemanticType().equals("Act")) {
			return new ActNode();
		} else {
			Node pNode = new Node(semantic, open);
			return pNode;
		}

	}

	/**
	 * This method builds a new closed node or proposition with the given down
	 * cable set specifications and case frame.
	 *
	 *
	 * @param relNodeSet
	 *            a 2D array of relation-nodeSet pairs that represents the down
	 *            cable set of the new closed or proposition node.
	 * @param caseFrame
	 *            the case frame implemented by the new closed or proposition
	 *            node.
	 *
	 * @return the newly created closed or proposition node.
	 *
	 * @throws Exception
	 *             if the semantic class specified by the case frame was not
	 *             successfully created and thus the node was not built.
	 */
	@SuppressWarnings("rawtypes")
	private static Node createClosedNode(Object[][] relNodeSet,
			CaseFrame caseFrame) throws Exception {
		LinkedList<DownCable> dCables = new LinkedList<DownCable>();
		for (int i = 0; i < relNodeSet.length; i++) {
			dCables.add(new DownCable((Relation) relNodeSet[i][0],
					(NodeSet) relNodeSet[i][1]));
		}
		DownCableSet dCableSet = new DownCableSet(dCables, caseFrame);
		String closedName = getNexMolName();
		Closed c = new Closed(closedName, dCableSet);
		String temp = caseFrame.getSemanticClass();
		Semantic semantic = new Semantic(temp);
		// builds a proposition node if the semantic class is proposition, and
		// closed node otherwise
		if (semantic.getSemanticType().equals("PropositionNode")) {
			PropositionNode propNode;
			if (caseFrame == RelationsRestrictedCaseFrame.andRule) {
				propNode = null;
				//TODO
//				propNode = new AndNode(c);
				}
			else if (caseFrame == RelationsRestrictedCaseFrame.orRule) {
				propNode = null;
				//TODO
//				propNode = new OrNode(c);
			}
			else if (caseFrame == RelationsRestrictedCaseFrame.andOrRule) {
				propNode = null;
				//TODO
//				propNode = new AndOrNode(c);
				}
			else if (caseFrame == RelationsRestrictedCaseFrame.threshRule) {
				propNode = null;
				//TODO
//				propNode = new ThreshNode(c);
				}
			else if (caseFrame == RelationsRestrictedCaseFrame.numericalRule) {
				propNode = null;
				//TODO
//				propNode = new NumericalNode(c);
				}
			else
				propNode = new PropositionNode(c);
			return propNode;
		} else if (semantic.getSemanticType().equals("Act")) {
			return new ActNode();
		} else {
			Node cNode = new Node(semantic, c);
			return cNode;
		}
	} 
	
	@SuppressWarnings("rawtypes")
	private static Node createClosedNode(Object[][] relNodeSet,
			RelationsRestrictedCaseFrame caseFrame) throws Exception {
		LinkedList<DownCable> dCables = new LinkedList<DownCable>();
		for (int i = 0; i < relNodeSet.length; i++) {
			dCables.add(new DownCable((Relation) relNodeSet[i][0],
					(NodeSet) relNodeSet[i][1]));
		}
		DownCableSet dCableSet = new DownCableSet(dCables, caseFrame);
		String closedName = getNexMolName();
		Closed c = new Closed(closedName, dCableSet);
		String temp = getCFSignature(turnIntoHashtable(relNodeSet),
				caseFrame);
		Semantic semantic = new Semantic(temp);
		// builds a proposition node if the semantic class is proposition, and
		// closed node otherwise
		if (semantic.getSemanticType().equals("Proposition")) {
			PropositionNode propNode;
			if (caseFrame == RelationsRestrictedCaseFrame.andRule) {
				propNode = null;
				//TODO
//				propNode = new AndNode(c);
				}
			else if (caseFrame == RelationsRestrictedCaseFrame.orRule) {
				propNode = null;
				//TODO
//				propNode = new OrNode(c);
			}
			else if (caseFrame == RelationsRestrictedCaseFrame.andOrRule) {
				propNode = null;
				//TODO
//				propNode = new AndOrNode(c);
				}
			else if (caseFrame == RelationsRestrictedCaseFrame.threshRule) {
				propNode = null;
				//TODO
//				propNode = new ThreshNode(c);
				}
			else if (caseFrame == RelationsRestrictedCaseFrame.numericalRule) {
				propNode = null;
				//TODO
//				propNode = new NumericalNode(c);
				}
			else
				propNode = new PropositionNode(c);
			return propNode;
		} else if (semantic.getSemanticType().equals("Act")) {
			return new ActNode();
		} else {
			Node cNode = new Node(semantic, c);
			return cNode;
		}

	}


	// not tested
	/**
	 * This method builds a hash table with each entry having the relation name
	 * as the key and and the node set that contains the nodes pointed to by the
	 * corresponding relation as the value.
	 *
	 * @param relNodeSet
	 *            a given 2D array of relation-nodeSet pairs that will be used
	 *            to create the hash table
	 *
	 * @return the newly created hash table.
	 */
	public static Hashtable<String, NodeSet> turnIntoHashtable(
			Object[][] relNodeSet) {
		Hashtable<String, NodeSet> result = new Hashtable<String, NodeSet>();
		for (int i = 0; i < relNodeSet.length; i++) {
			result.put(((Relation) relNodeSet[i][0]).getName(),
					(NodeSet) relNodeSet[i][1]);
		}
		return result;
	}

	/**
	 * This method gets the case frame signature specified by the case frame
	 * based on the down cable set of a certain node.
	 *
	 * @param relNodeSet
	 *            a hash table with entry having the relation name as the key
	 *            and the node set of nodes pointed to by the corresponding
	 *            relation as the value.
	 * @param caseframe
	 *            a given case frame.
	 *
	 * @return the (case frame signature) semantic type specified by the given
	 *         case frame based on the given down cable set specifications.
	 */
	@SuppressWarnings("unchecked")
	public static String getCFSignature(Hashtable<String, NodeSet> relNodeSet,
			RelationsRestrictedCaseFrame caseframe) {
		LinkedList<String> signatureIds = caseframe.getSignatureIDs();
		Hashtable<String, CFSignature> signatures = caseframe.getSignatures();
		for (int i = 0; i < signatureIds.size(); i++) {
			String currentId = signatureIds.get(i);
			if (signatures.containsKey(currentId)) {
				LinkedList<SubDomainConstraint> rules = (LinkedList<SubDomainConstraint>) signatures
						.get(currentId).getSubDomainConstraints().clone();
				for (int j = 0; j < rules.size(); j++) {
					SubDomainConstraint c = rules.get(j);
					LinkedList<CableTypeConstraint> checks = (LinkedList<CableTypeConstraint>) c
							.getNodeChecks().clone();
					NodeSet ns = relNodeSet.get(c.getRelation());
					for (int k = 0; k < checks.size(); k++) {
						CableTypeConstraint check = checks.get(k);
						int counter = 0;
						for (int l = 0; l < ns.size(); l++) {
							if (ns.getNode(l).getSemanticType()
									.equals(check.getSemanticType())
									|| ns.getNode(l).getSemanticSuperClass()
											.equals(check.getSemanticType())) {
								counter++;
							}
						}
						if (check.getLowerLimit() == null
								&& check.getUpperLimit() == null) {
							if (counter == ns.size()) {
								checks.remove(k);
								k--;
							}
						} else {
							if (check.getUpperLimit() == null) {
								if (counter >= check.getLowerLimit()) {
									checks.remove(k);
									k--;
								}
							} else {
								if (counter >= check.getLowerLimit()
										&& counter <= check.getUpperLimit()) {
									checks.remove(k);
									k--;
								}
							}
						}
					}
					if (checks.isEmpty()) {
						System.out.println("empty checks");
						rules.remove(j);
						j--;
					} else {
						break;
					}
				}
				if (rules.isEmpty()) {
					System.out.println("Satisfied");
					return signatures.get(currentId).getResultingType();
				}
			}
		}
		System.out.println("Not Satisfied");
		return caseframe.getSemanticClass();
	}

	/**
	 * @param array
	 *            a given 2D array that contains pairs of paths and node sets.
	 * @param context
	 *            a given context.
	 *
	 * @return the node set of nodes that we can start following those paths in
	 *         the array from, in order to reach at least one node at each node
	 *         set in all entries of the array.
	 */
	public static LinkedList<Object[]> find(Object[][] array, Context context) {
		return findIntersection(array, context, 0);
	}

	/**
	 * @param array
	 *            a given 2D array that contains pairs of paths and node sets.
	 * @param context
	 *            a given context.
	 * @param index
	 *            the index of the 2D array at which we should start traversing
	 *            it.
	 *
	 * @return the node set of nodes that we can start following those paths in
	 *         the array from, in order to reach at least one node of node sets
	 *         at each path-nodeSet pair.
	 */
	private static LinkedList<Object[]> findIntersection(Object[][] array,
			Context context, int index) {
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		// System.out.println("index " + index + " arra leng " + array.length);
		if (index == array.length) {
			return result;
		}
		Path path = (Path) array[index][0];
		NodeSet nodeSet = (NodeSet) array[index][1];

		if (index < array.length - 1) {
			LinkedList<Object[]> list1 = findUnion(path, nodeSet, context);
			LinkedList<Object[]> list2 = findIntersection(array, context,
					++index);
			for (int i = 0; i < list1.size(); i++) {
				Object[] ob1 = list1.get(i);
				Node n1 = (Node) ob1[0];
				PathTrace pt1 = (PathTrace) ob1[1];
				for (int j = 0; j < list2.size(); j++) {
					Object[] ob2 = list2.get(j);
					Node n2 = (Node) ob2[0];
					PathTrace pt2 = (PathTrace) ob2[1];
					if (n1.equals(n2)) {
						PathTrace pt = pt1.clone();
						pt.and(pt2.getPath());
						pt.addAllSupports(pt2.getSupports());
						Object[] o = { n1, pt };
						result.add(o);
					}
				}
			}
		} else {
			result.addAll(findUnion(path, nodeSet, context));
		}
		// System.out.println("yeeeah");
		return result;
	}

	/**
	 * @param path
	 *            the path that can be followed to get to one of the nodes
	 *            specified.
	 * @param nodeSet
	 *            the nodes that can be reached by following the path.
	 * @param context
	 *            a given context.
	 * @return a node set of nodes that we can start following the path from in
	 *         order to get to one of the nodes in the specified node set.
	 */
	private static LinkedList<Object[]> findUnion(Path path, NodeSet nodeSet,
			Context context) {
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		for (int i = 0; i < nodeSet.size(); i++) {
			LinkedList<Object[]> temp = path.followConverse(nodeSet.getNode(i),
					new PathTrace(), context);
			result.addAll(temp);
		}

		return result;
	}
/*
	/**
	 * This method builds an instance of the semantic class with the given name.
	 *
	 * @param name
	 *            the name of the semantic class.
	 *
	 * @return the instance of the semantic class that was newly created.
	 *
	 * @throws Exception
	 *             if the semantic class cannot be successfully built.
	 *
	public Entity buildSemanticClass(String name) throws Exception {
		Class<?> sem = Class.forName("sneps.network.classes.semantic." + name);
		Entity e = (Entity) sem.newInstance();
		return e;
	}
*/
	/**
	 * This method builds a new case frame signature with the given parameters.
	 *
	 * @param result
	 *            the name of the semantic class specified by the new case frame
	 *            signature.
	 * @param rules
	 *            the list of sub-domain constraints included in the new case
	 *            frame signature.
	 * @param caseframeId
	 *            the case frame if that this CFSignature will be included in
	 *
	 * @return the newly created case frame signature.
	 */
	public CFSignature createCFSignature(String result,
			LinkedList<SubDomainConstraint> rules, String caseframeId) {
		CFSignature r = new CFSignature(result, rules, caseframeId);
		return r;
	}

	/**
	 * This method adds a given case frame signature to a given case frame.
	 *
	 * @param rule
	 *            the given case frame signature that will be added to the
	 *            specified case frame.
	 *
	 * @param priority
	 *            the priority of the given case frame signature.
	 *
	 * @param caseframe
	 *            the given case frame.
	 *
	 * @return true if the case frame signature was successfully added to the
	 *         case frame and false otherwise.
	 */
	public boolean addSignatureToCaseFrame(CFSignature rule, Integer priority,
			RelationsRestrictedCaseFrame caseFrame) {
		return caseFrame.addSignature(rule, priority);
	}

	/**
	 * This method removes the given case frame signature from the given case
	 * frame
	 *
	 * @param signatureID
	 *            the id of the signature to be removed.
	 * @param caseFrame
	 *            the given case frame
	 *
	 * @return true if the case frame signature was successfully removed from
	 *         the given case frame and false otherwise.
	 */
	public boolean removeSignatureFromCaseFrame(String signatureID,
			RelationsRestrictedCaseFrame caseFrame) {
		return caseFrame.removeSignature(signatureID);
	}

	/**
	 * This method removes the given case frame signature from the given case
	 * frame
	 *
	 * @param signatureID
	 *            the signature to be removed.
	 * @param caseFrame
	 *            the given case frame
	 *
	 * @return true if the case frame signature was successfully removed from
	 *         the given case frame and false otherwise.
	 */
	public boolean removeSignatureFromCaseFrame(CFSignature sig,
			RelationsRestrictedCaseFrame caseFrame) {
		return caseFrame.removeSignature(sig);
	}

	/**
	 * This method builds a new RCFP with the given parameters
	 *
	 * @param r
	 *            the relation included in the new RCFP.
	 * @param adjust
	 *            the adjust of the given relation in the new RCFP.
	 * @param limit
	 *            the limit of the given relation in the new RCFP.
	 *
	 * @return the newly created RCFP.
	 */
	public static RCFP defineRelationPropertiesForCF(Relation r, String adjust,
			int limit) {
		RCFP properties = new RCFP(r, adjust, limit);
		return properties;
	}

	// Methods that was implemented to by
	// used by the UI (if needed)

	public static NodeSet getNodesHavingCF(CaseFrame caseFrame) {
		NodeSet ns = new NodeSet();
		if (molecularNodes.containsKey(caseFrame.getId())) {
			NodeSet temp = molecularNodes.get(caseFrame.getId());
			if (temp.isEmpty())
				return ns;
			else {
				// to handle if the hashing is not perfect
				for (int i = 0; i < temp.size(); i++) {
					if (((Molecular) temp.getNode(i).getTerm()).getDownCableSet()
							.getCaseFrame().getId().equals(caseFrame.getId())) {
						ns.addNode(temp.getNode(i));
					}
				}
			}
		}
		return ns;
	}

	// helper methods that generate the names for the
	// different types of nodes that are to built in the network

	/**
	 * @return a String representing the generated closed node name in the form
	 *         of "Mi" and i is an integer suffix.
	 */
	private static String getNexMolName() {
		molCounter++;
		String molName = "M";
		for (int i = 0; i < userDefinedMolSuffix.size(); i++) {
			if (userDefinedMolSuffix.get(i).intValue() == molCounter) {
				molCounter++;
				i = -1;
			}
		}

		molName += "" + molCounter;

		return molName;
	}

	/**
	 * @return a String representing the generated pattern node name in the form
	 *         of "Pi" and i is an integer suffix.
	 */
	private static String getNextPatName() {
		patternCounter++;
		String patName = "P";
		for (int i = 0; i < userDefinedPatSuffix.size(); i++) {
			if (userDefinedPatSuffix.get(i).intValue() == patternCounter) {
				patternCounter++;
				i = -1;
			}
		}
		patName += "" + patternCounter;

		return patName;
	}

	/**
	 * @return a String representing the generated variable node name in the
	 *         form of "Vi" and i is an integer suffix.
	 */
	private static String getNextVarName() {
		varCounter++;
		String varName = "V";
		for (int i = 0; i < userDefinedVarSuffix.size(); i++) {
			if (userDefinedVarSuffix.get(i).intValue() == varCounter) {
				varCounter++;
				i = -1;
			}
		}
		varName += "" + varCounter;

		return varName;
	}

	//Methods that update the lists
	//of user-defined suffix
	
	/**
	 * This method checks if a user-defined name of a base node has the same
	 * form as the closed nodes' identifiers "Mi"
	 *
	 * @param identifier
	 *            a user-defined identifier.
	 *
	 * @return -1 if the identifier does not have the form of "Mi" where 'i' is
	 *         an integer suffix, and return the int value of the 'i' otherwise.
	 */
	private static int isMolName(String identifier) {
		if (identifier.length() == 1)
			return -1;
		if (identifier.charAt(0) != 'm' && identifier.charAt(0) != 'M')
			return -1;
		for (int i = 1; i < identifier.length(); i++) {
			if (!isInt(identifier.charAt(i)))
				return -1;
		}
		return Integer.parseInt(identifier.substring(1, identifier.length()));
	}

	/**
	 * This method checks if a user-defined name of a base node has the same
	 * form as the pattern nodes' identifiers "Pi"
	 *
	 * @param identifier
	 *            a user-defined identifier.
	 *
	 * @return -1 if the identifier does not have the form of "Pi" where 'i' is
	 *         an integer suffix, and return the int value of the 'i' otherwise.
	 */
	private static int isPatName(String identifier) {
		if (identifier.length() == 1)
			return -1;
		if (identifier.charAt(0) != 'p' && identifier.charAt(0) != 'P')
			return -1;
		for (int i = 1; i < identifier.length(); i++) {
			if (!isInt(identifier.charAt(i)))
				return -1;
		}
		return Integer.parseInt(identifier.substring(1, identifier.length()));
	}

	/**
	 * This method checks if a user-defined name of a base node has the same
	 * form as the variable nodes' identifiers "Vi"
	 *
	 * @param identifier
	 *            a user-defined identifier.
	 *
	 * @return -1 if the identifier does not have the form of "Vi" where 'i' is
	 *         an integer suffix, and return the int value of the 'i' otherwise.
	 */
	private static int isVarName(String identifier) {
		if (identifier.length() == 1)
			return -1;
		if (identifier.charAt(0) != 'v' && identifier.charAt(0) != 'V')
			return -1;
		for (int i = 1; i < identifier.length(); i++) {
			if (!isInt(identifier.charAt(i)))
				return -1;
		}
		return Integer.parseInt(identifier.substring(1, identifier.length()));
	}

	/**
	 *
	 * @param c
	 *            a character that will be checked whether it is an int or not.
	 *
	 * @return true if the character is an int, and false otherwise.
	 */
	private static boolean isInt(char c) {
		switch (c) {
		case '0':
			;
		case '1':
			;
		case '2':
			;
		case '3':
			;
		case '4':
			;
		case '5':
			;
		case '6':
			;
		case '7':
			;
		case '8':
			;
		case '9':
			return true;
		default:
			return false;
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// /////////////////////////////// The method that checks for possible case
		// frames' conflicts (and its helper method)
		// ///////////////////////////////////

		/**
		 * This method checks whether the newly created case frame can cause
		 * conflicts on the semantic level with any of the case frame existing in
		 * the network. Two case frames are said to be conflicting if they can at
		 * any point produce nodes that are semantically the same.
		 *
		 * V.Imp Notes: - This method is not used anywhere in the code yet. - In the
		 * method it is assumed that the given case frame is newly created and thus
		 * not added to the hash table of case frames yet thus if the same id as the
		 * given case frame was found in the hash table of case frames, the method
		 * will return null. (It's assumed that the method will be used while
		 * creating the case frame .. so if it will be used somewhere else after
		 * adding the case frame to the hash table of case frames .. the first check
		 * in the method that returns null should be removed and it should be known
		 * that the given case frame will be returned in the result (along with the
		 * other conflicting case frames) because it will be conflicting with
		 * itself.
		 *
		 *
		 * @param cf
		 *            the newly created case frame.
		 *
		 * @return a list of the case frame that are conflicting with the given case
		 *         frame, and null if the given case frame already exists in the
		 *         system. if no case frames are conflicting with the given case
		 *         frame the list will be empty.
		 */
//		public static LinkedList<RCFPCaseFrame> CheckCFConflicts(RCFPCaseFrame cf) {
//			if (caseFrames.containsKey(cf.getId())) {
//				return null;
//			}
//			// loop over all defined case frames
//			Enumeration<CaseFrame> caseframes = caseFrames.elements();
//			LinkedList<RCFPCaseFrame> result = new LinkedList<RCFPCaseFrame>();
//			// looping on the case frames with supersets or subsets of relations
//			while (caseframes.hasMoreElements()) {
//				RCFPCaseFrame cf1 = caseframes.nextElement();
//				// get intersecting relations
//				Hashtable<String, RCFP> intersection = getIntersectingRelations(
//						cf1.getRelations(), cf.getRelations());
//				// if no intersecting relations not conflicting so skip
//				if (intersection.size() == 0) {
//					continue;
//				}
//				// check new case frame
//				Enumeration<RCFP> relations = cf.getRelations().elements();
//				boolean satisfied = true;
//				while (relations.hasMoreElements()) {
//					RCFP r = relations.nextElement();
//					if (intersection.containsKey(r.getRelation().getName()))
//						continue;
//					if (r.getLimit() != 0) {
//						satisfied = false;
//						break;
//					}
//				}
//				if (satisfied) {
//					// check other case frame
//					Enumeration<RCFP> relations1 = cf1.getRelations().elements();
//					boolean satisfied1 = true;
//					while (relations1.hasMoreElements()) {
//						RCFP r = relations1.nextElement();
//						if (intersection.containsKey(r.getRelation().getName()))
//							continue;
//						if (r.getLimit() != 0) {
//							satisfied1 = false;
//							break;
//						}
//					}
//					if (satisfied1) {
//						result.add(cf1);
//					}
//				}
//			}
//			return result;
//		}

	/**
	 * This method gets the intersecting relations between two different case
	 * frames. It is invoked and used by the method that checks the case frame
	 * conflicts.
	 *
	 * @param list1
	 *            a given hash table of relations of a certain case frame
	 *
	 * @param list2
	 *            a given hash table of relations of a another case frame
	 *
	 * @return a hash table that contains the relations that were in both case
	 *         frames. Each entry has the relation name as the key and the RCFP
	 *         of the corresponding relation as the value.
	 */
	private static Hashtable<String, RCFP> getIntersectingRelations(
			Hashtable<String, RCFP> list1, Hashtable<String, RCFP> list2) {
		Enumeration<RCFP> relations = list1.elements();
		Hashtable<String, RCFP> result = new Hashtable<String, RCFP>();
		while (relations.hasMoreElements()) {
			RCFP r = relations.nextElement();
			if (list2.containsKey(r.getRelation().getName())) {
				result.put(r.getRelation().getName(), r);
			}
		}
		return result;
	}

	//The Compact Method

	/**
	 * This method compacts the nodesIndex array-list by removing the null
	 * entries. (when a node is removed from the network its entry in the
	 * nodesIndex array-list is nullified). The method then adjust the count of
	 * the nodes and the id of the nodes accordingly.
	 *
	 * V.Imp Note: - This method is not used anywhere yet.
	 */
	public static void compact() {
		int nodes = 0;
		int empty = 0;
		for (int i = 0; i < nodesIndex.size(); i++) {
			if (nodesIndex.get(i) == null) {
				empty++;
			} else {
				if (empty > 0) {
					Node n = nodesIndex.get(i);
					int oldID = n.getId();
					n.setId(oldID - empty);
					nodesIndex.set(n.getId(), n);
					nodesIndex.set(oldID, null);
					 System.out.println("old id: " + oldID + " new id: "
							+ (oldID - empty) + " empty: " + empty);
				}
				nodes++;
			}
		}
		for (int i = nodes; i < nodesIndex.size(); i++) {
			nodesIndex.remove(i);
			i--;
		}
		 System.out.println("");
		 System.out.println("previous count of nodes before deletion: "
				+ Node.getCount());
		Node.setCount(nodes);
		 System.out.println("current count of nodes before deletion: "
				+ Node.getCount());
	}

	//Other Methods

	/**
	 * @param array
	 *            the array that contains pairs of paths and node sets
	 * @return the node set of non-variable nodes that we can start following
	 *         those paths in the array from, in order to reach at least one
	 */
	public static LinkedList<Object[]> findConstant(Object[][] array,
			Context context) {
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		LinkedList<Object[]> temp = find(array, context);
		for (int i = 0; i < temp.size(); i++) {
			Object[] o = temp.get(i);
			Node n = (Node) o[0];
			if (n.getSyntacticType().equals("Base")
					|| n.getSyntacticType().equals("Closed"))
				result.add(o);
		}
		return result;
	}

	/**
	 * @param array
	 *            the array that contains pairs of paths and node sets
	 * @return the node set of base nodes that we can start following those
	 *         paths in the array from, in order to reach at least one node at
	 *         each node set in all entries of the array
	 */
	public static LinkedList<Object[]> findBase(Object[][] array,
			Context context) {
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		LinkedList<Object[]> temp = find(array, context);
		for (int i = 0; i < temp.size(); i++) {
			Object[] o = temp.get(i);
			Node n = (Node) o[0];
			if (n.getSyntacticType().equals("Base"))
				result.add(o);
		}
		return result;
	}

	/**
	 * @param array
	 *            the array that contains pairs of paths and node sets
	 * @return the node set of variable nodes that we can start following those
	 *         paths in the array from, in order to reach at least one node at
	 *         each node set in all entries of the array
	 */
	public static LinkedList<Object[]> findVariable(Object[][] array,
			Context context) {
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		LinkedList<Object[]> temp = find(array, context);
		for (int i = 0; i < temp.size(); i++) {
			Object[] o = temp.get(i);
			Node n = (Node) o[0];
			if (n.getSyntacticType().equals("Variable"))
				result.add(o);
		}
		return result;
	}

	/**
	 * @param array
	 *            the array that contains pairs of paths and node sets
	 * @return the node set of pattern nodes that we can start following those
	 *         paths in the array from, in order to reach at least one node at
	 *         each node set in all entries of the array
	 */
	public static LinkedList<Object[]> findPattern(Object[][] array,
			Context context) {
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		LinkedList<Object[]> temp = find(array, context);
		for (int i = 0; i < temp.size(); i++) {
			Object[] o = temp.get(i);
			Node n = (Node) o[0];
			if (n.getSyntacticType().equals("Open"))
				result.add(o);
		}
		return result;
	}

	
	private static VariableSet getAllVariables(Molecular node) {
		VariableSet result = new VariableSet();

		Enumeration<DownCable> dCables = node.getDownCableSet().getDownCables()
				.elements();
		while (dCables.hasMoreElements()) {
			Cable c = dCables.nextElement();
			NodeSet ns = c.getNodeSet();
			for (int j = 0; j < ns.size(); j++) {
				Node n = ns.getNode(j);
				if (n.getSyntacticType().equals("Variable")) {
					result.addVariable((Variable) n.getTerm());
				}
				if (n.getSyntacticSuperClass().equals("Molecular")) {
					result.addAll(getAllVariables((Molecular) n.getTerm()));
				}
			}
		}

		return result;
	}

	public static NodeSet match(Node x) {
		return new NodeSet();
	}
	
	public static void defineDefaults() throws CustomException {
		Relation.createDefaultRelations();
		RCFP.createDefaultProperties();
		//CaseFrame.createDefaultCaseFrames();
		//SNeBR.getContextSet().add(SNeBR.getCurrentContext());
		//ControlActionNode.initControlActions();
	}
	
	
	public static void save(String relationsData, String caseFramesData, String nodesData) throws IOException {
		ObjectOutputStream ros = new ObjectOutputStream(new FileOutputStream(new File(relationsData)));
		ros.writeObject(relations);
		ros.close();
		
		ObjectOutputStream cFos = new ObjectOutputStream(new FileOutputStream(new File(caseFramesData)));
		cFos.writeObject(caseFrames);
		cFos.close();
		
		
		ObjectOutputStream nodesOS = new ObjectOutputStream(new FileOutputStream(new File(nodesData)));
		nodesOS.writeObject(nodes);
		nodesOS.close();
		
	}
	
	public static void load(String relationsData, String caseFramesData, String nodesData) throws IOException, ClassNotFoundException {
		ObjectInputStream ris= new ObjectInputStream(new FileInputStream(new File(relationsData)));
		Hashtable<String, Relation> tempRelations = (Hashtable<String, Relation>) ris.readObject();
		Network.relations = tempRelations;
		ris.close();
		tempRelations = null;
		
		ObjectInputStream cFis= new ObjectInputStream(new FileInputStream(new File(caseFramesData)));
		Hashtable<String, CaseFrame> tempcF = (Hashtable<String, CaseFrame>) cFis.readObject();
		Network.caseFrames = tempcF;
		cFis.close();
		tempcF = null;
		
		
		ObjectInputStream nodesis= new ObjectInputStream(new FileInputStream(new File(nodesData)));
		Hashtable<String, Node> tempNodes = (Hashtable<String, Node>) nodesis.readObject();
		Network.nodes = tempNodes;
		nodesis.close();
		tempNodes = null;
		
				
	}
	
	/**
	 * This method is used to clear the network entirely.
	 */
	public static void clearNetwork() {
		nodes.clear();
		propositionNodes.clear();
		nodesIndex.clear();
		molecularNodes.clear();
		caseFrames.clear();
		relations.clear();
		molCounter = 0;
		patternCounter = 0;
		varCounter = 0;
		userDefinedMolSuffix.clear();
		userDefinedPatSuffix.clear();
		userDefinedVarSuffix.clear();
	}
	
}
