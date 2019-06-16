package sneps.snip.matching;

import java.util.HashMap;
import sneps.network.Node;
import sneps.network.VariableNode;

public class HashSubstitutions implements Substitutions {
	HashMap<VariableNode, Node> sub;

	/**
	 *Creates new HashMap for substitutions 
	 */
	
	public HashSubstitutions() {
		sub = new HashMap<VariableNode, Node>();
	}

	/**
	 * Returns the number of bindings in the HashMap
	 * @return number of bindings
	 */
	
	@Override
		public int cardinality() {

		return sub.size();
	}

	/**
	 * Returns the first Binding in the HashMap
	 * @return Binding
	 */
	
	@Override
		public Binding choose() {
		VariableNode[] vn = new VariableNode[sub.size()];
		vn = sub.keySet().toArray(vn);
		return new Binding(vn[0], sub.get(vn[0]));
	}

	/**
	 * Clear all Bindings from the substitutions list
	 */
	
	@Override
	public void clear() {
		sub = new HashMap<VariableNode, Node>();

	}

	/**
	 * Return the Binding number x in the HashMap
	 * @param x binding number
	 * @return Binding
	 */
	
	@Override
	public Binding getBinding(int x) {

		VariableNode[] vns = new VariableNode[sub.size()];
		vns = sub.keySet().toArray(vns);
		return new Binding(vns[x], sub.get(vns[x]));
	}

	/**
	 *Returns the binding witch have mn as its node or null if mn is not in the
	 *HashMap
	 *@param mn node
	 *@return binding or null
	 */
	
	@Override
	public Binding getBindingByNode(Node mn) {

		VariableNode key = srcNode(mn);
		return key == null ? null : new Binding(key, mn);
	}

	/**
	 *Returns the binding witch have mv as its variable node or null if mv is
	 *not in the HashMap
	 *@param mv VariableNode
	 *@return Binding or null
	 */
	
	@Override
	public Binding getBindingByVariable(VariableNode mv) {

		return sub.containsKey(mv) ? new Binding(mv, sub.get(mv)) : null;
	}

	/**
	 * Returns a new substitution HashMap with the binding of this added to them
	 * the Binding m
	 * @param m Binding
	 * @return Substitutions
	 */
	
	@Override
	public Substitutions insert(Binding m) {
		HashSubstitutions s = new HashSubstitutions();
		s.insert(this);
		s.putIn(m);
		return s;
	}

	/**
	 * Insert s in this substitutions HashMap
	 * @param s
	 */
	
	@Override
	public void insert(Substitutions s) {
		for (int i = 0; i < s.cardinality(); i++)
			putIn(s.getBinding(i));

	}

	/**
	 * Insert Binding mb  in this substitutions HashMap if the variable is not bound
	 * if the variable of binding mb is bound then update it 
	 * @param mb
	 */
	
	@Override
	public void insertOrUpdate(Binding mb) {
		if (sub.containsKey(mb.getVariable()))
			update(getBindingByVariable(mb.getVariable()), mb.getNode());

		else
			putIn(mb);
	}
	
	/**
	 *Check if the variable node is bound in this substitution HashMap or not.
	 *@param mv the variable node
	 *@return true if the mv is bound false otherwise
	 */
	
	@Override
	public boolean isBound(VariableNode mv) {
		return sub.containsKey(mv);
	}

	/**
	 * Check if mb is compatible with this substitutions HashMap
	 * @param mb Binding
	 * @return true or false
	 */
	
	@Override
	public boolean isCompatible(Binding mb) {

		HashSubstitutions test = new HashSubstitutions();
		test.sub.put(mb.getVariable(), mb.getNode());
		return this.isCompatible(test);
	}
	
	/**
	 * Check if the substitutions s is compatible to this HashMap or not
	 * two substitutions HashMaps are compatible if every variable node in both are bound to the same
	 * node and every node in both are bound to the same variable node
	 * @param s 
	 * @return true or false
	 */

	@Override
	public boolean isCompatible(Substitutions s) {

		HashSubstitutions sl = (HashSubstitutions) s;
		VariableNode[]vns=new VariableNode[this.sub.size()];
		vns=this.sub.keySet().toArray(vns);
		VariableNode[] vn = new VariableNode[sl.sub.size()];
		vn = sl.sub.keySet().toArray(vn);

		for (int i = 0; i < this.sub.size(); i++) {
			
			for (int j = 0; j < sl.sub.size(); j++) {
				
				if (vn[j]== vns[i]) {
					if (sl.sub.get(vn[j]) != this.sub.get(vns[i]))
						return false;
				} else if (sl.sub.get(vn[j]) == this.sub.get(vns[i]))
					if (vn[j]!= vns[i])
						return false;
			}
		}
		return true;
	}

	/**
	 *Check if substitutions HashMap s is a equal to this substitutions HashMap
	 *@param s 
	 *@return true if s is a equal to this false otherwise
	 */
	
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
	
	/**
	 *Check if the binding mb is in the substitutions HashMap or not
	 *@param mb the binding
	 *@return true if mb exists and return false otherwise
	 */

	@Override
	public boolean isMember(Binding mb) {

		Node node = sub.get(mb.getVariable());
		return node != null && node == mb.getNode();
	}

	/**
	 *Check if the substitutions HashMap is new (Empty) or not
	 *@return true if new false otherwise
	 */
	
	@Override
	public boolean isNew() {
		return sub.isEmpty();
	}
	
	/**
	 *Check if substitutions HashMap s is a subset of this substitutions HashMap
	 *@param s 
	 *@return true if s is a subset of this false otherwise
	 */

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
	
	/**
	 *Check if the node is a value in this substitution HashMap or not.
	 *@param mn the node
	 *@return true if the mn is a value false otherwise
	 */

