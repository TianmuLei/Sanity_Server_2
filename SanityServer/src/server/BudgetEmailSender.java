package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class BudgetEmailSender extends Thread{
	JSONObject budget;
	String toEmail;
	String user;
	private final String createCategoryStatement= "<tr> <td ><strong>Category Name</strong></td> <td><strong>Spending</strong></td> <td><strong>Limit</strong></td> "
			+ "<td><strong>End Date</strong></td> <td><strong>Number of Transaction</strong></td> </tr> "
			+ "<tr> <td>$$$$$$CategoryName</td> <td>$$$$$$Spending</td> <td>$$$$$$Limit</td> "
			+ "<td>$$$$$$Due Date</td> <td>$$$$$$Transaction Number</td> </tr>";
	private final String createTransactionStatement =" <tr> <td>$$$$$$Transaction1Name</td> <td>$$$$$$Description</td> "
			+ "<td>$$$$$$Amount</td> <td>$$$$$$Date</td> <td>$$$$$$Category_name</td> </tr>";
	private final String createTransactionStatementHeader ="<tr> <td> <strong>Transaction Number</strong></td> <td><strong>Description</strong></td> <td><strong>Amount</strong></td> "
			+ "<td><strong>Date</strong></td> <td><strong>Category name</strong></td> </tr>";
	private final String footer ="</div> </div> </body> </html>";
	public BudgetEmailSender(JSONObject budget, String email,String username){
		this.budget=budget;
		toEmail=email;
		user=username;
	}
	@Override
	public void run(){
		try{
			Client client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter("api", "key-59d73d0d606d0df987743b0c84305aaf"));
			    
			WebResource webResource = client.resource("https://api.mailgun.net/v3/sandbox1a3192acb7454b4cbe565ad1ee6369e4.mailgun.org/messages");
			MultivaluedMapImpl formData = new MultivaluedMapImpl();
			formData.add("from", "Mailgun Sandbox <postmaster@sandbox1a3192acb7454b4cbe565ad1ee6369e4.mailgun.org>");
			// formData.add("to", "Jiaxinch <jiaxinch@usc.edu>");
			String to=user+" <"+toEmail+">";
			System.out.println(to);
			formData.add("to", to);
			String title ="Budget "+budget.getString("name")+" Summary";
			formData.add("subject", title);
			InputStream in = getClass().getResourceAsStream("/summaryHTML.html");
			BufferedReader input = new BufferedReader(new InputStreamReader(in));
			String emailToSend= new String();
			String temp = input.readLine();
			while(temp!=null){
				emailToSend+=temp;
				temp =input.readLine();
			}
			emailToSend =emailToSend.replace("$$$$$$budget", budget.getString("name"));
			emailToSend=emailToSend.replace("$$$$$$user",user);
			emailToSend=emailToSend.replace("$$$$$$currentSpending", budget.getDouble("budgetSpent")+"");
			emailToSend= emailToSend.replace("$$$$$$TotalBudget", budget.getDouble("budgetTotal")+"");
			emailToSend = emailToSend.replace("$$$$$$startdate", budget.getString("startDate"));
			emailToSend = emailToSend.replace("$$$$$$endDate", budget.getString("endDate"));
			
			JSONArray categoryList = budget.getJSONArray("categoryList");
			for(int i=0;i<categoryList.length();++i){
				emailToSend+="<table>";
				emailToSend+=createCategoryStatement;
				JSONObject category =(JSONObject)categoryList.get(i);
				emailToSend =emailToSend.replace("$$$$$$CategoryName", category.getString("name"));
				emailToSend =emailToSend.replace("$$$$$$Spending", category.getDouble("categorySpent")+"");
				emailToSend =emailToSend.replace("$$$$$$Limit", category.getDouble("limit")+"");
				emailToSend =emailToSend.replace("$$$$$$Due Date", budget.getString("endDate"));
				JSONArray transList=category.getJSONArray("transactionList");
				emailToSend =emailToSend.replace("$$$$$$Transaction Number", transList.length()+"");
				emailToSend+=createTransactionStatementHeader;
				for(int j=0;j<transList.length();++j){
					JSONObject trans =(JSONObject)transList.get(j);
					emailToSend+=createTransactionStatement;
					emailToSend =emailToSend.replace("$$$$$$Transaction1Name", (j+1)+"");
					emailToSend =emailToSend.replace("$$$$$$Description", trans.getString("description"));
					emailToSend =emailToSend.replace("$$$$$$Amount", trans.getDouble("amount")+"");
					emailToSend =emailToSend.replace("$$$$$$Date", trans.getString("date"));
					emailToSend =emailToSend.replace("$$$$$$Category_name", category.getString("name"));
				}
				
				emailToSend+="</table>";
				emailToSend+="<br/>";
				
			}
			
			emailToSend+=footer;
			formData.add("html",emailToSend);
			ClientResponse clientResponse=webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
			                                        post(ClientResponse.class, formData);
			System.out.println(clientResponse.getClientResponseStatus());
			System.out.println("Done");
			
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("JSON error in BudgetEmail sender");
		}catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("problems in email sender");
		}
		
		
	}
	public static void main(String[] args){
		BudgetDAO temp = new BudgetDAO();
		DAO.testing=0;
		temp.sendBudgetSummary("yearly", "tianmu.lei2@gmail.com");
	}
}
