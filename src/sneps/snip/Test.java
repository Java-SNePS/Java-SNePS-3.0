package sneps.snip;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import sneps.exceptions.ContextNameDoesntExistException;
import sneps.exceptions.IllegalIdentifierException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.VariableNode;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.Semantic;
import sneps.network.classes.SemanticHierarchy;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Variable;
import sneps.snebr.Controller;
import sneps.snip.channels.Channel;
import sneps.snip.channels.ChannelTypes;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.matching.Substitutions;
import sneps.snip.rules.AndEntailment;

public class Test {
	/* Report sending (support ) */
	/* switch changing report's subs */
	/* deduce */
	/* add */
	/* RuleResponse returns M4's channel ma3 el report el mafroud ytb3t always */
	public static void main(String[] args) throws Exception {
		Network.defineDefaults();
		Semantic.createDefaultSemantics();
		Relation.createDefaultRelations();
		/* Case frame creation - [member, class] */
		LinkedList<Relation> relationSet = new LinkedList<Relation>();
		relationSet.add(Network.defineRelation("member", "Proposition"));
		relationSet.add(Network.defineRelation("class", "Proposition"));
		CaseFrame classMemberCF = Network.defineCaseFrame("Proposition", relationSet);

		LinkedList<Relation> relationSet1 = new LinkedList<Relation>();
		relationSet1.add(Network.defineRelation("object", "Proposition"));
		relationSet1.add(Network.defineRelation("property", "Proposition"));
		CaseFrame objectPropertyCF = Network.defineCaseFrame("Proposition", relationSet1);
		LinkedList<Relation> relationSet2 = new LinkedList<Relation>();
		relationSet2.add(Network.defineRelation("ant", "ant"));
		relationSet2.add(Network.defineRelation("cq", "cq"));
		relationSet2.add(Network.defineRelation("quant", "quant"));
		CaseFrame antConsQuantCF = Network.defineCaseFrame("AntsCons", relationSet1);

		Node leo = Network.buildBaseNode("Leo", SemanticHierarchy.getSemantic("Proposition"));
		Node roger = Network.buildBaseNode("Roger", SemanticHierarchy.getSemantic("Proposition"));
		Node fido = Network.buildBaseNode("Fido", SemanticHierarchy.getSemantic("Proposition"));
		Node dog = Network.buildBaseNode("Dog", SemanticHierarchy.getSemantic("Proposition"));
		Node loyal = Network.buildBaseNode("Loyal", SemanticHierarchy.getSemantic("Proposition"));

		VariableNode X = Network.buildVariableNode("X");
		VariableNode Y = Network.buildVariableNode("Y");
		ArrayList<Wire> wires = new ArrayList<Wire>();
		wires.add(new Wire(Network.getRelation("member"), fido));
		wires.add(new Wire(Network.getRelation("class"), dog));
		Node fidoIsADog = Network.buildMolecularNode(wires, classMemberCF);
		wires.clear();
		wires.add(new Wire(Network.getRelation("member"), leo));
		wires.add(new Wire(Network.getRelation("class"), dog));
		Node leoIsADog = Network.buildMolecularNode(wires, classMemberCF);
		try {
			Controller.addPropToContext("default", fidoIsADog.getId());
			Controller.addPropToContext("default", leoIsADog.getId());
		} catch (ContextNameDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		wires.clear();
		wires.add(new Wire(Network.getRelation("member"), X));
		wires.add(new Wire(Network.getRelation("class"), dog));
		Node xIsADog = Network.buildMolecularNode(wires, classMemberCF);
		wires.clear();
		wires.add(new Wire(Network.getRelation("member"), Y));
		wires.add(new Wire(Network.getRelation("class"), dog));
		Node yIsADog = Network.buildMolecularNode(wires, classMemberCF);
		wires.clear();
		wires.add(new Wire(Network.getRelation("object"), X));
		wires.add(new Wire(Network.getRelation("property"), loyal));
		Node xIsLoyal = Network.buildMolecularNode(wires, objectPropertyCF);
		/*
		 * Knowledge Base established : [Dog(Leo), Dog(Fido), Dog(Roger), Dog(X),
		 * Dog(Y)]
		 */
		wires.clear();
		wires.add(new Wire(Relation.andAnt, xIsADog));
		wires.add(new Wire(Relation.cq, xIsLoyal));
		Node ifXIsADogThenXIsLoyal = Network.buildMolecularNode(wires, RelationsRestrictedCaseFrame.andRule);
//		wires.clear();
//		wires.add(new Wire(Network.getRelation("member"), roger));
//		wires.add(new Wire(Network.getRelation("class"), dog));
//		Node rogerIsADog = Network.buildMolecularNode(wires, classMemberCF);
		Hashtable<String, Node> nodes = Network.getNodes();
		System.out.println("Nodes in the Network\n" + nodes + "\n");
//		Substitutions filterSubs = new LinearSubstitutions();
//		filterSubs.putIn(new Binding(X, nodes.get("Leo")));
//		filterSubs.putIn(new Binding(Y, nodes.get("Roger")));
//		((PropositionNode) leoIsADog).isWhQuestion(filterSubs);
		((PropositionNode) xIsLoyal).deduce();
	}

}
