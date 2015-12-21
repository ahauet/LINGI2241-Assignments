import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

public class Server2 {

  public final static int SOCKET_PORT = 13268;  // you may change this
  public final static String FILE_TO_SEND = "/Users/maximilienroberti/Documents/workspace/Archi_Group_Work_2/filetosend/00003a-paysage-automne.jpg";  // you may change this
  public final static String FILE = "/Users/maximilienroberti/Documents/workspace/Archi_Group_Work_2/filetosend/00003a-paysage-automne6.jpg";
  public final static int FILE_SIZE = 6022386;
  
  
  
  // convert byte[] to BufferedImage
  private static BufferedImage createImageFromBytes(byte[] imageData) {
	    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
	    try {
	        return ImageIO.read(bais);
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}
  
  public static void main (String [] args ) throws IOException {
	  
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;
    ServerSocket servsock = null;
    Socket sock = null;
    
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    
    int bytesRead;
    int current = 0;
    byte[] byteToSend = null;
    
    try {
      servsock = new ServerSocket(SOCKET_PORT);
      while (true) {
        System.out.println("Waiting...");
        try {
          sock = servsock.accept();
          System.out.println("Accepted connection : " + sock);
          // receive file
          byte [] byteArrayToReceive  = new byte [FILE_SIZE];
          InputStream inputStream = sock.getInputStream();
          bytesRead = inputStream.read(byteArrayToReceive,0,byteArrayToReceive.length);
          current = bytesRead;
          System.out.println("flag1");
          //do {
        	//  System.out.println(bytesRead);
              //bytesRead = inputStream.read(byteArrayToReceive, current, (byteArrayToReceive.length-current));
             // if(bytesRead >= 0) current += bytesRead;
           //} while(bytesRead > -1);
          System.out.println("flag2");
          //fos = new FileOutputStream(FILE);
          //bos = new BufferedOutputStream(fos);
          
          //System.out.println("File " + FILE + " downloaded (" + current + " bytes read)");
          try {
              
        	  BufferedImage inputImage = createImageFromBytes(byteArrayToReceive);
        	  BufferedImage outputImage = new BufferedImage(
        			  inputImage.getWidth(), inputImage.getHeight(),
        			  BufferedImage.TYPE_INT_RGB);
        	  for (int x = 0; x < inputImage.getWidth(); x++) {
                  for (int y = 0; y < inputImage.getHeight(); y++) {
                      int rgb = inputImage.getRGB(x, y);
                      int blue = 0x0000ff & rgb;
                      int green = 0x0000ff & (rgb >> 8);
                      int red = 0x0000ff & (rgb >> 16);
                      int lum = (int) (red * 0.299 + green * 0.587 + blue * 0.114);
                      outputImage
                              .setRGB(x, y, lum | (lum << 8) | (lum << 16));
                  }
              }
        	  ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	  ImageIO.write(outputImage, "jpg", baos);
        	  byteToSend = baos.toByteArray();
        	  System.out.println("flag3");
        	  //byte[] imageBytes = ((DataBufferByte) outputImage.getData().getDataBuffer()).getData();
          } catch (IOException e) {
              e.printStackTrace();
          }
          //bos.write(byteToSend, 0 , byteToSend.length);
          //bos.flush();
          // send file
          os = sock.getOutputStream();
          System.out.println("Sending " + FILE_TO_SEND + "(" + byteToSend.length + " bytes)");
          os.write(byteToSend,0,byteToSend.length);
          os.flush();
          System.out.println("Done.");
        }
        finally {
          if (bis != null) bis.close();
          if (os != null) os.close();
          if (sock!=null) sock.close();
        }
      }
    }
    finally {
      if (servsock != null) servsock.close();
    }
  }
}