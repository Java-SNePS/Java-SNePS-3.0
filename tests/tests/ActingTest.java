package tests;

import static org.junit.Assert.assertTrue;


import sneps.network.VariableNode;
import sneps.snebr.Context;
import sneps.network.classes.term.Base;
import sneps.network.classes.term.Term;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;

import java.util.ArrayList;

import sneps.network.Node;
import sneps.network.ActionNode;
import sneps.network.PropositionNode;
import sneps.exceptions.CannotBuildNodeException;
import sneps.exceptions.CaseFrameMissMatchException;
import sneps.exceptions.ContextNameDoesntExistException;
import sneps.exceptions.ContradictionFoundException;
import sneps.exceptions.CustomException;
import sneps.exceptions.DuplicateContextNameException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.EquivalentNodeException;
import sneps.exceptions.IllegalIdentifierException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.exceptions.SemanticNotFoundInNetworkException;
import sneps.network.ActNode;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.Semantic;
import sneps.network.classes.Wire;
import sneps.snebr.Controller;
import sneps.snip.Runner;
import sneps.acts.Believe;

public class ActingTest {

	private static final Semantic proposition = new Semantic("Proposition");
	private static final Semantic Action = new Semantic("Action");
	public static ArrayList<PropositionNode> preconditions;
	public static ArrayList<PropositionNode> effects;



	@BeforeClass
	public static void beforeAll() throws DuplicateContextNameException, ContradictionFoundException,
			NodeNotFoundInNetworkException, NotAPropositionNodeException, IllegalIdentifierException, CustomException, DuplicatePropositionException, ContextNameDoesntExistException {
		Controller.clearSNeBR();
		Network.clearNetwork();
		
		
		

	}

	@Before
	public void beforeEach() throws DuplicateContextNameException, ContradictionFoundException,
			NodeNotFoundInNetworkException, NotAPropositionNodeException, IllegalIdentifierException, CustomException, DuplicatePropositionException, ContextNameDoesntExistException {
		Network.defineDefaults();
		CaseFrame.createDefaultCaseFrames();
		RelationsRestrictedCaseFrame.createDefaultCaseFrames();
		Semantic.createDefaultSemantics();
		
		
		
	
		
		
		Runner.initiate();
	}

	@After
	public void afterEach() {
		Controller.clearSNeBR();
		Network.clearNetwork();
		Runner.emptyStacks();
	}

	@AfterClass
	public static void tearDown() {
		Network.clearNetwork();
	}
///*
	@Test
	public void testProposition() throws NotAPropositionNodeException, NodeNotFoundInNetworkException,
			IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException,
			SemanticNotFoundInNetworkException, CustomException, DuplicatePropositionException,
			ContradictionFoundException, ContextNameDoesntExistException {
		PropositionNode prop = (PropositionNode) Network.buildBaseNode("red", proposition);
		ArrayList<Wire> wiresArr = new ArrayList<Wire>();
		Relation r1 = Relation.obj;
		Relation r2 = Relation.action;
		Node actionBelieve1 = Network.buildBaseNode("belaction", Action);
		ActionNode actionBelieve = new Believe("belaction");// problem was with the action node,the creation of term and
															// semantic
		Network.getNodes().put("belaction", actionBelieve);
		Network.getNodesWithIDs().add(actionBelieve.getId(), actionBelieve);
		
		wiresArr.add(new Wire(r1, prop));
		wiresArr.add(new Wire(r2, actionBelieve));
		
		ActNode believe = (ActNode) Network.buildMolecularNode(wiresArr, CaseFrame.act);
		Runner.initiate();
		believe.restartAgenda();
		Runner.addToActStack(believe);
		Runner.run(); //
		Boolean isAsserted = Controller.getCurrentContext().isAsserted(prop);
		assertTrue(isAsserted);
	}

	@Test
	public void testMolPropisition()
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException {
		PropositionNode prop1 = (PropositionNode) Network.buildBaseNode("red", proposition);
		PropositionNode prop2 = (PropositionNode) Network.buildBaseNode("favouriteColor", proposition);
		Relation r1 = Relation.obj;
		ArrayList<Wire> proparr = new ArrayList<Wire>();
		proparr.add(new Wire(r1, prop1));
		proparr.add(new Wire(r1, prop2));
		// PropositionNode prop = (PropositionNode);
		// Network.buildMolecularNode(wires, caseFrame);
	}

	@Test  
	public void
	   testWithPreconditions() throws NotAPropositionNodeException,
	   NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException,
	   CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
		
	   PropositionNode propos = (PropositionNode) Network.buildBaseNode("yellow",proposition);   
	   PropositionNode precondition = (PropositionNode)Network.buildBaseNode("blue", proposition);
	   PropositionNode precondition2 = (PropositionNode)Network.buildBaseNode("pink", proposition);
	   PropositionNode effect = (PropositionNode)Network.buildBaseNode("mo7m7y", proposition);
	   PropositionNode effect2 = (PropositionNode)Network.buildBaseNode("bo2b2y mz3tr", proposition);
	   
	   
	   ActionNode actionBelieve=new Believe("belaction");
	   
	   Network.getNodes().put("belaction",actionBelieve);  
	   Network.getNodesWithIDs().add(actionBelieve.getId(),actionBelieve);    
	   
	   ArrayList<Wire> wiresArr = new ArrayList<Wire>();
	   
	   Relation r1 =Relation.obj;   
	   Relation r2 = Relation.action; 
	   Relation r3 = Relation.precondition;
	   Relation r4 = Relation.act;
	   Relation r5 = Relation.effect;
	   
	   
	   wiresArr.add(new Wire(r1, propos));   
	   wiresArr.add(new Wire(r2,actionBelieve));
	   
	   
	   ActNode believe = (ActNode) Network.buildMolecularNode(wiresArr,CaseFrame.act);   
	   
	   
	   ArrayList<Wire> preActArr=new ArrayList<Wire>();   
	   preActArr.add(new Wire(r3,precondition));  
	   preActArr.add(new Wire(r4,believe));   
	   
	   
	   ArrayList<Wire> preActArr2=new ArrayList<Wire>();   
	   preActArr2.add(new Wire(r3,precondition2));  
	   preActArr2.add(new Wire(r4,believe)); 
	   
	   
	   ArrayList<Wire> actEffectArr=new ArrayList<Wire>();   
	   actEffectArr.add(new Wire(r5,effect));  
	   actEffectArr.add(new Wire(r4,believe));   
	   
	 
	   ArrayList<Wire> actEffectArr2=new ArrayList<Wire>();   
	   actEffectArr2.add(new Wire(r5,effect2));  
	   actEffectArr2.add(new Wire(r4,believe)); 
	   
	   
	  // PropositionNode preAct2=(PropositionNode)Network.buildMolecularNode(preActArr2,CaseFrame.preconditionAct);
	   PropositionNode preAct=(PropositionNode)Network.buildMolecularNode(preActArr,CaseFrame.preconditionAct);         
	   PropositionNode actEffect=(PropositionNode)Network.buildMolecularNode(actEffectArr,CaseFrame.actEffect);
	   PropositionNode actEffect2=(PropositionNode)Network.buildMolecularNode(actEffectArr2,CaseFrame.actEffect);
	   
	   
	   
