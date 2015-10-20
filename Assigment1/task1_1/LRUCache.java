/**
 * Implementation of LRU Cache :
 * 
 * Alexandre Hauet & Maximilien Roberti
 * 
 */
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;

public class LRUCache {
	
	//List contents the elements of the cache
	//LinkedList(FIFO) is used because we want
	//that the the oldest element will be the first out
	private LinkedList<String> cache;
	private int cacheSize;
	private int hit = 0;
	private int warmup;
	
	public LRUCache(int cacheSize, int warmup) {
		this.cache = new LinkedList<String>();
		this.cacheSize = cacheSize;
		this.warmup = warmup;
	}
	
	public int getHit() {
		return hit;
	}
	
	public void add (String request) {
		//if the cache contains the element, it's a hit
		if (cache.contains(request)) {
			//Remove and add element, to go to the end of the list
			cache.remove(request);
			cache.add(request);
			
			if (warmup == 0) {
				hit++;
			} else {
				warmup--;
			}
		}
		//The cache is full then we need to discard the oldest element
		//It's a miss
		else if(cache.size() == cacheSize) {
			cache.pop();
			cache.add(request);
			if (warmup == 0) {
			} else {
				warmup--;
			}
		//The cache is not full then we can add element
		//It's a miss
		} else {
			cache.add(request);
			if (warmup == 0) {
			} else {
				warmup--;
			}
		}
	}
	
	//Write the content of the cache in a file
	public void writeInFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("cache_lru.txt", "UTF-8");
		Iterator<String> it = cache.iterator();
		while(it.hasNext()) {
			writer.println(it.next());
		}
		writer.close();
	}
	
}
