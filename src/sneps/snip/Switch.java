package sneps.snip;

import sneps.network.VariableNode;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.matching.Substitutions;

public class Switch {
	private Substitutions substitutions;

	public Switch() {
		this.substitutions = new LinearSubstitutions();
	}

	public Substitutions getSubstitutions() {
		return substitutions;
	}

	public void setSubstitutions(Substitutions substitutions) {
		this.substitutions = substitutions;
	}

	public Switch(Substitutions substitution) {
		this.substitutions = substitution;
	}

	public void switchReport(Report r) {
		for (int i = 0; i < this.substitutions.cardinality(); i++) {
			Binding b = r.getSubstitutions().getBindingByVariable(this.substitutions.getBinding(i).getVariableNode());
			System.out.println(this.substitutions.getBinding(i).getVariableNode());
			System.out.println("i: " + i + " binding: " + b);
			if (b != null) {
				b.setVariable((VariableNode) this.substitutions.getBinding(i).getNode());
			} else {
				System.out.println("There u go " + this.substitutions.getBinding(i));
				r.getSubstitutions().putIn(this.substitutions.getBinding(i));
				System.out.println("Size now " + r.getSubstitutions().cardinality());
			}
		}
		System.out.println("No substitutions are done, brand new report: " + r.getSubstitutions().isNew());
		System.out.println("Done Switching:" + r.getSubstitutions());
		// {a/X, b/Y}, {X/W, Y/Z, K/C} => {a/W, b/Z, K/C}
		// r.getSubstitutions().unionIn(s);
	}

	public String toString() {
		return substitutions.toString();
	}
}
