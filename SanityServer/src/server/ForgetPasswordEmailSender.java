package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class ForgetPasswordEmailSender extends Thread{
	String code, toEmail;
	public ForgetPasswordEmailSender(String code, String toEmail){
		this.code=code;
		this.toEmail=toEmail;
	}
	@Override
	public void run() {
		super.run();
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("api", "key-59d73d0d606d0df987743b0c84305aaf"));
		    
		WebResource webResource = client.resource("https://api.mailgun.net/v3/sandbox1a3192acb7454b4cbe565ad1ee6369e4.mailgun.org/messages");
		MultivaluedMapImpl formData = new MultivaluedMapImpl();
		formData.add("from", "Mailgun Sandbox <postmaster@sandbox1a3192acb7454b4cbe565ad1ee6369e4.mailgun.org>");
		// formData.add("to", "Jiaxinch <jiaxinch@usc.edu>");
		String to="user"+" <"+toEmail+">";
		System.out.println(to);
		formData.add("to", to);
		formData.add("subject", "Sanity-Forget Password Verification Code");
	
			String emailToSend="Your Verification Code is:"+code;
			formData.add("html",emailToSend);
	
		ClientResponse clientResponse=webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
		                                        post(ClientResponse.class, formData);
		System.out.println(clientResponse.getClientResponseStatus());
		   // System.out.println( clientResponse.getResponseStatus());
		System.out.println("Done");
		
	
	}
	public static void main(String[] args){
		ForgetPasswordEmailSender sender = new ForgetPasswordEmailSender("123456", "tianmu.lei2@gmail.com");
		sender.start();
	}
}
