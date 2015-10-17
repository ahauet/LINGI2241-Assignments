import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;

public class Task1_1 {

	
	public static void main(String []args) throws IOException{
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		//Length of warm-up phase
		int x = Integer.parseInt(args[0]);
		//Cache size
		int n = Integer.parseInt(args[1]);
		
		int nbProcessedRequest = 0;
		
		LFUCache lfu = new LFUCache(n);
		LRUCache lru = new LRUCache(n);
		
		
		String s;
		while ((s = in.readLine()) != null && s.length() != 0) {
			if (x == 0) {
				String request[] = s.split(" ");
				lfu.add(request[0]);
				lru.add(request[0]);
				nbProcessedRequest++;
			} else {
				x--;
			}
		}
		
		lfu.writeInFile();
		lru.writeInFile();
		
		long lruHitRate = Long.valueOf(lru.getHit()/nbProcessedRequest * 100);
		long lfuHitRate = Long.valueOf(lfu.getHit()/nbProcessedRequest * 100);
		
		System.out.println("LRU Hit rate :" + lruHitRate);
		System.out.println("LFU Hit rate :" + lfuHitRate);
		
	}
}