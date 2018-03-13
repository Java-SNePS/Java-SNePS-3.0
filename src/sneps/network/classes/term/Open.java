package sneps.network.classes.term;

import sneps.network.cables.DownCableSet;
import sneps.network.classes.setClasses.VariableSet;

public class Open extends Molecular {
	protected VariableSet variables;

	public Open(String idenitifier) {
		super(idenitifier);
	}
	public Open(String closedName, DownCableSet dCableSet) {
		super(closedName, dCableSet);
	}
	public VariableSet getFreeVariables() {
		return variables;
	}


}
