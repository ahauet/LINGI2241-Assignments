import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class LRUCache {
	
	private LinkedHashMap<String,Integer> cache = new LinkedHashMap<String,Integer>();
	private int cacheSize;
	private int freeSpace;
	private int miss = 0;
	private int hit = 0;
	
	public LRUCache(int cacheSize) {
		this.cacheSize = cacheSize;
		this.freeSpace = cacheSize;
	}
	
	public int getHit() {
		return hit;
	}
	
	public int getMiss() {
		return miss;
	}
	
	public void add (String s, int size) {
		if (cache.containsKey(s)) {
			int oldSize = cache.get(s);
			cache.remove(s);
			cache.put(s, size);
			if(oldSize == size) {
				hit++;
			} else {
				freeSpace += oldSize;
				freeSpace -= size;
				miss++;
			}
		}
		else if(cache.size() == cacheSize){
			while(freeSpace < size) {
				String request = cache.keySet().iterator().next();
				freeSpace += cache.get(request);
				cache.remove(request);
			}
			cache.put(s, size);
			freeSpace -= size;
			miss++;
		} else {
			cache.put(s, size);
			freeSpace -= size;
			miss++;
		}
	}
	
	public void writeInFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("cache_lru.txt", "UTF-8");
		Iterator<String> it = cache.keySet().iterator();
		while(it.hasNext()) {
			writer.println(it.next());
		}
		writer.close();
	}
	
	public void print() {
		System.out.print("{ ");
		Iterator<String> it = cache.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			System.out.print(" [ "+ key +"-"+ cache.get(key) +" ] " );
		}
		System.out.println(" }");
	}
	
}
