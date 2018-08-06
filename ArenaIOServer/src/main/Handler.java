package main;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.Main.MainConnectionThread;

import org.json.JSONObject;

import utility.LittleEndianDataInputStream;
import utility.LittleEndianOutputStream;
import cern.jet.random.Binomial;
import cern.jet.random.engine.RandomEngine;

public class Handler {
	
	MainConnectionThread conn;

	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom rnd = new SecureRandom();
	public Player player;

	static int connections = 0;
	int id = 0;

	protected void onOpen(MainConnectionThread conn) throws IOException {
		System.out.println("Opened to client!");
		this.conn = conn;
		id = connections++;
		player = new Player();
		player.connection = this;
		
		synchronized (Main.ips) {
			System.out.println("Have " + Main.ips.size() + " servers");
			/*Send(c_checkConnections, (byte) Main.ips.size(), Main.ips);
			Send(c_entityDefinition, Main.entityDefinitions.length,
					Main.entityDefinitions);*/
		}
	}

	protected void onClose(MainConnectionThread conn) throws IOException {
		player = null;
		conn = null; 
	}

	protected void onError(MainConnectionThread conn, Exception ex) {
		System.out.println("Errored!");
		ex.printStackTrace();
	}

	protected void onMessage(MainConnectionThread conn, String message) {
	}

	void postInit() throws IOException {
		Main.sendToNetwork("Online: " + Main.ips.size());
	}

	protected void onMessage(MainConnectionThread conn,
			LittleEndianDataInputStream blob) throws IOException, SQLException {

		// this.ID
		int category = blob.get();
		
		if (Main.debugPackets)
			System.out.println("<- " + category);

		System.out.println("Got category " + category);

		if (category == PacketIdentifiers.S_) {
			return;
		}
		

		System.out.println("Unknown Packet " + category);
	}
	public void Send(Object... args) throws IOException {
		conn.send(PacketHelper.bytesFromParams(args));
	}

}
