import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;

public class LFUCache {
	
	class LFUElement {
		
		private String request;
		//Concatenation between frequency and date
		private long fdate;
		private int frequency;
		
		public LFUElement(String request, int frequency) {
			this.request = request;
			Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSS");
	        String date = sdf.format(cal.getTime());
	        if(date.length() < 9) {
	        	while(date.length() < 9) {
	        		date += "0";
	        	}
	        }
	        String s = Integer.toString(frequency) + date;
	        this.fdate = Long.valueOf(s);
	        this.frequency = frequency;
		}
		
		public long getFdate() {
			return fdate;
		}
		
		public String getRequest() {
			return request;
		}
		
		@Override
		public String toString() {
			return request + " - " + fdate;
		}
		
		@Override
		public boolean equals(Object obj) {
			LFUElement element = (LFUElement) obj;
			
			return this.request.equals(element.getRequest());
		}
	}
	
	class LFUELementComparator implements Comparator<LFUElement> {
		@Override
		public int compare(LFUElement e1, LFUElement e2) {
			return (int) (e1.fdate - e2.fdate);
		}	
	}
	
		
	private Comparator<LFUElement> comparator = new LFUELementComparator();
	private LinkedHashMap<String, LFUElement> cache = new LinkedHashMap<String, LFUElement >();
	private PriorityQueue<LFUElement> queue = new PriorityQueue<LFUElement>(comparator);
	private int size;
	private int miss = 0;
	private int hit = 0;
	
	public LFUCache(int size) {
		this.size = size;
	}
	
	public int getHit() {
		return hit;
	}
	
	public int getMiss() {
		return miss;
	}

	public void add(String request, int size) {
		if (cache.containsKey(request)) {
			LFUElement tmp = cache.get(request);
			queue.remove(tmp);
			LFUElement newElement = new LFUElement(tmp.request,tmp.frequency + 1);
			cache.put(request, newElement);
			queue.add(newElement);	
			hit++;
		} 
		else if (cache.size() == size) {
			LFUElement removeElement = queue.remove();
			cache.remove(removeElement.request);
			LFUElement newElement = new LFUElement(request, 1);
			queue.add(newElement);
			cache.put(request, newElement);
			miss++;
		}
		else {
			LFUElement newElement = new LFUElement(request, 1);
			queue.add(newElement);
			cache.put(request, newElement);
			miss++;
		}
	}

	public void print() {
		System.out.println(queue.toString());
	}

	public void writeInFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("cache_lfu.txt", "UTF-8");
		Iterator<String> it = cache.keySet().iterator();
		while(it.hasNext()) {
			writer.println(it.next());
		}
		writer.close();
	}
}