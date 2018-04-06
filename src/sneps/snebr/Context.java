package sneps.snebr;

import sneps.exceptions.NodeNotFoundException;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;

import java.util.HashSet;


public class Context {
	private PropositionSet hyps;

	private HashSet<String> names;

	protected Context() {
		names = new HashSet<String>();
		this.hyps = new PropositionSet();
	}

	protected Context(String contextName) {
		this();
		names.add(contextName);
	}

	protected Context(Context c) {
		this.hyps = c.getHypothesisSet();
		this.names = c.getNames();
	}

	protected Context(Context c, PropositionNode hyp) {
		this.names = c.getNames();
		PropositionSet propSet = new PropositionSet(c.getHypothesisSet().getProps(), hyp.getId());
		this.hyps = propSet;
	}

	protected Context(String contextName, Context c) {
		this(c);
		this.names.add(contextName);
	}

	protected Context(String contextName, PropositionSet hyps) {
		this(contextName);
		this.hyps = hyps;
	}

	protected Context(Context c, PropositionSet hyps) {

	}

	protected PropositionSet getHypothesisSet() {
		return hyps;
	}

	public HashSet<String> getNames() {
		return names;
	}

	public String getName() {return null;}

	//	public PropositionNode isAsserted(PropositionNode p) {
//
//	}

//	public PropositionSet allAsserted() {
//		Collection<PropositionNode> allPropositionNodes = Network.getPropositionNodes().values();
//		PropositionSet asserted = new PropositionSet();
//		for (PropositionNode node : allPropositionNodes) {
//			if (this.hyps.getNodes().contains(node)) {
//				asserted.add(node);
//			} else {
//				Collection<PropositionSet> justificationSets = node.getBasicSupport()
//													.getAssumptionBasedSupport()
//													.values();
//				for (PropositionSet justificationHyps: justificationSets) {
//					if (justificationHyps.isSubSet(this.hyps)) {
//						asserted.add(node);
//					}
//				}
//			}
//		}
//		return asserted;
//	}

	/**
	 * Adds a name to the set of names of the context if not a duplicate.
	 * @param name Name to be added to the context's names
	 * @return <code>true</code> if the name isn't a duplicate <code>false</code> otherwise.
	 */
	protected boolean addName(String name) {
		return this.names.add(name);
	}

	protected boolean addNames(HashSet<String> names) {
		return this.names.addAll(names);
	}

	/**
	 * Removes a name from the set of names of the context if present.
	 * @param name Name to be remove from the context's names
	 * @return <code>true</code> if this is found <code>false</code> otherwise.
	 */
	protected boolean removeName(String name) {
		return this.names.remove(name);
	}

}
