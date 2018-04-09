package sneps.network.classes.term;

public class Variable extends Term {
	private static int count = -1;
	private int id;

	public Variable(String idenitifier) {
		super(idenitifier);
		id = ++count;
	}

	public int getId() {
		return id;
	}

	public void setCount(int cnt){
		count = cnt;
	}

}
