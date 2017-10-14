import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;




public class ServerThread extends Thread{
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutput oos;
	private ServerTest gameServer;
	String name;
	int teamnum;
	
	public ServerThread(Socket s,ServerTest gs){
		name=null;
		socket=s;
		gameServer=gs;
		try{
			oos=new ObjectOutputStream(s.getOutputStream());
			ois=new ObjectInputStream(s.getInputStream());
			this.start();
		}catch(IOException e){
			System.out.println("2");
			System.out.println(e.getMessage());
		}
	}
	
	public void sendMessage(Object o){
		try{
			oos.writeObject(o);
			oos.flush();
		}catch (IOException e) {
			System.out.println("ioe message sending: "+e.getMessage());
		}
	
	}
	
	
	public void run(){
		
		try{
			while(true){
				Object o=(Object)ois.readObject();
				
				System.out.println((String)o);
			}
		
		}catch (IOException e) {
			System.out.println("33");
			System.out.println(e.getMessage());
		}
		catch(ClassNotFoundException e){
			System.out.println("3");
			System.out.println(e.getMessage());
		}finally{
			try{
				if(socket!=null){
					socket.close();
				}
			}catch (IOException ioe) {
				System.out.println("4");
				System.out.println(ioe.getMessage());
			}
		}
		
		
	}
	
	
	
	
}
