package sneps.network.classes.setClasses;

public class PropositionSet {
	private int[] props;

	public PropositionSet(int prop) {
		this.props = new int[]{prop};
	}

	public PropositionSet(int [] props) {
		this.props = new int[props.length];
		for (int i = 0; i < props.length ; i++) {
			this.props[i] = props[i];
		}
	}

	public PropositionSet(int [] props, int prop) {
		int i = 0, j = 0;
		this.props = new int[props.length + 1];
		boolean inserted = false;
		while (i < props.length) {
			if(!inserted && prop < props[i]) {
				this.props[j++] = prop;
				inserted = true;
			} else {
				this.props[j++] = props[i++];
			}
		}
		if(!inserted)
			this.props[j] = prop;

	}

	public int[] getProps() {
		return props;
	}

	public boolean equals(Object obj) {
		PropositionSet propositionSet = (PropositionSet)obj;
		int [] inputProps = propositionSet.getProps();
		if (inputProps.length != this.props.length) {
			return false;
		}
		else {
			for (int i = 0; i < this.props.length; i ++) {
				if (this.props[i] != inputProps[i])
					return false;
			}
		}
		return true;
	}

	public boolean isSubSet(PropositionSet hyps) {
		int [] props =  hyps.getProps();
		int i = 0, j = 0;
		while(i < this.props.length && j < props.length) {
			if (this.props[i] == props[j])
				i++;
			else
				j++;
		}
		return i == this.props.length;
	}
}