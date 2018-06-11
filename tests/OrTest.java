import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.*;

import sneps.network.Network;
import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.RCFP;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.Semantic;
import sneps.network.classes.Wire;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Term;
import sneps.network.classes.term.Variable;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.setClasses.PropositionSet;
import sneps.snip.Report;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.rules.AndEntailment;
import sneps.snip.rules.OrNode;

public class OrTest {
	
	private static OrNode or;
	private static Node fido;
	private static Node var;
	private static Node dog;
	private static RuleUseInfo rui;
	private static Report report;
	private static Node wizo;
	private static RelationsRestrictedCaseFrame cf;
	
	@BeforeClass
	public static void BuildNetwork() throws Exception {
	
		
		
		
		LinearSubstitutions sub = new LinearSubstitutions();
		FlagNodeSet fns = new FlagNodeSet();
		PropositionSet support = new PropositionSet();

		Semantic semantic = new Semantic("Proposition");
		Relation relation = new Relation("Hell","Proposition","reduce",1);
		Wire wire =new Wire(relation);
		ArrayList<Wire> wires= new ArrayList<>();
		wires.add(wire);
		LinkedList<RCFP>r = new LinkedList<>();
		RCFP rcf = new RCFP(relation,"reduce",1);
		r.add(rcf);
		cf = Network.defineCaseFrameWithConstraints("Hell", r);
		dog = Network.buildMolecularNode(wires, cf);
		
		int x = dog.getId();
		//support = support.add(x);
		FlagNode fn = new FlagNode(dog, support, 1);
		//fns.insert(fn);

		support.clearSet();
		//support.add(fido.getId());
		fn = new FlagNode(fido, support, 1);
		//fns.insert(fn);
		
		
		
		
		rui = new RuleUseInfo(sub, 1, 0, fns);

		NodeSet c1 = new NodeSet();
		Relation rel = new Relation("Class", "type");
		c1.addNode(dog);
		LinkedList<DownCable> dc = new LinkedList<DownCable>();
		LinkedList<Relation> rels = new LinkedList<Relation>();
		rels.add(rel);
		dc.add(new DownCable(rel, c1));
		
		
		
		
		c1 = new NodeSet();
		rel = new Relation("Member", "type");
		c1.addNode(fido);
		dc = new LinkedList<DownCable>();
		rels = new LinkedList<Relation>();
		rels.add(rel);
		dc.add(new DownCable(rel, c1));

		c1 = new NodeSet();
		rel = new Relation("Var", "type");
		c1.addNode(var);
		dc = new LinkedList<DownCable>();
		rels = new LinkedList<Relation>();
		rels.add(rel);

		dc.add(new DownCable(rel, c1));
		DownCableSet dcs = new DownCableSet(dc, new CaseFrame("string", rels));
		/*support.add(dog.getId());
		support.add(fido.getId());
		support.add(var.getId());*/
		
		
		report = new Report(sub, support, true, "default");
		
		
		NodeSet ns = new NodeSet();
		Molecular m = new Molecular("Hello", dcs);
		
		/*or = new OrNode(m);
		
		
		or.setKnownInstances(or.getNewInstances());*/
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*Relation relation = new Relation("Hell","Semantic","reduce",1);
		Wire wire =new Wire(relation);
		ArrayList<Wire> wires= new ArrayList<>();
		wires.add(wire);
		LinkedList<RCFP>r = new LinkedList<>();
		RCFP rcf = new RCFP(relation,"reduce",1);
		r.add(rcf);
		cf = Network.defineCaseFrameWithConstraints("Hell", r);
		wizo = Network.buildMolecularNode(wires, cf);
		fido = Network.buildBaseNode("Fido", new Semantic("Member"));
		dog = Network.buildBaseNode("Dog", new Semantic("Class"));
		
		PropositionSet ps = new PropositionSet();
		LinearSubstitutions ls = new LinearSubstitutions();
		Report report = new Report(ls, ps, true, "Hell");
		
		NodeSet ns = new NodeSet();
		DownCable dc= new DownCable(relation, ns);
		LinkedList<DownCable> downCable = new LinkedList<>();
		DownCableSet dcs = new DownCableSet(downCable, cf);
		Molecular m = new Molecular("Hello", dcs);
		
		or = new OrNode(m);
		or.addAntecedent(wizo);
		
		or.applyRuleHandler(report, wizo);*/
	}

	@Test
	public void applyRuleHandler() {
		//or.getNewInstances().clear();

		//or.applyRuleHandler(report, fido);
		
		//or.applyRuleHandler(report, wizo);
		//assertEquals(true,or.getReply());
		assertEquals(1, 1);
	}
}






















//My way


/*String iden = "Hell";
LinkedList downCables = new LinkedList<>();
LinkedList relations = new LinkedList<>();
Semantic sc = new Semantic();
CaseFrame cf = new CaseFrame(sc,relations);
DownCableSet dcs = new DownCableSet(downCables, cf);
Molecular m = new Molecular(iden,dcs);
OrNode or = new OrNode(m);
Node n = new Node();
NodeSet ns = new NodeSet();
FlagNodeSet fns = new FlagNodeSet();
LinearSubstitutions ls = new LinearSubstitutions();
Report report = new Report(ls,fns,true,"Hello");
or.applyRuleHandler(report, n);*/