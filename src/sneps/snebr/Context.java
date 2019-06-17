package sneps.snebr;

import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;


public class Context implements Serializable{
	private PropositionSet hyps;

	private ArrayList<PropositionSet> telescopedSet;

	private HashSet<String> names;

	private BitSet hypsBitset;

	private ArrayList<BitSet> telescopedBitset;

	protected BitSet getHypsBitset() {
		return hypsBitset;
	}

	/**
	 * Constructs a new empty Context
	 */
	protected Context() {
		names = new HashSet<String>();
		this.hyps = new PropositionSet();
		this.hypsBitset = new BitSet();
		this.telescopedSet = new ArrayList<PropositionSet>();
		this.telescopedBitset = new ArrayList<BitSet>();
	}

	/**
	 * Constructs a new Context given its name
	 *
	 * @param contextName the name of the Context to be created
	 */
	protected Context(String contextName) {
		this();
		names.add(contextName);
	}

	/**
	 * Constructs a new Context from another Context
	 *
	 * @param c the context that the new Context is constructed from
	 */
	protected Context(Context c) {
		this.hyps = c.getHypothesisSet();
		this.names = c.getNames();
		this.hypsBitset = c.getHypsBitset();
		this.telescopedSet = (ArrayList<PropositionSet>) c.getTelescopedSet().clone();
		this.telescopedBitset = c.getTelescopedBitset();
	}

	/**
	 * Constructs a new Context from a Context and asserts a hypothesis <i>hyp</i> in it.
	 *
	 * @param c   the context to be used for constructing this new Context
	 * @param hyp the hyp to be asserted in the new Context
	 * @throws DuplicatePropositionException  if the hyp is present in the context c
	 * @throws NodeNotFoundInNetworkException
	 */
	protected Context(Context c, int hyp) throws NotAPropositionNodeException, 
	DuplicatePropositionException, NodeNotFoundInNetworkException {
		this.names = c.getNames();
		this.hyps = c.getHypothesisSet().add(hyp);
		this.hypsBitset = (BitSet) c.getHypsBitset().clone();
		this.hypsBitset.set(hyp);
		this.telescopedSet = (ArrayList<PropositionSet>) c.getTelescopedSet().clone();
		this.telescopedBitset = c.getTelescopedBitset();
	}

	/**
	 * Constructs a new Context from an old Context <i>c</i>, and adds a new name to this new Context
	 *
	 * @param contextName
	 * @param c
	 */
	protected Context(String contextName, Context c) {
		this(c);
		this.names.add(contextName);
	}

	/**
	 * Constructs a new Context using its name and sets the Context's hyps to a passed Proposition set 
	 * <i>hyps</i>.
	 *
	 * @param contextName name of the new Context
	 * @param hyps        the hyps the Context's hyps should be set to
	 */
	protected Context(String contextName, PropositionSet hyps) throws NotAPropositionNodeException, 
	NodeNotFoundInNetworkException {
		this(contextName);
		this.hyps = hyps;
		this.hypsBitset = new BitSet();
		int [] arr = PropositionSet.getPropsSafely(this.hyps);
		for (int i = 0; i < arr.length; i++)
			this.hypsBitset.set(arr[i]);
		this.telescopedSet = new ArrayList<PropositionSet>();
		this.telescopedBitset = new ArrayList<BitSet>();
	}

	protected Context(String contextName, PropositionSet hyps, ArrayList<PropositionSet> props) throws NotAPropositionNodeException, 
	NodeNotFoundInNetworkException {
		this(contextName);
		this.hyps = hyps;
		this.hypsBitset = new BitSet();
		int [] arr = PropositionSet.getPropsSafely(this.hyps);
		for (int i = 0; i < arr.length; i++)
			this.hypsBitset.set(arr[i]);
		this.telescopedSet = (ArrayList<PropositionSet>) props.clone();
		this.telescopedBitset = new ArrayList<BitSet>();
		for(int i=0; i < this.telescopedSet.size(); i++) {
			arr = PropositionSet.getPropsSafely(this.telescopedSet.get(i));
			BitSet temp = new BitSet();
			if(this.telescopedBitset.size() > i) 
				temp = this.telescopedBitset.get(i);
			for (int j = 0; j < arr.length; j++) {

				temp.set(arr[j]);

			}
			this.telescopedBitset.add(temp);
		}
	}

