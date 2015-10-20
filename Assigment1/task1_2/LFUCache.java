/**
 * Implementation of LFU Cache :
 * 
 * Alexandre Hauet & Maximilien Roberti
 * 
 */
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;

public class LFUCache {
	
	
	//LFUElement represent an element in memory
	static class LFUElement {
		
		private String request;
		//Concatenation between frequency and indice
		//Allow to organize the elements by frequency and date of insertion in cache
		private long findice;
		private int frequency;
		private int size;
		//Allow to indicate when a element is insert in the cache
		//Element with indice x was inserted before the element with indice x+1
		private static int indice = 0;
		
		
		public LFUElement(String request, int frequency, int size) {
			this.size = size;
			this.request = request;
			
	        String s = Integer.toString(frequency) + indice;
	        this.findice = Long.valueOf(s);
	        indice++;
	        
	        this.frequency = frequency;
		}
		
		public long getFdate() {
			return findice;
		}
		
		public String getRequest() {
			return request;
		}
		
		@Override
		public String toString() {
			return request + " - " + findice;
		}
		
		@Override
		public boolean equals(Object obj) {
			LFUElement element = (LFUElement) obj;
			
			return this.request.equals(element.getRequest());
		}
	}
	
	//Comparator of LFUElement
	class LFUELementComparator implements Comparator<LFUElement> {
		@Override
		public int compare(LFUElement e1, LFUElement e2) {
			return (int) (e1.findice - e2.findice);
		}	
	}
	
	
	private Comparator<LFUElement> comparator = new LFUELementComparator();
	//Contains element of the cache and used to see if a element is in the cache or not
	private LinkedHashMap<String, LFUElement> cache = new LinkedHashMap<String, LFUElement >();
	//Indicate which element (=the first of he queue) of the cache will be discard if some space is needed
	private PriorityQueue<LFUElement> queue;
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
	
	
	public LFUCache(int cacheSize, int warmup) {
		this.cacheSize = cacheSize;
		this.freeSpace = cacheSize;
		this.warmup = warmup;
		this.queue = new PriorityQueue<LFUElement>(cacheSize,comparator);
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
			//If the cache contains the element
			if (cache.containsKey(request)) {
				LFUElement oldElement = cache.get(request);
				//If the size of the request element and the size of
				//the cache element are the same
				if(oldElement.size == elementSize) {
					//If warmup phase is over, it's a hit
					if(warmup == 0) {
						hit++;
						hitByte += elementSize;
						cacheByte += elementSize;
					} else {
						warmup--;
					}
					queue.remove(oldElement);
				} else {
					//Else if the size of the request element and the size of
					//the cache element are not the same. We need to change the
					//size of the element in the cache.
					freeSpace += oldElement.size;
					queue.remove(oldElement);
					//If it's needed, we free up more space for the element
					while(freeSpace < elementSize) {
						LFUElement removeElement = queue.remove();
						freeSpace += removeElement.size;
						cache.remove(removeElement.request);
					}
					freeSpace -= elementSize;
					//Check if warmup phase is over
					if(warmup == 0) {
						miss++;
						cacheByte += elementSize;
					} else {
						warmup--;
					}
				}
				//Change information for the element in the cache (i.e. new frequency, new size...)
				LFUElement newElement = new LFUElement(oldElement.request,oldElement.frequency + 1, elementSize);
				cache.put(request, newElement);
				queue.add(newElement);	
			
			//If the element is not in the cache, it's a miss
			} else {
				//If it's needed, we free up space for the elements
				while(freeSpace < elementSize) {
					LFUElement removeElement = queue.remove();
					freeSpace += removeElement.size;
					cache.remove(removeElement.request);
				}
				//Creation of the element in the cache with a frequency of 1
				LFUElement newElement = new LFUElement(request, 1,elementSize);
				queue.add(newElement);
				cache.put(request, newElement);
				freeSpace -= elementSize;
				//Check if warmup phase is over
				if(warmup == 0) {
					miss++;
					cacheByte += elementSize;
				} else {
					warmup--;
				}
			}
		
		//If the element is too big for the caches
		} else {
			//Check if warmup phase is over
			if(warmup == 0) {
				cacheByte += elementSize;
				miss++;
			} else {
				warmup--;
			}
		}
	}
	
	//Write the cache in a file
	public void writeInFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("cache_lfu.txt", "UTF-8");
		Iterator<String> it = cache.keySet().iterator();
		while(it.hasNext()) {
			writer.println(it.next());
		}
		writer.close();
	}
}