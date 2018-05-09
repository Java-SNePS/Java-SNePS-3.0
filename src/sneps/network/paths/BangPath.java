/**
 * @className BangPath.java
 * 
 * @ClassDescription This class is used to create a special instance of 
 * 	Path that requires the proposition to be asserted in the given context, 
 * 	i.e. if a segment of a path requires all propositions in it to be asserted
 *	then this segment is added as a BangPath. This class extends Path.
 * 
 * @author Nourhan Zakaria
 * @version 2.00 18/6/2014
 */
package sneps.network.paths;

import java.io.Serializable;
import java.util.LinkedList;

import sneps.network.classes.PathTrace;
import sneps.network.classes.Relation;
import sneps.network.Node;
import sneps.snebr.Context;

public class BangPath extends Path implements Serializable{

	/** (non-Javadoc)
     * @see sneps.Paths.Path#follow(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public LinkedList<Object[]> follow(Node node, PathTrace trace, Context context) {
		/*
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		// check it's proposition and it's asserted
		if ((node.getSemanticType().getSuperClassesNames().contains("Proposition") ||
				node.getSemanticType().getClass().getSimpleName().equals("Proposition")) &&
					context.getHypothesisSet().propositions.contains(node.getSemanticType())
		)
		{
			PathTrace pt = trace.clone();
			pt.addSupport(node);
			// add the pair to the result
			Object[] o = new Object[2];
			o[0] = node;
			o[1] = pt;
			result.add(o);
		}
		return result;*/return null;
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#followConverse(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public LinkedList<Object[]> followConverse(Node node, PathTrace trace, Context context) {
		/*
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		if ((node.getSemanticType().getSuperClassesNames().contains("Proposition") ||
				node.getSemanticType().getClass().getSimpleName().equals("Proposition")) &&
					context.getHypothesisSet().propositions.contains(node.getSemanticType()))
		{
			PathTrace pt = trace.clone();
			pt.addSupport(node);
			// add the pair to the result
			Object[] o = new Object[2];
			o[0] = node;
			o[1] = pt;
			result.add(o);
		}
		return result;*/return null;
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#clone(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public BangPath clone() {
		return new BangPath();
	}
	
	/** 
     * This method overrides the toString method inherited from the 
     * Object class.
     */
	@Override
	public String toString(){
		return "!";
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#equals(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public boolean equals(Object obj) {
		return obj.getClass().getSimpleName().equals("BangPath");
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#converse(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public Path converse() {
		return this;
	}

	public LinkedList<Relation> firstRelations(){
		return new LinkedList<Relation>();
		//TODO
	}
}
