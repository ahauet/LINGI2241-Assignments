/**
 * ServerMultiThread
 * 
 * Alexandre Hauet & Maximilien Roberti
 * 
 */
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;


public class ServerMultiThread {
	
	//Total response time of the server (needed to compute the average response time)
	private static long totalTime = 0;

	public static void main(String[] args) throws Exception {
		
	// Check the usage, we need one argument => the problem number
		if(args.length != 1) {
			System.err.println("Usage : java ServerMultiThread number_max_threads");
			System.exit(-1);
		}
			
		int number_max_threads = Integer.getInteger(args[0]);
		
		if(number_max_threads < 1) {
			System.err.println("The number of threads must be positif");
			System.exit(-1);
		}

		ServerSocket serverSocket = null;
		//Indicate the number of the client
		int numClient = 1;
		
		try {
			//Creation of a server socket with a backlog of 200
			serverSocket = new ServerSocket(22000,200);
			//Create a semaphore to not surpass the number of thread authorized
			Semaphore s  = new Semaphore(number_max_threads);
			//The server is running
			while (true) {
				System.out.println("Waiting....");
				//A client contact the server
				final Socket socket = serverSocket.accept();
				final int numRegisterClient = numClient;
				System.out.println("Received a  connection from  " + socket);
				//Creation of a thread to handle the connection
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							handleClientRequest(socket, numRegisterClient);
						} catch (Exception e) {
							e.printStackTrace();
						}  finally {
							//Release the semaphore
							s.release();
						}
					}
				}); 
				t.start(); // start a new thread
				numClient = numClient + 1;
			}
		}
		finally{
			if (serverSocket != null) serverSocket.close();
		}

	}
	
	
	public static void handleClientRequest(Socket socket, int numClient) throws Exception {
		
		int bytesRead;
		BufferedImage outputImage = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		long timeBeforeReading = 0;
		try {
			timeBeforeReading = System.currentTimeMillis();
			System.out.println("Client number : "+numClient );

			////////////////////
			// Read the image //
			////////////////////


			// create input stream
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
			while(sizeReaded != sizeIn){
				// read the image
				bytesRead = inputStream.read(imageAr, sizeReaded, sizeIn-sizeReaded);
				if(bytesRead >= 0){
					sizeReaded += bytesRead;
				}
			}
			long timeReading = System.currentTimeMillis() - timeBeforeReading;
			System.out.println("time to read : " + timeReading + " ms");

			///////////////////
			// Convert image //
			///////////////////

			long timeBeforeCalculation =  System.currentTimeMillis();
			BufferedImage inputImage = null;
			// change the image in color to black and white
			try {

				inputImage = ImageIO.read(new ByteArrayInputStream(imageAr));
				outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(),BufferedImage.TYPE_INT_RGB);
				for (int x = 0; x < inputImage.getWidth(); x++) {
					for (int y = 0; y < inputImage.getHeight(); y++) {
						int rgb = inputImage.getRGB(x, y);
						int blue = 0x0000ff & rgb;
						int green = 0x0000ff & (rgb >> 8);
						int red = 0x0000ff & (rgb >> 16);
						int lum = (int) (red * 0.299 + green * 0.587 + blue * 0.114);
						outputImage.setRGB(x, y, lum | (lum << 8) | (lum << 16));
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			// Create the byteArrayOutputStream
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			// Convert BufferedImage to byteArrayOutputStream
			ImageIO.write(outputImage, "jpg", byteArrayOutputStream);
			// Convert size in byte[]
			byte[] sizeOut = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();

			long calculationTime =  System.currentTimeMillis()-timeBeforeCalculation;
			System.out.println("Calculation time : " + calculationTime + " ms"); 

			////////////////////
			// Send the image //
			////////////////////

			// Time before sending the image
			long timeBeforeSend = System.currentTimeMillis();
			// Create output stream
			outputStream = socket.getOutputStream();
			// Send size to server
			outputStream.write(sizeOut);
			// Send image to server
			outputStream.write(byteArrayOutputStream.toByteArray());
			outputStream.flush();

			long timeToSend = System.currentTimeMillis()-timeBeforeSend;
			System.out.println("Time to send : " + timeToSend + " ms");


			System.out.println("close the socket");
		}
		finally {
			if (inputStream!=null) inputStream.close();
			if (outputStream!=null) outputStream.close();
			if (socket!=null){ 
				socket.close();
				long timeServer = System.currentTimeMillis()-timeBeforeReading;
				System.out.println("time client "+ numClient +" in server : " + timeServer + " ms");
				totalTime += timeServer;
				System.out.println("Total time = " + totalTime + " ms\n");
			}
		}
		
	}

}
