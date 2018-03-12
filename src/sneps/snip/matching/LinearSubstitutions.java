package sneps.snip.matching;

import sneps.network.Node;
import sneps.network.classes.term.Variable;

public class LinearSubstitutions implements Substitutions {
//TODO Empty methods
	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public void putIn(Binding mb) {
	}

	@Override
	public boolean isCompatible(Binding mb) {
		return false;
	}

	@Override
	public void update(Binding mb, Node mn) {
	}

	@Override
	public boolean isBound(Variable mv) {
		return false;
	}

	@Override
	public boolean isValue(Node mn) {
		return false;
	}

	@Override
	public Variable srcNode(Node mn) {
		return null;
	}

	@Override
	public Binding getBindingByVariable(Variable mv) {
		return null;
	}

	@Override
	public Binding getBindingByNode(Node mn) {
		return null;
	}

	@Override
	public boolean isMember(Binding mb) {
		return false;
	}

	@Override
	public boolean isSubSet(Substitutions s) {
		return false;
	}

	@Override
	public boolean isEqual(Substitutions s) {
		return false;
	}

	@Override
	public Substitutions union(Substitutions s) {
		return null;
	}

	@Override
	public void unionIn(Substitutions s) {
		
	}

	@Override
	public Substitutions restrict(Variable[] ns) {
		return null;
	}

	@Override
	public Node term(Variable mv) {
		return null;
	}

	@Override
	public int cardinality() {
		return 0;
	}

	@Override
	public Binding choose() {
		return null;
	}

	@Override
	public Substitutions others() {
		return null;
	}

	@Override
	public Node value(Variable n) {
		return null;
	}

	@Override
	public Substitutions insert(Binding m) {
		return null;
	}

	@Override
	public boolean isCompatible(Substitutions s) {
		return false;
	}

	@Override
	public Binding getBinding(int x) {
		return null;
	}

	@Override
	public Substitutions[] split() {
		return null;
	}

	@Override
	public void clear() {
	}

	@Override
	public void insert(Substitutions s) {
	}

	@Override
	public boolean sub(String x, String y) {
		return false;
	}

	@Override
	public int termID(int variableID) {
		return 0;
	}

	@Override
	public void insertOrUpdate(Binding mb) {
	}
}