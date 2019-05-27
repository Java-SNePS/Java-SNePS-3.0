package sneps.snip;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

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

	public static void main(String[] args) throws Exception {
		Semantic.createDefaultSemantics();

		LinkedList<Relation> relationSet = new LinkedList<Relation>();
		relationSet.add(Network.defineRelation("member", "Proposition"));
		relationSet.add(Network.defineRelation("class", "Proposition"));
		CaseFrame caseFrame = Network.defineCaseFrame("Proposition", relationSet);
		Node leo = Network.buildBaseNode("Leo", SemanticHierarchy.getSemantic("Proposition"));
		Node fido = Network.buildBaseNode("Fido", SemanticHierarchy.getSemantic("Proposition"));
		Node dog = Network.buildBaseNode("Dog", SemanticHierarchy.getSemantic("Proposition"));
		VariableNode X = Network.buildVariableNode("X");
		ArrayList<Wire> wires = new ArrayList<Wire>();
		wires.add(new Wire(Network.getRelation("member"), fido));
		wires.add(new Wire(Network.getRelation("class"), dog));
		Node fidoIsADog = Network.buildMolecularNode(wires, caseFrame);
		wires.clear();
		wires.add(new Wire(Network.getRelation("member"), leo));
		wires.add(new Wire(Network.getRelation("class"), dog));
		wires.clear();
		wires.add(new Wire(Network.getRelation("member"), X));
		wires.add(new Wire(Network.getRelation("class"), dog));

		Substitutions filterSubs = new LinearSubstitutions();
		filterSubs.insert(new Binding(X, fido));

//		Node leoIsADog = Network.buildMolecularNode(wires, caseFrame);
		Node xIsADog = Network.buildMolecularNode(wires, caseFrame);
		Channel channel = ((PropositionNode) xIsADog).establishChannel(ChannelTypes.MATCHED, fidoIsADog, null,
				filterSubs, Controller.getCurrentContextName(), 0);

		Report report = new Report(filterSubs, new PropositionSet(), true, InferenceTypes.BACKWARD);
		channel.testReportToSend(report);
		((PropositionNode) fidoIsADog).deduce();
		ArrayList<Wire> wires1 = new ArrayList<Wire>();
		wires1.add(new Wire(Network.getRelation("member"), X));
		wires1.add(new Wire(Network.getRelation("class"), dog));

		Node XIsADog = Network.buildMolecularNode(wires1, caseFrame);
	}

}
