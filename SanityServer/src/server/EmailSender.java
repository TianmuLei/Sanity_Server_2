package server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailSender {
	public void sendTest(String user){
		final String username = "sanity.absolutea@gmail.com";
		final String password = "sanityabsolutea";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
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
				InternetAddress.parse("sanity.absolutea@gmail.com"));
			message.setSubject("Welcome to $anity");
			byte[] encoded = Files.readAllBytes(Paths.get("emailHTML.html"));
			String emailToSend= new String(encoded,Charset.defaultCharset());
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
		}
	  }
	public static void main(String[] args){
		 EmailSender e=new EmailSender();
		 e.sendTest("mu");
	 }
}



