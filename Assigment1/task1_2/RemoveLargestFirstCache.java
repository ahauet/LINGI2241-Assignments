import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

public class RemoveLargestFirstCache {
	
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
	
	class ComparatorElementCache implements Comparator<CacheElement> {

		@Override
		public int compare(CacheElement e1, CacheElement e2) {
			return e2.elementSize - e1.elementSize;
		}
		
	}
	
	private ComparatorElementCache comparator = new ComparatorElementCache();
	private PriorityQueue<CacheElement> queue = new PriorityQueue<CacheElement>(comparator);
	private HashMap<String, Integer> cache = new HashMap<String, Integer>();
	private int cacheSize;
	private int cacheFreeSpace;
	private int miss = 0;
	private int hit = 0;
	
	public RemoveLargestFirstCache(int cacheSize) {
		this.cacheSize = cacheSize;
		this.cacheFreeSpace = cacheSize;
	}
	
	public int getMiss() {
		return miss;
	}
	
	public int getHit() {
		return hit;
	}
	
	public void add(String request, int requestSize) {
		CacheElement cacheElement = new CacheElement(request, requestSize);
		if(cache.containsKey(request)) {
			int oldRequestSize = cache.get(request);
			if(oldRequestSize == requestSize) {
				hit++;
			} else {
				queue.remove(cacheElement);
				queue.add(cacheElement);
				cacheFreeSpace += oldRequestSize;
				while(cacheFreeSpace < requestSize) {
					CacheElement tmpCacheElement = queue.remove();
					cache.remove(tmpCacheElement.request);
					cacheFreeSpace += tmpCacheElement.elementSize;
				}
				cacheFreeSpace -= requestSize;
				cache.put(request, requestSize);
				miss++;
			}
		} else {
			while(cacheFreeSpace < requestSize) {
				CacheElement tmpCacheElement = queue.remove();
				cache.remove(tmpCacheElement.request);
				cacheFreeSpace += tmpCacheElement.elementSize;
			}
			cacheFreeSpace -= requestSize;
			cache.put(request, requestSize);
			queue.add(cacheElement);
		}	
	}
	
	public void print() {
		Iterator<String> it = cache.keySet().iterator();
		while(it.hasNext()) {
			String s = it.next();
			System.out.print(" [ " + s + " - " + cache.get(s) + " ] ");
		}
		System.out.println();
	}
	
}
