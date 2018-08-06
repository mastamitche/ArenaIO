package main;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.kairus.Automater.MessageReciever;
import org.kairus.Automater.connection;

import utility.LittleEndianDataInputStream;


public class Main extends Thread implements MessageReciever {
	public static boolean debugPackets = true;
	
	public static connection connection;

    static HashMap<String, String> ips = new HashMap<String, String>();
    
    static byte[] entityDefinitions;

	public static void main( String[] args ) throws UnknownHostException, SQLException {
		//WebSocket.DEBUG = false;
		

		Main server = new Main();
		connection = new connection("localhost", server);
		
		server.start();
	}
	int port = 54321;
	
	@Override
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: "+port+".");
			System.exit(-1);
		}
		
		
		
		while (true){
			MainConnectionThread tmp;
			try {
				tmp = new MainConnectionThread(serverSocket.accept());
				tmp.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class MainConnectionThread extends Thread {
		Socket socket;
		public DataOutputStream out;
		public DataInputStream in;
		
		
		Handler handler;
		
		public MainConnectionThread(Socket accept){
			socket = accept;
		}
		
		public boolean running = true;
		
		@Override
		public void run() {
			try {
				out = new DataOutputStream(socket.getOutputStream());
				LittleEndianDataInputStream in = new LittleEndianDataInputStream(socket.getInputStream());
				
				handler = new Handler();
				handler.onOpen(this);
				
				// Connection between servers
				while (running) {
					handler.onMessage(this, in);
				}
			} catch (SocketException | EOFException e){ // Disconnect
				System.out.println("client disconnected");
			} catch (Exception e){
				handler.onError(this, e);
				e.printStackTrace();
			}
			try {
				handler.onClose(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void send(byte[] b) throws IOException {
			if (debugPackets) System.out.println("-> " + b[0]);
			try {
				out.write(b);
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		}
		
		public void close() {
			running = false;
			try {
				this.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	@Override
	public void recieveMessage(String message) {
	}

	@Override
	public void recieveMessage(byte[] message) {
	}
	
	public static void sendToNetwork(String message){
		connection.send(message);
	}
}
