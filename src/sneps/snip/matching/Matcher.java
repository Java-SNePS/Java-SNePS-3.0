package sneps.snip.matching;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import sneps.exceptions.CannotBuildNodeException;
import sneps.exceptions.CaseFrameMissMatchException;
import sneps.exceptions.EquivalentNodeException;
import sneps.exceptions.IllegalIdentifierException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.exceptions.RelationDoesntExistException;
import sneps.exceptions.SemanticNotFoundInNetworkException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.SemanticHierarchy;
import sneps.network.classes.Wire;

public class Matcher {

	public static List<Match> match(PropositionNode toBeDeduced) {
		List<Match> listOfMatches = new ArrayList<Match>();
		try {
			Node leo;
			Hashtable<String, Node> nodes = Network.getNodes();
			System.out.println(nodes);
			leo = Network.getNode("Leo");

			VariableNode X = (VariableNode) Network.getNode("X");
			Substitutions filterSubs = new LinearSubstitutions();
			filterSubs.insert(new Binding(X, leo));
			Match newMatch = new Match(filterSubs, new LinearSubstitutions(), leo, 0);

			listOfMatches.add(newMatch);
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listOfMatches;
	}

	public static List<Match> match(PropositionNode propositionNode, Substitutions currentReportSubs) {
		List<Match> listOfMatches = new ArrayList<Match>();
		try {
			Node nodeMatch;
			Match newMatch;
			VariableNode variableNode;
			Hashtable<String, Node> nodes = Network.getNodes();
			System.out.println("Nodes in the Network\n" + nodes + "\n");

			if (propositionNode.getIdentifier().equals("M1")) {
				nodeMatch = Network.getNode("M2");
				newMatch = new Match(new LinearSubstitutions(), new LinearSubstitutions(), nodeMatch, 0);
				listOfMatches.add(newMatch);
				nodeMatch = Network.getNode("P1");
				variableNode = (VariableNode) Network.getNode("X");
				Substitutions filterSubs = new LinearSubstitutions();
				filterSubs.putIn(new Binding(variableNode, nodeMatch));
				newMatch = new Match(filterSubs, new LinearSubstitutions(), nodeMatch, 0);
				listOfMatches.add(newMatch);
			}

		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listOfMatches;
	}

}
