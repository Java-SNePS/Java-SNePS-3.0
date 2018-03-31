package sneps.network;

import sneps.network.cables.UpCable;
import sneps.network.cables.UpCableSet;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Term;
import sneps.setClasses.NodeSet;
import sneps.snebr.Context;
import sneps.snip.channels.Channel;
import sneps.snip.matching.Substitutions;

public class Node {
	
	protected Term term;
	protected Semantic semanticType;
	private static int count=0;
	private int id;
	
	public Node() {	}
	
	public Node(Term trm){
		term = trm;
		id = count++;		
	}

	public Node(Semantic sem){
		semanticType = sem;
		id = count++;
	}
	
	public Node(Semantic sem, Term trm){
		semanticType = sem;
		term = trm;
		id = count++;
	}
	

	/**
	 *
	 * @return the instance of term class representing the term type
	 *         of the current node.
	 */
	public Term getTerm() {
		return this.term;
	}

	/**
	 *
	 * @return the instance of semantic class representing the semantic type of
	 *         the current node.
	 */
	public Semantic getSemantic() {
		return this.semanticType;
	}

	/**
	 *
	 * @return the simple name of term class representing the term
	 *         type of the current node.
	 */
	public String getSyntacticType() {
		return this.term.getClass().getSimpleName();
	}

	/**
	 *
	 * @return the simple name of the super class of the term class
	 *         representing the term type of the current node.
	 */
	public String getSyntacticSuperClass() {
		return this.term.getClass().getSuperclass().getSimpleName();
	}


	/**
	 *
	 * @return the simple name of the super class of the semantic class
	 *         representing the semantic type of the current node.
	 */
	public String getSemanticSuperClass() {
		return this.term.getClass().getSuperclass().getSimpleName();
	}

	/**
	 *
	 * @return the name or the label of the current node.
	 */
	public String getIdentifier() {
		return this.term.getIdentifier();
	}

	/**
	 *
	 * @return the up cable set of the current node.
	 */
	public UpCableSet getUpCableSet() {
		return this.term.getUpCableSet();
	}

	/**
	 *
	 * @return a node set containing all the parent nodes of the current node.
	 *         (whether direct or indirect parent nodes.)
	 */
	public NodeSet getParentNodes() {
		return this.term.getParentNodes();
	}

	/**
	 * This method overrides the default toString method inherited from the
	 * Object class.
	 */
	@Override
	public String toString() {
		return this.term.toString();
	}

	/**
	 * This method overrides the default equals method inherited from the Object
	 * class.
	 *
	 * @param obj
	 *            an Object that is to be compared to the current node to check
	 *            whether they are equal.
	 *
	 * @return true if the given object is an instance of the Node class and has
	 *         the same name as the current node, and false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().getSimpleName().contains("Node"))
			return false;
		if (!this.getIdentifier().equals(((Node) obj).getIdentifier()))
			return false;
		return true;
	}

	public void receiveRequest(Channel newChannel) {
		// TODO Auto-generated method stub
		
	}
	public void processReports() {
		// TODO Auto-generated method stub
		
	}
	public void processRequests() {
		// TODO Auto-generated method stub
		
	}
	
	public NodeSet getDominatingRules() {
		NodeSet ret = new NodeSet();
		UpCable consequentCable = this.getUpCableSet().getUpCable("cq");
		UpCable argsCable = this.getUpCableSet().getUpCable("arg");
		UpCable antCable = this.getUpCableSet().getUpCable("&ant");
		UpCable doCable = this.getUpCableSet().getUpCable("doo");
		UpCable ifCable = this.getUpCableSet().getUpCable("iff");
		UpCable whenCable = this.getUpCableSet().getUpCable("when");
		if (consequentCable != null) {
			ret.addAll(consequentCable.getNodeSet());
		}
		if (argsCable != null) {
			ret.addAll(argsCable.getNodeSet());
		}
		if (antCable != null) {
			ret.addAll(antCable.getNodeSet());
		}
		if (doCable != null) {
			ret.addAll(doCable.getNodeSet());
		}
		if (ifCable != null) {
			ret.addAll(ifCable.getNodeSet());
		}
		if (whenCable != null) {
			ret.addAll(whenCable.getNodeSet());
		}
		return ret;
	}
	
	public boolean isWhQuestion(Substitutions sub) {
		/*if (!this.getIdentifier().equalsIgnoreCase("patternnode"))
			return false;

		PatternNode node = (PatternNode) this;
		LinkedList<VariableNode> variables = node.getFreeVariables();

		for (int i = 0; i < variables.size(); i++) {
			Node termNode = sub.term(variables.get(i));
			if (termNode == null || (!termNode.getIdentifier().equalsIgnoreCase("basenode")))
				return true;

		}*/
		return false;
	}

	Context fake() {
		return null;
	}

	public void deduce(Node node) {
		/*Runner.initiate();
		NodeSet dominatingRules = getDominatingRules();
		sendRequests(dominatingRules, channel.getFilter().getSubstitution(),
				channel.getContextID(), ChannelTypes.RuleCons);
		// System.out.println("#$#$#$#$# 1");
		try {
			List<Object[]> matchesReturned = Matcher.Match(this);
			if(matchesReturned != null) {
				ArrayList<Pair> matches = new ArrayList<Pair>();
				for(Object[] match : matchesReturned) {
					Pair newPair = new Pair((Substitutions)match[1], (Substitutions)match[2], (Node)match[0]);
					matches.add(newPair);
				}
				sendRequests(matches, channel.getContextID(), ChannelTypes.MATCHED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Runner.run();
		// what to return here ?*/
	}

	public void setTerm(Term term) {
		this.term = term;
	}
	public Semantic getSemanticType() {
		return semanticType;
	}
	public void setSemanticType(Semantic semanticType) {
		this.semanticType = semanticType;
	}
	public static int getCount() {
		return count;
	}
	public static void setCount(int count) {
		Node.count = count;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isTemp() {
		return this.term.isTemp();
	}
	public void setTemp(boolean temp) {
		this.term.setTemp(temp);
	}


	
}
