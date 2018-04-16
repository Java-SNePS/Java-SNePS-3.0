package sneps.snip.rules;

import java.util.Hashtable;
import java.util.Set;

import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Term;
import sneps.setClasses.NodeSet;
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;

public class AndNode extends RuleNode {
	private Hashtable<Integer, Set<RuleUseInfo>> ruisNotSent;
	private NodeSet consequents;

	public AndNode(Term syn) {
		super(syn);
		ruisNotSent = new Hashtable<Integer, Set<RuleUseInfo>>();
		setConsequents(new NodeSet());
	}
	public AndNode(Semantic sym, Term syn) {
		super(sym, syn);
		ruisNotSent = new Hashtable<Integer, Set<RuleUseInfo>>();
		setConsequents(new NodeSet());
	}

	@Override
	protected void sendRui(RuleUseInfo tRui, String contextID) {
		// TODO Auto-generated method stub

	}

	public RuisHandler createRuisHandler() {
		PTree tree = new PTree();
		tree.buildTree(antNodesWithVars);
		return this.addContextRUIS(tree);
	};
	
	@Override
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("&ant");//ants for & TODO name convention
	}
	
	@Override
	public void clear(){
		super.clear();
		ruisNotSent.clear();
	}

	public 
	public NodeSet getConsequents() {
		return consequents;
	}
	public void setConsequents(NodeSet consequents) {
		this.consequents = consequents;
	}

}
