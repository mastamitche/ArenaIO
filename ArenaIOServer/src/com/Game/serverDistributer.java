package com.Game;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

public class serverDistributer extends WebSocketServer {
	static int targetPlayersPerServer = 1000000; // basically infinite, so, everyone is in one absolutely massive room
	static int maxPlayersPerServer = 1000000;
	static List<GameServer> serverList = Collections.synchronizedList(new ArrayList<GameServer>());
	
	public serverDistributer(int port) throws UnknownHostException {
		super( new InetSocketAddress( port ) );

		if (port == serverDistributer.port){
			serverList.add(new GameServer(this));
			(new DistributedConsole(this)).start();
		}
	}
	
	static int port = 32320;

	public static void main( String[] args ) {
		try {
			//WebSocketImpl.DEBUG = true;
	
			String version = "1";
			
			serverDistributer server;
			
			server = new serverDistributer(port+1);
			
			// load up the key store
			String STORETYPE = "JKS";
			String KEYSTORE = "server2.jks";
			String STOREPASSWORD = "pE0Wot27uRur";
			String KEYPASSWORD = "pE0Wot27uRur";
	
			KeyStore ks = KeyStore.getInstance( STORETYPE, "SUN" );
			File kf = new File( KEYSTORE );
			ks.load( new FileInputStream( kf ), STOREPASSWORD.toCharArray() );
	
			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( ks, KEYPASSWORD.toCharArray() );
			TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
			tmf.init( ks );
	
			SSLContext sslContext = SSLContext.getInstance( "TLS" );
			sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
			
			server.setWebSocketFactory( new DefaultSSLWebSocketServerFactory( sslContext ) );
			server.start();
			server = new serverDistributer(port);
			server.start();
	
			System.out.println("Arena Io server v"+version+" up on ports: "+ port + " and " + (port+1));
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public serverDistributer( int port , Draft d ) throws UnknownHostException {
		super( new InetSocketAddress( port ), Collections.singletonList( d ) );
		if (port == serverDistributer.port){
			serverList.add(new GameServer(this));
			(new DistributedConsole(this)).start();
		}
	}
	
	// Unused
	public serverDistributer( InetSocketAddress address, Draft d ) {
		super( address, Collections.singletonList( d ) );
	}
	
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		if (printConnections) System.out.println("Closing connection from " + conn.getRemoteSocketAddress());
		try{
			System.out.println("Socket closed");
			if (conn == null) return;
			GameServer s = (GameServer) ((WebSocketImpl)conn).connectionHandler.server;
			((WebSocketImpl)conn).connectionHandler.server = null;
			if (s != null)
				s.onClose(conn, code, reason, remote);
			//System.out.println("Connections: " + connectionToServer);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		try{
			System.out.println("Socket errored: ");
			ex.printStackTrace();
			if (conn == null) return;
			GameServer s = (GameServer) ((WebSocketImpl)conn).connectionHandler.server;
			if (s != null)
				s.onError(conn, ex);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		try{
			System.out.println("Got message " + message);
			if (conn == null) return;
			GameServer s = (GameServer) ((WebSocketImpl)conn).connectionHandler.server;
			if (s != null)
				s.onMessage(conn, message);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer blob) {
		try{
			System.out.println("Got message blob " +blob);
			
			if (conn == null || ((WebSocketImpl)conn).connectionHandler == null) return;
			((WebSocketImpl)conn).connectionHandler.onMessage(conn, blob);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static boolean printConnections = false;
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		if (printConnections) System.out.println("Opening connection from " + conn.getRemoteSocketAddress());
		
		try{
			if (conn == null) return;
			
			((WebSocketImpl)conn).connectionHandler = new ConnectionHandler(conn);
			
			// Find server
			for (GameServer s : serverList){
				//if (s.connected < targetPlayersPerServer){
					//System.out.println("Adding to server: " + s.serverNum);
					((WebSocketImpl)conn).connectionHandler.server = s;
					s.onNewConnection(conn, handshake);
					((WebSocketImpl)conn).connectionHandler.onOpen();
					return;
				//}
			}
			/*
			// Try to over-populate servers if need be
			for (GameServer s : serverList){
				//if (s.connected < maxPlayersPerServer){
					//System.out.println("Adding to server: " + s.serverNum);
					((WebSocketImpl)conn).connectionHandler.server = s;
					s.onNewConnection(conn, handshake);
					return;
				//}
			}
	
			//System.out.println("Max players reached! New server count: " + (numServers+1));
			
			// Shit. Make another server.
			GameServer newServer = new GameServer();
			serverList.add(newServer);
			
			((WebSocketImpl)conn).connectionHandler.server = newServer;
			newServer.onNewConnection(conn, handshake);
			((WebSocketImpl)conn).connectionHandler.onOpen();
			*/
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static int getServerPop(int server){
		return serverList.get(server).getPlayerCount();
	}
	/*
	public static byte[] getServerStatusPacket() {
		ByteBuffer bb = ByteBuffer.wrap(new byte[6]);
		bb.put((byte) 32);
		for (int i = 0; i < 20; i+=4){
			byte b = 0;
			b += getServerPop(i + 0);
			b += getServerPop(i + 1) << 2;
			b += getServerPop(i + 2) << 4;
			b += getServerPop(i + 3) << 6;
			bb.put(b);
		}
		return bb.array();
	}*/
	
}