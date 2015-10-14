package task1_1;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Task1_1 {

	
	public static void main(String []args) {
		
		if (args.length != 2) {
			//TODO lancer une erreur ?
		}
		
		//Length of warm-up phase
		int x = Integer.parseInt(args[0]);
		//Cache size
		int n = Integer.parseInt(args[1]);
		
		//String[] list = {"A","B", "C", "D", "A", "B", "E", "A", "B", "C" ,"D" ,"E"};
		String[] list = {"A","A", "A", "D", "A", "B", "E", "A", "B", "C" ,"D" ,"E"};

		//LRUCache LRU = new LRUCache(n);
		LFUCache LFU = new LFUCache(n);
		
		for(String s: list) {
			LFU.add(s);
			LFU.print();
		}
	}
}

class LRUCache {
	
	private int cacheSize;
	private LinkedList<String> list;
	
	public LRUCache(int cacheSize) {
		this.list = new LinkedList<String>();
		this.cacheSize = cacheSize;
	}
	
	public void add (String s) {
		if (list.contains(s)) {
			String tmp = list.pop();
			list.add(tmp);
		}
		else if(list.size() == cacheSize) {
			list.pop();
			list.add(s);
		} else {
			list.add(s);
		}
	}
	
	public void print() {
		System.out.println(Arrays.toString(list.toArray()));
	}
	
}

class LFUCache {
	private int cacheSize;
	private Map<String, Integer> list;
	
	public LFUCache(int cacheSize) {
		this.list = new LinkedHashMap<String, Integer>();
		this.cacheSize = cacheSize;
	}
	
	public void add (String s) {
		if (list.containsKey(s)) {
			int frequency = list.get(s);
			list.remove(s);
			list.put(s, frequency + 1);
		} else if (list.size() == cacheSize) {
			Iterator<String> it = list.keySet().iterator();
			String tmpKey = "";
			int tmpFrequency = Integer.MAX_VALUE;
			while(it.hasNext()) {
				String key = it.next();
				int frequency = list.get(key);
				if (frequency < tmpFrequency) {
					tmpKey = key;
					tmpFrequency = frequency;
				}
			}
			list.remove(tmpKey);
			list.put(s,1);
		} else {
			list.put(s, 1);
		}
	}
	
	public void print() {
		Iterator<String> it = list.keySet().iterator();
		System.out.print("[ ");
		while(it.hasNext()) {
			String key = it.next();
			int frequency = list.get(key);
			System.out.print("{ " + key + ":" + frequency + " }");
		}
		System.out.println(" ]");
	}
}
