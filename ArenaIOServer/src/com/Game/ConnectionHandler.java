package com.Game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;

import com.Entity.*;
import com.Odessa.utility.PacketHelper;
import com.Odessa.utility.XSRandom;
import com.Odessa.utility.vec2;

public class ConnectionHandler {

	GameServer server;
	public WebSocket conn;
	public Player player;

	public ConnectionHandler(WebSocket conn) {
		this.conn = conn;
	}

	public void onOpen() {
		if (server.lastLeaderboard != null)
			Send(server.lastLeaderboard);
	}

	protected void onClose(WebSocket conn, int code, String reason,
			boolean remote) {
		System.out.println("Closed! " + conn);
		if (((WebSocketImpl) conn).connectionHandler.player != null)
			((WebSocketImpl) conn).connectionHandler.server
					.removePlayer(((WebSocketImpl) conn));
		((WebSocketImpl) conn).connectionHandler.conn = null; // unlink handler
		((WebSocketImpl) conn).connectionHandler = null;

		player = null;
		conn = null;
	}

	protected void onError(WebSocket conn, Exception ex) {
		System.out.println("Errored! " + conn);
	}

	protected void onMessage(WebSocket conn, String message) {
		System.out.println("Got something through onMessage string should use blob : " + message);
	}

	// To server
	public static byte s_spawn = 0;
	public static byte s_playerMove = 1;
	public static byte s_playerPickUp = 2;
	public static byte s_playerFire = 3;
	public static byte s_ping = 4;
	public static byte s_nameReq = 5;
	public static byte s_reload = 6;

	// To client
	public static byte c_serverParams = 0;
	public static byte c_gameState = 1;
	public static byte c_setEntityPosition = 2;
	public static byte c_entitySpawn = 3;
	public static byte c_entityDespawn = 4;
	public static byte c_playerStats = 5;
	public static byte c_playerEquip = 6;
	public static byte c_pong = 8;
	public static byte c_name = 9;
	public static byte c_room = 10;

	public static float sizeMultiplier = (float) (0.5f / Math.PI);

	// All networking goes here
	public void onMessage(WebSocket conn, ByteBuffer blob) {
		try {
			if (!conn.isOpen() || conn.isConnecting()) {
				System.out
						.println("Packet recieved from connecting client! Return!");
				return;
			}

			blob.order(ByteOrder.LITTLE_ENDIAN);

			int category = blob.get();

			// System.out.println("Got category " + category);

			if (category == s_spawn) {
				if (player != null && !player.isDead)
					return;

				if (player != null)
					player.Destroy();

				// Get random spawn spot for the player
				vec2 pos = new vec2(0, 0);
				for (int i = 0; i < 100; i++) {
					pos = server.rndPosInWorld();
					if (Entity
							.getEntitiesinRangeMap(
									pos,
									server,
									7,
									new byte[] { Entity.TYPE_PLAYER,
											Entity.TYPE_BULLET }).size() > 0)
						continue;
				}

				byte color = (byte) (XSRandom.random() * 10);
				player = new Player(this, server, pos, color);
				player.name = server.nm.AddEntry("");
				Send((byte) c_gameState, (byte) 1, player.id);
				//player.sendPositionReset();

				Send(player.getSpawnPacket());
				return;
			}

			if (category == s_nameReq) {
				int id = blob.getInt();
				String name = server.nm.GetEntry(id);
				if (name != null)
					Send(PacketHelper.bytesFromParams(c_name, id, name));
				return;
			}

			if (category == s_ping) {
				Send(new byte[] { c_pong });
				return;
			}

			if (player == null || player.isDead)
				return;

			if (category == s_playerMove) {
				vec2 pos = new vec2(blob.getFloat(), blob.getFloat());
				player.setRotation(blob.get() / PacketHelper.RADIANSTOBYTE);
				player.moveTo(pos);
				player.sendPositionReset();
				return;
			}

			if (category == s_playerPickUp) {
				if (!player.canCollect)
					return;
				int id = blob.getInt();
				Entity e = server.idHandler.get(id);
				float effectiveRange = (float) (2 + (Math.sqrt(player.size) / 2f * sizeMultiplier)
						* pickupTolerance);
				if (e != null) {
					if (player.pos.distSquared(e.pos) < effectiveRange
							* effectiveRange) {
						if (e instanceof ShotGun)
							player.hit((ShotGun) e);
						else if (e instanceof HealthPack)
							player.hit((HealthPack) e);
						else if (e instanceof Ammo)
							player.hit((Ammo) e);
						else if (e instanceof Armour)
							player.hit((Armour) e);
						e.Destroy();
					} else {
						System.out.println("Out of range! "
								+ player.pos.dist(e.pos) + " > "
								+ effectiveRange);
					}
				}
				return;
			}
			if(category == s_reload){
				player.reload();
				return;
			}

			if (category == s_playerFire) {
				player.fire();
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	float pickupTolerance = 1.5f;

	class sendingThread extends Thread {
		Object[] args;

		sendingThread(Object... args) {
			this.args = args;
		}

		public void run() {
			try {
				Thread.sleep(300);
				ConvertToBytesAndSave(args);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void Send(Object... args) {
		ConvertToBytesAndSave(args);
		ActuallySend();
	}

	ByteArrayOutputStream buf = new ByteArrayOutputStream();

	// You must be VERY VERY CAREFUL to make sure variables going into this
	// function are of the type you want.
	public void ConvertToBytesAndSave(Object... args) {
		if (conn == null || conn.isConnecting() || conn.isClosed()
				|| !conn.isOpen())
			return;
		if (args.length == 1 && args[0] instanceof byte[]) // Don't be smart if
															// we already have
															// what we want
			try {
				synchronized (buf) {
					buf.write((byte[]) args[0]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			try {
				synchronized (buf) {
					buf.write(PacketHelper.bytesFromParams(args));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public void ActuallySend() {
		if (buf.size() == 0)
			return;
		// if (buf.size() > 600)
		// System.out.println("Large packet size: " + buf.size());
		if (conn.isOpen() && !conn.isConnecting() && !conn.isClosing()) {
			try {
				synchronized (buf) {
					buf.flush();
					conn.send(buf.toByteArray());
					buf.reset();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			buf.reset();

	}

	public static String getString(ByteBuffer blob) {
		String s = "";
		while (true) {
			byte b = blob.get();
			if (b == 0)
				break;
			s += (char) b;
		}
		return s;
	}

}
