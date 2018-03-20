package sneps.snebr;

import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class Context {
	private PropositionSet hyps;

	private HashSet<String> names;

	public Context() {
		hyps = new PropositionSet();
		names = new HashSet<String>();
	}

	public Context(Context c) {
		this.hyps = c.getHypothesisSet();
		this.names = c.getNames();
	}

	public Context(PropositionNode hyp) {
		this.hyps=  new PropositionSet();
		this.hyps.add(hyp);
	}

	public Context(PropositionSet hyps) {
		this.hyps = hyps;
	}

	public PropositionSet getHypothesisSet() {
		return hyps;
	}

	public HashSet<String> getNames() {
		return names;
	}
//
//	public PropositionNode isAsserted(PropositionNode p) {
//
//	}

	/**
	 * Creates a new Context with the propositionNode
	 * @param hyp Propsosition Node to be added to the context's hyps
	 * @return <code>true</code> if the hyp isn't a duplicate <code>false</code> otherwise.
	 */
	public Context addProp(PropositionNode hyp) {
		Context newContext = new Context(this);
		newContext.getHypothesisSet().add(hyp);
		// TODO: check for contradiciton
		return newContext;
	}

	public Context addProps(HashSet<PropositionNode> hyps) {
		Context newContex = new Context(this);
		newContex.getHypothesisSet().addAll(hyps);
		// TODO: check for contradiciton
		return newContex;
	}

	public Context removeProp(PropositionNode hyp) {
		Context newContext = new Context(this);
		newContext.getHypothesisSet().remove(hyp);
		// TODO: 13/03/18 check for contradiciton
		return newContext;
	}

	public PropositionSet allAsserted() {
		Collection<PropositionNode> allPropositionNodes = Network.getPropositionNodes().values();
		PropositionSet asserted = new PropositionSet();
		for (PropositionNode node : allPropositionNodes) {
			if (this.hyps.getNodes().contains(node)) {
				asserted.add(node);
			} else {
				Collection<PropositionSet> justificationSets = node.getBasicSupport()
													.getAssumptionBasedSupport()
													.values();
				for (PropositionSet justificationHyps: justificationSets) {
					if (justificationHyps.isSubSet(this.hyps)) {
						asserted.add(node);
					}
				}
			}
		}
		return asserted;
	}

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

}
