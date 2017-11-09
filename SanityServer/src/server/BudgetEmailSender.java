package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class BudgetEmailSender extends Thread{
	JSONObject budget;
	private final String createCategoryStatement="";
	private final String createTransactionStatement ="";
	public BudgetEmailSender(JSONObject budget){
		this.budget=budget;
	}
	@Override
	public void run(){
		String user="",toEmail="";
		try{
			user= budget.getString("user");
			toEmail = budget.getString("email");
			Client client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter("api", "my-key"));
			    
			WebResource webResource = client.resource("https://api.mailgun.net/v3/sandbox1a3192acb7454b4cbe565ad1ee6369e4.mailgun.org/messages");
			MultivaluedMapImpl formData = new MultivaluedMapImpl();
			formData.add("from", "Mailgun Sandbox <postmaster@sandbox1a3192acb7454b4cbe565ad1ee6369e4.mailgun.org>");
			// formData.add("to", "Jiaxinch <jiaxinch@usc.edu>");
			String to=user+" <"+toEmail+">";
			System.out.println(to);
			formData.add("to", to);
			String title ="Budget "+budget.getString("name")+" Summary";
			formData.add("subject", title);
			InputStream in = getClass().getResourceAsStream("/emailHTML.html");
			BufferedReader input = new BufferedReader(new InputStreamReader(in));
			String emailToSend= new String();
			String temp = input.readLine();
			while(temp!=null){
				emailToSend+=temp;
				temp =input.readLine();
			}
			emailToSend=emailToSend.replace("$$$$$$",user);
			formData.add("html",emailToSend);
			ClientResponse clientResponse=webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
			                                        post(ClientResponse.class, formData);
			System.out.println(clientResponse.getClientResponseStatus());
			   // System.out.println( clientResponse.getResponseStatus());
			System.out.println("Done");
			
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("JSON error in BudgetEmail sender");
		}catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("problems in email sender");
		}
		
		
	}
}
