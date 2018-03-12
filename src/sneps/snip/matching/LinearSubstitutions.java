/**
 * @(#)Substitutions.java
 *
 *
 * @author Mohamed Karam Gabr
 * @version 1.00 2010/3/14
 */
package sneps.snip.matching;
import java.util.Vector;

import sneps.network.Node;
import sneps.network.classes.term.Variable;

public class LinearSubstitutions implements Substitutions
{

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void putIn(Binding mb) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCompatible(Binding mb) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(Binding mb, Node mn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isBound(Variable mv) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValue(Node mn) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Variable srcNode(Node mn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Binding getBindingByVariable(Variable mv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Binding getBindingByNode(Node mn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMember(Binding mb) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubSet(Substitutions s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEqual(Substitutions s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Substitutions union(Substitutions s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unionIn(Substitutions s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Substitutions restrict(Variable[] ns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node term(Variable mv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int cardinality() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Binding choose() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Substitutions others() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node value(Variable n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Substitutions insert(Binding m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCompatible(Substitutions s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Binding getBinding(int x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Substitutions[] split() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(Substitutions s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean sub(String x, String y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int termID(int variableID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void insertOrUpdate(Binding mb) {
		// TODO Auto-generated method stub
		
	}
}