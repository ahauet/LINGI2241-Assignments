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
import java.util.LinkedHashMap;


public class LRUCache {
	
	//Contains element of the cache and used to see if a element is in the cache or not
	private LinkedHashMap<String,Integer> cache = new LinkedHashMap<String,Integer>();
	//Total size of the cache
	private int cacheSize;
	//Free space available in the cache
	private int freeSpace;
	//Number of hit
	private int hit = 0;
	//Number of miss
	private int miss =0;
	//Indicate the number of element needed to warm up the cache
	private int warmup;
	//Number of byte with a hit
	private long hitByte = 0; 
	//Total number of bytes handled by the cache
	private long cacheByte =0;
	
	public LRUCache(int cacheSize, int warmup) {
		this.cacheSize = cacheSize;
		this.freeSpace = cacheSize;
		this.warmup = warmup;
	}
	
	public int getHit() {
		return hit;
	}
	
	public int getMiss() {
		return miss;
	}
	
	public long getHitByte() {
		return hitByte;
	}
	
	public long getCacheByte() {
		return cacheByte;
	}
	
	//Add an element(=request) of size(=elementSize) in the cache
	public void add (String request, int elementSize) {
		//If the element has a size bigger then the cache size then it's a miss
		if(elementSize < cacheSize) {
			//If the cache contains the element
			if (cache.containsKey(request)) {
				
				int oldSize = cache.get(request);
				cache.remove(request);
				cache.put(request, elementSize);
				
				////If the size of the request element and the size of
				//the cache element are the same, it's a hit
				if(oldSize == elementSize) {
					
					if(warmup == 0) {
						hit++;
						hitByte += elementSize;
						cacheByte += elementSize;
					} else {
						warmup--;
					}
					
					//Else if the size of the request element and the size of
					//the cache element are not the same, it's a miss
				} else {
					freeSpace += oldSize;
					//If it's needed, we free up more space for the element
					while(freeSpace < elementSize) {
						String removeElement = cache.keySet().iterator().next();
						freeSpace += cache.get(removeElement);
						cache.remove(removeElement);
					}
					freeSpace -= elementSize;
					
					if(warmup == 0) {
						miss++;
						cacheByte += elementSize;
					} else {
						warmup--;
					}
				}
			}
			//If the element is not in the cache, it's a miss
			else {
				//If it's needed, we free up more space for the element
				while(freeSpace < elementSize) {
					String removeElement = cache.keySet().iterator().next();
					freeSpace += cache.get(removeElement);
					cache.remove(removeElement);
				}
				//We put the element in the cache
				cache.put(request, elementSize);
				freeSpace -= elementSize;
				
				if(warmup == 0) {
					miss++;
					cacheByte += elementSize;
				} else {
					warmup--;
				}
			}
		//If the element size is too big for the caches
		} else {
			if(warmup == 0) {
				miss++;
				cacheByte += elementSize;
			} else {
				warmup--;
			}
		}
	}
	
	//Write the cache in a file
	public void writeInFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("cache_lru.txt", "UTF-8");
		Iterator<String> it = cache.keySet().iterator();
		while(it.hasNext()) {
			writer.println(it.next());
		}
		writer.close();
	}
}