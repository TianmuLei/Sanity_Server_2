
import java.net.ServerSocket;
import java.net.Socket;
import org.java_websocket.server.WebSocketServer;


import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;





public class ServerTest {
	
	 public static void main(String[] args){
		 try{
			 ServerSocket server = new ServerSocket(9999);

		        System.out.println("Server has started on 127.0.0.1:80.\r\nWaiting for a connection...");

		        Socket client = server.accept();


		        System.out.println("A client connected.");
		        while (true) {
				//
					
				}
		 }catch (Exception e) {
			// TODO: handle exception
		}
	       
	    }
	
}
