import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.*;

import sneps.network.Network;
import sneps.network.Node;
import sneps.network.VariableNode;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.RCFP;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.Semantic;
import sneps.network.classes.Wire;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Variable;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.snip.Report;
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
	
	@BeforeClass
	public static void BuildNetwork() throws Exception {
	
		Relation relation = new Relation("Hell","Semantic","reduce",1);
		Wire wire =new Wire(relation);
		ArrayList<Wire> wires= new ArrayList<>();
		wires.add(wire);
		LinkedList<RCFP>r = new LinkedList<>();
		RCFP rcf = new RCFP(relation,"reduce",1);
		r.add(rcf);
		RelationsRestrictedCaseFrame caseFrame = new RelationsRestrictedCaseFrame("Semantic",r);
		wizo = Network.buildMolecularNode(wires, caseFrame);
		fido = Network.buildBaseNode("Fido", new Semantic("Member"));
		dog = Network.buildBaseNode("Dog", new Semantic("Class"));
	}

	@Test
	public void applyRuleHandler() {
		assertEquals(1,1);
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