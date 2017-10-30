package test;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
 
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
 
public class test {
 
public static Connection getSSLCon() throws SQLException {
        
        Connection connection = null;
        Session session= null;
        
        String host = "165.227.14.202";
        String servUser = "root";
        String servPwd = "chenyang";
        int port = 22;
        
        String rhost = "localhost";
        int rport = 3306;
        int lport = 3307;
 
        String driverName = "com.mysql.jdbc.Driver";
        String db2Url = "jdbc:mysql://localhost:" + lport + "/SanityDB";
        String dbUsr = "root";
        String dbPwd = "chenyang";
        
        try {
            JSch jsch = new JSch();
            // Get SSH session
            session = jsch.getSession(servUser, host, port);
            session.setPassword(servPwd);
            java.util.Properties config = new java.util.Properties();
            // Never automatically add new host keys to the host file
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            // Connect to remote server
            session.connect();
            // Apply the port forwarding
            session.setPortForwardingL(lport, rhost, rport);
            // Connect to remote database
            Class.forName(driverName);
            connection = DriverManager.getConnection(db2Url, dbUsr, dbPwd);
            System.out.println ("Connection to database established!");

             return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
           
        }
        return null;
    }
 
}