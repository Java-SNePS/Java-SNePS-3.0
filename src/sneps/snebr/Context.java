package sneps.snebr;

import sneps.network.PropositionNode;

import java.util.HashSet;

public class Context {
	private HashSet<PropositionNode> hyps;

	private HashSet<String> names;

	public Context() {
	}

	public Context(Context c) {
		this.hyps = c.getHypothesisSet();
		this.names = c.getNames();
	}

	public Context(PropositionNode hyp) {
		this.hyps=  new HashSet<PropositionNode>();
		this.hyps.add(hyp);
	}

	public Context(HashSet<PropositionNode> hyps) {
		this.hyps = hyps;
	}

	public HashSet<PropositionNode> getHypothesisSet() {
		return hyps;
	}

	public HashSet<String> getNames() {
		return names;
	}


	/**
	 * Adds a name to the set of names of the context if not a duplicate.
	 * @param name Name to be added to the context's names
	 * @return <code>true</code> if the name isn't a duplicate <code>false</code> otherwise.
	 */
	public boolean addName(String name) {
		return this.names.add(name);
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
