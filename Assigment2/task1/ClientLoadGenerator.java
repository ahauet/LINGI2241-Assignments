import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.plaf.SliderUI;

public class ClientLoadGenerator {
	static int bytesRead;
	
	// Each file names in this array correspond to a problem 
	// For example : 	file name at index 0 is the problem number 1
	//								file name at index x is the problem number x+1
	private static String[] files = {"50x34.png","75x51.png","105x102.png","300x205.png","700x478.png", "500x341.png"
			,"1000x683.png","1200x820.png","1400x957.png","1600x1094.png","1800x1231.png","2000x1368.png",
			"2400x1641.png","2800x1915.png","3200x2188.png","3600x2462.png	","4000x2735.png","4500x3077.png	"
			,"6000x4103.png","8134x5563.png"};

	private static Random rand = new Random();
	private static double lambda = 0.5;

	public static double getDelay() {
		return  Math.log(1-rand.nextDouble())/(-lambda);
	}
	public static void Client(String fileName,int clientNumber) throws Exception
	{
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
			socket = new Socket("localhost", 13085);

			//////////////////////////
			// Send image to server //
			//////////////////////////

			outputStream = socket.getOutputStream();
			// Read the image from the file
			image = ImageIO.read(new File(fileName));
			// Create the byteArrayOutputStream
			byteArrayOutputStream = new ByteArrayOutputStream();
			// Convert BufferedImage to byteArrayOutputStream
			ImageIO.write(image, "png", byteArrayOutputStream);
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
			
			ImageIO.write(outputImage, "png", new File("final_"+fileName));
			
			timeToWriteOnDisk = System.currentTimeMillis() - timeBeforeWriteOnDisk;
		}
		finally{
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
		final Random r = new Random();
		int clientNumber=0;
		
		while(clientNumber<100) {
			final int registerNumber = clientNumber;
			Thread t = new Thread(new Runnable() {
				public void run() {
					int problem_number = r.nextInt(files.length - 1);
					try {
						Client(files[problem_number],registerNumber);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}// code goes here.
				}
			}); 

			t.start();

			Thread.sleep((long) getDelay());
			clientNumber = clientNumber + 1; 
		}
	}

}