package tests;

import java.util.LinkedList;

import org.junit.Test;

import junit.framework.TestCase;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.classes.Relation;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.Molecular;
import sneps.snebr.Controller;
import sneps.snepslog.AP;

public class SnepslogTest extends TestCase {

	public void testWffDot() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		Network.defineDefaults();
		AP.executeSnepslogCommand("dog(Fido).");
		PropositionSet ps = Controller.getCurrentContext().allAsserted();
		boolean success = false;
		for (int i = 0; i < PropositionSet.getPropsSafely(ps).length; i++) {
			Node n = Network.getNodeById(PropositionSet.getPropsSafely(ps)[i]);
			if (n.getTerm() instanceof Molecular) {
				Molecular m = (Molecular)n.getTerm();
				LinkedList<Relation> rels = m.getDownCableSet().getCaseFrame().getRelations();
				if(rels.size()==2&&rels.get(0).getName().equals("r")&&rels.get(1).getName().equals("a1")) {
					Node dog = Network.getNode("dog");
					Node Fido = Network.getNode("Fido");
					if(m.getDownCableSet().getDownCable("r").getNodeSet().contains(dog)&&m.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)){
						success = true;
					}
				}
			}
		}
		if(!success) {
			fail("dog(Fido) assertion failed!");
		}
	}

}
