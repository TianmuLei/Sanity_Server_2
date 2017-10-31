package server;
/*
 * Copyright (c) 2010-2017 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice.Info;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.jdbc.object.UpdatableSqlQuery;


public class Server extends WebSocketServer {
	private UserDAO userDao;
	private BudgetDAO budgetDao;
	private TransactionDAO transactionDao;

	public Server( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
		userDao = new UserDAO();
		budgetDao = new BudgetDAO();
		transactionDao = new TransactionDAO();
	}

	public Server( InetSocketAddress address ) {
		super( address );
		userDao = new UserDAO();
		budgetDao = new BudgetDAO();
		transactionDao = new TransactionDAO();
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		broadcast( "new connection: " + handshake.getResourceDescriptor() );
		System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected!" );
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		broadcast( conn + " disconnect" );
		System.out.println( conn + " disconnect" );
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {	
		JSONObject JSONMessage;
		try{
			JSONMessage = new JSONObject(message);
			String message1 = JSONMessage.getString("function");
			if(message1.equals("register")){		
				User user=new User(JSONMessage.getJSONObject("information"));
				JSONObject returnMessage=userDao.Register(user);	
				sendMessagetoClient(conn,returnMessage);
			}
			else if(message1.equals("login")){	
				User user = new User(JSONMessage.getJSONObject("information"));
				JSONObject returnMessage=userDao.Login(user);
				if(returnMessage.getString("status").equals("success")){
					JSONObject info=budgetDao.getEverything(user, 0);
					System.out.println("FINISH FETCHING");
					returnMessage.put("information", info.getJSONObject("information"));
				}
				
				
				sendMessagetoClient(conn,returnMessage);
			}
			else if(message1.equals("createBudget")){
				Budget toAdd = new Budget(JSONMessage.getJSONObject("information"));
				JSONObject returnMessage= budgetDao.createBudget(toAdd);
				User user = new User(JSONMessage.getJSONObject("information").getString("email"));
				if(returnMessage.getString("status").equals("success")){
					JSONObject info=budgetDao.getEverything(user, 0);
					returnMessage.put("information", info.getJSONObject("information"));
				}
				
				sendMessagetoClient(conn,returnMessage);
			}
			else if (message1.equals("addTransaction")){
				Transaction toAdd = new Transaction(JSONMessage.getJSONObject("information"));
				JSONObject returnMessage = transactionDao.createTransaction(toAdd);
				User user = new User(JSONMessage.getJSONObject("information").getString("email"));
				if(returnMessage.getString("status").equals("success")){
					JSONObject info=budgetDao.getEverything(user, 0);
					returnMessage.put("information", info.getJSONObject("information"));
				}
				sendMessagetoClient(conn, returnMessage);
			}
			else if(message1.equals("requestBudgetList")){
				User user = new User(JSONMessage.getJSONObject("information").getString("email"));
				JSONObject returnMessage = budgetDao.getBudgetList(user);
				sendMessagetoClient(conn, returnMessage);
			}
			else if(message1.equals("requestBudget")){
				User user = new User(JSONMessage.getJSONObject("information").getString("email"));
				Budget budget=new Budget(JSONMessage.getJSONObject("information").getString("name"));
				JSONObject returnMessage=budgetDao.CateDao.getCategories(user, budget);
				sendMessagetoClient(conn, returnMessage);
			}
			else if(message1.equals("requestCategory")){
				User user = new User(JSONMessage.getJSONObject("information").getString("email"));
				Budget budget= new Budget(JSONMessage.getJSONObject("information").getString("budget"));
				Category category = new Category(JSONMessage.getJSONObject("information").getString("category"));
				JSONObject returnMessage = transactionDao.getTransactions(user,budget, category);
				sendMessagetoClient(conn, returnMessage);
			}
			else if(message1.equals("deleteTransaction")){
				Transaction transaction = new Transaction(JSONMessage.getJSONObject("information"));
				JSONObject returnMessage = transactionDao.deleteTransaction(transaction);
				sendMessagetoClient(conn, returnMessage);
			}
			else if(message1.equals("requestEverything")){
				User user= new User(JSONMessage.getJSONObject("information").getString("email"));
				JSONObject returnMessage=budgetDao.getEverything(user, 0);
				sendMessagetoClient(conn, returnMessage);
			}
			else if(message1.equals("changeUsername")){	
				User user = new User(JSONMessage.getJSONObject("information"));
				JSONObject returnMessage=userDao.changeUsername(user);
				sendMessagetoClient(conn,returnMessage);
			}
			else if(message1.equals("changePassword")){	
				User user1 = new User(JSONMessage.getJSONObject("information1"));
				User user2 = new User(JSONMessage.getJSONObject("information2"));
				JSONObject returnMessage=userDao.changePassword(user1, user2);
				sendMessagetoClient(conn,returnMessage);
			}
			else if(message1.equals("requestHistory")){	
				User user= new User(JSONMessage.getJSONObject("information").getString("email"));
				JSONObject returnMessage1=budgetDao.getEverything(user, 1);
				JSONObject returnMessage2=budgetDao.getEverything(user, 2);
				JSONObject returnMessage3=budgetDao.getEverything(user, 3);
				JSONObject returnMessage4=budgetDao.getEverything(user, 4);
				JSONObject returnMessage5=budgetDao.getEverything(user, 5);
				JSONObject returnMessage6=budgetDao.getEverything(user, 6);
				JSONObject messagenew= new JSONObject();
				messagenew.put("function", "requestHistory");
				messagenew.put("status", "success");
				messagenew.put("information1", returnMessage1.getJSONObject("information"));
				messagenew.put("information2", returnMessage2.getJSONObject("information"));
				messagenew.put("information3", returnMessage3.getJSONObject("information"));
				messagenew.put("information4", returnMessage4.getJSONObject("information"));
				messagenew.put("information5", returnMessage5.getJSONObject("information"));
				messagenew.put("information6", returnMessage6.getJSONObject("information"));
				sendMessagetoClient(conn,messagenew);
			}
			else if(message1.equals("editBudget")){
				Budget toAdd = new Budget(JSONMessage.getJSONObject("information"));
				Budget old=new Budget(JSONMessage.getJSONObject("information").getString("oldName"));
				JSONObject returnMessage=budgetDao.editBudget(toAdd, old);
				User user = new User(JSONMessage.getJSONObject("information").getString("email"));
				if(returnMessage.getString("status").equals("success")){
					JSONObject info=budgetDao.getEverything(user, 0);
					returnMessage.put("information", info.getJSONObject("information"));
				}
				sendMessagetoClient(conn, returnMessage);
			}
			else if(message1.equals("deleteBudget")){
				String email = JSONMessage.getJSONObject("information").getString("email");
				String budgetName = JSONMessage.getJSONObject("information").getString("name");
				JSONObject returnMessage = budgetDao.deleteBudget(email, budgetName);
				if(returnMessage.getString("status").equals("success")){
					JSONObject info=budgetDao.getEverything(new User(email), 0);
					returnMessage.put("information", info.getJSONObject("information"));
				}
				sendMessagetoClient(conn, returnMessage);
			}
			else if (message1.equals("deleteCategory")){
				String email = JSONMessage.getJSONObject("information").getString("email");
				String budgetName = JSONMessage.getJSONObject("information").getString("budgetName");
				String categoryName = JSONMessage.getJSONObject("information").getString("categoryName");
				JSONObject returnMessage =budgetDao.CateDao.deleteCategories(email, budgetName, categoryName);
				if(returnMessage.getString("status").equals("success")){
					JSONObject info=budgetDao.getEverything(new User(email), 0);
					returnMessage.put("information", info.getJSONObject("information"));
				}
				sendMessagetoClient(conn, returnMessage);
			}
			else if (message1.equals("editCategory")){
				String email = JSONMessage.getJSONObject("information").getString("email");
				String budgetName = JSONMessage.getJSONObject("information").getString("budgetName");
				String oldName = JSONMessage.getJSONObject("information").getString("categoryOldName");
				String newName = JSONMessage.getJSONObject("information").getString("categoryNewName");
				Double newLimit = JSONMessage.getJSONObject("information").getDouble("limit");
				JSONObject returnMessage = budgetDao.CateDao.editCategory(email, oldName, newName, budgetName, newLimit);
				if(returnMessage.getString("status").equals("success")){
					JSONObject info=budgetDao.getEverything(new User(email), 0);
					returnMessage.put("information", info.getJSONObject("information"));
				}
				sendMessagetoClient(conn, returnMessage);
			}
			else if (message1.equals("addCategory")){
				String email =JSONMessage.getJSONObject("information").getString("email");
				String budgetName = JSONMessage.getJSONObject("information").getString("budgetName");
				String categoryName = JSONMessage.getJSONObject("information").getString("categoryName");
				Double limit = JSONMessage.getJSONObject("information").getDouble("limit");
				JSONObject returnMessage=budgetDao.CateDao.addSingleCategory(email, budgetName,categoryName,limit);
				if(returnMessage.getString("status").equals("success")){
					JSONObject info=budgetDao.getEverything(new User(email), 0);
					returnMessage.put("information", info.getJSONObject("information"));
				}
				sendMessagetoClient(conn, returnMessage);
			}
		}catch(JSONException e){
			
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onFragment( WebSocket conn, Framedata fragment ) {
		System.out.println( "received fragment: " + fragment );
	}

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	@Override
	public void onStart() {
		System.out.println("Server started!");
	}
	
	private void sendMessagetoClient(WebSocket conn,JSONObject Message){
		 List<WebSocket> client = new ArrayList<WebSocket>();
		 client.add(conn);
		 broadcast(Message.toString(),client);
	}
	
	public static void main( String[] args ) throws InterruptedException , IOException {
		WebSocketImpl.DEBUG = true;
		int port = 9999; // 843 flash policy port
		Server s = new Server( port );
		s.start();
		System.out.println( "ChatServer started on port: " + s.getPort() );

		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String in = sysin.readLine();
			s.broadcast( in );
			if( in.equals( "exit" ) ) {
				s.stop();
				break;
			}
		}
	}

}
