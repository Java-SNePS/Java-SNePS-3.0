package sneps.snip.matching;

import java.util.HashMap;
import sneps.network.Node;
import sneps.network.VariableNode;

public class HashSubstitutions implements Substitutions {
	HashMap<VariableNode, Node> sub;

	public HashSubstitutions() {
		sub = new HashMap<VariableNode, Node>();
	}

	@Override
	public int cardinality() {

		return sub.size();
	}

	@Override
	public Binding choose() {

		VariableNode v = (VariableNode) sub.keySet();
		return new Binding(v, sub.get(0));
	}

	@Override
	public void clear() {
		sub = new HashMap<VariableNode, Node>();

	}

	@Override
	public Binding getBinding(int x) {

		VariableNode[] vns = new VariableNode[sub.size()];
		vns = sub.keySet().toArray(vns);
		return new Binding(vns[x], sub.get(vns[x]));
	}

	@Override
	public Binding getBindingByNode(Node mn) {

		VariableNode key = srcNode(mn);
		return key == null ? null : new Binding(key, mn);
	}

	@Override
	public Binding getBindingByVariable(VariableNode mv) {

		return sub.containsKey(mv) ? new Binding(mv, sub.get(mv)) : null;
	}

	@Override
	public Substitutions insert(Binding m) {
		HashSubstitutions s = new HashSubstitutions();
		s.insert(this);
		s.putIn(m);
		return s;
	}

	@Override
	public void insert(Substitutions s) {
		for (int i = 0; i < s.cardinality(); i++)
			putIn(s.getBinding(i));

	}

	@Override
	public void insertOrUpdate(Binding mb) {
		if (sub.containsKey(mb.getVariable()))
			update(getBindingByVariable(mb.getVariable()), mb.getNode());

		else
			putIn(mb);
	}

	@Override
	public boolean isBound(VariableNode mv) {
		return sub.containsKey(mv);
	}

	@Override
	public boolean isCompatible(Binding mb) {

		HashSubstitutions test = new HashSubstitutions();
		test.sub.put(mb.getVariable(), mb.getNode());
		return this.isCompatible(test);
	}

	@Override
	public boolean isCompatible(Substitutions s) {

		HashSubstitutions sl = (HashSubstitutions) s;

		for (int i = 0; i < this.sub.size(); i++) {
			for (int j = 0; j < sl.sub.size(); j++) {
				if (sl.sub.keySet() == this.sub.keySet()) {
					if (sl.sub.get(j) != this.sub.get(i))
						return false;
				} else if (sl.sub.get(j) == this.sub.get(i))
					if (sl.sub.keySet() != this.sub.keySet())
						return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEqual(Substitutions s) {

		HashSubstitutions sl = (HashSubstitutions) s;
		if (this.sub.size() == sl.sub.size()) {
			for (int i = 0; i < sl.sub.size(); i++) {
				boolean found = false;
				for (int j = 0; j < this.sub.size() && !found; j++) {
					if (sl.sub.get(i).equals(this.sub.get(j)))
						found = true;
				}
				if (!found)
					return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isMember(Binding mb) {

		Node node = sub.get(mb.getVariable());
		return node != null && node == mb.getNode();
	}

	@Override
	public boolean isNew() {
		return sub.isEmpty();
	}

	@Override
	public boolean isSubSet(Substitutions s) {

		HashSubstitutions sl = (HashSubstitutions) s;
		if (this.sub.size() < sl.sub.size())
			return false;
		for (int i = 0; i < sl.sub.size(); i++) {
			boolean found = false;
			for (int j = 0; j < this.sub.size() && !found; j++) {
				if (sl.sub.get(i).equals(this.sub.get(j)))
					found = true;
			}
			if (!found)
				return false;
		}
		return true;
	}

	@Override
	public boolean isValue(Node mn) {
		return sub.containsValue(mn);
	}

	@Override
	public Substitutions others() {

		HashSubstitutions s1 = new HashSubstitutions();
		VariableNode v = (VariableNode) sub.keySet();
		for (int i = 1; i < this.sub.size(); i++) {
			s1.putIn(new Binding(v, sub.get(i)));
		}
		return s1;
	}

	@Override
	public void putIn(Binding mb) {
		sub.put(mb.getVariable(), mb.getNode());
	}

	@Override
	public Substitutions restrict(VariableNode[] variables) {

		HashSubstitutions hs = new HashSubstitutions();

		for (VariableNode variable : variables)
			hs.putIn(new Binding(variable, sub.get(variable)));
		return hs;
	}

	@Override
	public Substitutions[] split() {

		HashSubstitutions[] res = new HashSubstitutions[2];
		res[0] = new HashSubstitutions();
		res[1] = new HashSubstitutions();
		VariableNode v = (VariableNode) sub.keySet();
		for (int i = 0; i < sub.size(); i++) {
			Binding x = new Binding(v, sub.get(i));
			Node n = x.getNode();
			String name = n.getClass().getName();
			if (sub(name, "sneps.BaseNode"))
				res[0].putIn(x);
			else
				res[1].putIn(x);
		}
		return res;
	}

	@Override
	public VariableNode srcNode(Node mn) {
		VariableNode[] variables = (VariableNode[]) sub.keySet().toArray();

		for (VariableNode variable : variables)

			if (sub.get(variable).equals(mn))

				return variable;

		return null;
	}

	@Override
	public boolean sub(String x, String y) {
		for (int i = 0; i < y.length(); i++)

		{
			if (y.charAt(i) != x.charAt(i))

				return false;
		}
		return true;
	}

	@Override
	public Node term(VariableNode mv) {

		return sub.get(mv);
	}

	@Override
	public Substitutions union(Substitutions s) {

		HashSubstitutions sl = (HashSubstitutions) s;
		HashSubstitutions newList = new HashSubstitutions();
		VariableNode v = (VariableNode) sub.keySet();
		for (int i = 0; i < this.sub.size(); i++) {
			if (!newList.equals(sub.get(i))) {
				newList.putIn(new Binding(v, sub.get(i)));
			}
		}
		for (int i = 0; i < sl.sub.size(); i++) {
			if (!newList.equals(sl.sub.get(i))) {
				newList.putIn(new Binding(v, sub.get(i)));
			}
		}
		return newList;
	}

	@Override
	public void unionIn(Substitutions s) {

		HashSubstitutions sl = (HashSubstitutions) s;
		VariableNode v = (VariableNode) sub.keySet();
		for (int i = 0; i < sl.sub.size(); i++) {
			if (!this.equals(sl.sub.get(i))) {
				this.putIn(new Binding(v, sub.get(i)));
			}
		}
	}

	@Override
	public void update(Binding mb, Node mn) {
		sub.put(mb.getVariable(), mn);
	}

	@Override
	public Node value(VariableNode v) {

		Node n = sub.get(v);
		return n == null ? v : n;
	}

	public String toString() {
		String res = "";
		for (VariableNode vn : sub.keySet()) {
			res += vn + "substitutes " + sub.get(vn).toString()+'\n';

		}
		return res;
	}

}
