package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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


public class EmailSender extends Thread{
	String user, toEmail;
	public EmailSender(String user, String toEmail){
		this.user=user;
		this.toEmail=toEmail;
	}
	@Override
	public void run() {
		super.run();
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
				InternetAddress.parse(toEmail));
			message.setSubject("Welcome to $anity");
//			byte[] encoded = Files.readAllBytes(Paths.get());
//			String emailToSend= new String(encoded,Charset.defaultCharset());
			String emailToSend = readFromJARFile();
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
	private String readFromJARFile()
			throws IOException
			{
			  InputStream is = getClass().getResourceAsStream("src/emailHTML.html");
			  InputStreamReader isr = new InputStreamReader(is);
			  BufferedReader br = new BufferedReader(isr);
			  StringBuffer sb = new StringBuffer();
			  String line;
			  while ((line = br.readLine()) != null) 
			  {
			    sb.append(line);
			  }
			  br.close();
			  isr.close();
			  is.close();
			  return sb.toString();
			}
}



