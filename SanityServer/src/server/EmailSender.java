package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;


public class EmailSender extends Thread{
	String user, toEmail;
	public EmailSender(String user, String toEmail){
		this.user=user;
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
		   String to=user+" <"+toEmail+">";
		   System.out.println(to);
		   formData.add("to", to);
		    formData.add("subject", "Welcome to Sanity");
		    try{
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
		    }catch (IOException e) {
		    	System.out.println(e.getMessage());
				System.out.println("problems in email sender");
			}
		    
		    
		    
		   
		    ClientResponse clientResponse=webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
		                                        post(ClientResponse.class, formData);
		    System.out.println(clientResponse.getClientResponseStatus());
		   // System.out.println( clientResponse.getResponseStatus());
		   
		     System.out.println("Done");
		     return;
		
		
	/*	final String username = "sanity.absolutea@gmail.com";
		final String password = "sanityabsolutea";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
		try{
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("sanity.absolutea@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(toEmail));
			message.setSubject("Welcome to $anity");
//			byte[] encoded = Files.readAllBytes(Paths.get("/emailHTML.html"));
//			String emailToSend= new String(encoded,Charset.defaultCharset());
			InputStream in = getClass().getResourceAsStream("/emailHTML.html");
			BufferedReader input = new BufferedReader(new InputStreamReader(in));
			String emailToSend= new String();
			String temp = input.readLine();
			while(temp!=null){
				emailToSend+=temp;
				temp =input.readLine();
			}
			emailToSend=emailToSend.replace("$$$$$$",user);
			message.setContent(
					emailToSend,
		             "text/html");
			Transport.send(message);
			System.out.println("Done");
		}catch (MessagingException e) {
			throw new RuntimeException(e);
		}catch(IOException e){
			System.out.println(e.getMessage());
			System.out.println("problems in email sender");
		}*/	
	}
	public static void main(String[] args){
		EmailSender sender = new EmailSender("mu", "tianmu.lei2@gmail.com");
		sender.start();
	}
	
	
}



