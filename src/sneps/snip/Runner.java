package sneps.snip;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

import sneps.network.ActNode;
import sneps.network.Node;

public class Runner {
	
	private static Queue<Node> highQueue;
	private static Queue<Node> lowQueue;
	private static Deque<ActNode> actQueue;
	
	public static void initiate() {
		highQueue = new ArrayDeque<Node>();
		lowQueue = new ArrayDeque<Node>();
		actQueue = new ArrayDeque<ActNode>();
	}

	public static String run() {
		String sequence = "";
		main: while(!highQueue.isEmpty() || !lowQueue.isEmpty() || !actQueue.isEmpty()) {
			while(!highQueue.isEmpty()) {
				System.out.println("\n\n");
				System.out.println(" AT HIGH QUEUE ");
				Node toRunNext = highQueue.poll();
				System.out.println(toRunNext);
				System.out.println("\n\n");
				toRunNext.processReports();
				sequence += 'H';
			}
			while(!lowQueue.isEmpty()) {
				System.out.println("in");
				Node toRunNext = lowQueue.poll();
				toRunNext.processRequests();
				sequence += 'L';
				if(!highQueue.isEmpty())
					continue main;
			}
			while(!actQueue.isEmpty()) {
				System.out.println("AT ACT QUEUE");
				ActNode toRunNext = actQueue.removeLast();
				System.out.println(toRunNext + " agenda: " + toRunNext.getAgenda());
				System.out.println("\n\n");
				toRunNext.processIntends();
				sequence += 'A';
				if(!highQueue.isEmpty() || !lowQueue.isEmpty()) {
					continue main;
				}
			}
		}
		return sequence;
	}
	
	public static void addToHighQueue(Node node) {
		highQueue.add(node);
	}
	
	public static void addToLowQueue(Node node) {
		lowQueue.add(node);
	}
	
	public static void addToActStack(ActNode node) {
		actQueue.addLast(node);
	}

}
