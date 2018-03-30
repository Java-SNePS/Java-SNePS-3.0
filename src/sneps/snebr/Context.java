package sneps.snebr;

import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;

import java.util.Collection;
import java.util.HashSet;


public class Context {
	private PropositionSet hyps;

	private HashSet<String> names;

	public Context() {

	}

	public Context(String contextName) {
		names = new HashSet<String>();
		names.add(contextName);
	}

	public Context(Context c) {
		this.hyps = c.getHypothesisSet();
		this.names = c.getNames();
	}

	public Context(Context c, PropositionNode hyp) {
		this.names = c.getNames();
		PropositionSet propSet = new PropositionSet(c.getHypothesisSet().getProps(), hyp.getId());
		this.hyps = propSet;
	}

	public Context(String contextName, Context c) {
		this(c);
		this.names.add(contextName);
	}

	public Context(String contextName, PropositionSet hyps) {
		this(contextName);
		this.hyps = hyps;
	}

	public PropositionSet getHypothesisSet() {
		return hyps;
	}

	private void setHypothesisSet(PropositionSet hyps) {
		this.hyps = hyps;
	}

	public HashSet<String> getNames() {
		return names;
	}
//
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
	public boolean addName(String name) {
		return this.names.add(name);
	}

	public boolean addNames(HashSet<String> names) {
		return this.names.addAll(names);
	}

	/**
	 * Removes a name from the set of names of the context if present.
	 * @param name Name to be remove from the context's names
	 * @return <code>true</code> if this is found <code>false</code> otherwise.
	 */
	public boolean removeName(String name) {
		return this.names.remove(name);
	}

	public Object getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
