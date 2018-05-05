package sneps.network.classes;

import java.util.Hashtable;

import sneps.exceptions.SemanticNotFoundInNetworkException;

public class SemanticHierarchy {

	private static Hashtable<String, Semantic> semantics = new Hashtable<String, Semantic>();

	public static Semantic createSemanticType(String identifier) {
		if (semantics.containsKey(identifier)) {
			return semantics.get(identifier);
		} else {
			Semantic semantic = new Semantic(identifier);
			semantics.put(identifier, semantic);
			return semantics.get(identifier);
		}
	}

	public static Semantic getSemantic(String identifier) throws SemanticNotFoundInNetworkException {
		if (semantics.containsKey(identifier)) {
			return semantics.get(identifier);
		} else {
			throw new SemanticNotFoundInNetworkException(
					"There is no semantic type named '" + identifier + "' in the system");
		}
	}

	public static Hashtable<String, Semantic> getSemantics() {
		return semantics;
	}

}
