package sneps.snip.matching;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.*;

//switchsubs is the list of substitutions given of source node
//filtersubs is the list of substitutions returned of target node


public class Matcher {
	
	private static boolean UVBR = true;
	
	/*public static LinkedList<Match>helperMatch(Node sourceNode, Substitutions s ) throws Exception {
		if (!(sourceNode.getSyntacticSuperClass().equals("Molecular"))){
			return null; }
		Molecular sourceNodes= (Molecular)sourceNode;
		LinkedList<Object[]> matched = new LinkedList<Object[]>();
		CaseFrame sourceCF = sourceNode.getDownCableSet().getCaseFrame();
		return matched;
		
	}*/
	
	/*public static LinkedList<Match> match (Node sourceNode){
		if (!(sourceNode.getTerm()instanceof Molecular))
			return null; 
		Molecular sourceNodem=(Molecular)sourceNode.getTerm();
		LinkedList<Match>matches = new LinkedList<Match>();
		CaseFrame sourceCF = sourceNodem.getDownCableSet().getCaseFrame();
		NodeSet candidateNodes = Network.getMolecularNodes().get(sourceCF.getId());
		for (int i = 0; i < candidateNodes.size(); i++) {
			Molecular candidateNode = (Molecular) candidateNodes.getNode(i);
			if (sourceNode.equals(candidateNode))
				continue;
		return null;
		
	}*/

			
		
	
	
	private static boolean helperCompatible (Node sourceNode, Node targetNode){
		
		String sourceType = sourceNode.getSyntacticSuperClass();

		String targetType = targetNode.getSyntacticSuperClass();
		
        if (sourceType.equals("Variable") ||targetType.equals("Variable"))
			
			if(sourceNode.getSemanticType().equals(targetNode.getSemanticType()))//TODO wa7da super class men tanya
				return true;
        				
		if(sourceType.equals("Base")&& targetType.equals("Base"))
			
			if(sourceNode.getSemanticType().equals(targetNode.getSemanticType()))
				return true;
		
		if(sourceType.equals("Molecular")&&targetType.equals("Molecular"))
			
			if(sourceNode.getSemanticType().equals(targetNode.getSemanticType()))
				return true;
			else
				return (sourceNode.getIdentifier().equals(targetNode
						.getIdentifier()));
		   			  
		return false;

	    }
		
	public static boolean VARHERE (Node sourceNode,Node targetNode,LinkedList<Substitutions>switchSubs,
			LinkedList<Substitutions> filterSubs ,boolean rightOrder) throws  Exception {
		//TODO
		    boolean unifiable = false;
		    int switchsubs=switchSubs.size();
		    int filtersubs=filterSubs.size();
		    LinkedList<Substitutions> variableList;
		    LinkedList<Substitutions> bindingList;
		    VariableNode variableNode;
		    Node bindingNode;
		    
		    if (rightOrder) {
				variableList = switchSubs;
				bindingList = filterSubs;
				variableNode = (VariableNode) sourceNode;
				bindingNode = targetNode;

			} else {
				
				variableList = filterSubs;
				bindingList = switchSubs;
				variableNode = (VariableNode) targetNode;
				bindingNode = sourceNode;
						}
		    for (int i = 0; i < variableList.size(); i++) {

				Substitutions currentVSub = variableList.removeFirst();

				Substitutions currentBSub = bindingList.removeFirst();

				if (!currentVSub.isBound(variableNode)) {

					if (bindingNode.getSyntacticType().equals("Variable") && UVBR)

						if (currentBSub.isBound((VariableNode) bindingNode)

								|| currentVSub.isValue(bindingNode))

							continue;

			        currentVSub.putIn(new Binding(variableNode, bindingNode));

					variableList.add(currentVSub);

					bindingList.add(currentBSub);

					unifiable = true;

				} else {

					if (currentVSub.getBindingByVariable(variableNode).getNode()

							.equals(bindingNode)) {

						variableList.add(currentVSub);

						bindingList.add(currentBSub);

						unifiable = true;
					}
				}
			}
		    return unifiable;
		}
	
	
	
