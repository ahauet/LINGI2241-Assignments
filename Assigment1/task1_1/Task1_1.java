import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;


public class Task1_1 {

	
	public static void main(String []args) throws IOException {
		
		if (args.length != 2) {
			//TODO lancer une erreur ?
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		

		//Length of warm-up phase
		int x = Integer.parseInt(args[0]);
		//Cache size
		int n = Integer.parseInt(args[1]);
		
		int nbProcessedRequest = 0;
		
		LRUCache LRU = new LRUCache(n);
		LFUCache LFU = new LFUCache(n);
		
		String s;
		while ((s = in.readLine()) != null && s.length() != 0) {
			if (x == 0) {
				String request[] = s.split(" ");
				LRU.add(request[0]);
				LFU.add(request[0]);
				nbProcessedRequest++;
			} else {
				x--;
			}
		}
		if(nbProcessedRequest > 0) {
			System.out.println("LRU Hit rate : " + LRU.getHit()+"%");
			System.out.println("LFU Hit rate : " + LFU.getHit() +"%");
		} else {
			System.out.println("Nothing write in the cache");
		}

		LRU.writeInFile();
		LFU.writeInFile();
		
	}
}

class LRUCache {
	
	private int cacheSize;
	private LinkedList<String> list;
	private int miss = 0;
	private int hit = 0;
	
	public LRUCache(int cacheSize) {
		this.list = new LinkedList<String>();
		this.cacheSize = cacheSize;
	}
	
	public void add (String s) {
		if (list.contains(s)) {
			list.remove(s);
			list.add(s);
			hit++;
		}
		else if(list.size() == cacheSize) {
			list.pop();
			list.add(s);
			miss++;
		} else {
			list.add(s);
			miss++;
		}
	}
	
	public int getHit() {
		return this.hit;
	}
	
	public void print() {
		System.out.println(Arrays.toString(list.toArray()));
	}
	
	public void printHitMiss() {
		System.out.println("Hit = " +hit+" Miss = " +miss);
	}
	
	public void writeInFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("cache_lru.txt", "UTF-8");
		Iterator<String> it = list.iterator();
		while(it.hasNext()) {
			writer.println(it.next());
		}
		writer.close();
	}
	
}

class LFUCache {
	private int cacheSize;
	private Map<String, Integer> list;
	private int miss = 0;
	private int hit = 0;
	
	public LFUCache(int cacheSize) {
		this.list = new LinkedHashMap<String, Integer>();
		this.cacheSize = cacheSize;
	}
	
	public int getHit() {
		return this.hit;
	}

	public void add (String s) {
		if (list.containsKey(s)) {
			int frequency = list.remove(s);
			list.put(s, frequency + 1);
			hit++;
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
			miss++;
		} else {
			list.put(s, 1);
			miss++;
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
	
	public void writeInFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("cache_lfu.txt", "UTF-8");
		Iterator<String> it = list.keySet().iterator();
		while(it.hasNext()) {
			writer.println(it.next());
		}
		writer.close();
	}
}


