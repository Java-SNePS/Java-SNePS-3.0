package sneps.network.classes;

import sneps.exceptions.CustomException;
import sneps.network.Network;

public class RCFP {
	public static RCFP andAnt, ant, cq, arg, min, max, i, threshMax, thresh,
	action, obj, precondition, act, doo, iff, when,
	plan, goal, effect, obj1, obj2, obj3, obj4, obj5,
	obj6, obj7, obj8, obj9, obj10;

/**
* The relation included in this 3-tuple (Relation, adjust and limit).
*/
private Relation relation;

/**
* The specified adjustability of the relation in the current RCFP. This
* adjustability is to be applied and override the default adjustability of
* the relation within a certain case frame.
*/
private String adjust;

/**
* The specified limit of the relation in the current RCFP. This limit is to
* be applied and override the default limit of the relation within a
* certain case frame.
*/
private int limit;

/**
* The constructor of this class.
* 
* @param relation
*            the Relation included in the current RCFP
* @param adjust
*            the adjustability defined for the relation within the current
*            RCFP.
* @param limit
*            the limit defined for the relation within the current RCFP.
*/
public RCFP(Relation relation, String adjust, int limit) {
this.relation = relation;
this.adjust = adjust;
this.limit = limit;
}

/**
* @return the adjustability defined for the relation within the current
*         RCFP.
*/
public String getAdjust() {
return adjust;
}

/**
* @return the limit defined for the relation within the current RCFP.
*/
public int getLimit() {
return limit;
}

/**
* @return the Relation included in the current RCFP.
*/
public Relation getRelation() {
return relation;
}

/**
* This method overrides the default toString method inherited from the
* Object class.
*/
@Override
public String toString() {
String s = "(";
s += "relation:" + this.relation + ", ";
s += "adjust:" + this.adjust + ", ";
s += "limit:" + this.limit;
s += ")";
return s;
}

public static void createDefaultProperties() throws CustomException {
if (Relation.andAnt == null)
	Relation.createDefaultRelations();
andAnt = Network.defineRelationPropertiesForCF(Relation.andAnt, "none",
		1);
ant = Network.defineRelationPropertiesForCF(Relation.ant, "none", 1);
cq = Network.defineRelationPropertiesForCF(Relation.cq, "none", 1);
arg = Network.defineRelationPropertiesForCF(Relation.arg, "none", 1);
min = Network.defineRelationPropertiesForCF(Relation.min, "none", 1);
max = Network.defineRelationPropertiesForCF(Relation.max, "none", 1);
i = Network.defineRelationPropertiesForCF(Relation.i, "none", 1);
thresh = Network.defineRelationPropertiesForCF(Relation.thresh, "none",
		1);
threshMax = Network.defineRelationPropertiesForCF(Relation.threshMax,
		"none", 1);

action = Network.defineRelationPropertiesForCF(Relation.action, "none",
		1);
obj = Network.defineRelationPropertiesForCF(Relation.obj,
		"none", 1);
obj1 = Network.defineRelationPropertiesForCF(Relation.obj1, "none", 1);
obj2 = Network.defineRelationPropertiesForCF(Relation.obj2, "none", 1);
obj3 = Network.defineRelationPropertiesForCF(Relation.obj3, "none", 1);
obj4 = Network.defineRelationPropertiesForCF(Relation.obj4, "none", 1);
obj5 = Network.defineRelationPropertiesForCF(Relation.obj5, "none", 1);
obj6 = Network.defineRelationPropertiesForCF(Relation.obj6, "none", 1);
obj7 = Network.defineRelationPropertiesForCF(Relation.obj7, "none", 1);
obj8 = Network.defineRelationPropertiesForCF(Relation.obj8, "none", 1);
obj9 = Network.defineRelationPropertiesForCF(Relation.obj9, "none", 1);
obj10 = Network.defineRelationPropertiesForCF(Relation.obj10, "none", 1);

precondition = Network.defineRelationPropertiesForCF(Relation.precondition,
		"none", 1);
act = Network.defineRelationPropertiesForCF(Relation.act, "none", 1);
doo = Network.defineRelationPropertiesForCF(Relation.doo, "none", 1);
when = Network.defineRelationPropertiesForCF(Relation.when, "none", 1);
iff = Network.defineRelationPropertiesForCF(Relation.iff, "none", 1);
plan = Network.defineRelationPropertiesForCF(Relation.plan, "none", 1);
goal = Network.defineRelationPropertiesForCF(Relation.goal, "none", 1);
effect = Network.defineRelationPropertiesForCF(Relation.effect, "none", 1);
}

}
