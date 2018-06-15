package sneps.snebr;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.Hashtable;
import java.util.Iterator;

import sneps.exceptions.CannotInsertJustificationSupportException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.IllegalIdentifierException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.PropositionSet;

public class Support implements Serializable{
	private int id;
	private Hashtable<String, PropositionSet> justificationSupport;
	private Hashtable<String, PropositionSet> assumptionBasedSupport;
	private ArrayList<ArrayList<ArrayList<Integer>>> mySupportsTree;
	private ArrayList<ArrayList<Integer>> intialTreeSet;
	private ArrayList<Integer> parentNodes;
	private boolean hasChildren;
	private boolean TreeComputed;
	private boolean isHyp;

	/**
     * Constructs a new Support for a propositionNode.
     * And intializes all supports attributes
     * Attributes needed to be intialized are the lists Justification, Assumption, SupportsTree, and parentSupports
     *
     * @param id   the id of the proposition node. to be used for setting the id attribute in support. Will be used later in setHyp method.
     * @throws NotAPropositionNodeException  if the id does not belong to a proposition node id
     * @throws NodeNotFoundInNetworkException
     */
	public Support(int id) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		this.id = id;
		assumptionBasedSupport = new Hashtable<String, PropositionSet>();
		justificationSupport = new Hashtable<String, PropositionSet>();
		mySupportsTree = new ArrayList<ArrayList<ArrayList<Integer>>>();
		intialTreeSet = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> intialTree = new ArrayList<Integer>();
		intialTree.add(id);
		intialTreeSet.add(intialTree);
		mySupportsTree.add(intialTreeSet);
		parentNodes = new ArrayList<Integer>();
		TreeComputed = true;
		hasChildren = false;
	}

	/**
     * toString method. retrieving the assumptions and justification supports in a string.
     */
	@Override
	public String toString() {
		return "Support [justificationSupport=" + justificationSupport.values() + ", assumptionBasedSupport="
				+ assumptionBasedSupport.values() + "]";
	}

	/**
     * Returns the justificationsupports of a propositionNode.
     *
     * @return a Hashtable containing the sets representing the direct supports of this node.
     */
	public Hashtable<String, PropositionSet> getJustificationSupport() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		
		return justificationSupport;
	}

	/**
	 * Makes sure that a proposition node is either a hyp or is supported.
	 * And reconstruct the justifications of a proposition node.
	 * When removing a node from the supports, This node could  reflect  into  deleting  
	 * another  sets  from  another  nodes  justificationSupport.
	 * Therefore,  this method checks that every node in the supports strucure has a valid justificationSupport.
	 * By valid i mean that each node must be either a hypothesis (user asserted) therefore no supports needed,
	 * or supported by one or more valid sets
     *
     * @return a boolean indicating whether the node is restrucured correctly or no.
     */
	public boolean reStructureJustifications() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		Iterator<PropositionSet> it = justificationSupport.values().iterator();
		while(it.hasNext()){
			PropositionSet anSupport = it.next();
			int[] anSupportNodes = PropositionSet.getPropsSafely(anSupport);
			for (int i = 0; i < anSupportNodes.length; i++) {
				PropositionNode aNode = (PropositionNode)Network.getNodeById(anSupportNodes[i]);
				if(aNode.HasChildren()){
					aNode.reStructureJustifications();
				}else{
					Hashtable<String, PropositionSet> assumptionBasedSupport = aNode.getAssumptionBasedSupport();
					PropositionSet intialSet = new PropositionSet(aNode.getId());
					if(!(assumptionBasedSupport.contains(intialSet)) && !isHyp()){
						justificationSupport.remove(anSupport.getHash());
						boolean Empty = justificationSupport.isEmpty();
						setHasChildren(!Empty);
						if(isHyp() && Empty){
							assumptionBasedSupport = new Hashtable<String, PropositionSet>();
							PropositionSet intialSet2 = new PropositionSet(id);
							assumptionBasedSupport.put(Integer.toString(id), intialSet2);
						}
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Tells whether this proposition node got supports or no
     *
     * @return a boolean.
     */
	public boolean HasChildren() {
		return hasChildren;
	}

	/**
	 * Set this node hasChildren attribute to true or false depending on the param.
     *
     * @param a boolean.
     */
	public void setHasChildren(boolean flag) {
		hasChildren = flag;
	}

	/**
	 * Tells if we previously computed this proposition node supports tree structure.
     *
     * @return a boolean.
     */
	private boolean isTreeComputed() {
		return TreeComputed;
	}

	/**
	 * Set this node TreeComputed attribute to true or false depending on the param.
     *
     * @param a boolean.
     */
	private void setTreeComputed(boolean flag) {
		TreeComputed = flag;
	}

	/**
	 * Accepts  a  PropositionSet which represent a direct support of this proposition node, and adds the
	 * propSet to the hash table of justificationSupport.  
	 * Moreover, this method computes the assumptionBased support. 
	 * When adding a new set to the justification basedsupport,  
	 * each node of this set has an assumptionBasedSupport set. 
	 * Simply,  this method computes the assumptionBasedSupport as the cross product of the propSetnodes
	 * assumption supports.For example if the propSet is equal to {"x", "y"},
	 * the node "x" has an assumption-BasedSupports{"a",  "b"}and{"c",  "d"}and the node "y"
	 * has an assumption-BasedSupport{"e", "f", "g"}, then the output cross product assumptionSupport will 
	 * be{"a", "b", "e", "f", "g"}and{"c", "d", "e", "f", "g"}.
	 * Moreover,  this method takes care of both direct cycles by throwing 
	 * an exception“CannotInsertJustificationSupportException”,  and  in-direct  cycles  by  neglecting the path
	 *  having cycles when computing assumptionBasedSupport. 
     *
     * @param a propSet representing the newly support of a node.
     */
	public void addJustificationBasedSupport(PropositionSet propSet)
			throws NodeNotFoundInPropSetException, NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException, CannotInsertJustificationSupportException {
		
		if (!HasChildren()) {
			assumptionBasedSupport = new Hashtable<String, PropositionSet>();
		}
		String hash = propSet.getHash();
		if (!justificationSupport.containsKey(hash)) {
			
			if((new PropositionSet(this.getId())).isSubSet(propSet))
				throw new CannotInsertJustificationSupportException("This PropositionSet contain a Cyclic Supports in the node " + propSet.toString() + this.getId());
			
			justificationSupport.put(hash, propSet);
			setHasChildren(true);
			setTreeComputed(false);
			int[] nodes = PropositionSet.getPropsSafely(propSet);
			for (int i = 0; i < nodes.length; i++) {
				PropositionNode node = (PropositionNode) Network.getNodeById(nodes[i]);
				node.addParentNode(id);
			}
			PropositionSet setSofar = new PropositionSet();
			int idx = 1;
			PropositionNode node = (PropositionNode) Network.getNodeById(nodes[0]);
			if(node.getId() == this.getId()){
				node = (PropositionNode) Network.getNodeById(nodes[1]);
				idx++;
			}
			Hashtable<String, PropositionSet> NodeAssumptions = node.getAssumptionBasedSupport();
			Iterator<PropositionSet> it = NodeAssumptions.values().iterator();

			while (it.hasNext()) {
				setSofar = it.next();
				if(!((new PropositionSet(this.getId())).isSubSet(setSofar))){
				computeAssumptionRec(nodes, setSofar, idx);
				}
			}
			
		}
	}

	/**
	 * This is a recursive method for computing the assumption support of a node.
	 * It is a private method since only the method "addJustificationBasedSupport" can use it
     */
	private void computeAssumptionRec(int[] nodes, PropositionSet setSofar, int idx)
			throws NodeNotFoundInNetworkException, NotAPropositionNodeException {
		if (idx == nodes.length) {
			assumptionBasedSupport.put(setSofar.getHash(), setSofar);
			return;
		}
		PropositionNode node = (PropositionNode) Network.getNodeById(nodes[idx]);
		if(node.getId() == this.getId()){
			return;
		}
		Hashtable<String, PropositionSet> NodeAssumptions = node.getAssumptionBasedSupport();
		Iterator<PropositionSet> it = NodeAssumptions.values().iterator();
		idx += 1;
		while (it.hasNext()) {
			PropositionSet thisSet = it.next();
			if(!((new PropositionSet(this.getId())).isSubSet(thisSet))){
			computeAssumptionRec(nodes, setSofar.union(thisSet), idx);
		}
		}
	}

	/**
	 * Returns  the  set  (mySupportsTree)  of  this  propositionnode only if treeComputed is true.
	 * Otherwise, it computes the supportsTree from the  assumptionBasedSupport  structure.   
	 * And  sets treeComputed to true.
     *
     * @return ArrayList<ArrayList<ArrayList<Integer>>> representing the supports hierarchy of this node.
     */
	
	public ArrayList<ArrayList<ArrayList<Integer>>> getMySupportsTree() throws NotAPropositionNodeException, NodeNotFoundInNetworkException{
		if(isTreeComputed())
			return mySupportsTree;
		
		mySupportsTree = new ArrayList<ArrayList<ArrayList<Integer>>>();
		intialTreeSet = new ArrayList<ArrayList<Integer>>();
		
		Iterator<PropositionSet> it = assumptionBasedSupport.values().iterator();
		while(it.hasNext()){
			int[] nodes = PropositionSet.getPropsSafely(it.next());
			
			for (int i = 0; i < nodes.length; i++) {
				PropositionNode node = (PropositionNode) Network.getNodeById(nodes[i]);
				
				ArrayList<Integer> aPath = new ArrayList<Integer>();
				aPath.add(nodes[i]);
				aPath.addAll(node.getParentSupports());
				
				intialTreeSet.add(aPath);
			}
			mySupportsTree.add(intialTreeSet);
			intialTreeSet = new ArrayList<ArrayList<Integer>>();
		}
		return mySupportsTree;
	}
	
	/**
	 * Returns the set (assumptionBasedSupport) of this proposition node.
	 * @return Hashtable<String, PropositionSet> representing the assumptions of this node.
	 */
	public Hashtable<String, PropositionSet> getAssumptionBasedSupport() {
		return assumptionBasedSupport;
	}

	/**
	 * Removes thepropNode from any set, either justificationBasedSupport or assumptionBasedSup-port,
	 * containing this node as one of it’s elements.  
	 * Also sets the attribute hasChildren  to  false,  if  the  justificationBasedSupport  becomes  empty  after 
	 * the  removal. 
	 * Moreover, this method calls the method reStructureJustifications(), discussed above.
	 * restrucureJustification only makes sure that all justifications are valid "are hyps or have valid supports".
	 * @param propNode
	 * @throws NotAPropositionNodeException
	 * @throws NodeNotFoundInNetworkException
	 */
	public void removeNodeFromSupports(PropositionNode propNode) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		PropositionSet removeSet = new PropositionSet(propNode.getId());
		ArrayList<String> willBeRemoved = new ArrayList<String>();
		Iterator<PropositionSet> it = justificationSupport.values().iterator();
		while(it.hasNext()){
			PropositionSet thisSet = it.next();
			if(removeSet.isSubSet(thisSet)){
				willBeRemoved.add(thisSet.getHash());
				setTreeComputed(false);
			}
		}
		while(!willBeRemoved.isEmpty()){
			justificationSupport.remove(willBeRemoved.get(0));
			willBeRemoved.remove(0);
		}
		
		Iterator<PropositionSet>  it2 = assumptionBasedSupport.values().iterator();
		while(it2.hasNext()){
			PropositionSet thisSet = it2.next();
			if(removeSet.isSubSet(thisSet)){
				willBeRemoved.add(thisSet.getHash());
				setTreeComputed(false);
			}
		}
		if(propNode.HasChildren()){
		Iterator<PropositionSet>  it3 = propNode.getAssumptionBasedSupport().values().iterator();
		while(it3.hasNext()){
			PropositionSet set = it3.next();
			int [] setnodes = PropositionSet.getPropsSafely(set);
			for (int i = 0; i < setnodes.length; i++) {
				PropositionSet removeSetNew = new PropositionSet(setnodes[i]);
				it2 = assumptionBasedSupport.values().iterator();
				while(it2.hasNext()){
					PropositionSet thisSet = it2.next();
					if(removeSetNew.isSubSet(thisSet)){
						willBeRemoved.add(thisSet.getHash());
						setTreeComputed(false);
					}
				}
			}
		}
		}
		while(!willBeRemoved.isEmpty()){
			assumptionBasedSupport.remove(willBeRemoved.get(0));
			willBeRemoved.remove(0);
		}
		
		if(justificationSupport.isEmpty()){
			setHasChildren(false);
			if(isHyp && assumptionBasedSupport.isEmpty()){
				setHyp(true);
			}
		}
		boolean reStrucured = false;
		while(!reStrucured){
			reStrucured = reStructureJustifications();
		}
		TreeComputed = false;
		
	}

	/**
	 * 
	 * @return int representing the id of this proposition node
	 */
	public int getId() {
		return id;
	}

	/**
	 * This method returns the parent supports of a node.
	 * By parent support i mean " All the nodes that this proposition node supports"
	 * for example if we have the following set of hyps {A, B, A^B --> C, C --> D}
	 * The set of parent supports of D are <C, A^B>
	 * @return ArrayList<Integer> array list of integers for representing the nodes ids.
	 */
	public ArrayList<Integer> getParentSupports() {
		return parentNodes;
	}
	
	/**
	 * This method recursively adds a node id to parentSupport structure in the supports hierarchy.
	 * This method is only used by addJustificationSupport() Method.
	 * @param id representing the node id that is a parent for all below support structure nodes.
	 * @throws NotAPropositionNodeException
	 * @throws NodeNotFoundInNetworkException
	 * @throws DuplicatePropositionException
	 */
	public void addParentNode(int id) throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
		if(this.getId() != id){
		parentNodes.add(id);
		Iterator<PropositionSet> it = justificationSupport.values().iterator();
		while(it.hasNext()){
			int[] nodes = PropositionSet.getPropsSafely(it.next());
			for (int i = 0; i < nodes.length; i++) {
				PropositionNode node = (PropositionNode) Network.getNodeById(nodes[i]);
				node.addParentNode(id);
			}
		}
		}
	}
	
	/**
	 * Tells whether this proposition node is a hypothesis and user asserted, or this proposition node is a derived node.
	 * @return boolean
	 */
	private boolean isHyp() {
		return isHyp;
	}
	
	/*
	 * Sets the attribute isHyp to the method parameter isHyp. 
	 * In-addition, if the method parameter isHyp is equal to true, 
	 * the assumptionBased-Support of this node must will have only an element which is this node id.
	 * Any  hypothesis  or  user  asserted  node  does  not  need  an  supports,  
	 * because  a  hypothesis node is believed by the system without any supports.  
	 * Therefore this node will contain itself only as the assumptionBasedSupport, 
	 * and will have empty set ofJustificationSupport.
	 */
	public void setHyp(boolean isHyp) throws NotAPropositionNodeException, NodeNotFoundInNetworkException{
		this.isHyp = isHyp;
		if(isHyp){
		assumptionBasedSupport = new Hashtable<String, PropositionSet>();
		PropositionSet intialSet = new PropositionSet(id);
		assumptionBasedSupport.put(Integer.toString(id), intialSet);
		}
	}
	/**
	 * Main method contains the run time testing approach for the supports class.
	 * Check SupportTest class to see the unit testing for the supports class.
	 * @param args
	 * @throws NotAPropositionNodeException
	 * @throws NodeNotFoundInPropSetException
	 * @throws NodeNotFoundInNetworkException
	 * @throws DuplicatePropositionException
	 * @throws CannotInsertJustificationSupportException
	 * @throws IllegalIdentifierException
	 */
	public static void main(String[] args)
			throws NotAPropositionNodeException, NodeNotFoundInPropSetException, NodeNotFoundInNetworkException, DuplicatePropositionException, CannotInsertJustificationSupportException, IllegalIdentifierException {
		
		Network net;
		Semantic sem;
		String semanticType = "Proposition";
		
		PropositionNode n0;
		PropositionNode n1;
		PropositionNode n2;
		PropositionNode n3;
		PropositionNode n4;
		PropositionNode n5;
		PropositionNode n6;
		PropositionNode n7;
		PropositionNode n8;
		PropositionNode n9;
		PropositionNode n10;
		PropositionNode n11;
		PropositionNode n12;
		PropositionNode n13;
		PropositionNode n14;
		
		sem = new Semantic(semanticType);
    	net = new Network();
    	
    	//Building Network Nodes
    	//The Network Nodes Labels and Corresponding Ids
    	net.buildBaseNode("s", sem);// 0
		net.buildBaseNode("p", sem);// 1
		net.buildBaseNode("q", sem);// 2
		net.buildBaseNode("r", sem);// 3
		net.buildBaseNode("m", sem);// 4
		net.buildBaseNode("n", sem);// 5
		net.buildBaseNode("v", sem);// 6
		net.buildBaseNode("z", sem);// 7
		net.buildBaseNode("a", sem);// 8
		net.buildBaseNode("b", sem);// 9
		net.buildBaseNode("c", sem);// 10
		net.buildBaseNode("d", sem);// 11
		net.buildBaseNode("e", sem);// 12
		net.buildBaseNode("f", sem);// 13
		net.buildBaseNode("g", sem);// 14
		
		
		//Getting the Network PropositionNodes
		 n0 = (PropositionNode) net.getNode("s");
		 n1 = (PropositionNode) net.getNode("p");
		 n2 = (PropositionNode) net.getNode("q");
		 n3 = (PropositionNode) net.getNode("r");
		 n4 = (PropositionNode) net.getNode("m");
		 n5 = (PropositionNode) net.getNode("n");
		 n6 = (PropositionNode) net.getNode("v");
		 n7 = (PropositionNode) net.getNode("z");
		 n8 = (PropositionNode) net.getNode("a");
		 n9 = (PropositionNode) net.getNode("b");
		 n10 = (PropositionNode) net.getNode("c");
		 n11 = (PropositionNode) net.getNode("d");
		 n12 = (PropositionNode) net.getNode("e");
		 n13 = (PropositionNode) net.getNode("f");
		 n14 = (PropositionNode) net.getNode("g");
		 
		 //Setting Specific Nodes to be Hyps. So that no support are needed for this node.
		 //If node is not set, it is considers as Derived node.
		 n2.setHyp(true);
		 n4.setHyp(true);
		 n5.setHyp(true);
		 n6.setHyp(true);
		 n7.setHyp(true);
		 n9.setHyp(true);
		 n10.setHyp(true);
		 n11.setHyp(true);
		 n12.setHyp(true);
		 n13.setHyp(true);
		 n14.setHyp(true);
		 n0.setHyp(true);
		 
		 int[] pqr = new int[3];
			pqr[0] = 1;
			pqr[1] = 2;
			pqr[2] = 3;
			
			int[] mn = new int[3];
			mn[0] = 5;
			mn[1] = 4;
			mn[2] = 0;
			
			
			int[] vz = new int[2];
			vz[0] = 6;
			vz[1] = 7;
			
			int[] ab = new int[2];
			ab[0] = 9;
			ab[1] = 8;
			
			int[] cde = new int[3];
			cde[0] = 10;
			cde[1] = 11;
			cde[2] = 12;
			
			
			
			int[] fg = new int[2];
			fg[0] = 14;
			fg[1] = 13;
			
		//Creating Proposition Sets by the previous Arrays of int
			PropositionSet s1 = new PropositionSet(pqr);
			PropositionSet s2 = new PropositionSet(mn);
			PropositionSet s3 = new PropositionSet(vz);
			PropositionSet s4 = new PropositionSet(ab);
			PropositionSet s5 = new PropositionSet(cde);
			PropositionSet s6 = new PropositionSet(fg);
			
			
		    
		//Construct the tree "Bottum-Up" See the Graph above the method to imagine the Support Structure!
			
			n1.addJustificationBasedSupport(s2);
		
			n1.addJustificationBasedSupport(s3);
		
			n8.addJustificationBasedSupport(s5);
	
			n3.addJustificationBasedSupport(s4);
		
			n0.addJustificationBasedSupport(s6);
			
			n0.addJustificationBasedSupport(s1);
			
			
			
			//Getting the justification supports of node 1.
			Hashtable<String,PropositionSet>n1Justs= n1.getJustificationSupport();
			//Getting the justification supports of node 0.
			Hashtable<String,PropositionSet>n0Justs= n0.getJustificationSupport();
			//Getting the assumption supports of node 1.
			Hashtable<String,PropositionSet>n1Assump= n1.getAssumptionBasedSupport();
			//Getting the assumption supports of node 0.
			Hashtable<String,PropositionSet>n0Assump= n0.getAssumptionBasedSupport();
			//Getting the supports Tree of node 0.
			ArrayList<ArrayList<ArrayList<Integer>>>mySupportsTree=n0.getMySupportsTree();
			
			System.out.println(n1Justs.toString());
			System.out.println(n1Assump.toString());
			System.out.println(n0Justs.toString());
			System.out.println(n0Assump.toString());
			System.out.println(mySupportsTree.toString());

		
		
	}

	

}
