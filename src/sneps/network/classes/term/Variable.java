package sneps.network.classes.term;

public class Variable extends Term {
	private static int id = -1;

	public Variable(String idenitifier) {
		super(idenitifier);
		id++;
	}

	public int getId() {
		return id;
	}


}
