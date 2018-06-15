package tests;
//Asssert, set of set of set, Cycles{Assumption, Tree}, Removing hwa el by update the structure
import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import sneps.exceptions.CannotInsertJustificationSupportException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.IllegalIdentifierException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.PropositionSet;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SupportTest {
	
	Network net;
	Semantic sem;
	final static String semanticType = "Proposition";
	
	PropositionNode n0;
	PropositionNode n1;
	PropositionNode n2;
	PropositionNode n3;
	PropositionNode n4;
	PropositionNode n5;
	PropositionNode n6;
	PropositionNode n7;
	PropositionNode n8;
	PropositionNode n9;
	PropositionNode n10;
	PropositionNode n11;
	PropositionNode n12;
	PropositionNode n13;
	PropositionNode n14;

	
	 @Before
	 
	    public void setUp() throws NodeNotFoundInNetworkException, NotAPropositionNodeException, IllegalIdentifierException{
		 	//Defining Semantic Type as PropositionNode
	    	sem = new Semantic(semanticType);
	    	net = new Network();
	    	
	    	//Building Network Nodes
	    	//The Network Nodes Labels and Corresponding Ids
	    	net.buildBaseNode("s", sem);// 0
			net.buildBaseNode("p", sem);// 1
			net.buildBaseNode("q", sem);// 2
			net.buildBaseNode("r", sem);// 3
			net.buildBaseNode("m", sem);// 4
			net.buildBaseNode("n", sem);// 5
			net.buildBaseNode("v", sem);// 6
			net.buildBaseNode("z", sem);// 7
			net.buildBaseNode("a", sem);// 8
			net.buildBaseNode("b", sem);// 9
			net.buildBaseNode("c", sem);// 10
			net.buildBaseNode("d", sem);// 11
			net.buildBaseNode("e", sem);// 12
			net.buildBaseNode("f", sem);// 13
			net.buildBaseNode("g", sem);// 14
			
			
			//Getting the Network PropositionNodes
			 n0 = (PropositionNode) net.getNode("s");
			 n1 = (PropositionNode) net.getNode("p");
			 n2 = (PropositionNode) net.getNode("q");
			 n3 = (PropositionNode) net.getNode("r");
			 n4 = (PropositionNode) net.getNode("m");
			 n5 = (PropositionNode) net.getNode("n");
			 n6 = (PropositionNode) net.getNode("v");
			 n7 = (PropositionNode) net.getNode("z");
			 n8 = (PropositionNode) net.getNode("a");
			 n9 = (PropositionNode) net.getNode("b");
			 n10 = (PropositionNode) net.getNode("c");
			 n11 = (PropositionNode) net.getNode("d");
			 n12 = (PropositionNode) net.getNode("e");
			 n13 = (PropositionNode) net.getNode("f");
			 n14 = (PropositionNode) net.getNode("g");
			 
			 //Setting Specific Nodes to be Hyps. So that no support are needed for this node.
			 //If node is not set, it is considers as Derived node.
			 n2.setHyp(true);
			 n4.setHyp(true);
			 n5.setHyp(true);
			 n6.setHyp(true);
			 n7.setHyp(true);
			 n9.setHyp(true);
			 n10.setHyp(true);
			 n11.setHyp(true);
			 n12.setHyp(true);
			 n13.setHyp(true);
			 n14.setHyp(true);
			
	    }
	
	 
	 /*
	  * Checking The Assumptions of a Node when it is initially Created.
	  * The Hyp Node once created must contain itself as Assumption Support.
	  * Expected The Hashtable “assumptions” is equal to the set{{2}}.
	  */
	 @Test
	 
	    public void AInitialAssumptionBasedSupports() throws NotAPropositionNodeException, NodeNotFoundInNetworkException{
	     	Hashtable<String, PropositionSet> assumptions = new Hashtable<String, PropositionSet>();
	     	assumptions.put("2", new PropositionSet(n2.getId()));
	        assertEquals(n2.getAssumptionBasedSupport(), assumptions);
	        
	        assumptions = new Hashtable<String, PropositionSet>();
	        assumptions.put("11", new PropositionSet(n11.getId()));
	        assertEquals(n11.getAssumptionBasedSupport(), assumptions);
	        
	    }
	 
	 /*
	  *When the node is initially created. The Supports Tree must only contain this node id only.
	  *Expected "mySupportsTree” structure is equal to the{{<0>}}
	  */
	 @Test
	    public void BInitialSupportsTree() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		 //Checking the supports tree structure of node “0”.
		 	ArrayList<ArrayList<ArrayList<Integer>>> mySupportsTree = new ArrayList<ArrayList<ArrayList<Integer>>>();
		 	ArrayList<ArrayList<Integer>> branch = new ArrayList<ArrayList<Integer>>();
		 	ArrayList<Integer> intialTree = new ArrayList<Integer>();
			intialTree.add(0);
			branch.add(intialTree);
			mySupportsTree.add(branch);
	        assertEquals(n0.getMySupportsTree(), mySupportsTree);
	        
	    }
	 
	 /*
	  * Checking The Justification of a Node when we insert some amount of sets as it's JustificationSupports
	  * 						
	  * 						0
	  * 				       / \ 
	  * 					  /   \->  13 | 14     
	  * 			     1 |2 | 3   
	  *                 /\       \
	  *                /  \       \
	  *              4|5  6|7     8|9 
	  *              			 /
	  *              			/
	  *              	  10 | 11 | 12
	  * 
	  * 
	  * Expected to Have the following set as the JustificationBased Support of Node 0 <<{1,2,3}, {13,14}>>
	  * Expected to Have the following set as the JustificationBased Support of Node 1 <<{4,5}, {6,7}>>
	  */
	 @Test
	    public void CStructuredJustificationBasedSupports() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, NodeNotFoundInPropSetException, DuplicatePropositionException, CannotInsertJustificationSupportException{
		 //The Set of Justifications We will use
		 	int[] pqr = new int[3];
			pqr[0] = 1;
			pqr[1] = 2;
			pqr[2] = 3;
			
			int[] mn = new int[2];
			mn[0] = 5;
			mn[1] = 4;
			
			int[] vz = new int[2];
			vz[0] = 6;
			vz[1] = 7;
			
			int[] ab = new int[2];
			ab[0] = 9;
			ab[1] = 8;
			
			int[] cde = new int[3];
			cde[0] = 10;
			cde[1] = 11;
			cde[2] = 12;
			
			int[] fg = new int[2];
			fg[0] = 14;
			fg[1] = 13;
			
		//Creating Proposition Sets by the previous Arrays of int
			PropositionSet s1 = new PropositionSet(pqr);
			PropositionSet s2 = new PropositionSet(mn);
			PropositionSet s3 = new PropositionSet(vz);
			PropositionSet s4 = new PropositionSet(ab);
			PropositionSet s5 = new PropositionSet(cde);
			PropositionSet s6 = new PropositionSet(fg);
			
		//Construct the tree "Bottum-Up" See the Graph above the method to imagine the Support Structure!
			n1.addJustificationBasedSupport(s2);
			n1.addJustificationBasedSupport(s3);
			n8.addJustificationBasedSupport(s5);
			n3.addJustificationBasedSupport(s4);
			n0.addJustificationBasedSupport(s6);
			n0.addJustificationBasedSupport(s1);
			
			//Retrieving the justification supports of node 0.
	     	Hashtable<String, PropositionSet> justifications = new Hashtable<String, PropositionSet>();
	     	justifications.put("1,2,3,", new PropositionSet(pqr));
	     	justifications.put("13,14,", new PropositionSet(fg));
	        assertEquals(n0.getJustificationSupport(), justifications);
	        
	      //Retrieving the justification supports of node 1.
	        justifications = new Hashtable<String, PropositionSet>();
	        justifications.put("4,5,", new PropositionSet(mn));
	        justifications.put("6,7,", new PropositionSet(vz));
	        assertEquals(n1.getJustificationSupport(), justifications);
	        
	    }
	 
	 /*
	  * Checking The Assumptions of a Node when we insert some amount of sets as it's JustificationSupports
	  * 						
	  * 						0
	  * 				       / \ 
	  * 					  /   \->  13 | 14     
	  * 			     1 |2 | 3   
	  *                 /\       \
	  *                /  \       \
	  *              4|5  6|7     8|9 
	  *              			 /
	  *              			/
	  *              	  10 | 11 | 12	  * 
	  * 
	  * 
	  * Expected to Have the following set as the AssumptionBased Support  <<{2,4,5,9,10,11,12} , {2,6,7,9,10,11,12} , {13,14}>>
	  */
	 @Test
	    public void DStructuredAssumptionBasedSupports() throws NotAPropositionNodeException, NodeNotFoundInNetworkException{
		//Checking the assumption supports of node 0.
	     	Hashtable<String, PropositionSet> assumptions = new Hashtable<String, PropositionSet>();
	     	int[] firstAssumption = new int[7];
	     	firstAssumption[0] = 2;
	     	firstAssumption[1] = 4;
	     	firstAssumption[2] = 5;
	     	firstAssumption[3] = 10;
	     	firstAssumption[4] = 9;
	     	firstAssumption[5] = 11;
	     	firstAssumption[6] = 12;
	     	
	     	int[] secondAssumption = new int[7];
	     	secondAssumption[0] = 2;
	     	secondAssumption[1] = 6;
	     	secondAssumption[2] = 7;
	     	secondAssumption[3] = 10;
	     	secondAssumption[4] = 9;
	     	secondAssumption[5] = 11;
	     	secondAssumption[6] = 12;
	     	
	     	int[] thirdAssumption = new int[2];
	     	thirdAssumption[0] = 13;
	     	thirdAssumption[1] = 14;
	     	
	     	assumptions.put("2,4,5,9,10,11,12,", new PropositionSet(firstAssumption));
	     	assumptions.put("2,6,7,9,10,11,12,", new PropositionSet(secondAssumption));
	     	assumptions.put("13,14,", new PropositionSet(thirdAssumption));
	        assertEquals(n0.getAssumptionBasedSupport(), assumptions);
	        
	    }
	 /*
	  * Checking The Supports Tree Hierarchy of a Node when we insert some amount of sets as it's JustificationSupports
	  * 						
	  * 						0
	  * 				       / \ 
	  * 					  /   \->  13 | 14     
	  * 			     1 |2 | 3   
	  *                 /\       \
	  *                /  \       \
	  *              4|5  6|7     8|9 
	  *              			 /
	  *              			/
	  *              	  10 | 11 | 12	  * 
	  * 
	  * 
	  * Expected to Have the following set as the Support Tree Structure “mySupportsTree” 
	  * {{<2,0>,<4,1,0>,<5,1,0>,<9,3,0>,<10,8,3,0>,<11,8,3,0>,<12,8,3,0>},{<2,0>,<6,1,0>,<7,1,0>,<9,3,0>,<10,8,3,0>,<11,8,3,0>,<12,8,3,0>},{<13,0>,<14,0>}}.
	  */
	 @Test
	    public void EStructuredSupportsTree() throws NotAPropositionNodeException, NodeNotFoundInNetworkException{
		 ArrayList<ArrayList<ArrayList<Integer>>> mySupportsTree = new ArrayList<ArrayList<ArrayList<Integer>>>();
		 
		 	
			//Constructing the List {<2,0>,<6,1,0>,<7,1,0>,<9,3,0>,<10,8,3,0>,<11,8,3,0>,<12,8,3,0>}.
		 	ArrayList<ArrayList<Integer>> branch = new ArrayList<ArrayList<Integer>>();
		 	ArrayList<Integer> intialTree = new ArrayList<Integer>();
			intialTree.add(2);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(6);intialTree.add(1);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(7);intialTree.add(1);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(9);intialTree.add(3);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(10);intialTree.add(8);intialTree.add(3);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(11);intialTree.add(8);intialTree.add(3);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(12);intialTree.add(8);intialTree.add(3);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			mySupportsTree.add(branch);
			branch = new ArrayList<ArrayList<Integer>>();

			//Constructing the List {<2,0>,<4,1,0>,<5,1,0>,<9,3,0>,<10,8,3,0>,<11,8,3,0>,<12,8,3,0>}.
		 	branch = new ArrayList<ArrayList<Integer>>();
		 	intialTree = new ArrayList<Integer>();
			intialTree.add(2);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(4);intialTree.add(1);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(5);intialTree.add(1);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(9);intialTree.add(3);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(10);intialTree.add(8);intialTree.add(3);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(11);intialTree.add(8);intialTree.add(3);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(12);intialTree.add(8);intialTree.add(3);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			mySupportsTree.add(branch);
			branch = new ArrayList<ArrayList<Integer>>();

			
			//Constructing the List {<13,0>,<14,0>}.
		 	branch = new ArrayList<ArrayList<Integer>>();
		 	intialTree = new ArrayList<Integer>();
			intialTree.add(13);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(14);intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			mySupportsTree.add(branch);
			branch = new ArrayList<ArrayList<Integer>>();
			
	        assertEquals(n0.getMySupportsTree(), mySupportsTree);
	        
	    }
	 
	 /*
	  * Consider We need To delete the node 1 from the Network (IntermidateNode)
	  *						
	  * 						0											
	  * 				       / \ 
	  * 					  /   \->  13 | 14     
	  * 			     1 |2 | 3   
	  *                  /\      \
	  *                 /  \      \
	  *               4|5  6|7    8|9 
	  *              			 /
	  *              			/
	  *              	  10 | 11 | 12	  
	  * 
	  *Expected to remove the whole support 1| 2| 3 From the structure. So the Justification Support of the Node 0 will be <{13, 14}> Only
	  */
	 @Test
	    public void FremoveIntermidateNodeJustification() throws NotAPropositionNodeException, NodeNotFoundInNetworkException{
		 //Because the removal of any node only reflects on it's parent supports.
		 //Therefore the removal of n1 will only reflect on the supports structures of n0.
		 
		 //Getting parent supports of n1.
		 ArrayList<Integer> parents = n1.getParentSupports();
		 
		 //Loop over parent supports and delete n1 from them.
		 for (int i : parents) {
			 PropositionNode nx = (PropositionNode) Network.getNodeById(i);
			//Deleting n1 from the supports of nx "n0 in this example"
			 nx.removeNodeFromSupports(n1);
		}
		 
		 Hashtable<String, PropositionSet> justifications = new Hashtable<String, PropositionSet>();
		 int[] res = new int[2];
		 res[0] = 13;
		 res[1] = 14;
		 justifications.put("13,14,", new PropositionSet(res));
		 
		 assertEquals(n0.getJustificationSupport(), justifications);
	    }
	 
	 /*
	  * Consider We need To delete the node 1 from the Network (IntermidateNode)
	  *						
	  * 						0											
	  * 				       / \ 
	  * 					  /   \->  13 | 14     
	  * 			     1 |2 | 3   
	  *                  /\      \
	  *                 /  \      \
	  *               4|5  6|7    8|9 
	  *              			 /
	  *              			/
	  *              	  10 | 11 | 12	  
	  * 
	  *Expected to remove the whole support 1| 2| 3 From the structure. So the Supports Tree of the Node 0 will be {{<13,0>,<14,0>}} Only
	  */
	 @Test
	    public void GremoveIntermidateNodeTree() throws NotAPropositionNodeException, NodeNotFoundInNetworkException{ 
		 
		 //Node is already removed from the previous method.
		 	ArrayList<ArrayList<ArrayList<Integer>>> mySupportsTree = new ArrayList<ArrayList<ArrayList<Integer>>>();
		 	ArrayList<ArrayList<Integer>> branch = new ArrayList<ArrayList<Integer>>();
		 	ArrayList<Integer> intialTree = new ArrayList<Integer>();
			intialTree.add(13);
			intialTree.add(0);
			branch.add(intialTree);
			intialTree = new ArrayList<Integer>();
			intialTree.add(14);
			intialTree.add(0);
			branch.add(intialTree);
			mySupportsTree.add(branch);
			assertEquals(n0.getMySupportsTree(), mySupportsTree);
	    }
	 /*
	  * Consider We need To delete the node 12 from the Network (LeafNode)
	  *						
	  * 						0											
	  * 				       / \ 
	  * 					  /   \->  13 | 14     
	  * 			     1 |2 | 3   
	  *                  /\      \
	  *                 /  \      \
	  *               4|5  6|7    8|9 
	  *              			 /
	  *              			/
	  *              	  10 | 11 | 12	  
	  * 
	  *Expected to remove the whole support 10| 11| 12 From the structure. 
	  *So the Justification Support of the Node 8 will be Empty.
	  *And because Node 8 is not a Hyp. Therefore Now it got no supports. the set <8,9> will be removed also.
	  *And because Node 3 is not a Hyp. Therefore Now it got no supports. the set <1,2,3> will be removed also.
	  *Therefore the Justification of Node 0 will be the set <13,14> only
	  */
	 @Test
	    public void HremoveLeafNodeJustification12() throws NodeNotFoundInPropSetException, NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException, CannotInsertJustificationSupportException{ 
	  //Because n1 was removed in the previous method. we need to return the set 1 | 2 | 3 to the hierarchy again..
		 int[] pqr = new int[3];
			pqr[0] = 1;
			pqr[1] = 2;
			pqr[2] = 3;
			
		 n0.addJustificationBasedSupport(new PropositionSet(pqr));
		 
		//Because the removal of any node only reflects on it's parent supports.
		 //Therefore the removal of n12 will only reflect on the supports structures of n0, n8, and n3.
		 
		 //Getting parent supports of n12.
		 ArrayList<Integer> parents = n12.getParentSupports();
		 
		 //Loop over parent supports and delete n12 from them.
		 for (int i : parents) {
			 PropositionNode nx = (PropositionNode) Network.getNodeById(i);
			//Deleting n1 from the supports of nx "n8 then n3 then n0 in this example"
			 nx.removeNodeFromSupports(n12);
		}
		 
		 Hashtable<String, PropositionSet> justifications = new Hashtable<String, PropositionSet>();
		 int[] res = new int[2];
		 res[0] = 13;
		 res[1] = 14;
		 justifications.put("13,14,", new PropositionSet(res));
		 
		 assertEquals(n0.getJustificationSupport(), justifications);
	     
	 }
	 
	 /*
	  * Consider We need To delete the node 12 from the Network (LeafNode)
	  *						
	  * 						0											
	  * 				       / \ 
	  * 					  /   \->  13 | 14     
	  * 			     1 |2 | 3   
	  *                  /\      \
	  *                 /  \      \
	  *               4|5  6|7    8|9 
	  *              			 /
	  *              			/
	  *              	  10 | 11 | 12	  
	  * 
	  *Expected to remove the whole support 10| 11| 12 From the structure.
	  *Expected to remove the whole support 8 | 9 From the structure.
	  *Expected to remove the whole support 1 | 2 | 3 From the structure.
	  * So the Assumption Support of the Node 0 will be 13 | 14 only
	  */
	 @Test
	    public void IremoveLeafNodeAssumption12() throws NotAPropositionNodeException, NodeNotFoundInNetworkException{ 
		 //We already removed node 12 in the previous method.
		 //Working on that to check n0 assumption supports.
		 
		 Hashtable<String, PropositionSet> assumptions = new Hashtable<String, PropositionSet>();
		 int[] res = new int[2];
		 res[0] = 13;
		 res[1] = 14;
		 assumptions.put("13,14,", new PropositionSet(res));
		 
		 assertEquals(n0.getAssumptionBasedSupport(), assumptions);
	     
	    }
	 
	 /*
	  * Consider We need To delete the node 6 from the Network (LeafNode)
	  *						
	  * 						0											
	  * 				       / \ 
	  * 					  /   \->  13 | 14     
	  * 			     1 |2 | 3   
	  *                  /\      \
	  *                 /  \      \
	  *               4|5  6|7    8|9 
	  *              			 /
	  *              			/
	  *              	  10 | 11 | 12	  
	  * 
	  *Expected to remove the whole support 6 | 7 From the structure.
	  *Not Expected to remove the whole support 1 | 2 | 3 From the structure. As Node 1 will still be supported by set 4|5
	  * So the Justification Support of the Node 0 will be <<1 | 2 | 3>, <13 | 14>>
	  */
	 @Test
	    public void JremoveLeafNodeJustificationSupport6() throws NodeNotFoundInPropSetException, NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException, CannotInsertJustificationSupportException{ 
		 //Because we have removed node 12 from node 8.
		 //And we have removed node 8 from node 3.
		 //And we have removed node 3 from node 0.
		 //Therefore, we need to return the set <1 | 2 | 3> , <8 | 9> , and <10| 11 | 12> to the hierarchy again..
		 
				 int[] pqr;
				 
				 pqr = new int[3];
					pqr[0] = 10;
					pqr[1] = 11;
					pqr[2] = 12;
					
				 n8.addJustificationBasedSupport(new PropositionSet(pqr));
				 
				 pqr = new int[2];
					pqr[0] = 8;
					pqr[1] = 9;
					
				 n3.addJustificationBasedSupport(new PropositionSet(pqr));
				 
				 pqr = new int[3];
					pqr[0] = 1;
					pqr[1] = 2;
					pqr[2] = 3;
					
				 n0.addJustificationBasedSupport(new PropositionSet(pqr));
				 
				//Because the removal of any node only reflects on it's parent supports.
				 //Therefore the removal of n6 will only reflect on the supports structures of n0, and n1.
				 
				 //Getting parent supports of n6.
				 ArrayList<Integer> parents = n6.getParentSupports();
				 
				 //Loop over parent supports and delete n12 from them.
				 for (int i : parents) {
					 PropositionNode nx = (PropositionNode) Network.getNodeById(i);
					//Deleting n1 from the supports of nx "n8 then n3 then n0 in this example"
					 nx.removeNodeFromSupports(n6);
				}	
				 Hashtable<String, PropositionSet> justifications = new Hashtable<String, PropositionSet>();
				 int[] res;
				 res = new int[3];
				 res[0] = 1;
				 res[1] = 2;
				 res[2] = 3;
				 
				 justifications.put("1,2,3,", new PropositionSet(res));
				 
				 res = new int[2];
				 res[0] = 13;
				 res[1] = 14;
				 
				 justifications.put("13,14,", new PropositionSet(res));
				 
				 assertEquals(n0.getJustificationSupport(), justifications);
				 
	 }
	 
	 /*
	  * Consider We need To delete the node 6 from the Network (LeafNode)
	  *						
	  * 						0											
	  * 				       / \ 
	  * 					  /   \->  13 | 14     
	  * 			     1 |2 | 3   
	  *                  /\      \
	  *                 /  \      \
	  *               4|5  6|7    8|9 
	  *              			 /
	  *              			/
	  *              	  10 | 11 | 12	  
	  * 
	  *Expected to remove the whole support 6 | 7 From the structure.
	  *Not Expected to remove the whole support 1 | 2 | 3 From the structure. As Node 1 will still be supported by set 4|5
	  * So the Assumption Support of the Node 0 will be <<2, 4, 5, 9, 10, 11, 12>, <13 | 14>>
	  */
	 @Test
	    public void KremoveLeafNodeAssumption6() throws NotAPropositionNodeException, NodeNotFoundInNetworkException{ 
		//We already removed node 6 in the previous method.
		 //Working on that to check n0 assumption supports.
		 
		 Hashtable<String, PropositionSet> assumptions = new Hashtable<String, PropositionSet>();
		 int[] res;
		 res = new int[7];
		 res[0] = 2;
		 res[1] = 4;
		 res[2] = 5;
		 res[3] = 9;
		 res[4] = 10;
		 res[5] = 11;
		 res[6] = 12;
		 
		 assumptions.put("2,4,5,9,10,11,12,", new PropositionSet(res));
		 
		 res = new int[2];
		 res[0] = 13;
		 res[1] = 14;
		 
		 assumptions.put("13,14,", new PropositionSet(res));
		 
		 assertEquals(n0.getAssumptionBasedSupport(), assumptions);
	    }
	 
	 
	 /*
	  * Consider the following 2 Tree Structures below
	  *
	  * Direct Cyclic Support:						
	  * 						0											
	  * 				       / \ 
	  * 					  /   \->  13 | 14     
	  * 			  0 |1 |2 | 3   
	  *                  /\      \
	  *                 /  \      \
	  *               4|5  6|7    8|9 
	  *              			 /
	  *              			/
	  *              	  10 | 11 | 12	  
	  * 
	  *Expected to Have an Exception Says we have a cyclic supports, So that the justification support 0 | 1 | 2 | 3 cannot be support for proposition 0
	  *So that the Justification support 0 | 1 | 2 | 3 cannot be added to the justification support of 0
	  *
	  * InDirect Cyclic Support:						
	  * 						0											
	  * 				       / \ 
	  * 					  /   \->  13 | 14     
	  * 			     1 |2 | 3   
	  *                 /\       \
	  *                /  \       \
	  *             0|4|5  6|7     8|9 
	  *              			  /
	  *              			 /
	  *                	  10 | 11 | 12 
	  *              	  
	  * The path containning the cycle 0 |4 |5 will not be included in the supportTree, As it is an invalid path.
	  * Therefore we cannot also start by Node 1 as it is not a Hyp. So it will be deleted too. And the same goes for Node 3.
	  * So support Tree will be <<[13,0],[14,0]>>
	  * N0000: Expected to Have an Exception Says we have a cyclic supports, So that the justification support 0 | 10 | 11 | 12 cannot be support for proposition 0
	  * So that the Justification support 1 | 2 | 3 cannot be added to the justification support of 0
	  */
	 @Rule
	 public ExpectedException thrown = ExpectedException.none();
	 @Test
	    public void LAddDirectCyclicJustificationSupport() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, NodeNotFoundInPropSetException, DuplicatePropositionException, CannotInsertJustificationSupportException, IllegalIdentifierException{
		//Clear all Nodes to implement the new Support Structure Above
		 setUp();
		//1st: Direct Support
		//Build the Direct Support Structure
		//The Set of Justifications We will use
		 	int[] pqr = new int[4];
			pqr[0] = 1;
			pqr[1] = 2;
			pqr[2] = 3;
			pqr[3] = 0;
			
			int[] mn = new int[2];
			mn[0] = 5;
			mn[1] = 4;
			
			int[] vz = new int[2];
			vz[0] = 6;
			vz[1] = 7;
			
			int[] ab = new int[2];
			ab[0] = 9;
			ab[1] = 8;
			
			int[] cde = new int[3];
			cde[0] = 10;
			cde[1] = 11;
			cde[2] = 12;
			
			int[] fg = new int[2];
			fg[0] = 14;
			fg[1] = 13;
			
		//Creating Proposition Sets by the previous Arrays of int
			PropositionSet s1 = new PropositionSet(pqr);
			PropositionSet s2 = new PropositionSet(mn);
			PropositionSet s3 = new PropositionSet(vz);
			PropositionSet s4 = new PropositionSet(ab);
			PropositionSet s5 = new PropositionSet(cde);
			PropositionSet s6 = new PropositionSet(fg);
			
			
		    
		//Construct the tree "Bottum-Up" See the Graph above the method to imagine the Support Structure!
			
			n1.addJustificationBasedSupport(s2);
			n1.addJustificationBasedSupport(s3);
			n8.addJustificationBasedSupport(s5);
			n3.addJustificationBasedSupport(s4);
			n0.addJustificationBasedSupport(s6);
			
			//We Expect that adding Justification Supports bellow will throw CannotInsertJustificationSupportException
			//And The Exception will be thrown when adding the propositionSet [[0, 1, 2, 3]]
			thrown.expect(CannotInsertJustificationSupportException.class);
		    thrown.expectMessage("This PropositionSet contain a Cyclic Supports in the node PropositionSet [props=[0, 1, 2, 3]]");
		    
			
			n0.addJustificationBasedSupport(s1); //This Have to cause the Previous Exception "CannotInsertJustificationSupportException"
			
	        
	    }

	 @Test
	    public void MAddInDirectCyclicJustificationSupport() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, NodeNotFoundInPropSetException, DuplicatePropositionException, CannotInsertJustificationSupportException, InterruptedException, IllegalIdentifierException{
		//Clear all Nodes to implement the new Support Structure Above
		 setUp();
		
		//2nd: InDirect Support
		//Build the InDirect Support Structure
		//The Set of Justifications We will use
		 	int[] pqr = new int[3];
			pqr[0] = 1;
			pqr[1] = 2;
			pqr[2] = 3;
			
			int[] mn = new int[3];
			mn[0] = 5;
			mn[1] = 4;
			mn[2] = 0;
			
			int[] vz = new int[2];
			vz[0] = 6;
			vz[1] = 7;
			
			int[] ab = new int[2];
			ab[0] = 9;
			ab[1] = 8;
			
			int[] cde = new int[3];
			cde[0] = 10;
			cde[1] = 11;
			cde[2] = 12;
			
			
			
			int[] fg = new int[2];
			fg[0] = 14;
			fg[1] = 13;
			
		//Creating Proposition Sets by the previous Arrays of int
			PropositionSet s1 = new PropositionSet(pqr);
			PropositionSet s2 = new PropositionSet(mn);
			PropositionSet s3 = new PropositionSet(vz);
			PropositionSet s4 = new PropositionSet(ab);
			PropositionSet s5 = new PropositionSet(cde);
			PropositionSet s6 = new PropositionSet(fg);
			
			
		    
		//Construct the tree "Bottum-Up" See the Graph above the method to imagine the Support Structure!
			
			n1.addJustificationBasedSupport(s2);
		
			n1.addJustificationBasedSupport(s3);
		
			n8.addJustificationBasedSupport(s5);
	
			n3.addJustificationBasedSupport(s4);
		
			n0.addJustificationBasedSupport(s6);
			
			n0.addJustificationBasedSupport(s1);
			
			
			
			//Getting the justification supports of node 1.
			Hashtable<String,PropositionSet>n1Justs= n1.getJustificationSupport();
			//Getting the justification supports of node 0.
			Hashtable<String,PropositionSet>n0Justs= n0.getJustificationSupport();
			//Getting the assumption supports of node 1.
			Hashtable<String,PropositionSet>n1Assump= n1.getAssumptionBasedSupport();
			//Getting the assumption supports of node 0.
			Hashtable<String,PropositionSet>n0Assump= n0.getAssumptionBasedSupport();
			//Getting the supports Tree of node 0.
			ArrayList<ArrayList<ArrayList<Integer>>>mySupportsTree=n0.getMySupportsTree();
			
			System.out.println(n1Justs.toString());
			System.out.println(n1Assump.toString());
			System.out.println(n0Justs.toString());
			System.out.println(n0Assump.toString());
			System.out.println(mySupportsTree.toString());
	        
	    }


}