import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.imageio.ImageIO;

public class Client2 {

  public final static int SOCKET_PORT = 13268;      // you may change this
  public final static String SERVER = "127.0.0.1";  // localhost
  public final static String
       FILE_TO_RECEIVED = "/Users/maximilienroberti/Documents/workspace/Archi_Group_Work_2/filetosend/00003a-paysage-automne2.jpg";  // you may change this, I give a
                                                            // different name because i don't want to
                                                            // overwrite the one used by server...
  		
  public final static String
  FILE_TO_RECEIVED2 = "/Users/maximilienroberti/Documents/workspace/Archi_Group_Work_2/filetosend/00003a-paysage-automne3.jpg";
  public final static String FILE_TO_SEND = "/Users/maximilienroberti/Documents/workspace/Archi_Group_Work_2/filetosend/00003a-paysage-automne1.jpg";
  public final static int FILE_SIZE = 6022386; // file size temporary hard coded
                                               // should bigger than the file to be downloaded
  
  

  public static void main (String [] args ) throws IOException {
    int bytesRead;
    int current = 0;
    
    FileInputStream fileInputStream = null;
    BufferedInputStream buffuredInputStream = null;
    OutputStream outputStream = null;
    
    
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    Socket sock = null;
    try {
      sock = new Socket(SERVER, SOCKET_PORT);
      System.out.println("Connecting...");
      
      // send file
      File fileToSend = new File (FILE_TO_SEND);
      byte [] byteArrayToSend  = new byte [(int)fileToSend.length()];
      fileInputStream = new FileInputStream(fileToSend);
      buffuredInputStream = new BufferedInputStream(fileInputStream);
      buffuredInputStream.read(byteArrayToSend,0,byteArrayToSend.length);
      outputStream = sock.getOutputStream();
      System.out.println("Sending to server" + FILE_TO_SEND + "(" + byteArrayToSend.length + " bytes)");
      
      outputStream.write(byteArrayToSend,0,byteArrayToSend.length);
      outputStream.flush();
      System.out.println("Done.");
      
      // receive file
      System.out.println("Flag1");
      byte [] mybytearray  = new byte [FILE_SIZE];
      InputStream is = sock.getInputStream();
      fos = new FileOutputStream(FILE_TO_RECEIVED);
      bos = new BufferedOutputStream(fos);
      System.out.println("Flag1.2");
      bytesRead = is.read(mybytearray,0,mybytearray.length);
      System.out.println("Flag1.3");
      System.out.println(bytesRead);
      current = bytesRead;
      System.out.println("Flag2");
      do {
         bytesRead =
            is.read(mybytearray, current, (mybytearray.length-current));
         System.out.println(bytesRead);
         if(bytesRead >= 0) current += bytesRead;
      } while(bytesRead > -1);
 
     
      bos.write(mybytearray, 0 , current);
      bos.flush();
      System.out.println("File " + FILE_TO_RECEIVED
          + " downloaded (" + current + " bytes read)");
    }
    finally {
      if (buffuredInputStream != null) buffuredInputStream.close();
      if (outputStream != null) outputStream.close();
      if (fos != null) fos.close();
      if (bos != null) bos.close();
      if (sock != null) sock.close();
    }
  }

}