	@Override
	public boolean isValue(Node mn) {
		return sub.containsValue(mn);
	}
	
	/**
	 * Return a substitutions HashMap with all the bindings in the substitutions HashMap
	 * except the first binding
	 * @return Substitutions
	 */

	@Override
	public Substitutions others() {

		HashSubstitutions s1 = new HashSubstitutions();
		VariableNode[] vn = new VariableNode[sub.size()];
		vn = sub.keySet().toArray(vn);
		
		for (int i = 1; i < this.sub.size(); i++) {
			s1.putIn(new Binding(vn[i], sub.get(vn[i])));
		}
		return s1;
	}
	
	/**
	 *Insert a new binding in the HashMap substitutions
	 *@param mb Binding
	 */

	@Override
	public void putIn(Binding mb) {
		sub.put(mb.getVariable(), mb.getNode());
	}
	
	/**
	 * returns a substitutions 	HashMap consisting of only those bindings
	 * whose variable node are in variables
	 * @param variables array of variableNode nodes
	 * @return substitutions HashMap
	 */

	@Override
	public Substitutions restrict(VariableNode[] variables) {

		HashSubstitutions hs = new HashSubstitutions();

		for (VariableNode variable : variables)
			hs.putIn(new Binding(variable, sub.get(variable)));
		return hs;
	}
	
	/**
	 * Split the substitutions HashMap into two parts. The first one is that bindings
	 * with a base node as its node, and the second one is the rest of the
	 * substitutions HashMap
	 * @return the new substitution HashMap 
	 */

	@Override
	public Substitutions[] split() {

		HashSubstitutions[] res = new HashSubstitutions[2];
		res[0] = new HashSubstitutions();
		res[1] = new HashSubstitutions();
		VariableNode[] vn = new VariableNode[sub.size()];
		vn = sub.keySet().toArray(vn);
		
		for (int i = 0; i < sub.size(); i++) {
			Binding x = new Binding(vn[i], sub.get(vn[i]));
			Node n = x.getNode();
			String name = n.getClass().getName();
			if (sub(name, "sneps.BaseNode"))
				res[0].putIn(x);
			else
				res[1].putIn(x);
		}
		return res;
	}
	
	/**
	 *Returns the variable node of the node mn in the substitutions HashMap if node is
	 *not in the substitutions HashMap return null
	 *@param mn is the node
	 *@return VariableNode or null
	 */

	@Override
	public VariableNode srcNode(Node mn) {
		VariableNode[] variables = (VariableNode[]) sub.keySet().toArray();
		
		for (VariableNode variable : variables)
		  for (int i = 0; i < sub.size(); i++)
			//if (sub.get(variable).equals(mn))
			  if(sub.get(variables[i]).equals(mn))

				return variable;
		        

		return null;
	}

	/**
	 * String checking.
	 * @param x String
	 * @param y String
	 * @return true or false
	 */
	
	@Override
	public boolean sub(String x, String y)
	{
		for(int i=0;i<y.length();i++)
		{
			if(y.charAt(i)!=x.charAt(i))
				return false;
		}
		return true;
	}
	
	/**
	 * If mv is an variable node which is bound, then returns the node to which mv is
	 * bound  otherwise it returns null
	 * @param mv variable node
	 * @return node or null
	 */

	@Override
	public Node term(VariableNode mv) {
        if(sub.get(mv)!=null){
		  return sub.get(mv); }
        return null;
       
       
	}
	
	/**
	 * Union the substitution HashMap s with this substitution HashMap in a new
	 * substitutions HashMap
	 * @param s
	 * @return substitutions
	 */

	@Override
	public Substitutions union(Substitutions s) {

		HashSubstitutions sl = (HashSubstitutions) s;
		HashSubstitutions newList = new HashSubstitutions();
		VariableNode[] vn = new VariableNode[sub.size()];
		vn = sub.keySet().toArray(vn);
		for (int i = 0; i < this.sub.size(); i++) {
			if (!newList.equals(sub.get(i))) {
				newList.putIn(new Binding(vn[i], sub.get(vn[i])));
			}
		}
		for (int i = 0; i < sl.sub.size(); i++) {
			if (!newList.equals(sl.sub.get(i))) {
				newList.putIn(new Binding(vn[i], sub.get(vn[i])));
			}
		}
		return newList;
	}
	
	/**
	 * Union the substitution HashMap s with this substitution HashMap in this
	 * @param s 
	 */

	@Override
	public void unionIn(Substitutions s) {

		HashSubstitutions sl = (HashSubstitutions) s;
		VariableNode[] vn = new VariableNode[sub.size()];
		vn = sub.keySet().toArray(vn);
		for (int i = 0; i < sl.sub.size(); i++) {
			if (!this.equals(sl.sub.get(i))) {
				this.putIn(new Binding(vn[i], sub.get(vn[i])));
			}
		}
	}
	
	/**
	 *Update the value of a binding with the new node
	 *@param mb the binding
	 *@param mn the new node
	 */

	@Override
	public void update(Binding mb, Node mn) {
		sub.put(mb.getVariable(), mn);
	}
	
	/**
	 * If the node v is bound to another node return the one bounding it
	 * otherwise return the node it self
	 * @param v node
	 * @return node
	 */

	@Override
	public Node value(VariableNode v) {

		Node n = sub.get(v);
		return n == null ? v : n;
	}

	/**
	 * Print the substitutions HashMap
	 */
	
	public String toString() {
		String res = "";
		for (VariableNode vn : sub.keySet()) {
			res += vn + "substitutes " + sub.get(vn).toString()+'\n';

		}
		return res;
	}

}
