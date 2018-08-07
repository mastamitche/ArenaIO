using UnityEngine;
using System.Collections;
using WebSocketSharp;
using System.IO;
using System;

public class Networking : MonoBehaviour {
    // To server
    public static byte s_spawn = 0;
    public static byte s_playerMove = 1;
    public static byte s_playerEatFood = 2;
    public static byte s_playerFire = 3;
    public static byte s_ping = 4;
    public static byte s_nameReq = 5;

    // To client
    public const byte c_serverParams = 0;
    public const byte c_gameState = 1;
    public const byte c_setEntityPosition = 2;
    public const byte c_entitySpawn = 3;
    public const byte c_entityDespawn = 4;
    public const byte c_leaderboard = 5;
    public const byte c_myRank = 6;
    public const byte c_entitySizeChange = 7;
    public const byte c_pong = 8;
    public const byte c_name = 9;
    public const byte c_absorberHitChange = 10;

    static WebSocket socket;

    void Awake()
    {
        socket = new WebSocket("ws://192.168.1.12:32320");
        socket.OnOpen += (sender,e) => OnOpen();
        socket.OnClose += (sender, e) => OnClose();
        socket.OnError += (sender, e) => OnError();
        socket.OnMessage += (sender, e) => OnMessage(e);
        socket.Connect();
    }

	// Use this for initialization
	void Start () {
	    
	}
	
	// Update is called once per frame
	void Update () {
	
	}
    void OnError()
    {

    }

    void OnMessage(MessageEventArgs e)
    {
        if (e.RawData.Length > 0)
            OnPacket(e.RawData);
        if (e.Data.Length > 0)
            Debug.Log(e.Data);
    }

    void OnOpen()
    {
        Debug.Log("Connection open");
        socket.Send("first message");
    }

    void OnClose()
    {

    }

    void OnPacket(byte[] data)
    {
        using (MemoryStream stream = new MemoryStream(data))
        {
            using (BinaryReader reader = new BinaryReader(stream))
            {
                int category = reader.ReadByte();


                switch (category) {
                    case c_serverParams:
                        break;

                    case c_gameState:
                        break;

                    case c_setEntityPosition:
                        break;

                    case c_entitySpawn:
                        break;

                    case c_entityDespawn:
                        break;

                    case c_leaderboard:
                        break;

                    case c_myRank:
                        break;

                    case c_entitySizeChange:
                        break;

                    case c_pong:
                        break;

                    case c_name:
                        break;

                    case c_absorberHitChange:
                        break;
                }

            }
        }
    }

    public static void sendPlayerMove(Vector2 v2, float angle)
    {
        send(s_playerMove, v2.x, v2.y, getAngleByte(angle));
    }

    public static void sendOnPickup(int id)
    {
        send(s_playerEatFood,id);
    }
    public static void onPlayerFire()
    {
        send(s_playerFire);
    }

    public static void send(params object[] args)
    {
        try
        {
            socket.Send(NetworkingUtility.bytesFromParams(args));
        }
        catch (Exception e)
        {
            print("Error");
            print(e.StackTrace);
        }
    }

    public static readonly float RADIANSTOBYTE = 40.58f;
    public static byte getAngleByte(float angle)
    {
        return (byte)((angle % 6.2832f) * RADIANSTOBYTE);
    }
}
