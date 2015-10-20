import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task1_2 {

	
	public static void main(String []args) throws IOException{
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		//Length of warm-up phase
		int x = Integer.parseInt(args[0]);
		//Cache size
		int n = Integer.parseInt(args[1]);
		
		double nbProcessedRequest = 0;
		
		LFUCache lfu = new LFUCache(n, x);
		LRUCache lru = new LRUCache(n, x);
		RemoveLargestFirstCache rlf = new RemoveLargestFirstCache(n, x);
		
		
		String s;
		while ((s = in.readLine()) != null && s.length() != 0) {
			String request[] = s.split(" ");
			lfu.add(request[0], Integer.parseInt(request[1]));
			lru.add(request[0], Integer.parseInt(request[1]));
			rlf.add(request[0], Integer.parseInt(request[1]));
			nbProcessedRequest++;
		}
		lfu.writeInFile();
		lru.writeInFile();
		rlf.writeInFile();

		double lruHitRate = lru.getHit()/(nbProcessedRequest-x) * 100;
		double lruByteRate = lru.getHitByte()/((double) lru.getCacheByte()) * 100;
		double lfuHitRate = lfu.getHit()/(nbProcessedRequest-x) * 100;
		double lfuByteRate = lfu.getHitByte()/((double) lfu.getCacheByte()) * 100;
		double rlfHitRate = rlf.getHit()/(nbProcessedRequest-x) * 100;
		double rlfByteRate = rlf.getHitByte()/((double) rlf.getCacheByte()) * 100;

		
		System.out.println("LRU Hit rate :" + lruHitRate +"%");
		System.out.println("LRU Byte hit rate :" + lruByteRate +"%");
		System.out.println("LFU Hit rate :" + lfuHitRate + "%");
		System.out.println("LFU Byte hit rate :" + lfuByteRate + "%");
		System.out.println("Size-based Hit rate :" + rlfHitRate + "%");
		System.out.println("Size-based Byte hit rate :" + rlfByteRate + "%");

		
	}
}