package sneps.snip.matching;

import sneps.network.Node;
import sneps.network.VariableNode;

public interface Substitutions {
	
	public int cardinality();
	public Binding choose();
	public void clear();
	public Binding getBinding(int x);
    public Binding getBindingByNode(Node mn);
    public Binding getBindingByVariable(VariableNode mv);
    public Substitutions insert(Binding m);
    public void insert(Substitutions s);
    public void insertOrUpdate(Binding mb);
    public boolean isBound(VariableNode mv);
    public boolean isCompatible(Binding mb);
    public boolean isCompatible(Substitutions s);
    public boolean isEqual(Substitutions s);
    public boolean isMember(Binding mb);
    public boolean isNew();
    public boolean isSubSet(Substitutions s);
    public boolean isValue(Node mn);
    public Substitutions others();
    public void putIn(Binding mb);
    public Substitutions restrict(VariableNode [] ns);
    public Substitutions[] split();
    public VariableNode srcNode(Node mn);
    public boolean sub(String x, String y);
    public Node term(VariableNode mv);
    public String toString();
    public Substitutions union (Substitutions s);
    public void unionIn (Substitutions s);
    public void update(Binding mb , Node mn);
    public Node value(VariableNode n);
   


}
