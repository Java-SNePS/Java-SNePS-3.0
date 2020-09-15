package sneps.snip.rules;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Rule;

import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Term;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuleUseInfo;

public class AndEntailment extends RuleNode {
	private static final long serialVersionUID = -8545987005610860977L;
	private NodeSet consequents;//TODO Proposition Nodes get

	public AndEntailment(Term syn) {
		super(syn);
		setConsequents(new NodeSet());
	}

	public ArrayList<Report> getConsequentReports(ArrayList<Node> concequents, PropositionNode Node, Context c) {
		ArrayList<Report> reports = new ArrayList<Report>();
		for(Node n : consequents) {
			reports.add(n.processReports());
		}
		return reports;
	}
	
	
	
	
	
	
	
	private void ConcludeIntroduction(boolean wrongRule, int posCount, int negCount, ArrayList<Node> consequents2) {
		// TODO Auto-generated method stub
		if(wrongRule) {
			System.out.println("This rule is a wrong rule");
		}
	}
	
	
	public int isAndEntReportSetValid(ArrayList<Report> reports, Node RulNode, ArrayList<Node> antecedents) {
		
	  int isValid = 0;
		for(Report report : reports) {
			for(Support s : report.getSupports()) {
				boolean containsIntroRule = false;
				boolean containsNodesReq = true;
				for(Node n : s.getNodes()) {
					if(n instanceof RuleNode) {
						containsIntroRule = true;
					}
					for(Node ant : s.getAntecedents()) {
						if(!((RuleNode) n).getPatternNodes().contains(ant)) {
							containsNodesReq = false;
							break;
						}	
					}
					if (!containsIntroRule && !containsNodesReq) {
					   return 3;	
					}
					if (report.isNegative() && !containsIntroRule && containsNodesReq) {
						   return -1;	
						}
					if (report.isPositive() && !containsIntroRule && containsNodesReq) {
						   isValid = 1;	
						}
					
			}
		}

			
	}
		return isValid;
}

	public void applyAndRuleHandler(PropositionNode AndNode, int ContextId ) throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
		 
		IntroductionProcess i = new IntroductionProcess();

		ArrayList<Node> antecedents = new ArrayList<Node>();
	    ArrayList<Node> consequents = new ArrayList<Node>();
	    Context TempContext = i.getTempContext(antecedents,ContextId);
	   
	    int posCount = 0;
	    int negCount = 0;
	    boolean wrongRule = false;
	    
	    for(Node n: consequents ) {
	    	
	    	ArrayList<Report> reports = getConsequentReports(consequents, AndNode, TempContext);
	    	int isAndEntReportSetValid = isAndEntReportSetValid(reports, AndNode,antecedents);
	    	
	    	
	    	if (isAndEntReportSetValid > 1) {
	    		wrongRule = true;
	    		break;
	    		}
	    	
	    	else if(isAndEntReportSetValid == -1) {
	    		negCount += 1 ;
	    		break;
	    	}
	    	else if(isAndEntReportSetValid == 1) {
	    		posCount += 1 ;
	    
	    	}
	    	ConcludeIntroduction(wrongRule, posCount, negCount, consequents);
	    }
	    
	    

	    
	
		
    		
		
		
		
		
		
		
		
		
		
		
	// OLD	
//		String contxt = report.getContextName();
//		if (report.isPositive()) {
//			FlagNodeSet fns = new FlagNodeSet();
//			fns.putIn(new FlagNode(signature, report.getSupports(), 1));
//			RuleUseInfo rui = new RuleUseInfo(report.getSubstitutions(),
//					1, 0, fns);
//			addNotSentRui(rui, contxt,signature);
//		}
//		if (contextRuisSet.getByContext(contxt).getPositiveNodes().size() >= antNodesWithoutVars.size() + antNodesWithVars.size())
//			sendSavedRUIs(report.getContextName());
//	}
//
//	@Override
//	protected void applyRuleOnRui(RuleUseInfo Rui, String contextID) {
//		//addNotSentRui(Rui, contextID);
//		if (Rui.getPosCount() != antNodesWithVars.size() + antNodesWithoutVars.size())
//			return;
//		Support originSupports = this.getBasicSupport();
//		HashSet<Support> sup = new HashSet<Support>();
//		sup.add(originSupports);
//		
//		//Send this V
//		contextRuisSet.getByContext(contextID).getPositiveNodes().addNode(this);
//
//		//contextRuisSet.getByContext((String) Controller.getContextByName(contextID)).insertRUI(Rui);
//
//		Report reply = new Report(Rui.getSub(), Rui.getSupport(sup), true, contextID);
//		broadcastReport(reply);
	}
	
	
	
	
	
	
	
	
	


