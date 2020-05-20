package sneps.network.classes.setClasses;

import java.util.LinkedList;

public class LinkedList_SubCollection {
	
	public boolean SubList (LinkedList a , LinkedList b) {
		
		LinkedList<Integer> counter = new LinkedList<Integer>();
		int check = 0;
		if(a.size() > b.size()) {
			SubList(b, a);
	}
		else 
			
			for(int i=0; i<a.size();i++) {
				for(int j=0;j<b.size();j++) {
					if(b.contains(a.get(i)) == true) {
					if(a.get(i) != (b.get(j))) {
						
					}
					else 
						if(!counter.isEmpty()) {
							if(counter.get(check)<j) {
								counter.add(j);
					  }
							
					}
						else 
							counter.add(j);
				}
					else 
						return false;
				}
			}
		
		System.out.println(counter);
		
			for (int i=0; i<counter.size();i++) {
					
					if((counter.get(i)+1)-1 == (counter.get(i))) {
						System.out.println("tamam");
					}
					else {
						return false;
					} 
			}
			return true;
}
	public static void main(String[] args) {
		
		LinkedList_SubCollection x = new LinkedList_SubCollection();
		
		LinkedList<String> object = new LinkedList<String>(); 
		  
        // Adding elements to the linked list 
        object.add("K"); 
        object.add("H"); 
        object.addLast("C"); 
        System.out.println("Linked list : " + object); 
        
        LinkedList<String> a = new LinkedList<String>(); 
		  
        // Adding elements to the linked list 
        a.addFirst("T"); 
        a.add("D"); 
        a.add("K");
        a.add("H"); 
        a.add("C"); 
        a.addLast("S"); 
        System.out.println("Linked list : " + a); 
		System.out.println();
		
		System.out.print(x.SubList(object,a));

	}

}
