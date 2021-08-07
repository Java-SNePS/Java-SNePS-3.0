/**
 * @className KPlusPath.java
 * 
 * @ClassDescription A k-plus path exists between two nodes A and B, 
 * 	if B can be reached after the following the path specified in the 
 * 	k-plus path one or more times starting at A. This class extends 
 * 	Path.
 * 
 * @author Nourhan Zakaria
 * @version 2.00 18/6/2014
 */
package sneps.network.paths;

import java.io.Serializable;
import java.util.LinkedList;

import sneps.snebr.Context;
import sneps.network.classes.PathTrace;
import sneps.network.classes.Relation;
import sneps.network.Node;

public class KPlusPath extends Path implements Serializable{
	
	/**
	 * a path that is to be followed one or more times until no more 
	 * new nodes can be reached by following it again. 
	 * This path can be of any type.
	 */
	private Path path;
	
	/**
	 * The constructor of this class.
	 * 
	 * @param path
	 * 			the path defined in the k-plus
	 * 			path.
	 */
	public KPlusPath(Path path){
		this.path = path;
	}
	
	/**
	 * 
	 * @return the path specified in the current
	 * 	k-plus path.
	 */
	public Path getPath(){
		return this.path;
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#follow(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public LinkedList<Object[]> follow(Node node, PathTrace trace, Context context) {
		
		 LinkedList<Object[]> temp = this.path.follow(node,trace,context);
         
         return follow(temp,context);
	}

	/**
     * A helper follow method. It follows the current path starting at  
     * the nodes in the list of pairs (temp) in the given context.
     *
     * @param temp 
     * 			a LinkedList of Node-PathTrace pairs.
     * @param context 
     * 			the context that propositions in this path are asserted in.
     * 
     * @return a LinkedList of Node-PathTrace pairs resulted from following the path 
     * starting at all nodes in temp until no more nodes are reachable.
     */
	private LinkedList<Object[]> follow(LinkedList<Object[]> temp,Context context)
    {
        LinkedList<Object[]> result = new LinkedList<Object[]>();
        for(int i=0;i<temp.size();i++)
        {
            Object[] o = temp.get(i);
            Node node = (Node) o[0];
            PathTrace trace = (PathTrace) o[1];
            LinkedList<Object[]> f = this.path.follow(node,trace,context);
            result.addAll(f);
        }
        while(! result.isEmpty())
        {
            for(int i=0;i<temp.size();i++)
            {
                Object[] ob1 = temp.get(i);
                Node n1 = (Node) ob1[0];
                PathTrace pt1 = (PathTrace) ob1[1];
                for(int j=0;j<result.size();j++)
                {
                    Object[] ob2 = result.get(j);
                    Node n2 = (Node) ob2[0];
                    PathTrace pt2 = (PathTrace) ob2[1];
                    if(n1.equals(n2))
                    {
                        if(! pt1.getSupports().equals(pt2.getSupports()))
                                temp.add(ob2);
                        result.remove(j);
                        j--;
                    }
                }
            }
            temp.addAll(result);
            result = follow(result,context);
        }
        return temp;
    }

	
	/** (non-Javadoc)
     * @see sneps.Paths.Path#followConverse(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public LinkedList<Object[]> followConverse(Node node, PathTrace trace, Context context) {
		
		LinkedList<Object[]> temp = this.path.followConverse(node,trace,context);
        
        return followConverse(temp,context);
	}

	/**
     * A helper followConverse method. It follows the converse of the 
     * current path starting at nodes in the list of pairs (temp) in
     * the given context.
     *
     * @param temp 
     * 			a LinkedList of Node-PathTrace pairs.
     * @param context 
     * 			the context that propositions in this path are asserted in.
     * 
     * @return a LinkedList of Node-PathTrace pairs resulted from following the converse of
     * the path starting at all nodes in temp until no more nodes are reachable.
     */
	private LinkedList<Object[]> followConverse(LinkedList<Object[]> temp,Context context)
    {
        LinkedList<Object[]> result = new LinkedList<Object[]>();
        for(int i=0;i<temp.size();i++)
        {
            Object[] o = temp.get(i);
            Node node = (Node) o[0];
            PathTrace trace = (PathTrace) o[1];
            LinkedList<Object[]> f = this.path.followConverse(node,trace,context);
            result.addAll(f);
        }
        while(! result.isEmpty())
        {
            for(int i=0;i<temp.size();i++)
            {
                Object[] ob1 = temp.get(i);
                Node n1 = (Node) ob1[0];
                PathTrace pt1 = (PathTrace) ob1[1];
                for(int j=0;j<result.size();j++)
                {
                    Object[] ob2 = result.get(j);
                    Node n2 = (Node) ob2[0];
                    PathTrace pt2 = (PathTrace) ob2[1];
                    if(n1.equals(n2))
                    {
                        if(! pt1.getSupports().equals(pt2.getSupports()))
                                temp.add(ob2);
                        result.remove(j);
                        j--;
                    }
                }
            }
            temp.addAll(result);
            result = followConverse(result,context);
        }
        return temp;
    }
	
	/** (non-Javadoc)
     * @see sneps.Paths.Path#clone(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public KPlusPath clone() {
		return new KPlusPath(this.path.clone());
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#equals(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public boolean equals(Object obj) {
		if (! obj.getClass().getSimpleName().equals("KPlusPath"))
			return false;
		KPlusPath kPath = (KPlusPath) obj;
		if(! kPath.getPath().equals(this.path))
			return false;
		return true;
	}
	
	/**
	 * This method overrides the toString method inherited from
	 * the Object class.
	 */
	@Override
	public String toString(){
		return "kplus("+this.path.toString()+")";
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#converse(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public Path converse() {
		return new KPlusPath(path.converse());
	}

	@Override
	public LinkedList<Relation> firstRelations() {
		return path.firstRelations();
	}
	
}