	   ActionNode actionBelpre=new Believe("actionbelpre"); 
	   
	   Network.getNodes().put("belaction", actionBelpre);  
	   Network.getNodesWithIDs().add(actionBelpre.getId(),actionBelpre);    
	   
	   ArrayList<Wire> wirePreBel=new ArrayList<Wire>();   
	   wirePreBel.add(new Wire(r2,actionBelpre));   
	   wirePreBel.add(new Wire(r1,precondition));
	   
	   ActNode belPre = (ActNode) Network.buildMolecularNode(wirePreBel,CaseFrame.act);      
	   believe.restartAgenda();  
	   belPre.restartAgenda();    
	   //believe.getPreconditions().add(precondition);   
	   
	   Runner.addToActStack(believe);  
	   Runner.addToActStack(belPre);  
	   Runner.run();
	   
	     Boolean isAsserted1=Controller.getCurrentContext().isAsserted(precondition);
	     Boolean isAsserted2=Controller.getCurrentContext().isAsserted(propos);
	     Boolean isAsserted3=Controller.getCurrentContext().isAsserted(effect);
	     Boolean isAsserted4=Controller.getCurrentContext().isAsserted(precondition2);
	     Boolean isAsserted5=Controller.getCurrentContext().isAsserted(effect2);
	     Boolean allAsserted=false;
	     
	     if(isAsserted1 && isAsserted2 && isAsserted3 && isAsserted5)
	    	 allAsserted=true;
	     
	     assertTrue(isAsserted5);   
	     
	}

	@Test
	public void testEffects() throws NotAPropositionNodeException, NodeNotFoundInNetworkException,
			IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException,
			SemanticNotFoundInNetworkException {
		PropositionNode propos = (PropositionNode) Network.buildBaseNode("yellow", proposition);
		PropositionNode effect = (PropositionNode) Network.buildBaseNode("blue", proposition);
		
		
		ActionNode actionBelieve = new Believe("belaction");
		
		Network.getNodes().put("belaction", actionBelieve);
		Network.getNodesWithIDs().add(actionBelieve.getId(), actionBelieve);
		
		ArrayList<Wire> wiresArr = new ArrayList<Wire>();
		
		Relation r1 = Relation.obj;
		Relation r2 = Relation.action;
		
		
		wiresArr.add(new Wire(r1, propos));
		wiresArr.add(new Wire(r2, actionBelieve));
		
		ActNode believe = (ActNode) Network.buildMolecularNode(wiresArr, CaseFrame.act);

		ActionNode actionBeleffect = new Believe("actionbelpre");
		
		Network.getNodes().put("belaction", actionBeleffect);
		Network.getNodesWithIDs().add(actionBeleffect.getId(), actionBeleffect);

		ArrayList<Wire> wirePreBel = new ArrayList<Wire>();
		wirePreBel.add(new Wire(r2, actionBeleffect));
		wirePreBel.add(new Wire(r1, effect));

		ActNode belPre = (ActNode) Network.buildMolecularNode(wirePreBel, CaseFrame.act);

		// believe.getEffects().add(effect);

		Runner.addToActStack(believe);
		Runner.addToActStack(belPre);
		Runner.run();

		// Boolean isAsserted=Controller.getContextByName("Test
		// context").isAsserted(effect);
		Boolean isAsserted = Controller.getCurrentContext().isAsserted(effect);

		assertTrue(isAsserted);

	}


	

	@Test
	public void testDoAll() throws NotAPropositionNodeException, NodeNotFoundInNetworkException,
			IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException,
			SemanticNotFoundInNetworkException {
		Relation r1 = Relation.obj;
		Relation r2 = Relation.action;
		Relation r3 = Relation.doo;

		PropositionNode propos1 = (PropositionNode) Network.buildBaseNode("yellow", proposition);
		PropositionNode propos2 = (PropositionNode) Network.buildBaseNode("blue", proposition);
		PropositionNode propos3 = (PropositionNode) Network.buildBaseNode("red", proposition);

		ActionNode belAction1 = (ActionNode) Network.buildActionNode("belAction1", "believe");
		ActionNode belAction2 = (ActionNode) Network.buildActionNode("belAction3", "believe");
		ActionNode belAction3 = (ActionNode) Network.buildActionNode("belAction3", "believe");

		ArrayList<Wire> bel1arr = new ArrayList<Wire>();
		ArrayList<Wire> bel2arr = new ArrayList<Wire>();
		ArrayList<Wire> bel3arr = new ArrayList<Wire>();

		bel1arr.add(new Wire(r1, propos1));
		bel1arr.add(new Wire(r2, belAction1));

		bel2arr.add(new Wire(r1, propos2));
		bel2arr.add(new Wire(r2, belAction2));

		bel3arr.add(new Wire(r1, propos3));
		bel3arr.add(new Wire(r2, belAction3));

		ActNode bel1 = (ActNode) Network.buildMolecularNode(bel1arr, CaseFrame.act);
		ActNode bel2 = (ActNode) Network.buildMolecularNode(bel2arr, CaseFrame.act);
		ActNode bel3 = (ActNode) Network.buildMolecularNode(bel3arr, CaseFrame.act);

		ActionNode doall = (ActionNode) Network.buildActionNode("doAll1", "doall");

		ArrayList<Wire> doAllarr = new ArrayList<Wire>();

		doAllarr.add(new Wire(r3, bel1));
		doAllarr.add(new Wire(r3, bel2));
		doAllarr.add(new Wire(r3, bel3));

		doAllarr.add(new Wire(r2, doall));

		ActNode doAll = (ActNode) Network.buildMolecularNode(doAllarr, CaseFrame.doAllAct);

		Runner.addToActStack(doAll);
		Runner.run();

		boolean allAsserted = false;
		
		if (Controller.getCurrentContext().isAsserted(propos1) && Controller.getCurrentContext().isAsserted(propos2)
				&& Controller.getCurrentContext().isAsserted(propos3)) {
			allAsserted = true;
		}

		assertTrue(allAsserted);

	}

	@Test
	public void testDoOne() throws CannotBuildNodeException, EquivalentNodeException, NotAPropositionNodeException,
			NodeNotFoundInNetworkException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException,
			IllegalIdentifierException {
		Relation r1 = Relation.obj;
		Relation r2 = Relation.action;
		Relation r3 = Relation.doo;

		PropositionNode propos1 = (PropositionNode) Network.buildBaseNode("yellow", proposition);
		PropositionNode propos2 = (PropositionNode) Network.buildBaseNode("blue", proposition);
		PropositionNode propos3 = (PropositionNode) Network.buildBaseNode("red", proposition);

		ActionNode belAction1 = (ActionNode) Network.buildActionNode("belAction1", "believe");
		ActionNode belAction2 = (ActionNode) Network.buildActionNode("belAction3", "believe");
		ActionNode belAction3 = (ActionNode) Network.buildActionNode("belAction3", "believe");

		ArrayList<Wire> bel1arr = new ArrayList<Wire>();
		ArrayList<Wire> bel2arr = new ArrayList<Wire>();
		ArrayList<Wire> bel3arr = new ArrayList<Wire>();

		bel1arr.add(new Wire(r1, propos1));
		bel1arr.add(new Wire(r2, belAction1));

		bel2arr.add(new Wire(r1, propos2));
		bel2arr.add(new Wire(r2, belAction2));

		bel3arr.add(new Wire(r1, propos3));
		bel3arr.add(new Wire(r2, belAction3));

		ActNode bel1 = (ActNode) Network.buildMolecularNode(bel1arr, CaseFrame.act);
		ActNode bel2 = (ActNode) Network.buildMolecularNode(bel2arr, CaseFrame.act);
		ActNode bel3 = (ActNode) Network.buildMolecularNode(bel3arr, CaseFrame.act);

		ActionNode doOneAction = (ActionNode) Network.buildActionNode("doOne", "doone");

		ArrayList<Wire> doOnearr = new ArrayList<Wire>();

		doOnearr.add(new Wire(r3, bel1));
		doOnearr.add(new Wire(r3, bel2));
		doOnearr.add(new Wire(r3, bel3));

		doOnearr.add(new Wire(r2, doOneAction));

		ActNode doOne = (ActNode) Network.buildMolecularNode(doOnearr, CaseFrame.doAllAct);

		Runner.addToActStack(doOne);
		Runner.run();

		boolean oneAsserted = false;

		if (Controller.getCurrentContext().isAsserted(propos1) || Controller.getCurrentContext().isAsserted(propos2)
				|| Controller.getCurrentContext().isAsserted(propos3)) {
			oneAsserted = true;
		}

		assertTrue(oneAsserted);
	}

	@Test
	public void testSNSequence() throws CannotBuildNodeException, EquivalentNodeException, NotAPropositionNodeException,
			NodeNotFoundInNetworkException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException,
			IllegalIdentifierException {
		Relation r1 = Relation.obj;
		Relation r2 = Relation.action;
		Relation r6 = Relation.doo;

		PropositionNode propos1 = (PropositionNode) Network.buildBaseNode("yellow", proposition);
		PropositionNode propos2 = (PropositionNode) Network.buildBaseNode("blue", proposition);
		PropositionNode propos3 = (PropositionNode) Network.buildBaseNode("red", proposition);

		ActionNode belAction1 = (ActionNode) Network.buildActionNode("belAction1", "believe");
		ActionNode belAction2 = (ActionNode) Network.buildActionNode("belAction3", "believe");
		ActionNode belAction3 = (ActionNode) Network.buildActionNode("belAction3", "believe");

		ArrayList<Wire> bel1arr = new ArrayList<Wire>();
		ArrayList<Wire> bel2arr = new ArrayList<Wire>();
		ArrayList<Wire> bel3arr = new ArrayList<Wire>();

		bel1arr.add(new Wire(r1, propos1));
		bel1arr.add(new Wire(r2, belAction1));

		bel2arr.add(new Wire(r1, propos2));
		bel2arr.add(new Wire(r2, belAction2));

		bel3arr.add(new Wire(r1, propos3));
		bel3arr.add(new Wire(r2, belAction3));

		ActNode bel1 = (ActNode) Network.buildMolecularNode(bel1arr, CaseFrame.act);
		ActNode bel2 = (ActNode) Network.buildMolecularNode(bel2arr, CaseFrame.act);
		ActNode bel3 = (ActNode) Network.buildMolecularNode(bel3arr, CaseFrame.act);

		ActionNode doall = (ActionNode) Network.buildActionNode("SNSequence", "SNSequence");

		ArrayList<Wire> doAllarr = new ArrayList<Wire>();

		doAllarr.add(new Wire(r6, bel1));
		doAllarr.add(new Wire(r6, bel2));
		doAllarr.add(new Wire(r6, bel3));

		doAllarr.add(new Wire(r2, doall));

		ActNode doAll = (ActNode) Network.buildMolecularNode(doAllarr, CaseFrame.SNSequenceAct);

		Runner.addToActStack(doAll);
		Runner.run();

		boolean allAsserted = false;

		if (Controller.getCurrentContext().isAsserted(propos1) && Controller.getCurrentContext().isAsserted(propos2)
				&& Controller.getCurrentContext().isAsserted(propos3)) {
			allAsserted = true;
		}

		assertTrue(allAsserted);

	}
