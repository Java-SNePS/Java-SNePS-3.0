package sneps.network.classes.setClasses;

import sneps.exceptions.NodeNotFoundException;

public class PropositionSet {
	private int[] props;
	private String hash;

	public PropositionSet() {
		this.props = new int[0];
	}

	public PropositionSet(int prop) {
		this.props = new int[] { prop };
		hash = prop + ", ";
	}

	public PropositionSet(int[] props, int prop) {
		int i = 0, j = 0;
		this.props = new int[props.length + 1];
		boolean inserted = false;
		while (i < props.length) {
			if (!inserted && prop < props[i]) {
				this.props[j] = prop;
				hash += this.props[j++] + ", ";
				inserted = true;
			} else {
				this.props[j] = props[i++];
				hash += this.props[j++] + ", ";
			}
		}
		if (!inserted)
			this.props[j] = prop;

	}

	public PropositionSet(int[] props) {
		this.props = new int[props.length];
		for (int i = 0; i < props.length; i++) {
			this.props[i] = props[i];
			hash += this.props[i] + ", ";
		}
	}

	public PropositionSet add(int prop) {
		return new PropositionSet(this.getProps(), prop);
	}

	public PropositionSet remove(int prop) throws NodeNotFoundException {
		int[] current = this.getProps();
		int[] newSet = new int[current.length - 1];
		int j = 0;
		boolean found = false;
		for (int i = 0; i < current.length; i++) {
			if (prop < current[i] && !found)
				throw new NodeNotFoundException("The Node You Are Trying To Remove is Not Found");
			if (!(prop == current[i])) {
				newSet[j] = current[i];
				j++;
			} else {
				found = true;
			}
		}
		PropositionSet temp = new PropositionSet(newSet);
		return temp;
	}

	public boolean equals(Object obj) {
		PropositionSet propositionSet = (PropositionSet) obj;
		int[] inputProps = propositionSet.getProps();
		if (inputProps.length != this.props.length) {
			return false;
		} else {
			for (int i = 0; i < this.props.length; i++) {
				if (this.props[i] != inputProps[i])
					return false;
			}
		}
		return true;
	}

	public boolean isSubSet(PropositionSet propositionSet) {
		int[] props = propositionSet.getProps();
		int i = 0, j = 0;
		while (i < this.props.length && j < props.length) {
			if (this.props[i] == props[j])
				i++;
			else
				j++;
		}
		return i == this.props.length;
	}

	public PropositionSet union(int[] props) {
		int[] FirstSet = this.getProps();
		int[] SecondSet = props;
		int[] newSet = new int[FirstSet.length + SecondSet.length];
		int i = 0, j = 0, q = 0, elements=0;
		while (i < FirstSet.length || j < SecondSet.length) {
			if (i == FirstSet.length) {
				newSet[q++] = SecondSet[j++];
				elements++;
			} else {
				if (j == SecondSet.length) {
					newSet[q++] = FirstSet[i++];
					elements++;
				} else {
					if (FirstSet[i] < SecondSet[j]) {
						newSet[q++] = FirstSet[i];
						elements++;
					} else {
						if (FirstSet[i] > SecondSet[j]) {
							newSet[q++] = SecondSet[j++];
							elements++;
						} else {
							newSet[q++] = FirstSet[i++];
							j++;
							elements++;
						}
					}
				}
			}

		}
		if(elements == newSet.length){
			return new PropositionSet(newSet);
		}else{
			int[] smallSet = new int[elements];
			for(int z = 0; z< smallSet.length; z++)
				smallSet[z] = newSet[z];
			return new PropositionSet(smallSet);
		}
		
	}

	public boolean isEmpty() {
		return props.length == 0;
	}

	public PropositionSet clearSet() {
		return new PropositionSet();
	}

	public int[] getProps() {
		return props;
	}

	public String getHash() {
		return hash;
	}

}