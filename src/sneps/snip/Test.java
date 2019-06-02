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
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.Semantic;
import sneps.network.classes.SemanticHierarchy;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Variable;
import sneps.snebr.Controller;
import sneps.snip.channels.Channel;
import sneps.snip.channels.ChannelTypes;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.matching.Substitutions;

public class Test {
	/* Report sending (support ) */
	/* switch changing report's subs */
	/* deduce */
	/* add */
	/* RuleResponse returns M4's channel ma3 el report el mafroud ytb3t always */
	public static void main(String[] args) throws Exception {
		Semantic.createDefaultSemantics();

		/* Case frame creation - [member, class] */
		LinkedList<Relation> relationSet = new LinkedList<Relation>();
		relationSet.add(Network.defineRelation("member", "Proposition"));
		relationSet.add(Network.defineRelation("class", "Proposition"));
		CaseFrame classMemberCF = Network.defineCaseFrame("Proposition", relationSet);
		Node leo = Network.buildBaseNode("Leo", SemanticHierarchy.getSemantic("Proposition"));
		Node roger = Network.buildBaseNode("Roger", SemanticHierarchy.getSemantic("Proposition"));
		Node fido = Network.buildBaseNode("Fido", SemanticHierarchy.getSemantic("Proposition"));
		Node dog = Network.buildBaseNode("Dog", SemanticHierarchy.getSemantic("Proposition"));
		VariableNode X = Network.buildVariableNode("X");
		VariableNode Y = Network.buildVariableNode("Y");
		ArrayList<Wire> wires = new ArrayList<Wire>();
		wires.add(new Wire(Network.getRelation("member"), fido));
		wires.add(new Wire(Network.getRelation("class"), dog));
		Node fidoIsADog = Network.buildMolecularNode(wires, classMemberCF);
		try {
			Controller.addPropToContext("default", fidoIsADog.getId());
		} catch (ContextNameDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wires.clear();
		wires.add(new Wire(Network.getRelation("member"), leo));
		wires.add(new Wire(Network.getRelation("class"), dog));
		Node leoIsADog = Network.buildMolecularNode(wires, classMemberCF);
		wires.clear();
		wires.add(new Wire(Network.getRelation("member"), roger));
		wires.add(new Wire(Network.getRelation("class"), dog));
		Node rogerIsADog = Network.buildMolecularNode(wires, classMemberCF);
		wires.clear();
		wires.add(new Wire(Network.getRelation("member"), X));
		wires.add(new Wire(Network.getRelation("class"), dog));
		Node xIsADog = Network.buildMolecularNode(wires, classMemberCF);
		wires.clear();
		wires.add(new Wire(Network.getRelation("member"), Y));
		wires.add(new Wire(Network.getRelation("class"), dog));
		Node yIsADog = Network.buildMolecularNode(wires, classMemberCF);
		/*
		 * Knowledge Base established : [Dog(Leo), Dog(Fido), Dog(Roger), Dog(X),
		 * Dog(Y)]
		 */

		Substitutions filterSubs = new LinearSubstitutions();
		filterSubs.insert(new Binding(X, fido));

//		Report report = new Report(filterSubs, new PropositionSet(), true, InferenceTypes.BACKWARD);
//		channel.testReportToSend(report);
		((PropositionNode) fidoIsADog).deduce();
	}

}
