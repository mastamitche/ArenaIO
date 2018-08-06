package utility;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import cern.jet.random.engine.MersenneTwister;

import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

public class DatabaseInterface {

	private Connection con = null;

	private String url = "jdbc:mysql://104.131.187.46:3306/";
	private String dbName = "WarOfWizards";
	private String driver = "com.mysql.jdbc.Driver";
	private String userName = "root";
	private String password = "nd5tafrrsshn";
	

	private static DatabaseInterface instance;
	static MersenneTwister rnd;

	public static void instanceCheck() {
		try {
			if (instance == null || !instance.con.isValid(3)) {
				System.out.println("Initializing link to database");
				new DatabaseInterface();
				System.out.println("Database hooked");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	DatabaseInterface() {
		rnd = new MersenneTwister(new Date());

		try {dbName = InetAddress.getLocalHost().getHostName().equals("odessaWebsite") ? "WarOfWizards" : "WarOfWizardsDev";} catch (Exception e) {e.printStackTrace();}
		
		try {
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection(url + dbName
					+ "?autoReconnect=true", userName, password);
			System.out.println("Connected to the database");
			instance = this;
		} catch (CommunicationsException e) {
			System.out.println("COULDN'T CONNECT TO MYSQL SERVER!");
			e.printStackTrace();
			System.exit(1);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		// System.exit(0);
	}

	public static ResultSet stmt(String statement, boolean isUpdate, Object... values) throws SQLException {
		instanceCheck();
		PreparedStatement stmt = instance.con.prepareStatement(statement);
		for (int i = 0; i < values.length; i++){
			//System.out.println("Set " + (i + 1) + " to " + values[i]);
			stmt.setString(i + 1, ""+values[i]);
		}
		if (isUpdate) {
			stmt.executeUpdate();
			return null;
		} else
			return stmt.executeQuery();
	}
	public static ResultSet stmtC(String statement, boolean isUpdate, Object... values) {
		try{
			return stmt(statement, isUpdate, values);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public static ResultSet stmtGetInsertedAI(String statement, Object... values) {
		try{
			instanceCheck();
			PreparedStatement stmt = instance.con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < values.length; i++){
				//System.out.println("Set " + (i + 1) + " to " + values[i]);
				stmt.setString(i + 1, ""+values[i]);
			}
			stmt.executeUpdate();
			return stmt.getGeneratedKeys();
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getChallenge() {
		return getSHA512(("" + rnd.nextFloat()).getBytes());
	}
	public static String getSHA512(byte[] bytes) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-512");
			return (new HexBinaryAdapter()).marshal(md.digest(bytes));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void threadedStatement(String statement, Runnable callback, Object... values){
		new threadedSQLStatement(statement, callback, values);
	}

}

class threadedSQLStatement extends Thread {
	String statement;
	Object[] values;
	Runnable callback;

	threadedSQLStatement(String statement, Runnable callback, Object... values) {
		this.statement = statement;
		this.values = values;
		this.callback = callback;
		start();
		// System.out.println("Created SQL thread");
	}

	@Override
	public void run() {
		// System.out.println("Running sql thread");
		while (true) {
			// System.out.println("In while");
			try {
				// System.out.print("Running statement: " + statement.toString()
				// + ", ");
				// for (String s : values)
				// System.out.print(s + ", ");
				// System.out.println();
				DatabaseInterface.stmt(statement, true, values);
				break;
			} catch (SQLException e2) {
				System.out.print("Error doing sql credit giving!");
				e2.printStackTrace();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		// System.out.println("Finished");
		if (callback != null)
			callback.run();
	}
}