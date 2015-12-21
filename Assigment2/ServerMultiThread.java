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



	public static void main(String[] args) throws Exception {

		ServerSocket serverSocket = null;
		int numClient = 0;
		
		try {
			serverSocket = new ServerSocket(13085,0);
			final Semaphore s = new Semaphore(5);// the number of thread is 5.
			while (true) {
				System.out.println("Waiting....");
				
				final Socket socket = serverSocket.accept();;
				final int numRegisterClient = numClient;
				System.out.println("Received a  connection from  " + socket);
				s.acquire();
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							handleClientRequest(socket, numRegisterClient);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							s.release();
						}
					}
				}); 

				t.start(); // start a new thread
				System.out.println("Number of thread: "+t.activeCount());
				
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
			ImageIO.write(outputImage, "png", byteArrayOutputStream);
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
				System.out.println("time client "+ numClient +" in server : " + timeServer + " ms\n");
			}
		}
		
	}

}
