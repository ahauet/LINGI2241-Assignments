import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;

public class LRUCache {
	
	private int cacheSize;
	private LinkedList<String> list;
	private int miss = 0;
	private int hit = 0;
	
	public LRUCache(int cacheSize) {
		this.list = new LinkedList<String>();
		this.cacheSize = cacheSize;
	}
	
	public int getHit() {
		return hit;
	}
	
	public int getMiss() {
		return miss;
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
	
	public void printHitMiss() {
		System.out.println("LFU : Hit = " + hit + " Miss = " + miss);
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
