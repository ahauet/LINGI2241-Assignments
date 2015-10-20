/**
 * Implementation of RemoveLargestFirst Cache :
 * 
 * Alexandre Hauet & Maximilien Roberti
 * 
 */
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

public class RemoveLargestFirstCache {
	
	//CacheElement represent an element in memorys
	class CacheElement {
		
		private String request;
		private int elementSize;
		
		public CacheElement(String request, int elementSize) {
			this.request = request;
			this.elementSize = elementSize;
		}
		
		public String getRequest() {
			return request;
		}
		
		public int getElementSize() {
			return elementSize;
		}
		
		@Override
		public boolean equals(Object obj) {
			CacheElement cacheElement = (CacheElement) obj;
			return this.request.equals(cacheElement.request);
		}
	}
	
	//Comparator of ElementCache
	class ComparatorElementCache implements Comparator<CacheElement> {

		@Override
		public int compare(CacheElement e1, CacheElement e2) {
			return e2.elementSize - e1.elementSize;
		}
		
	}
	
	
	private ComparatorElementCache comparator = new ComparatorElementCache();
	//Contains element of the cache and used to see if a element is in the cache or not
	private HashMap<String, Integer> cache = new HashMap<String, Integer>();
	//Indicate which element (=the first of he queue) of the cache will be discard if some space is needed
	private PriorityQueue<CacheElement> queue;
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
	
	public RemoveLargestFirstCache(int cacheSize, int warmup) {
		this.queue = new PriorityQueue<CacheElement>(cacheSize,comparator);
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
	public void add(String request, int elementSize) {
		//If the element has a size bigger then the cache size then it's a miss
		if(elementSize < cacheSize) {
			
			CacheElement cacheElement = new CacheElement(request, elementSize);
			//If the cache contains the element
			if(cache.containsKey(request)) {
				
				int oldRequestSize = cache.get(request);
				//If the size of the request element and the size of
				//the cache element are the same
				if(oldRequestSize == elementSize) {
					if(warmup == 0) {
						hit++;
						hitByte += elementSize;
						cacheByte += elementSize;
					} else {
						warmup--;
					}
				} else {
					queue.remove(cacheElement);
					freeSpace += oldRequestSize;
					//If it's needed, we free up space for the elements
					while(freeSpace < elementSize) {
						CacheElement tmpCacheElement = queue.remove();
						cache.remove(tmpCacheElement.request);
						freeSpace += tmpCacheElement.elementSize;
					}
					queue.add(cacheElement);
					if(warmup == 0) {
						miss++;
						cacheByte += elementSize;
					} else {
						warmup--;
					}
					freeSpace -= elementSize;
					cache.put(request, elementSize);
				}
			//If the element is not in the cache, it's a miss
			} else {
				//If it's needed, we free up space for the elements
				while(freeSpace < elementSize) {
					CacheElement tmpCacheElement = queue.remove();
					cache.remove(tmpCacheElement.request);
					freeSpace += tmpCacheElement.elementSize;
				}
				if(warmup == 0) {
					miss++;
					cacheByte += elementSize;
				} else {
					warmup--;
				}
				freeSpace -= elementSize;
				cache.put(request, elementSize);
				queue.add(cacheElement);
			}
		//If the element is too big for the caches
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
		PrintWriter writer = new PrintWriter("cache_size-based.txt", "UTF-8");
		Iterator<String> it = cache.keySet().iterator();
		while(it.hasNext()) {
			writer.println(it.next());
		}
		writer.close();
	}
	
	
}
