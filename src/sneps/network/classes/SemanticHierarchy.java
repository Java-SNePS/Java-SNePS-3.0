package sneps.network.classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import sneps.exceptions.SemanticNotFoundInNetworkException;
import sneps.network.Network;

public class SemanticHierarchy implements Serializable{

	public static Hashtable<String, Semantic> semantics = new Hashtable<String, Semantic>();

	public static Semantic createSemanticType(String identifier) {
		if (semantics.containsKey(identifier)) {
			return semantics.get(identifier);
		} else {
			
			if((identifier.equals("Entity"))) {
				Semantic semantic = new Semantic(identifier);
				semantics.put(identifier, semantic);
				return semantics.get(identifier);
			}
			
			else {
				Semantic semantic = new Semantic(identifier,"Entity");
				semantics.put(identifier, semantic);
				return semantics.get(identifier);
			}
		}
	}
	
	public static Semantic createSemanticType(String identifier, String superClassIdentifier) throws SemanticNotFoundInNetworkException {
		if(semantics.containsKey(identifier)) {
			return semantics.get(identifier);
		} 
		else {
			if(semantics.containsKey(superClassIdentifier)) {
				Semantic semantic = new Semantic(identifier, superClassIdentifier);
				semantics.put(identifier, semantic);
				return semantics.get(identifier);
			}
			else {
				throw new SemanticNotFoundInNetworkException("The super class named '" + superClassIdentifier + "' does not exist!");
			}
		}
	}
	
	public static Semantic createSemanticType(String identifier, ArrayList<String> superSemanticSet) {
		
		if(semantics.containsKey(identifier)) {
			return semantics.get(identifier);
		} 
		
		else {
			
			boolean flag = true;
			
			for (int i = 0; i<superSemanticSet.size(); i++) {
				if (semantics.containsKey(superSemanticSet.get(i)) 
						&& semantics.get(superSemanticSet.get(i)).getSuperClassesNames().contains("Individual")) {
					
						continue;
					
				}
				
				else {
					flag = false;
					break;
				}
			}
			
			if (flag == true) {
				
				Semantic semantic = new Semantic(identifier,superSemanticSet);
				semantics.put(identifier, semantic);
				return semantics.get(identifier);
			}
			
			else {
				return null;
			}
			
		}
		
	}
		
		
		public static ArrayList<Semantic> findChildren(Semantic sem) throws SemanticNotFoundInNetworkException {
	    	Hashtable<String, Semantic> semantics1 = SemanticHierarchy.getSemantics();
	    	Set<String> semantics2 = semantics1.keySet();
			Object[] semantics =   semantics2.toArray();
	    	
	    	ArrayList<Semantic> children = new ArrayList<Semantic>();
	    	
	    	for (int i = 0; i<semantics.length; i++) {
	    		
	    		Semantic s = SemanticHierarchy.getSemantic((String) semantics[i]);
	    		
	    		
	    		if (s.superClass == sem.getSemanticType()) {
	    			children.add(s);
	    		}
	    		
	    		if (s.getSuperClassSet() != null && s.getSuperClassSet().contains(sem.getSemanticType())) {
	    			children.add(s);
	    		}
	    	}
	    	
	    	return children;
	    }
	    
	    
	    public static Semantic findOcc(ArrayList<Semantic> semantics) {
	    	HashSet<Semantic> semanticSet = new HashSet<Semantic>();
	    	for (Semantic s : semantics) {
	    		if (semanticSet.add(s) == false) {
	    			return s;
	    			}
	    		
	    		}
			return null;
	    	}
					
	  
	    
	    public static Semantic greatestLowerBound(Semantic sem1, Semantic sem2) throws SemanticNotFoundInNetworkException {
	    	ArrayList<Semantic> subtypes = new ArrayList<Semantic>();
	    	subtypes.addAll(findChildren(sem1));
	    	subtypes.addAll(findChildren(sem2));
	    	Semantic glb = new Semantic();
	    	
	    	while(!(subtypes.isEmpty())) {
	    		
	    		for (int i = 0; i<subtypes.size(); i++) {
	    			if (findOcc(subtypes) != null) {
		    			glb = findOcc(subtypes);
		    		}
	    			
	    			if (findChildren(subtypes.get(0)).isEmpty()) {
	    				subtypes.remove(subtypes.get(0));
	    			}
	    			
	    			else {
	    				subtypes.addAll(findChildren(subtypes.get(0)));
	    				subtypes.remove(subtypes.get(0));
	    			}
	    		}
	    		
	    	}
	    	
	    	if (glb.getSemanticType() == null) {
	    		throw new NullPointerException("Greatest Lower Bound not found");
	    	}
	    	
	    	else {
	    		return glb;
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
	
	public static void save(String f) throws FileNotFoundException, IOException {
		ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(new File(f)));
		fos.writeObject(semantics);
		fos.close();
	}
	
	public static void load(String f) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream sems= new ObjectInputStream(new FileInputStream(new File(f)));
		Hashtable<String, Semantic> tempSems = (Hashtable<String, Semantic>) sems.readObject();
		SemanticHierarchy.semantics = tempSems;
		sems.close();
		
		try {
			Semantic.proposition = getSemantic("Proposition");
			Semantic.act =  getSemantic("Act");
			Semantic.individual = getSemantic("Individual");
			Semantic.entity = getSemantic("Entity");
		} catch (SemanticNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}