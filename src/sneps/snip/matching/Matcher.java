package sneps.snip.matching;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Stack;

import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.cables.Cable;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.term.Molecular;
import sneps.network.paths.Path;
import sneps.snebr.Controller;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.PathTrace;
import sneps.network.classes.RCFP;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.*;



public class Matcher {
	
	
	private static boolean UVBR = true;
	
	public static LinkedList<Match> matched (Node sourceNode) throws Exception{
		
		if(!(sourceNode.getTerm() instanceof Molecular)) 
              return null; 
              
		Molecular sourceNodet = (Molecular) sourceNode.getTerm();
		LinkedList<Match> matches = new LinkedList<Match>();
		CaseFrame sourceCF = sourceNodet.getDownCableSet().getCaseFrame();
		NodeSet targetNodes = Network.getMolecularNodes().get(sourceCF.getId());
		
		for (int i = 0; i < targetNodes.size(); i++) {
			Node targetNodee=null;
			Molecular targetNode = (Molecular) targetNodes.getNode(i).getTerm();
			if (sourceNode.equals(targetNode))
				continue;
			LinkedList<Substitutions> switchSubs = new LinkedList<Substitutions>();
            int switchsubSize=0;
			LinkedList<Substitutions> filterSubs = new LinkedList<Substitutions>();
			int filtersubSize=0;
			switchSubs.add(new LinearSubstitutions());

			filterSubs.add(new LinearSubstitutions());
			NodeSet sourceNodeVariables = getTerms(sourceNodet, true);
			if (HERE(targetNodee, sourceNode, filterSubs,filtersubSize, switchSubs,switchsubSize ,true)) {

				 possibleMatches: for (int j = 0; j < switchSubs.size(); j++) {

					Substitutions sourceR = switchSubs.get(j);

					Substitutions sourceS = new LinearSubstitutions();

					Substitutions targetR = filterSubs.get(j);

					Substitutions targetS = new LinearSubstitutions();


					Substitutions sourceBindings = new LinearSubstitutions();

					Substitutions targetBindings = new LinearSubstitutions();

					NodeSet targetNodeVariables = getTerms(targetNode,true);

					for (int k = 0; k < sourceNodeVariables.size(); k++) {

						Node sbinding = vere((VariableNode) sourceNodeVariables.getNode(k),
								sourceR, targetR, sourceS, targetS);

						if (sbinding == null)

							continue possibleMatches;

						else

							sourceBindings.insert(new Binding((VariableNode) sourceNodeVariables
											.getNode(k), sbinding));

					}

					for (int k = 0; k < targetNodeVariables.size(); k++) {

						Node cbinding = vere(
								(VariableNode) targetNodeVariables
										.getNode(k),
								targetR, sourceR, targetS, sourceS);

						if (cbinding == null)

							continue possibleMatches;

						else

							targetBindings.insert(new Binding((VariableNode) targetNodeVariables
											.getNode(k), cbinding));

					}
                     
					
					//Match match = new Match { filterSubs,
						//	switchSubs, targetNodee,matchType};
					//matches.add(match);
				}

			}

		}

		return matches;
			
	}

			
		
	
	