	/**
	 * Returns the hyps of this Context
	 *
	 * @return a PropositionSet containing the hyps of this Context
	 */
	public PropositionSet getHypothesisSet() {
		return hyps;
	}

	protected ArrayList<BitSet> getTelescopedBitset() {
		return telescopedBitset;
	}

	public ArrayList<PropositionSet> getTelescopedSet() {
		return telescopedSet;
	}

	/**
	 * Returns the names of this Context object.
	 *
	 * @return a Hashset of type String containing the names of this context.
	 */
	protected HashSet<String> getNames() {
		return names;
	}

	public String getName() {
		return null;
	}

	/**
	 * Checks if a propositions is asserted in this context
	 *
	 * @param p the proposition to be checked for assertion.
	 * @return <code>true</code> if the proposition exists, otherwise <code>false</code>
	 * @throws NotAPropositionNodeException   If the node p is not a proposition.
	 * @throws NodeNotFoundInNetworkException If the node p doesn't exist in the network.
	 */
	public boolean isAsserted(PropositionNode p) throws NotAPropositionNodeException,
	NodeNotFoundInNetworkException {
		return isAsserted(p, 0);
	}

	public boolean isAsserted(PropositionNode p, int level) throws NotAPropositionNodeException,
	NodeNotFoundInNetworkException {
		int hyp = p.getId();

		return Arrays.binarySearch(PropositionSet.getPropsSafely(this.hyps), hyp) > 0
				|| isSupported(p, level);
	}

	public boolean isSupported(PropositionNode node) {
		return isSupported(node, 0);
	}

	/**
	 * Checks if a node is supported by checking if it's origin set O is a subset
	 * of the current context, and also if T exists if it's a subset of the current context's
	 * telescopedSet. Both have to be true in order for the node to be supported.
	 * @param level represents which level of telescoping is this node asserted in
	 */

	public boolean isSupported(PropositionNode node, int level) {
		Collection<PropositionSet> assumptionSet = node.getBasicSupport()
				.getAssumptionBasedSupport()
				.values();
		for (PropositionSet assumptionHyps : assumptionSet) {
			if (assumptionHyps.isSubSet(this.hyps)) {
				if(level == 0)
					return true;
				PropositionSet telescopedSet = node.getBasicSupport()
						.getTelescopedSupport().get(assumptionHyps.getHash());
				if(this.telescopedSet.size() >= level && level > 0) {
					if (telescopedSet.isSubSet(this.telescopedSet.get(level-1))||telescopedSet.isEmpty()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public PropositionSet allAsserted() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
		Collection<PropositionNode> allPropositionNodes = Network.getPropositionNodes().values();
		PropositionSet asserted = new PropositionSet();
		int[] hyps;
		hyps = PropositionSet.getPropsSafely(this.hyps);
		for (PropositionNode node : allPropositionNodes) {
			if (Arrays.binarySearch(hyps, node.getId()) > 0) {
				asserted = asserted.add(node.getId());
			} else if (isSupported(node)) {
				asserted = asserted.add(node.getId());
			}
		}

		return asserted;
	}


	/**
	 * Adds a name to the set of names of the context if not a duplicate.
	 *
	 * @param name Name to be added to the context's names
	 * @return <code>true</code> if the name isn't a duplicate <code>false</code> otherwise.
	 */
	protected boolean addName(String name) {
		return this.names.add(name);
	}

	/**
	 * Adds multiple names to this Context
	 *
	 * @param names a HashSet of type String of the names to add to this Context
	 * @return <code>true</code> if one at least one of the names isn't a duplicate <code>false</code> otherwise.
	 */
	protected boolean addNames(HashSet<String> names) {
		return this.names.addAll(names);
	}

	/**
	 * Removes a name from the set of names of the context if present.
	 *
	 * @param name Name to be remove from the context's names
	 * @return <code>true</code> if this is found <code>false</code> otherwise.
	 */
	protected boolean removeName(String name) {
		return this.names.remove(name);
	}

}