//	@Override
//	public void applyRuleHandler(Report report, Node signature) {
//		String contxt = report.getContextName();
//		if (report.isPositive()) {
//			FlagNodeSet fns = new FlagNodeSet();
//			fns.putIn(new FlagNode(signature, report.getSupports(), 1));
//			RuleUseInfo rui = new RuleUseInfo(report.getSubstitutions(),
//					1, 0, fns);
//			addNotSentRui(rui, contxt,signature);
//		}
//		if (contextRuisSet.getByContext(contxt).getPositiveNodes().size() >= antNodesWithoutVars.size() + antNodesWithVars.size())
//			sendSavedRUIs(report.getContextName());
//	}
//
//	@Override
//	protected void applyRuleOnRui(RuleUseInfo Rui, String contextID) {
//		//addNotSentRui(Rui, contextID);
//		if (Rui.getPosCount() != antNodesWithVars.size() + antNodesWithoutVars.size())
//			return;
//		Support originSupports = this.getBasicSupport();
//		HashSet<Support> sup = new HashSet<Support>();
//		sup.add(originSupports);
//		
//		//Send this V
//		contextRuisSet.getByContext(contextID).getPositiveNodes().addNode(this);
//
//		//contextRuisSet.getByContext((String) Controller.getContextByName(contextID)).insertRUI(Rui);
//
//		Report reply = new Report(Rui.getSub(), Rui.getSupport(sup), true, contextID);
//		broadcastReport(reply);
//	}
//
//	public void addNotSentRui(RuleUseInfo rui, String contxt, Node signature){
//		PTree tree = (PTree) contextRuisSet.getByContext(contxt);
//		if (tree == null)
//			tree = (PTree) createRuisHandler(contxt);
//		tree.insertRUI(rui);
//		tree.getPositiveNodes().addNode(signature);
//		contextRuisSet.addHandlerSet(contxt, tree);
//	}
//	private void sendSavedRUIs(String contextID) {
//		RuleUseInfo addedConstant = getConstantRUI(contextID);
//		if (addedConstant == null && antNodesWithoutVars.size() != 0)
//			return;
//
//		if (antNodesWithoutVars.size() != addedConstant.getPosCount())
//			return;
//
//		RuleUseInfoSet ruis = ((PTree)contextRuisSet.getByContext(contextID)).getAllRootRuis();
//		if (ruis == null) {
//			applyRuleOnRui(addedConstant, contextID);
//			return;
//		}
//
//		RuleUseInfo combined;
//		for (RuleUseInfo info : ruis) {
//			combined = info.combine(addedConstant);
//			if (combined != null)
//				applyRuleOnRui(combined, contextID);
//		}
//	}
//
//	@Override
//	public RuisHandler createRuisHandler(String context) {
//		Context contxt = (Context) Controller.getContextByName(context);
//		PTree tree = new PTree(context);
//		NodeSet ants = antNodesWithoutVars;
//		ants.addAll(antNodesWithVars);
//		tree.buildTree(ants);
//		return this.addContextRUIS(contxt, tree);
//	}
	@Override
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("&ant");//ants for & TODO name convention
	}

	@Override
	public void clear(){
		super.clear();
		consequents.clear();
	}


	public NodeSet getConsequents() {
		return consequents;
	}
	public void setConsequents(NodeSet consequents) {
		this.consequents = consequents;
	}
	@Override
	protected void sendRui(RuleUseInfo tRui, String contextID) {
		// TODO Auto-generated method stub
		
	}

}