//*/
	@Test
	public void testPreNew() throws CannotBuildNodeException, EquivalentNodeException, NotAPropositionNodeException,
			NodeNotFoundInNetworkException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException,
			IllegalIdentifierException, DuplicatePropositionException, ContradictionFoundException,
			ContextNameDoesntExistException {

		PropositionNode propos = (PropositionNode) Network.buildBaseNode("yellow", Semantic.proposition);
		PropositionNode precondition = (PropositionNode) Network.buildBaseNode("purple", Semantic.proposition);

		ActionNode actionBelieve = new Believe("belaction");// problem was with the action node,the creation of term and
															// semantic
		Network.getNodes().put("belaction", actionBelieve);
		Network.getNodesWithIDs().add(actionBelieve.getId(), actionBelieve);

		ArrayList<Wire> wiresArr = new ArrayList<Wire>();
		Relation r1 = Relation.obj;
		Relation r2 = Relation.action;

		wiresArr.add(new Wire(r1, propos));
		wiresArr.add(new Wire(r2, actionBelieve));

		ActNode believe = (ActNode) Network.buildMolecularNode(wiresArr, CaseFrame.act);

		ActionNode actionBelpre = new Believe("actionbelpre");
		Network.getNodes().put("belaction", actionBelpre);
		Network.getNodesWithIDs().add(actionBelpre.getId(), actionBelpre);

		ArrayList<Wire> wirePreBel = new ArrayList<Wire>();
		wirePreBel.add(new Wire(r1, precondition));
		wirePreBel.add(new Wire(r2, actionBelpre));
		
		ActNode belPre = (ActNode) Network.buildMolecularNode(wirePreBel, CaseFrame.act);
		//

		believe.restartAgenda();
		belPre.restartAgenda();

		// believe.getPreconditions().add(precondition);

		Runner.addToActStack(believe);
		// Runner.addToActStack(belPre);
		
	
		Runner.run();

		// Boolean isAsserted=Controller.getContextByName("Test
		// context").isAsserted(propos);
		Boolean isAsserted = Controller.getCurrentContext().isAsserted(propos);

		assertTrue(isAsserted);

	}
	
	
	@Test  
	public void
	   testMultipleActs() throws NotAPropositionNodeException,
	   NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException,
	   CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
		
	   PropositionNode propos = (PropositionNode) Network.buildBaseNode("yellow",proposition);
	   PropositionNode propos1 = (PropositionNode) Network.buildBaseNode("al ahly",proposition); 
	   PropositionNode precondition1 = (PropositionNode)Network.buildBaseNode("al Zmalek", proposition);
	   PropositionNode precondition = (PropositionNode)Network.buildBaseNode("blue", proposition);
	   PropositionNode precondition2 = (PropositionNode)Network.buildBaseNode("pink", proposition);
	   PropositionNode effect = (PropositionNode)Network.buildBaseNode("mo7m7y", proposition);
	   PropositionNode effect2 = (PropositionNode)Network.buildBaseNode("bo2b2y mz3tr", proposition);
	   
	   
	   ActionNode actionBelieve=new Believe("belaction");
	   
	   Network.getNodes().put("belaction",actionBelieve);  
	   Network.getNodesWithIDs().add(actionBelieve.getId(),actionBelieve);    
	   
	   ArrayList<Wire> wiresArr = new ArrayList<Wire>();
	   
	   Relation r1 =Relation.obj;   
	   Relation r2 = Relation.action; 
	   Relation r3 = Relation.precondition;
	   Relation r4 = Relation.act;
	   Relation r5 = Relation.effect;
	   
	   
	   wiresArr.add(new Wire(r1, propos));   
	   wiresArr.add(new Wire(r2,actionBelieve));
	   
	   
	   ActNode believe = (ActNode) Network.buildMolecularNode(wiresArr,CaseFrame.act);   
	   
	   
	   
	   
      ActionNode actionBelieve1=new Believe("belaction1");
	   
	   Network.getNodes().put("belaction1",actionBelieve1);  
	   Network.getNodesWithIDs().add(actionBelieve1.getId(),actionBelieve1);    
	   
	   ArrayList<Wire> wiresArr1 = new ArrayList<Wire>();
	    
	   wiresArr1.add(new Wire(r1, propos1));   
	   wiresArr1.add(new Wire(r2,actionBelieve1));
	   
	   
	   ActNode believe1 = (ActNode) Network.buildMolecularNode(wiresArr1,CaseFrame.act);
	   
	   
	   ArrayList<Wire> preActArr1=new ArrayList<Wire>();   
	   preActArr1.add(new Wire(r3,precondition1));  
	   preActArr1.add(new Wire(r4,believe1));   
	   
	   
	   ArrayList<Wire> preActArr=new ArrayList<Wire>();   
	   preActArr.add(new Wire(r3,precondition));  
	   preActArr.add(new Wire(r4,believe));   
	   
	   
	   ArrayList<Wire> preActArr2=new ArrayList<Wire>();   
	   preActArr2.add(new Wire(r3,precondition2));  
	   preActArr2.add(new Wire(r4,believe)); 
	   
	   
	   ArrayList<Wire> actEffectArr=new ArrayList<Wire>();   
	   actEffectArr.add(new Wire(r5,effect));  
	   actEffectArr.add(new Wire(r4,believe));   
	   
	 
	   ArrayList<Wire> actEffectArr2=new ArrayList<Wire>();   
	   actEffectArr2.add(new Wire(r5,effect2));  
	   actEffectArr2.add(new Wire(r4,believe)); 
	   
	   
	  // PropositionNode preAct2=(PropositionNode)Network.buildMolecularNode(preActArr2,CaseFrame.preconditionAct);
	   PropositionNode preAct=(PropositionNode)Network.buildMolecularNode(preActArr,CaseFrame.preconditionAct);
	   PropositionNode preAct1=(PropositionNode)Network.buildMolecularNode(preActArr1,CaseFrame.preconditionAct);
	   PropositionNode actEffect=(PropositionNode)Network.buildMolecularNode(actEffectArr,CaseFrame.actEffect);
	   PropositionNode actEffect2=(PropositionNode)Network.buildMolecularNode(actEffectArr2,CaseFrame.actEffect);
	   
      ActionNode actionBelpre1=new Believe("actionbelpre"); 
	   
	   Network.getNodes().put("belaction", actionBelpre1);  
	   Network.getNodesWithIDs().add(actionBelpre1.getId(),actionBelpre1);    
	   
	   ArrayList<Wire> wirePreBel1=new ArrayList<Wire>();   
	   wirePreBel1.add(new Wire(r2,actionBelpre1));   
	   wirePreBel1.add(new Wire(r1,precondition1));
	   
	   ActNode belPre1 = (ActNode) Network.buildMolecularNode(wirePreBel1,CaseFrame.act);      
	   believe1.restartAgenda();  
	   belPre1.restartAgenda();    
	   
	   
	   
	   ActionNode actionBelpre=new Believe("actionbelpre"); 
	   
	   Network.getNodes().put("belaction", actionBelpre);  
	   Network.getNodesWithIDs().add(actionBelpre.getId(),actionBelpre);    
	   
	   ArrayList<Wire> wirePreBel=new ArrayList<Wire>();   
	   wirePreBel.add(new Wire(r2,actionBelpre));   
	   wirePreBel.add(new Wire(r1,precondition));
	   
	   ActNode belPre = (ActNode) Network.buildMolecularNode(wirePreBel,CaseFrame.act);      
	   believe.restartAgenda();  
	   belPre.restartAgenda();    
	   
	   Runner.addToActStack(believe1);
	   Runner.addToActStack(belPre1);
	   Runner.addToActStack(believe);  
	   Runner.addToActStack(belPre);  
	   Runner.run();
	   
	     Boolean isAsserted1=Controller.getCurrentContext().isAsserted(precondition);
	     Boolean isAsserted2=Controller.getCurrentContext().isAsserted(propos);
	     Boolean isAsserted3=Controller.getCurrentContext().isAsserted(effect);
	     Boolean isAsserted4=Controller.getCurrentContext().isAsserted(precondition2);
	     Boolean isAsserted5=Controller.getCurrentContext().isAsserted(effect2);
	     Boolean isAsserted6=Controller.getCurrentContext().isAsserted(precondition1);
	     Boolean isAsserted7=Controller.getCurrentContext().isAsserted(propos1);
	     Boolean allAsserted=false;
	     
	     if(isAsserted1 && isAsserted2 && isAsserted3 && isAsserted5 && isAsserted6 && isAsserted7)
	    	 allAsserted=true;
	     
	     assertTrue(allAsserted);   
	     
	}
	
	
	
	@Test  
	public void
	   testAcheive() throws NotAPropositionNodeException,
	   NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException,
	   CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
		
		//a believe act with precondition that initially unsatisfied 
		//will be achieved by a plan that simply believes the precondition
		
		
		
		
	   PropositionNode propos = (PropositionNode) Network.buildBaseNode("yellow",proposition);
	   PropositionNode precondition = (PropositionNode)Network.buildBaseNode("blue", proposition);
	   
	   
	   ActionNode actionBelieve=(ActionNode)Network.buildActionNode("belaction","believe");
	   
	 
	   
	   ArrayList<Wire> wiresArr = new ArrayList<Wire>();
	   
	   Relation r1 =Relation.obj;   
	   Relation r2 = Relation.action; 
	   Relation r3 = Relation.precondition;
	   Relation r4 = Relation.act;
	   Relation r6 = Relation.plan;
	   Relation r7 = Relation.goal;
	   
	   
	   wiresArr.add(new Wire(r1, propos));   
	   wiresArr.add(new Wire(r2,actionBelieve));
	   
	   
	   ActNode believe = (ActNode) Network.buildMolecularNode(wiresArr,CaseFrame.act);   
	   
	   
	   
       
       ActionNode actionBelievePrecondition=(ActionNode)Network.buildActionNode("believepre","believe");
	   
	  
	   
	   ArrayList<Wire> wiresArrPre = new ArrayList<Wire>();
	    
	   wiresArrPre.add(new Wire(r1, precondition));   
	   wiresArrPre.add(new Wire(r2,actionBelievePrecondition));
	   
	   
	   ActNode believePrecondtion = (ActNode) Network.buildMolecularNode(wiresArrPre,CaseFrame.act);
	   
	   
	   
	   
	   ArrayList<Wire> preActArr=new ArrayList<Wire>();   
	   preActArr.add(new Wire(r3,precondition));  
	   preActArr.add(new Wire(r4,believe));   
	   
	   ArrayList<Wire> planGoalarr=new ArrayList<Wire>();   
	   planGoalarr.add(new Wire(r6,believePrecondtion));  
	   planGoalarr.add(new Wire(r7,precondition));   
	   
	   
	   
	   PropositionNode preAct=(PropositionNode)Network.buildMolecularNode(preActArr,CaseFrame.preconditionAct);
	   PropositionNode planGoal=(PropositionNode)Network.buildMolecularNode(planGoalarr,CaseFrame.planGoal);
	   
	
	   
	   believe.restartAgenda();
	   believePrecondtion.restartAgenda();
	   
	 //  Runner.addToActStack(believe1);
	 //  Runner.addToActStack(belPre1);
	   Runner.addToActStack(believe);  
	   //Runner.addToActStack(believePrecondtion);  
	   Runner.run();
	   
	     Boolean isAsserted1=Controller.getCurrentContext().isAsserted(precondition);
	     Boolean isAsserted2=Controller.getCurrentContext().isAsserted(propos);
	     Boolean allAsserted=false;
	     
	     if(isAsserted1 && isAsserted2)
	    	 allAsserted=true;
	     
	     assertTrue(allAsserted);   
	     
	}
	
	
	/*
	@Test  
	public void
	   testPlans() throws NotAPropositionNodeException,
	   NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException,
	   CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
		//This act will fail if the testNonPrimitice class does not exist
		
		
		
		//use Action Test to make non primitive act 
		//test the plan with it
		//need to make Test act and a PlanAct node which got act Test and some plan
		//
		
		
		
		
	   PropositionNode propos = (PropositionNode) Network.buildBaseNode("yellow",proposition);

	   
	   
	   ActionNode actionBelieve=(ActionNode)Network.buildActionNode("believeYellow","believe");
	   
	 
	   
	   ArrayList<Wire> wiresArr = new ArrayList<Wire>();
	   
	   Relation r1 =Relation.obj;   
	   Relation r2 = Relation.action; 
	   Relation r3 = Relation.precondition;
	   Relation r4 = Relation.act;
	   Relation r5 = Relation.effect;
	   Relation r6 = Relation.plan;
	   Relation r7 = Relation.goal;
	   
	   
	   wiresArr.add(new Wire(r1, propos));   
	   wiresArr.add(new Wire(r2,actionBelieve));
	   
	   
	   ActNode believe = (ActNode) Network.buildMolecularNode(wiresArr,CaseFrame.act);   
	   //this will be the plan
	   
	   
       
       ActionNode actionTest=new TestNonPrimitive("testNonPrimitive");
       Network.getNodes().put("testNonPrimitive", actionTest);
       Network.getNodesWithIDs().add(actionTest.getId(), actionTest);
	   
       ArrayList<Wire> testWiresArr = new ArrayList<Wire>();
       
       testWiresArr.add(new Wire(r1,propos));
       testWiresArr.add(new Wire(r2,actionTest));
	   
       ActNode testAct = (ActNode) Network.buildMolecularNode(testWiresArr,CaseFrame.act);  
	   
	   
       
       ArrayList<Wire> planActArr=new ArrayList<Wire>();   
	   planActArr.add(new Wire(r6,believe));  
	   planActArr.add(new Wire(r4,testAct));   
       
	   
	   PropositionNode planAct=(PropositionNode)Network.buildMolecularNode(planActArr,CaseFrame.planAct);
     
	   
	 
	   
	   
	   believe.restartAgenda();
	   testAct.restartAgenda();
	   
	   Runner.addToActStack(testAct);  
	   Runner.run();
	   
	    // Boolean isAsserted1=Controller.getCurrentContext().isAsserted(precondition);
	     Boolean isAsserted2=Controller.getCurrentContext().isAsserted(propos);
	     Boolean allAsserted=false;
	     
	     if(isAsserted2)
	    	 allAsserted=true;
	     
	     assertTrue(isAsserted2);   
	     
	}
	*/
	
	
	@Test  
	public void
	   testSNIF() throws NotAPropositionNodeException,
	   NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException,
	   CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
	   //first i will need to make several guardAct nodes 
	   //then make a SNIF act and it will point to them using doo relation arc
	   //the first try will have two guardact nodes with each one having one guard
		
		
		
	   PropositionNode propos1 = (PropositionNode) Network.buildBaseNode("orange",proposition);
	   PropositionNode propos2 = (PropositionNode) Network.buildBaseNode("barca",proposition); 
	   PropositionNode guardOf1 = (PropositionNode)Network.buildBaseNode("purple", proposition);
	   PropositionNode guardOf2 = (PropositionNode)Network.buildBaseNode("realmadrid", proposition);
	   
	   ActionNode actionBelieve1=(ActionNode)Network.buildActionNode("believeYellow","believe");
	   
	  
	   
	   ArrayList<Wire> wiresArr1 = new ArrayList<Wire>();
	   
	   Relation r1 = Relation.obj;   
	   Relation r2 = Relation.action; 
	   Relation r3 = Relation.precondition;
	   Relation r4 = Relation.act;
	   Relation r5 = Relation.doo;
	   Relation r6= Relation.guard;
	   
	   
	   wiresArr1.add(new Wire(r1, propos1));   
	   wiresArr1.add(new Wire(r2,actionBelieve1));
	   
	   
	   ActNode believe1 = (ActNode) Network.buildMolecularNode(wiresArr1,CaseFrame.act);   
	   //first act
	   
	   
	   
       ActionNode actionBelieve2=(ActionNode)Network.buildActionNode("believeAlahly","believe");
	   
	   ArrayList<Wire> wiresArr2 = new ArrayList<Wire>();
	    
	   wiresArr2.add(new Wire(r1, propos2));   
	   wiresArr2.add(new Wire(r2,actionBelieve2));
	   
	   
	   ActNode believe2 = (ActNode) Network.buildMolecularNode(wiresArr2,CaseFrame.act);
	   //second act
	   //////////////////////////
	   
	   ActionNode actionBelieveGuard1=(ActionNode)Network.buildActionNode("believeGuard1","believe");
	   //action node of believing the guard of the first node
	   
	   ArrayList<Wire> believeGuard1Arr= new ArrayList<Wire>();  
	   believeGuard1Arr.add(new Wire(r1, guardOf1));   
	   believeGuard1Arr.add(new Wire(r2,actionBelieveGuard1));
	   //wires of the believe act of the first guard
	   
	   ActNode believeGuard1 = (ActNode) Network.buildMolecularNode(believeGuard1Arr,CaseFrame.act);
	   ////////////////////////////
	   
	   
	   ActionNode actionBelieveGuard2=(ActionNode)Network.buildActionNode("believeGuard2","believe");
	   //action node of believing the guard of the first node
	   
	   ArrayList<Wire> believeGuard2Arr= new ArrayList<Wire>();  
	   believeGuard2Arr.add(new Wire(r1, guardOf2));   
	   believeGuard2Arr.add(new Wire(r2,actionBelieveGuard2));
	   //wires of the believe act of the first guard
	   
	   ActNode believeGuard2 = (ActNode) Network.buildMolecularNode(believeGuard2Arr,CaseFrame.act);
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   ArrayList<Wire> guardActArr1=new ArrayList<Wire>();   
	   guardActArr1.add(new Wire(r6,guardOf1));  
	   guardActArr1.add(new Wire(r4,believe1));
	   //wires of the first guardAct node
	   
	   
	   ArrayList<Wire> guardActArr2=new ArrayList<Wire>();   
	   guardActArr2.add(new Wire(r6,guardOf2));  
	   guardActArr2.add(new Wire(r4,believe2));   
	   //wires of second guardAct node
	   
	   
	   
	  
	   PropositionNode guardAct1=(PropositionNode)Network.buildMolecularNode(guardActArr1,CaseFrame.guardAct);
	   PropositionNode guardAct2=(PropositionNode)Network.buildMolecularNode(guardActArr2,CaseFrame.guardAct);
	   //the propositiionNodes are done
	   //now making the SNIf act
	   
       ActionNode snif=(ActionNode)Network.buildActionNode("snifAct","snif");
       //creating the action node of the snif act
       
       ArrayList<Wire> SNIFArr=new ArrayList<Wire>();   
       SNIFArr.add(new Wire(r2,snif));  
       SNIFArr.add(new Wire(r1,guardAct1));
       SNIFArr.add(new Wire(r1,guardAct2));  
	   //creating the wires of the snif act
       
       ActNode SNIF = (ActNode) Network.buildMolecularNode(SNIFArr,CaseFrame.SNIF);
	   //the SNIF act node.
       
       
       
	   
	    
	   believe1.restartAgenda();  
	   believe2.restartAgenda();    
	   
	   Runner.addToActStack(SNIF);
	   Runner.addToActStack(believeGuard1);
	   Runner.addToActStack(believeGuard2);
	   Runner.run();
	   
	     Boolean isAsserted1=Controller.getCurrentContext().isAsserted(propos1);
	     Boolean isAsserted2=Controller.getCurrentContext().isAsserted(propos2);
	     Boolean isAsserted3=Controller.getCurrentContext().isAsserted(guardOf1);
	     Boolean isAsserted4=Controller.getCurrentContext().isAsserted(guardOf2);
	     Boolean allAsserted=false;
	     
	     if((isAsserted1 || isAsserted2)/* && (isAsserted3 && isAsserted4)*/)
	    	 allAsserted=true;
	     
	     assertTrue(allAsserted);   
	     
	}
	
	
	
	
	@Test  
	public void
	   testSNIterate() throws NotAPropositionNodeException,
	   NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException,
	   CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
	   //first i will need to make several guardAct nodes 
	   //then make a SNIF act and it will point to them using doo relation arc
	   //the first try will have two guardact nodes with each one having one guard
		
		
		
	   PropositionNode propos1 = (PropositionNode) Network.buildBaseNode("orange",proposition);
	   PropositionNode propos2 = (PropositionNode) Network.buildBaseNode("barca",proposition); 
	   PropositionNode guardOf1 = (PropositionNode)Network.buildBaseNode("purple", proposition);
	   PropositionNode guardOf2 = (PropositionNode)Network.buildBaseNode("realmadrid", proposition);
	  // PropositionNode precondition2 = (PropositionNode)Network.buildBaseNode("pink", proposition);
	   //PropositionNode effect = (PropositionNode)Network.buildBaseNode("mo7m7y", proposition);
	  // PropositionNode effect2 = (PropositionNode)Network.buildBaseNode("bo2b2y mz3tr", proposition);
	   
	   
	   ActionNode actionBelieve1=(ActionNode)Network.buildActionNode("believeYellow","believe");
	   
	  
	   
	   ArrayList<Wire> wiresArr1 = new ArrayList<Wire>();
	   
	   Relation r1 = Relation.obj;   
	   Relation r2 = Relation.action; 
	   Relation r3 = Relation.precondition;
	   Relation r4 = Relation.act;
	   Relation r5 = Relation.doo;
	   Relation r6= Relation.guard;
	   
	   
	   wiresArr1.add(new Wire(r1, propos1));   
	   wiresArr1.add(new Wire(r2,actionBelieve1));
	   
	   
	   ActNode believe1 = (ActNode) Network.buildMolecularNode(wiresArr1,CaseFrame.act);   
	   //first act
	   
	   
	   
       ActionNode actionBelieve2=(ActionNode)Network.buildActionNode("believeAlahly","believe");
	   
	   ArrayList<Wire> wiresArr2 = new ArrayList<Wire>();
	    
	   wiresArr2.add(new Wire(r1, propos2));   
	   wiresArr2.add(new Wire(r2,actionBelieve2));
	   
	   
	   ActNode believe2 = (ActNode) Network.buildMolecularNode(wiresArr2,CaseFrame.act);
	   //second act
	   //////////////////////////
	   
	   ActionNode actionBelieveGuard1=(ActionNode)Network.buildActionNode("believeGuard1","believe");
	   //action node of believing the guard of the first node
	   
	   ArrayList<Wire> believeGuard1Arr= new ArrayList<Wire>();  
	   believeGuard1Arr.add(new Wire(r1, guardOf1));   
	   believeGuard1Arr.add(new Wire(r2,actionBelieveGuard1));
	   //wires of the believe act of the first guard
	   
	   ActNode believeGuard1 = (ActNode) Network.buildMolecularNode(believeGuard1Arr,CaseFrame.act);
	   ////////////////////////////
	   
	   
	   ActionNode actionBelieveGuard2=(ActionNode)Network.buildActionNode("believeGuard2","believe");
	   //action node of believing the guard of the first node
	   
	   ArrayList<Wire> believeGuard2Arr= new ArrayList<Wire>();  
	   believeGuard2Arr.add(new Wire(r1, guardOf2));   
	   believeGuard2Arr.add(new Wire(r2,actionBelieveGuard2));
	   //wires of the believe act of the first guard
	   
	   ActNode believeGuard2 = (ActNode) Network.buildMolecularNode(believeGuard2Arr,CaseFrame.act);
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   ArrayList<Wire> guardActArr1=new ArrayList<Wire>();   
	   guardActArr1.add(new Wire(r6,guardOf1));  
	   guardActArr1.add(new Wire(r4,believe1));
	   //wires of the first guardAct node
	   
	   
	   ArrayList<Wire> guardActArr2=new ArrayList<Wire>();   
	   guardActArr2.add(new Wire(r6,guardOf2));  
	   guardActArr2.add(new Wire(r4,believe2));   
	   //wires of second guardAct node
	   
	   
	   
	  
	   PropositionNode guardAct1=(PropositionNode)Network.buildMolecularNode(guardActArr1,CaseFrame.guardAct);
	   PropositionNode guardAct2=(PropositionNode)Network.buildMolecularNode(guardActArr2,CaseFrame.guardAct);
	   //the propositiionNodes are done
	   //now making the SNIf act
	   
       ActionNode sniterate=(ActionNode)Network.buildActionNode("snifAct","sniterate");
       //creating the action node of the snif act
       
       ArrayList<Wire> SNIFArr=new ArrayList<Wire>();   
       SNIFArr.add(new Wire(r2,sniterate));  
       SNIFArr.add(new Wire(r1,guardAct1));
       SNIFArr.add(new Wire(r1,guardAct2));  
	   //creating the wires of the snif act
       
       ActNode SNIterate = (ActNode) Network.buildMolecularNode(SNIFArr,CaseFrame.SNIF);
	   //the SNIF act node.
       
       
       
	   
	    
	   believe1.restartAgenda();  
	   believe2.restartAgenda();    
	   
	   
	   
	  
	   Runner.addToActStack(SNIterate);
	  // Runner.addToActStack(believeGuard1);
	 //  Runner.addToActStack(believeGuard2);
	   Runner.run();
	   
	     Boolean isAsserted1=Controller.getCurrentContext().isAsserted(propos1);
	     Boolean isAsserted2=Controller.getCurrentContext().isAsserted(propos2);
	     Boolean isAsserted3=Controller.getCurrentContext().isAsserted(guardOf1);
	     Boolean isAsserted4=Controller.getCurrentContext().isAsserted(guardOf2);
	     Boolean allAsserted=false;
	     
	     if((isAsserted1 || isAsserted2)/* && (isAsserted3 && isAsserted4)*/)
	    	 allAsserted=true;
	     
	     assertTrue(allAsserted);   
	     
	}
	
	
	
	
	
	@Test  
	public void
	   testWithSome() throws NotAPropositionNodeException,
	   NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException,
	   CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
	   //first a propertyObject node will be done with the object pointing to a variable
	   //then a believe act with the object pointing to the same variable
	   //withsome that has the suchthat arc pointing to the propertobj node,vars that point to the variable
		//and do arc that point to the act
		
		
		
	   PropositionNode property = (PropositionNode) Network.buildBaseNode("meow",proposition);
	   PropositionNode object = (PropositionNode) Network.buildBaseNode("cat",proposition); 
	   VariableNode var = (VariableNode)Network.buildVariableNode("V1");
	 
	   
	   
	   ActionNode actionBelieve=(ActionNode)Network.buildActionNode("believePlease","believe");
	   
	  
	   
	   ArrayList<Wire> wiresArr1 = new ArrayList<Wire>();
	   
	   Relation r1 = Relation.obj;   
	   Relation r2 = Relation.action; 
	   Relation r3 = Relation.property;
	   
	   
	   wiresArr1.add(new Wire(r1, var));   
	   wiresArr1.add(new Wire(r2,actionBelieve));
	   
	   
	   ActNode believe = (ActNode) Network.buildMolecularNode(wiresArr1,CaseFrame.act);   
	   //first act
	   
	   
	   
       
	   //////////////////////////
	   
	  
	   ////////////////////////////
	   
	   
	  
	   
	   
	   
	   
	   
	   
	   
	   
	   ArrayList<Wire> objectPropertyVarArr=new ArrayList<Wire>();   
	   objectPropertyVarArr.add(new Wire(r1,var));  
	   objectPropertyVarArr.add(new Wire(r3,property));
	   //wires of the first guardAct node
	   
	   
	   ArrayList<Wire> objectPropertyArr=new ArrayList<Wire>();   
	   objectPropertyArr.add(new Wire(r1,object));  
	   objectPropertyArr.add(new Wire(r3,property));   
	   //wires of second guardAct node
	   
	   
	   
	  
	   PropositionNode objPropertyVar=(PropositionNode)Network.buildMolecularNode(objectPropertyVarArr,CaseFrame.propertyObject);
	   PropositionNode objProperty=(PropositionNode)Network.buildMolecularNode(objectPropertyArr,CaseFrame.propertyObject);
	   //the propositiionNodes are done
	   //now making the SNIf act
	   
       ActionNode withSome=(ActionNode)Network.buildActionNode("withSome","withsome");
       //creating the action node of the snif act
       
       ArrayList<Wire> withSomeArr=new ArrayList<Wire>();   
       withSomeArr.add(new Wire(Relation.suchthat,objPropertyVar));  
       withSomeArr.add(new Wire(Relation.doo,believe)); 
       withSomeArr.add(new Wire(Relation.action,withSome));
       withSomeArr.add(new Wire(Relation.vars,var));
	   //creating the wires of the snif act
       
       ActNode withSomeAct = (ActNode) Network.buildMolecularNode(withSomeArr,CaseFrame.withSome);
	   //the SNIF act node.
       
       
       
	   
	    
	   believe.restartAgenda();  
	   withSomeAct.restartAgenda();    
	   
	   
	   
	  
	   Runner.addToActStack(withSomeAct);
	 //  Runner.addToActStack(believeGuard1);
	  // Runner.addToActStack(believeGuard2);
	   Runner.run();
	   
	     Boolean isAsserted1=Controller.getCurrentContext().isAsserted(object);
	     Boolean isAsserted2=Controller.getCurrentContext().isAsserted(property);
	     //Boolean isAsserted3=Controller.getCurrentContext().isAsserted(guardOf1);
	     //Boolean isAsserted4=Controller.getCurrentContext().isAsserted(guardOf2);
	     Boolean allAsserted=false;
	     
	     if((isAsserted1 && isAsserted2)/* && (isAsserted3 && isAsserted4)*/)
	    	 allAsserted=true;
	     
	     assertTrue(isAsserted1);   
	     
	}
	
	
	
	
	@Test  
	public void
	   testWithAll() throws NotAPropositionNodeException,
	   NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException,
	   CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
	   //first a propertyObject node will be done with the object pointing to a variable
	   //then a believe act with the object pointing to the same variable
	   //withsome that has the suchthat arc pointing to the propertobj node,vars that point to the variable
		//and do arc that point to the act
		
		
		
	   PropositionNode property = (PropositionNode) Network.buildBaseNode("meow",proposition);
	   PropositionNode object = (PropositionNode) Network.buildBaseNode("cat",proposition);
	   PropositionNode object2 = (PropositionNode) Network.buildBaseNode("genticallyEngineeredDog",proposition); 
	   VariableNode var = (VariableNode)Network.buildVariableNode("V1");
	 
	   
	   
	   ActionNode actionBelieve=(ActionNode)Network.buildActionNode("believePlease","believe");
	   
	  
	   
	   ArrayList<Wire> wiresArr1 = new ArrayList<Wire>();
	   
	   Relation r1 = Relation.obj;   
	   Relation r2 = Relation.action; 
	   Relation r3 = Relation.property;
	 
	   
	   wiresArr1.add(new Wire(r1, var));   
	   wiresArr1.add(new Wire(r2,actionBelieve));
	   
	   
	   ActNode believe = (ActNode) Network.buildMolecularNode(wiresArr1,CaseFrame.act);   
	   //first act
	   
	   
	   
       
	   //////////////////////////
	   
	  
	   ////////////////////////////
	   
	   
	  
	   
	   
	   
	   
	   
	   
	   
	   
	   ArrayList<Wire> objectPropertyVarArr=new ArrayList<Wire>();   
	   objectPropertyVarArr.add(new Wire(r1,var));  
	   objectPropertyVarArr.add(new Wire(r3,property));
	   
	   ArrayList<Wire> objectPropertyArr=new ArrayList<Wire>();   
	   objectPropertyArr.add(new Wire(r1,object));  
	   objectPropertyArr.add(new Wire(r3,property));   
	   //wires of the first guardAct node
	   
	   
	   
	   ArrayList<Wire> objectProperty2Arr=new ArrayList<Wire>();   
	   objectProperty2Arr.add(new Wire(r1,object2));  
	   objectProperty2Arr.add(new Wire(r3,property));   
	   //wires of second guardAct node
	   
	   
	   
	  
	   PropositionNode objPropertyVar=(PropositionNode)Network.buildMolecularNode(objectPropertyVarArr,CaseFrame.propertyObject);
	   PropositionNode objProperty=(PropositionNode)Network.buildMolecularNode(objectPropertyArr,CaseFrame.propertyObject);
	   PropositionNode objProperty2=(PropositionNode)Network.buildMolecularNode(objectProperty2Arr,CaseFrame.propertyObject);
	   //the propositiionNodes are done
	   //now making the SNIf act
	   
       ActionNode withSome=(ActionNode)Network.buildActionNode("withAll","withAll");
       //creating the action node of the snif act
       
       ArrayList<Wire> withAllArr=new ArrayList<Wire>();   
       withAllArr.add(new Wire(Relation.suchthat,objPropertyVar));  
       withAllArr.add(new Wire(Relation.doo,believe)); 
       withAllArr.add(new Wire(Relation.action,withSome));
       withAllArr.add(new Wire(Relation.vars,var));
	   //creating the wires of the snif act
       
       ActNode withAllAct = (ActNode) Network.buildMolecularNode(withAllArr,CaseFrame.withSome);
	   //the SNIF act node.
       
       
       
	   
	    
	   believe.restartAgenda();  
	   withAllAct.restartAgenda();    
	   
	   
	   
	  
	   Runner.addToActStack(withAllAct);
	 //  Runner.addToActStack(believeGuard1);
	  // Runner.addToActStack(believeGuard2);
	   Runner.run();
	   
	     Boolean isAsserted1=Controller.getCurrentContext().isAsserted(object);
	     Boolean isAsserted2=Controller.getCurrentContext().isAsserted(property);
	     Boolean isAsserted3=Controller.getCurrentContext().isAsserted(object2);
	     //Boolean isAsserted4=Controller.getCurrentContext().isAsserted(guardOf2);
	     Boolean allAsserted=false;
	     
	     if((isAsserted1 && isAsserted3)/* && (isAsserted3 && isAsserted4)*/)
	    	 allAsserted=true;
	     
	     assertTrue(allAsserted);   
	     
	}
	
	@Test
	public void testDisbelieve()
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
		PropositionNode prop1 = (PropositionNode) Network.buildBaseNode("red", proposition);
		PropositionNode prop2 = (PropositionNode) Network.buildBaseNode("favouriteColor", proposition);
		
		Relation r1 = Relation.obj;
		Relation r2 = Relation.action;
		
		
		ActionNode believeAction=(ActionNode)Network.buildActionNode("believe","believe");
		ActionNode disBelieveAction=(ActionNode)Network.buildActionNode("disBelieve","disBelieve");
		
		ArrayList<Wire> believearr = new ArrayList<Wire>();
		believearr.add(new Wire(r1, prop1));
		believearr.add(new Wire(r2, believeAction));
		
		
		ActNode believe=(ActNode)Network.buildMolecularNode(believearr, CaseFrame.act);
		
		ArrayList<Wire> disBelievearr = new ArrayList<Wire>();
		disBelievearr.add(new Wire(r1, prop1));
		disBelievearr.add(new Wire(r2, disBelieveAction));
		
		ActNode disBelieve=(ActNode)Network.buildMolecularNode(disBelievearr, CaseFrame.act);
		
		believe.restartAgenda();
		disBelieve.restartAgenda();
		
		
		Runner.addToActStack(disBelieve);
		Runner.addToActStack(believe);
	
		
		Runner.run();
		
		boolean notAsserted=!Controller.getCurrentContext().isAsserted(prop1);
		
		
		assertTrue(notAsserted);
		// PropositionNode prop = (PropositionNode);
		// Network.buildMolecularNode(wires, caseFrame);
	}
	
	
	
	
	

}
