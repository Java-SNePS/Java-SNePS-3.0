/**
 * @className Relation.java
 * 
 * @ClassDescription This is the class that represents the labeled, directed arcs 
 * 	that are used to connect the nodes in SNePS network. The class is implemented
 * 	as a 6-tuple (name, type, decoration, limit, path and quantifier).
 * 
 * @author Nourhan Zakaria
 * @version 2.00 18/6/2014
 */
package sneps.network.classes;

import java.io.Serializable;
import java.util.ArrayList;

import sneps.exceptions.CustomException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.paths.Path;

public class Relation implements Serializable {

	public static Relation andAnt, ant, cq, arg, min, max, i, threshMax, thresh, action, obj, precondition, act, when,
			whenever, doo, iff, effect, plan, goal, vars, suchthat, elsee, obj1, obj2, obj3, obj4, obj5, obj6, obj7,
			obj8, obj9, obj10;

	/**
	 * the name (string) that should label any arc representing this relation. Any
	 * relation in SNePS is uniquely identified by its name.
	 */
	private String name;

	/**
	 * the name of the semantic class that represents the semantic type of the nodes
	 * that this relation can point to.
	 */
	private String type;

	/**
	 * the string that represents the decoration of the relation. It can be one
	 * of 7 options: 'reducible', 'expandable','rotary', 'reversible','concatenate', 'symmetric' or 'none'.
	 */

	private ArrayList<String> decoration = new ArrayList<String>();

	/**
	 * the number that represents the minimum number of nodes that this relation can
	 * point to within a down-cable.
	 */
	private int limit;

	/**
	 * the path defined for this relation to be used in path-based inference.
	 */
	private Path path;

	/**
	 * a boolean that tells whether this relation is a quantifier relation (true if
	 * the relation is a quantifier, and false otherwise).
	 */
	private boolean quantifier;

	/**
	 * The constructor of this class.
	 * 
	 * @param n
	 *            a string representing the name of the relation
	 * @param t
	 *            a string representing the name of the semantic type of the nodes
	 *            that this relation can point to.
	 * @param a
	 *            a string representing the decoration of the relations. 'reduce'
	 *            if the relation is reducible. 'expand' if the relation is
	 *            expandable. 'none' if the relation is neither reducible nor
	 *            expandable.
	 * @param l
	 *            an integer representing the limit of the relation.
	 */
	public Relation(String n, String t, ArrayList<String> a, int l) {
		this.name = n;
		this.type = t;
		this.decoration = a;
		this.limit = l;
		this.path = null;
		setQuantifier();
	}

	public Relation(String name, String type) {
		this.name = name;
		this.type = type;
		this.decoration = null;
		this.limit = 1;
		this.path = null;
		setQuantifier();

	}

	/**
	 * @return the name of the current relation.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the name of the semantic type of the nodes that can be pointed to by
	 *         the current relation.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @return the decoration of the current relation. ('reduce', 'expand' or
	 *         'none')
	 */
	public ArrayList<String> getDecoration() {
		return this.decoration;
	}

	/**
	 * @return the limit of the current relation.
	 */
	public int getLimit() {
		return this.limit;
	}

	/**
	 * @return the path defined for the current relation. (can return null if there
	 *         is no defined path for the current relation).
	 */
	public Path getPath() {
		return this.path;
	}

	/**
	 * @param path
	 *            a Path that will be defined for the current relation to be used in
	 *            path-based inference.
	 */
	public void setPath(Path path) {
		this.path = path;
	}

	/**
	 * @return true if the current relation is a quantifier relation, and false
	 *         otherwise.
	 */
	public boolean isQuantifier() {
		return this.quantifier;
	}

	/**
	 * This method sets the boolean quantifier to true if the name of the current
	 * relation represents a quantifier relation.
	 */
	public void setQuantifier() {
		if (name.equals("forall") || name.equals("min") || name.equals("max") || name.equals("thresh")
				|| name.equals("threshmax") || name.equals("emin") || name.equals("emax") || name.equals("etot")
				|| name.equals("pevb") || name.equals("vars")) {
			this.quantifier = true;
		}
	}

	/**
	 * This method overrides the default equals method inherited from the Object
	 * class.
	 * 
	 * @param obj
	 *            an Object that is to be compared to the current relation to check
	 *            whether they are equal.
	 * 
	 * @return true if the given object is an instance of the Relation class and has
	 *         the same name as the current relation, and false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().getSimpleName().equals("Relation"))
			return false;
		if (!this.name.equals(((Relation) obj).getName()))
			return false;
		return true;
	}

	/**
	 * This method overrides the default toString method inherited from the Object
	 * class.
	 * 
	 * @return a string representing the name of the current relation.
	 */
	@Override
	public String toString() {
		return this.name;
	}

	public static void createDefaultRelations() {
		andAnt = Network.defineRelation("&ant", "Proposition" , null , 1);
		ant = Network.defineRelation("ant", "Proposition", null, 1);
		cq = Network.defineRelation("cq", "Proposition", null, 1);
		arg = Network.defineRelation("arg", "Proposition", null, 1);
		min = Network.defineRelation("min", "Infimum", null, 1);
		max = Network.defineRelation("max", "Infimum", null, 1);
		i = Network.defineRelation("i", "Infimum", null, 1);
		thresh = Network.defineRelation("thresh", "Infimum", null, 1);
		threshMax = Network.defineRelation("threshmax", "Infimum", null, 1);

		action = Network.defineRelation("action", "Action", null, 1);

		obj = Network.defineRelation("obj", "Entity", null, 1);

		obj1 = Network.defineRelation("obj1", "Entity", null, 1);
		obj2 = Network.defineRelation("obj2", "Entity", null, 1);
		obj3 = Network.defineRelation("obj3", "Entity", null, 1);
		obj4 = Network.defineRelation("obj4", "Entity", null, 1);
		obj5 = Network.defineRelation("obj5", "Entity", null, 1);
		obj6 = Network.defineRelation("obj6", "Entity", null, 1);
		obj7 = Network.defineRelation("obj7", "Entity", null, 1);
		obj8 = Network.defineRelation("obj8", "Entity", null, 1);
		obj9 = Network.defineRelation("obj9", "Entity", null, 1);
		obj10 = Network.defineRelation("obj10", "Entity", null, 1);

		precondition = Network.defineRelation("precondition", "Proposition", null, 1);
		act = Network.defineRelation("act", "Act", null, 1);
		doo = Network.defineRelation("do", "Act", null, 1);
		iff = Network.defineRelation("if", "Proposition", null, 1);
		when = Network.defineRelation("when", "Proposition", null, 1);
		whenever = Network.defineRelation("whenever", "Proposition", null, 1);
		plan = Network.defineRelation("plan", "Act", null, 1);
		goal = Network.defineRelation("goal", "Proposition", null, 1);
		effect = Network.defineRelation("effect", "Proposition", null, 1);
		suchthat = Network.defineRelation("suchthat", "Proposition", null, 1);
		vars = Network.defineRelation("vars", "Infimum", null, 1);
		elsee = Network.defineRelation("else", "Act", null, 1);
	}
	
	
	public static void main(String[] args) {
		Relation aa = new Relation("bro", "Proposition",null, 1);
	}

}