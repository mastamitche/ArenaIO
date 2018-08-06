using UnityEngine;
using System.Collections;
using WebSocketSharp;
using System.IO;

public class Networking : MonoBehaviour {
    // To server
    public static byte s_spawn = 0;
    public static byte s_playerMove = 1;
    public static byte s_playerEatFood = 2;
    public static byte s_playerFire = 3;
    public static byte s_ping = 4;
    public static byte s_nameReq = 5;

    // To client
    public static byte c_serverParams = 0;
    public static byte c_gameState = 1;
    public static byte c_setEntityPosition = 2;
    public static byte c_entitySpawn = 3;
    public static byte c_entityDespawn = 4;
    public static byte c_leaderboard = 5;
    public static byte c_myRank = 6;
    public static byte c_entitySizeChange = 7;
    public static byte c_pong = 8;
    public static byte c_name = 9;
    public static byte c_absorberHitChange = 10;

    WebSocket socket;

    void Awake()
    {
        socket = new WebSocket("ws://192.168.20.13:32320");
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
            }
        }
    }
}
