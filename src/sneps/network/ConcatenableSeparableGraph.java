package sneps.network;

import java.util.Enumeration;
import java.util.Hashtable;

public class ConcatenableSeparableGraph {
	
	private Hashtable<Integer, String> StartNodes;
	private Hashtable<Integer, String> EndNodes;
	private String start;
	private String end;
	private Hashtable<Integer, Hashtable<Integer,Integer>>  hashtable;
	private Hashtable<Integer, Integer> aaa;
	private Hashtable<Integer, Integer> aaaa;
	
	public ConcatenableSeparableGraph(){
		 StartNodes = new Hashtable<Integer, String>();
		 EndNodes = new Hashtable<Integer, String>();
		 start = "Start";
		 end = "End";
		 hashtable = new Hashtable<Integer, Hashtable<Integer,Integer>>();
		 aaa = new Hashtable<Integer, Integer>();
		 aaaa = new Hashtable<Integer, Integer>();
	}

	
	public Hashtable<Integer, Hashtable<Integer, Integer>> printhashtableSeparable (int [] data){ 

		for(int ii =0; ii<data.length;ii++) {
			this.StartNodes.put(data[ii], start);
			this.EndNodes.put(data[ii], end);
		}
		
		if(this.hashtable.isEmpty()) {
	
		aaa.put(data[1], data[1]);
		hashtable.put(data[0], aaa);
		
		for(int i = 1; i<data.length-1;i++) {
		
			Hashtable<Integer, Integer> aa = new Hashtable<Integer, Integer>();
			aa.put(data[i+1], data[i+1]);
			aa.put(data[i-1], data[i-1]);
			hashtable.put(data[i], aa);
			
	}	
		aaaa.put(data[data.length-2], data[data.length-2]);
		hashtable.put(data[data.length-1], aaaa);
		
		System.out.println();
		System.out.println(StartNodes);
		System.out.println(EndNodes);
		System.out.println();
		}
		else 
		{
			Hashtable<Integer,Integer> temp = new Hashtable<Integer,Integer>();
			Hashtable<Integer,Integer> tempo = new Hashtable<Integer,Integer>();
			Hashtable<Integer, Hashtable<Integer,Integer>> tempor = new Hashtable<Integer, Hashtable<Integer,Integer>>();
			
			temp.put(data[1], data[1]);
			tempor.put(data[0], temp);
			
			for(int i = 1; i<data.length-1;i++) {
			
				Hashtable<Integer, Integer> aa = new Hashtable<Integer, Integer>();
				aa.put(data[i+1], data[i+1]);
				aa.put(data[i-1], data[i-1]);
				tempor.put(data[i], aa);	
		}		
			tempo.put(data[data.length-2], data[data.length-2]);
			tempor.put(data[data.length-1], tempo);
			this.hashtable.putAll(tempor);
			
		}
	return hashtable;
	
}
	
	public static void removehashtable (Hashtable<Integer, Hashtable<Integer,Integer>> hashtable , int key) {
		
		Enumeration<Hashtable<Integer, Integer>> enumration = hashtable.elements();
		while(enumration.hasMoreElements()){
            Hashtable<Integer, Integer> elmnt = enumration.nextElement();
            if(elmnt.containsKey(key)){
            	elmnt.remove(key);
            }
        }
		hashtable.remove(key); 
		System.out.println();
	}

	public boolean searchpath (Hashtable<Integer, Hashtable<Integer,Integer>> hashtable , int [] arr) {
			
			for(int i=0; i<arr.length-1;i++) {
				
					int element = arr[i];
					int nextelement = arr[i+1];
					
					try {
						if(hashtable.get(element).get(nextelement) == nextelement) {
							
						}
						else 
							return false;
					} 
					catch (NullPointerException e){
						
						return false;
					}
					
				
			}
			return true;
			
		}
	
public static void main(String[] args) {
		
	    ConcatenableSeparableGraph a = new ConcatenableSeparableGraph();
		int[] x = new int[]{ 1,2,3 };	
		int[] y = new int[]{ 3,2,1 };
		int[] z = new int[]{ 2,3 };
		int[] zz = new int[]{ 2,1,3 };
		System.out.println(a.printhashtableSeparable(x));
		System.out.println(a.searchpath(a.printhashtableSeparable(x), y));
		System.out.println(a.searchpath(a.printhashtableSeparable(x), z));
		System.out.println(a.searchpath(a.printhashtableSeparable(x), zz));
		
	}

}
