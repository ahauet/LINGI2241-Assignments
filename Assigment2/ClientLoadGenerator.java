/**
 * ClientLoadGenerator
 * 
 * Alexandre Hauet & Maximilien Roberti
 * 
 */
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.imageio.ImageIO;

public class ClientLoadGenerator {
	static int bytesRead;
	
	// Each file names in this array correspond to a problem 
	// For example : 	file name at index 0 is the problem number 1
	//								file name at index x is the problem number x+1
	private static String[] files = {"5x4.jpg", "10x7.jpg","25x17.jpg","50x34.jpg","75x52.jpg","100x69.jpg", "150x102.jpg",
			"200x137.jpg", "250x171.jpg","300x205.jpg", "400x247.jpg", "500x341.jpg", "600x411.jpg", "700x478.jpg", "800x548.jpg",
			"900x617.jpg","1000x683.jpg", "1100x754.jpg", "1200x820.jpg", "1300x891.jpg","1400x957.jpg","1500x1028.jpg","1600x1094.jpg",
			"1700x1165.jpg","1800x1231.jpg","1900x1302.jpg","2000x1368.jpg","2100x1439.jpg","2200x1507.jpg","2300x1576.jpg", "2400x1641.jpg",
			"2500x1713.jpg","2600x1781.jpg","2700x1850.jpg","2800x1915.jpg","2900x1987.jpg","3000x2055.jpg","3100x2123.jpg","3200x2188.jpg",
			"3200x2191.jpg","3300x2259.jpg","3400x2327.jpg","3500x2395.jpg","3600x2462.jpg","3600x2463.jpg","3700x2531.jpg","3800x2599.jpg",
			"3900x2667.jpg","4000x2735.jpg"		
	};

	private static Random rand = new Random();
	private static double lambda = 0.5; // Default value 

	public static double getDelay() {
		return  Math.log(1-rand.nextDouble())/(-lambda);
	}
	
	public static void Client(String fileName,int clientNumber, String address) throws Exception {
		Socket socket = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		BufferedImage image = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		long beginTime = 0;
		long timeToSend = 0;
		long timeBeforeReceive = 0;
		long timeToReceive = 0;
		long timeBeforeWriteOnDisk = 0;
		long timeToWriteOnDisk = 0;
		try{
			
			beginTime = System.currentTimeMillis();
			// Connect to server 
			socket = new Socket(address, 22000);

			//////////////////////////
			// Send image to server //
			//////////////////////////

			outputStream = socket.getOutputStream();
			// Read the image from the file
			image = ImageIO.read(new File("images//"+ fileName));
			// Create the byteArrayOutputStream
			byteArrayOutputStream = new ByteArrayOutputStream();
			// Convert BufferedImage to byteArrayOutputStream
			ImageIO.write(image, "jpg", byteArrayOutputStream);
			// Convert size in byte[]
			byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
			// Send size to server
			outputStream.write(size);
			// Send image to server
			outputStream.write(byteArrayOutputStream.toByteArray());
			outputStream.flush();

			timeToSend = System.currentTimeMillis()-beginTime;
			
			///////////////////////////////
			// Receive image from server //
			///////////////////////////////
			
			timeBeforeReceive = System.currentTimeMillis();

			inputStream = socket.getInputStream();
			// size of the in byte[] of the file received
			byte[] sizeAr = new byte[4];
			// read the size
			inputStream.read(sizeAr);
			// convert the size in byte[] to int
			int sizeIn = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
			// Image in bite[]
			byte[] imageAr = new byte[sizeIn];
			// size of the image already read
			int sizeReaded = 0;
			// while the image is not completely read
			while(bytesRead > -1 && sizeReaded != sizeIn){
				// read part by part the image
				bytesRead = inputStream.read(imageAr, sizeReaded, sizeIn-sizeReaded);

				if(bytesRead >= 0){
					sizeReaded += bytesRead;
				}
			}
			timeToReceive = System.currentTimeMillis()- timeBeforeReceive;

			BufferedImage outputImage = ImageIO.read(new ByteArrayInputStream(imageAr));
			
			timeBeforeWriteOnDisk =  System.currentTimeMillis();
			
			ImageIO.write(outputImage, "jpg", new File("final_"+fileName));
			
			timeToWriteOnDisk = System.currentTimeMillis() - timeBeforeWriteOnDisk;
		}
		finally{
			if (inputStream != null) inputStream.close();
			if (outputStream != null) outputStream.close();
			if (socket!=null) socket.close();
			long totalTimeClient = System.currentTimeMillis() - beginTime;
			// print measurements
			System.out.println("Client "+ clientNumber+ " dificulty : "+fileName);
			System.out.println("time to send " +timeToSend+" ms");
			System.out.println("Time to reveive :" + timeToReceive + " ms");
			System.out.println("Time to write on disk :" + timeToWriteOnDisk + " ms");
			System.out.println("Total time client :" + totalTimeClient + " ms\n");
		}

	}

	public static void main(String[] args) throws Exception {
		
		// Check the usage, we need two arguments => lambda and the ip address of the server
		if(args.length != 2) {
			System.err.println("Usage : java client lambda address");
			System.exit(-1);
		}
		
		lambda = Double.parseDouble(args[0]);
		String address = args[1];
		
		final Random r = new Random();
		int clientNumber=0;
		
		//Creation of 100 clients
		while(clientNumber<100) {
			final int registerNumber = clientNumber;
			Thread t = new Thread(new Runnable() {
				public void run() {
					int problem_number = r.nextInt(files.length - 1);
					try {
						Client(files[problem_number],registerNumber, address);
					} catch (Exception e) {
						System.err.println("Problem with the picture " + files[problem_number]);
						e.printStackTrace();
					}
				}
			}); 

			t.start();
			// Delay in seconds
			Thread.sleep((long) (getDelay() * 1000));
			clientNumber = clientNumber + 1; 
		}
	}

}