	public static boolean HERE (Node sourceNode,Node targetNode,LinkedList<Substitutions>switchSubs,
			LinkedList<Substitutions> filterSubs,boolean rightOrder) throws  Exception {
		String sNode=sourceNode.getSyntacticType();
		String tNode=targetNode.getSyntacticType();
		if(sourceNode.equals(targetNode)&&(sNode=="Base"||sNode=="closed"))
			return true;
		if (!helperCompatible(sourceNode, targetNode))
                return false;
		if(sNode.equals("Base")&&tNode.equals("Base")||sNode.equals("Closed")&&tNode.equals("Closed"))
			return false;
		if (sNode.equals("Variable")) {

			return(VARHERE(sourceNode, targetNode, switchSubs, filterSubs,
					rightOrder));
			
		}
		
		
		else if (tNode.equals("Variable")) {

			return VARHERE(targetNode, sourceNode, filterSubs, switchSubs,
					!rightOrder);
			
			}
						
		else if(sourceNode.getTerm()instanceof Molecular||targetNode.getTerm()instanceof Molecular){
			if(rightOrder)
				return patHere(sourceNode,targetNode,switchSubs,filterSubs,rightOrder);
			
			else
			  return patHere(targetNode,sourceNode,filterSubs,switchSubs,!rightOrder);
			}
		return false;
		
	}
	/*public static Node vere(VariableNode n,Substitutions switchSubs,
			Substitutions filterSubs,Substitutions switchsubsSize,Substitutions filtersubsSize){
		
		return UVBR ? vereUVBR(n, switchSubs, filterSubs, switchsubsSize,filtersubsSize ):
			vereNUVBR(n, switchSubs, filterSubs, switchsubsSize,filtersubsSize );
		return n;
		}*/

	
	/*private static Molecular termVere(Molecular sourceNode,
			Substitutions switchSubs, Substitutions filterSubs,
			Substitutions switchsubsSize, Substitutions filtersubsSize){
		return UVBR ? termVereUVBR(sourceNode, switchSubs, filterSubs, switchsubsSize, filtersubsSize)
				: termVERENUVBR(sourceNode, switchSubs, filterSubs, switchsubsSize, filtersubsSize);
			}*/
	
	
	
	
	public static boolean patHere(Node sourceNode,Node targetNode,
			LinkedList<Substitutions>switchSubs,LinkedList<Substitutions>filterSubs,boolean rightOrder){
		
		boolean flag=false;
		if(sourceNode.getTerm()instanceof Molecular||targetNode.getTerm()instanceof Molecular){
				Molecular newNodes=(Molecular)sourceNode.getTerm();
			    CaseFrame sourceCF=newNodes.getDownCableSet().getCaseFrame();
			    Molecular newNodet=(Molecular)sourceNode.getTerm();
			    CaseFrame targetCF=newNodet.getDownCableSet().getCaseFrame();
			    
			     			     				 }
		return true;
	   }
	
	
	
	
	public static boolean UVBR() {
		return UVBR;
	}

	public static void setUVBR(boolean value) {
		UVBR = value;
	}

	private static boolean setUnify(NodeSet ns1, NodeSet ns2,LinkedList<Substitutions> switchSubs,
		int switchsubsSize	, LinkedList<Substitutions> filterSubs,int filtersubsSize,boolean rightOrder) throws Exception {
		
		if (ns1.size() == 0 || ns2.size() == 0)
			return true;
		boolean unifiable=false;
		int switchSubss = switchSubs.size();
		
		for (int i = 0; i < switchSubss; i++) {
			Substitutions cSSub = switchSubs.removeFirst();
			Substitutions cTSub = filterSubs.removeFirst();
			Substitutions currentSourceSub = cSSub
					.union(new LinearSubstitutions());
			Substitutions currentTargetSub = cTSub
					.union(new LinearSubstitutions());
			 for (int j = 0; j < ns1.size(); j++) {
				 Node n1 = ns1.getNode(j);
				 NodeSet others1 = new NodeSet();
				 others1.addAll(ns1);
				 others1.removeNode(n1);
				 
				 for (int j2 = 0; j2 < ns2.size(); j2++) {
						Node n2 = ns2.getNode(j2);
						NodeSet others2 = new NodeSet();
						others2.addAll(ns2);
						others2.removeNode(n2);
						LinkedList<Substitutions> newSList = new LinkedList<Substitutions>();
						LinkedList<Substitutions> newTList = new LinkedList<Substitutions>();
						newSList.add(currentSourceSub);
						newTList.add(currentTargetSub);
						System.out.println(currentSourceSub);
						if (UVBR && uvbrConflict(ns1, ns2, n1, n2))
							continue;
						if ((HERE(n1, n2, newSList, newTList, rightOrder))
								&& (setUnify(others1, others2, newSList, switchsubsSize,newTList,
										filtersubsSize,rightOrder))) {
							switchSubs.addAll(newSList);
							filterSubs.addAll(newTList);

							unifiable = true;
							}
						else {

							currentSourceSub = cSSub.union(new LinearSubstitutions());

							currentTargetSub = cTSub.union(new LinearSubstitutions());
						}

					}

				}

			}
		return unifiable;
	}
	
	private static boolean uvbrConflict(NodeSet ns1, NodeSet ns2, Node n, Node m) {

		return (n.getSyntacticType().equals("Variable") || m.getSyntacticType()
				.equals("Variable")) && (ns1.contains(m) || ns2.contains(n));
	}

	/*public static Node applySubstitution(Node node, Substitutions sub) {
	Molecular nodem=(Molecular)node.getTerm();
	
	Node boundNode = null;
	Molecular boundNodem=(Molecular)boundNode.getTerm();

		if (node.getSyntacticType().equals("Variable"))

			boundNode = vere((VariableNode) node, sub,

					new LinearSubstitutions(), new LinearSubstitutions(),

					new LinearSubstitutions());

		else if (node.getTerm()instanceof Base)

			boundNodem =nodem;

		else
			boundNodem = termVere(nodem,
					new LinearSubstitutions(), sub, new LinearSubstitutions(),
					new LinearSubstitutions());

		return boundNode;}*/

	}

	   
