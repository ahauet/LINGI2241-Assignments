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
	private static String[] files = {"picture50x32.jpg","picture75x48.jpg", 
		"picture150x96.jpg", "picture250x160.jpg", "picture400x300.jpg",
		"picture640x409.jpg", "picture800x511.jpg", "picture1024x645.jpg", "picture1600x1022.jpg"};
	// Name for the solution of a problem
	private static String[] result_files = {"picture50x32_result.jpg","picture75x48_result.jpg", 
		"picture150x96_result.jpg", "picture250x160_result.jpg", "picture400x300_result.jpg",
		"picture640x409_result.jpg", "picture800x511_result.jpg", "picture1024x645_result.jpg", "picture1600x1022_result.jpg"};

	private static Random rand = new Random();
	private static double lambda = 0.5;

	public static double getDelay() {
		return  Math.log(1-rand.nextDouble())/(-lambda);
	}
	public static void Client(String fileName) throws Exception
	{
		Socket socket = null;
		OutputStream outputStream = null;
		BufferedImage image = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try{
		socket = new Socket("localhost", 13085);

		//Send to server

		outputStream = socket.getOutputStream();
		// Read the image from the file
		image = ImageIO.read(new File(fileName));
		// Create the byteArrayOutputStream
		byteArrayOutputStream = new ByteArrayOutputStream();
		// Convert BufferedImage to byteArrayOutputStream
		ImageIO.write(image, "png", byteArrayOutputStream);
		// Convert size in byte[]
		System.out.println("size avant envoit :"+byteArrayOutputStream.size());
		byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
		// Send size to server
		outputStream.write(size);
		// Send image to server
		outputStream.write(byteArrayOutputStream.toByteArray());
		outputStream.flush();

		//Receive to server

		InputStream inputStream = socket.getInputStream();
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
		System.out.println("size apres reçu :"+imageAr.length);

		BufferedImage outputImage = ImageIO.read(new ByteArrayInputStream(imageAr));
		ImageIO.write(outputImage, "png", new File("result_"+fileName));
		System.out.println("close socket");
		}
		finally{
			if (outputStream != null) outputStream.close();
			if (socket!=null) socket.close();
		}

	}

	public static void main(String[] args) throws Exception {
		final Random r = new Random();

		while(true) {
			
			Thread t = new Thread(new Runnable() {
			     public void run() {
			    	 int problem_number = r.nextInt(files.length - 1);
			    	 try {
						Client(files[problem_number]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}// code goes here.
			     }
			}); 
			
			t.start();
			
			Thread.sleep((long) getDelay());
		}
	}

}