	private static boolean helperCompatible (Node targetNode, Node sourceNode){
		
		String sourceType = sourceNode.getSyntacticSuperClass();

		String targetType = targetNode.getSyntacticSuperClass();
		
        if (sourceType.equals("Variable") ||targetType.equals("Variable"))
			
			if(sourceNode.getSemanticType().equals(targetNode.getSemanticType()))//TODO wa7da super class men tanya Nawar
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
		
	public static boolean VARHERE (Node targetNode,Node sourceNode,LinkedList<Substitutions>filterSubs,
			LinkedList<Substitutions> switchSubs ,boolean rightOrder) throws  Exception {
		
		    boolean unifiable = false;
		    int switchSub=switchSubs.size();
		    int filterSub=filterSubs.size();
		    LinkedList<Substitutions> variableList;
		    LinkedList<Substitutions> bindingList;
		    VariableNode variableNode;
		    Node bindingNode;
		    
		    if (rightOrder) {
				variableList = filterSubs;
				bindingList = switchSubs;
				variableNode = (VariableNode) targetNode;
				bindingNode = sourceNode;

			} else {
				
				variableList = switchSubs;
				bindingList = filterSubs;
				variableNode = (VariableNode) sourceNode;
				bindingNode = targetNode;
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
	
	
	
	public static boolean HERE (Node targetNode,Node sourceNode,LinkedList<Substitutions>filterSubs,
			int filtersubSize,LinkedList<Substitutions> switchSubs,int switchsubSize,boolean rightOrder) throws  Exception {
		
		String sNode=sourceNode.getSyntacticType();
		String tNode=targetNode.getSyntacticType();
		
		
		if(sourceNode.equals(targetNode)&&(sNode=="Base"||sNode=="closed"))
			return true;
		if (!helperCompatible(targetNode, sourceNode))
                return false;
		if(sNode.equals("Base")&&tNode.equals("closed")||sNode.equals("closed")&&tNode.equals("Base"))
			return false;
		
		if (tNode.equals("Variable")) {

			return(VARHERE(targetNode, sourceNode, filterSubs, switchSubs,
					rightOrder));
			
		}
		
		
		else if (sNode.equals("Variable")) {

			return VARHERE(sourceNode, targetNode, switchSubs, filterSubs,
					!rightOrder);
			
			}
						
		else if(sourceNode.getTerm()instanceof Molecular||targetNode.getTerm()instanceof Molecular){
			if(rightOrder)
				return patHere(targetNode,sourceNode,filterSubs,filtersubSize,switchSubs,switchsubSize,rightOrder);
			
			else
			  return patHere(sourceNode,targetNode,switchSubs,switchsubSize,filterSubs,filtersubSize,!rightOrder);
			}
		
		return false;
		
	}
	public static Node vere(VariableNode n,Substitutions filterSubs,
			Substitutions switchSubs,Substitutions filterSubss,Substitutions switchSubss){
		
		return UVBR ? vereUVBR(n, filterSubs, switchSubs, filterSubss,switchSubss ):
			vereNUVBR(n, filterSubs, filterSubss, switchSubs,switchSubss );
		
		 }
	
	public static Node vereUVBR(VariableNode n, Substitutions filterSubs,
			Substitutions switchSubs, Substitutions filterSubss, Substitutions switchSubss) {

		Node bindingNode=null; 
		Molecular w=(Molecular)bindingNode.getTerm();
		 
		if (filterSubs.isBound(n)){
			Node RbindingNode = filterSubs.term(n);
			 Molecular z=(Molecular)RbindingNode.getTerm();
			if (RbindingNode.getSyntacticType().equals("Base")
					|| RbindingNode.getSyntacticType().equals("Closed")) {

				bindingNode = RbindingNode;
			

				filterSubss.putIn(new Binding(n, RbindingNode));

				filterSubs.update(filterSubs.getBindingByVariable(n), n); }
			
			else if (RbindingNode.getSyntacticType().equals("Pattern")) {
				
				     filterSubs.update(filterSubs.getBindingByVariable(n), n);
				     filterSubss.putIn(new Binding(n, n));
						w = termVere(z, filterSubs,
								switchSubs, filterSubss, switchSubss);
						if (w == null)
							return null;
						filterSubss.update(filterSubss.getBindingByVariable(n),RbindingNode); }
			else if (filterSubss.isBound(n)|| switchSubss.isBound((VariableNode) RbindingNode)) {
				if (switchSubss.term((VariableNode) RbindingNode).equals(RbindingNode))

					return null;

				else

					bindingNode = filterSubss.term((VariableNode) n); }
			bindingNode = filterSubs.term(n);
			}
		else {

			filterSubs.insert(new Binding(n, n));
			bindingNode = n;
			filterSubss.insert(new Binding(n, bindingNode));

		}
		return bindingNode;
		 }
	
	public static Node vereNUVBR(VariableNode n, Substitutions filterSubs,
			Substitutions filterSubss, Substitutions switchSubs, Substitutions switchSubss) {
		
		Stack<VariableNode> path = source(n, filterSubs, switchSubs);
		VariableNode v = path.pop();
		Node bindingNode = null;
		 Molecular b=(Molecular)bindingNode.getTerm();
		
		if (!filterSubs.isBound(v)) {
			bindingNode = v;
			filterSubs.update(filterSubs.getBindingByVariable(v), v); }
		else if (filterSubs.value(v).getSyntacticType().equals("Base")

				|| filterSubs.value(v).getSyntacticType().equals("Closed")) {

			bindingNode = filterSubs.value(v);

			
			filterSubs.update(filterSubs.getBindingByVariable(v), v);

			if (filterSubss.isBound(v))

				filterSubss.update(filterSubss.getBindingByVariable(v), bindingNode);

			else

				filterSubss.insert(new Binding(v, bindingNode));

		} else if (filterSubs.value(v).getSyntacticSuperClass().equals("Pattern")) {

			
			filterSubs.update(filterSubs.getBindingByVariable(v), v);
			
			 Molecular k=(Molecular)bindingNode.getTerm();
			
			// TODO:stack loop start on x

			while (!path.isEmpty()) {

				VariableNode x = path.pop();

				if (filterSubs.isBound(x))

					filterSubss.insertOrUpdate(new Binding(x, x));

			}

			
			b = termVERENUVBR(k, filterSubs, switchSubs, filterSubss, switchSubss);

			if (filterSubss.isBound(v))

				filterSubss.update(filterSubss.getBindingByVariable(v), bindingNode);

			else

				filterSubss.insert(new Binding(v, bindingNode));



		} else if (!filterSubss.isBound(v)) {

			bindingNode = v;



		} else if (filterSubss.value(v).equals(v)) {

			return null;

		} else {

			bindingNode = filterSubss.value(v);

		}
		while (!path.isEmpty()) {

			VariableNode x = path.pop();

			if (filterSubss.isBound(x))

				filterSubss.update(filterSubss.getBindingByVariable(x), bindingNode);

			else

				filterSubss.insert(new Binding(x, bindingNode));

		}
			return bindingNode;

	}
		

	
	private static Molecular termVere(Molecular sourceNode,
			Substitutions filterSubs, Substitutions switchSubs,
			Substitutions filterSubss, Substitutions switchSubss){
		return UVBR ? termVereUVBR(sourceNode, filterSubs, switchSubs, filterSubss, switchSubss)
				: termVERENUVBR(sourceNode, filterSubs, switchSubs, filterSubss, switchSubss);
			}
	
	private static Molecular termVereUVBR(Molecular sourceNode,
			Substitutions filterSubs, Substitutions switchSubs,Substitutions filterSubss, Substitutions switchSubss) {

		Molecular targetNode = null;
		DownCableSet newDCS = null;
		DownCableSet oldDCS = sourceNode.getDownCableSet();
		Enumeration<DownCable> oldDCs = oldDCS.getDownCables().elements();
		LinkedList<DownCable> newDClist = new LinkedList<DownCable>();

		while (oldDCs.hasMoreElements()) {

			DownCable currentOldCable = oldDCs.nextElement();

			NodeSet currentOldNS = currentOldCable.getNodeSet();

			NodeSet currentNewNS = new NodeSet();

			for (int i = 0; i < currentOldNS.size(); i++) {

				Node currentSourceNode = currentOldNS.getNode(i);
				
				 Molecular w=(Molecular)currentSourceNode.getTerm();

				Node currentTargetNode = null;
				
				 Molecular n=(Molecular)currentTargetNode.getTerm();



				if (currentSourceNode.getSyntacticType().equals("Variable")) {

					if (switchSubs.isBound((VariableNode) currentSourceNode)) {

						if (switchSubs.term((VariableNode) currentSourceNode) == currentSourceNode) {

							if (switchSubss.term((VariableNode) currentSourceNode) == currentSourceNode)

								return null;

							else

								currentTargetNode = switchSubss.term((VariableNode) currentSourceNode);

						} else

							currentTargetNode = vere((VariableNode) currentSourceNode, switchSubs,filterSubs, switchSubss, filterSubss);

						if (currentTargetNode == null)

							return null;

					}



					else

						currentTargetNode = currentSourceNode;



				} else if (currentSourceNode.getSyntacticType().equals("Pattern"))

					n = termVere(w,switchSubs, filterSubs, switchSubss, filterSubss);



				else

					currentTargetNode = currentSourceNode;



				currentNewNS.addNode(currentTargetNode);

			}

			newDClist.add(new DownCable(currentOldCable.getRelation(),currentNewNS));

		}

		newDCS = new DownCableSet(newDClist, oldDCS.getCaseFrame());

		try {

			targetNode = new Molecular(sourceNode.getIdentifier(), newDCS);

		} catch (Exception e) {



			e.printStackTrace();

		}

		return targetNode;

	}
	
	
	public static Molecular termVERENUVBR(Molecular n,Substitutions filterSubs, Substitutions switchSubs,
			Substitutions filterSubss, Substitutions switchSubss) {

		DownCableSet dcset = n.getDownCableSet();

		Hashtable<String, DownCable> dcs = dcset.getDownCables();

		String[] dckeys = dcs.keySet().toArray(new String[dcs.size()]);



		for (String dckey : dckeys) {

			DownCable dc = dcs.get(dckey);

			Relation relation = dc.getRelation();

			NodeSet ns = dc.getNodeSet();



			for (int i = 0; i < ns.size(); i++) {

				Node currentNode = ns.getNode(i);
				 Molecular w=(Molecular)currentNode.getTerm();



				if (currentNode.getSyntacticType().equals("Variable")) {

					if (filterSubs.value((VariableNode) currentNode).equals(

							currentNode)) {

						if (filterSubss.value((VariableNode) currentNode).equals(

								currentNode)) {

							return null;

						} else {

							// replace by s(currentNode)

						}

					} else if (filterSubs.isBound((VariableNode) currentNode)) {

						// replace by VERE(currentNode)
						currentNode=vere((VariableNode) currentNode, switchSubs,filterSubs, switchSubss, filterSubss);

					}

				} else if (currentNode.getSyntacticSuperClass().equals(

						"Molecular")) {

					// replace by termvere(currentNode)
					w=termVere(w,switchSubs, filterSubs, switchSubss, filterSubss);

				}



			}

		}

		return null;

	}
	
	private static boolean checkRuleCompatibility(Node targetNode,

			Node sourceNode) { 
		return true;
	}
	
	
	public static boolean patHere(Node targetNode,Node sourceNode,
			LinkedList<Substitutions>filterSubs,int filtersubSize,LinkedList<Substitutions>switchSubs,int switchsubSize,boolean rightOrder) throws Exception{
		
		Node n=null;
		NodeSet ns=null;
		boolean flag=false;
		
		Molecular t=(Molecular)targetNode.getTerm();
		Molecular s=(Molecular)sourceNode.getTerm();
		
		if(!checkRuleCompatibility(sourceNode,targetNode))
			return false;
		
		if(checkRuleCompatibility(sourceNode,targetNode)){
			 CaseFrame targetCF=t.getDownCableSet().getCaseFrame();
			 CaseFrame sourceCF= s.getDownCableSet().getCaseFrame();
	         Iterator<Relation> targetRelations=targetCF.getRelations().listIterator();

				while(targetRelations.hasNext()){

				  Relation current =targetRelations.next();
				  Path path=current.getPath();
				  if(path==null)
				    continue;
				  else {
					  LinkedList<Object[]> w= path.follow(sourceNode,new PathTrace(),Controller.getCurrentContext());
					  for(Object[]list:w){
						  n=(Node)list[0];
						  LinkedList<Relation> rel=path.firstRelations();
						  ns= new NodeSet();
						   if((rel.size()==1&& rel.get(0).equals(current))||sourceCF.equals(targetCF)){
							   ns.addAll(s.getDownCableSet().getDownCable(current.getName()).getNodeSet());
							    if(!targetCF.equals(sourceCF)){
							    	flag=false;
							    	break;
							    }
							 						   }
						   if(!ns.contains(n)){
							   ns.addNode(n);
							   flag=true;}
						   else {
							   
							
								int lsum = w.size();
								int sum = 0;
								DownCableSet sDownCableSet = null;
								boolean relationAdded = false;
								if(!sourceCF.getRelations().contains(current)) {
									sourceCF.getRelations().add(current);
									relationAdded = true;
								}
								else {
									ns = s.getDownCableSet().getDownCable(current.getName()).getNodeSet();
								}
								
								if(!targetCF.equals(sourceCF)) {
									LinkedList<Relation> rels = new LinkedList<Relation>();
									for(Relation r : sourceCF.getRelations()) {
										if(!targetCF.getRelations().contains(r)) {
											rels.add(r);
										}
									}
									for(Relation r : rels) {
										if(lsum != 0 && r.equals(rels.getFirst())){
											s.getDownCableSet().getDownCable(r.getName()).getNodeSet().removeNode(s.getDownCableSet().getDownCable(r.getName()).getNodeSet().getNode(0));
											lsum--;
											if(s.getDownCableSet().getDownCable(r.getName()).getNodeSet().isEmpty()) {
												LinkedList<DownCable> dc = new LinkedList<>();
												while(s.getDownCableSet().getDownCables().elements().hasMoreElements()) {
													DownCable currentCable = s.getDownCableSet().getDownCables().elements().nextElement();
													if(currentCable.getRelation().equals(r))
														continue;
													else
														dc.add(currentCable);
												}
												sDownCableSet = new DownCableSet(dc, s.getDownCableSet().getCaseFrame());
												LinkedList<Relation> newRels = new LinkedList<Relation>();
												for(Relation tempRel : sourceCF.getRelations()) {
													if(tempRel.equals(r))
														continue;
													else
														newRels.add(tempRel);
												}
												sourceCF = new CaseFrame(sourceCF.getSemanticClass(), newRels);
												
											}}
											else {
												sum += s.getDownCableSet().getDownCable(r.getName()).getNodeSet().size();
											}
										}
									}
									
									if(!ns.contains(n)) {
										ns.addNode(n);
										flag = true;
									}
									if(relationAdded && sourceCF.getRelations().contains(current)) {
										LinkedList<Relation> newRelsForCF = new LinkedList<>();
										for(Relation tempRelation: sourceCF.getRelations()) {
											if(tempRelation.equals(current))
												continue;
											else
												newRelsForCF.add(tempRelation);
										}
										sourceCF = new CaseFrame(sourceCF.getSemanticClass(), newRelsForCF);
									}
							}
						}
						
						if(flag && setUnify(ns, t.getDownCableSet().getDownCable(current.getName()).getNodeSet(), filterSubs,filtersubSize, switchSubs,switchsubSize, rightOrder)){
							continue;
						}
					}
				}
			}
			
			return flag;
		}
						  
	
	
	
	
	public static boolean UVBR() {
		return UVBR;
	}

	public static void setUVBR(boolean value) {
		UVBR = value;
	}

	public static boolean setUnify(NodeSet targetNode, NodeSet sourceNode,LinkedList<Substitutions> filterSubs,
		int filtersubSize	, LinkedList<Substitutions> switchSubs,int switchsubSize,boolean rightOrder) throws Exception {
		
		if (targetNode.size() == 0 || sourceNode.size() == 0)
			return true;
		boolean unifiable=false;
		
		
		for (int i = 0; i < filtersubSize; i++) {
			Substitutions fSub = filterSubs.removeFirst();
			Substitutions sSub = switchSubs.removeFirst();
			Substitutions currentTargetSub = fSub
					.union(new LinearSubstitutions());
			Substitutions currentSourceSub =  sSub
					.union(new LinearSubstitutions());
			 for (int j = 0; j < targetNode.size(); j++) {
				 Node n1 = targetNode.getNode(j);
				 NodeSet others1 = new NodeSet();
				 others1.addAll(targetNode);
				 others1.removeNode(n1);
				 
				 for (int w = 0; w < sourceNode.size(); w++) {
						Node n2 = sourceNode.getNode(w);
						NodeSet others2 = new NodeSet();
						others2.addAll(sourceNode);
						others2.removeNode(n2);
						LinkedList<Substitutions> newTList = new LinkedList<Substitutions>();
						LinkedList<Substitutions> newSList = new LinkedList<Substitutions>();
						newTList.add(currentTargetSub);
						newSList.add(currentSourceSub);
						System.out.println(currentTargetSub);
						if (UVBR && uvbrConflict(targetNode, sourceNode, n1, n2))
							continue;
						if ((HERE(n1, n2, newTList,filtersubSize ,newSList,switchsubSize, rightOrder))
								&& (setUnify(others1, others2, newTList, filtersubSize,newSList,
										switchsubSize,rightOrder))) {
							filterSubs.addAll(newTList);
							switchSubs.addAll(newSList);

							unifiable = true;
							}
						else {

							currentTargetSub = fSub.union(new LinearSubstitutions());

							currentSourceSub = sSub.union(new LinearSubstitutions());
						}

					}

				}

			}
		return unifiable;
	}
	
	public static boolean uvbrConflict(NodeSet targetNode, NodeSet sourceNode, Node n1, Node n2) {

		return (n1.getSyntacticType().equals("Variable") || n2.getSyntacticType()
				.equals("Variable")) && (targetNode.contains(n2) || sourceNode.contains(n1));
	}

	public static Node applySubstitution(Node node, Substitutions sub) {
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

		return boundNode;
		}
	
	protected static boolean sameFunction(Molecular f1, Molecular f2) {

		DownCableSet DCS1 = f1.getDownCableSet();

		DownCableSet DCS2 = f2.getDownCableSet();

		CaseFrame cs1 = DCS1.getCaseFrame();

		CaseFrame cs2 = DCS2.getCaseFrame();
		
		if (!cs1.equals(cs2))

			return false;

		else {



			Enumeration<String> relations = DCS1.getDownCables().keys();

			Hashtable<String, DownCable> DCables1 = DCS1.getDownCables();

			Hashtable<String, DownCable> DCables2 = DCS2.getDownCables();



			while (relations.hasMoreElements()) {

				String relation = relations.nextElement();

				DownCable downcable1 = DCables1.get(relation);

				DownCable downcable2 = DCables2.get(relation);

				NodeSet ns1 = downcable1.getNodeSet().Union(new NodeSet());

				NodeSet ns2 = downcable2.getNodeSet().Union(new NodeSet());
				


				for (int i = 0; i < ns1.size(); i++) {

					Node n1 = ns1.getNode(i);

					boolean molecular = n1.getSyntacticSuperClass().equals(

							"Molecular");

					for (int j = 0; j < ns2.size(); j++) {

						Node n2 = ns2.getNode(j);
						 Molecular k=(Molecular)n1.getTerm();
						 Molecular m=(Molecular)n2.getTerm();
						if (molecular) {

							if (n2.getSyntacticSuperClass().equals("Molecular")

									&& sameFunction(k,m))
							{      ns1.removeNode(n1);
								   ns2.removeNode(n2); }

						} else if (n1.equals(n2)) {

							ns1.removeNode(n1);
							ns2.removeNode(n2);

						}
					}
				}

				if (ns1.size() != 0 || ns2.size() != 0)

					return false;

			}

		}

		return true;

	}
	
	public static Stack<VariableNode> source(VariableNode n,

			Substitutions filterSubs, Substitutions switchSubs) {

		// TODO:UVBR Rloop

		Stack<VariableNode> path = new Stack<VariableNode>();

		Substitutions f = filterSubs;

		VariableNode current = n;

		while (f.value(current).getSyntacticType().equals("Variable")

				&& !f.value(current).equals(n)) {

			path.push(current);

			current = (VariableNode) f.value(current);

			f = f.equals(filterSubs) ? switchSubs : filterSubs;

		}

		return path;

	}
	
	private static LinkedList<Object[]> downCableDifference(Molecular target,Molecular source) {
		LinkedList<Object[]> relationNodePairs = new LinkedList<Object[]>();

		RCFP[] tRelations = new RCFP[target.getDownCableSet().getCaseFrame()

				.getRelations().size()];

		tRelations = target.getDownCableSet().getCaseFrame().getRelations().toArray(tRelations);

		RCFP[] sRelations = new RCFP[source.getDownCableSet().getCaseFrame()
				.getRelations().size()];

		sRelations = source.getDownCableSet().getCaseFrame().getRelations().toArray(sRelations);

		Hashtable<String, DownCable> tDCs = target.getDownCableSet().getDownCables();

		Hashtable<String, DownCable> sDCs = source.getDownCableSet().getDownCables();

		for (int i = 0; i < tRelations.length; i++) {

			boolean found = false;

			for (int j = 0; j < sRelations.length; j++) {

				if (tRelations[i].equals(sRelations[j])) {

					found = true;

					int sns = tDCs.get(tRelations[i]).getNodeSet().size();

					int tns = sDCs.get(sRelations[j]).getNodeSet().size();

					if (sns > tns)

						relationNodePairs.add(new Object[] {

								tRelations[i].getRelation().getName(),

								tns - sns });

					break;

				}

			}

			if (!found)

				relationNodePairs.add(new Object[] {

						tRelations[i].getRelation().getName(),

						tDCs.get(tRelations[i]).getNodeSet().size() });

		}

		return relationNodePairs;
		
	}
	
	private static boolean caseFramesCompatibleThroughAdjustability(
			RelationsRestrictedCaseFrame adjusted, RelationsRestrictedCaseFrame goal) {

		Iterator<Relation> adjustedKeys = adjusted.getRelations().listIterator();

		Hashtable<String, RCFP> adjustedRelations = adjusted.getrelationsWithConstraints();

		Hashtable<String, RCFP> goalRelations = goal.getrelationsWithConstraints();

		while (adjustedKeys.hasNext()) {

			Relation adjustedKey = adjustedKeys.next();

			if (!goalRelations.containsKey(adjustedKey))

				if (adjustedRelations.get(adjustedKey).getAdjust()
						.equals("reduce")) {

					if (adjustedRelations.get(adjustedKey).getLimit() > 0)

						return false;

				} else

					return false;

		}

		return true;

	}
	private static NodeSet getTerms(Molecular term, boolean var) {

		NodeSet ns = new NodeSet();

		Hashtable<String, DownCable> dcs = term.getDownCableSet()
				.getDownCables();

		Enumeration<DownCable> elements = dcs.elements();



		while (elements.hasMoreElements()) {

			NodeSet nodes = elements.nextElement().getNodeSet();


			for (int i = 0; i < nodes.size(); i++) {

				Node n = nodes.getNode(i);


				if (var && n.getSyntacticType().equals("Variable"))

					ns.addNode(n);

				else if (!var)

					ns.addNode(n);

			}


		}

		return ns;

	 }
	
	


	}

	   
