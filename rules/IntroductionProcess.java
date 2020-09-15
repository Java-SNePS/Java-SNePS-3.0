package sneps.snip.rules;

import sneps.network.RuleNode;
import sneps.network.VariableNode;

import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;

import sneps.snebr.Context;
import sneps.snip.Report;
import sneps.snip.channels.Channel;
import sneps.snip.channels.ChannelTypes;
import sneps.snip.classes.RuleUseInfo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.PropositionNode;

public class IntroductionProcess extends RuleNode {

	
	 int contextId;
     public RuleNode n1 = new RuleNode() {
		
		@Override
		protected void sendRui(RuleUseInfo tRui, String contextID) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public NodeSet getDownAntNodeSet() {
			// TODO Auto-generated method stub
			return null;
		}
	};
     Report re ;
     static Channel ch = new Channel() {};
     Context cx = new Context(0);
     
     
     AndEntailment ae = new AndEntailment(term);
//     OrNode on = new OrNode(term);
     
     
     static String type;// for testing purpose
     
     public boolean CanBeIntroduced(PropositionNode node, Channel channel) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
 		
//// 		String currentContextName = channel.getContextName();
// 		
//// 		if(channel.getReporter().equals(node)) { // if already working on this channel
//// 			return false;
//// 		}
 		if(!(node instanceof RuleNode)) { // if a node is not a Rule node
 			return false;
 		}
// 		if(cx.isAsserted(node)) { //rule is already asserted in the context
// 			return false;
// 		}

// 			
// 		}
 		System.out.println("Rule Can Be Introduced");
 		return true;
 		
 		
 	}
	
	public void BeginIntroduction(Channel channel) throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
	
//		int ContextID = 0;
		
		 
		if (CanBeIntroduced((PropositionNode)n1, channel)==true) {
			
			if(n1 instanceof RuleNode ) {
//				System.out.println("Can BeIntroduced");
				
//				if(n1 instanceof AndEntailment) {
//					
//					System.out.println("This is AndEntailment Node");
//					ae.applyRuleHandler(re, n1);
//				}
//				else if(n1 instanceof OrNode) {
//					System.out.println("This is OrEntailment Node");
//					applyRuleHandler(re, n1);
//				}
//				else if(n1 instanceof AndOrNode ) {
//					System.out.println("This is AndOr Node");
//					applyRuleHandler(re, n1);
//				}
//				else if(n1 instanceof NumericalEntailment ) {
//					System.out.println("This is NumericalEntailment Node");
//					applyRuleHandler(re, n1);
//					
//				}
//				else if(n1 instanceof ThreshNode ) {
//					System.out.println("This is NumericalEntailment Node");
//					applyRuleHandler(re, n1);
//				}
//				else 
//					System.out.println("Not a rule node");
				
				// for testing
				if(type.equals("and")) {
					System.out.println("This is AndEntailment Node");
					ae.applyAndRuleHandler(n1, contextId);
				}
				
				if(type.equals("or")) {
					System.out.println("This is an OrEntailment Node");
					ae.applyAndRuleHandler(n1, contextId);
				}
			}
			

			
	}
		else
		 System.out.println("Rule Can't be introduced");
    }

	public Context getTempContext(ArrayList<Node> nodes, int ContextID) throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
		Context tempContext =  new Context(ContextID);
//		String currentContextName = ch.getContextName();
		
		
		for(Node node : nodes) {
//			if(!(((RuleNode) node).assertedInContext(currentContextName))) {
//				// tempContext.add(node);
//			}
			if(!this.cx.isAsserted((PropositionNode)node)) {
				cx.getHypothesisSet().add(node.getId());
				tempContext.getHypothesisSet().add(node.getId());
			}
		}
		return tempContext;
	}
	
	

	
	
	@Override
	protected void sendRui(RuleUseInfo tRui, String contextID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NodeSet getDownAntNodeSet() {
		// TODO Auto-generated method stub
		return null;
	}
	 
	public static void main(String[] args) throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
		//Node M1 : PhD(x) ∧ Researcher (x) → Prof(x)
		String testRule = "PhD(Jack) & Researcher(Jack) -> Prof(Jack)";
		String ant = testRule.split("->")[0].trim();
		String cns = testRule.split("->")[1].trim();
		
		
		
		IntroductionProcess ii = new IntroductionProcess();
		ii.n1 = new RuleNode() {
			
			@Override
			protected void sendRui(RuleUseInfo tRui, String contextID) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public NodeSet getDownAntNodeSet() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		if(ant.indexOf('&') != -1) { // if the index of (&) is in the rule
			String node1 = ant.split("&")[0].trim();
			String node2 = ant.split("&")[1].trim();
			
			String class1 = node1.split("\\(")[0].trim();
			String member1 = node1.split("\\(")[1].split("\\)")[0].trim();
			

			String class2 = node2.split("\\(")[0].trim();
			String member2 = node2.split("\\(")[1].split("\\)")[0].trim();
			System.out.println("Member " + member2 + " is in class " + class2);
			ii.type = "and";
			
			String class3 = cns.split("\\(")[0].trim();
			String member3 = cns.split("\\(")[1].split("\\)")[0].trim();
			System.out.println("Member " + member3 + " is in class " + class3);
	
		} else if(ant.indexOf('|') != -1) {
			String n1 = ant.split("|")[0].trim();
			String n2 = ant.split("|")[1].trim();
			
			
//			
//			String class1 = n1.split("\\(")[0].trim();
//			String member1 = n1.split("\\(")[1].split("\\)")[0].trim();
//			
//
//			String class2 = n2.split("\\(")[0].trim();
//			String member2 = n2.split("\\(")[1].split("\\)")[0].trim();
//			System.out.println("Member " + member2 + " is in class " + class2);
			ii.type = "or";
			
			
		} else {
			
		}
		
		ii.BeginIntroduction(ch);
		System.out.println("The antecedents of the rule are"+ "  " +ant);
		System.out.println("The concequents of the rule are"+ "  " +cns);
		System.out.println("Rule is introduced");	
	}
       
    
}
